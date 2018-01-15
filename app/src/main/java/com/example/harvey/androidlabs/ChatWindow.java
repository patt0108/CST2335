package com.example.harvey.androidlabs;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import java.util.Objects;

public class ChatWindow extends FragmentActivity {
    protected static final String ACTIVITY_NAME = "ChatWindow";
    private static final int DELETE_MESSAGE = 9;
    ArrayList<String> chatMessages = new ArrayList<>();
    SQLiteDatabase db;
    boolean frameLayoutExists;
    Cursor c;
    ChatAdapter messageAdapter;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        final ChatDatabaseHelper dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        final EditText messageText = (EditText) findViewById(R.id.messageText);

        messageAdapter = new ChatAdapter(this);

        final Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                String message = messageText.getText().toString();

                db.beginTransaction();
                try {
                    ContentValues cv = new ContentValues();
                    cv.put(ChatDatabaseHelper.KEY_MESSAGE, message);
                    db.insert(ChatDatabaseHelper.TABLE_NAME, ChatDatabaseHelper.KEY_ID, cv);
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                    refreshMessages();
                    messageText.setText("");
                }
            }
        });

        getMessages();

        ListView chatView = (ListView) findViewById(R.id.chatView);
        chatView.setAdapter(messageAdapter);
        chatView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView<?> av, View view, int i, long l) {
                Bundle bundle = new Bundle(2);
                bundle.putString("message", messageAdapter.getItem(i));
                bundle.putLong("itemId", messageAdapter.getItemId(i));

                if (frameLayoutExists) { // if in tablet mode
                    MessageFragment messageFragment = new MessageFragment();
                    messageFragment.setArguments(bundle);
                    messageFragment.passObjectReference(ChatWindow.this);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.messageFragment_container, messageFragment).commit();
                }
                else { // if in phone mode
                    Intent intent = new Intent(ChatWindow.this, MessageDetails.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // test for presence of FrameLayout every time execution resumes in case orientation changed
        frameLayoutExists = findViewById(R.id.messageFragment_container) != null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        frameLayoutExists = findViewById(R.id.messageFragment_container) != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == DELETE_MESSAGE) {
            Bundle intentData = intent.getExtras();
            Log.i(ACTIVITY_NAME, "Delete message requested for " + intentData.getLong("itemId"));

            deleteMessage(intentData.getLong("itemId"));
        }
    }

    protected void deleteMessage(long itemId) {
        // if in tablet mode, delete the fragment with the details of the deleted message
        if (frameLayoutExists) {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.messageFragment_container);

            getSupportFragmentManager().beginTransaction()
                    .remove(fragment).commit();
        }

        /* have to put query in a string to prevent ? from throwing error "<expr> expected got ?".
         * Known bug in Android Studio 3.0.1, per https://issuetracker.google.com/issues/68293745
         * Fixed in 3.1, which is in preview.
        */
        String query = "DELETE FROM ChatMessages WHERE _id = " + itemId;
        db.execSQL(query);
        refreshMessages();
    }

    protected void refreshMessages() {
        chatMessages.clear();
        messageAdapter.notifyDataSetChanged();
        getMessages();
    }

    protected void getMessages() {
        // gets the messages from the database when ChatWindow activity begins

        c = db.query(ChatDatabaseHelper.TABLE_NAME, new String[] {"*"}, null, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String message = c.getString(c.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE));
            String _id = Objects.toString(c.getLong(c.getColumnIndex(ChatDatabaseHelper.KEY_ID)));
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
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();

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
