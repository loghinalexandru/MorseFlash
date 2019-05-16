package com.example.flashapp;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
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
    private TextInputEditText textInput;
    private String currentPhrase;
    private static long speed = 3000;

    private CountDownTimer timer;
    private static Camera camera;
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

    private void clearText(){
        this.morseEncoding.setText("");
        this.asciiEncoding.setText("");
    }

    private void StartDecoding(final String phrase){
        if(timer != null){
            timer.cancel();
        }
        timer = new CountDownTimer(phrase.length() * this.speed, speed){
            private int currentIndex = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                if(phrase.charAt(currentIndex) != ' '){
                    translatePhraseToMorse(phrase , currentIndex);
                }
                currentIndex += 1;
            }

            @Override
            public void onFinish() {
                clearText();
            }
        }.start();
    }

    private void translatePhraseToMorse(String phrase , int index) {
        asciiEncoding.setText(String.valueOf(phrase.charAt(index)));
        morseEncoding.setText(this.asciiToMorse.get(phrase.charAt(index)));
        flashMorseCode(this.asciiToMorse.get(phrase.charAt(index)));
    }

    public static void flashMorseCode(String morseCode){
        new Thread(new Worker(camera , morseCode)).start();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        asciiEncoding = findViewById(R.id.ascii_encoding);
        morseEncoding = findViewById(R.id.morse_encoding);
        translateButton = findViewById(R.id.submit);
        textInput = findViewById(R.id.text_input);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPhrase = textInput.getText().toString().toUpperCase();
                textInput.setText("");
                StartDecoding(currentPhrase);
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
