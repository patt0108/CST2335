package com.example.harvey.androidlabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class MessageFragment extends Fragment {
    private ChatWindow chatWindow = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Bundle bundle = getArguments();

        TextView tvMessage = (TextView) getView().findViewById(R.id.tvMessage);
        tvMessage.setText(bundle.getString("message"));

        TextView tvId = (TextView) getView().findViewById(R.id.tvId);
        tvId.setText(Objects.toString(bundle.getLong("itemId")));

        Button btnDelete = (Button) getView().findViewById(R.id.btnDelete);
        if (chatWindow != null) {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatWindow.deleteMessage(bundle.getLong("itemId"));
                }
            });
        }
    }

    public void passObjectReference(ChatWindow chatWindow) {
        this.chatWindow = chatWindow;
    }
}