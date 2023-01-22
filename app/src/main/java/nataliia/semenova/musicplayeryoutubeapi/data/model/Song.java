package nataliia.semenova.musicplayeryoutubeapi.data.model;

import android.net.Uri;

public class Song {
    private long song_id;
    private String title;
    private String artist;
    private Uri coverUri;

    public Song(long song_id, String title, String artist, Uri coverUri) {
        this.song_id = song_id;
        this.title = title;
        this.artist = artist;
        this.coverUri = coverUri;
    }

    public long getId() {
            return song_id;
        }

    public void setId(long song_id) {
            this.song_id = song_id;
        }

    public String getTitle() {
            return title;
        }

    public void setTitle(String title) {
            this.title = title;
        }

    public String getArtist() {
            return artist;
        }

    public void setArtist(String artist) {
            this.artist = artist;
        }

    public Uri getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(Uri coverUri) {
        this.coverUri = coverUri;
    }
}
