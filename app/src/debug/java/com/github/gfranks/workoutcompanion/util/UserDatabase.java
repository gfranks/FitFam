package com.github.gfranks.workoutcompanion.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.github.gfranks.workoutcompanion.data.model.WCUser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDatabase {

    private String[] ALL_COLUMNS = {
            UserSQLiteHelper.COLUMN_ID, UserSQLiteHelper.COLUMN_IMAGE,
            UserSQLiteHelper.COLUMN_FIRST_NAME, UserSQLiteHelper.COLUMN_LAST_NAME,
            UserSQLiteHelper.COLUMN_PHONE_NUMBER, UserSQLiteHelper.COLUMN_BIRTHDAY,
            UserSQLiteHelper.COLUMN_EMAIL, UserSQLiteHelper.COLUMN_PASSWORD,
            UserSQLiteHelper.COLUMN_SEX, UserSQLiteHelper.COLUMN_WEIGHT,
            UserSQLiteHelper.COLUMN_EXERCISES, UserSQLiteHelper.COLUMN_CAN_SEE_INFO
    };

    private UserSQLiteHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    public UserDatabase(Context context) {
        mDbHelper = new UserSQLiteHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public void createUser(WCUser user) {
        ContentValues values = new ContentValues();
        values.put(UserSQLiteHelper.COLUMN_ID, user.getId());
        values.put(UserSQLiteHelper.COLUMN_IMAGE, user.getImage());
        values.put(UserSQLiteHelper.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(UserSQLiteHelper.COLUMN_LAST_NAME, user.getLastName());
        values.put(UserSQLiteHelper.COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(UserSQLiteHelper.COLUMN_BIRTHDAY, user.getBirthday());
        values.put(UserSQLiteHelper.COLUMN_EMAIL, user.getEmail());
        values.put(UserSQLiteHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(UserSQLiteHelper.COLUMN_SEX, user.getSex());
        values.put(UserSQLiteHelper.COLUMN_WEIGHT, user.getWeight());
        values.put(UserSQLiteHelper.COLUMN_EXERCISES, TextUtils.join(",", user.getExercises()));
        values.put(UserSQLiteHelper.COLUMN_CAN_SEE_INFO, user.isCanSeeContactInfo() ? 1 : 0);
        mDatabase.insert(UserSQLiteHelper.TABLE_USERS, null, values);
    }

    public void updateUser(WCUser user) {
        ContentValues values = new ContentValues();
        values.put(UserSQLiteHelper.COLUMN_ID, user.getId());
        values.put(UserSQLiteHelper.COLUMN_IMAGE, user.getImage());
        values.put(UserSQLiteHelper.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(UserSQLiteHelper.COLUMN_LAST_NAME, user.getLastName());
        values.put(UserSQLiteHelper.COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(UserSQLiteHelper.COLUMN_BIRTHDAY, user.getBirthday());
        values.put(UserSQLiteHelper.COLUMN_EMAIL, user.getEmail());
        values.put(UserSQLiteHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(UserSQLiteHelper.COLUMN_SEX, user.getSex());
        values.put(UserSQLiteHelper.COLUMN_WEIGHT, user.getWeight());
        values.put(UserSQLiteHelper.COLUMN_EXERCISES, TextUtils.join(",", user.getExercises()));
        values.put(UserSQLiteHelper.COLUMN_CAN_SEE_INFO, user.isCanSeeContactInfo() ? 1 : 0);
        mDatabase.update(UserSQLiteHelper.TABLE_USERS, values, UserSQLiteHelper.COLUMN_ID + "=?", new String[]{user.getId()});
    }

    public WCUser findUserById(String id) {
        Cursor cursor = mDatabase.query(UserSQLiteHelper.TABLE_USERS,
                ALL_COLUMNS, UserSQLiteHelper.COLUMN_ID + "=?", new String[]{id}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursorToUser(cursor);
        }
        return null;
    }

    public WCUser findUserByEmail(String email) {
        Cursor cursor = mDatabase.query(UserSQLiteHelper.TABLE_USERS,
                ALL_COLUMNS, UserSQLiteHelper.COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursorToUser(cursor);
        }
        return null;
    }

    public void deleteUser(WCUser user) {
        System.out.println("Comment deleted with id: " + user.getId());
        mDatabase.delete(UserSQLiteHelper.TABLE_USERS, UserSQLiteHelper.COLUMN_ID
                + " = " + user.getId(), null);
    }

    public List<WCUser> getAllUsers() {
        List<WCUser> users = new ArrayList<WCUser>();

        Cursor cursor = mDatabase.query(UserSQLiteHelper.TABLE_USERS,
                ALL_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WCUser user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return users;
    }

    private WCUser cursorToUser(Cursor cursor) {
        WCUser.Builder builder = new WCUser.Builder();
        builder.setImage(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_IMAGE)));
        builder.setFirstName(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_FIRST_NAME)));
        builder.setLastName(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_LAST_NAME)));
        builder.setPhoneNumber(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_PHONE_NUMBER)));
        builder.setBirthday(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_BIRTHDAY)));
        builder.setEmail(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_EMAIL)));
        builder.setPassword(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_PASSWORD)));
        builder.setSex(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_SEX)));
        builder.setWeight(cursor.getInt(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_WEIGHT)));
        builder.setExercises(new ArrayList<>(Arrays.asList(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_EXERCISES)).split(","))));
        builder.setCanSeeContactInfo(cursor.getInt(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_CAN_SEE_INFO)) == 1);
        WCUser user = builder.build();
        user.setId(cursor.getString(cursor.getColumnIndex(UserSQLiteHelper.COLUMN_ID)));
        return user;
    }

    public class UserSQLiteHelper extends SQLiteOpenHelper {

        public static final String TABLE_USERS = "users";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_BIRTHDAY = "birthday";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_EXERCISES = "exercises";
        public static final String COLUMN_CAN_SEE_INFO = "can_see_info";

        private static final String DATABASE_NAME = "users.db";
        private static final int DATABASE_VERSION = 1;

        // Database creation sql statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_USERS + "("
                + COLUMN_ID + " text primary key, "
                + COLUMN_IMAGE + " text, "
                + COLUMN_FIRST_NAME + " text, "
                + COLUMN_LAST_NAME + " text, "
                + COLUMN_PHONE_NUMBER + " text, "
                + COLUMN_BIRTHDAY + " text, "
                + COLUMN_EMAIL + " text not null, "
                + COLUMN_PASSWORD + " text not null, "
                + COLUMN_SEX + " text, "
                + COLUMN_WEIGHT + " int, "
                + COLUMN_EXERCISES + " text, "
                + COLUMN_CAN_SEE_INFO + " int);";

        public UserSQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(UserDatabase.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }
}