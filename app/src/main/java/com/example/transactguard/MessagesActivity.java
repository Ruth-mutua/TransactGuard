package com.example.transactguard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    ImageView btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = findViewById(R.id.recyclerView);
        btnLogout = (ImageView) findViewById(R.id.btnLogout);
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagesActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Call a method to load messages from your database or wherever you store them
        loadMessages();
    }

    private void loadMessages() {
        // Fetch messages and update the adapter
        // You may fetch messages from a database, API, or any other source
        List<messageModel> messages = getMessagesFromDataSource();
        adapter.setMessages(messages);
    }

    private List<messageModel> getMessagesFromDataSource() {
        // Replace this with your logic to fetch messages
        // For example, you might fetch messages from a database
        List<messageModel> messages = new ArrayList<>();
        // Add your logic here
        return messages;
    }
}



