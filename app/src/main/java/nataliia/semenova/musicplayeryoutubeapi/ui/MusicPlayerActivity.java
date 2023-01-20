package nataliia.semenova.musicplayeryoutubeapi.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.data.PermissionHelper;
import nataliia.semenova.musicplayeryoutubeapi.data.SongHelper;
import nataliia.semenova.musicplayeryoutubeapi.data.model.ISongList;
import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;
import nataliia.semenova.musicplayeryoutubeapi.service.SongService;
import nataliia.semenova.musicplayeryoutubeapi.ui.adapter.SongListAdapter;
import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.DeviceFragment;
import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.YoutubeFragment;
import nataliia.semenova.musicplayeryoutubeapi.databinding.ActivityMainBinding;


public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener, ISongList {
    private static final int PHONE_PERMISSION_CODE = 100;
    private final PhoneListener phoneListener = new PhoneListener();

    private ActivityMainBinding binding;

    private boolean isDisplayProgress = true;
    private SongService songService;
    private final PlayerCallback callback = new PlayerCallback();

    private List<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new YoutubeFragment());

        binding.navBottomMenu.setOnItemSelectedListener(item -> {
        switch (item.getItemId()) {
            case (R.id.music):
                replaceFragment(new YoutubeFragment());
                break;
            case (R.id.files):
                replaceFragment(new DeviceFragment());
                break;
        }
            return true;
        });

        binding.navBottomPlayer.sbSongMp
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        isDisplayProgress = false;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        isDisplayProgress = true;
                        int pos = songService.getCurrentPosition();
                        if (pos < 0) {
                            seekBar.setProgress(0);
                            return;
                        }
                        songService.setSongProgress(seekBar.getProgress());
                    }
                });

        binding.navBottomPlayer.btnPreviousMp.setOnClickListener(this);
        binding.navBottomPlayer.btnPlayMp.setOnClickListener(this);
        binding.navBottomPlayer.btnNextMp.setOnClickListener(this);

        PermissionHelper.checkPermission(Manifest.permission.READ_PHONE_STATE,
                PHONE_PERMISSION_CODE, MusicPlayerActivity.this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        if(songs == null) {
            binding.navBottomPlayer.getRoot().setVisibility(View.GONE);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), fragment).commit();
    }

    @Override
    public void fragmentSongList(List<Song> songs, int position) {
        this.songs = songs;
        binding.navBottomPlayer.getRoot().setVisibility(View.VISIBLE);
        songService.setMusic(songs);
        songService.beginSong(position);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SongService.start(this, songConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (songService != null) {
            songService.setCallback(new PlayerCallback());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btn_previous_mp):
                int position;
                position = songService.getCurrentPosition() - 1;
                if (position < 0) {
                    position = songs.size() - 1;
                }
                songService.beginSong(position);
                break;
            case (R.id.btn_play_mp):
                if (songService.isPlaying()) {
                    songService.pauseSong();
                } else {
                    songService.playSong();
                }
                break;
            case (R.id.btn_next_mp):
                playNext();
                break;
        }
    }

    private final ServiceConnection songConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SongService.SongBinder binder = (SongService.SongBinder) service;
            songService = binder.getService();
            songService.setCallback(callback);

            songService.setMusic(songs);
            if (songs != null && songs.size() > 0) {
                int currentPosition = songService.getCurrentPosition();
                if (currentPosition != -1) {
                    callback.onBeginSong(currentPosition);
                    songService.callProgressChange();
                    binding.navBottomPlayer.btnPlayMp.setActivated(songService.isPlaying());
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void playNext() {
        int position = songService.getCurrentPosition() + 1;
        if (position >= songs.size()) {
            position = 0;
        }
        songService.beginSong(position);
    }

    class PlayerCallback implements SongService.ICallback {

        @Override
        public void onBeginSong(int position) {
            binding.navBottomPlayer.tvSongTitleMp
                    .setText(songs.get(position).getTitle());
            binding.navBottomPlayer.tvSongArtistMp
                    .setText(songs.get(position).getPerformer());
            onPlaySong();
        }

        @Override
        public void onProgressChange(int maxProgress, int progress) {
            SeekBar seekBar = binding.navBottomPlayer.sbSongMp;
            seekBar.setMax(maxProgress);
            if (isDisplayProgress) {
                seekBar.setProgress(progress);
            }
        }

        @Override
        public void onDurationChange(int duration) {
            binding.navBottomPlayer.sbSongMp.setMax(duration);
        }

        @Override
        public void onPlaySong() {
            binding.navBottomPlayer.btnPlayMp.setActivated(true);
        }

        @Override
        public void onPauseSong() {
            binding.navBottomPlayer.btnPlayMp.setActivated(false);
        }

        @Override
        public void onFinishSong() {
            onPauseSong();
            playNext();
        }
    }

    public class PhoneListener extends PhoneStateListener {
        private boolean isPause;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    if (songService.isPlaying()) {
                        songService.pauseSong();
                        isPause = true;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (songService != null && isPause) {
                        songService.playSong();
                        isPause = false;
                    }
                    break;
            }
        }
    }
}