package com.example.chrisbridgesweighttracker3;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//Logs weight entries into SQLite and sends sms if allowed
public class LogWeightActivity extends AppCompatActivity {
    private EditText weightInput;
    private WeightRepo weightRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_weight);

        weightRepo = new WeightRepo(this);

        weightInput = findViewById(R.id.editTextWeightLog);
        Button logButton = findViewById(R.id.buttonLog);

        logButton.setOnClickListener(v -> {
            String w = weightInput.getText().toString().trim();
            if (w.isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_weight), Toast.LENGTH_SHORT).show();
                return;
            }
            double weight;
            try {
                weight = Double.parseDouble(w);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.invalid_weight), Toast.LENGTH_SHORT).show();
                return;
            }

            long userId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getLong("user_id", -1);
            if (userId <= 0) {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            long rowId = weightRepo.add(userId, today, weight);
            if (rowId <= 0) {
                Toast.makeText(this, "Failed to log weight.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, getString(R.string.weight_logged), Toast.LENGTH_SHORT).show();

            // Checks goal and maybe send SMS if permission was granted
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String goalStr = prefs.getString("goal_weight", null);
            if (goalStr != null) {
                try {
                    double goal = Double.parseDouble(goalStr);
                    boolean reached = weight <= goal;
                    if (reached) maybeSendGoalSms("Goal reached: " + weight + " lbs");
                } catch (NumberFormatException ignored) { }
            }

            finish();
        });
    }

    /** Sends an SMS if allowed */
    private void maybeSendGoalSms(String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            String phone = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    .getString("notify_phone", null);
            if (phone == null || phone.trim().isEmpty()) return;

            try {
                SmsManager.getDefault().sendTextMessage(phone, null, message, null, null);
                Toast.makeText(this, "SMS alert sent.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
