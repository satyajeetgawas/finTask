package com.appmagnet.fintaskanyplace.initializer;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.appmagnet.fintaskanyplace.activity.EvernoteLoginActivity;
import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.evernote.client.android.EvernoteOAuthActivity;
import com.evernote.client.android.EvernoteSession;

import java.util.Arrays;
import java.util.List;

/**
 * @author rwondratschek
 */
public class LoginChecker implements Application.ActivityLifecycleCallbacks {

    private static final List<Class<? extends Activity>> IGNORED_ACTIVITIES = Arrays.asList(
            EvernoteLoginActivity.class,
            com.evernote.client.android.login.EvernoteLoginActivity.class,
            EvernoteOAuthActivity.class
    );

    private Intent mCachedIntent;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        if (!EvernoteSession.getInstance().isLoggedIn() && !isIgnored(activity)) {
            mCachedIntent = activity.getIntent();
            EvernoteLoginActivity.launch(activity);

            activity.finish();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {


    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof EvernoteLoginActivity && EvernoteSession.getInstance().isLoggedIn()) {
            if (mCachedIntent != null) {
                activity.startActivity(mCachedIntent);
                mCachedIntent = null;
            } else {
                activity.startActivity(new Intent(activity, MainActivity.class));
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private boolean isIgnored(Activity activity) {
        return IGNORED_ACTIVITIES.contains(activity.getClass());
    }
}
