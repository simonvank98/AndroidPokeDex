package com.example.nomis.androidpokedex;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

//    ArrayList<Drawable> sprites = new ArrayList<>();
//    ArrayList<Drawable> spritesHelper = new ArrayList<>();
//    ArrayList<String> pokemonNames = new ArrayList<>();
//    ArrayList<String> classifications = new ArrayList<>();
//
//    PokedexAdapter  pokedexAdapter;
//
//    String id = "#-1"; // A default value to show ID is not called correctly. Also prevents nullpointerexceptions.
//    String pokemonName = "Placeholder"; // Same goes for this string
//
//    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseApp.initializeApp(this);

        setTheme(R.style.AppTheme);

        //standard code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //toggle for navigation(drawer)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //set home component to nav_main aka the Pokedex class
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Pokedex()).commit();
            navigationView.setCheckedItem(R.id.nav_main);
        }
    }

    //switch case for 'routes'
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.nav_main:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Pokedex()).commit();
                break;
                case R.id.nav_favorite:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Favorites()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //drawer method
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
