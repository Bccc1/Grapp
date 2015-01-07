package com.dsi11.grapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by David on 05.01.15.
 */
public class TagImageHelper {

    public class RenderSettings{
        public static final int Original = 0;
        public static final int SquareZoomed = 1;
        public static final int Icon = 2;
    }

    public static Bitmap tagAsBitmap(SerializablePath path, Integer color){
        return tagAsBitmap(path,color,RenderSettings.Original);
    }

    public static Bitmap tagAsBitmapIcon(SerializablePath path, Integer color){
        return tagAsBitmapSquareZoomed(path,color,100);
    }

    public static Bitmap tagAsBitmap(SerializablePath path, Integer color, Integer params){
        Bitmap bitmap = null;
        if(path == null){
            Log.w("TagImageHelper", "SerializablePath is null");
        }else{
            if(path.getActions()==null){
                Log.w("TagImageHelper", "SerializablePath.actions is null");
            }else{
                if(path.getActions().isEmpty()){
                    Log.i("TagImageHelper", "SerializablePath.actions is empty");
                }
                switch (params){
                    case RenderSettings.Original: {
                        bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888); //FIXME Größe ermitteln
                        Canvas canvas = new Canvas(bitmap);
                        Paint brush = new Paint();
                        brush.setAntiAlias(true);
                        brush.setColor(color == null ? Color.BLACK : color);
                        brush.setStyle(Paint.Style.STROKE);
                        brush.setStrokeJoin(Paint.Join.ROUND);
                        brush.setStrokeWidth(10f);
                        canvas.drawPath(path, brush);
                    }
                        break;
                    case RenderSettings.SquareZoomed:
                        bitmap = tagAsBitmapSquareZoomed(path,color,200);
                        break;
                    default: {
                        bitmap = Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_8888); //FIXME Größe ermitteln
                        Canvas canvas = new Canvas(bitmap);
                        Paint brush = new Paint();
                        brush.setAntiAlias(true);
                        brush.setColor(color == null ? Color.BLACK : color);
                        brush.setStyle(Paint.Style.STROKE);
                        brush.setStrokeJoin(Paint.Join.ROUND);
                        brush.setStrokeWidth(10f);
                        canvas.drawPath(path, brush);
                    }
                        break;
                }

            }
        }

        return bitmap;
    }

    private static Bitmap tagAsBitmapSquareZoomed(SerializablePath path, Integer color, Integer size) {
        Bitmap bitmap = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint brush = new Paint();
        brush.setAntiAlias(true);
        brush.setColor(color == null ? Color.BLACK : color);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        float strokeWidth = 0.05f*size; //10f;    //TODO errechne Stroke Width aus Size
        brush.setStrokeWidth(strokeWidth);
        SerializablePath newPath = scalePathToSquare(path,size,strokeWidth);
        canvas.drawPath(newPath, brush);
        return bitmap;
    }

    private static SerializablePath scalePathToSquare(SerializablePath path, float size, float strokeWidth){
        float scale = 1;
        float offsetX = 0;
        float offsetY = 0;

        ArrayList<SerializablePath.PathAction> actions = path.getActions();
        float Xmin=Float.MAX_VALUE, Ymin=Float.MAX_VALUE;
        float Xmax=Float.MIN_VALUE, Ymax=Float.MIN_VALUE;
        for(SerializablePath.PathAction a : actions){
            if(a.getX()<Xmin)
                Xmin=a.getX();
            if(a.getX()>Xmax)
                Xmax=a.getX();
            if(a.getY()<Ymin)
                Ymin=a.getY();
            if(a.getY()>Ymax)
                Ymax=a.getY();
        }

        offsetX = (strokeWidth/2)-Xmin;
        offsetY = (strokeWidth/2)-Ymin;
        float XDelta = Xmax-Xmin;
        float YDelta = Ymax-Ymin;
        if(XDelta > YDelta){
            scale = size/(XDelta+strokeWidth);
        }else{
            scale = size/(YDelta+strokeWidth);
        }
        SerializablePath newPath = transformPath(path,offsetX,offsetY,scale);
        return newPath;
    }

    private static SerializablePath transformPath(SerializablePath path, float offsetX, float offsetY, float scale){
        ArrayList<SerializablePath.PathAction> actions = path.getActions();
        ArrayList<SerializablePath.PathAction> newActions = new ArrayList<SerializablePath.PathAction>();
        for(SerializablePath.PathAction a: actions){
            SerializablePath.PathAction ac = SerializablePath.createPathAction(
                    (a.getX() + offsetX) * scale,
                    (a.getY() + offsetY) * scale,
                    a.getType());
            newActions.add(ac);
        }
        SerializablePath newPath = new SerializablePath();
        newPath.setActions(newActions);
        newPath.drawThisPath();
        return newPath;
    }
}
