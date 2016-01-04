package com.huya.myvideos;

import android.app.Fragment;

/**
 * Created by even on 2016-01-04.
 */
public class FragmentFactory {
    //
    public static Fragment getFragmentInstance(int checkId) {
        Fragment fragment = null;
        switch (checkId) {
            case R.id.btn_message:
            case R.id.btn_square:
            case R.id.btn_mine:
            case R.id.btn_attection:
                fragment = new VideoFragment();
                break;
        }
        return fragment;
    }
}
