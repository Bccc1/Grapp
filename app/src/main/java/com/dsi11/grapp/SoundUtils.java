package com.dsi11.grapp;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by David on 22.01.2015.
 */
public class SoundUtils {

    static SoundPool soundPool;
    static HashMap<Integer, Integer> soundPoolMap;
    static int sprayLoopSoundID = 1;
    static int btnClickSoundID = 2;

    static final int no_loop = 0;
    static final int loop = -1;
    static float normal_playback_rate = 1f;
    static float leftVolume = 0;
    static float rightVolume = 0;

    public static void init(Context context){
//        SoundPool.Builder soundPoolBuilder = new SoundPool.Builder();
//        soundPoolBuilder.setMaxStreams(4);
//        AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder();
//        audioAttributesBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION);
//        audioAttributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
//        soundPoolBuilder.setAudioAttributes(audioAttributesBuilder.build());
//        soundPool = soundPoolBuilder.build();
//        Der Auskommentierte Code wäre besser, aber da wir API 18 unterstützen nicht möglich.
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

        sprayLoopSoundID = soundPool.load(context, R.raw.paint_spray_loop, 1);
        btnClickSoundID = soundPool.load(context, R.raw.paint_spray_loop, 1);

        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        leftVolume = curVolume/maxVolume;
        rightVolume = curVolume/maxVolume;
    }

    public static void playSprayLoop(){
        int priority = 1;
        soundPool.play(btnClickSoundID, leftVolume, rightVolume, priority, loop, normal_playback_rate);
    }

    public static void stopSprayLoop(){
        soundPool.stop(sprayLoopSoundID);
    }

    public static void playButtonClick(){
        int priority = 1;
        float normal_playback_rate = 1f;
        soundPool.play(btnClickSoundID, leftVolume, rightVolume, priority, no_loop, normal_playback_rate);
    }
}
