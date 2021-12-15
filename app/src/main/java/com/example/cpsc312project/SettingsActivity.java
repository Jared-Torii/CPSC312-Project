package com.example.cpsc312project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText timeEditText;
    Button saveButton;

    String nameSetting;
    int timeSetting;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        nameEditText = findViewById(R.id.nameEditText);
        timeEditText = findViewById(R.id.timeEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString().length() > 0
                || timeEditText.getText().toString().length() > 0) {
                    nameSetting = nameEditText.getText().toString();
                    if (timeEditText.getText().toString().length() > 0)
                        timeSetting = Integer.parseInt(timeEditText.getText().toString());

                    sharedPreferences = getSharedPreferences("usersettings", 0);
                    editor = sharedPreferences.edit();

                    if (nameEditText.getText().toString().length() > 0)
                        editor.putString("nameSetting", nameSetting);
                    if (timeEditText.getText().toString().length() > 0)
                        editor.putInt("timeSetting", timeSetting);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Settings saved!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Must enter a value!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}