package com.adam.phonecontacts_app;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adam.phonecontacts_app.data.DatabaseDescription;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Metoda wywołania zwrotnego implemntowane przez klasę MainACtivity
    public interface ContactsFragmentListener {
        // Wywołąnie w wyniku wybrania kontaktu
        void onContactSelected(Uri contactUri);

        // Wywołanie w wyniku dotknięcia przycisku (+)
        void onAddContact();
    }

    // Identyfikator obiektu Loader
    private static final int CONTACTS_LOADER = 0;

    // Obiekt informujący  aktywnośc MainActivity o wybraniu kontaktu
    private ContactsFragmentListener listener;

    // Adapter obiektu RecyclerViev
    private ContactsAdapter contactsAdapter;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Przygotowanie do wyświetlenia graficznego interfejsu użytkownika
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Uzyskanie odwołąnia do widoku RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Konfiguracja widoku RecyclerView - widok powinien wyświetlać elementy w formie pionowej listy
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        this.contactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactsClickListener() {
            @Override
            public void onClick(Uri contactUri) {
                ContactsFragment.this.listener.onContactSelected(contactUri);
            }
        });

        // Ustawienie adaptera widoku RecyclerView
        recyclerView.setAdapter(this.contactsAdapter);

        // Dołączenie spersonalizowanego obiektu ItemDivider
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // Rozmiar widoku RecyclerView nie ulega zmianie
        recyclerView.setHasFixedSize(true);

        // Inicjalizacja i konfiguracja przycisku dodawaania kontaktu (+)
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsFragment.this.listener.onAddContact();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (ContactsFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getLoaderManager().initLoader(
                ContactsFragment.CONTACTS_LOADER,
                null,
                this
        );
    }

    public void updateContactList() {
        this.contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Utworzenie obiektu CursorLoader

        switch (id) {
            case CONTACTS_LOADER:
                return new CursorLoader(
                        getActivity(),
                        DatabaseDescription.Contact.CONTENT_URI, // Adres URI tabeli
                        null, // zwraca wszystkie kolumny
                        null, // zwraca wsyztskie wiersze
                        null, // Brak argumentów selekcji
                        DatabaseDescription.Contact.COLUMN_NAME + "COLLATE NOCASE ASC" // Kolejnosc sortowania
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.contactsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.contactsAdapter.swapCursor(null);
    }
}
