package com.feherrera.listaconsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Felipe on 07-02-2018.
 * DataBaseHelper communicates with a SQLite database.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    //Database general info
    private static final int VERSION = 1;
    private static final String DB_NAME = "people.db";
    private static final String DB_COLUMN_ID = "_id";

    //Database structure
    public static final String DB_TABLE_PERSON = "person";
    public static final String DB_COLUMN_PERSON_NAME = "name";
    public static final String DB_COLUMN_PERSON_AGE = "age";

    public static final String DB_TABLE_POSSESSION = "possession";
    public static final String DB_COLUMN_POSSESSION_NAME = "name";
    public static final String DB_COLUMN_POSSESSION_PERSONID = "person_id";

    private static DataBaseHelper dataBaseHelper;

    /**
     * Return the singleton instance of DataBaseHelper
     * @param context Any context from the application
     * @return The DataBaseHelper
     */
    public static DataBaseHelper getDataBaseHelper(Context context){
        if (dataBaseHelper == null){
            dataBaseHelper = new DataBaseHelper(context.getApplicationContext());
        }
        return dataBaseHelper;
    }

    /**
     * Private constructor for singleton
     * @param context Application context
     */
    private DataBaseHelper(Context context) {
        super (context, DB_NAME, null, VERSION);
    }

    //region onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        loadDefaultValues(db);
    }

    /**
     * Create tables if database is new
     * @param db Application database
     */
    private void createTables(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + DB_TABLE_PERSON + "(" +
                DB_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                DB_COLUMN_PERSON_NAME + " TEXT NOT NULL, " +
                DB_COLUMN_PERSON_AGE + " INTEGER NOT NULL" +
                ");");

        db.execSQL("CREATE TABLE " + DB_TABLE_POSSESSION + "(" +
                DB_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                DB_COLUMN_POSSESSION_NAME + " TEXT NOT NULL, " +
                DB_COLUMN_POSSESSION_PERSONID + " INTEGER NOT NULL" +
                ");");
    }

    /**
     * Load default values if database is new
     * @param db Application database
     */
    private void loadDefaultValues(SQLiteDatabase db){
        addPerson(db, "Felipe", 25);
        addPerson(db, "Bebé", 20);
        addPerson(db, "Gato", 10);
        addPerson(db, "Simio", 19);
        addPerson(db, "Simio Rubio", 19);
        addPerson(db, "Sujeto", 27);
        addPerson(db, "Sashita", 21);
        addPerson(db, "Gon", 14);
        addPerson(db, "Killua", 14);

        addPossession(db, 1, "Pata");
        addPossession(db, 1, "Peta");
        addPossession(db, 1, "Pita");
        addPossession(db, 1, "Pota");
        addPossession(db, 1, "Rut");
        addPossession(db, 2, "Arduino UNO");
        addPossession(db, 2, "Arduino nano");
        addPossession(db, 2, "Arduino mini");
        addPossession(db, 2, "Arduino mega");
        addPossession(db, 2, "Arduino UNO");
        addPossession(db, 2, "Resistencias");
        addPossession(db, 2, "Buzzer");
        addPossession(db, 2, "Laptop");
        addPossession(db, 2, "Gato feo");
        addPossession(db, 2, "Arduino UNO");
        addPossession(db, 3, "Elemento gato");
        addPossession(db, 4, "Elemento simio");
        addPossession(db, 5, "Elemento simio rubio");
        addPossession(db, 6, "Elemento sujeto");
        addPossession(db, 7, "Elemento sashita");
        addPossession(db, 8, "Elemento gon");
        addPossession(db, 9, "Elemento killua");
    }
    //endregion onCreate

    //region onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                upgrade01(db);
            default:
                throw new IllegalStateException(
                        "onUpgrade() with unknown oldVersion " + oldVersion);
        }
    }

    /**
     * Upgrade from version 1 to 2
     * @param db Application database
     */
    private void upgrade01(SQLiteDatabase db){
    }
    //endregion onUpgrade

    //region queries
    /**
     * Return a cursor object with all rows in the table 'person'.
     * @return A cursor suitable for use in a SimpleCursorAdapter
     */
    public Cursor getAllPeople() {
        SQLiteDatabase db = super.getReadableDatabase();
        if (db == null) {
            return null;
        }
        Cursor cursor = db.query(DB_TABLE_PERSON
                , new String[]{DB_COLUMN_ID, DB_COLUMN_PERSON_NAME, DB_COLUMN_PERSON_AGE}
                , null
                , null
                , null
                , null
                , null
                , null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    /**
     * Return a cursor with all possessions associated to one person
     * @param personID ID of the person which possessions will be returned
     * @return Cursor suitable for use in a CursorAdapter
     */
    public Cursor getPossessions(long personID){
        SQLiteDatabase db = super.getReadableDatabase();
        if (db == null) {
            return null;
        }

        Cursor cursor = db.query(DB_TABLE_POSSESSION
                , new String[]{DB_COLUMN_ID, DB_COLUMN_POSSESSION_NAME}
                , DB_COLUMN_POSSESSION_PERSONID + " = ?"
                , new String[]{Long.toString(personID)}
                , null
                , null
                , null
                , null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }

    /**
     * Add a new person to db
     * @param name person's name
     * @return boolean indicating if query was successful
     */
    public boolean addPerson(String name, Integer age){
        SQLiteDatabase db = super.getWritableDatabase();
        if (db == null) return false;
        long response = addPerson(db, name, age);
        db.close();
        return response != -1;
    }

    private long addPerson(SQLiteDatabase db, String name, Integer age){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_PERSON_NAME, name);
        contentValues.put(DB_COLUMN_PERSON_AGE, age);
        return db.insert(DB_TABLE_PERSON, null, contentValues);
    }

    /**
     * Delete a person from db
     * @param id person's ID
     * @return boolean indicating if query was successful
     */
    public boolean deletePerson(long id){
        SQLiteDatabase db = super.getReadableDatabase();
        if (db == null) return false;
        int response = db.delete(DB_TABLE_PERSON, DB_COLUMN_ID + "=?", new String[]{ "" + id });
        db.close();
        return response == 1;
    }

    /**
     * Add a possession to a person
     * @param personID _id of person associated
     * @param name name of the possession
     * @return boolean indicating if query was successful
     */
    public boolean addPossession(long personID, String name){
        SQLiteDatabase db = super.getWritableDatabase();
        if (db == null) return false;
        long response = addPossession(db, personID, name);
        db.close();
        return response != -1;
    }

    private long addPossession(SQLiteDatabase db, long personID, String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_POSSESSION_NAME, name);
        contentValues.put(DB_COLUMN_POSSESSION_PERSONID, personID);
        return db.insert(DB_TABLE_POSSESSION, null, contentValues);
    }

    /**
     * Delete a possession
     * @param id possession ID
     * @return boolean indicating if query was successful
     */
    public boolean deletePossession(long id){
        SQLiteDatabase db = super.getReadableDatabase();
        if (db == null) return false;
        int response = db.delete(DB_TABLE_POSSESSION, DB_COLUMN_ID + "=?", new String[]{ "" + id });
        db.close();
        return response == 1;
    }
    //endregion queries
}
