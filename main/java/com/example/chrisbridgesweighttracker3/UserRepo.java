// UserRepo.java
package com.example.chrisbridgesweighttracker3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

// Username repo
public class UserRepo {

    private final AppDb helper;

    public UserRepo(Context ctx) {
        helper = new AppDb(ctx);
    }

    // New user registration
    public long register(String username, String rawPassword) {
        // Hash the password
        final String saltedHash = PasswordUtil.hash(rawPassword);

        // Inserts username and hash into DB
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AppDb.U_USERNAME, username);
        cv.put(AppDb.U_PASSWORD, saltedHash);

        try {
            return db.insertOrThrow(AppDb.T_USERS, null, cv);
        } catch (SQLiteConstraintException dup) {
            // username violation, username taken
            return -1;
        }
    }

   // Username authentication
    public long login(String username, String rawPassword) {
        SQLiteDatabase db = helper.getReadableDatabase();

        try (Cursor c = db.query(
                AppDb.T_USERS,
                new String[]{AppDb.U_ID, AppDb.U_PASSWORD},
                AppDb.U_USERNAME + "=?",
                new String[]{username},
                null, null, null
        )) {
            if (c != null && c.moveToFirst()) {
                long userId = c.getLong(c.getColumnIndexOrThrow(AppDb.U_ID));
                String storedSaltPlusHash = c.getString(c.getColumnIndexOrThrow(AppDb.U_PASSWORD));

                // Verify raw password against stored hash
                boolean ok = PasswordUtil.verify(rawPassword, storedSaltPlusHash);
                return ok ? userId : -1;
            }
        }
        return -1; // user not found
    }
}
