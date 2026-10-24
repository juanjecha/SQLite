package com.example.musica.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
//esta clase es administrada por Android al ser una extensión de SQLiteOpenHelper.
public class DBhelper extends SQLiteOpenHelper { //extensión del openhelper
    /*
    El open helper es la clase que se encarga de manejar la db.
    Abre la db si se creó anteriormente o la crea si no existe
    */

    private static final int DATABASE_VERSION = 1; //sin modificaciones, es la primera version
    private static final String DATABASE_NAME = "music_player.db"; //nombre de la db
    public static final String TABLE_SONGS = "songs"; // nombre de la tabla que se creará

    //creación de la db
    public DBhelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        /*
        context pasa la información para pasar labase de datos al almacenamiento interno
        database_name es el nombre con el que se guardará la db
        null representa el parámetro SQLiteDatabase.CursorFactory, null es para que se eliga el contenido por defecto en Android Studio
        la versión se compara con la del archivo creado, si son diferentes se ejecuta el método onUpgrade
         */
    }

    /*
    el método onCreate se encarga de crear la base de datos
    Se ejecuta cuando se inicializa la aplicación
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Definición del esquema en una consulta SQL
        //execSQL es el método que ejecuta la consulta
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_SONGS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(200) NOT NULL," +
                "artist VARCHAR(200) NOT NULL," +
                "cover VARCHAR(200) NOT NULL," +
                "album VARCHAR(200) NOT NULL," +
                "audio VARCHAR(200) NOT NULL," +
                "duration INT NOT NULL" + ")");
    }

    /*
    onUpgrade se ejecuta siempre que realizamos una modificación a la base de datos

    Si hacemos una primera versión esa se almacena en el dispositivom, pero si después actualizamos se actualiza la versión, lo que desencadena la ejecución de esta función
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        //el sql se ejecuta para eliminar la tabla anterior, y crea una nueva ejecutando el Oncreate
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_SONGS);
        onCreate(sqLiteDatabase);

    }
}