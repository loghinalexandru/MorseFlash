package com.example.flashapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    private TextView asciiEncoding;
    private TextView morseEncoding;
    private Button translateButton;
    private Button sendSms;
    private TextInputEditText textInput;
    private TextInputLayout textLayout;
    private String currentPhrase;
    private static int currentIndex = 0;
    private static Thread currentThread;
    public static Handler callbackHandler;

    public static Camera camera;
    private static SurfaceTexture surface = new SurfaceTexture(1);
    private static Hashtable<Character, String>  asciiToMorse = new Hashtable<>();

    private static void init(){
        fillAsciiToMorse();
    }

    private static void fillAsciiToMorse(){
        asciiToMorse.put('A', ".-");
        asciiToMorse.put('B', "-...");
        asciiToMorse.put('C', "-.-.");
        asciiToMorse.put('D', "-..");
        asciiToMorse.put('E', ".");
        asciiToMorse.put('F', "..-.");
        asciiToMorse.put('G', "--.");
        asciiToMorse.put('H', "....");
        asciiToMorse.put('I', "..");
        asciiToMorse.put('J', ".---");
        asciiToMorse.put('K', "-.-");
        asciiToMorse.put('L', ".-..");
        asciiToMorse.put('M', "--");
        asciiToMorse.put('N', "-.");
        asciiToMorse.put('O', "---");
        asciiToMorse.put('P', ".--.");
        asciiToMorse.put('Q', "--.-");
        asciiToMorse.put('R', ".-.");
        asciiToMorse.put('S', "...");
        asciiToMorse.put('T', "-");
        asciiToMorse.put('U', "..-");
        asciiToMorse.put('V', "...-");
        asciiToMorse.put('W', ".--");
        asciiToMorse.put('X', "-..-");
        asciiToMorse.put('Y', "-.--");
        asciiToMorse.put('Z', "--..");
        asciiToMorse.put('0', "-----");
        asciiToMorse.put('1', ".----");
        asciiToMorse.put('2', "..---");
        asciiToMorse.put('3', "...--");
        asciiToMorse.put('4', "....-");
        asciiToMorse.put('5', ".....");
        asciiToMorse.put('6', "-....");
        asciiToMorse.put('7', "--...");
        asciiToMorse.put('8', "---..");
        asciiToMorse.put('9', "----.");
        asciiToMorse.put('.', ".-.-.-");
        asciiToMorse.put(',', "--..--");
        asciiToMorse.put('?', "..--..");
    }

    private void resetUI(){
        this.morseEncoding.setText("");
        this.asciiEncoding.setText("");
        this.currentPhrase = "";
        this.currentIndex = 0;
    }

    private boolean isValidPhrase(String phrase){
        for(int i = 0; i < phrase.length(); ++i){
            if(phrase.charAt(i) != ' ' && asciiToMorse.get(phrase.charAt(i)) == null)
                return false;
        }
        return true;
    }

    private void createHandler(){
        callbackHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                if(currentIndex < currentPhrase.length())
                    translatePhraseToMorse();
                else{
                    resetUI();
                }
            }
        };
    }

    private String asciiToMorse(String phrase){
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < phrase.length(); ++i){
            if(phrase.charAt(i) == ' '){
                output.append('/');
            }
            else{
                output.append(asciiToMorse.get(phrase.charAt(i)));
            }
            output.append(' ');
        }
        return output.toString().trim();
    }

    private void sendSmsIntent(String phrase){
        Intent sms = new Intent(Intent.ACTION_VIEW);
        sms.setData(Uri.parse("sms:"));
        sms.putExtra("sms_body" , this.asciiToMorse(phrase));
        startActivity(sms);
    }

    private void translatePhraseToMorse() {
        if(currentPhrase.charAt(currentIndex) != ' '){
            asciiEncoding.setText(String.valueOf(this.currentPhrase.charAt(currentIndex)));
            morseEncoding.setText(this.asciiToMorse.get(this.currentPhrase.charAt(currentIndex)));
            flashMorseCode(this.asciiToMorse.get(this.currentPhrase.charAt(currentIndex)));
        }
        else{
            flashMorseCode(" ");
        }
        currentIndex += 1;
    }

    public static void flashMorseCode(String morseCode){
        currentThread = new Thread(new Worker(morseCode));
        currentThread.start();
    }

    private void hideTitleBar(){
        getSupportActionBar().hide();
    }

    private static void getCamera(){
        if(camera == null){
            try {
                camera = Camera.open();
                camera.setPreviewTexture(surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void releaseCamera(){
        if(camera != null){
            camera.release();
            camera = null;
        }
    }

    private static void stopCurrentThread(){
        if(currentThread != null){
            currentThread.interrupt();
        }
    }

    private void raiseInvalidInput(){
        Toast.makeText(getBaseContext(),"Invalid phrase input!",
                Toast.LENGTH_SHORT).show();
    }

    private void getCameraPermissions(){
        ActivityCompat.requestPermissions(this , new String[] {Manifest.permission.CAMERA , Manifest.permission.READ_SMS} , 1);
    }

    private void setNavigationListener(){
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(0).setChecked(true);
        navView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                break;
                            case R.id.navigation_history:
                                Intent history = new Intent(MainActivity.this , HistoryActivity.class);
                                startActivity(history);
                                finish();
                                overridePendingTransition(0, 0);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
    }

    private void setReferencesUI(){
        asciiEncoding = findViewById(R.id.ascii_encoding);
        morseEncoding = findViewById(R.id.morse_encoding);
        translateButton = findViewById(R.id.submit);
        sendSms = findViewById(R.id.sms);
        textInput = findViewById(R.id.text_input);
        textLayout = findViewById(R.id.text_layout);
        textLayout.setHintAnimationEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getCameraPermissions();
        createHandler();
        setNavigationListener();
        setReferencesUI();
        hideTitleBar();
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUI();
                currentPhrase = textInput.getText().toString().toUpperCase();
                if(currentPhrase.length() > 0 && isValidPhrase(currentPhrase)){
                    translatePhraseToMorse();
                    textInput.clearFocus();
                    textLayout.setHint("");
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textInput.getWindowToken(), 0);
                }
                else{
                    raiseInvalidInput();
                }
            }
        });
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phrase = textInput.getText().toString().toUpperCase();
                if(isValidPhrase(phrase) && phrase.length() > 0){
                    sendSmsIntent(textInput.getText().toString().toUpperCase());
                }
                else{
                    raiseInvalidInput();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCurrentThread();
        releaseCamera();
        resetUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopCurrentThread();
        getCamera();
        resetUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCurrentThread();
        releaseCamera();
        resetUI();
    }


}
