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
        pokemonNameTextView = findViewById(R.id.pokemonName);
        pokemonNameTextView.append(pokemonName);

        mQueue = Volley.newRequestQueue(this);
        jsonParse(pokemonId);
    }

    private void jsonParse(int id) {

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

                                pokemonTypeTextView.append(typeName + " ");
                            }

                            // ability names
                            JSONArray jsonArrayAbility = response.getJSONArray("abilities");
                            for (int i = 0; i < jsonArrayAbility.length(); i++) {
                                JSONObject abilityObject = jsonArrayAbility.getJSONObject(i);
                                JSONObject ability = abilityObject.getJSONObject("ability");

                                String abilityName = ability.getString("name");

//                                pokemonAbilityTextView.append(abilityName + "\n");
                            }


                            // height
                            double heightDouble = response.getDouble("height");
                            heightDouble = heightDouble / 10;
                            String height = String.valueOf(heightDouble);
                            height += " Meter";
//                            pokemonAbilityTextView.append(height + "\n");

                            // weight
                            double weightDouble = response.getDouble("weight");
                            weightDouble = weightDouble / 10;
                            String weight = String.valueOf(weightDouble);
                            weight += " Kilo";
//                            pokemonAbilityTextView.append(weight + "\n");

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