package com.example.fn.WhatsAppClone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppUsersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // globale ArrayList anlegen
    private ArrayList<String> waUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_users);

        // den Title für die Activity setzen
        setTitle("WhatsApp - Users");

		/*
        // Begrüßung angemeldeter User
        FancyToast.makeText(this, "Welcome " +
                        ParseUser.getCurrentUser().getUsername(),
                Toast.LENGTH_LONG, FancyToast.INFO, true).show();
         */

        // anlegen und initialisieren
        final ListView listView = findViewById(R.id.listView);
        waUsers = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, waUsers);
        final SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.swipeContainer);

        // OnKeyListener erstellen für die Funktion
        listView.setOnItemClickListener(this);


        try {
            // bestehenden User von der DB auf dem Server abfragen
            // ParseObjekt erstellen
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                // Methode anlegen
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {

                        for (ParseUser user : objects) {
                            // User hinzufügen
                            waUsers.add(user.getUsername());
                        }
                        // die ListView zu dem Adapter setzen
                        listView.setAdapter(adapter);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        // OnRefreshListener erstellen für die Funktion RefreshLayout/ Activity
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        try {
                            // bestehenden User von der DB auf dem Server abfragen
                            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                            parseQuery.whereNotContainedIn("username", waUsers);
                            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> objects, ParseException e) {
                                    // Abfrage ob Objekte der Liste hinzugefügt wurden
                                    if (objects.size() > 0) {
                                        // Abfrage ob kein Error
                                        if (e == null) {
                                            for (ParseUser user : objects) {
                                                // User hinzufügen
                                                waUsers.add(user.getUsername());
                                            }
                                            // neuen Objekte werden der ArrayList hinzugefügt (add)
                                            adapter.notifyDataSetChanged();
                                            // Abfrage ob das RefreshLayout/ Activity bereits aktualisiert wurde
                                            // wenn ja, wird eine Aktualisierung erst wieder bei Datenänderung vorgenommen
                                            if (mySwipeRefreshLayout.isRefreshing()) {
                                                mySwipeRefreshLayout.setRefreshing(false);
                                            }
                                        }

                                    } else {
                                        // Abfrage ob das RefreshLayout/ Activity bereits aktualisiert wurde
                                        // wenn ja, wird eine Aktualisierung erst wieder bei Datenänderung vorgenommen
                                        if (mySwipeRefreshLayout.isRefreshing()) {
                                            mySwipeRefreshLayout.setRefreshing(false);
                                        }
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
        );
    }

    @Override
    // Methode anlegen für Funktion Menu
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switch-Case für Auswahl/ Funktion Button
        switch (item.getItemId()) {
            // User ausloggen
            case R.id.logout_item:
                // der angemeldete User wird ausgeloggt
                FancyToast.makeText(WhatsAppUsersActivity.this,
                        ParseUser.getCurrentUser().getUsername() + " is logged out!",
                        Toast.LENGTH_SHORT, FancyToast.DEFAULT, true).show();

                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Intent anlegen mit Zuordnung der Klasse für Activity wechseln
                        // überprüfen ob kein Fehler aufgetreten ist
                        if (e == null) {
                            Intent intent = new Intent(WhatsAppUsersActivity.this, MainActivity.class);
                            // die Activity starten
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // lokalen Intent anlegen mit Zuordnung der Klasse für Activity wechseln
        Intent intent = new Intent(WhatsAppUsersActivity.this, WhatsAppChatActivity.class);
        intent.putExtra("selectedUser", waUsers.get(position));
        // die Activity starten
        startActivity(intent);
    }
}

