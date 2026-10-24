package com.example.musica;

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
import androidx.appcompat.app.AppCompatActivity;

import com.example.musica.db.DBsongs;
import com.example.musica.entitites.Songs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class EditActivity extends AppCompatActivity {

    EditText txtName, txtArtist, txtAlbum;
    Button btnSave, btnSelectCover, btnSelectAudio;
    FloatingActionButton floatEdit, floatDelete;
    ImageView imgCover;
    boolean confirm = false;
    Songs song;
    int id = 0;
    private String imageName;
    private ActivityResultLauncher<String> audioPicker;
    private int audioDuration;
    private String audioName;

//mismos métodos que en AddActivity, en general el mismo funcionamiento

    private final ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        imageName = saveImageAsJPG(uri);
                        File imgFile = new File(getFilesDir(), "images/" + imageName);
                        imgCover.setImageURI(Uri.fromFile(imgFile));

                        Toast.makeText(this, "Imagen subida", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();

                        Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    private String saveImageAsJPG(Uri imageUri) throws IOException {

        Bitmap bitmap;

        try (InputStream inputStream =
                     getContentResolver().openInputStream(imageUri)) {

            if (inputStream == null) {
                throw new IOException("No se pudo abrir la imagen");
            }

            bitmap = BitmapFactory.decodeStream(inputStream);
        }

        if (bitmap == null) {
            throw new IOException("No se pudo decodificar la imagen");
        }


        String randomName = UUID.randomUUID().toString() + ".jpg";


        File directory = new File(getFilesDir(), "images");

        if (!directory.exists() && !directory.mkdirs()) {
            bitmap.recycle();
            throw new IOException("No se puedo crear la imagen");
        }


        File imageFile = new File(directory, randomName);

        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {

            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            if (!success) {
                throw new IOException("Compresión fallida");
            }

            outputStream.flush();

        } catch (IOException e) {
            imageFile.delete();
            throw e;

        } finally {
            bitmap.recycle();
        }


        return randomName;
    }





    //Audio
    private void processAudio(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {

            retriever.setDataSource(this, uri);

            String durationStr = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION);

            long duration = Long.parseLong(durationStr);
            audioDuration = (int) duration / 1000;


            audioName = "audio_" + UUID.randomUUID().toString();


            File savedFile = saveAudio(uri,audioName);

            if (savedFile != null) {
                Toast.makeText(this, "audio guardado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "no se pudo procesar audio", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File saveAudio(Uri uri, String randomName)
            throws IOException {


        String extension = ".audio";

        String mimeType = getContentResolver()
                .getType(uri);

        if (mimeType != null) {
            switch (mimeType) {
                case "audio/mpeg":
                    extension = ".mp3";
                    break;
                case "audio/wav":
                case "audio/x-wav":
                    extension = ".wav";
                    break;
                case "audio/mp4":
                    extension = ".m4a";
                    break;
                case "audio/ogg":
                    extension = ".ogg";
                    break;
                case "audio/aac":
                    extension = ".aac";
                    break;
            }
        }

        File destination = new File(
                getFilesDir(),
                randomName + extension
        );

        try (InputStream input =
                     getContentResolver().openInputStream(uri);
             FileOutputStream output =
                     new FileOutputStream(destination)) {

            if (input == null) {
                throw new IOException("No se puede procesar audio");
            }

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }

        return destination;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_see);

        txtName = findViewById(R.id.txtName);
        txtArtist = findViewById(R.id.txtArtist);
        txtAlbum = findViewById(R.id.txtAlbum);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnSelectAudio = findViewById(R.id.btnSelectAudio);
        btnSave = findViewById(R.id.btnSave);
        imgCover = findViewById(R.id.imgCover);
        floatDelete = findViewById(R.id.floatDelete);
        floatEdit = findViewById(R.id.floatEdit);



        btnSelectCover.setOnClickListener(v ->
                imagePicker.launch("image/*")
        );



        audioPicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        processAudio(uri);
                    }
                }
        );

        btnSelectAudio.setOnClickListener(v->
                audioPicker.launch("audio/*"));


        floatEdit.setVisibility(View.INVISIBLE);
        floatDelete.setVisibility(View.INVISIBLE);

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

        DBsongs dbsongs = new DBsongs(EditActivity.this);
        song = dbsongs.seeSongs(id);

        if(song != null) {
            txtName.setText(song.getName());
            txtArtist.setText(song.getArtist());
            txtAlbum.setText(song.getAlbum());

            File imgFile = new File(new File(getFilesDir(), "images"), song.getCover());
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgCover.setImageBitmap(bitmap);

            imageName = song.getCover();
            audioDuration = song.getDuration();
            audioName = song.getAudio();
        }

        //parte diferente

        /*
        Al hacer click se valida que no haya campos vacíos y muestra el mensaje del estado
         */

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtName.getText().toString().equals("")
                        && !txtAlbum.getText().toString().equals("")
                        && !txtArtist.getText().toString().equals("")) {
                    confirm = dbsongs.editSong(id, txtName.getText().toString(), txtArtist.getText().toString(), imageName, txtAlbum.getText().toString(), audioName, audioDuration);

                    if (confirm) {
                        Toast.makeText(EditActivity.this, "Registro modificado", Toast.LENGTH_SHORT).show();
                        seeLog(); //se abre seeActivity
                    } else {
                        Toast.makeText(EditActivity.this, "Error al modificar el registro", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditActivity.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //regresa SeeActivity con el Id de la canción que editamos
    private void seeLog() {
        Intent intent = new Intent(this, SeeActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }
}