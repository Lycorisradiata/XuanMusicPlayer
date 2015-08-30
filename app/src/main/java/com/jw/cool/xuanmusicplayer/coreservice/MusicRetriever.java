/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jw.cool.xuanmusicplayer.coreservice;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.jw.cool.xuanmusicplayer.utils.HandlerString;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Retrieves and organizes media to play. Before being used, you must call {@link #prepare()},
 * which will retrieve all of the music on the user's device (by performing a query on a content
 * resolver). After that, it's ready to retrieve a random song, with its title and URI, upon
 * request.
 */
public class MusicRetriever {
    final static String TAG = "MusicRetriever";

    ContentResolver mContentResolver;

    // the items (songs) we have queried
    static List<Item> mItems = new ArrayList<Item>();
    static int currentPos;
    static int playMode;

    static Random mRandom = new Random();

    public MusicRetriever(ContentResolver cr) {
        mContentResolver = cr;
    }

    /**
     * Loads music data. This method may take long, so be sure to call it asynchronously without
     * blocking the main thread.
     */
    public void prepare() {
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.i(TAG, "Querying media...");
        Log.i(TAG, "URI: " + uri.toString());

        // Perform a query on the content resolver. The URI we're passing specifies that we
        // want to query for all audio media on external storage (e.g. SD card)
        Cursor cur = mContentResolver.query(uri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));

        if (cur == null) {
            // Query failed...
            Log.e(TAG, "Failed to retrieve music: cursor is null :-(");
            return;
        }
        if (!cur.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            Log.e(TAG, "Failed to move cursor to first row (no query results).");
            return;
        }

        Log.i(TAG, "Listing...");

        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        int fileName = cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int albumId = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        int albumArt = cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);
        ;
        Log.i(TAG, "Title column index: " + String.valueOf(titleColumn));
        Log.i(TAG, "ID column index: " + String.valueOf(titleColumn));

        // add each song to mItems
        do {
            String displayName = HandlerString.getFileNameNoEx(cur.getString(fileName));
//            String albumArts = cur.getString(albumArt);
//            Log.d(TAG, "prepare albumArts " + albumArts);
//            Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn)
//            + " DISPLAY_NAME " + cur.getString(fileName));
            mItems.add(new Item(
                    cur.getLong(idColumn),
                    cur.getString(artistColumn),
                    cur.getString(titleColumn),
                    cur.getString(albumColumn),
                    cur.getLong(durationColumn),
                    displayName,
                    cur.getLong(albumId)
                    ));

        } while (cur.moveToNext());


        Uri uri2 = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "prepare uri2 " + uri2);
//        for(int i = 0; i < mItems.size(); i++){
//            int id =  (int)mItems.get(i).getAlbumId();
            Cursor cur2 = mContentResolver.query(uri2, null,
                    null, null, null);
        int size = cur2.getColumnCount();

//        Log.d(TAG, "prepare getCount" + cur2.getCount());
//            cur2.moveToFirst();
//            do{
//                for (int i = 0; i < size; i ++){
//                    Log.d(TAG, "prepare " + cur2.getColumnName(i) + " " + cur2.getCount());
//                    String str = cur2.getString(cur2.getColumnIndex(cur2.getColumnName(i)));
//                    Log.d(TAG, "prepare str " + str);
//                }
//            }while (cur2.moveToNext());
//        }

        Log.i(TAG, "Done querying media. MusicRetriever is ready.");
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /** Returns a random Item. If there are no items available, returns null. */
    public Item getRandomItem() {
        if (mItems.size() <= 0) return null;
        return mItems.get(mRandom.nextInt(mItems.size()));
    }

    public static List<Item> getItems(){
        return mItems;
    }

    public static Item getCurrentItem(){
        return mItems.get(currentPos);
    }

    public static Item getNextItem(){
        Item item = null;
        switch (playMode){
            case PlayMode.all_order:
                if(currentPos < mItems.size() - 1)
                item = mItems.get(++currentPos);
                break;
            case PlayMode.all_repeat:
                currentPos = (++currentPos)%mItems.size();
                item = mItems.get(currentPos);
                break;
            case PlayMode.random:
                currentPos = mRandom.nextInt(mItems.size());
                item = mItems.get(currentPos);
                break;
            case PlayMode.one_repeat:
                item = mItems.get(currentPos);
                break;
            case PlayMode.one_once:
                break;
            default:
        }
        return item;
    }

    public static Item getPreviousItem(){
        Item item = null;
        switch (playMode){
            case PlayMode.all_order:
                if(currentPos > 0)
                    item = mItems.get(--currentPos);
                break;
            case PlayMode.all_repeat:
                currentPos = (--currentPos + mItems.size())%mItems.size();
                item = mItems.get(currentPos);
                break;
            case PlayMode.random:
                currentPos = mRandom.nextInt(mItems.size());
                item = mItems.get(currentPos);
                break;
            case PlayMode.one_repeat:
                item = mItems.get(currentPos);
                break;
            case PlayMode.one_once:
                break;
            default:
        }
        Log.d(TAG, "getPreviousItem currentPos " + currentPos);
        return item;
    }



    public static void setCurrentPos(int pos){
        currentPos = pos;
    }

    public static int getCurrentPos(){
        return currentPos;
    }

    public static void setPlayMode(int mode){
        playMode = mode;
    }

    public static int getPlayMode(){
        return playMode;
    }


    public static class Item {
        long id;
        String artist;
        String title;
        String album;
        long duration;
        String displayName;
        long albumId;

        public Item(long id, String artist, String title, String album,
                    long duration, String displayName, long albumId) {
            this.id = id;
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.duration = duration;
            this.displayName = displayName;
            this.albumId = albumId;
        }

        public long getId() {
            return id;
        }

        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getAlbum() {
            return album;
        }

        public long getDuration() {
            return duration;
        }
        public String getDisplayName() {
            return displayName;
        }

        public long getAlbumId() {
            return albumId;
        }


        public Uri getURI() {
            return ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        }
    }
}