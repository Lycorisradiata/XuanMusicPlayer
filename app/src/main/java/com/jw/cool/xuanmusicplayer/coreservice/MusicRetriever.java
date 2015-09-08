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
import android.content.ContentValues;
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
    List<MediaInfo> allItems;
    int currentPos;
    int playMode;
    boolean isPlaylistMode;
    List<MediaInfo> playlistItems;

    public boolean isRetrieverPrepared() {
        return isRetrieverPrepared;
    }

    public void setIsRetrieverPrepared(boolean isRetrieverPrepared) {
        this.isRetrieverPrepared = isRetrieverPrepared;
    }

    boolean isRetrieverPrepared;

    public boolean isPlaylistMode() {
        return isPlaylistMode;
    }

    public void setIsPlaylistMode(boolean isPlaylistMode) {
        this.isPlaylistMode = isPlaylistMode;
    }

    Random random ;
    private MusicRetriever(Context context){
        this.context = context;
        contentResolver = context.getContentResolver();
        allItems = new ArrayList<>();
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
            long id = cur.getLong(idColumn);
            String name = cur.getString(nameColumn);
            String data = cur.getString(dataColumn);
            long dateadd = cur.getLong(dateAddColumn);
            long datemodify = cur.getLong(dateModifyColumn);
//            int count = cur.getInt(countColumn);
            PlayList item = new PlayList(id,
                    name,
                    data,
                    dateadd,
                    datemodify,
                    0);
            list.add(item);
        }while (cur.moveToNext());
        for(PlayList item:list){
            Log.d(TAG, "getPlaylist list " + item.toString());
        }

        return list;
    }

    public List<MediaInfo> getPlaylistItems(long playlistId){
        if(playlistItems == null){
            playlistItems = new ArrayList<>();
        }else{
            playlistItems.clear();
        }

        if(playlistId <= 0){
            return playlistItems;
        }

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String[] showColumns = new String[]{MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.DURATION,
                MediaStore.Audio.Playlists.Members.DISPLAY_NAME,
                MediaStore.Audio.Playlists.Members.ALBUM_ID,
                MediaStore.Audio.Playlists.Members.DATA
        };

        Cursor cursor = getContentResolver().query(uri, showColumns,
                MediaStore.Audio.Playlists.Members.PLAYLIST_ID + "=?",
                new String[]{Long.toString(playlistId)},
                null);
        if(cursor == null || !cursor.moveToFirst()) {
            Log.d(TAG, "getPlaylistItems failed!");
            return playlistItems;
        }

        Log.d(TAG, "getPlaylistItems columns " + cursor.getColumnCount() + " " +cursor.getCount());

        do{
            long id = cursor.getLong(cursor.getColumnIndex(showColumns[0]));
            String artist = cursor.getString(cursor.getColumnIndex(showColumns[1]));
            String title = cursor.getString(cursor.getColumnIndex(showColumns[2]));
            String album = cursor.getString(cursor.getColumnIndex(showColumns[3]));
            long duration = cursor.getLong(cursor.getColumnIndex(showColumns[4]));
            String displayName = cursor.getString(cursor.getColumnIndex(showColumns[5]));
            long albumId = cursor.getLong(cursor.getColumnIndex(showColumns[6]));
            String path = cursor.getString(cursor.getColumnIndex(showColumns[7]));
            MediaInfo item = new MediaInfo(id, artist, title, album, duration, displayName, albumId, path);
            playlistItems.add(item);
        }while (cursor.moveToNext());
        cursor.close();
        return playlistItems;
    }

    public void addToPlaylist(List<PlaylistItem> list, long playlistId) {
        Log.d(TAG, "addToPlaylist ");
        if(list.size() == 0){
            return;
        }
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Uri uri = null;
        if(playlistId > 0){
            uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        }

        for(PlaylistItem item:list){
            if(playlistId < 0){
                uri = MediaStore.Audio.Playlists.Members.getContentUri("external", item.playListId);
            }

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

    public void removeSongList(List<Uri> list){
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (Uri uri : list) {
            ops.add(ContentProviderOperation.newDelete(uri)
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

    public long createPlaylist(String name){
        long id = -1;
        Log.d(TAG, "addToPlaylist ");
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Audio.Playlists.NAME, name);
        getContentResolver().insert(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, cv);

        List<PlayList> list = getPlaylist();
        for(PlayList item:list){
            if(item.getName().equals(name)){
                id = item.getId();
                Log.d(TAG, "createPlaylist match " + item.toString());
                break;
            }else{
                Log.d(TAG, "createPlaylist " + item.toString());
            }
        }
        return id;
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
        int dataColumn = cur.getColumnIndex(MediaStore.Audio.Media.DATA);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        int fileName = cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int albumId = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        int albumArt = cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);
        Log.i(TAG, "cur.getCount() : " + cur.getCount());

        // add each song to allItems
        do {
            String displayName = HandlerString.getFileNameNoEx(cur.getString(fileName));
            allItems.add(new MediaInfo(
                    cur.getLong(idColumn),
                    cur.getString(artistColumn),
                    cur.getString(titleColumn),
                    cur.getString(albumColumn),
                    cur.getLong(durationColumn),
                    displayName,
                    cur.getLong(albumId),
                    cur.getString(dataColumn)
            ));
        } while (cur.moveToNext());
        cur.close();
        Log.i(TAG, "Done querying media. MusicRetriever is ready.");
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    /** Returns a random MediaInfo. If there are no allItems available, returns null. */
    public MediaInfo getRandomItem() {
        if (getCurrentItems().size() <= 0) return null;
        return getCurrentItems().get(getRandomPos());
    }

    int getRandomPos(){
        if(random == null){
            random = new Random();
        }

        return random.nextInt(getCurrentItems().size());
    }


    public List<MediaInfo> getItems(){
        return allItems;
    }

    public MediaInfo getCurrentItem(){
        return getCurrentItems().get(currentPos);
    }

    List<MediaInfo> getCurrentItems(){
        if(isPlaylistMode){
            return playlistItems;
        }
        return allItems;
    }

    public MediaInfo getNextItem(){
        MediaInfo item = null;
        List<MediaInfo> items = getCurrentItems();

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
        List<MediaInfo> items = getCurrentItems();
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
        currentPos = getCurrentItems().indexOf(item);
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
