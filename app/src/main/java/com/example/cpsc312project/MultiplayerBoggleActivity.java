package com.example.cpsc312project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MultiplayerBoggleActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Game game;

//    ActivityResultLauncher<Intent> launcher;
    Intent intent;
    Intent scoreIntent;
    String lobbyCode;
    int userNum;

    Runnable runnable;
    Handler handler = null;
    SharedPreferences sharedPreferences;
    int seconds;

    Boggle boggle;

    TextView pointsView;
    TextView timeView;
    TextView infoView;

    Button button11;
    Button button21;
    Button button31;
    Button button41;
    Button button12;
    Button button22;
    Button button32;
    Button button42;
    Button button13;
    Button button23;
    Button button33;
    Button button43;
    Button button14;
    Button button24;
    Button button34;
    Button button44;

    Button clearButton;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_boggle);

        getSupportActionBar().setTitle("Multiplayer Boggle Game");

//        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        //TODO: FINISH THIS
//                    }
//                });

        db = FirebaseFirestore.getInstance();

        intent = getIntent();
        lobbyCode = intent.getStringExtra("lobbyCode");
        userNum = intent.getIntExtra("userNum", 2);

        boggle = new Boggle(getApplicationContext());
        boggle.loadWordsFromFile();

        pointsView = findViewById(R.id.pointsText);
        timeView = findViewById(R.id.timeText);
        infoView = findViewById(R.id.infoText);

        button11 = findViewById(R.id.button11);
        button21 = findViewById(R.id.button21);
        button31 = findViewById(R.id.button31);
        button41 = findViewById(R.id.button41);
        button12 = findViewById(R.id.button12);
        button22 = findViewById(R.id.button22);
        button32 = findViewById(R.id.button32);
        button42 = findViewById(R.id.button42);
        button13 = findViewById(R.id.button13);
        button23 = findViewById(R.id.button23);
        button33 = findViewById(R.id.button33);
        button43 = findViewById(R.id.button43);
        button14 = findViewById(R.id.button14);
        button24 = findViewById(R.id.button24);
        button34 = findViewById(R.id.button34);
        button44 = findViewById(R.id.button44);

        clearButton = findViewById(R.id.clearButton);
        submitButton = findViewById(R.id.submitButton);

        db.collection("games").document(lobbyCode)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        game = documentSnapshot.toObject(Game.class);

                        pointsView.setText("Points: " + boggle.getCurrPoints());

                        int sharedGridIncrement = 0;
                        String[][] grid = new String[4][4];
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {
                                grid[i][j] = game.getSharedGrid().get(sharedGridIncrement);
                                sharedGridIncrement++;
                            }
                        }
                        boggle.setUpGrid(grid);

                        button11.setEnabled(true);
                        button21.setEnabled(true);
                        button31.setEnabled(true);
                        button41.setEnabled(true);
                        button12.setEnabled(true);
                        button22.setEnabled(true);
                        button32.setEnabled(true);
                        button42.setEnabled(true);
                        button13.setEnabled(true);
                        button23.setEnabled(true);
                        button33.setEnabled(true);
                        button43.setEnabled(true);
                        button14.setEnabled(true);
                        button24.setEnabled(true);
                        button34.setEnabled(true);
                        button44.setEnabled(true);

                        button11.setText(boggle.getLetter(1,1));
                        button21.setText(boggle.getLetter(2,1));
                        button31.setText(boggle.getLetter(3,1));
                        button41.setText(boggle.getLetter(4,1));
                        button12.setText(boggle.getLetter(1,2));
                        button22.setText(boggle.getLetter(2,2));
                        button32.setText(boggle.getLetter(3,2));
                        button42.setText(boggle.getLetter(4,2));
                        button13.setText(boggle.getLetter(1,3));
                        button23.setText(boggle.getLetter(2,3));
                        button33.setText(boggle.getLetter(3,3));
                        button43.setText(boggle.getLetter(4,3));
                        button14.setText(boggle.getLetter(1,4));
                        button24.setText(boggle.getLetter(2,4));
                        button34.setText(boggle.getLetter(3,4));
                        button44.setText(boggle.getLetter(4,4));

                        infoView.setText("Start building a word!");

                        seconds = game.getTime();

                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                updateSeconds(seconds - 1);
                                handler.postDelayed(this, 1000);
                                if (seconds == 0) {
                                    stopTimer(this);

                                    button11.setEnabled(false);
                                    button21.setEnabled(false);
                                    button31.setEnabled(false);
                                    button41.setEnabled(false);
                                    button12.setEnabled(false);
                                    button22.setEnabled(false);
                                    button32.setEnabled(false);
                                    button42.setEnabled(false);
                                    button13.setEnabled(false);
                                    button23.setEnabled(false);
                                    button33.setEnabled(false);
                                    button43.setEnabled(false);
                                    button14.setEnabled(false);
                                    button24.setEnabled(false);
                                    button34.setEnabled(false);
                                    button44.setEnabled(false);

                                    clearButton.setEnabled(false);
                                    submitButton.setEnabled(false);

                                    db.collection("games").document(lobbyCode)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    game = documentSnapshot.toObject(Game.class);

                                                    //Send list of words found to the database
                                                    //True score will be calculated on next screen
                                                    //where we will have access to both players' word lists
                                                    if (userNum == 1) {
                                                        game.setFirstUserWords(boggle.getAlreadyUsedWords());
                                                        db.collection("games").document(lobbyCode)
                                                                .update("firstUserWords", game.getFirstUserWords())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        scoreIntent = new Intent(MultiplayerBoggleActivity.this,
                                                                                ScoreActivity.class);
                                                                        scoreIntent.putExtra("lobbyCode", lobbyCode);
                                                                        startActivity(scoreIntent);
                                                                        MultiplayerBoggleActivity.this.finish();
                                                                    }
                                                                });
                                                    }
                                                    else if (userNum == 2) {
                                                        game.setSecondUserWords(boggle.getAlreadyUsedWords());
                                                        db.collection("games").document(lobbyCode)
                                                                .update("secondUserWords", game.getSecondUserWords())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        scoreIntent = new Intent(MultiplayerBoggleActivity.this,
                                                                                ScoreActivity.class);
                                                                        scoreIntent.putExtra("lobbyCode", lobbyCode);
                                                                        startActivity(scoreIntent);
                                                                        MultiplayerBoggleActivity.this.finish();
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        };

                        if (handler == null) {
                            handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(runnable, 1000);
                        }

                    }
                });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boggle.clearWord();
                infoView.setText("Start building a word!");

                button11.setEnabled(true);
                button21.setEnabled(true);
                button31.setEnabled(true);
                button41.setEnabled(true);
                button12.setEnabled(true);
                button22.setEnabled(true);
                button32.setEnabled(true);
                button42.setEnabled(true);
                button13.setEnabled(true);
                button23.setEnabled(true);
                button33.setEnabled(true);
                button43.setEnabled(true);
                button14.setEnabled(true);
                button24.setEnabled(true);
                button34.setEnabled(true);
                button44.setEnabled(true);

                boggle.setLastSelectedTile(-999, -999);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boggle.getCurrWord().length() == 0) {
                    infoView.setText("ERROR: you must input a word");
                } else if (!boggle.checkIfWordLongEnough()) {
                    infoView.setText("ERROR: " + boggle.getCurrWord() + " is not long enough");
                } else if (!boggle.checkIfValidWord()) {
                    infoView.setText("ERROR: " + boggle.getCurrWord() + " is not a word");
                } else if (!boggle.checkIfNewWord()) {
                    infoView.setText("ERROR: " + boggle.getCurrWord() + " has already been used");
                } else {
                    infoView.setText(boggle.getCurrWord() + " earned you " + boggle.scoreWord() + " points!");
                    boggle.addToPoints();
                    pointsView.setText("Points: " + boggle.getCurrPoints());

                    boggle.markWordAsUsed();
                    boggle.clearWord();

                    button11.setEnabled(true);
                    button21.setEnabled(true);
                    button31.setEnabled(true);
                    button41.setEnabled(true);
                    button12.setEnabled(true);
                    button22.setEnabled(true);
                    button32.setEnabled(true);
                    button42.setEnabled(true);
                    button13.setEnabled(true);
                    button23.setEnabled(true);
                    button33.setEnabled(true);
                    button43.setEnabled(true);
                    button14.setEnabled(true);
                    button24.setEnabled(true);
                    button34.setEnabled(true);
                    button44.setEnabled(true);

                    boggle.setLastSelectedTile(-999, -999);
                }
            }
        });

        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(1,1)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(1, 1);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button11.setEnabled(false);
                    boggle.setLastSelectedTile(1, 1);
                }
            }
        });

        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(2,1)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(2, 1);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button21.setEnabled(false);
                    boggle.setLastSelectedTile(2, 1);
                }
            }
        });

        button31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(3,1)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(3, 1);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button31.setEnabled(false);
                    boggle.setLastSelectedTile(3, 1);
                }
            }
        });

        button41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(4,1)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(4, 1);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button41.setEnabled(false);
                    boggle.setLastSelectedTile(4, 1);
                }
            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(1,2)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(1, 2);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button12.setEnabled(false);
                    boggle.setLastSelectedTile(1, 2);
                }
            }
        });

        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(2,2)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(2, 2);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button22.setEnabled(false);
                    boggle.setLastSelectedTile(2, 2);
                }
            }
        });

        button32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(3,2)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(3, 2);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button32.setEnabled(false);
                    boggle.setLastSelectedTile(3, 2);
                }
            }
        });

        button42.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(4,2)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(4, 2);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button42.setEnabled(false);
                    boggle.setLastSelectedTile(4, 2);
                }
            }
        });

        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(1,3)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(1, 3);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button13.setEnabled(false);
                    boggle.setLastSelectedTile(1, 3);
                }
            }
        });

        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(2,3)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(2, 3);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button23.setEnabled(false);
                    boggle.setLastSelectedTile(2, 3);
                }
            }
        });

        button33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(3,3)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(3, 3);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button33.setEnabled(false);
                    boggle.setLastSelectedTile(3, 3);
                }
            }
        });

        button43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(4,3)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(4, 3);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button43.setEnabled(false);
                    boggle.setLastSelectedTile(4, 3);
                }
            }
        });

        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(1,4)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(1, 4);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button14.setEnabled(false);
                    boggle.setLastSelectedTile(1, 4);
                }
            }
        });

        button24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(2,4)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(2, 4);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button24.setEnabled(false);
                    boggle.setLastSelectedTile(2, 4);
                }
            }
        });

        button34.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(3,4)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(3, 4);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button34.setEnabled(false);
                    boggle.setLastSelectedTile(3, 4);
                }
            }
        });

        button44.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boggle.checkIfValidTile(4,4)))
                    infoView.setText("ERROR: not a valid letter to select");
                else {
                    boggle.addLetter(4, 4);
                    infoView.setText("Current Word: " + boggle.getCurrWord());

                    button44.setEnabled(false);
                    boggle.setLastSelectedTile(4, 4);
                }
            }
        });
    }

    private void stopTimer(Runnable runnable) {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    private void updateSeconds(int newSeconds) {
        seconds = newSeconds;
        timeView.setText("Time: " + seconds);
    }

}