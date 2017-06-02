package com.example.esmaeel.projectapp;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ConnectionInfo";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_INFO_TABLE = "CREATE TABLE info ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "port TEXT, "+
                "ip TEXT )";

        // create books table
        db.execSQL(CREATE_INFO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS info");

        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    // (create "add", read "get", update, delete) book + get all books + delete all books


    // Books table name
    private static final String TABLE_INFO = "info";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PORT = "port";
    private static final String KEY_IP = "ip";

    private static final String[] COLUMNS = {KEY_ID, KEY_PORT, KEY_IP};

    public void addInfo(ConnectionInfo connectionInfo){
        Log.d("addInfo", connectionInfo.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PORT, connectionInfo.getPort()); // get title
        values.put(KEY_IP, connectionInfo.getIp()); // get author

        // 3. insert
        db.insert(TABLE_INFO, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public ConnectionInfo getInfo(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_INFO, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // eThread. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build connectionInfo object
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setId(Integer.parseInt(cursor.getString(0)));
        connectionInfo.setPort(cursor.getString(1));
        connectionInfo.setIp(cursor.getString(2));

        Log.d("getInfo("+id+")", connectionInfo.toString());

        // 5. return connectionInfo
        return connectionInfo;
    }

    // Get All info
    public List<ConnectionInfo> getAllInfo() {
        List<ConnectionInfo> connectionInfos = new LinkedList<ConnectionInfo>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_INFO;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build connectionInfo and add it to list
        ConnectionInfo connectionInfo = null;
        if (cursor.moveToFirst()) {
            do {
                connectionInfo = new ConnectionInfo();
                connectionInfo.setId(Integer.parseInt(cursor.getString(0)));
                connectionInfo.setPort(cursor.getString(1));
                connectionInfo.setIp(cursor.getString(2));

                // Add connectionInfo to connectionInfos
                connectionInfos.add(connectionInfo);
            } while (cursor.moveToNext());
        }

        Log.d("getAllInfo()", connectionInfos.toString());

        // return connectionInfos
        return connectionInfos;
    }

    // Updating single connectionInfo
    public int updateInfo(ConnectionInfo connectionInfo) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("port", connectionInfo.getPort()); // get title
        values.put("ip", connectionInfo.getIp()); // get author

        // 3. updating row
        int i = db.update(TABLE_INFO, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(connectionInfo.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single connectionInfo
    public void deleteInfo(ConnectionInfo connectionInfo) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_INFO,
                KEY_ID+" = ?",
                new String[] { String.valueOf(connectionInfo.getId()) });

        // 3. close
        db.close();

        Log.d("deleteInfo", connectionInfo.toString());

    }
}