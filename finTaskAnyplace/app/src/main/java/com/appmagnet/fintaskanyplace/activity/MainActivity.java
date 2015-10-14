package com.appmagnet.fintaskanyplace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appmagnet.fintaskanyplace.R;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.User;
import com.appmagnet.fintaskanyplace.evernote.GetUserTask;
import com.evernote.thrift.TException;

import net.vrallev.android.task.TaskResult;

/**
 * Created by satyajeet on 10/12/2015.
 */
public class MainActivity extends AppCompatActivity {
    private TextView mTextViewUserName;
    private static final String KEY_USER = "KEY_USER";
    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

            mTextViewUserName = (TextView) findViewById(R.id.textView_user_name);
            mTextViewUserName.setText("Hi");

        if (!EvernoteSession.getInstance().isLoggedIn()) {
            // LoginChecker will call finish
            return;
        }
        if (savedInstanceState != null) {
            Log.d("error","error");
            mUser = (User) savedInstanceState.getSerializable(KEY_USER);

        }
        if (savedInstanceState == null) {
            new GetUserTask().start(this);

        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_USER, mUser);
    }



}
