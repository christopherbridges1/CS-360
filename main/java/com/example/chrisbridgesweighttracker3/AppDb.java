package com.example.chrisbridgesweighttracker3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** SQLite database used in the app. SHA-256 used for passwords **/
public class AppDb extends SQLiteOpenHelper {
    public static final String DB_NAME = "weight_app.db"; //db name
    public static final int DB_VERSION = 1; //schema version

    //Users table
    public static final String T_USERS = "users";
    public static final String U_ID = "_id";    // Primary key
    public static final String U_USERNAME = "username";     //Unique username
    public static final String U_PASSWORD = "password";     // SHA-256 hash

    //Weight table
    public static final String T_WEIGHTS = "weights";
    public static final String W_ID = "_id";    //Primary key
    public static final String W_USER_ID = "user_id";
    public static final String W_DATE = "date"; //date
    public static final String W_WEIGHT = "weight"; // weight value

    public AppDb(Context ctx) { super(ctx, DB_NAME, null, DB_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table with username and hashed passwords
        db.execSQL("CREATE TABLE " + T_USERS + " (" +
                U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                U_USERNAME + " TEXT UNIQUE NOT NULL, " +
                U_PASSWORD + " TEXT NOT NULL)");

        // Table with weight entries linked to user
        db.execSQL("CREATE TABLE " + T_WEIGHTS + " (" +
                W_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                W_USER_ID + " INTEGER NOT NULL, " +
                W_DATE + " TEXT NOT NULL, " +
                W_WEIGHT + " REAL NOT NULL, " +
                "FOREIGN KEY(" + W_USER_ID + ") REFERENCES " + T_USERS + "(" + U_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Drop table and recreate if version change
        db.execSQL("DROP TABLE IF EXISTS " + T_WEIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        onCreate(db);
    }
}
