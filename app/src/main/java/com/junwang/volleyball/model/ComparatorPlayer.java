package com.junwang.volleyball.model;


import java.util.Comparator;

/**
 * Created by junwang on 2017/2/2.
 */

public class ComparatorPlayer implements Comparator {

    @Override
    public int compare(Object o, Object t1) {
        Player o1 = (Player) o;
        Player o2 = (Player) t1;
        return o1.getName().compareTo(o2.getName());
    }
}
