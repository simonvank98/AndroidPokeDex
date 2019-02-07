package com.example.nomis.androidpokedex;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;

public class Pokedex extends Fragment {
    private int pokemonId;
    //fragment
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pokedex, null);
    }

    ArrayList<Drawable> sprites = new ArrayList<>();
    ArrayList<Drawable> spritesHelper = new ArrayList<>();
    ArrayList<String> pokemonNames = new ArrayList<>();
    ArrayList<String> classifications = new ArrayList<>();

    CustomAdapter  customAdapter;

    String id = "#-1"; // A default value to show ID is not called correctly. Also prevents nullpointerexceptions.
    String pokemonName = "Placeholder"; // Same goes for this string

    private RequestQueue requestQueue;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getContext());

        classifications.add("Seed pokémon");
        classifications.add("Seed pokémon");
        classifications.add("Seed pokémon");
        classifications.add("Lizard pokémon");
        classifications.add("Flame pokémon");
        classifications.add("Flame pokémon");
        classifications.add("Tiny turtle pokémon");
        classifications.add("Turtle pokémon");
        classifications.add("Turtle pokémon");
        classifications.add("Worm pokémon");

        for(int i= 0; i < 141; i++){
            classifications.add("test");
        }

        getPokemonData();


    }



    private void getPokemonData() {
        String url = "https://pokeapi.co/api/v2/pokedex/kanto/";
        Log.d("debugs", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("debug", "try block triggered");

                            JSONArray entriesArray = response.getJSONArray("pokemon_entries");

                            for(int i = 0; i< entriesArray.length(); i++){
                                JSONObject entriesIndex = entriesArray.getJSONObject(i);

                                JSONObject speciesInfo = entriesIndex.getJSONObject("pokemon_species");

                                pokemonName = speciesInfo.getString("name");

                                id = "#" + entriesIndex.getString("entry_number");

                                pokemonName = pokemonName.substring(0, 1).toUpperCase() +
                                        pokemonName.substring(1); // Capitalize first letter.

                                pokemonName = id + " - " + pokemonName;

                                pokemonNames.add(pokemonName);

                            }

                        } catch (JSONException e) {
                            Log.e("debugs", "onResponse catch block triggered");
                            e.printStackTrace();
                        } finally {

                            Log.d("debugs", "Finally block triggered");

                            customAdapter = new CustomAdapter();

                            ListView pokedexlist = (ListView)  getView().findViewById(R.id.pokedexlist);

                            pokedexlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    // start new activity upon clicking an item in the listview
                                    Intent myIntent = new Intent(getContext(), Pokemon.class);
                                    pokemonId = i + 1;
                                    myIntent.putExtra("pokemonId", pokemonId);

                                    startActivity(myIntent);

                                }
                            });

                            Thread webAccessThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for(int i = 1; i <= 151; i++) {
                                            spritesHelper.add(spriteFromWeb(i));
                                        }
                                    } catch (Exception e){
                                        Log.e("debugs", "an error has occured.");
                                        e.printStackTrace();
                                    }
                                }
                            });

                            webAccessThread.start();

                            while(sprites.size() < 151){
                                sprites = spritesHelper;
                            }

                            customAdapter.notifyDataSetChanged();



                            pokedexlist.setAdapter(customAdapter);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("debugs", "onErrorResponse code triggered");
            }
        });



        requestQueue.add(request);

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

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sprites.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            Log.d("debugs", "Array call triggered");

            view = getLayoutInflater().inflate(R.layout.customlayout, null);

            ImageView spriteView = (ImageView) view.findViewById(R.id.spriteView);
            TextView pokemonName = (TextView) view.findViewById(R.id.pokemonName);
            TextView pokemonClassification = (TextView) view.findViewById(R.id.pokemonClassification);

            spriteView.setImageDrawable(sprites.get(i));
            pokemonName.setText(pokemonNames.get(i));
            pokemonClassification.setText(classifications.get(i));

            return view;
        }
    }
}
