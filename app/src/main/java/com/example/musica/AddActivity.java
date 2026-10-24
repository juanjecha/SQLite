package com.example.musica;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;
import android.media.MediaMetadataRetriever;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musica.db.DBsongs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

//controlador para agregar nueva canción
public class AddActivity extends AppCompatActivity {

    //vairables globales con las que se estará trabajando
    EditText txtName, txtArtist, txtAlbum;
    Button btnSelectCover, btnSelectAudio, btnSave;
    ImageView imgCover;

    private String imageName;
    public String getImageName() {
        return imageName;
    }

    private ActivityResultLauncher<String> audioPicker;
    private String audioName;
    private int audioDuration;


    //Selector de imagen
    private final ActivityResultLauncher<String> imagePicker = registerForActivityResult( //gestionar el retorno del dato sin riesgo de perder la referencia si la actividad se recrea
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) { //uri: objeto que se regresa al subir la imagen
                    try {
                        imageName = saveImageAsJPG(uri); //nombre de la imagen cargada, obtenido aleatoriamente desde la función SaveImage
                        File imgFile = new File(getFilesDir(), "images/" + imageName); //objeto de tipo image con el nombre
                        imgCover.setImageURI(Uri.fromFile(imgFile));

                        Toast.makeText(this, "Imagen subida", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();

                        Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    //esta función guarda la imagen en el almacenamiento de la aplicación con un nombre generado aleatoriamente
    private String saveImageAsJPG(Uri imageUri) throws IOException {

        Bitmap bitmap; //decodificación de la imagen en mapa de bits

        try (InputStream inputStream =
                     getContentResolver().openInputStream(imageUri)) { //lee el uri

            if (inputStream == null) {
                throw new IOException("No se pudo abrir la imagen");
            }

            bitmap = BitmapFactory.decodeStream(inputStream); //convierte el flujo de bytes en un objeto Bitmap
        }

        if (bitmap == null) {
            throw new IOException("No se pudo decodificar la imagen");
        }

        // Genera nombre aleatorio
        String randomName = UUID.randomUUID().toString() + ".jpg";

        // Creación del directorio privado para guardar las imágenes
        File directory = new File(getFilesDir(), "images"); //getFilesDir llama a al almcenamiento de la aplicación

        if (!directory.exists() && !directory.mkdirs()) {
            bitmap.recycle();
            throw new IOException("No se puedo crear la imagen");
        }

        // Crear el archivo de destino
        File imageFile = new File(directory, randomName);

        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) { //apunta al nuevo archivo creado

            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream); //si se realiza correctente comprime la imagen guardándola en formato jpg y el 90% de calidad

            if (!success) {
                throw new IOException("Compresión fallida");
            }

            outputStream.flush();

        } catch (IOException e) {
            imageFile.delete(); //en caso de que algún fallo ocurra, la imagen se borra
            throw e;

        } finally {
            bitmap.recycle(); //el bitmap ya no se utiliza, se elimina
        }

        // Retorna el nombre de la imagen, incluyendo .jpg extensión
        return randomName;
    }


    //Audio

    //process trabaja con el audio y obtiene los datos
    private void processAudio(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever(); //esta clase sirve para extraer metadatos de archivos
        try {
            // Obtener la duración del audio
            retriever.setDataSource(this, uri);

            String durationStr = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION); //el metodo KEY_duration retorna la duración del audio en milisegundos

            //conversión a segundos
            long duration = Long.parseLong(durationStr);
            audioDuration = (int) duration / 1000;

            //Generar un archivo aleatorio
            audioName = "audio_" + UUID.randomUUID().toString();

            //Guardar el archivo en la app
            File savedFile = saveAudio(uri,audioName);

            //mensajes y control de error
            if (savedFile != null) {
                Toast.makeText(this, "audio guardado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "no se pudo procesar audio", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                retriever.release(); //liberar códecs que usa el sistema multimedia de android
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //esta función guarda el archivo en el almacenamiento de la aplicación
    private File saveAudio(Uri uri, String randomName)
            throws IOException {

        // Obtener la extensión original del audio
        String extension = ".audio"; //funciona como respaldo si hay un error encontrando el formato

        String mimeType = getContentResolver()
                .getType(uri);

        //asignar la extensión del audio
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
        ); //el archivo se crea en el almacenamiento privado de la app

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

        return destination; //retorna objeto File que hace referencia al archivo guardado
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_song);

        txtName = findViewById(R.id.txtName);
        txtArtist = findViewById(R.id.txtArtist);
        txtAlbum = findViewById(R.id.txtAlbum);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnSelectAudio = findViewById(R.id.btnSelectAudio);
        btnSave = findViewById(R.id.btnSave);
        imgCover = findViewById(R.id.imgCover);


        //configurar para elegir una imagen
        btnSelectCover.setOnClickListener(v ->
                imagePicker.launch("image/*")
        );

        //configurar para elegir audios
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



        //set up the listener to save song
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBsongs dbsongs = new DBsongs(AddActivity.this);
                //long id = dbsongs.new_song(txtName.getText().toString(), txtArtist.getText().toString(), imageName, txtAlbum.getText().toString(), audioName, audioDuration);

                if (txtName.getText().toString().trim().isEmpty() || txtArtist.getText().toString().trim().isEmpty() || txtAlbum.getText().toString().trim().isEmpty() ||
                imageName == null || audioName == null || audioDuration == 0) {
                    Toast.makeText(AddActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                long id = dbsongs.new_song(txtName.getText().toString(), txtArtist.getText().toString(), imageName, txtAlbum.getText().toString(), audioName, audioDuration);

                if (id > 0) {
                    Toast.makeText(AddActivity.this, "Registro guardado", Toast.LENGTH_SHORT).show();
                    clear();
                } else {
                    Toast.makeText(AddActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //limpiar los campos cuando se envía la canción
    private void clear() {
        txtAlbum.setText("");
        txtArtist.setText("");
        txtName.setText("");
        imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
        imageName = null;
        audioName = null;
        audioDuration = 0;
    }
}