package com.junwang.volleyball.model;


import java.util.Comparator;

/**
 * Created by junwang on 2017/2/2.
 */

public class ComparatorCourt implements Comparator {

    @Override
    public int compare(Object o, Object t1) {
        Court o1 = (Court)o;
        Court o2 = (Court)t1;

        Long time1 = o1.getCreatedTime().getTime();
        Long time2 = o2.getCreatedTime().getTime();

        return time2.compareTo(time1); //时间晚的排前边
    }
}
