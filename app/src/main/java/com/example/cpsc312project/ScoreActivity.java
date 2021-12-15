package com.example.cpsc312project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    FirebaseFirestore db;

    Game finishedGame;
    Intent intent;
    String lobbyCode;

    List<String> firstUserWords;
    List<String> secondUserWords;
    List<String> sharedWords;

    Boggle boggleScoring;
    int firstUserPoints;
    int secondUserPoints;

    TextView scoreTextView;
    TextView firstUserNameTextView;
    TextView secondUserNameTextView;
    RecyclerView firstUserWordsRecyclerView;
    RecyclerView secondUserWordsRecyclerView;
    TextView firstUserPointsTextView;
    TextView secondUserPointsTextView;
    TextView winnerTextView;
    Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        db = FirebaseFirestore.getInstance();

        intent = getIntent();
        lobbyCode = intent.getStringExtra("lobbyCode");

        sharedWords = new ArrayList<>();

        boggleScoring = new Boggle(getApplicationContext());
        firstUserPoints = 0;
        secondUserPoints = 0;

        scoreTextView = findViewById(R.id.scoreTextView);
        firstUserNameTextView = findViewById(R.id.firstUserNameTextView);
        secondUserNameTextView = findViewById(R.id.secondUserNameTextView);
        firstUserWordsRecyclerView = findViewById(R.id.firstUserWordsRecyclerView);
        secondUserWordsRecyclerView = findViewById(R.id.secondUserWordsRecyclerView);
        firstUserPointsTextView = findViewById(R.id.firstUserPointsTextView);
        secondUserPointsTextView = findViewById(R.id.secondUserPointsTextView);
        winnerTextView = findViewById(R.id.winnerTextView);
        returnButton = findViewById(R.id.returnButton);

        scoreTextView.setVisibility(View.INVISIBLE);
        firstUserNameTextView.setVisibility(View.INVISIBLE);
        secondUserNameTextView.setVisibility(View.INVISIBLE);
        firstUserWordsRecyclerView.setVisibility(View.INVISIBLE);
        secondUserWordsRecyclerView.setVisibility(View.INVISIBLE);
        firstUserPointsTextView.setVisibility(View.INVISIBLE);
        secondUserPointsTextView.setVisibility(View.INVISIBLE);
        winnerTextView.setText("Waiting...");
        returnButton.setVisibility(View.INVISIBLE);

        db.collection("games").document(lobbyCode)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        finishedGame = documentSnapshot.toObject(Game.class);
                        if (finishedGame.getFirstUserWords() != null && finishedGame.getSecondUserWords() != null) {
                            prepareScoreScreen(finishedGame);
                        } else {
                            db.collection("games").document(lobbyCode)
                                    .addSnapshotListener(ScoreActivity.this, new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                            @Nullable FirebaseFirestoreException e) {
                                            finishedGame = snapshot.toObject(Game.class);
                                            if (finishedGame.getFirstUserWords() != null && finishedGame.getSecondUserWords() != null)
                                                prepareScoreScreen(finishedGame);
                                        }
                                    });
                        }
                    }
                });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainScreenIntent = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(mainScreenIntent);
                ScoreActivity.this.finish();
            }
        });

    }

    public void prepareScoreScreen(Game game) {

        firstUserWords = game.getFirstUserWords();
        secondUserWords = game.getSecondUserWords();

        for (String word : firstUserWords) {
            if (secondUserWords.contains(word))
                sharedWords.add(word);
            else
                firstUserPoints += boggleScoring.scoreWord(word);
        }

        for (String word : secondUserWords) {
            if (!(sharedWords.contains(word)))
                secondUserPoints += boggleScoring.scoreWord(word);
        }

        firstUserNameTextView.setText(game.getFirstUser().getUsername());
        secondUserNameTextView.setText(game.getSecondUser().getUsername());

        RecyclerView.LayoutManager firstLayoutManager = new LinearLayoutManager(ScoreActivity.this);
        RecyclerView.LayoutManager secondLayoutManager = new LinearLayoutManager(ScoreActivity.this);
        firstUserWordsRecyclerView.setLayoutManager(firstLayoutManager);
        secondUserWordsRecyclerView.setLayoutManager(secondLayoutManager);
        FirstUserWordsAdapter firstAdapter = new FirstUserWordsAdapter();
        SecondUserWordsAdapter secondAdapter = new SecondUserWordsAdapter();
        firstUserWordsRecyclerView.setAdapter(firstAdapter);
        secondUserWordsRecyclerView.setAdapter(secondAdapter);

        firstUserPointsTextView.setText(firstUserPoints + " Points");
        secondUserPointsTextView.setText(secondUserPoints + " Points");

        if (firstUserPoints > secondUserPoints)
            winnerTextView.setText(game.getFirstUser().getUsername() + " wins!");
        else
            winnerTextView.setText(game.getSecondUser().getUsername() + " wins!");

        scoreTextView.setVisibility(View.VISIBLE);
        firstUserNameTextView.setVisibility(View.VISIBLE);
        secondUserNameTextView.setVisibility(View.VISIBLE);
        firstUserWordsRecyclerView.setVisibility(View.VISIBLE);
        secondUserWordsRecyclerView.setVisibility(View.VISIBLE);
        firstUserPointsTextView.setVisibility(View.VISIBLE);
        secondUserPointsTextView.setVisibility(View.VISIBLE);
        returnButton.setVisibility(View.VISIBLE);

    }

    class FirstUserWordsAdapter extends RecyclerView.Adapter<FirstUserWordsAdapter.WordViewHolder> {

        class WordViewHolder extends RecyclerView.ViewHolder {

            CardView wordCardView;
            TextView wordTextView;

            public WordViewHolder(@NonNull View itemView) {
                super(itemView);

                wordCardView = itemView.findViewById(R.id.wordCardView);
                wordTextView = itemView.findViewById(R.id.wordTextView);
            }

            public void updateView(String word) {
                wordTextView.setText(word);
                if (sharedWords.contains(word)) {
                    wordTextView.setTextColor(getResources().getColor(R.color.red, getTheme()));
                }
            }

        }

        public FirstUserWordsAdapter() {
            super();
        }

        @NonNull
        @Override
        public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ScoreActivity.this)
                    .inflate(R.layout.card_view_word, parent, false);
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
            String word = firstUserWords.get(position);
            holder.updateView(word);
        }

        @Override
        public int getItemCount() {
            return firstUserWords.size();
        }

    }

    class SecondUserWordsAdapter extends RecyclerView.Adapter<SecondUserWordsAdapter.WordViewHolder> {

        class WordViewHolder extends RecyclerView.ViewHolder {

            CardView wordCardView;
            TextView wordTextView;

            public WordViewHolder(@NonNull View itemView) {
                super(itemView);

                wordCardView = itemView.findViewById(R.id.wordCardView);
                wordTextView = itemView.findViewById(R.id.wordTextView);
            }

            public void updateView(String word) {
                wordTextView.setText(word);
                if (sharedWords.contains(word)) {
                    wordTextView.setTextColor(getResources().getColor(R.color.red, getTheme()));
                }
            }

        }

        public SecondUserWordsAdapter() {
            super();
        }

        @NonNull
        @Override
        public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ScoreActivity.this)
                    .inflate(R.layout.card_view_word, parent, false);
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
            String word = secondUserWords.get(position);
            holder.updateView(word);
        }

        @Override
        public int getItemCount() {
            return secondUserWords.size();
        }

    }

}