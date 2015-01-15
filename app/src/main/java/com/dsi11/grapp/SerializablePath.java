package com.dsi11.grapp;

import android.graphics.Path;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 05.01.15.
 */
public class SerializablePath extends Path implements Serializable {

    private static final long serialVersionUID = -5974912367682897467L;

    private ArrayList<PathAction> actions = new ArrayList<SerializablePath.PathAction>();

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        drawThisPath();
    }

    //temp dummy method
    public ArrayList<PathAction> getActions(){
        return actions;
    }

    public void setActions(ArrayList<PathAction> actions){
        this.actions = actions;
    }

    @Override
    public void moveTo(float x, float y) {
        actions.add(new ActionMove(x, y));
        super.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y){
        actions.add(new ActionLine(x, y));
        super.lineTo(x, y);
    }

    @Override
    public void reset() {
        super.reset();
        actions.clear();
    }

    public void removeLastLine(){
        PathAction latestAction = actions.get(actions.size() - 1);
        boolean removedLine = false;
        //latestAction.getType() == PathAction.PathActionType.LINE_TO
        while (!removedLine) {
            actions.remove(actions.size() - 1);
            if (latestAction.getType() == PathAction.PathActionType.LINE_TO) {
                removedLine = true;
            }
            latestAction = actions.get(actions.size() - 1);
        }
    }

    /** Transfers the serializable stored Path to the actual Path */
    public void drawThisPath(){
        super.reset();
        for(PathAction p : actions){
            if(p.getType().equals(PathAction.PathActionType.MOVE_TO)){
                super.moveTo(p.getX(), p.getY());
            } else if(p.getType().equals(PathAction.PathActionType.LINE_TO)){
                super.lineTo(p.getX(), p.getY());
            }
        }
    }

    public interface PathAction {
        public enum PathActionType {LINE_TO,MOVE_TO};
        public PathActionType getType();
        public float getX();
        public float getY();
    }

    public static PathAction createPathAction(float x, float y, PathAction.PathActionType type){
        if(type== PathAction.PathActionType.LINE_TO){
            return new ActionLine(x,y);
        }
        if(type== PathAction.PathActionType.MOVE_TO){
            return new ActionMove(x,y);
        }
        return null;
    }

    public static class ActionMove implements PathAction, Serializable{
        private static final long serialVersionUID = -7198142191254133295L;

        private float x,y;

        public ActionMove(float x, float y){
            this.x = x;
            this.y = y;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.MOVE_TO;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

    }

    public static class ActionLine implements PathAction, Serializable{
        private static final long serialVersionUID = 8307137961494172589L;

        private float x,y;

        public ActionLine(float x, float y){
            this.x = x;
            this.y = y;
        }

        @Override
        public PathActionType getType() {
            return PathActionType.LINE_TO;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }

    }
}
