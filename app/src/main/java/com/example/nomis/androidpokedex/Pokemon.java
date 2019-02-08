package com.example.nomis.androidpokedex;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Pokemon extends AppCompatActivity {
    private ImageView pokemonSprite;
    private RequestQueue mQueue;
    private TextView pokemonAbilityTextView;
    private TextView pokemonNameTextView;
    private TextView pokemonTypeTextView;
    private TextView pokemonDescTextView;
    private TextView pokemonWeightTextView;
    private TextView pokemonHeightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon);

        Bundle extras = getIntent().getExtras();
        int pokemonId = extras.getInt("pokemonId");

        // pokemon name with #number
        String pokemonName = extras.getString("pokemonName");

        // initialize xml objects
        pokemonSprite = findViewById(R.id.pokemonSprite);
        pokemonAbilityTextView = findViewById(R.id.pokemonAblitiy);
        pokemonTypeTextView = findViewById(R.id.pokemonType);
        pokemonDescTextView = findViewById(R.id.pokemonDesc);
        pokemonWeightTextView = findViewById(R.id.pokemonWeight);
        pokemonHeightTextView = findViewById(R.id.pokemonHeight);
        pokemonNameTextView = findViewById(R.id.pokemonName);
        pokemonNameTextView.append(pokemonName);

        mQueue = Volley.newRequestQueue(this);
        jsonParseTwo(pokemonId);
        jsonParse(pokemonId);
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

                                pokemonTypeTextView.append(typeName + "\n");
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