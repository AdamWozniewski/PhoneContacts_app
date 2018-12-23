package com.adam.phonecontacts_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adam.phonecontacts_app.data.DatabaseDescription;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Interfejs z metodą wywołania zwrotnego  implementowaną przez główną aktywność

    public interface DetailFragmentListener {

        // Metoda wywołania w przypadku usuwania kontaktu
        void onContactDeleted();

        // Przekazanie adresu URI, który ma być edytowany
        void onEditContact(Uri uri);
    }

    // Pole uzywane do identyfikacji obiektu Loader
    private static final int CONTACT_LOADER = 0;

    // Pole obiektu implementującego zagnieżdzony interfejs - główna aktywnośc (MainACtivity)
    private DetailFragmentListener listener;

    // Adres URI wybranego kontaktu
    private Uri contactUri;

    // Pola widoków TextView
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView streetTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView zipTextView;

    // Inicjalizacja interfejsu DetailFragmentListener przy dołaczaeniu fragmentu
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (DetailFragmentListener) context;
    }

    // Usuwanie interfejsu DetailFragmentListener przy dołaczaeniu fragmentu
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        /* Uzyskanie obiektu Bundle zawierającego argumenty */
        Bundle arguments = getArguments();

        /* Odczytanie z argumentów adresu URI */
        if (arguments != null) {
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        /* Przygotowanie do wyświetlenia rozkładu fragmentu DetailFragment */
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        /* Inicjalizacja pól TextView */
        this.nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        this.phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        this.emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        this.streetTextView = (TextView) view.findViewById(R.id.streetTextView);
        this.cityTextView = (TextView) view.findViewById(R.id.cityTextView);
        this.stateTextView = (TextView) view.findViewById(R.id.stateTextView);
        this.zipTextView = (TextView) view.findViewById(R.id.zipTextView);

        /* Załadowanie kontaktu */
        getLoaderManager().initLoader(CONTACT_LOADER, null, this);

        /* Zwrócenie widoku */
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    // Wyświetla elementy menu fragmentu DetailFragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                listener.onEditContact(this.contactUri);
                return true;
            case R.id.action_delete:
                deleteContact();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Usunięcie kontaktu
    private void deleteContact() {

        /* Utworzenie obiektu AlertDialog potwierdzającego usunięcie wybranego kontaktu */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_title);
        builder.setMessage(R.string.confirm_message);
        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getContentResolver().delete(
                        DetailFragment.this.contactUri,
                        null,
                        null
                );
                listener.onContactDeleted();
            }
        });
        builder.setNegativeButton(R.string.button_cancel, null);

        /* Wyświetlenie zdefiniowanego okna dialogowego */
        builder.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /* Definicja obiektu CursorLoader */
        CursorLoader cursorLoader;

        switch (id) {
            case CONTACT_LOADER:
                cursorLoader = new CursorLoader(
                        getActivity(),
                        this.contactUri,
                        null,
                        null,
                        null,
                        null
                );
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /* Wyświetlenie danych istniejącego kontaktu */
        if (data != null && data.moveToFirst()) {

            /* Odczytanie indeksów kolumn z tabeli */
            int nameIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_NAME);
            int phoneIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_PHONE);
            int emailIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_EMAIL);
            int streetIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_STREET);
            int cityIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_CITY);
            int stateIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_STATE);
            int zipIndex = data.getColumnIndex(DatabaseDescription.Contact.COLUMN_ZIP);

            /* Wypełnienie pól EditText */
            this.nameTextView.setText(data.getString(nameIndex));
            this.phoneTextView.setText(data.getString(phoneIndex));
            this.emailTextView.setText(data.getString(emailIndex));
            this.streetTextView.setText(data.getString(streetIndex));
            this.cityTextView.setText(data.getString(cityIndex));
            this.stateTextView.setText(data.getString(stateIndex));
            this.zipTextView.setText(data.getString(zipIndex));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
