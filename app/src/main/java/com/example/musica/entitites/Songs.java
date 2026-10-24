package com.example.musica.entitites;
//esta clase sirve para poder llegar a la DB, representa cómo se van a guardar los datos en el dispositivo
public class Songs {
    //variables privadas (encapsulamiento de la DB)
    private int id;
    private String name;
    private String artist;
    private String cover;
    private String album;
    private String audio;
    private int duration;

    /*
    getters y setters
        getters permiten leer los datos
        setters nos ayudan a asignar y/o modificar los datos
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}