package com.example.harvey.androidlabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

public class MessageDetails extends FragmentActivity {
    protected static final String ACTIVITY_NAME = "MessageDetails";
    private static final int DELETE_MESSAGE = 9;

    private Intent intentData = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        final Bundle bundle = getIntent().getExtras();

        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.messageFragment_container, messageFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // put this in onStart() rather than onCreate() because it was being executed before
        // fragment transaction was complete, causing a NullPointerException
        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentData = new Intent();
                intentData.putExtras(getIntent().getExtras());
                setResult(DELETE_MESSAGE, intentData);
                finish();
            }
        });
    }
}