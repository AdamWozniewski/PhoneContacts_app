package com.adam.phonecontacts_app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AddressBookDatabaseHelper extends SQLiteOpenHelper {
    // Nazwa bazy danych
    private static final String DATABASE_NAME = "AddressBook.db";

    // Wersja bazy danych
    private static final int DATABASE_VER = 1;

    // Konstruktor tworzący baze danych
    public AddressBookDatabaseHelper(Context context) {
        super(context, AddressBookDatabaseHelper.DATABASE_NAME, null, AddressBookDatabaseHelper.DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Zapytanie SQL tworzące tabelę w bazie danych

        final String CREATE_CONTACTS_TABLE =
                "CREATE TABLE" + DatabaseDescription.Contact.TABLE_NAME +
                        "(" + DatabaseDescription.Contact._ID + "integer primary key, " +
                        DatabaseDescription.Contact.COLUMN_NAME + "TEXT, " +
                        DatabaseDescription.Contact.COLUMN_PHONE + "TEXT, " +
                        DatabaseDescription.Contact.COLUMN_EMAIL + "TEXT, " +
                        DatabaseDescription.Contact.COLUMN_STREET + "TEXT, " +
                        DatabaseDescription.Contact.COLUMN_CITY + "TEXT, " +
                        DatabaseDescription.Contact.COLUMN_STATE + "TEXT, " +
                        DatabaseDescription.Contact.COLUMN_ZIP + "TEXT)";

        // Egzekucja zapytania SQL
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
