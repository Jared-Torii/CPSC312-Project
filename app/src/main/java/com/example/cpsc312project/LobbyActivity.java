package com.example.cpsc312project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LobbyActivity extends AppCompatActivity {

    int numPlayers;
    int lobbyCode;
    List<User> usersInLobby;
    Random rand;

    TextView numPlayersTextView;
    Button joinButton;
    TextView codeTextView;
    TextView user1TextView;
    TextView user2TextView;
    TextView user3TextView;
    TextView user4TextView;
    Button startButton;

    FirebaseFirestore db;
    Map<String, Object> game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        numPlayersTextView = findViewById(R.id.numPlayersTextView);
        joinButton = findViewById(R.id.joinButton);
        codeTextView = findViewById(R.id.codeTextView);
        user1TextView = findViewById(R.id.user1TextView);
        user2TextView = findViewById(R.id.user2TextView);
        user3TextView = findViewById(R.id.user3TextView);
        user4TextView = findViewById(R.id.user4TextView);
        startButton = findViewById(R.id.startButton);

        User currUser = new User("Test User");
        usersInLobby = new ArrayList<>();
        usersInLobby.add(currUser);
        numPlayers = usersInLobby.size();
        numPlayersTextView.setText("Players: " + numPlayers + "/4");
        user1TextView.setText(currUser.getUsername());
        user2TextView.setVisibility(View.INVISIBLE);
        user3TextView.setVisibility(View.INVISIBLE);
        user4TextView.setVisibility(View.INVISIBLE);

        rand = new Random();
        lobbyCode = Integer.parseInt(String.format("%04d", rand.nextInt(10000)));
        codeTextView.setText("CODE: " + lobbyCode);

        db = FirebaseFirestore.getInstance();
        game = new HashMap<>();
        game.put("lobbyCode", lobbyCode);
        game.put("firstUser", currUser.getUsername());
        db.collection("games")
                .add(game)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}