package com.jw.cool.xuanmusicplayer.constant;

/**
 * Created by Administrator on 15-9-23.
 */
public interface PatternLockStatus {
    String KEY = "PatternLockStatus";
    int SET_LOCK = 0x00000000;
    int FIRST_DRAW_START = 0x00000001;
    int FIRST_DRAW_FINISH = 0x00000002;
    int SECOND_DRAW_START = 0x00000003;
    int SET_LOCK_SECOND_DRAW = 0x00000004;
    int SET_LOCK_SUCCESSFULLY = 0x00000005;
    int UNLOCK = 0x10000000;
    int UNLOCK_SUCCESSFULLY = 0x10000001;
    int MODIFY_LOCK = 0x20000000;
    int MODIFY_LOCK_FIRST_DRAW = 0x2000001;
    int MODIFY_LOCK_SECOND_DRAW = 0x2000002;
    int MODIFY_LOCK_SUCCESSFULLY = 0x2000003;
    int CLEAR_LOCK = 0x30000000;
}
