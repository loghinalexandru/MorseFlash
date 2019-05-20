package com.example.flashapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private static Hashtable<String , Character> morseToAscii = new Hashtable<>();
    private ListView list;
    private SimpleAdapter adapter;

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

    public List<List<String>> getAllSms() {
        List<String> smsAdress = new ArrayList<String>();
        List<String> smsBody = new ArrayList<String>();
        List<List<String>> output = new ArrayList<>();
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[] {Telephony.Sms.Inbox.ADDRESS , Telephony.Sms.Inbox.BODY},
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);

        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                smsAdress.add("FROM: " + c.getString(0));
                smsBody.add(c.getString(1));
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        c.close();

        c = cr.query(Telephony.Sms.Sent.CONTENT_URI,
                new String[] {Telephony.Sms.Inbox.ADDRESS , Telephony.Sms.Inbox.BODY},
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);

        totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                smsAdress.add("TO: " + c.getString(0));
                smsBody.add(c.getString(1));
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        output.add(smsAdress);
        output.add(smsBody);
        return output;
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

    private String decode(String phrase){
        String[] letters = phrase.split(" ");
        StringBuilder output = new StringBuilder();
        for(String encoding : letters){
            if(encoding.charAt(0) == '/'){
                output.append(' ');
            }
            else{
                output.append(morseToAscii.get(encoding));
            }
        }
        return output.toString();
    }

    private List<Map<String , String>> getMorseMessages(){
        List<List<String>> allMessages = getAllSms();
        List<Map<String , String>> morseMessages = new ArrayList<>();
        for(int i = 0; i < allMessages.get(0).size(); ++i){
            boolean validMessage = true;
            for(Character letter : allMessages.get(1).get(i).toCharArray()){
                if("./- ".indexOf(letter) == -1){
                    validMessage = false;
                    continue;
                }
            }
            if(validMessage){
                Map<String , String> listItem = new HashMap<>();
                listItem.put("title" , allMessages.get(0).get(i));
                listItem.put("body" ,allMessages.get(1).get(i) + "\n\n" + decode(allMessages.get(1).get(i)));
                morseMessages.add(listItem);
            }
        }
        return morseMessages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setNavigationListener();
        init();
        list = findViewById(R.id.message_list);
        adapter = new SimpleAdapter(this ,  getMorseMessages() , R.layout.message_layout, new String[]{"title" , "body"} , new int[]{R.id.title, R.id.body});
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String , String> item = (Map<String , String>)list.getItemAtPosition(position);
                Intent sms = new Intent(Intent.ACTION_VIEW , Uri.fromParts("sms" , item.get("title").split(" ")[1] , null));
                startActivity(sms);
            }
        });
    }
}
