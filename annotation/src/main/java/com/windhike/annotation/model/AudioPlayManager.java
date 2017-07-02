package com.windhike.annotation.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import com.windhike.annotation.common.LogUtils;

/**
 * author: zyongjun on 2017/6/30 0030.
 * email: zhyongjun@windhike.cn
 */

public class AudioPlayManager implements SensorEventListener {
    private static final String TAG = "AudioPlayManager";
    private MediaPlayer _mediaPlayer;
    private IAudioPlayListener _playListener;
    private Uri _playingUri;
    private Sensor _sensor;
    private SensorManager _sensorManager;
    private AudioManager _audioManager;
    private PowerManager _powerManager;
    private PowerManager.WakeLock _wakeLock;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    public AudioPlayManager() {
    }

    public static AudioPlayManager getInstance() {
        return AudioPlayManager.SingletonHolder.sInstance;
    }

    @TargetApi(11)
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if(this._mediaPlayer != null && this._mediaPlayer.isPlaying()) {
            if(range == this._sensor.getMaximumRange()) {
                this._audioManager.setMode(0);
                this._audioManager.setSpeakerphoneOn(true);
                this.setScreenOn();
            } else {
                this._audioManager.setSpeakerphoneOn(false);
                if(Build.VERSION.SDK_INT >= 11) {
                    this._audioManager.setMode(3);
                } else {
                    this._audioManager.setMode(2);
                }

                this.setScreenOff();
                this._mediaPlayer.setVolume(1.0F, 1.0F);
                int maxVolume = this._audioManager.getStreamMaxVolume(3);
                this._audioManager.setStreamVolume(3, maxVolume, 4);
            }
        } else if(range == this._sensor.getMaximumRange()) {
            this._audioManager.setMode(0);
            this._audioManager.setSpeakerphoneOn(true);
            this.setScreenOn();
        }

    }

    @TargetApi(21)
    private void setScreenOff() {
        if(this._wakeLock == null) {
            if(Build.VERSION.SDK_INT >= 21) {
                this._wakeLock = this._powerManager.newWakeLock(32, "AudioPlayManager");
            } else {
                LogUtils.LogError("AudioPlayManager", "Does not support on level " + Build.VERSION.SDK_INT);
            }
        }

        if(this._wakeLock != null) {
            this._wakeLock.acquire();
        }

    }

    private void setScreenOn() {
        if(this._wakeLock != null) {
            this._wakeLock.setReferenceCounted(false);
            this._wakeLock.release();
            this._wakeLock = null;
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void startPlay(final Context context, Uri audioUri, IAudioPlayListener playListener) {
        if(context != null && audioUri != null) {
            this.resetMediaPlayer();
            if(this.afChangeListener != null) {
                AudioManager e = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                e.abandonAudioFocus(this.afChangeListener);
                this.afChangeListener = null;
            }

            this.afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    LogUtils.LogInfo("AudioPlayManager", "OnAudioFocusChangeListener " + focusChange);
                    AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    if(focusChange == -1) {
                        am.abandonAudioFocus(AudioPlayManager.this.afChangeListener);
                        AudioPlayManager.this.afChangeListener = null;
                        AudioPlayManager.this.resetMediaPlayer();
                    }

                }
            };

            try {
                this._powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                this._audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                this._sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
                this._sensor = this._sensorManager.getDefaultSensor(8);
                this._sensorManager.registerListener(this, this._sensor, 3);
                this.muteAudioFocus(this._audioManager, true);
                this._playListener = playListener;
                this._playingUri = audioUri;
                this._mediaPlayer = new MediaPlayer();
                this._mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        AudioPlayManager.this.muteAudioFocus(AudioPlayManager.this._audioManager, false);
                        mp.stop();
                        mp.release();
                        AudioPlayManager.this._mediaPlayer = null;
                        Uri temp = AudioPlayManager.this._playingUri;
                        AudioPlayManager.this._playingUri = null;
                        if(AudioPlayManager.this._playListener != null) {
                            AudioPlayManager.this._playListener.onComplete(temp);
                        }

                    }
                });
                this._mediaPlayer.setDataSource(context, audioUri);
                this._mediaPlayer.prepare();
                this._mediaPlayer.start();
                if(this._playListener != null) {
                    this._playListener.onStart(this._playingUri);
                }
            } catch (Exception var5) {
                var5.printStackTrace();
                if(playListener != null) {
                    playListener.onStop(audioUri);
                }
            }

        } else {
            LogUtils.LogInfo("AudioPlayManager", "startPlay context or audioUri is null.");
        }
    }

    public void stopPlay() {
        this.resetMediaPlayer();
    }

    private void resetMediaPlayer() {
        if(this._mediaPlayer != null) {
            this._mediaPlayer.stop();
            this._mediaPlayer.release();
            this.muteAudioFocus(this._audioManager, false);
            if(this._playListener != null && this._playingUri != null) {
                this._playListener.onStop(this._playingUri);
            }

            if(this._sensorManager != null) {
                this._sensorManager.unregisterListener(this);
            }
        }

        this._playListener = null;
        this._playingUri = null;
        this._mediaPlayer = null;
    }

    public Uri getPlayingUri() {
        return this._playingUri;
    }

    @TargetApi(8)
    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if(Build.VERSION.SDK_INT < 8) {
            LogUtils.LogInfo("AudioPlayManager", "muteAudioFocus Android 2.1 and below can not stop music");
        } else {
            if(bMute) {
                audioManager.requestAudioFocus(this.afChangeListener, 3, 2);
            } else {
                audioManager.abandonAudioFocus(this.afChangeListener);
                this.afChangeListener = null;
            }

        }
    }

    static class SingletonHolder {
        static AudioPlayManager sInstance = new AudioPlayManager();

        SingletonHolder() {
        }
    }
}