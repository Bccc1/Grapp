package com.dsi11.grapp;

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
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 03.01.2015.
 */
public class ParseDao {

    public static Player savePlayer(Player mPlayer) {
        /** das Speichern des Leaders ist ein blöder Sonderfall, +
         * da wenn der Player auf die Gang verweist und diese als Leader wieder auf den selben Player verweist,
         * eine "circular dependency" vorliegt.
         * */
        boolean playerIsLeader = false;

        PPlayer player = PPlayer.create();
        player.setName(mPlayer.name);
        if (mPlayer.gang != null) {
            if (mPlayer.gang.leader != null) {
                playerIsLeader = mPlayer.gang.leader.name.equals(mPlayer.name);
                Log.i("Superimportantstuff!!!!!!!!!!Superimportantstuff","Player is Leader is "+playerIsLeader);
            }
            PGang gang = PGang.create();
            gang.setName(mPlayer.gang.name);
            gang.setColor(mPlayer.gang.color);
            if (!playerIsLeader) {
                gang.setLeaderId(mPlayer.gang.leader.id);
            }
            player.setGang(gang);
        }
        try {
            player.save();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        Log.i("Superimportantstuff!!!!!!!!!!Superimportantstuff","Player saved");
        List<Player> newPlayers = getPlayerByName(mPlayer.name);
        Player newPlayer = null;
        if (!newPlayers.isEmpty()) {
            newPlayer = newPlayers.get(0); //Wenn das null ist weiß ich auch nicht mehr weiter ;)
            Log.i("Superimportantstuff!!!!!!!!!!Superimportantstuff","Player loaded");
            if (playerIsLeader) {
                //hier die Gang nochmal speichern mit Referenz auf Player
                newPlayer.gang.leader = newPlayer;
                saveGangWithoutReferences(newPlayer.gang);
            }
        }
        return newPlayer;
    }

    /** Speichert (hoffentlich) nur die Gang und geht den referenzierten Objekten nicht nach*/
    public static Gang saveGangWithoutReferences(Gang mGang){
        PGang gang = PGang.create();
        if(mGang.id != null){
            gang.setId(mGang.id);
        }
        gang.setName(mGang.name);
        gang.setColor(mGang.color);
        gang.setLeaderId(mGang.leader.id);
        try {
            gang.save(); //FIXME Ist das Async? Wenn ja, darf ich hier noch kein Get machen.
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        Log.i("Superimportantstuff!!!!!!!!!!Superimportantstuff","Gang saved");
        List<Gang> newGang = getGangByName(mGang.name);
        return newGang.isEmpty() ? null : newGang.get(0);
    }

    public static Gang saveGang(Gang mGang){
        PGang gang = new PGang();
        gang.setName(mGang.name);
        gang.setColor(mGang.color);
        gang.setLeaderId(mGang.leader.id);
        gang.saveInBackground(); //FIXME Ist das Async? Wenn ja, darf ich hier noch kein Get machen.
        List<Gang> newGang = getGangByName(mGang.name);
        return newGang.isEmpty() ? null : newGang.get(0);
    }

    public static Player getPlayerById(String id){
        Player player = null;
        final ArrayList<Player> dirtyHack = new ArrayList<Player>();
        ParseQuery<PPlayer> query = ParseQuery.getQuery(PPlayer.class);
        query.getInBackground(id, new GetCallback<PPlayer>() {
            public void done(PPlayer object, ParseException e) {
                if (e == null) {
                    dirtyHack.add(parseObjectAsPlayer( object));
                } else {
                    // something went wrong
                }
            }
        });
        if(!dirtyHack.isEmpty())
            player = dirtyHack.get(0);

        return player;
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

    public static Gang getGangById(String id){
        Gang gang = null;
        final ArrayList<Gang> dirtyHack = new ArrayList<Gang>();
        ParseQuery<PGang> query = ParseQuery.getQuery(PGang.class);
        query.getInBackground(id, new GetCallback<PGang>() {
            public void done(PGang object, ParseException e) {
                if (e == null) {
                    dirtyHack.add(parseObjectAsGang(object));
                } else {
                    // something went wrong
                }
            }
        });
        if(!dirtyHack.isEmpty())
            gang = dirtyHack.get(0);

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

        query.findInBackground(new FindCallback<PGang>() {
            public void done(List<PGang> objectList, ParseException e) {
                if (e == null) {
                    for(PGang obj : objectList)
                        gangList.add(parseObjectAsGang(obj));
                } else {
                    // something went wrong
                }
            }
        });
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

    public static List<Tag> getAllTags(){
        final ArrayList<Tag> tagList = new ArrayList<Tag>();
        ParseQuery<PTag> query = ParseQuery.getQuery(PTag.class);
//        query.findInBackground(new FindCallback<PTag>() {
//            public void done(List<PTag> objectList, ParseException e) {
//                if (e == null) {
//                    for(PTag obj : objectList)
//                        tagList.add(parseObjectAsTag(obj));
//                } else {
//                    // something went wrong
//                }
//            }
//        });
        try {
            for(PTag tag : query.find()){
                tagList.add(parseObjectAsTag(tag));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tagList;
//        List<Tag> tags = new ArrayList<Tag>();
//        Tag t1 = new Tag();
//        t1.id="lkjs902fs0";
//        t1.latitude=52.852318;
//        t1.longitude=8.723670;
//        tags.add(t1);
//        return tags;
    }

    private static Player parseObjectAsPlayer(PPlayer pO){
        Player player = new Player();
        player.id = pO.getId();
        player.name = pO.getName();

        PGang pgang = pO.getGang();
        if(pgang!=null && pgang.getId()!=null){
            Gang gang = new Gang();
            gang.id=pgang.getId();
            gang.color=pgang.getColor();
            gang.name=pgang.getName();
            if(pgang.getLeader()!=null){
                Player leader = new Player();
                leader.id = pgang.getLeader().getId();
                //TODO Andere Vars füllen
                gang.leader=leader;
            }
            player.gang = gang;
        }
        return player;
    }


    private static Gang fullyParseObjectAsGang(PGang pO){
        Gang gang = parseObjectAsGang(pO);
        //lade Player
        //lade Tag
        return gang;
    }

    private static Gang parseObjectAsGang(PGang pO){
        Gang gang = new Gang();
        gang.name = pO.getName();
        gang.color = pO.getColor();

        //Leader wird nur als leeres Objekt mit ID geladen.
        Player player = new Player();
        player.id = pO.getLeaderId();
        if(player!=null)
            gang.leader = player;

        //Image wird nur als leeres Objekt mit ID geladen.
        TagImage img = new TagImage();
        img.id = pO.getTagId();
        if(img.id!=null)
            gang.tag = img;

        return gang;
    }

    private static TagImage parseObjectAsTagImage(PTagImage pO){
        TagImage tag = new TagImage();
        tag.id = pO.getId();
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
            pO = PTagImage.createWithoutData(tI.id);
        }else{
            pO = new PTagImage();
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


    private static Tag parseObjectAsTag(PTag pO){
        Tag tag = new Tag();
        tag.id = pO.getId();
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
}

