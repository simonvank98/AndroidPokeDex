package com.example.nomis.androidpokedex;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Favorites extends Fragment {

    private ArrayList<Drawable> favSprites = new ArrayList<>();
    private ArrayList<Integer> favIDs = new ArrayList<>();
    private ArrayList<String> favNames = new ArrayList<>();
    private ArrayList<String> favClassifications = new ArrayList<>();

    private ListView favoritesList;

    private FavoritesAdapter favoritesAdapter;

    private SharedPreferences pref;

    //fragment
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        favoritesAdapter = new FavoritesAdapter();

        favoritesAdapter.notifyDataSetChanged();

        favoritesList = (ListView) getView().findViewById(R.id.favorites_list);

        favoritesList.setAdapter(favoritesAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getActivity().getSharedPreferences("PokemonData", Context.MODE_PRIVATE);

        loadDataFromStorage();
    }

    // Load all pokémon that need to be shown in favorites

    // Classification string format is as follows: ID#ClassificationName-ID#ClassificationName-... etc
    // Favorites string format is as follows: ID#FavName-ID#FavName-... etc
    public void loadDataFromStorage() {

        String rawFavString = pref.getString("favorites", "empty");
        if (rawFavString.equals("empty")) {
            Toast notFoundError = Toast.makeText(getContext(), "No favorite pokémon found.", Toast.LENGTH_LONG);
            notFoundError.show();
        } else {
            String[] favoriteArray = rawFavString.split("-");
            for(int i = 0; i < favoriteArray.length; i++){
                favIDs.add(Integer.valueOf(favoriteArray[i].split("#")[0]));
                favNames.add(favoriteArray[i].split("#")[1]);
                favSprites.add(loadImageFromStorage(Integer.valueOf(favoriteArray[i].split("#")[0])));
            }
        }

        String rawClassificationString = pref.getString("classifications", "empty");
        if (rawClassificationString.equals("empty")) {
            Toast notFoundError = Toast.makeText(getContext(), "No classifications found.", Toast.LENGTH_LONG);
            notFoundError.show();
        } else {
            String[] classificationArray = rawClassificationString.split("-");
            for(int i = 0; i < classificationArray.length; i++){
                favClassifications.add(classificationArray[i]);
            }
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

    class FavoritesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return favSprites.size();
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

            for(int j = 0; j < favClassifications.size(); j++){
                if(Integer.valueOf(favClassifications.get(j).split("#")[0]).equals(favIDs.get(i))){
                    pokemonClassification.setText(favClassifications.get(j).split("#")[1]);
                }
            }

            spriteView.setImageDrawable(favSprites.get(i));
            pokemonName.setText(favNames.get(i));

            spriteView.setScaleX((float)1.5);
            spriteView.setScaleY((float)1.5);

            return view;
        }
    }

}
