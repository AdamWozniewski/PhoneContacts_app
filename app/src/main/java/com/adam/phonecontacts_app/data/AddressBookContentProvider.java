package com.adam.phonecontacts_app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.adam.phonecontacts_app.R;

public class AddressBookContentProvider extends ContentProvider {

    // Egzemplarz klasy - umożliwia obiektowi COntentProvider uzyskanie dostepu do bazy dancych
    private AddressBookDatabaseHelper dbHelper;

    //Pomocnik obiektu ContentProvider
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Stałe obiektu UriMatcher używane w celu określenia operacji do wykonania na bazie danych
    private final static int ONE_CONTACT = 1; // Wykonanie operacji dla jednego kontaktu
    private final static int CONTACTS = 2; // Wykonanie operacji dla wszystkich kontaktow

    // Konfiguracja obiektu UriMatcher
    static {
        // Adres URI kontaktu o określonym identyfikatorze (#)
        AddressBookContentProvider.uriMatcher
                .addURI(
                        DatabaseDescription.AUTHORITY,
                        DatabaseDescription.Contact.TABLE_NAME + "/#",
                        AddressBookContentProvider.ONE_CONTACT
                );

        // Adres URI dla całej tabeli kontaktow
        AddressBookContentProvider.uriMatcher
                .addURI(
                        DatabaseDescription.AUTHORITY,
                        DatabaseDescription.Contact.TABLE_NAME,
                        AddressBookContentProvider.CONTACTS
                );
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Przyjmuje wartośc 1 jeżeli aktualizacja przebiegła pomyślnie, w przeciwnym razie 0
        int numberOfRowsDeleted;

        // Sprawdza adres URI
        switch (AddressBookContentProvider.uriMatcher.match(uri)) {
            case AddressBookContentProvider.ONE_CONTACT:
                // Odczytanie identyfikatora kontaktu, który ma zostać zaktualizowany
                String id = uri.getLastPathSegment();

                // Aktualizacja zawartości kontaktu
                numberOfRowsDeleted = dbHelper.getWritableDatabase()
                        .delete(
                                DatabaseDescription.Contact.TABLE_NAME,
                                DatabaseDescription.Contact._ID + "=" + id,
                                selectionArgs
                        );
                break;

            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        // Jezeli dokonano aktualizajcji to powiadom obiekty nasłuchujące zmian w bazie danych
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Zwróc info o aktualizacji
        return numberOfRowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Deklaracja obiektu URI
        Uri newContactUri = null;

        // sprawdzenie czy adres uri odwołuje się do tabeli contacts
        switch (AddressBookContentProvider.uriMatcher.match(uri)) {
            case AddressBookContentProvider.CONTACTS:
                // Wstawienie noweko kontaktu do tabeli
                Long rowId = this.dbHelper.getWritableDatabase().insert(DatabaseDescription.Contact.TABLE_NAME, null, values);

                // Tworzenie adresu URI dla dodanego kontaktu
                // jeżeli dodanie się powiodło
                if (rowId > 0) {
                    newContactUri = DatabaseDescription.Contact.buildContactUri(rowId);

                    // Powiadomienie obiektów nasłuchujących zmian w tabeli
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new SQLException(getContext().getString(R.string.insert_failed) + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        // Zwrócenie addresu URI
        return newContactUri;
    }

    @Override
    public boolean onCreate() {
        // Utworzenie obiektu AddressBookDatabaseHelper
        this.dbHelper = new AddressBookDatabaseHelper(getContext());

        // Operacja utworzenia obiektu ContentPrivider utworzona z sukcesem
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Obiekt SQLiteQueryBulder służacy do tworzenia zapytań sql
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(DatabaseDescription.Contact.TABLE_NAME);

        // Wybranie jednego lub wszystkich kontaktów z tabeli
        switch (AddressBookContentProvider.uriMatcher.match(uri)) {
            case AddressBookContentProvider.ONE_CONTACT:
                sqLiteQueryBuilder.appendWhere(DatabaseDescription.Contact._ID + "=" + uri.getLastPathSegment());
                break;
            case AddressBookContentProvider.CONTACTS:
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_query_uri) + uri);
        }

        // Wykonanie zapytania SQL i inicjalizacja obiektu Cursor
        Cursor cursor = sqLiteQueryBuilder.query(
                this.dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        // Konfiguracja obiektu cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Zwrócenie obiektu cursor
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // Przyjmuje wartośc 1 jeżeli aktualizacja przebiegła pomyślnie, w przeciwnym razie 0
        int numberOfRowsUpdated;

        // Sprawdza adres URI
        switch (AddressBookContentProvider.uriMatcher.match(uri)) {
            case AddressBookContentProvider.ONE_CONTACT:
                // Odczytanie identyfikatora kontaktu, który ma zostać zaktualizowany
                String id = uri.getLastPathSegment();

                // Aktualizacja zawartości kontaktu
                numberOfRowsUpdated = dbHelper.getWritableDatabase()
                        .update(
                                DatabaseDescription.Contact.TABLE_NAME,
                                values,
                                DatabaseDescription.Contact._ID + "=" + id,
                                selectionArgs
                        );
                break;

            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        // Jezeli dokonano aktualizajcji to powiadom obiekty nasłuchujące zmian w bazie danych
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Zwróc info o aktualizacji
        return numberOfRowsUpdated;
    }
}
