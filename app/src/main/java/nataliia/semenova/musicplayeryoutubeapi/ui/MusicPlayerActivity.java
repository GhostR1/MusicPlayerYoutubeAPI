package nataliia.semenova.musicplayeryoutubeapi.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.data.model.device.ISongList;
import nataliia.semenova.musicplayeryoutubeapi.data.model.device.Song;
import nataliia.semenova.musicplayeryoutubeapi.data.model.youtube.IYoutubeVideo;
import nataliia.semenova.musicplayeryoutubeapi.data.model.youtube.YoutubeVideo;
import nataliia.semenova.musicplayeryoutubeapi.databinding.FragmentPlayerBinding;
import nataliia.semenova.musicplayeryoutubeapi.service.NotificationServiceConnection;
import nataliia.semenova.musicplayeryoutubeapi.service.SongService;
import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.DeviceFragment;
import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.PlayerFragment;
import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.YoutubeFragment;
import nataliia.semenova.musicplayeryoutubeapi.databinding.ActivityMainBinding;
import nataliia.semenova.musicplayeryoutubeapi.utils.CreateNotification;


public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener, ISongList, IYoutubeVideo, SeekBar.OnSeekBarChangeListener {
    private ActivityMainBinding binding;
    private FragmentPlayerBinding playerBinding = null;

    private boolean isDisplayProgress = true;
    private SongService songService;
    private final PlayerCallback callback = new PlayerCallback();

    private List<Song> songs;

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new YoutubeFragment(), false);

        bindNavBottomActions();

        if(songs == null) {
            binding.navBottomPlayer.getRoot().setVisibility(View.GONE);
        }
    }

    public void replaceFragment(Fragment fragment, boolean isPlayerFragment) {
        playerBinding = null;
        if(isPlayerFragment) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("songs", (ArrayList<? extends Parcelable>) songs);
            bundle.putInt("position", songService.getCurrentPosition());
            fragment.setArguments(bundle);
        } else {
            if (songs != null) {
                binding.navBottomPlayer.getRoot().setVisibility(View.VISIBLE);
            }
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), fragment).commit();
    }

    private void bindNavBottomActions() {
        binding.navBottomMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.music):
                    playerBinding = null;
                    replaceFragment(new YoutubeFragment(), false);
                    break;
                case (R.id.files):
                    playerBinding = null;
                    replaceFragment(new DeviceFragment(), false);
                    break;
            }
            return true;
        });

        binding.navBottomPlayer.sbSongMp.setOnSeekBarChangeListener(this);

        binding.navBottomPlayer.btnPreviousMp.setOnClickListener(this);
        binding.navBottomPlayer.btnPlayMp.setOnClickListener(this);
        binding.navBottomPlayer.btnNextMp.setOnClickListener(this);
        binding.navBottomPlayer.ivSongCoverMp.setOnClickListener(this);
    }

    @Override
    public void fragmentSongList(List<Song> songs, int position) {
        this.songs = songs;
        binding.navBottomPlayer.getRoot().setVisibility(View.VISIBLE);
        binding.navBottomPlayer.tvSongTitleMp.setMovementMethod(new ScrollingMovementMethod());
        binding.navBottomPlayer.tvSongTitleMp.setHorizontallyScrolling(true);
        binding.navBottomPlayer.tvSongArtistMp.setMovementMethod(new ScrollingMovementMethod());
        binding.navBottomPlayer.tvSongArtistMp.setHorizontallyScrolling(true);
        songService.setMusic(songs);
        songService.beginSong(position);
        CreateNotification.createNotification(MusicPlayerActivity.this, songs.get(position), true);
    }

    @Override
    public void fragmentPlayerNext() {
        playNext();
    }

    @Override
    public void fragmentPlayerPause() {
        pausePlay();
    }

    @Override
    public void fragmentPlayerPrevious() {
        playPrevious();
    }

    @Override
    public void fragmentPlayerBinding(FragmentPlayerBinding fragmentPlayerBinding) {
        playerBinding = fragmentPlayerBinding;
        binding.navBottomPlayer.getRoot().setVisibility(View.INVISIBLE);
    }

    @Override
    public void fragmentPlayerDisplayProgress(boolean displayProgress) {
        isDisplayProgress = displayProgress;
    }

    @Override
    public void fragmentPlayerSetProgress(int progress) {
        songService.setSongProgress(progress);
    }


    @Override
    protected void onStart() {
        super.onStart();
        SongService.start(this, songConnection);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), NotificationServiceConnection.class));
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
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
                playPrevious();
                break;
            case (R.id.btn_play_mp):
                pausePlay();
                break;
            case (R.id.btn_next_mp):
                playNext();
                break;
            case (R.id.iv_song_cover_mp):
                replaceFragment(new PlayerFragment(), true);
                break;
        }
    }

    private void pausePlay() {
        if (songService.isPlaying()) {
            songService.pauseSong();
            CreateNotification.createNotification(MusicPlayerActivity.this, songs.get(songService.getCurrentPosition()), false);
        } else {
            songService.playSong();
            CreateNotification.createNotification(MusicPlayerActivity.this, songs.get(songService.getCurrentPosition()), true);
        }
    }

    private void playPrevious() {
        int position;
        position = songService.getCurrentPosition() - 1;
        if (position < 0) {
            position = songs.size() - 1;
        }
        CreateNotification.createNotification(MusicPlayerActivity.this, songs.get(position), true);
        songService.beginSong(position);
    }

    private void playNext() {
        int position = songService.getCurrentPosition() + 1;
        if (position >= songs.size()) {
            position = 0;
        }
        CreateNotification.createNotification(MusicPlayerActivity.this, songs.get(position), true);
        songService.beginSong(position);
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
                    if(playerBinding != null) {
                        playerBinding.btnPlay.setActivated(songService.isPlaying());
                    }
                    binding.navBottomPlayer.btnPlayMp.setActivated(songService.isPlaying());
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

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

    @Override
    public void startVideo(YoutubeVideo video) {
        playerBinding = null;
        songService.pauseSong();
        Intent intent = new Intent(MusicPlayerActivity.this, YoutubePlayerActivity.class);
        intent.putExtra("video", video);
        startActivity(intent);
    }


    class PlayerCallback implements SongService.ICallback {

        @Override
        public void onBeginSong(int position) {
            Song song = songs.get(position);
            binding.navBottomPlayer.tvSongTitleMp.setText(song.getTitle());
            binding.navBottomPlayer.tvSongArtistMp.setText(song.getArtist());
            Glide.with(MusicPlayerActivity.this)
                    .load(song.getCoverUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.song_cover))
                    .into(binding.navBottomPlayer.ivSongCoverMp);
            if(playerBinding != null) {
                playerBinding.tvSongTitle.setText(song.getTitle());
                playerBinding.tvSongArtist.setText(song.getArtist());
                Glide.with(MusicPlayerActivity.this)
                        .load(song.getCoverUri())
                        .apply(RequestOptions.placeholderOf(R.drawable.song_cover))
                        .into(playerBinding.ivSongCover);
                playerBinding.sbSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            }
            onPlaySong();
        }

        @Override
        public void onProgressChange(int maxProgress, int progress) {
            SeekBar seekBar = binding.navBottomPlayer.sbSongMp;
            seekBar.setMax(maxProgress);
            if (isDisplayProgress) {
                seekBar.setProgress(progress);
            }
            if(playerBinding != null) {
                seekBar = playerBinding.sbSong;
                seekBar.setMax(maxProgress);
                if (isDisplayProgress) {
                    seekBar.setProgress(progress);
                }
            }
        }

        @Override
        public void onDurationChange(int duration) {
            binding.navBottomPlayer.sbSongMp.setMax(duration);
            if(playerBinding != null) {
                playerBinding.sbSong.setMax(duration);
            }
        }

        @Override
        public void onPlaySong() {
            binding.navBottomPlayer.btnPlayMp.setActivated(true);
            if(playerBinding != null) {
                playerBinding.btnPlay.setActivated(true);
            }
        }

        @Override
        public void onPauseSong() {

                binding.navBottomPlayer.btnPlayMp.setActivated(false);
            if(playerBinding != null) {
                playerBinding.btnPlay.setActivated(false);
            }
        }

        @Override
        public void onFinishSong() {
            onPauseSong();
            playNext();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("666",
                    "666", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("description");
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action");

            switch (action){
                case CreateNotification.ACTION_PREVIOUS:
                    playPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    pausePlay();
                    break;
                case CreateNotification.ACTION_NEXT:
                    playNext();
                    break;
            }
        }
    };
}