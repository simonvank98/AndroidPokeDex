package com.example.nomis.androidpokedex;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Favorites extends Fragment {

    //fragment
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites, container, false);
    }

    ArrayList<Drawable> favSprites = new ArrayList<>();
    ArrayList<Drawable> favSpritesHelper = new ArrayList<>();
    ArrayList<String> favNames = new ArrayList<>();
    ArrayList<String> favClassifications = new ArrayList<>();

    FavoritesAdapter favoritesAdapter;



    public void loadFromStorage(){
        // Load which pokemon need to be shown in favorites
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

            spriteView.setImageDrawable(favSprites.get(i));
            pokemonName.setText(favNames.get(i));
            pokemonClassification.setText(favClassifications.get(i));

            spriteView.setScaleX((float)1.5);
            spriteView.setScaleY((float)1.5);

            return view;
        }
    }

}
