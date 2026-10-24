package com.example.musica;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musica.entitites.Songs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


//este es el recyclerView, es la tarjeta que le da formato a una canción individualmente
public class ListSongsAdapter extends RecyclerView.Adapter<ListSongsAdapter.SongViewHolder> {

    ArrayList<Songs> songsList; //lista completa de canciones
    ArrayList<Songs> filteredList; //lista de coincidencias con el filtro aplicado

    private MediaPlayer player; //player para reproducir el audio de las canciones
    private int currentSongId = -1; //id de la canción, se inicializa como -1 pero se reasigna más adelante




    private int indexOfSong(int songId) {
        for (int i = 0; i < songsList.size(); i++) {
            if (songsList.get(i).getId() == songId) return i;
        }
        return -1;
    }

    private void refreshSong(int songId) {
        int pos = indexOfSong(songId);
        if (pos != -1) notifyItemChanged(pos);
    }


    public ListSongsAdapter(ArrayList<Songs> songsList) {

        this.songsList = songsList;
        filteredList = new ArrayList<>();
        filteredList.addAll(songsList);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Songs song = songsList.get(position);

        holder.tvName.setText(songsList.get(position).getName());
        holder.tvArtist.setText(songsList.get(position).getArtist());
        holder.tvAlbum.setText(songsList.get(position).getAlbum());

        int duration = songsList.get(position).getDuration();
        holder.tvDuration.setText(String.format("%d:%02d", duration / 60, duration % 60));

        File imgFile = new File(holder.itemView.getContext().getFilesDir(), "images/" + song.getCover());
        holder.imgCover.setImageURI(Uri.fromFile(imgFile));

        boolean isPlayingThis = song.getId() == currentSongId && player != null && player.isPlaying();

        holder.btnPlay.setImageResource(isPlayingThis
                ? android.R.drawable.ic_media_pause
                : android.R.drawable.ic_media_play);

        holder.btnPlay.setOnClickListener(v ->
                togglePlay(holder.getBindingAdapterPosition(), v.getContext()));
    }

    public void filt(String txtUser) {
        String query = normalize(txtUser.trim());

        songsList.clear();

        if (query.isEmpty()) {
            songsList.addAll(filteredList);
        } else {
            for (Songs s : filteredList) {
                if (normalize(s.getName()).contains(query)
                        || normalize(s.getArtist()).contains(query)
                        || normalize(s.getAlbum()).contains(query)) {
                    songsList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Pasa a minúsculas y quita acentos: "canción" coincide con "cancion"
    private String normalize(String text) {
        if (text == null) return "";
        return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvArtist, tvAlbum, tvDuration;
        ImageButton btnPlay;
        ImageView imgCover;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvAlbum = itemView.findViewById(R.id.tvAlbum);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            imgCover = itemView.findViewById(R.id.imgCover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return;
                    }
                    Context context = v.getContext();
                    Intent intent = new Intent(context, SeeActivity.class);
                    intent.putExtra("ID", songsList.get(position).getId());
                    context.startActivity(intent);
                }
            });
        }
    }

    private void togglePlay(int position, Context context) {
        if (position == RecyclerView.NO_POSITION) return;

        Songs song = songsList.get(position);

        // Decide if continue or pause a the song
        if (song.getId() == currentSongId && player != null) {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.start();
            }
            notifyItemChanged(position);
            return;
        }

        // Another song stops the current song
        int previousId = currentSongId;
        releasePlayer();

        File audioFile = getAudioFile(context, song.getAudio());
        if (audioFile == null) {
            Toast.makeText(context, "No se encontró el archivo de audio", Toast.LENGTH_SHORT).show();
            refreshSong(previousId);
            return;
        }

        try {
            player = new MediaPlayer();
            player.setDataSource(audioFile.getAbsolutePath());
            player.prepare();
            player.start();
            currentSongId = song.getId();

            // change the icon when song finishes
            player.setOnCompletionListener(mp -> {
                int finishedId = currentSongId;
                releasePlayer();
                refreshSong(finishedId);
            });
        } catch (IOException e) {
            e.printStackTrace();
            releasePlayer();
            Toast.makeText(context, "No se pudo reproducir", Toast.LENGTH_SHORT).show();
        }

        refreshSong(previousId);
        notifyItemChanged(position);
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
        currentSongId = -1;
    }

    private File getAudioFile(Context context, String name) {
        if (name == null) return null;

        File file = new File(context.getFilesDir(), name);
        if (file.exists()) return file;

        // Save name with extension
        String[] extensions = {".mp3", ".wav", ".m4a", ".ogg", ".aac", ".audio"};
        for (String ext : extensions) {
            File f = new File(context.getFilesDir(), name + ext);
            if (f.exists()) return f;
        }
        return null;
    }
}