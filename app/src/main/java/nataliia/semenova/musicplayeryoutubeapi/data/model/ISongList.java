package nataliia.semenova.musicplayeryoutubeapi.data.model;

import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.databinding.FragmentPlayerBinding;

public interface ISongList {
    void fragmentSongList (List<Song> songs, int position);

    void fragmentPlayerNext();

    void fragmentPlayerPause();

    void fragmentPlayerPrevious();

    void fragmentPlayerBinding(FragmentPlayerBinding fragmentPlayerBinding);

    void fragmentPlayerDisplayProgress(boolean displayProgress);

    void fragmentPlayerSetProgress(int progress);
}