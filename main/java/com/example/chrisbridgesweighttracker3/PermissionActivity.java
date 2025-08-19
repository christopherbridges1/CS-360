package com.example.chrisbridgesweighttracker3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        Button allowButton = findViewById(R.id.buttonAllowSms);
        Button denyButton = findViewById(R.id.buttonDenySms);

        allowButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            } else {
                Toast.makeText(this, "SMS permission is already granted", Toast.LENGTH_SHORT).show();
                launchNextScreen();
            }
        });

        denyButton.setOnClickListener(v -> {
            Toast.makeText(this, "SMS permission denied: notifications are disabled", Toast.LENGTH_SHORT).show();
            launchNextScreen();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission has been granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission has been denied", Toast.LENGTH_SHORT).show();
            }
            launchNextScreen();
        }
    }

    private void launchNextScreen() {
        Intent intent = new Intent(PermissionActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
