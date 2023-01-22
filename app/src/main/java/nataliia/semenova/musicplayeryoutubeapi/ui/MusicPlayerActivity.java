package nataliia.semenova.musicplayeryoutubeapi.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.data.model.ISongList;
import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;
import nataliia.semenova.musicplayeryoutubeapi.service.NotificationServiceConnection;
import nataliia.semenova.musicplayeryoutubeapi.service.SongService;
import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.DeviceFragment;
import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.YoutubeFragment;
import nataliia.semenova.musicplayeryoutubeapi.databinding.ActivityMainBinding;
import nataliia.semenova.musicplayeryoutubeapi.utils.CreateNotification;


public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener, ISongList {
    private ActivityMainBinding binding;

    private boolean isDisplayProgress = true;
    private SongService songService;
    private final PlayerCallback callback = new PlayerCallback();

    private List<Song> songs;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new YoutubeFragment());

        bindNavBottomActions();

        if(songs == null) {
            binding.navBottomPlayer.getRoot().setVisibility(View.GONE);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), fragment).commit();
    }

    private void bindNavBottomActions() {
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
                    binding.navBottomPlayer.btnPlayMp.setActivated(songService.isPlaying());
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    class PlayerCallback implements SongService.ICallback {

        @Override
        public void onBeginSong(int position) {
            binding.navBottomPlayer.tvSongTitleMp
                    .setText(songs.get(position).getTitle());
            binding.navBottomPlayer.tvSongArtistMp
                    .setText(songs.get(position).getArtist());
            Glide.with(MusicPlayerActivity.this)
                    .load(songs.get(position).getCoverUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.song_cover))
                    .into(binding.navBottomPlayer.ivSongCoverMp);
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
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_PREVIOUS:
                    int position;
                    position = songService.getCurrentPosition() - 1;
                    if (position < 0) {
                        position = songs.size() - 1;
                    }
                    songService.beginSong(position);
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (songService.isPlaying()) {
                        songService.pauseSong();
                        CreateNotification.createNotification(MusicPlayerActivity.this, songs.get(songService.getCurrentPosition()), false);
                    } else {
                        songService.playSong();
                        CreateNotification.createNotification(MusicPlayerActivity.this, songs.get(songService.getCurrentPosition()), true);
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    playNext();
                    break;
            }
        }
    };
}