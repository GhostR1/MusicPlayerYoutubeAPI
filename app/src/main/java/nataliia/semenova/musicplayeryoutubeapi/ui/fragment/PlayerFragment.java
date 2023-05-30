package nataliia.semenova.musicplayeryoutubeapi.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


import nataliia.semenova.musicplayeryoutubeapi.R;

import nataliia.semenova.musicplayeryoutubeapi.data.model.device.ISongList;
import nataliia.semenova.musicplayeryoutubeapi.data.model.device.Song;

import nataliia.semenova.musicplayeryoutubeapi.databinding.FragmentPlayerBinding;
import nataliia.semenova.musicplayeryoutubeapi.ui.MusicPlayerActivity;

public class PlayerFragment extends Fragment implements View.OnClickListener {
    public FragmentPlayerBinding binding;

    private static ArrayList<Song> songs = new ArrayList<>();
    private static int position = 0;

    private MusicPlayerActivity context;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            songs = bundle.getParcelableArrayList("songs");
            position = bundle.getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
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
        binding = FragmentPlayerBinding.bind(view);
        Song song = songs.get(position);
        binding.tvSongTitle.setText(song.getTitle());
        binding.tvSongArtist.setText(song.getArtist());
        Glide.with(view.getContext())
                .load(song.getCoverUri())
                .apply(RequestOptions.placeholderOf(R.drawable.song_cover).centerInside())
                .into(binding.ivSongCover);
        binding.btnPlay.setActivated(true);
        binding.btnNext.setOnClickListener(this);
        binding.btnPrevious.setOnClickListener(this);
        binding.btnPlay.setOnClickListener(this);
        binding.ivClosePlayer.setOnClickListener(this);
        binding.sbSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ((ISongList)context).fragmentPlayerDisplayProgress(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((ISongList)context).fragmentPlayerDisplayProgress(true);
                if (position < 0) {
                    seekBar.setProgress(0);
                    return;
                }
                ((ISongList)context).fragmentPlayerSetProgress(seekBar.getProgress());
            }
        });
        ((ISongList)context).fragmentPlayerBinding(binding);
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case (R.id.btn_next):
                    ((ISongList)context).fragmentPlayerNext();
                    break;
                case (R.id.btn_previous):
                    ((ISongList)context).fragmentPlayerPrevious();
                    break;
                case(R.id.btn_play):
                    ((ISongList)context).fragmentPlayerPause();
                    break;
                case(R.id.iv_close_player):
                    context.replaceFragment(new DeviceFragment(), false);
                    break;
            }
        } catch (ClassCastException ignored) {}
    }
}