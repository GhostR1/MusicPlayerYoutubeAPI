package nataliia.semenova.musicplayeryoutubeapi.data.model;

public class Song {
        private long song_id;
        private String title;
        private String performer;

        public Song(long song_id, String title, String performer) {
            this.song_id = song_id;
            this.title = title;
            this.performer = performer;
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

        public String getPerformer() {
            return performer;
        }

        public void setPerformer(String performer) {
            this.performer = performer;
        }
}
