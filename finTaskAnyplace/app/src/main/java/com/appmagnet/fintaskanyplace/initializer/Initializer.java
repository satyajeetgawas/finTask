package com.appmagnet.fintaskanyplace.initializer;


import android.app.Application;
import com.evernote.client.android.EvernoteSession;

public class Initializer extends Application {

    private static final String CONSUMER_KEY = "satya-5228";
    private static final String CONSUMER_SECRET = "bfd65c66026815fb";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    @Override
    public void onCreate() {
    super.onCreate();
        //Set up the Evernote singleton session, use EvernoteSession.getInstance() later
        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
//                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();

        registerActivityLifecycleCallbacks(new LoginChecker());
    }



}
