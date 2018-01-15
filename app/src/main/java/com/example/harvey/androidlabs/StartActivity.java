package com.example.harvey.androidlabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends Activity {
    public final static int MESSAGE_REQUEST_CODE = 10;
    protected static final String ACTIVITY_NAME = "StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.i(ACTIVITY_NAME, "In onCreate()");

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getResult = new Intent(getApplicationContext(), ListItemsActivity.class);
                startActivityForResult(getResult, MESSAGE_REQUEST_CODE);
            }
        });

        Button startChat = (Button) findViewById(R.id.startChat);
        startChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i(ACTIVITY_NAME, "User clicked Start Chat");
                Intent startChatActivity = new Intent(getApplicationContext(), ChatWindow.class);
                startActivity(startChatActivity);
            }
        });

        Button weather = (Button) findViewById(R.id.weather);
        weather.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent startWeatherActivity = new Intent(getApplicationContext(), WeatherForecast.class);
                startActivity(startWeatherActivity);
            }
        });
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent data){
        if (requestCode == MESSAGE_REQUEST_CODE) {
            Log.i(ACTIVITY_NAME, "Returned to StartActivity.onActivityResult");
            if (responseCode == Activity.RESULT_OK){
                String messagePassed = data.getStringExtra("Response");

                CharSequence text = "ListItemsActivity passed: " + messagePassed;
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}