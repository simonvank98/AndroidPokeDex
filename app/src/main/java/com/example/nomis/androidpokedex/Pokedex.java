package com.example.nomis.androidpokedex;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Pokedex extends Fragment {

    private ArrayList<Drawable> sprites = new ArrayList<>();
    private ArrayList<Drawable> spritesHelper = new ArrayList<>();
    private ArrayList<String> pokemonNames = new ArrayList<>();
    private ArrayList<String> classifications = new ArrayList<>();

    private DatabaseReference dbclassifications;

    private String storedClassification;
    private SharedPreferences pref;

    private PokedexAdapter pokedexAdapter;

    private String id = "#-1"; // A default value to show ID is not called correctly. Also prevents nullpointerexceptions.
    private String pokemonName = "Placeholder"; // Same goes for this string

    private RequestQueue requestQueue;

    private int pokemonId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pokedex, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        pref = getActivity().getSharedPreferences("PokemonData", Context.MODE_PRIVATE);

        dbclassifications = FirebaseDatabase.getInstance().getReference("classifications");

        dbclassifications.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                classifications.clear();
                storedClassification = "";
                pref.edit().clear().commit();
                int id = 1;

                for(DataSnapshot classificationSnap : dataSnapshot.getChildren()){
                    Classification cls = classificationSnap.getValue(Classification.class);
                    classifications.add(cls.getClassification() + " Pokémon");
                    storedClassification = storedClassification + id + "#" + cls.getClassification() + " Pokémon-";
                    id++;
                }
                pref.edit().putString("classifications", storedClassification).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        requestQueue = Volley.newRequestQueue(getContext());

        getPokemonData();
        
    }

    private void loadClassifications(){
        String classificationString = pref.getString("classifications", "empty");
        if (classificationString.equals("empty")) {
            Toast internetError = Toast.makeText(getContext(), "No internet connection detected. Try again.", Toast.LENGTH_LONG);
            for(int i = 0; i < 151; i++){
                classifications.add("");
            }
            internetError.show();
        } else {
            String[] classificationArray = classificationString.split("-");
            for(int i = 0; i < 151; i++){
                classifications.add(classificationArray[i].split("#")[1]);
            }
        }
    }

    private Bitmap drawabletoBitmap(Drawable sprite) {
        if (sprite instanceof BitmapDrawable) {
            BitmapDrawable bitmapSprite = (BitmapDrawable) sprite;
            if(bitmapSprite.getBitmap() != null) {
                return bitmapSprite.getBitmap();
            }
        }
        return null;
    }

    private void saveToInternalStorage(Drawable sprite, int id){
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getDir("sprites", Context.MODE_PRIVATE);
        File filePath = new File(directory,"pokemon_sprite_" + id + ".png");
        Bitmap bitmapSprite = drawabletoBitmap(sprite);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            bitmapSprite.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BitmapDrawable loadImageFromStorage(int id) {
        String path = "/data/user/0/com.example.nomis.androidpokedex/app_sprites";
        Bitmap bitmapSprite;
        try {
            File f=new File(path, "pokemon_sprite_" + id + ".png");
            bitmapSprite = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e) {
            return null;
        }
        return new BitmapDrawable(getResources(), bitmapSprite);
    }

    private void getPokemonData() {
        String url = "https://pokeapi.co/api/v2/pokedex/kanto/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

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
                            e.printStackTrace();
                        } finally {
                            pokedexAdapter = new PokedexAdapter();

                            ListView pokedexlist = (ListView)  getView().findViewById(R.id.pokedexlist);

                            pokedexlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    // start new activity upon clicking an item in the listview
                                    Intent myIntent = new Intent(getContext(), Pokemon.class);
                                    pokemonId = i + 1;
                                    String name = pokemonNames.get(i);
                                    myIntent.putExtra("pokemonId", pokemonId);
                                    myIntent.putExtra("pokemonName", name);
                                    startActivity(myIntent);

                                }
                            });

                            Thread webAccessThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for(int i = 1; i <= 151; i++) {
                                            if(loadImageFromStorage(i) != null){
                                                spritesHelper.add(loadImageFromStorage(i));
                                            } else {
                                                spritesHelper.add(spriteFromWeb(i));
                                            }

                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });

                            webAccessThread.start();

                            while(sprites.size() < 151){
                                sprites = spritesHelper;
                            }

                            pokedexAdapter.notifyDataSetChanged();

                            pokedexlist.setAdapter(pokedexAdapter);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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
            saveToInternalStorage(sprite, id); //Once it is loaded, save it to internal storage so it will start faster in future.
            return sprite;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    class PokedexAdapter extends BaseAdapter {

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

            view = getLayoutInflater().inflate(R.layout.customlayout, null);

            ImageView spriteView = (ImageView) view.findViewById(R.id.spriteView);
            TextView pokemonName = (TextView) view.findViewById(R.id.pokemonName);
            TextView pokemonClassification = (TextView) view.findViewById(R.id.pokemonClassification);

            if (classifications.isEmpty()) {
                loadClassifications();
            }

            spriteView.setImageDrawable(sprites.get(i));
            pokemonName.setText(pokemonNames.get(i));
            pokemonClassification.setText(classifications.get(i));

            spriteView.setScaleX((float)1.5);
            spriteView.setScaleY((float)1.5);

            return view;
        }
    }
}
