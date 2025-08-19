package com.example.chrisbridgesweighttracker3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// Crud operations for weight db
public class WeightRepo {
    private final AppDb helper;
    public WeightRepo(Context ctx) { helper = new AppDb(ctx); }

    // CREATE
    public long add(long userId, String date, double weight) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AppDb.W_USER_ID, userId);
        cv.put(AppDb.W_DATE, date);
        cv.put(AppDb.W_WEIGHT, weight);
        return db.insert(AppDb.T_WEIGHTS, null, cv);
    }

    // READ
    public Cursor readAll(long userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(AppDb.T_WEIGHTS, null,
                AppDb.W_USER_ID + "=?",
                new String[]{ String.valueOf(userId) },
                null, null,
                AppDb.W_DATE + " DESC");
    }

    // READ latest single row
    public Cursor readLatest(long userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(AppDb.T_WEIGHTS, null,
                AppDb.W_USER_ID + "=?",
                new String[]{ String.valueOf(userId) },
                null, null,
                AppDb.W_DATE + " DESC",
                "1");
    }

    // UPDATE by row id
    public int update(long id, String date, double weight) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AppDb.W_DATE, date);
        cv.put(AppDb.W_WEIGHT, weight);
        return db.update(AppDb.T_WEIGHTS, cv, AppDb.W_ID + "=?",
                new String[]{ String.valueOf(id) });
    }

    // DELETE by row id
    public int delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(AppDb.T_WEIGHTS, AppDb.W_ID + "=?",
                new String[]{ String.valueOf(id) });
    }
}
