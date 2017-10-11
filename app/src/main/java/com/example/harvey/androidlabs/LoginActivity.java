package com.example.harvey.androidlabs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity {
    protected final String ACTIVITY_NAME = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(ACTIVITY_NAME, "In onCreate()");
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("@app_name", MODE_PRIVATE);
        String defaultEmail = sharedPreferences.getString("DefaultEmail", "email@domain.com");

        TextView textEmailAddress = (TextView) findViewById(R.id.txtEmailAddress);
        textEmailAddress.setText(defaultEmail);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SharedPreferences sharedPreferences = getSharedPreferences("@app_name", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                TextView textEmailAddress = (TextView) findViewById(R.id.txtEmailAddress);
                editor.putString("DefaultEmail", textEmailAddress.getText().toString());
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

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
