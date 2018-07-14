package com.lu.mbcontrol.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.lu.mbcontrol.R;

import java.util.HashMap;

/**
 * Created by LU on 2018/5/11.
 */

public class SoundPlay {
    private static SoundPool sp;
    private static float volume;
    private static int currentID;
    private static HashMap<Integer, Integer> hm;

    public static void initSoundPool(Context context) {
        sp=new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        hm =new HashMap<Integer, Integer>();
        hm.put(1, sp.load(context.getApplicationContext(), R.raw.click, 1));
        playSound(1,0,context);
    }
    /** 播放即时音效*/
    public static void playSound(int num,int loop,Context context){
        AudioManager am=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float currentSound=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxSound=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume=currentSound/maxSound;
        currentID=sp.play(hm.get(num), volume, volume, 1, loop, 1.0f);
    }
}
