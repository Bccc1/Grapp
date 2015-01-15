package com.dsi11.grapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

/**
 * Created by David on 05.01.15.
 */
public class BrushView extends View {

    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;
    int soundID = 1;
    int sprayStreamId;

    public void setColor(int color){
        brush.setColor(color);
        //set background color according to luminance of the brush color
        if (Color.red(color) + Color.green(color) + Color.blue(color) < 384)
            setBackgroundColor(Color.WHITE);
        else
            setBackgroundColor(Color.BLACK);
        //Force a view to draw again
        postInvalidate();
    }

    private Paint brush = new Paint();
    private SerializablePath path = new SerializablePath();

    public SerializablePath getPath(){
        return path;
    }

    public void setPath(SerializablePath path){
        this.path = path;
    }

    public BrushView(Context context) {
        super(context);
        initialise();
    }

    public BrushView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    private void initialise(){
        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(10f);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(soundID, soundPool.load(getContext(), R.raw.paint_spray_can_spraying_single_line, 1));
    }

    public void undo(){
        path.removeLastLine();
        path.drawThisPath();
        // Force a view to draw again
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, brush);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(pointX, pointY);
                playSpraySound();
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_UP:
                stopSpraySound();
                break;
            default:
                return false;
        }

        // Force a view to draw again
        postInvalidate();

        return false;
    }

    private void playSpraySound(){
        AudioManager audioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume/maxVolume;
        float rightVolume = curVolume/maxVolume;
        int priority = 1;
        int no_loop = 0;
        int loop = -1;
        float normal_playback_rate = 1f;
        sprayStreamId = soundPool.play(soundID, leftVolume, rightVolume, priority, loop, normal_playback_rate);

    }

    private void stopSpraySound(){
        soundPool.stop(sprayStreamId);
    }

    public void reset(){
        //reset the path
        path.reset();
        //invalidate the view
        postInvalidate();
    }


}
