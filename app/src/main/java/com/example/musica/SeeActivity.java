package com.example.musica;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musica.db.DBsongs;
import com.example.musica.entitites.Songs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class SeeActivity extends AppCompatActivity {

    //variables globales
    EditText txtName, txtArtist, txtAlbum;
    Button btnSave, btnSelectCover, btnSelectAudio;
    ImageView imgCover;
    FloatingActionButton floatEdit, floatDelete;
    Songs song;
    int id = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_see);

        //obtener los elementos
        txtName = findViewById(R.id.txtName);
        txtArtist = findViewById(R.id.txtArtist);
        txtAlbum = findViewById(R.id.txtAlbum);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnSelectAudio = findViewById(R.id.btnSelectAudio);
        btnSave = findViewById(R.id.btnSave);
        imgCover = findViewById(R.id.imgCover);
        floatEdit = findViewById(R.id.floatEdit);
        floatDelete = findViewById(R.id.floatDelete);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras == null) {
                id = Integer.parseInt(null);
            } else {
                id = extras.getInt("ID");
            }
        } else {
            id = (int) savedInstanceState.getSerializable("ID");
        }

        DBsongs dbsongs = new DBsongs(SeeActivity.this);
        song = dbsongs.seeSongs(id);

        if(song != null) {
            txtName.setText(song.getName());
            txtArtist.setText(song.getArtist());
            txtAlbum.setText(song.getAlbum());

            File imgFile = new File(new File(getFilesDir(), "images"), song.getCover());
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgCover.setImageBitmap(bitmap);

            //en la vista de información los campos no pueden modificarse, y algunos elementos se ocultan
            btnSave.setVisibility(View.INVISIBLE);
            btnSelectAudio.setVisibility(View.INVISIBLE);
            btnSelectCover.setVisibility(View.INVISIBLE);
            txtName.setInputType(InputType.TYPE_NULL);
            txtAlbum.setInputType(InputType.TYPE_NULL);
            txtArtist.setInputType(InputType.TYPE_NULL);
        }

        //listener que desencadena editar una canción llamando al método en DBsongs
        floatEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeeActivity.this, EditActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });


        //listener que desencadena la eliminación de una canción llamando al método en DBsongs
        floatDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SeeActivity.this);
                builder.setMessage("¿Desea eliminar esta canción?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         if(dbsongs.deleteSong(id)) {
                            list();
                         }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
    }

    private void list() {
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}