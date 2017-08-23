package com.dropbox.core.examples.android;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by davide-syn on 8/23/17.
 */

public class Utils {

    protected static boolean hasToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        return prefs.getString("access-token", null) != null;
    }
}
