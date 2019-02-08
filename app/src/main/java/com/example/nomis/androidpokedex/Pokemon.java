package com.example.nomis.androidpokedex;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Pokemon extends AppCompatActivity {
    private ImageView pokemonSprite;
    private RequestQueue mQueue;
    private TextView pokemonAbilityTextView;
    private TextView pokemonNameTextView;
    private TextView pokemonTypeTextView;
    private TextView pokemonDescTextView;
    private TextView pokemonWeightTextView;
    private TextView pokemonHeightTextView;
    private ImageButton favoriteButton;
    private Boolean favoriteStatus;

    private String pokemonName;
    private int pokemonId;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon);

        favoriteStatus = false;

        pref = this.getSharedPreferences("PokemonData", MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        pokemonId = extras.getInt("pokemonId");

        // pokemon name with #number
        pokemonName = extras.getString("pokemonName");

        // initialize xml objects
        pokemonSprite = findViewById(R.id.pokemonSprite);
        pokemonSprite.setImageDrawable(loadImageFromStorage(pokemonId));
        pokemonAbilityTextView = findViewById(R.id.pokemonAblitiy);
        pokemonTypeTextView = findViewById(R.id.pokemonType);
        pokemonDescTextView = findViewById(R.id.pokemonDesc);
        pokemonWeightTextView = findViewById(R.id.pokemonWeight);
        pokemonHeightTextView = findViewById(R.id.pokemonHeight);
        pokemonNameTextView = findViewById(R.id.pokemonName);
        favoriteButton = findViewById(R.id.favoriteButton);
        pokemonNameTextView.append(pokemonName);

        mQueue = Volley.newRequestQueue(this);
        jsonParseTwo(pokemonId);
        jsonParse(pokemonId);

        checkFavoriteStatus();
    }

    private void addFavorite() {
        String rawFavString = pref.getString("favorites", "");
        Log.d("Debugs", rawFavString);
        rawFavString = rawFavString + pokemonId + "#" + pokemonName.split(" ")[2] + "-";
        Log.d("Debugs", rawFavString);
        pref.edit().putString("favorites",  rawFavString).commit();

    }

    public void onClickFavorite(View v) {
        if(favoriteStatus){
            removeFavorite();
            favoriteButton.setImageResource(R.drawable.star);
            favoriteStatus = false;
        }
        else {
            addFavorite();
            favoriteButton.setImageResource(R.drawable.star_pressed);
            favoriteStatus = true;
        }
    }

    private void checkFavoriteStatus() {
        String rawFavString = pref.getString("favorites", "");
        if (rawFavString.contains(pokemonName.split(" ")[2])){
            favoriteStatus = true;
            favoriteButton.setImageResource(R.drawable.star_pressed);
        }
    }



    private void removeFavorite(){
        String rawFavString = pref.getString("favorites", "empty");
        if(rawFavString.equals("empty")) {
            Toast notFoundError = Toast.makeText(this, "The pokemon is not a favorite.", Toast.LENGTH_LONG);
            notFoundError.show();
        }
        else {
            Log.d("debugs", rawFavString);
            if(rawFavString.contains(pokemonId + "#" + pokemonName.split(" ")[2] + "-")) {
                 rawFavString = rawFavString.replace(pokemonId + "#" + pokemonName.split(" ")[2] + "-", "");
                Log.d("debugs 2", rawFavString);
            }
            String tempString = pref.getString("classifications", "");
            pref.edit().clear().commit();
            pref.edit().putString("classifications", tempString).commit();
            pref.edit().putString("favorites", rawFavString).commit();
        }
    }

    private BitmapDrawable loadImageFromStorage(int id) {
        String path = "/data/user/0/com.example.nomis.androidpokedex/app_sprites";
        Bitmap bitmapSprite;
        try {
            File f = new File(path, "pokemon_sprite_" + id + ".png");
            bitmapSprite = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e) {
            return null;
        }
        return new BitmapDrawable(getResources(), bitmapSprite);
    }

    private void jsonParse(final int id) {

        String url = "https://pokeapi.co/api/v2/pokemon/" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            // sprites
//                            sprite = spriteFromWeb(pokemonId);
//                            pokemonSprite.setImageDrawable(something);

                            // types
                            JSONArray jsonArrayType = response.getJSONArray("types");
                            for (int j = 0; j < jsonArrayType.length(); j++) {
                                JSONObject typeObject = jsonArrayType.getJSONObject(j);
                                JSONObject type = typeObject.getJSONObject("type");

                                String typeName = type.getString("name");

                                pokemonTypeTextView.append("- " + typeName + "\n");
                            }

                            // ability names
                            JSONArray jsonArrayAbility = response.getJSONArray("abilities");
                            for (int i = 0; i < jsonArrayAbility.length(); i++) {
                                JSONObject abilityObject = jsonArrayAbility.getJSONObject(i);
                                JSONObject ability = abilityObject.getJSONObject("ability");

                                String abilityName = ability.getString("name");

                                pokemonAbilityTextView.append("- " + abilityName + "\n");
                            }


                            // height
                            double heightDouble = response.getDouble("height");
                            heightDouble = heightDouble / 10;
                            String height = String.valueOf(heightDouble);
                            height += " Meter";
                            pokemonHeightTextView.append(height);

                            // weight
                            double weightDouble = response.getDouble("weight");
                            weightDouble = weightDouble / 10;
                            String weight = String.valueOf(weightDouble);
                            weight += " Kilo";
                            pokemonWeightTextView.append(weight);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

    private void jsonParseTwo(final int id) {
        String url = "https://pokeapi.co/api/v2/pokemon-species/" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // description
                            JSONArray jsonArrayDesc = response.getJSONArray("flavor_text_entries");

                            for (int i = 0; i < jsonArrayDesc.length(); i ++) {
                                JSONObject languageObject = jsonArrayDesc.getJSONObject(i);
                                JSONObject language = languageObject.getJSONObject("language");
                                String languageName = language.getString("name");

                                JSONObject version = languageObject.getJSONObject("version");
                                String versionName = version.getString("name");


                                if (languageName.equals("en")&& versionName.equals("leafgreen")) {

                                    JSONObject descObject = jsonArrayDesc.getJSONObject(i);
                                    String desc =  descObject.getString("flavor_text");

                                    pokemonDescTextView.append(desc);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}