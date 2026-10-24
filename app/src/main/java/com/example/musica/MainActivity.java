package com.example.musica;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;

import com.example.musica.db.DBsongs;
import com.example.musica.entitites.Songs;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    RecyclerView listSongs;
    ArrayList<Songs> songsArrayList;
    ListSongsAdapter adapter;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        listSongs = findViewById(R.id.listSongs);
        search = findViewById(R.id.search);

        listSongs.setLayoutManager(new LinearLayoutManager(this));

        //DBsongs dBsongs = new DBsongs(MainActivity.this);

        //songsArrayList = new ArrayList<>();

        //ListSongsAdapter adapter = new ListSongsAdapter(dBsongs.showSongs());
        //listSongs.setAdapter(adapter);


        search.setOnQueryTextListener(this);

    }

    //aquí se llama a la clase que va a imprimir los elementos dentro del recyclerView
    @Override
    protected void onResume() {
        super.onResume();
        DBsongs dBsongs = new DBsongs(this);
        listSongs.setAdapter(new ListSongsAdapter(dBsongs.showSongs()));
        adapter = new ListSongsAdapter(dBsongs.showSongs());
        listSongs.setAdapter(adapter);
    }

    //detener la canción
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.releasePlayer();
    }

    //creación del inflater: menú que sale en los 3 puntos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //mostrar las opciones (en este caso solo agregar canción)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuNuevo) {
            newSongv(); //abrir la vista de registro de canción
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //iniciar la vista de nueva canción
    private void newSongv() {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    //recopilar la query del filtro a listsongsAdapter para que se actualicen las fias que se muestran
    @Override
    public boolean onQueryTextChange(String query) {
        adapter.filt(query);
        return false;
    }

    //activar el filtro
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}