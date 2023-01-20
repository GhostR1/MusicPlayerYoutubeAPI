package nataliia.semenova.musicplayeryoutubeapi.ui.fragment;

import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.data.PermissionHelper;
import nataliia.semenova.musicplayeryoutubeapi.data.SongHelper;
import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;
import nataliia.semenova.musicplayeryoutubeapi.ui.MusicPlayerActivity;
import nataliia.semenova.musicplayeryoutubeapi.ui.adapter.SongListAdapter;

public class DeviceFragment extends Fragment {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int PHONE_PERMISSION_CODE = 100;

    private static List<Song> songs;

    MusicPlayerActivity context;

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
        context = (MusicPlayerActivity) inflater.getContext();
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(PermissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE, context
        )) {
            setSongList(view.findViewById(R.id.rv_song_list));
        } else if (PermissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE, context
        )) {
            setSongList(view.findViewById(R.id.rv_song_list));
        }
/*        PermissionHelper.checkPermission(Manifest.permission.READ_PHONE_STATE,
                PHONE_PERMISSION_CODE,  (MusicPlayerActivity) getActivity());
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);*/
    }

    private void setSongList(RecyclerView rvSongs) {
        songs = SongHelper.getSongList(context);
        SongListAdapter songAdapter = new SongListAdapter(context, songs, new SongListAdapter.Callback() {
            @Override
            public void onSongClick(int position) {
                /*songService.beginSong(position);*/
            }
        });

        rvSongs.setAdapter(songAdapter);
    }
}