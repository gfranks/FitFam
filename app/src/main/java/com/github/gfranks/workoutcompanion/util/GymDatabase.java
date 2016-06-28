package com.github.gfranks.workoutcompanion.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.github.gfranks.workoutcompanion.data.model.WCGymGeometry;
import com.github.gfranks.workoutcompanion.data.model.WCGymGeometryLocation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GymDatabase {

    public static final String BROADCAST = "gyms_modified";

    private String[] ALL_COLUMNS = {
            GymSQLiteHelper.COLUMN_ID, GymSQLiteHelper.COLUMN_PLACE_ID,
            GymSQLiteHelper.COLUMN_NAME, GymSQLiteHelper.COLUMN_ICON,
            GymSQLiteHelper.COLUMN_VICINITY, GymSQLiteHelper.COLUMN_RATING,
            GymSQLiteHelper.COLUMN_GEOMETRY
    };

    private GymSQLiteHelper mDbHelper;
    private SQLiteDatabase mDatabase;
    private LocalBroadcastManager mBroadcastManager;
    private Context mContext;

    public GymDatabase(Context context) {
        mContext = context;
        mDbHelper = new GymSQLiteHelper(context);
        mBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public boolean isOpen() {
        return mDatabase != null && mDatabase.isOpen();
    }

    public void saveGym(String userId, WCGym gym) {
        ContentValues values = new ContentValues();
        values.put(GymSQLiteHelper.COLUMN_ID, gym.getId());
        values.put(GymSQLiteHelper.COLUMN_USER_ID, userId);
        values.put(GymSQLiteHelper.COLUMN_PLACE_ID, gym.getPlace_id());
        values.put(GymSQLiteHelper.COLUMN_NAME, gym.getName());
        if (gym.getPhotos() != null && !gym.getPhotos().isEmpty()) {
            values.put(GymSQLiteHelper.COLUMN_ICON, GymUtils.getRandomGymPhoto(mContext, gym.getPhotos()));
        } else {
            values.put(GymSQLiteHelper.COLUMN_ICON, gym.getIcon());
        }
        values.put(GymSQLiteHelper.COLUMN_VICINITY, gym.getVicinity());
        values.put(GymSQLiteHelper.COLUMN_RATING, String.valueOf(gym.getRating()));
        values.put(GymSQLiteHelper.COLUMN_GEOMETRY, getStringFromGeometry(gym.getGeometry()));
        mDatabase.insert(GymSQLiteHelper.TABLE_GYMS, null, values);
        mBroadcastManager.sendBroadcast(new Intent(BROADCAST));
    }

    public void deleteGym(String userId, WCGym gym) {
        deleteGym(userId, gym.getId());
    }

    public void deleteGym(String userId, String gymId) {
        mDatabase.delete(GymSQLiteHelper.TABLE_GYMS, GymSQLiteHelper.COLUMN_ID + "=? AND "
                + GymSQLiteHelper.COLUMN_USER_ID + "=?", new String[]{gymId, userId});
        mBroadcastManager.sendBroadcast(new Intent(BROADCAST));
    }

    public void isFavorite(String userId, WCGym gym) {
        isFavorite(userId, gym.getId());
    }

    public boolean isFavorite(String userId, String gymId) {
        Cursor cursor = mDatabase.query(GymSQLiteHelper.TABLE_GYMS,
                ALL_COLUMNS, GymSQLiteHelper.COLUMN_ID + "=? AND " + GymSQLiteHelper.COLUMN_USER_ID + "=?",
                new String[]{gymId, userId}, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<WCGym> getAllGyms(String userId) {
        List<WCGym> gyms = new ArrayList<>();

        Cursor cursor = mDatabase.query(GymSQLiteHelper.TABLE_GYMS,
                ALL_COLUMNS, GymSQLiteHelper.COLUMN_USER_ID + "=?", new String[]{userId}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WCGym gym = cursorToGym(cursor);
            gyms.add(gym);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return gyms;
    }

    private WCGym cursorToGym(Cursor cursor) {
        return new WCGym.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(GymSQLiteHelper.COLUMN_ID)))
                .setPlace_id(cursor.getString(cursor.getColumnIndex(GymSQLiteHelper.COLUMN_PLACE_ID)))
                .setName(cursor.getString(cursor.getColumnIndex(GymSQLiteHelper.COLUMN_NAME)))
                .setIcon(cursor.getString(cursor.getColumnIndex(GymSQLiteHelper.COLUMN_ICON)))
                .setVicinity(cursor.getString(cursor.getColumnIndex(GymSQLiteHelper.COLUMN_VICINITY)))
                .setRating(Float.valueOf(cursor.getString(cursor.getColumnIndex(GymSQLiteHelper.COLUMN_RATING))))
                .setGeometry(getGeometryFromString(cursor.getString(cursor.getColumnIndex(GymSQLiteHelper.COLUMN_GEOMETRY))))
                .build();
    }

    private String getStringFromGeometry(WCGymGeometry geometry) {
        StringBuilder sb = new StringBuilder();
        sb.append(geometry.getLocation().getLat());
        sb.append(",");
        sb.append(geometry.getLocation().getLng());
        return sb.toString();
    }

    private WCGymGeometry getGeometryFromString(String value) {
        String[] values = value.split(",");
        WCGymGeometry geometry = new WCGymGeometry();
        WCGymGeometryLocation location = new WCGymGeometryLocation();
        location.setLat(Double.parseDouble(values[0]));
        location.setLng(Double.parseDouble(values[1]));
        geometry.setLocation(location);
        return geometry;
    }

    private class GymSQLiteHelper extends SQLiteOpenHelper {

        static final String TABLE_GYMS = "gyms";
        static final String COLUMN_ID = "id";
        static final String COLUMN_USER_ID = "user_id";
        static final String COLUMN_PLACE_ID = "place_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_ICON = "icon";
        static final String COLUMN_VICINITY = "vicinity";
        static final String COLUMN_RATING = "rating";
        static final String COLUMN_GEOMETRY = "geometry";

        private static final String DATABASE_NAME = "gyms.db";
        private static final int DATABASE_VERSION = 1;

        // Database creation sql statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_GYMS + "("
                + COLUMN_ID + " text, "
                + COLUMN_USER_ID + " text, "
                + COLUMN_PLACE_ID + " text, "
                + COLUMN_NAME + " text, "
                + COLUMN_ICON + " text, "
                + COLUMN_VICINITY + " text, "
                + COLUMN_RATING + " text, "
                + COLUMN_GEOMETRY + " text);";

        GymSQLiteHelper(Context context) {
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
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GYMS);
            onCreate(db);
        }
    }
}
