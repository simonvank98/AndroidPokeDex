package com.example.nomis.androidpokedex;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Pokemon extends AppCompatActivity {
    private int id;
    private static int total;
    Drawable sprite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon);
        total++;
        String pokemonId= getIntent().getStringExtra("pokemonId");
        Log.d("debug", "Totaal: " + total);
        Log.d("debug", "pokemonId: " + pokemonId);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("debug", "On Destroy .....");
    }

    public Drawable spriteFromWeb(int id){
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
        InputStream inputStream = null;
        try {
            inputStream = (InputStream) new URL(url).getContent();
            Drawable sprite = Drawable.createFromStream(inputStream, "" + id + ".png");
            return sprite;
        } catch (IOException e) {
            Log.e("debugs", "An error has occured in spriteFromWeb");
            e.printStackTrace();
        }
        return null;
    }
}

//    Thread webAccessThread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            try {
//                sprite = spriteFromWeb(2);
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    });
//
//        webAccessThread.start();
//
//                while(sprite == null) {}
//
//                ImageView spriteView = (ImageView) findViewById(R.id.pokemonSprite);
//                spriteView.setImageDrawable(sprite);