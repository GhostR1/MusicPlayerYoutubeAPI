package nataliia.semenova.musicplayeryoutubeapi.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.DeviceFragment;
import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.ui.fragment.YoutubeFragment;
import nataliia.semenova.musicplayeryoutubeapi.databinding.ActivityMainBinding;


public class MusicPlayerActivity extends AppCompatActivity {
    ActivityMainBinding binding;
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
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment).commit();
    }
}