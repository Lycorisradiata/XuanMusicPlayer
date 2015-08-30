package com.jw.cool.xuanmusicplayer;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.events.CompletionEvent;
import com.jw.cool.xuanmusicplayer.events.ProcessEvent;
import com.jw.cool.xuanmusicplayer.utils.HandlerTime;
import com.jw.cool.xuanmusicplayer.utils.MediaUtil;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

public class PlayActivity extends Activity implements View.OnClickListener{
    final String TAG = "PlayActivity";
    TextView song, singer, album, currentTime, totalTime;
    SeekBar seekBar;
    Button previous, playOrPause, next;
    boolean isNeedRefreshMediaInfo;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        song = (TextView) findViewById(R.id.song);
        singer = (TextView) findViewById(R.id.singer);
        album = (TextView) findViewById(R.id.album);
        currentTime = (TextView) findViewById(R.id.current_time);
        totalTime = (TextView) findViewById(R.id.total_time);
        previous = (Button) findViewById(R.id.previous);
        previous.setOnClickListener(this);
        playOrPause = (Button) findViewById(R.id.play_or_pause);
        playOrPause.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.image);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d(TAG, "onProgressChanged i " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch seekBar " + seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch seekBar " + seekBar.getProgress() + " " + seekBar.getMax());
                String action = MusicService.ACTION_SEEK;
                int totalMilliseconds = HandlerTime.totalSeconds(totalTime.getText().toString())*1000;
                int seekPos = totalMilliseconds * seekBar.getProgress() / seekBar.getMax();
                sendAction(action, seekPos);
            }
        });
        refresh(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        isNeedRefreshMediaInfo = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ProcessEvent event) {
        Log.d(TAG, "onEventMainThread isNeedRefreshMediaInfo " + isNeedRefreshMediaInfo);
        if(isNeedRefreshMediaInfo){
            refresh(MusicRetriever.getCurrentItem());
        }
        currentTime.setText(HandlerTime.seconds2HHMMSS(event.currentPos / 1000));
        seekBar.setProgress(event.currentPos * 100 / event.totalMilliSeconds);
    }
    public void onEvent(CompletionEvent event) {
        Log.d(TAG, "onEventThread event.isCompleted " + event.isCompleted);
        if(event.isCompleted){
            isNeedRefreshMediaInfo = true;
        }
    }


    void refresh(MusicRetriever.Item item){
        Log.d(TAG, "refresh item " + isNeedRefreshMediaInfo);
        if(item != null){
            song.setText(item.getDisplayName());
            singer.setText(item.getArtist());
            album.setText(item.getAlbum());
            totalTime.setText(HandlerTime.seconds2HHMMSS(item.getDuration() / 1000));
//            Bitmap bitmap = getBitMap(item.getAlbumId());
//            Bitmap bitmap = MediaUtil.getArtwork(this, item.getId(), item.getAlbumId(), false);
//            Log.d(TAG, "refresh " + item.getId() + " " + item.getAlbumId());
////            String fileName = getAlbumArt(item.getAlbumId());
//            if(bitmap != null){
//
////                imageView.setImageBitmap(bitmap);
////                Bitmap bitmap = BitmapFactory.decodeFile(fileName);
////                BitmapDrawable bmpDraw = new BitmapDrawable(bitmap);
////                imageView.setImageDrawable(bmpDraw);
//                imageView.setImageBitmap(bitmap);
//            }else{
//                Log.d(TAG, "refresh bitmap " + bitmap);
//                imageView.setBackgroundResource(R.drawable.ic_launcher);
//
//            }
            Bitmap bitmap = getArtAlbum(item.getId());

            Log.d(TAG, "refreshbitmap " + bitmap + " " + getAlbumArt((int)item.getAlbumId()));
            if(null != bitmap){
                imageView.setImageBitmap(bitmap);
            }

            isNeedRefreshMediaInfo = false;

        }
    }

    private String getAlbumArt(int albumid) {
        String strAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] {android.provider.MediaStore.Audio.AlbumColumns.ALBUM_ART };
        Cursor cur = this.getContentResolver().query(
                Uri.parse(strAlbums + "/" + Integer.toString(albumid)),
                projection, null, null, null);
        String strPath = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            strPath = cur.getString(0);
        }
        cur.close();
        cur = null;
        return strPath;
    }

    public Bitmap getArtAlbum(long audioId){
        String str = "content://media/external/audio/media/" + audioId+ "/albumart";
        Uri uri = Uri.parse(str);
        ParcelFileDescriptor pfd = null;
        try {
            pfd = this.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            return null;
        }
        Bitmap bm;
        if (pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            bm = BitmapFactory.decodeFileDescriptor(fd);
            return bm;
        }
        return null;
    }

