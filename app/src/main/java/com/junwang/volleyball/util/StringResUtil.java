package com.junwang.volleyball.util;

import android.content.Context;

import com.junwang.volleyball.R;
import com.junwang.volleyball.model.PlayItem;

/**
 * Created by junwang on 2017/2/1.
 */

public class StringResUtil {
    public static String getString(Context context, PlayItem playItem) {
        String str = "";
        switch (playItem) {
            case FAQIU:
                str = context.getString(R.string.faqiu);
                break;
            case LANWANG:
                str = context.getString(R.string.lanwang);
                break;
            case JINGONG:
                str = context.getString(R.string.jingong);
                break;
            case FANGSHOU:
                str = context.getString(R.string.fangshou);
                break;
            case YICHUAN:
                str = context.getString(R.string.yichuan);
                break;
        }
        return str;
    }
}
