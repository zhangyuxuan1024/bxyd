package net.iclassmate.bxyd.utils;

import net.iclassmate.bxyd.bean.attention.Attention;

import java.util.Comparator;

/**
 * Created by xydbj on 2016.7.13.
 */
public class PinyinComparator implements Comparator<Attention> {

    public int compare(Attention o1, Attention o2) {
        if (o1.getUserPinyin().equals("@") || o2.getUserPinyin().equals("#")) {
            return -1;
        } else if (o1.getUserPinyin().equals("#") || o2.getUserPinyin().equals("@")) {
            return 1;
        } else {
            return o1.getUserPinyin().compareTo(o2.getUserPinyin());
        }
    }
}
