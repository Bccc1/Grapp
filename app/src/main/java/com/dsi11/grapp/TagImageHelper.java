package com.dsi11.grapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by David on 05.01.15.
 */
public class TagImageHelper {
    public static Bitmap tagAsBitmap(SerializablePath path, Color color){
        Bitmap bitmap = Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_8888); //FIXME Größe ermitteln
        Canvas canvas = new Canvas(bitmap);
        Paint brush = new Paint();
        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(10f);

        canvas.drawPath(path, brush);
        return bitmap;
    }
}
