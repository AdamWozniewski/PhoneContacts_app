package com.adam.phonecontacts_app.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {

    // Nazwa obiektu ContentProvider - zwukle jest to nazwa pakietu
    public static final String AUTHORITY = "com.adam.phonecontacts_app.data";

    // Adres URI uzywany do nawiązywania interackji z obiektem ContentProider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + DatabaseDescription.AUTHORITY);

    private DatabaseDescription() {}

    public static final class Contact implements BaseColumns {
        // Nazwa tabeli
        public static final String TABLE_NAME = "contacts";

        // Adres tabeli
        public static final Uri CONTENT_URI = DatabaseDescription
                .BASE_CONTENT_URI
                .buildUpon()
                .appendPath(Contact.TABLE_NAME)
                .build();

        // Nazwy kolumn w tabeli
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIP = "zip";

        // Metoda do tworzenia adresu dla nowego kontaktu
        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(Contact.CONTENT_URI, id);
        }
    }
}
