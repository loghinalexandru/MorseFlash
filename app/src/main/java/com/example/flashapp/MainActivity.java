package com.example.flashapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

//TODO: Prompt for camera permission;
//TODO: Solve desync

public class MainActivity extends AppCompatActivity {
    private TextView asciiEncoding;
    private TextView morseEncoding;
    private Button translateButton;
    private Button sendSms;
    private TextInputEditText textInput;
    private String currentPhrase;
    private static int currentIndex = 0;
    public static Handler callbackHandler;

    public static Camera camera;
    private static SurfaceTexture surface = new SurfaceTexture(1);
    private static Dictionary<Character, String>  asciiToMorse = new Hashtable<>();
    private static Dictionary<String , Character> morseToAscii = new Hashtable<>();

    private static void init(){
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

    private void reset(){
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
                    reset();
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
        new Thread(new Worker(morseCode)).start();
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

    private void raiseInvalidInput(){
        Toast.makeText(getBaseContext(),"Invalid phrase input!",
                Toast.LENGTH_SHORT).show();
    }

    private void getCameraPermissions(){
        ActivityCompat.requestPermissions(this , new String[] {Manifest.permission.CAMERA} , 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getCameraPermissions();
        createHandler();
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        asciiEncoding = findViewById(R.id.ascii_encoding);
        morseEncoding = findViewById(R.id.morse_encoding);
        translateButton = findViewById(R.id.submit);
        sendSms = findViewById(R.id.sms);
        textInput = findViewById(R.id.text_input);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                currentPhrase = textInput.getText().toString().toUpperCase();
                if(currentPhrase.length() > 0 && isValidPhrase(currentPhrase)){
                    translatePhraseToMorse();
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
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }


}
