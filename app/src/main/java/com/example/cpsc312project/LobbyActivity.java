package com.example.cpsc312project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LobbyActivity extends AppCompatActivity {

    TextView numPlayersTextView;
    TextView lobbyTimeTextView;
    Button joinButton;
    TextView codeTextView;
    TextView user1TextView;
    TextView user2TextView;
    Button startButton;

    String currUserNameSetting;
    int currUserTimeSetting;
    User currUser;
    Game game;
    boolean isHost;
    String lobbyCode;

    SharedPreferences sharedPreferences;
    Random rand;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        numPlayersTextView = findViewById(R.id.numPlayersTextView);
        lobbyTimeTextView = findViewById(R.id.lobbyTimeTextView);
        joinButton = findViewById(R.id.joinButton);
        codeTextView = findViewById(R.id.codeTextView);
        user1TextView = findViewById(R.id.user1TextView);
        user2TextView = findViewById(R.id.user2TextView);
        startButton = findViewById(R.id.startButton);

        db = FirebaseFirestore.getInstance();

        rand = new Random();

        sharedPreferences = getSharedPreferences("usersettings", 0);
        currUserNameSetting = sharedPreferences.getString("nameSetting", "user" + String.format("%03d", rand.nextInt(1000)));
        currUserTimeSetting = sharedPreferences.getInt("timeSetting", 90);

        currUser = new User(currUserNameSetting, currUserTimeSetting);

        Intent intent = getIntent();
        isHost = intent.getBooleanExtra("isHost", false);

        if (isHost) {

            lobbyCode = String.format("%04d", rand.nextInt(10000));

            game = new Game(lobbyCode, currUser, null, 1, currUser.getTimeSetting());

            db.collection("games").document(game.getLobbyCode())
                    .set(game);

            codeTextView.setText("CODE: " + game.getLobbyCode());
            numPlayersTextView.setText("Players: " + game.getNumUsers() + "/2");
            lobbyTimeTextView.setText("Game Time: " + game.getTime() + " sec");
            user1TextView.setText(game.getFirstUser().getUsername());
            user2TextView.setVisibility(View.INVISIBLE);

            startButton.setEnabled(false);

            db.collection("games").document(game.getLobbyCode())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (snapshot != null && snapshot.exists()) {
                                game = snapshot.toObject(Game.class);
                                if (game.getSecondUser() != null) {
                                    numPlayersTextView.setText("Players: " + game.getNumUsers() + "/2");
                                    user2TextView.setText(game.getSecondUser().getUsername());
                                    user2TextView.setVisibility(View.VISIBLE);
                                    if (game.getNumUsers() == 2) {
                                        startButton.setEnabled(true);
                                    }
                                }
                            }
                        }
                    });

        } else {

            lobbyCode = intent.getStringExtra("lobbyCode");
            currUser.setUsername(intent.getStringExtra("savedName")); //in case of randomly generated username

            db.collection("games").document(lobbyCode)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            game = documentSnapshot.toObject(Game.class);

                            game.setSecondUser(currUser);
                            game.setNumUsers(game.getNumUsers() + 1);

                            db.collection("games").document(game.getLobbyCode())
                                    .set(game);

                            codeTextView.setText("CODE: " + game.getLobbyCode());
                            numPlayersTextView.setText("Players: " + game.getNumUsers() + "/2");
                            lobbyTimeTextView.setText("Game Time: " + game.getTime() + " sec");
                            user1TextView.setText(game.getFirstUser().getUsername());
                            user2TextView.setText(game.getSecondUser().getUsername());

                            startButton.setVisibility(View.INVISIBLE);
                        }
                    });

        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(LobbyActivity.this, JoinActivity.class);
                //for preserving randomly generated username in case of no shared pref name setting
                joinIntent.putExtra("savedName", currUserNameSetting);
                startActivity(joinIntent);
            }
        });

    }

}