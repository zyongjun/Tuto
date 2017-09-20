package com.windhike.tuto.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.windhike.annotation.model.IAudioPlayListener;

/**
 * author: gzzyj on 2017/3/29.
 * email:zhyongjun@windhike.cn
 */

public class AudioQuietPlayManager {
    private MediaPlayer _mediaPlayer;
    private IAudioPlayListener _playListener;
    private Uri _playingUri;
    private AudioManager _audioManager;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    public AudioQuietPlayManager() {
    }

    public static AudioQuietPlayManager getInstance() {
        return AudioQuietPlayManager.SingletonHolder.sInstance;
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
                    AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    if(focusChange == -1) {
                        am.abandonAudioFocus(AudioQuietPlayManager.this.afChangeListener);
                        AudioQuietPlayManager.this.afChangeListener = null;
                        AudioQuietPlayManager.this.resetMediaPlayer();
                    }

                }
            };

            try {
                this._audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                this.muteAudioFocus(this._audioManager, true);
                this._playListener = playListener;
                this._playingUri = audioUri;
                this._mediaPlayer = new MediaPlayer();
                this._mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        AudioQuietPlayManager.this.muteAudioFocus(AudioQuietPlayManager.this._audioManager, false);
                        mp.stop();
                        mp.release();
                        AudioQuietPlayManager.this._mediaPlayer = null;
                        Uri temp = AudioQuietPlayManager.this._playingUri;
                        AudioQuietPlayManager.this._playingUri = null;
                        if(AudioQuietPlayManager.this._playListener != null) {
                            AudioQuietPlayManager.this._playListener.onComplete(temp);
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
            Log.e("AudioQuietPlayManager", "startPlay context or audioUri is null.");
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
        if(bMute) {
            audioManager.requestAudioFocus(this.afChangeListener, 3, 2);
        } else {
            audioManager.abandonAudioFocus(this.afChangeListener);
            this.afChangeListener = null;
        }
    }

    static class SingletonHolder {
        static AudioQuietPlayManager sInstance = new AudioQuietPlayManager();

        SingletonHolder() {
        }
    }
}
