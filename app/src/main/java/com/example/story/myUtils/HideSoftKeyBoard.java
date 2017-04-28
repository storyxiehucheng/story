package com.example.story.myUtils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 *
 * Created by story on 2017/4/18.
 */

public class HideSoftKeyBoard {
    public static void hindSoftkeyBoard(Activity activity)
    {
        InputMethodManager inputMethodManager= (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
    }
}
