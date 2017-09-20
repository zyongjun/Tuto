package com.windhike.tuto.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import java.io.File;

/**
 * author: zyongjun on 2017/3/18 0018.
 * email: zhyongjun@windhike.cn
 */

public class AnnotationAudioRecordManager implements Handler.Callback {
    private static final String TAG = "AudioRecordManager";
    private int RECORD_INTERVAL;
    IAudioState cancelState;
    IAudioState idleState;
    private AudioManager.OnAudioFocusChangeListener mAfChangeListener;
    private AudioManager mAudioManager;
    private Uri mAudioPath;
    private Context mContext;
    private IAudioState mCurAudioState;
    private Handler mHandler;
    private MediaRecorder mMediaRecorder;
    private PopupWindow mRecordWindow;
    private View mRootView;
    private ImageView mStateIV;
    private TextView mStateTV;
    private TextView mTimerTV;
    IAudioState recordState;
    private long smStartRecTime;
    IAudioState timerState;

    class CancelState extends IAudioState {
        CancelState() {
        }

        void handleMessage(AudioStateMessage msg) {
            Log.e(TAG, getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch (msg.what) {
                case 4:
                    setRecordingView();
                    mCurAudioState = recordState;
                    sendEmptyMessage(2);
                    return;
                case 5:
                case 6:
                    stopRec();
                    destroyView();
                    deleteAudioFile();
                    mCurAudioState = idleState;
                    idleState.enter();
                    return;
                case 7:
                    int counter = ((Integer) msg.obj).intValue();
                    if (counter > 0) {
                        Message message = Message.obtain();
                        message.what = 8;
                        message.obj = Integer.valueOf(counter - 1);
                        mHandler.sendMessageDelayed(message, 1000);
                        return;
                    }
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            stopRec();
                            sendAudioFile();
                            destroyView();
                        }
                    }, 500);
                    mCurAudioState = idleState;
                    idleState.enter();
            }
        }
    }

    class IdleState extends IAudioState {
        public IdleState() {
            Log.e(AnnotationAudioRecordManager.TAG, "IdleState");
        }

        public void enter() {
            super.enter();
            if (mHandler != null) {
                mHandler.removeMessages(7);
                mHandler.removeMessages(8);
                mHandler.removeMessages(2);
            }
        }

        void handleMessage(AudioStateMessage msg) {
            Log.e(TAG, "IdleState handleMessage : " + msg.what);
            switch (msg.what) {
                case 1:
                    initView(mRootView);
                    setRecordingView();
                    startRec();
                    smStartRecTime = SystemClock.elapsedRealtime();
                    mCurAudioState = recordState;
                    sendEmptyMessage(2);
                    return;
                default:
                    return;
            }
        }
    }

    class RecordState extends IAudioState {
        RecordState() {
        }

        void handleMessage(AudioStateMessage msg) {
            Log.e(TAG, getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch (msg.what) {
                case 2:
                    audioDBChanged();
                    mHandler.sendEmptyMessageDelayed(2, 150);
                    return;
                case 3:
                    setCancelView();
                    mCurAudioState = cancelState;
                    return;
                case 5:
                    final boolean checked = checkAudioTimeLength();
                    if (checked) {
                        mStateIV.setImageResource(R.mipmap.rc_ic_volume_wraning);
                        mStateTV.setText("录音时间太短");
                    }
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            stopRec();
                            if (!checked) {
                                sendAudioFile();
                            }
                            destroyView();
                        }
                    }, 500);
                    mCurAudioState = idleState;
                    return;
                case 6:
                    stopRec();
                    destroyView();
                    deleteAudioFile();
                    mCurAudioState = idleState;
                    idleState.enter();
                    return;
                case 7:
                    int counter = ((Integer) msg.obj).intValue();
                    setTimeoutView(counter);
                    mCurAudioState = timerState;
                    if (counter > 0) {
                        Message message = Message.obtain();
                        message.what = 8;
                        message.obj = Integer.valueOf(counter - 1);
                        mHandler.sendMessageDelayed(message, 1000);
                        return;
                    }
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            stopRec();
                            sendAudioFile();
                            destroyView();
                        }
                    }, 500);
                    mCurAudioState = idleState;
                    return;
                default:
                    return;
            }
        }
    }

    static class SingletonHolder {
        static AnnotationAudioRecordManager sInstance = new AnnotationAudioRecordManager();

        SingletonHolder() {
        }
    }

    class TimerState extends IAudioState {
        TimerState() {
        }

        void handleMessage(AudioStateMessage msg) {
            Log.e(TAG, getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch (msg.what) {
                case 3:
                    setCancelView();
                    mCurAudioState = cancelState;
                    return;
                case 5:
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            stopRec();
                            sendAudioFile();
                            destroyView();
                        }
                    }, 500);
                    mCurAudioState = idleState;
                    idleState.enter();
                    return;
                case 6:
                    stopRec();
                    destroyView();
                    deleteAudioFile();
                    mCurAudioState = idleState;
                    idleState.enter();
                    return;
                case 7:
                    int counter = ((Integer) msg.obj).intValue();
                    if (counter > 0) {
                        Message message = Message.obtain();
                        message.what = 8;
                        message.obj = Integer.valueOf(counter - 1);
                        mHandler.sendMessageDelayed(message, 1000);
                        setTimeoutView(counter);
                        return;
                    }
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            stopRec();
                            sendAudioFile();
                            destroyView();
                        }
                    }, 500);
                    mCurAudioState = idleState;
                    return;
                default:
                    return;
            }
        }
    }

    public static AnnotationAudioRecordManager getInstance() {
        return SingletonHolder.sInstance;
    }

    @TargetApi(21)
    private AnnotationAudioRecordManager() {
        this.RECORD_INTERVAL = 60;
        this.idleState = new IdleState();
        this.recordState = new RecordState();
        this.cancelState = new CancelState();
        this.timerState = new TimerState();
        Log.e(TAG, TAG);
        if (Build.VERSION.SDK_INT < 21) {
            try {
                ((TelephonyManager) TutoApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE)).listen(new PhoneStateListener() {
                    public void onCallStateChanged(int state, String incomingNumber) {
                        switch (state) {
                            case 1:
                                sendEmptyMessage(6);
                                break;
                        }
                        super.onCallStateChanged(state, incomingNumber);
                    }
                }, 32);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        this.mCurAudioState = this.idleState;
        this.idleState.enter();
    }

    public final boolean handleMessage(Message msg) {
        Log.e(TAG, "handleMessage " + msg.what);
        AudioStateMessage m;
        switch (msg.what) {
            case 2:
                sendEmptyMessage(2);
                break;
            case 7:
                m = AudioStateMessage.obtain();
                m.what = msg.what;
                m.obj = msg.obj;
                sendMessage(m);
                break;
            case 8:
                m = AudioStateMessage.obtain();
                m.what = 7;
                m.obj = msg.obj;
                sendMessage(m);
                break;
        }
        return false;
    }

    private void initView(View root) {
        this.mHandler = new Handler(root.getHandler().getLooper(), this);
        View view = LayoutInflater.from(root.getContext()).inflate(R.layout.rc_wi_vo_popup, null);
        this.mStateIV = (ImageView) view.findViewById(R.id.rc_audio_state_image);
        this.mStateTV = (TextView) view.findViewById(R.id.rc_audio_state_text);
        this.mTimerTV = (TextView) view.findViewById(R.id.rc_audio_timer);
        this.mRecordWindow = new PopupWindow(view, -1, -1);
        this.mRecordWindow.showAtLocation(root, 17, 0, 0);
        this.mRecordWindow.setFocusable(true);
        this.mRecordWindow.setOutsideTouchable(false);
        this.mRecordWindow.setTouchable(false);
    }

    private void setTimeoutView(int counter) {
        if (this.mRecordWindow != null) {
            this.mStateIV.setVisibility(View.GONE);
            this.mStateTV.setVisibility(View.VISIBLE);
            this.mStateTV.setText(R.string.rc_voice_rec);
            //
            this.mStateTV.setBackgroundResource(R.color.transparent);
            this.mTimerTV.setText(String.format("%s", new Object[]{Integer.valueOf(counter)}));
            this.mTimerTV.setVisibility(View.VISIBLE);
        }
    }

    private void setRecordingView() {
        Log.e(TAG, "setRecordingView");
        if (this.mRecordWindow != null) {
            this.mStateIV.setVisibility(View.VISIBLE);
            this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_1);
            this.mStateTV.setVisibility(View.VISIBLE);
            this.mStateTV.setText(R.string.rc_voice_rec);
            //
            this.mStateTV.setBackgroundResource(R.color.transparent);
            this.mTimerTV.setVisibility(View.VISIBLE);
        }
    }

    private void setCancelView() {
        Log.e(TAG, "setCancelView");
        if (this.mRecordWindow != null) {
            this.mTimerTV.setVisibility(View.GONE);
            this.mStateIV.setVisibility(View.VISIBLE);
            this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_cancel);
            this.mStateTV.setVisibility(View.VISIBLE);
            this.mStateTV.setText(R.string.rc_voice_cancel);
            this.mStateTV.setBackgroundResource(R.drawable.rc_corner_voice_style);
        }
    }

    private void destroyView() {
        Log.e(TAG, "destroyView");
        if (this.mRecordWindow != null) {
            this.mHandler.removeMessages(7);
            this.mHandler.removeMessages(8);
            this.mHandler.removeMessages(2);
            this.mRecordWindow.dismiss();
            this.mRecordWindow = null;
            this.mStateIV = null;
            this.mStateTV = null;
            this.mTimerTV = null;
            this.mHandler = null;
            this.mContext = null;
        }
    }

    public void setMaxVoiceDuration(int maxVoiceDuration) {
        this.RECORD_INTERVAL = maxVoiceDuration;
    }

    public int getMaxVoiceDuration() {
        return this.RECORD_INTERVAL;
    }

    public void startRecord(View rootView) {
        this.mRootView = rootView;
        this.mContext = rootView.getContext().getApplicationContext();
        this.mAudioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
        if (this.mAfChangeListener != null) {
            this.mAudioManager.abandonAudioFocus(this.mAfChangeListener);
            this.mAfChangeListener = null;
        }
        this.mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                Log.e(TAG, "OnAudioFocusChangeListener " + focusChange);
                if (focusChange == -1) {
                    mAudioManager.abandonAudioFocus(mAfChangeListener);
                    mAfChangeListener = null;
                    sendEmptyMessage(6);
                }
            }
        };
        sendEmptyMessage(1);

    }

    public void willCancelRecord() {
        sendEmptyMessage(3);
    }

    public void continueRecord() {
        sendEmptyMessage(4);
    }

    public void stopRecord() {
        sendEmptyMessage(5);
    }

    void sendMessage(AudioStateMessage message) {
        this.mCurAudioState.handleMessage(message);
    }

    void sendEmptyMessage(int event) {
        AudioStateMessage message = AudioStateMessage.obtain();
        message.what = event;
        this.mCurAudioState.handleMessage(message);
    }

    private void startRec() {
        Log.e(TAG, "startRec");
        try {
            muteAudioFocus(this.mAudioManager, true);
            this.mAudioManager.setMode(0);
            this.mMediaRecorder = new MediaRecorder();
            try {
                int bps = 7950;
                this.mMediaRecorder.setAudioSamplingRate(8000);
                this.mMediaRecorder.setAudioEncodingBitRate(bps);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            this.mMediaRecorder.setAudioChannels(1);
            this.mMediaRecorder.setAudioSource(1);
            this.mMediaRecorder.setOutputFormat(3);
            this.mMediaRecorder.setAudioEncoder(1);
            this.mAudioPath = Uri.fromFile(new File(this.mContext.getCacheDir(), System.currentTimeMillis() + "temp.voice"));
            this.mMediaRecorder.setOutputFile(this.mAudioPath.getPath());
            this.mMediaRecorder.prepare();
            this.mMediaRecorder.start();
            Message message = Message.obtain();
            message.what = 7;
            message.obj = Integer.valueOf(10);
            this.mHandler.sendMessageDelayed(message, (long) ((this.RECORD_INTERVAL * 1000) - 10000));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private boolean checkAudioTimeLength() {
        return SystemClock.elapsedRealtime() - this.smStartRecTime < 1000;
    }

    private void stopRec() {
        Log.e(TAG, "stopRec");
        try {
            muteAudioFocus(this.mAudioManager, false);
            if (this.mMediaRecorder != null) {
                this.mMediaRecorder.stop();
                this.mMediaRecorder.release();
                this.mMediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAudioFile() {
        Log.e(TAG, "deleteAudioFile");
        if (this.mAudioPath != null) {
            File file = new File(this.mAudioPath.getPath());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void sendAudioFile() {
//        EventBus.getDefault().post(new AudioRecordStopEvent(mAudioPath));
    }

    private void audioDBChanged() {
        if (this.mMediaRecorder != null) {
            switch ((this.mMediaRecorder.getMaxAmplitude() / 600) / 5) {
                case 0:
                    this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_1);
                    return;
                case 1:
                    this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_2);
                    return;
                case 2:
                    this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_3);
                    return;
                case 3:
                    this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_4);
                    return;
                case 4:
                    this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_5);
                    return;
                case 5:
                    this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_6);
                    return;
                case 6:
                    this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_7);
                    return;
                default:
                    this.mStateIV.setImageResource(R.mipmap.rc_ic_volume_8);
                    return;
            }
        }
    }

    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if (Build.VERSION.SDK_INT < 8) {
            Log.e(TAG, "muteAudioFocus Android 2.1 and below can not stop music");
        } else if (bMute) {
            audioManager.requestAudioFocus(this.mAfChangeListener, 3, 2);
        } else {
            audioManager.abandonAudioFocus(this.mAfChangeListener);
            this.mAfChangeListener = null;
        }
    }
}
