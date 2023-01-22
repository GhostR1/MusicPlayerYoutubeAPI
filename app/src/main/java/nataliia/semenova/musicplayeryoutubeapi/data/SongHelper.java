package nataliia.semenova.musicplayeryoutubeapi.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import java.util.ArrayList;
import java.util.List;


import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;

public class SongHelper {
    final private static Uri ALBUM_URI = Uri.parse("content://media/external/audio/albumart");

    public static List<Song> getSongList(Context context) {
        ContentResolver songResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = songResolver.query(musicUri, null, null, null, null);

        List<Song> songList = new ArrayList<>();

        if (songCursor != null && songCursor.moveToFirst()) {
            int idColumn = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            do {
                long id = songCursor.getLong(idColumn);
                String title = songCursor.getString(titleColumn);
                String artist = songCursor.getString(artistColumn);
                long albumID = songCursor.getLong(albumColumn);
                Uri albumArtUri = ContentUris.withAppendedId(ALBUM_URI, albumID);
                songList.add(new Song(id, title, artist, albumArtUri));
            } while (songCursor.moveToNext());
        }

        if (songCursor != null) {
            songCursor.close();
        }
        return songList;
    }
}
