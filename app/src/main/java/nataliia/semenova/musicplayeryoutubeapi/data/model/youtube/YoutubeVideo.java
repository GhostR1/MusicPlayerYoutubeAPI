package nataliia.semenova.musicplayeryoutubeapi.data.model.youtube;

public class YoutubeVideo {
    private VideoID id;
    private Snippet snippet;

    public VideoID getId() {
        return id;
    }

    public void setId(VideoID id) {
        this.id = id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }
}
