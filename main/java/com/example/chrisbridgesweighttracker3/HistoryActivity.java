package com.example.chrisbridgesweighttracker3;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 History activity displays weight history in RecyclerView
 CRUD functionality*/
public class HistoryActivity extends AppCompatActivity implements WeightAdapter.DeleteClickListener {

    // UI Components
    private RecyclerView recyclerView;
    private EditText editDate, editWeight;
    private Button buttonAdd;

    //Data and adapter for RecyclerView
    private final List<WeightEntry> entries = new ArrayList<>();
    private WeightAdapter adapter;
    // Repo for DB
    private WeightRepo repo;
    private long userId;
    // Date format
    private final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    // Number format
    private static final NumberFormat ONE_DEC = NumberFormat.getNumberInstance(Locale.US);

    static {
        ONE_DEC.setMaximumFractionDigits(1);
        ONE_DEC.setMinimumFractionDigits(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ISO.setLenient(false);  // Strict date parsing

        //Initialize db repo and load user id
        repo = new WeightRepo(this);
        userId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getLong("user_id", -1);

        // UI components
        recyclerView = findViewById(R.id.recyclerView);
        editDate = findViewById(R.id.editTextDate);
        editWeight = findViewById(R.id.editTextWeight);
        buttonAdd = findViewById(R.id.buttonAddEntry);

        // Back button
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // RecyclerView with grid
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new WeightAdapter(entries, this);
        recyclerView.setAdapter(adapter);

        // Input listeners
        editDate.setOnClickListener(v -> showDatePicker());
        buttonAdd.setOnClickListener(v -> addEntry());

        // Load data from db
        refreshFromDb();
    }


    // Date picker dialog box
    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(Calendar.YEAR, y);
                    picked.set(Calendar.MONTH, m);
                    picked.set(Calendar.DAY_OF_MONTH, d);
                    editDate.setText(ISO.format(picked.getTime())); // yyyy-MM-dd
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dlg.show();
    }

    // Verifies date is valid
    private boolean isValidIsoDate(String s) {
        try { ISO.parse(s); return true; }
        catch (ParseException e) { return false; }
    }

    // Adds weight entry to SQLite db and update RecylcerView
    private void addEntry() {
        String date = editDate.getText().toString().trim();
        String w = editWeight.getText().toString().trim();

        // Date validation
        if (!isValidIsoDate(date)) {
            editDate.setError("Use yyyy-MM-dd (pick from calendar)");
            return;
        }
        // Weight validation
        if (TextUtils.isEmpty(w)) {
            editWeight.setError("Required");
            return;
        }
        double wd;
        try { wd = Double.parseDouble(w); }
        catch (NumberFormatException e) { editWeight.setError("Number like 156.0"); return; }

        // Inserts into db
        long id = repo.add(userId, date, wd);
        // If successful, format and add to RecyclerView
        if (id > 0) {
            String pretty = ONE_DEC.format(wd);
            entries.add(0, new WeightEntry(id, date, pretty));
            adapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            editDate.setText("");
            editWeight.setText("");
        } else {
            Toast.makeText(this, "Insert failed.", Toast.LENGTH_SHORT).show();
        }
    }

    // All weight entries from SQLite db and refresh
    private void refreshFromDb() {
        entries.clear();
        try (Cursor c = repo.readAll(userId)) {
            int colId = c.getColumnIndexOrThrow(AppDb.W_ID);
            int colDate = c.getColumnIndexOrThrow(AppDb.W_DATE);
            int colWeight = c.getColumnIndexOrThrow(AppDb.W_WEIGHT);
            while (c.moveToNext()) {
                long id = c.getLong(colId);
                String date = c.getString(colDate);
                double w = c.getDouble(colWeight);
                String pretty = ONE_DEC.format(w);
                entries.add(new WeightEntry(id, date, pretty));
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Remove entry from db and update RecyclerView
    @Override
    public void onDeleteClick(int position) {
        WeightEntry e = entries.get(position);
        int rows = repo.delete(e.getId());
        if (rows > 0) {
            entries.remove(position);
            adapter.notifyItemRemoved(position);
        } else {
            Toast.makeText(this, "Delete failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
