package com.example.chrisbridgesweighttracker3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//Main activity
public class MainActivity extends AppCompatActivity {

    //Input for username and pass
    private EditText usernameEt, passwordEt;

    // Login and register buttons
    private Button loginButton, registerButton;

    // Repo for db operations
    private UserRepo users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize repo
        users = new UserRepo(this);

        // Links UI to code
        usernameEt = findViewById(R.id.editTextUsername);
        passwordEt = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);

        // Button click actions
        loginButton.setOnClickListener(v -> doLogin());
        registerButton.setOnClickListener(v -> doRegister());
    }

    // Login validation
    private void doLogin() {
        String u = usernameEt.getText().toString().trim();
        String p = passwordEt.getText().toString();

        // Ensures both fields are filled
        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Enter username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Login attempt
        long userId = users.login(u, p);
        if (userId > 0) {
            //Saves user ID in SharedPreferences
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    .putLong("user_id", userId).apply();
            // Permission screen
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
        }
    }

    // User registration
    private void doRegister() {
        String u = usernameEt.getText().toString().trim();
        String p = passwordEt.getText().toString();

        // Ensures both fields are filled
        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Enter username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = users.register(u, p);
        if (id > 0) {
            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    .putLong("user_id", id).apply();
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
        }
    }
}
