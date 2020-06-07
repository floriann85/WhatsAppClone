package com.example.fn.WhatsAppClone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppChatActivity extends AppCompatActivity implements View.OnClickListener {

    // globalen ListView anlegen
    private ListView chatListView;
    // globale ArrayList anlegen
    private ArrayList<String> chatsList;
    // globalen ArrayAdapter anlegen
    private ArrayAdapter adapter;
    // globale StringVar anlegen
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_chat);

        // den Title für die Activity setzen
        setTitle("WhatsApp - Chats");

        // anlegen und initialisieren
        selectedUser = getIntent().getStringExtra("selectedUser");
        FancyToast.makeText(this, "Chat with "
                        + selectedUser + " Now!!!", Toast.LENGTH_SHORT,
                FancyToast.INFO, true).show();

        chatListView = findViewById(R.id.chatListView);
        chatsList = new ArrayList();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chatsList);
        chatListView.setAdapter(adapter);

        // auf den Button referenzieren
        // und OnClickListener erstellen mit Verweis implents der Klasse
        findViewById(R.id.btnSend).setOnClickListener(this);

        try {
            ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

            firstUserChatQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("waTargetRecipient", selectedUser);

            secondUserChatQuery.whereEqualTo("waSender", selectedUser);
            secondUserChatQuery.whereEqualTo("waTargetRecipient", ParseUser.getCurrentUser().getUsername());


            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            // neue Nachrichten anordnen bzw. anhängen
            myQuery.orderByAscending("createdAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {

                        for (ParseObject chatObject : objects) {

                            String waMessage = chatObject.get("waMessage") + "";

                            if (chatObject.get("waSender").equals(ParseUser.getCurrentUser().getUsername())) {

                                waMessage = ParseUser.getCurrentUser().getUsername() + ": " + waMessage;
                            }
                            if (chatObject.get("waSender").equals(selectedUser)) {

                                waMessage = selectedUser + ": " + waMessage;
                            }

                            chatsList.add(waMessage);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        // lokalen EditText anlegen und initialisieren
        final EditText edtMessage = findViewById(R.id.edtSend);

        // bestehenden User von der DB auf dem Server abfragen für Chat
        // ParseObjekt erstellen
        ParseObject chat = new ParseObject("Chat");
        // dem Chat-Verlauf hinzufügen
        chat.put("waSender", ParseUser.getCurrentUser().getUsername());
        chat.put("waTargetRecipient", selectedUser);
        chat.put("waMessage", edtMessage.getText().toString());
        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    FancyToast.makeText(WhatsAppChatActivity.this,
                            "Message from " +
                                    ParseUser.getCurrentUser().getUsername() +
                                    " sent to " + selectedUser, Toast.LENGTH_SHORT,
                            FancyToast.SUCCESS, true).show();
                    chatsList.add(ParseUser.getCurrentUser().getUsername() +
                            ": " + edtMessage.getText().toString());
                    // neuen Objekte werden der ListView hinzugefügt (add)
                    adapter.notifyDataSetChanged();
                    // nach dem Senden der Nachricht das Eingabefeld leeren
                    edtMessage.setText("");
                }
            }
        });
    }
}
