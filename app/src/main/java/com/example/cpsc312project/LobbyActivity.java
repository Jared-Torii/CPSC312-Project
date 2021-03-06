package com.example.cpsc312project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    Boggle boggle;
    List<String> sharedGrid;

    SharedPreferences sharedPreferences;
    Random rand;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        currUserNameSetting = sharedPreferences.getString("nameSetting", "user" + String.format("%03d",
                rand.nextInt(1000)));
        currUserTimeSetting = sharedPreferences.getInt("timeSetting", 90);

        currUser = new User(currUserNameSetting, currUserTimeSetting);

        Intent intent = getIntent();
        isHost = intent.getBooleanExtra("isHost", false);

        if (isHost) { //For first user in lobby

            getSupportActionBar().setTitle("My Lobby");

            lobbyCode = String.format("%05d", rand.nextInt(100000));

            game = new Game(lobbyCode, currUser, null, 1, currUser.getTimeSetting(),
                    null, null, null);

            db.collection("games").document(game.getLobbyCode())
                    .set(game);

            codeTextView.setText("CODE: " + game.getLobbyCode());
            numPlayersTextView.setText("Players: " + game.getNumUsers() + "/2");
            lobbyTimeTextView.setText("Game Time: " + game.getTime() + " sec");
            user1TextView.setText(game.getFirstUser().getUsername());
            user2TextView.setVisibility(View.INVISIBLE);

            startButton.setEnabled(false);

            db.collection("games").document(game.getLobbyCode())
                    .addSnapshotListener(LobbyActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (snapshot != null && snapshot.exists()) {
                                game = snapshot.toObject(Game.class);

                                if (game.getSecondUser() != null) { //Listen for a second user joining
                                    joinButton.setVisibility(View.INVISIBLE);
                                    numPlayersTextView.setText("Players: " + game.getNumUsers() + "/2");
                                    user2TextView.setText(game.getSecondUser().getUsername());
                                    user2TextView.setVisibility(View.VISIBLE);
                                    if (game.getNumUsers() == 2) {
                                        startButton.setEnabled(true);
                                    }
                                }

                                else if (game.getSecondUser() == null) { //Listen for second user leaving
                                    joinButton.setVisibility(View.VISIBLE);
                                    numPlayersTextView.setText("Players: " + game.getNumUsers() + "/2");
                                    user2TextView.setVisibility(View.INVISIBLE);
                                    startButton.setEnabled(false);
                                }

                            }
                        }
                    });

        } else { //For second user in lobby

            joinButton.setVisibility(View.INVISIBLE);

            Toast.makeText(getApplicationContext(), "Successfully joined game!", Toast.LENGTH_SHORT)
                    .show();

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

                            getSupportActionBar().setTitle(game.getFirstUser().getUsername() +
                                    "\'s Lobby");
                        }
                    });

            db.collection("games").document(lobbyCode)
                    .addSnapshotListener(LobbyActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (snapshot != null & snapshot.exists()) {
                                game = snapshot.toObject(Game.class);

                                if (game.getSharedGrid() != null) { //Listen for game starting
                                    Intent secondUserBoggleIntent = new Intent(LobbyActivity.this,
                                            MultiplayerBoggleActivity.class);
                                    secondUserBoggleIntent.putExtra("lobbyCode", game.getLobbyCode());
                                    secondUserBoggleIntent.putExtra("userNum", 2);
                                    startActivity(secondUserBoggleIntent);
                                    LobbyActivity.this.finish();
                                }

                                else if (game.getFirstUser() == null) { //Listen for first user leaving
                                    db.collection("games").document(lobbyCode)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Host cancelled the game...",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent mainScreenIntent = new Intent(LobbyActivity.this,
                                                            MainActivity.class);
                                                    startActivity(mainScreenIntent);
                                                    LobbyActivity.this.finish();
                                                }
                                            });
                                }

                            }
                        }
                    });

        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("games").document(lobbyCode)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent joinIntent = new Intent(LobbyActivity.this, JoinActivity.class);
                                //for preserving randomly generated username in case of no shared pref name setting
                                joinIntent.putExtra("savedName", currUserNameSetting);
                                startActivity(joinIntent);
                                finish();
                            }
                        });
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boggle = new Boggle(getApplicationContext());
                boggle.setUpGrid();

                sharedGrid = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        sharedGrid.add(boggle.getGrid()[i][j]);
                    }
                }

                game.setSharedGrid(sharedGrid);

                db.collection("games").document(game.getLobbyCode())
                        .set(game)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent boggleIntent = new Intent(LobbyActivity.this,
                                        MultiplayerBoggleActivity.class);
                                boggleIntent.putExtra("lobbyCode", game.getLobbyCode());
                                boggleIntent.putExtra("userNum", 1);
                                startActivity(boggleIntent);
                                LobbyActivity.this.finish();
                            }
                        });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                db.collection("games").document(lobbyCode)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                game = documentSnapshot.toObject(Game.class);
                                if (game.getNumUsers() == 1) {
                                    db.collection("games").document(lobbyCode)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Intent mainScreenIntent = new Intent(LobbyActivity.this,
                                                            MainActivity.class);
                                                    startActivity(mainScreenIntent);
                                                    LobbyActivity.this.finish();
                                                }
                                            });
                                } else {
                                    if (isHost) {
                                        db.collection("games").document(lobbyCode)
                                                .update("firstUser", null,
                                                        "numUsers", 1)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Intent mainScreenIntent = new Intent(LobbyActivity.this,
                                                                MainActivity.class);
                                                        startActivity(mainScreenIntent);
                                                        LobbyActivity.this.finish();
                                                    }
                                                });
                                    } else {
                                        db.collection("games").document(lobbyCode)
                                                .update("secondUser", null,
                                                        "numUsers", 1)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Intent mainScreenIntent = new Intent(LobbyActivity.this,
                                                                MainActivity.class);
                                                        startActivity(mainScreenIntent);
                                                        LobbyActivity.this.finish();
                                                    }
                                                });
                                    }
                                }
                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}