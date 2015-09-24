package com.jw.cool.xuanmusicplayer.constant;

/**
 * Created by ljw on 15-9-23.
 */
public interface PatternLockStatus {
    String KEY = "PatternLockStatus";
    int SET_LOCK = 0x0000;
    int SET_LOCK_FIRST_DRAW_FINISH = SET_LOCK + 1;
    int SET_LOCK_SECOND_DRAW_FINISH = SET_LOCK + 2;
    int SET_LOCK_SUCCESSFULLY = SET_LOCK + 3;
    int UNLOCK = 0x1000;
    int UNLOCK__CONFIRM_LOCK_FINISH = UNLOCK + 1;
    int MODIFY_LOCK = 0x2000;
    int MODIFY_LOCK_CONFIRM_LOCK_FINISH = MODIFY_LOCK + 1;
    int MODIFY_LOCK_FIRST_DRAW_FINISH = MODIFY_LOCK + 2;
    int MODIFY_LOCK_SECOND_DRAW_FINISH = MODIFY_LOCK + 3;
    int MODIFY_LOCK_SUCCESSFULLY = MODIFY_LOCK + 4;
    int CLEAR_LOCK = 0x3000;
    int CLEAR_LOCK_CONFIRM_LOCK_FINISH = CLEAR_LOCK + 1;
}
