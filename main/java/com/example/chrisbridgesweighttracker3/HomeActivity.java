package com.example.chrisbridgesweighttracker3;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//HomeActivity
public class HomeActivity extends AppCompatActivity {

    // TextView for displaying goal, current, and weekly progress
    private TextView textGoalWeight, textCurrentWeight, textWeeklyProgress;

    // Navigation buttons
    private Button buttonLogWeight, buttonSetGoal, buttonHistory;

    // Date/number helpers
    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final NumberFormat ONE_DEC = NumberFormat.getNumberInstance(Locale.US);

    static {
        ISO.setLenient(false);
        ONE_DEC.setMaximumFractionDigits(1);
        ONE_DEC.setMinimumFractionDigits(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Links UI widgets
        textGoalWeight = findViewById(R.id.textGoalWeight);
        textCurrentWeight = findViewById(R.id.textCurrentWeight);
        textWeeklyProgress = findViewById(R.id.textWeeklyProgress);

        buttonLogWeight = findViewById(R.id.buttonLogWeight);
        buttonSetGoal = findViewById(R.id.buttonSetGoal);
        buttonHistory = findViewById(R.id.buttonHistory);

        // Navigation
        buttonLogWeight.setOnClickListener(
                v -> startActivity(new android.content.Intent(this, LogWeightActivity.class)));
        buttonSetGoal.setOnClickListener(
                v -> startActivity(new android.content.Intent(this, SetGoalActivity.class)));
        buttonHistory.setOnClickListener(
                v -> startActivity(new android.content.Intent(this, HistoryActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Goal weight
        var prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String goalWeight = prefs.getString("goal_weight", "Not set");
        textGoalWeight.setText(getString(R.string.goal_weight, goalWeight));

        // Current weight from thew latest DB entry
        long userId = prefs.getLong("user_id", -1);
        if (userId > 0) {
            WeightRepo repo = new WeightRepo(this);
            try (Cursor c = repo.readLatest(userId)) {
                if (c != null && c.moveToFirst()) {
                    double w = c.getDouble(c.getColumnIndexOrThrow(AppDb.W_WEIGHT));
                    String pretty = getString(R.string.weight_with_unit, ONE_DEC.format(w));
                    textCurrentWeight.setText(getString(R.string.current_weight, pretty));
                } else {
                    textCurrentWeight.setText(getString(R.string.current_weight, "—"));
                }
            }
        } else {
            textCurrentWeight.setText(getString(R.string.current_weight, "—"));
        }

        // Computes weekly progress
        textWeeklyProgress.setText(getString(R.string.weekly_progress, "Coming soon"));
    }
}


    /*Computes the weekly progress
    private String computeWeeklyProgress(long userId) {
        WeightRepo repo = new WeightRepo(this);

        // Finds latest entry for progress
        String latestDateStr = null;
        double latestWeight = 0.0;
        try (Cursor latest = repo.readLatest(userId)) {
            if (latest == null || !latest.moveToFirst()) return "—";
            latestDateStr = latest.getString(latest.getColumnIndexOrThrow(AppDb.W_DATE));
            latestWeight = latest.getDouble(latest.getColumnIndexOrThrow(AppDb.W_WEIGHT));
        }

        // Parses the latest date and computes weekly progress
        Calendar latestCal = Calendar.getInstance();
        try {
            latestCal.setTime(ISO.parse(latestDateStr));
        } catch (ParseException e) {
            return "—";
        }
        Calendar startCal = (Calendar) latestCal.clone();
        startCal.add(Calendar.DAY_OF_MONTH, -7);
        String startDateStr = ISO.format(startCal.getTime());

     */
