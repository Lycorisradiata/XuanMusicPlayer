package com.jw.cool.xuanmusicplayer.events;

/**
 * Created by admin on 2015/8/30.
 */
public class ProcessEvent {
    public final int currentPos;
    public final int totalMilliSeconds;
//    public final boolean isCompleted;

    public ProcessEvent(int currentPos, int totalMilliSeconds) {
        this.currentPos = currentPos;
        this.totalMilliSeconds = totalMilliSeconds;
    }

//    Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
//    Log.e("cursor" , (cursor==null) + "");
//    while(cursor.moveToNext()) {
//// 查找封面图片
//        long albumId = cursor.getLong(5);
//// 读取专辑图片
//        String album_uri = "content://media/external/audio/albumart"; // 专辑Uri对应的字符串
//        Uri albumUri = ContentUris.withAppendedId(Uri.parse(album_uri), albumId);
//// 取图片 ==> 得到一个输入流
//        Bitmap coverPhoto = null ;
//        try {
//            InputStream is = context.getContentResolver().openInputStream(albumUri);
//            if(null != is) {
//                coverPhoto = BitmapFactory.decodeStream(is);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        data.add(new Audio(cursor.getLong(0), cursor.getString(1) , cursor.getString(2) , cursor.getLong(3) , cursor.getString(4) , albumId , coverPhoto));
//    }
//    cursor.close();
}
