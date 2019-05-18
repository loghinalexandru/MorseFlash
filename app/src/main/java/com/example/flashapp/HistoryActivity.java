package com.example.flashapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private static Hashtable<String , Character> morseToAscii = new Hashtable<>();

    private static void fillMorseToAscii(){
        morseToAscii.put(".-" , 'A');
        morseToAscii.put("-..." , 'B');
        morseToAscii.put("-.-." , 'C');
        morseToAscii.put( "-..", 'D');
        morseToAscii.put( "." , 'E');
        morseToAscii.put( "..-." , 'F');
        morseToAscii.put( "--." , 'G');
        morseToAscii.put( "...." , 'H');
        morseToAscii.put( ".." , 'I');
        morseToAscii.put( ".---" , 'J');
        morseToAscii.put("-.-" , 'K');
        morseToAscii.put( ".-.." ,'L');
        morseToAscii.put( "--" , 'M');
        morseToAscii.put( "-." , 'N');
        morseToAscii.put( "---" , 'O');
        morseToAscii.put( ".--." , 'P');
        morseToAscii.put( "--.-" , 'Q');
        morseToAscii.put( ".-." , 'R');
        morseToAscii.put( "..." , 'S');
        morseToAscii.put( "-" , 'T');
        morseToAscii.put( "..-" , 'U');
        morseToAscii.put( "...-" ,'V');
        morseToAscii.put( ".--" , 'W');
        morseToAscii.put( "-..-" , 'X');
        morseToAscii.put( "-.--" , 'Y');
        morseToAscii.put( "--.." , 'Z');
        morseToAscii.put( "-----" , '0');
        morseToAscii.put( ".----" , '1');
        morseToAscii.put( "..---" , '2');
        morseToAscii.put( "...--" , '3');
        morseToAscii.put( "....-" , '4');
        morseToAscii.put( "....." , '5');
        morseToAscii.put( "-...." , '6');
        morseToAscii.put( "--..." , '7');
        morseToAscii.put( "---.." , '8');
        morseToAscii.put( "----." , '9');
        morseToAscii.put( ".-.-.-" , '.');
        morseToAscii.put("--..--" , ',');
        morseToAscii.put( "..--.." , '?');
    }

    private void init(){
        fillMorseToAscii();
    }

    public List<String> getAllSmsFromProvider() {
        List<String> lstSms = new ArrayList<String>();
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { Telephony.Sms.Inbox.BODY }, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(c.getString(0));
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        c.close();

        return lstSms;
    }

    private void setNavigationListener(){
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(1).setChecked(true);
        navView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                Intent main = new Intent(HistoryActivity.this , MainActivity.class);
                                startActivity(main);
                                finish();
                                overridePendingTransition(0, 0);

                                break;
                            case R.id.navigation_history:
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setNavigationListener();
        init();
    }
}
