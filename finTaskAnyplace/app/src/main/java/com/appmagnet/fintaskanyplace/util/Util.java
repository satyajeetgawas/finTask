package com.appmagnet.fintaskanyplace.util;

import android.app.Activity;

import com.appmagnet.fintaskanyplace.activity.EvernoteLoginActivity;
import com.evernote.client.android.EvernoteSession;

/**
 * @author rwondratschek
 */
public final class Util {

    private Util() {
        // no op
    }

    public static void logout(Activity activity) {
        EvernoteSession.getInstance().logOut();
        EvernoteLoginActivity.launch(activity);
        activity.finish();
    }
}
