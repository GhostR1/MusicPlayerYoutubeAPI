package nataliia.semenova.musicplayeryoutubeapi.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;

public class SongHelper {
    public static List<Song> getSongList(Context context) {
        ContentResolver songResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = songResolver.query(musicUri, null, null, null, null);

        List<Song> songList = new ArrayList<>();

        if (songCursor != null && songCursor.moveToFirst()) {
            int idColumn = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int performerColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                long id = songCursor.getLong(idColumn);
                String title = songCursor.getString(titleColumn);
                String performer = songCursor.getString(performerColumn);
                songList.add(new Song(id, title, performer));
            } while (songCursor.moveToNext());
        }

        if (songCursor != null) {
            songCursor.close();
        }
        return songList;
    }
}
