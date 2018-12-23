package com.adam.phonecontacts_app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements
        ContactsFragment.ContactsFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener {

    // Klucz przechowujący adres URI kontaktu w obiekcie przekazanych do fragmentu
    public static final String CONTACT_URI = "contact_uri";

    // Fragment wyświetlający listę kontaktów z bazy
    private ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Jeżeli rozkład głównej aktywności zawiera fragment fragmentContainer (content_main.xml) - rozkłąd dla telefonu
        if (savedInstanceState == null && findViewById(R.id.fragmentContainer) != null) {
            this.contactsFragment = new ContactsFragment();

            // Dodanie fragmrentu do rozkłądu FrameLayout
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, this.contactsFragment);

            // Wyświetl obiekt ContactFragment
            transaction.commit();
        } else {
            // Uzyskanie odwolania do juz istniejącego fragmentu ContactFragment
            this.contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.contactsFragment);
        }
    }

    // Utworzenie i wyświetlenie fragmentu DetailFragment
    private void displayDetailFragment(Uri uri, int viewId) {
        // Utworzenie fragmentu DetailFragment
        DetailFragment detailFragment = new DetailFragment();

        // Przekazanie adresu URI jako argumentu fragmentu
        Bundle args = new Bundle();
        args.putParcelable(CONTACT_URI, uri);
        detailFragment.setArguments(args);

        // Dodanie fragmentu do rozkłądu
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewId, detailFragment);
        transaction.addToBackStack(null);

        // Wyśiwtl obiekt DetailFragment
        transaction.commit();
    }

    // Utworzenie i wyświetlenie obiektu AddEditFragment
    private void displayAddEditFragment(Uri uri, int viewId) {
        // Utwopreznie fragmentu AddEditFragment
        AddEditFragment addEditFragment = new AddEditFragment();

        // Jezlei jest wczesniej edytowany kontakt, to przekazywany jest kontakt uri
        if (uri != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.CONTACT_URI, uri);
            addEditFragment.setArguments(bundle);
        }

        // Dodanie fragmentu do rozkładu
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewId, addEditFragment);
        transaction.addToBackStack(null);

        // Wyświetl obiekt AddEditFragment
        transaction.commit();
    }

    @Override
    public void onAddEditCompleted(Uri uri) {
        // Usunięcie górnego elementu stosu aplikacji
        getSupportFragmentManager().popBackStack();

        // Odświeżenie listy kontaktów
        this.contactsFragment.updateContactList();

        // Obsługa aktualizacji na tablecie
        if (findViewById(R.id.fragmentContainer) == null) {
            // Usunięcie górnego elementu stosu aplikacji
            getSupportFragmentManager().popBackStack();

            // WYświetlenie kontaktu który został dodany lub zaktualizowany
            this.displayDetailFragment(uri, R.id.rightPaneContainer);
        }
    }

    @Override
    public void onContactSelected(Uri contactUri) {
        // Wyświetlenie fragmentu detail fragment dla kontaktu na telefonie
        if (findViewById(R.id.fragmentContainer) != null) {
            this.displayDetailFragment(contactUri, R.id.fragmentContainer);
        }

        // na tablecie
        else {
            getSupportFragmentManager().popBackStack();
            this.displayDetailFragment(contactUri, R.id.rightPaneContainer);
        }
    }

    @Override
    public void onAddContact() {
        // Wyświetlenie fragmentu AddEditFragment dla wybranego kontaktu na telefonie
        if (findViewById(R.id.fragmentContainer) != null) {
            this.displayAddEditFragment(null, R.id.fragmentContainer);
        }
        // Na tablecie
        else {
            this.displayAddEditFragment(null, R.id.rightPaneContainer);
        }

    }

    @Override
    public void onContactDeleted() {
        // Usunięcie elementu znajdującego się na szczycie stosu aplikacji
        getSupportFragmentManager().popBackStack();

        // Odświeżenie listy kontaków fragmentu ContactsFragment
        this.contactsFragment.updateContactList();
    }

    @Override
    public void onEditContact(Uri uri) {
        // Wyświetlenie fragmentu AddEditFragment dla wybranego kontaktu na telefonie
        if (findViewById(R.id.fragmentContainer) != null) {
            this.displayAddEditFragment(uri, R.id.fragmentContainer);
        }
        // Na tablecie
        else {
            this.displayAddEditFragment(uri, R.id.rightPaneContainer);
        }
    }
}
//
//public class MainActivity extends AppCompatActivity implements ContactsFragment.ContactsFragmentListener,
//        DetailFragment.DetailFragmentListener, AddEditFragment.AddEditFragmentListener {
//
//    /* Klucz przechowujący adres URI kontaktu w obiekcie przekazanym do fragmentu */
//    public static final String CONTACT_URI = "contact_uri";
//
//    /* Fragment wyświetlający listę kontaktów z bazy */
//    private ContactsFragment contactsFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        /* Jeżeli rozkład głównej aktywności zawiera fragment fragmentContainer (content_main.xml),
//         * to oznacza używanie rozkładu przeznaczonego dla telefonu. W takiej sytuacji tworzymy
//         * i wyświetlamy fragment ContactsFragment. */
//        if (savedInstanceState == null && findViewById(R.id.fragmentContainer) != null) {
//
//            /* Utworzenie fragmentu ContactsFragment */
//            contactsFragment = new ContactsFragment();
//
//            /* Dodanie fragmentu do rozkładu FrameLayout */
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.add(R.id.fragmentContainer, contactsFragment);
//
//            /* Wyświetl obiekt ContactsFragment */
//            transaction.commit();
//        } else {
//
//            /* Uzyskanie odwołania do już istniejącego fragmentu ContactsFragment */
//            contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.contactsFragment);
//        }
//    }
//
//    /* Utworzenie i wyświetlenie fragmentu DetailFragment */
//    private void displayDetailFragment(Uri contactUri, int viewID) {
//
//        /* Utworzenie fragmentu DetailFragment */
//        DetailFragment detailFragment = new DetailFragment();
//
//        /* Przekazanie adresu URI jako argumentu fragmentu */
//        Bundle arguments = new Bundle();
//        arguments.putParcelable(CONTACT_URI, contactUri);
//        detailFragment.setArguments(arguments);
//
//        /* Dodanie fragmentu do rozkładu */
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(viewID, detailFragment);
//        transaction.addToBackStack(null);
//
//        /* Wyświetl obiekt DetailFragment */
//        transaction.commit();
//    }
//
//    /* Utworzenie i wyświetlenie fragmentu AddEditFragment */
//    private void displayAddEditFragment(Uri contactUri, int viewID) {
//
//        /* Utworzenie fragmentu AddEditFragment */
//        AddEditFragment addEditFragment = new AddEditFragment();
//
//        /* Jeżeli edytowany jest wcześniej zapisany kontakt, to jako argument przekazywany jest contactUri */
//        if (contactUri != null) {
//            Bundle arguments = new Bundle();
//            arguments.putParcelable(CONTACT_URI, contactUri);
//            addEditFragment.setArguments(arguments);
//        }
//
//        /* Dodanie fragmentu do rozkładu */
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(viewID, addEditFragment);
//        transaction.addToBackStack(null);
//
//        /* Wyświetl obiekt AddEditFragment */
//        transaction.commit();
//    }
//
//    @Override
//    public void onContactSelected(Uri contactUri) {
//
//        /* Wyświetlenie fragmentu DetailFragment dla wybranego kontaktu na telefonie... */
//        if (findViewById(R.id.fragmentContainer) != null) {
//            displayDetailFragment(contactUri, R.id.fragmentContainer);
//        }
//
//        /* ...na tablecie. */
//        else {
//            getSupportFragmentManager().popBackStack();
//            displayDetailFragment(contactUri, R.id.rightPaneContainer);
//        }
//    }
//
//    @Override
//    public void onAddContact() {
//
//        /* Wyświetlenie fragmentu AddEditFragment dla wybranego kontaktu na telefonie... */
//        if (findViewById(R.id.fragmentContainer) != null) {
//            displayAddEditFragment(null, R.id.fragmentContainer);
//        }
//
//        /* ...na tablecie. */
//        else {
//            displayAddEditFragment(null, R.id.rightPaneContainer);
//        }
//    }
//
//    @Override
//    public void onContactDeleted() {
//
//        /* Usunięcie elementu znajdującego się na szczycie stosu aplikacji */
//        getSupportFragmentManager().popBackStack();
//
//        /* Odświeżenie listy kontaktów fragmentu ContactsFragment */
//        contactsFragment.updateContactList();
//    }
//
//    @Override
//    public void onEditContact(Uri contactUri) {
//
//        /* Wyświetlenie fragmentu AddEditFragment dla wybranego kontaktu na telefonie... */
//        if (findViewById(R.id.fragmentContainer) != null) {
//            displayAddEditFragment(contactUri, R.id.fragmentContainer);
//        }
//
//        /* ...na tablecie. */
//        else {
//            displayAddEditFragment(contactUri, R.id.rightPaneContainer);
//        }
//    }
//
//    @Override
//    public void onAddEditCompleted(Uri contactUri) {
//
//        /* Usunięcie górnego elementu stosu aplikacji */
//        getSupportFragmentManager().popBackStack();
//
//        /* Odświeżenie listy kontaktów */
//        contactsFragment.updateContactList();
//
//        /* Obsługa aktualizacji na tablecie */
//        if (findViewById(R.id.fragmentContainer) == null) {
//
//            /* Usunięcie górnego elementu stosu aplikacji */
//            getSupportFragmentManager().popBackStack();
//
//            /* Wyświetlenie kontaktu, który został dodany lub zaktualizowany */
//            displayDetailFragment(contactUri, R.id.rightPaneContainer);
//        }
//    }
//}