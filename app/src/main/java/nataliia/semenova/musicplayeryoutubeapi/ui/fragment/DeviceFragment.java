package nataliia.semenova.musicplayeryoutubeapi.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.utils.PermissionHelper;
import nataliia.semenova.musicplayeryoutubeapi.data.SongHelper;
import nataliia.semenova.musicplayeryoutubeapi.data.model.ISongList;
import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;
import nataliia.semenova.musicplayeryoutubeapi.ui.MusicPlayerActivity;
import nataliia.semenova.musicplayeryoutubeapi.ui.adapter.SongListAdapter;

public class DeviceFragment extends Fragment implements TextWatcher {
    private static final int STORAGE_PERMISSION_CODE = 101;

    private static List<Song> songs;

    private MusicPlayerActivity context;
    private RecyclerView rvSongs;

    public DeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (MusicPlayerActivity) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvSongs = view.findViewById(R.id.rv_song_list);
        if(PermissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE, context)) {
            songs = SongHelper.getSongList(context);
            setSongList(rvSongs, songs);
        } else if (PermissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE, context)) {
            songs = SongHelper.getSongList(context);
            setSongList(rvSongs, songs);
        }
        ((EditText)view.findViewById(R.id.sv_device_songs)).addTextChangedListener(this);
    }

    private void setSongList(RecyclerView rvSongs, List<Song> songs) {
        SongListAdapter songAdapter = new SongListAdapter(context, songs, position -> {
            try {
                ((ISongList)context).fragmentSongList(songs, position);
            } catch (ClassCastException ignored) {}
        });
        rvSongs.setAdapter(songAdapter);
    }

    private void searchSong(String query) {
        if(query.length() == 0) {
            setSongList(rvSongs, songs);
        } else {
            query = query.toLowerCase();
            List<Song> songList = new ArrayList<>();
            for (int i = 0; i < songs.size(); i++) {
                String title = songs.get(i).getTitle().toLowerCase();
                String artist = songs.get(i).getArtist().toLowerCase();
                if (title.contains(query) || artist.contains(query)) {
                    songList.add(songs.get(i));
                }
            }
            setSongList(rvSongs, songList);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        searchSong(charSequence.toString());
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        searchSong(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // Unused method
    }
}