//    String getAlbumArt(int album_id) {
//        String mUriAlbums = "content://media/external/audio/albums";
//        String[] projection = new String[]{"album_art"};
//        Cursor cur = this.getContentResolver().query(
//                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
//                projection, null, null, null);
//        String album_art = null;
//        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
//            cur.moveToNext();
//            album_art = cur.getString(0);
//        }
//        cur.close();
//        cur = null;
//        return album_art;
//    }

        Bitmap getBitMap(long albumId){
// 读取专辑图片
        String album_uri = "content://media/external/audio/albumart"; // 专辑Uri对应的字符串
        Uri albumUri = ContentUris.withAppendedId(Uri.parse(album_uri), albumId);
// 取图片 ==> 得到一个输入流
        Bitmap coverPhoto = null ;
        try {
            InputStream is = getContentResolver().openInputStream(albumUri);
            if(null != is) {
                coverPhoto = BitmapFactory.decodeStream(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coverPhoto;
    }



//    Bitmap getAlbumArt(int album_id) {
//        String mUriAlbums = "content://media/external/audio/albums";
//        String[] projection = new String[] { "album_art" };
//        Cursor cur = this.getContentResolver().query(
//                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
//                projection, null, null, null);
//        String album_art = null;
//        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
//            cur.moveToNext();
//            album_art = cur.getString(0);
//        }
//        cur.close();
//        cur = null;
//
//        Bitmap bm = null;
//        if (album_art == null) {
////            imageView.setBackgroundResource(R.drawable.audio_default_bg);
//        } else {
//            bm = BitmapFactory.decodeFile(album_art);
//            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
//            imageView.setImageDrawable(bmpDraw);
//        }
//        return null;
//    }
//    private void getImage(){
//        Cursor currentCursor = getCursor("/mnt/sdcard/"+mp3Info);
//        int album_id = currentCursor.getInt(currentCursor
//                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
//        String albumArt = getAlbumArt(album_id);
//        Bitmap bm = null;
//        if (albumArt == null) {
//            mImageView.setBackgroundResource(R.drawable.staring);
//        } else {
//            bm = BitmapFactory.decodeFile(albumArt);
//            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
//            mImageView.setImageDrawable(bmpDraw);
//        }

        @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick view" + view);
        Log.d(TAG, "onClick playOrPause" + playOrPause);
        String action = null;

        if(view == previous){
            action = MusicService.ACTION_PREVIOUS;
        }else if (view == playOrPause){
            action = MusicService.ACTION_TOGGLE_PLAYBACK;
        }else if (view == next){
            action = MusicService.ACTION_SKIP;
        }

        sendAction(action, -1);
    }

    void sendAction(String action, int seekPos){
        if(action != null && action.length() > 0){
            Intent intent = new Intent();
            intent.setAction(action);
            intent.setPackage(getPackageName());//这里你需要设置你应用的包名
            if(seekPos >= 0){
                intent.putExtra("seekPos", seekPos);
            }
            startService(intent);
            isNeedRefreshMediaInfo = true;
            Log.d(TAG, "onClick intent " + intent.getAction());
        }
    }

//    intent.setAction(MusicService.ACTION_PLAY);
//    MusicRetriever.Item item = itemList.get(position);
//    Bundle bundle = new Bundle();
////        Item item = new Item(bundle.getLong("id"),
////                strEmpty ,
////                bundle.getString("title"),
////                strEmpty,
////                bundle.getLong("duration"),
////                bundle.getString("displayName")
//    bundle.putLong("id", item.getId());
//    bundle.putLong("duration", item.getDuration());
//    bundle.putString("title", item.getTitle());
//    bundle.putString("displayName", item.getDisplayName());
//    intent.putExtras(bundle);
//    getActivity().startService(intent);
}
