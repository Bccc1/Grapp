package com.dsi11.grapp;

import android.location.Location;
import android.util.Log;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;
import com.dsi11.grapp.Core.Tag;
import com.dsi11.grapp.Core.TagImage;
import com.dsi11.grapp.Parse.PGang;
import com.dsi11.grapp.Parse.PPlayer;
import com.dsi11.grapp.Parse.PTag;
import com.dsi11.grapp.Parse.PTagImage;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 03.01.2015.
 */
public class ParseDao {

    public static Player addPlayer(Player mPlayer) {
        //code für den Fall das Gang und Player neu sind
        PPlayer player = PPlayer.create();
        player.setName(mPlayer.name);
        if (mPlayer.gang != null) {
            PGang gang = createPGangFromGang(mPlayer.gang);
            player.setGang(gang);
        }
        player.setLeader(mPlayer.leader == null ? false : mPlayer.leader.booleanValue());
        savePPlayer(player);
        Player newPlayer = parseObjectAsPlayer(player);
        return newPlayer;
    }

    public static Player updatePlayer(Player mPlayer) {
        //TODO Konzept überdenken, evtl splitten in updatePlayer und updatePlayerWithGang und updatePlayerWithGangAndTagImage
        PPlayer player = getPPlayerById(mPlayer.id);
        player.setName(mPlayer.name);
        player.setLeader(mPlayer.leader);
        if (mPlayer.gang != null) {
            if (mPlayer.gang.id != null) {
                PGang gang = null;
                if (mPlayer.gang.id.equals(player.getGangId())) {
                    //existierende Gang wurde verändert
                    gang = player.getGang();
                    gang.setName(mPlayer.gang.name);
                    gang.setColor(mPlayer.gang.color);
                    gang.setTag(tagImageAsParseObject(mPlayer.gang.tag));
                } else {
                    //Referenz auf andere existierende Gang
                    gang = PGang.createWithoutData(mPlayer.gang.id);
                }
                player.setGang(gang);
            } else {
                //neue Gang
                player.setGang(addGangRaw(mPlayer.gang));
            }
        } else {
            //Player hat keine Gang. Dieser Zustand ist im Update nicht unterstützt.
        }
        savePPlayer(player);
        return parseObjectAsPlayer(player);
    }

    private static PGang createPGangFromGang(Gang mGang){
        PGang gang = PGang.create();
        gang.setName(mGang.name);
        gang.setColor(mGang.color);
        //gang.setTag(mGang.tag);
        return gang;
    }

    public static void savePPlayer(PPlayer player){
        try {
            player.save();
        } catch (ParseException e) {
            e.printStackTrace();
            //return false;
        }
        return;
    }

    public static void addPGang(PGang gang){
        try {
            gang.save();
        } catch (ParseException e) {
            e.printStackTrace();
            //return false;
        }
        return;
    }

