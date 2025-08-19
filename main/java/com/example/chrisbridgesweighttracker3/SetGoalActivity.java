package com.example.chrisbridgesweighttracker3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Set goal
public class SetGoalActivity extends AppCompatActivity {

    private EditText goalInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        goalInput = findViewById(R.id.editTextGoal);
        Button saveButton = findViewById(R.id.buttonSaveGoal);

        // Pre-filled if a goal weight exists
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String existing = prefs.getString("goal_weight", "");
        if (existing != null && !existing.isEmpty()) {
            goalInput.setText(existing);
        }

        saveButton.setOnClickListener(v -> {
            String g = goalInput.getText().toString().trim();
            if (g.isEmpty()) {
                goalInput.setError("Enter a goal weight");
                return;
            }
            try {
                double d = Double.parseDouble(g);
                if (d <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                goalInput.setError("Enter a valid number (e.g., 165.0)");
                return;
            }
            prefs.edit().putString("goal_weight", g).apply();
            Toast.makeText(this, "Goal saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
