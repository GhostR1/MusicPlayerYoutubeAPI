package nataliia.semenova.musicplayeryoutubeapi.data.model.youtube;


public class Snippet {
    private String title;
    private String channelTitle;
    private ThumbNail thumbnails;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public ThumbNail getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ThumbNail thumbnails) {
        this.thumbnails = thumbnails;
    }
}
