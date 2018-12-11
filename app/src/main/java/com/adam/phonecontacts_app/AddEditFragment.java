package com.adam.phonecontacts_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.adam.phonecontacts_app.data.DatabaseDescription;

public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Interfejs z metodą wywołąnia zwrotnego implementowania przez główną aktywnośc
    public interface AddEditFragmentListener {
        // Wywołanie gdy kontakt jest zapisywany
        void onAddEditCompleted(Uri uri);
    }

    // Pole używane do identyfikacji obiektu loader
    private static final int CONTACT_LOADER = 0;

    // Pole obiektu implementującego zagnieżdzony interfejs - główna aktywność (MainActivity)
    private AddEditFragmentListener addEditFragmentListener;

    // Adres URI wybranego kontaktu
    private Uri contactUri;

    // Dodawanie (true) / edycja (false) kontaktu
    private boolean addingNewContact = true;

    // Pola graficznego interfejsu użytkownika
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout streetTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipTextInputLayout;
    private FloatingActionButton saveContactFAB;

    // Uzywany wraz z obiektami Snackbar
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.addEditFragmentListener = (AddEditFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.addEditFragmentListener = null;
    }

    // Utworzenie widoku obiektu Fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Przygotowanie elementów graficznego interfejsu użytkownika
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        this.nameTextInputLayout = (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);
        this.nameTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AddEditFragment.this.updateSaveButtonFAB();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.phoneTextInputLayout = (TextInputLayout) view.findViewById(R.id.phoneTextInputLayout);
        this.emailTextInputLayout = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        this.streetTextInputLayout = (TextInputLayout) view.findViewById(R.id.streetTextInputLayout);
        this.cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        this.stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        this.zipTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipTextInputLayout);

        // Przygotowanie przycisku
        this.saveContactFAB = (FloatingActionButton) view.findViewById(R.id.saveFloatingActionButton);
        this.saveContactFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ukrycie klawiatury ekranowej
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromInputMethod(getView().getWindowToken(), 0));

                // Zapisanie kontaktu w bazie danych
                AddEditFragment.this.saveContact();
            }
        });

        // Kod używany do wyświetlania obiektów SnackBar
        this.coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        Bundle args = getArguments();

        if (args != null) {
            this.addingNewContact = false;
            this.contactUri = args.getParcelable(MainActivity.CONTACT_URI);
        }

        // Utowrzenie obiektu Loader
        if (this.contactUri != null) {
            getLoaderManager().initLoader(CONTACT_LOADER, null, this);
        }

        // Zwrócenie widoku
        return view;

    }

    // Wyświetla lub chowa przycisk zapisu kontaktu (FAB)
    private void updateSaveButtonFAB() {
        // Pobierz tekst z pola EditText
        String input = this.nameTextInputLayout.getEditText().getText().toString();

        // Wyświtl lub schowaj przycisk zapisu kontaktu
        if (input.trim().length() != 0) {
            this.saveContactFAB.show();
        } else {
            this.saveContactFAB.hide();
        }
    }

    // Zapisuje dane kontaktu w bazie danych
    private void saveContact() {
        // Utworzenie i obsadzeineie obiektu content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseDescription.Contact.COLUMN_NAME, this.nameTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_PHONE, this.phoneTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_EMAIL, this.emailTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_STREET, this.streetTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_CITY, this.cityTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_STATE, this.stateTextInputLayout.getEditText().getText().toString());
        contentValues.put(DatabaseDescription.Contact.COLUMN_ZIP, this.zipTextInputLayout.getEditText().getText().toString());

        // Zapisanie kontaktu w bazie danych korzystając z obiektu AddressBookContentProvider i jego metody insert
        if (this.addingNewContact) {
            // Dodanie kontaktu
            Uri newContactUri = getActivity().getContentResolver().insert(DatabaseDescription.Contact.CONTENT_URI, contentValues);

            // Wyświetlenie obiektoów Snackbar
            if (newContactUri != null) {
                Snackbar.make(this.coordinatorLayout, R.string.contact_added, Snackbar.LENGTH_LONG).show();
                this.addEditFragmentListener.onAddEditCompleted(newContactUri);
            } else {
                Snackbar.make(this.coordinatorLayout, R.string.contact_not_added, Snackbar.LENGTH_LONG).show();
            }
        }

        // Aktualizacja kontaktu w bazie danych korzystając z obiektu AddressBookContentProvider i jego metody update
        else {
            // Aktualizacja kontaktu
            int updateRows = getActivity().getContentResolver().update(this.contactUri, contentValues, null, null);

            // Wyświetlenie obiektów snackbar
            Uri ContactUri = getActivity().getContentResolver().insert(DatabaseDescription.Contact.CONTENT_URI, contentValues);
            if (updateRows > 0) {
                Snackbar.make(this.coordinatorLayout, R.string.contact_updated, Snackbar.LENGTH_LONG).show();
                this.addEditFragmentListener.onAddEditCompleted(contactUri);
            } else {
                Snackbar.make(this.coordinatorLayout, R.string.contact_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case CONTACT_LOADER:
                return new CursorLoader(getActivity(), contactUri, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Sprawdzenie czy wybrany kontakt istnieje w bazie danych
        if (cursor != null && cursor.moveToFirst()) {
            // Odczytanie indeksów kolumn z tabeli
            int nameIndex = cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_NAME);
            int phoneIndex = cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_PHONE);
            int emailIndex = cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_EMAIL);
            int streetIndex = cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_STREET);
            int cityIndex = cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_CITY);
            int stateIndex = cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_STATE);
            int zipIndex = cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_ZIP);

            // Wypełniennie pól editText
            this.nameTextInputLayout.getEditText().setText(cursor.getString(nameIndex));
            this.phoneTextInputLayout.getEditText().setText(cursor.getString(phoneIndex));
            this.emailTextInputLayout.getEditText().setText(cursor.getString(emailIndex));
            this.streetTextInputLayout.getEditText().setText(cursor.getString(streetIndex));
            this.cityTextInputLayout.getEditText().setText(cursor.getString(cityIndex));
            this.stateTextInputLayout.getEditText().setText(cursor.getString(stateIndex));
            this.zipTextInputLayout.getEditText().setText(cursor.getString(zipIndex));

            // Wywoąlnie metody wyświetlającej przycisk zapisu kontaktu
            this.updateSaveButtonFAB();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
