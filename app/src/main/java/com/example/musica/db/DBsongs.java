package com.example.musica.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;
import com.example.musica.entitites.Songs;

import java.util.ArrayList;

/*
extensión del BDhelper
esta clase se encarga de la administración de la base de datos, aquí enviamos los INSERT, UPDATE y DELETE a la db
 */
public class DBsongs extends DBhelper {

    Context context; //context nos permite acceder a la información del sistema se necesita porque estaremos escribiendo y modificando datos

    //constructor de la clase
    public DBsongs(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    //método para añadir nueva canción, recibe los datos que el usuario ingresó en add_song.xml
    public long new_song(String name, String artist, String cover, String album, String audio, int duration) {
        long id = -1; //variable de control
        try {
            DBhelper dbHelper = new DBhelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase(); //obtenemos acceso a la db

            //string de valores que se insertarán en las columnas
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("artist", artist);
            values.put("cover", cover);
            values.put("album", album);
            values.put("audio", audio);
            values.put("duration", duration);

            id = db.insert(TABLE_SONGS, null, values); //insertar
        } catch (Exception ex) {
            Log.e("DB", "Error al insertar", ex);
        }

        return id; // si no se realizó el INSERT, el id queda negativo
    }

    //Mostrar la lista de canciones registradas
    public ArrayList<Songs> showSongs() {

        DBhelper dBhelper = new DBhelper(context);
        SQLiteDatabase db = dBhelper.getWritableDatabase();

        //
        ArrayList<Songs> songslist = new ArrayList<>(); //array de retorno que se usa para mostrar las canciones
        Songs song = null; //definición del objeto song
        Cursor cursorSongs = null; //el cursor nos permite recorrer los registros por cada fila

        /*
        SQL que ejecuta el cursor, el resultado de la consulta es lo que se recorre
         */
        cursorSongs = db.rawQuery("SELECT * FROM " + TABLE_SONGS, null);

        if (cursorSongs.moveToFirst()) {
            do{
                song = new Songs();
                //llamada a los set
                song.setId(cursorSongs.getInt(0));
                song.setName(cursorSongs.getString(1));
                song.setArtist(cursorSongs.getString(2));
                song.setCover(cursorSongs.getString(3));
                song.setAlbum(cursorSongs.getString(4));
                song.setAudio(cursorSongs.getString(5));
                song.setDuration(cursorSongs.getInt(6));
                songslist.add(song); //agregar la canción actual al array list
            } while (cursorSongs.moveToNext()); //siguiente arreglo a obtener
        }

        cursorSongs.close(); //cerrar el cursor una vez que termina de revisar

        return songslist;
    }

    // método para visualizar activity_see.xml
    // muestra la información de una canción a la que le demos click
    public Songs seeSongs(int id) {

        DBhelper dBhelper = new DBhelper(context);
        SQLiteDatabase db = dBhelper.getWritableDatabase();

        Songs song = null; //definición del objeto song
        Cursor cursorSongs; //definición del cursor

        //información que usará el cursor
        cursorSongs = db.rawQuery("SELECT * FROM " + TABLE_SONGS + " WHERE id = " + id + " LIMIT 1", null);

        //recuperar la infromación de la consulta, es una sola sola pues estamos buscando un id específico
        if (cursorSongs.moveToFirst()) {
                song = new Songs();
                song.setId(cursorSongs.getInt(0));
                song.setName(cursorSongs.getString(1));
                song.setArtist(cursorSongs.getString(2));
                song.setCover(cursorSongs.getString(3));
                song.setAlbum(cursorSongs.getString(4));
                song.setAudio(cursorSongs.getString(5));
                song.setDuration(cursorSongs.getInt(6));
        }

        cursorSongs.close(); //cerrar el cursor

        return song; //regresar la canción que nos interesa que se abrirá en activity_see
    }

    //método para editar la información de la canción
    public boolean editSong(int id, String name, String artist, String cover, String album, String audio, int duration){ //la función recibe los datos nuevos
        boolean confirm = false; //variable de control que indica si el INSERT se realizó correctamente

        DBhelper dBhelper = new DBhelper(context); // llamada al modelo (helper)
        SQLiteDatabase db = dBhelper.getWritableDatabase();  // acceso de edición a la DB

        try {
            //SQL que se enviará a la DB
            db.execSQL("UPDATE " + TABLE_SONGS +
" SET name = '"+name+"',  artist = '"+artist+"', cover = '"+cover+"', album = '"+album+"', audio = '"+audio+"', duration = '"+duration+"' WHERE id = '"+id+"' ");
            confirm = true;
        } catch (Exception ex) {
            ex.toString();
            confirm = false;
        } finally {
            db.close(); //cerrar el acceso a la db una vez realizado el proceso
        }

        return confirm; //si el INSERT fue realizado satisfactoriamente se regresa true, caso contrario el valor es false
    }

    //método para eliminar la canción deseada, solo recibe el id
    public boolean deleteSong(int id) {
        boolean confirm = false; //variable de control
        DBhelper dBhelper = new DBhelper(context); //llamada al modelo (helper)
        SQLiteDatabase db = dBhelper.getWritableDatabase(); //acceso de edición a la base de datos

        try {
            db.execSQL("DELETE FROM " + TABLE_SONGS + " WHERE id = '"+id+"'");
            confirm = true; //ejecución de DELETE con el id
        } catch (Exception ex) {
            ex.toString();
            confirm = false;
        } finally {
            db.close();
        }
        return confirm; //regresa como false o true dependiendo si hubo o no error
    }
}