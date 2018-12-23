package com.adam.phonecontacts_app;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adam.phonecontacts_app.data.DatabaseDescription;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    // Definicja interfejsu implemnetowanego przez klasę ContactsFragment
    public interface ContactsClickListener {
        void onClick(Uri contactUri);
    }

    // Klasa używana do implementacji wzorca ViewHolder w kontekscie widoku RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Widok textView wyświetlającay nazwę kontaktu
        public final TextView textView;

        // Identyfikator rzędu kontaktów
        private long rowId;

        // konstruktor klasy ViewHolder
        public ViewHolder(View view) {
            super(view);

            // Inicjalizacja widoku TextView
            this.textView = (TextView) view.findViewById(android.R.id.text1);

            // Podłącz do obiektu View obiekt nasłuchujacy zdarzen
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactsAdapter.this.clickListener.onClick(DatabaseDescription.Contact.buildContactUri(ContactsAdapter.ViewHolder.this.rowId));
                }
            });
        }

        // Określenie identyfikatora rzędu
        public void setRowId(long rowId) {
            this.rowId = rowId;
        }
    }

    // Zmienne egzemplarzowe
    private Cursor cursor = null;
    private final ContactsClickListener clickListener;

    public ContactsAdapter(ContactsClickListener contactsClickListener) {
        this.clickListener = contactsClickListener;
    }

    // Uzyskanie obiektu ViewHolder bieżącego elemetu Kontaktu
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Przygotowanie do wyświetlenie predefiniowanego widoku
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(
                        android.R.layout.simple_list_item_1,
                        viewGroup,
                        false
                );
        return new ViewHolder(view);
    }

    // Określenie tekstu elementu listy
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        /* Przeniesienie wybranego kontaktu w odpowiednie miejsce widoku RecyclerView */
        this.cursor.moveToPosition(position);

        /* Określenie identyfikatora rowID elementu ViewHolder */
        holder.setRowId(this.cursor.getLong(this.cursor.getColumnIndex(DatabaseDescription.Contact._ID)));

        /* Ustawienie tekstu widoku TextView elementu widoku RecyclerView */
        holder.textView.setText(this.cursor.getString(this.cursor.getColumnIndex(DatabaseDescription.Contact.COLUMN_NAME)));
    }

    // Zwraca liczbę elementów wiązynch przez adapter
    @Override
    public int getItemCount() {
        return (this.cursor != null) ? this.cursor.getCount() : 0;
    }

    // Zamienia bierzący obiekt Cursor na nowy
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
