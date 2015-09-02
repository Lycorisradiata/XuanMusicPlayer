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

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
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
    private static MusicRetriever instance;
    private  Context context;
    ContentResolver contentResolver;
    List<MediaInfo> items;
    int currentPos;
    int playMode;
    Random random ;
    private MusicRetriever(Context context){
        this.context = context;
        contentResolver = context.getContentResolver();
        items = new ArrayList<MediaInfo>();
    }

    public static void initInstance(Context context){
        if(context != null && instance == null){
            synchronized (MusicRetriever.class){
                instance = new MusicRetriever(context);
            }
        }
    }

    public static MusicRetriever getInstance(){
        return instance;
    }

    public List<PlayList> getPlaylist(){
        List<PlayList> list = new ArrayList<>();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cur = contentResolver.query(uri, null, null, null, null);
        if (cur == null || !cur.moveToFirst()) {
            Log.d(TAG, "getPlaylist failed");
            return list;
        }

        int nameColumn = cur.getColumnIndex(MediaStore.Audio.Playlists.NAME);
        int dataColumn = cur.getColumnIndex(MediaStore.Audio.Playlists.DATA);
        int dateAddColumn = cur.getColumnIndex(MediaStore.Audio.Playlists.DATE_ADDED);
        int dateModifyColumn = cur.getColumnIndex(MediaStore.Audio.Playlists.DATE_MODIFIED);
        int countColumn = cur.getColumnIndex(MediaStore.Audio.Playlists._COUNT);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Playlists._ID);

        do{
            PlayList item = new PlayList(cur.getLong(idColumn),
                                        cur.getString(nameColumn),
                                        cur.getString(dataColumn),
                                        cur.getLong(dateAddColumn),
                                        cur.getLong(dateModifyColumn),
                                        cur.getInt(countColumn));
            list.add(item);
        }while (cur.moveToNext());
        return list;
    }

    public void addToPlaylist(List<PlaylistItem> list) {
        Log.d(TAG, "addToPlaylist ");
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        for(PlaylistItem item:list){
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", item.playListId);
            ops.add(ContentProviderOperation.newInsert(uri)
                    .withValue(MediaStore.Audio.Playlists.Members.PLAY_ORDER, item.playOrder)
                    .withValue(MediaStore.Audio.Playlists.Members.AUDIO_ID, item.audioId)
                    .withYieldAllowed(true)
                    .build());
        }

        try {
            getContentResolver().applyBatch(MediaStore.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }



    static class PlayList{
        long id;
        String name;
        String data;
        long dateAdd;
        long dateModified;
        int count;

        public PlayList(long id, String name, String data, long dateAdd, long dateModified, int count) {
            this.id = id;
            this.name = name;
            this.data = data;
            this.dateAdd = dateAdd;
            this.dateModified = dateModified;
            this.count = count;
        }
    }

    static class PlaylistItem{
        long playListId;
        long audioId;
        long playOrder;
        long _Id;
        String contentDirectory;

        public PlaylistItem(long playListId, long audioId, long playOrder, long _Id, String contentDirectory) {
            this.playListId = playListId;
            this.audioId = audioId;
            this.playOrder = playOrder;
            this._Id = _Id;
            this.contentDirectory = contentDirectory;
        }
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
        Cursor cur = contentResolver.query(uri, null,
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
//        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.DATA);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        int fileName = cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int albumId = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        int albumArt = cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);
        Log.i(TAG, "cur.getCount() : " + cur.getCount());

        // add each song to items
        do {
            String displayName = HandlerString.getFileNameNoEx(cur.getString(fileName));
            items.add(new MediaInfo(
                    cur.getLong(idColumn),
                    cur.getString(artistColumn),
                    cur.getString(titleColumn),
                    cur.getString(albumColumn),
                    cur.getLong(durationColumn),
                    displayName,
                    cur.getLong(albumId)
                    ));

        } while (cur.moveToNext());
        Log.i(TAG, "Done querying media. MusicRetriever is ready.");
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    /** Returns a random MediaInfo. If there are no items available, returns null. */
    public MediaInfo getRandomItem() {
        if (items.size() <= 0) return null;
        return items.get(getRandomPos());
    }

    int getRandomPos(){
        if(random == null){
            random = new Random();
        }

        return random.nextInt(items.size());
    }


    public List<MediaInfo> getItems(){
        return items;
    }

    public MediaInfo getCurrentItem(){
        return items.get(currentPos);
    }

    public MediaInfo getNextItem(){
        MediaInfo item = null;
        switch (playMode){
            case PlayMode.all_order:
                if(currentPos < items.size() - 1)
                item = items.get(++currentPos);
                break;
            case PlayMode.all_repeat:
                currentPos = (++currentPos)%items.size();
                item = items.get(currentPos);
                break;
            case PlayMode.random:
                currentPos = getRandomPos();
                item = items.get(currentPos);
                break;
            case PlayMode.one_repeat:
                item = items.get(currentPos);
                break;
            case PlayMode.one_once:
                break;
            default:
        }
        return item;
    }

    public MediaInfo getPreviousItem(){
        MediaInfo item = null;
        switch (playMode){
            case PlayMode.all_order:
                if(currentPos > 0)
                    item = items.get(--currentPos);
                break;
            case PlayMode.all_repeat:
                currentPos = (--currentPos + items.size())%items.size();
                item = items.get(currentPos);
                break;
            case PlayMode.random:
                currentPos = getRandomPos();
                item = items.get(currentPos);
                break;
            case PlayMode.one_repeat:
                item = items.get(currentPos);
                break;
            case PlayMode.one_once:
                break;
            default:
        }
        Log.d(TAG, "getPreviousItem currentPos " + currentPos);
        return item;
    }



    public void setCurrentPos(int pos){
        currentPos = pos;
    }

    public void setCurrentPos(MediaInfo item){
        Log.d(TAG, "setCurrentPos item " + item.getDisplayName());
        currentPos = items.indexOf(item);
        currentPos = currentPos == -1 ? 0 : currentPos;
    }


    public  int getCurrentPos(){
        return currentPos;
    }

    public  void setPlayMode(int mode){
        playMode = mode;
    }

    public  int getPlayMode(){
        return playMode;
    }



}
