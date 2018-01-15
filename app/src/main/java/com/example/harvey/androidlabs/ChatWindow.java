package com.example.harvey.androidlabs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatActivity extends Activity {
    protected static final String ACTIVITY_NAME = "ChatActivity";
    ArrayList<String> chatMessages = new ArrayList<>();
    SQLiteDatabase db;
    boolean isFrameLayout;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ChatAdapter messageAdapter = new ChatAdapter(this);
        final ChatDatabaseHelper dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        ListView chatView = (ListView) findViewById(R.id.chatView);
        chatView.setAdapter(messageAdapter);

        final EditText messageText = (EditText) findViewById(R.id.messageText);
        
        final Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                String message = messageText.getText().toString();

                chatMessages.add(message);
                messageAdapter.notifyDataSetChanged(); // this restarts the process of getCount() / getView()
                messageText.setText("");

                ContentValues cv = new ContentValues();
                cv.put(ChatDatabaseHelper.KEY_MESSAGE, message);
                db.insert(ChatDatabaseHelper.TABLE_NAME, ChatDatabaseHelper.KEY_MESSAGE, cv);
            }
        });

        getMessages();

        isFrameLayout = findViewById(R.id.messageDetails_container) == null;

        chatView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView<?> av, View view, int i, long l) {

            }

        });
    }

    protected void getMessages() {
        // gets the messages from the database when ChatActivity activity begins

        c = db.query(ChatDatabaseHelper.TABLE_NAME, new String[] {"*"}, null, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String message = c.getString(c.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE));
            chatMessages.add(message);
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + message);
            c.moveToNext();
        }

        Log.i(ACTIVITY_NAME, "Cursor's column count =" + c.getColumnCount());

        for (int i=0; i<c.getColumnCount(); i++) {
            Log.i(ACTIVITY_NAME, "Column name: " + c.getColumnName(i));
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

  private class ChatAdapter extends ArrayAdapter<String> {
        public ChatAdapter(Context ctx) {
            super(ctx, 0);
        }

        @Override
        public int getCount() {
            return chatMessages.size();
        }

        @Override
        public String getItem(int position) {
            return chatMessages.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ChatActivity.this.getLayoutInflater();

            View result = null;
            if (position%2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView) result.findViewById(R.id.message_text);
            message.setText(getItem(position)); // get the string at position
            return result;
        }

        public long getItemId(int position) {
            c.moveToPosition(position);
            return c.getInt(c.getColumnIndex(ChatDatabaseHelper.KEY_ID));
        }
    }
}
