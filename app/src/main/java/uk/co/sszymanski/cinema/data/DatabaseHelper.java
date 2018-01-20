package uk.co.sszymanski.cinema.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.co.sszymanski.cinema.pojo.MovieItem;
import uk.co.sszymanski.cinema.pojo.Watched;

/**
 * Created by rex on 18/01/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = getClass().getSimpleName();
    private static final String DATABASE_NAME = "WhatToWatchDatabase";
    private static final String WATCHED_TABLE_NAME = "watched";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_WATCHED_ID = "watched_id";
    private static final int DATABSE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Map<String, String> watchTableColumns = createColumnMap(WATCHED_TABLE_NAME);
        String createWatchedTableQuery = createTableQuery(WATCHED_TABLE_NAME, watchTableColumns);
        db.execSQL(createWatchedTableQuery);
        Log.v(TAG, "Database created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Upgrading database, all values will be cleared");
        String dropWatchedTableQuery = dropTableQuery(WATCHED_TABLE_NAME);
        db.execSQL(dropWatchedTableQuery);
        onCreate(db);
    }

    private Map<String, String> createColumnMap(String tableName) {
        Map<String, String> watchedColumns = new LinkedHashMap<>();
        switch (tableName) {
            case WATCHED_TABLE_NAME:
                watchedColumns.put(COLUMN_ID, "integer primary key autoincrement");
                watchedColumns.put(COLUMN_WATCHED_ID, "integer not null");
                break;
            default:
                break;
        }
        return watchedColumns;
    }


    private String createTableQuery(String tableName, Map<String, String> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append("(");
        Iterator columnMapIterator = columns.keySet().iterator();
        do {
            String key = (String) columnMapIterator.next();
            sb.append(key);
            sb.append(" ");
            sb.append(columns.get(key));
            if (columnMapIterator.hasNext()) {
                sb.append(", ");
            }
        } while (columnMapIterator.hasNext());
        sb.append(")");
        Log.d(TAG, "created table query: " + sb.toString());
        return sb.toString();
    }

    private String dropTableQuery(String tableName) {
        return "DROP TABLE IF EXIST " + tableName;
    }

    private String getSelectAllQuery(String tableName) {
        return "SELECT * FROM " + tableName;
    }

    public List<Watched> getWatchedMovies() {
        Cursor cursor = getWritableDatabase().rawQuery(getSelectAllQuery(WATCHED_TABLE_NAME), null);
        List<Watched> watchedList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Watched watched = new Watched(cursor.getInt(cursor.getColumnIndex(COLUMN_WATCHED_ID)));
                watchedList.add(watched);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return watchedList;
    }

    private List<MovieItem> filterOutWatchedMovies(List<MovieItem> items){

    }

    public void addWatchedMovie(Watched watched){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_WATCHED_ID, watched.getId());
        getWritableDatabase().insert(WATCHED_TABLE_NAME, null, contentValues);
    }

    public void removeWatchedMovie(Watched watched)
    {   String whereClause = COLUMN_WATCHED_ID+"=?";
        String whereArgs[] = {""+watched.getId()};
        getWritableDatabase().delete(WATCHED_TABLE_NAME, whereClause, whereArgs);
    }


}
