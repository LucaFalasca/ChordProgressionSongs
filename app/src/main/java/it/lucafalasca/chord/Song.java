package it.lucafalasca.chord;

public class Song {
    private String title;
    private String artist;
    private String chords;

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getChords() {
        return chords;
    }
    public Song(String title, String artist, String chords) {
        this.title = title;
        this.artist = artist;
        this.chords = chords;
    }
}