    /** Speichert (hoffentlich) nur die Gang und geht den referenzierten Objekten nicht nach*/
    public static Gang saveGangWithoutReferences(Gang mGang){
        PGang gang = PGang.create();
        if(mGang.id != null){
            gang.setObjectId(mGang.id);
        }
        gang.setName(mGang.name);
        gang.setColor(mGang.color);
        try {
            gang.save();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        List<Gang> newGang = getGangByName(mGang.name);
        return newGang.isEmpty() ? null : newGang.get(0);
    }

    public static PGang addGangRaw(Gang mGang){
        PGang gang = new PGang();
        gang.setName(mGang.name);
        gang.setColor(mGang.color);
        if(mGang.tag!=null){
            gang.setTag(tagImageAsParseObject(mGang.tag));
        }
        addPGang(gang);
        return gang;
    }

    public static Gang addGang(Gang mGang){
        PGang gang = addGangRaw(mGang);
        Gang result = parseObjectAsGang(gang);
        return result;
    }

    private static PPlayer getPPlayerById(String id){
        PPlayer player = null;
        ParseQuery<PPlayer> query = ParseQuery.getQuery(PPlayer.class);
        try {
            player = query.get(id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return player;
    }

    public static Player getPlayerById(String id){
        Player player = null;
        PPlayer pplayer = getPPlayerById(id);
        if(pplayer!=null) {
            player=parseObjectAsPlayer(pplayer);
        }
        return player;
    }

    public static Player getPlayerByIdWithAllData(String id){
        Player player = null;
        PPlayer pplayer = getPPlayerById(id);
        if(pplayer!=null) {
            player=parseObjectAsPlayerWithAllData(pplayer);
        }
        return player;
    }

    public static List<Player> getGangMembersByGangId(String gangId){
        List<PPlayer> pplayers = getPPlayersByGangId(gangId);
        List<Player> players = new ArrayList<Player>();
        for(PPlayer player : pplayers){
            players.add(parseObjectAsPlayer(player));
        }
        return players;
    }

    private static List<PPlayer> getPPlayersByGangId(String gangId){
        PGang gang = PGang.createWithoutData(gangId);
        List<PPlayer> players = new ArrayList<PPlayer>();
        ParseQuery<PPlayer> query = ParseQuery.getQuery(PPlayer.class);
        query.whereEqualTo(PPlayer.COLUMN_GANG,gang);
        try {
            players = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return players;
    }

    public static List<Player> getAllPlayers(){
        final ArrayList<Player> playerList = new ArrayList<>();
        ParseQuery<PPlayer> query = ParseQuery.getQuery(PPlayer.class);
        try {
            List<PPlayer> objList = query.find();
            for(PPlayer obj : objList){
                playerList.add(parseObjectAsPlainPlayer(obj));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return playerList;
    }

    /**
     * Returns a List of Players with this name. Should always be only one item.
     * If none is found, an empty List is returned.
     * @param name The name to search for. Must match exactly.
     * @return A List of Player
     */
    public static List<Player> getPlayerByName(String name){
        final ArrayList<Player> playerList = new ArrayList<Player>();
        ParseQuery<PPlayer> query = ParseQuery.getQuery(PPlayer.class);
        query.include(PGang.CLASS_NAME);
        query.whereEqualTo(PPlayer.COLUMN_NAME,name);
        try {
            List<PPlayer> objectList = query.find();
            for(PPlayer obj : objectList)
                playerList.add(parseObjectAsPlayer(obj));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return playerList;
    }

    public static PGang getPGangById(String id){
        PGang gang = null;
        ParseQuery<PGang> query = ParseQuery.getQuery(PGang.class);
        try {
            gang = query.get(id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return gang;
    }

    public static Gang getGangById(String id){
        Gang gang = null;
        PGang pgang = getPGangById(id);
        if(pgang!=null) {
            gang = fullyParseObjectAsGang(pgang);
        }
        return gang;
    }

    /**
     * Returns a List of Gangs with this name. Should always be only one item.
     * If none is found, an empty List is returned.
     * @param name The name to search for. Must match exactly.
     * @return A List of Gangs
     */
    public static List<Gang> getGangByName(String name){
        final ArrayList<Gang> gangList = new ArrayList<Gang>();
        ParseQuery<PGang> query = ParseQuery.getQuery(PGang.class);
        query.whereEqualTo(PGang.COLUMN_NAME,name);
        try {
            List<PGang> list = query.find();
            for(PGang obj : list)
                gangList.add(parseObjectAsGang(obj));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return gangList;
    }

    //Das ist ein nichtrekursives hinzufügen
    public static void saveTag(Tag mTag){
        PTag tag = new PTag();
        tag.setPosition(new ParseGeoPoint(mTag.latitude, mTag.longitude));
        tag.setGangId(mTag.gang.id);
        tag.setTimestamp(mTag.timestamp);
        tag.saveInBackground();
    }

    public static List<Tag> getAllTagsFullyLoaded(){
        final ArrayList<Tag> tagList = new ArrayList<Tag>();
        ParseQuery<PTag> query = ParseQuery.getQuery(PTag.class);
        query.include(PGang.CLASS_NAME);
        query.include(PGang.CLASS_NAME + "." + PTagImage.CLASS_NAME);
        try {
            for(PTag tag : query.find()){
                tagList.add(parseObjectAsTagWithGangAndImage(tag));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tagList;
    }

    public static List<Tag> getAllTags(){
        final ArrayList<Tag> tagList = new ArrayList<Tag>();
        ParseQuery<PTag> query = ParseQuery.getQuery(PTag.class);
        try {
            for(PTag tag : query.find()){
                tagList.add(parseObjectAsTag(tag));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tagList;
    }

    /** Only Id and Name are set */
    private static Player parseObjectAsPlainPlayer(PPlayer pO){
        Player player = new Player();
        player.id = pO.getObjectId();
        player.name = pO.getName();
        return player;
    }


    private static Player parseObjectAsPlayer(PPlayer pO){
        Player player = new Player();
        player.id = pO.getObjectId();
        player.name = pO.getName();
        PGang pgang = pO.getGang();
        if(pgang!=null && pgang.getObjectId()!=null){//FIXME Bedingung ist doof -.-
            try {
                pgang.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Gang gang = parseObjectAsGang(pgang);
            player.gang = gang;
        }
        return player;
    }

    private static Player parseObjectAsPlayerWithAllData(PPlayer pO){
        Player player = parseObjectAsPlayer(pO);
        PGang pgang = pO.getGang();
        if(pgang!=null && pgang.getObjectId()!=null){//FIXME Bedingung ist doof -.-
            PTagImage tag = pgang.getTag();
            try {
                tag.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            player.gang.tag=parseObjectAsTagImage(tag);
        }
        return player;
    }


    private static Gang fullyParseObjectAsGang(PGang pO){
        Gang gang = parseObjectAsGang(pO);
        try {
            pO.getTag().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TagImage img = parseObjectAsTagImage(pO.getTag());
        gang.tag=img;
        return gang;
    }

    /**ohne tag*/
    private static Gang parseObjectAsGang(PGang pO){
        Gang gang = new Gang();
        gang.id = pO.getObjectId();
        gang.name = pO.getName();
        gang.color = pO.getColor();

        //Image wird nur als leeres Objekt mit ID geladen.
        //TagImage img = new TagImage();
        //img.id = pO.getTagId();
        //if(img.id!=null)
        //    gang.tag = img;

        return gang;
    }

    private static TagImage parseObjectAsTagImage(PTagImage pO){
        TagImage tag = new TagImage();
        tag.id = pO.getObjectId();
        byte[] bImage = pO.getImage();

        try {
            tag.image = (SerializablePath) Utils.deserialize(bImage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tag;
    }

    private static PTagImage tagImageAsParseObject(TagImage tI){
        PTagImage pO;
        if(tI.id != null){
            pO = getPTagImageById(tI.id);
        }else{
            pO = PTagImage.create();
        }
        byte[] bImage = null;
        try {
            bImage = Utils.serialize(tI.image);
            pO.setImage(bImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pO;
    }

    private static PTagImage getPTagImageById(String id) {
        PTagImage tagImage = null;
        ParseQuery<PTagImage> query = ParseQuery.getQuery(PTagImage.class);
        try {
            tagImage = query.get(id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tagImage;
    }


    private static Tag parseObjectAsTag(PTag pO){
        Tag tag = new Tag();
        tag.id = pO.getObjectId();
        tag.timestamp = pO.getTimestamp();
        tag.latitude = pO.getPosition().getLatitude();
        tag.longitude = pO.getPosition().getLongitude();
        //Gang wird nur als leeres Objekt mit ID geladen.
        Gang gang = new Gang();
        gang.id = pO.getGangId();
        if(gang.id!=null)
            tag.gang = gang;
        return tag;
    }

    private static Tag parseObjectAsTagWithGang(PTag pO){
        Tag tag = parseObjectAsTag(pO);
        try {
            pO.getGang().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tag.gang=parseObjectAsGang(pO.getGang());
        return tag;
    }

    private static Tag parseObjectAsTagWithGangAndImage(PTag pO){
        Tag tag = parseObjectAsTag(pO);
        try {
            pO.getGang().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tag.gang=fullyParseObjectAsGang(pO.getGang());
        return tag;
    }

    private static PTag tagAsParseObject(Tag mTag){
        PTag tag = null;
        if(mTag!=null) {
            if (mTag.id != null) {
                //TODO tag = getTagById(mTag.id);
            } else {
                tag = PTag.create();
            }
            tag.setPosition(new ParseGeoPoint(mTag.latitude, mTag.longitude));
            tag.setTimestamp(mTag.timestamp);
            if(mTag.gang != null && mTag.gang.id != null)
                tag.setGang(PGang.createWithoutData(mTag.gang.id));
        }
        return tag;
    }

    public static Tag addTag(Tag mTag){
        PTag tag = tagAsParseObject(mTag);
        addPTag(tag);
        Tag newTag = parseObjectAsTag(tag);
        return newTag;
    }

    public static void addTagEventually(Tag mTag){
        PTag tag = tagAsParseObject(mTag);
        addPTagEventually(tag);
        return;
    }

    public static PTag addPTag(PTag tag){
        try {
            tag.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tag;
    }

    public static PTag addPTagEventually(PTag tag){
        tag.saveEventually();
        return tag;
    }

    public static List<Gang> getAllGangs() {
        List<Gang> gangs = new ArrayList<Gang>();
        for(PGang pg : getAllPGangs()){
            Gang g = parseObjectAsGang(pg);
            gangs.add(g);
        }
        return gangs;
    }

    public static List<PGang> getAllPGangs(){
        List<PGang> gangs = null;
        ParseQuery<PGang> query = new ParseQuery<PGang>(PGang.class);
        try {
            gangs = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return gangs;
    }

    public static List<Gang> getAllGangsWithTagImages() {
        List<Gang> gangs = new ArrayList<Gang>();
        for(PGang pg : getAllPGangsWithTagImages()){
            Gang g = fullyParseObjectAsGang(pg);
            gangs.add(g);
        }
        return gangs;
    }

    public static List<PGang> getAllPGangsWithTagImages(){
        List<PGang> gangs = null;
        ParseQuery<PGang> query = new ParseQuery<PGang>(PGang.class);
        query.include(PTagImage.CLASS_NAME);
        try {
            gangs = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return gangs;
    }
}

