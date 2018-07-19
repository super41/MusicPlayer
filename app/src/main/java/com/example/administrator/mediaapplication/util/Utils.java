package com.example.administrator.mediaapplication.util;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/7/19.
 */

public class Utils {
    public static void registerClickListner(ViewGroup vg, View.OnClickListener clickListener) {
        for (int i = 0; i < vg.getChildCount(); i++) {
             if(vg.getChildAt(i) instanceof ViewGroup){
                 registerClickListner((ViewGroup) vg.getChildAt(i),clickListener);
             }else{
                 vg.getChildAt(i).setOnClickListener(clickListener);
             }
        }
    }
}
