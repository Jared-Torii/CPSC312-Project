package com.example.cpsc312project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class JoinActivity extends AppCompatActivity {

    EditText codeEditText;
    Button joinLobbyButton;

    String enteredCode;

    FirebaseFirestore db;
    Game foundGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Join a Game");

        codeEditText = findViewById(R.id.codeEditText);
        joinLobbyButton = findViewById(R.id.joinLobbyButton);

        db = FirebaseFirestore.getInstance();

        joinLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredCode = codeEditText.getText().toString();

                db.collection("games").document(enteredCode)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        foundGame = document.toObject(Game.class);
                                        if (foundGame.getNumUsers() == 1) {
                                            Intent lobbyIntent = new Intent(JoinActivity.this, LobbyActivity.class);
                                            lobbyIntent.putExtra("isHost", false);
                                            lobbyIntent.putExtra("lobbyCode", enteredCode);
                                            lobbyIntent.putExtra("savedName", getIntent().getStringExtra("savedName"));
                                            startActivity(lobbyIntent);
                                            JoinActivity.this.finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Sorry, that game is full...",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Game not found...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mainScreenIntent = new Intent(JoinActivity.this, MainActivity.class);
                startActivity(mainScreenIntent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}