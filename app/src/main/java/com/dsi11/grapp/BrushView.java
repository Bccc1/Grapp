package com.dsi11.grapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by David on 05.01.15.
 */
public class BrushView extends View {
    private Paint brush = new Paint();
    private SerializablePath path = new SerializablePath();

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
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }

        // Force a view to draw again
        postInvalidate();

        return false;
    }

    public void reset(){
        //reset the path
        path.reset();
        //invalidate the view
        postInvalidate();
    }


}
