package nataliia.semenova.musicplayeryoutubeapi.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.BuildConfig;
import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.api.IYoutubeApi;
import nataliia.semenova.musicplayeryoutubeapi.data.SongHelper;
import nataliia.semenova.musicplayeryoutubeapi.data.model.ISongList;
import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;
import nataliia.semenova.musicplayeryoutubeapi.data.model.youtube.IYoutubeVideo;
import nataliia.semenova.musicplayeryoutubeapi.data.model.youtube.RequestResponse;
import nataliia.semenova.musicplayeryoutubeapi.data.model.youtube.YoutubeVideo;
import nataliia.semenova.musicplayeryoutubeapi.ui.MusicPlayerActivity;
import nataliia.semenova.musicplayeryoutubeapi.ui.adapter.SongListAdapter;
import nataliia.semenova.musicplayeryoutubeapi.ui.adapter.Top100PlaylistAdapter;
import nataliia.semenova.musicplayeryoutubeapi.ui.adapter.Top10PlaylistAdapter;
import nataliia.semenova.musicplayeryoutubeapi.utils.PermissionHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YoutubeFragment extends Fragment implements EditText.OnEditorActionListener {
    private MusicPlayerActivity context;
    private IYoutubeApi iYoutubeApi;

    private RecyclerView rvTop10Playlist;
    private RecyclerView rvTop100Playlist;

    private final static String PLAYLIST_NAME = "Imagine Dragons";

    public YoutubeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_YOUTUBE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        iYoutubeApi = retrofit.create(IYoutubeApi.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_youtube, container, false);
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
        rvTop10Playlist = view.findViewById(R.id.rv_top_10_playlist);
        rvTop100Playlist = view.findViewById(R.id.rv_top_100_playlist);
        getTopPlaylist(PLAYLIST_NAME, 10);
        getTopPlaylist(PLAYLIST_NAME, 100);
        ((EditText)view.findViewById(R.id.sv_youtube_songs)).setOnEditorActionListener(this);
    }

    private void getTopPlaylist(String query, int maxResults) {
        Call<RequestResponse> call = iYoutubeApi.searchQuery(
                BuildConfig.YOTUBE_DATA_TOKEN, maxResults, "relevance", "snippet", query, "video"
        );
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                if (response.code() == 200) {
                    if(response.body() != null) {
                        if(maxResults == 10) {
                            setTopPlaylist(response.body().getItems(), rvTop10Playlist);
                        } else if(maxResults == 50) {
                            setSearchList(response.body().getItems());
                        } else {
                            setTopPlaylist(response.body().getItems(), rvTop100Playlist);
                        }
                    }
                } else {
                    Toast.makeText(context, getString(R.string.error_happened), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                Toast.makeText(context, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchSong(String query) {
        if(query.length() == 0) {
            setSearchList(null);
        } else {
            query = query.toLowerCase();
            getTopPlaylist(query, 50);
        }
    }

    private void setSearchList(ArrayList<YoutubeVideo> songs) {
        if(songs == null) {
            rvTop10Playlist.setVisibility(View.VISIBLE);
            context.findViewById(R.id.tv_top_10_playlist).setVisibility(View.VISIBLE);
            context.findViewById(R.id.tv_top_100_playlist).setVisibility(View.VISIBLE);
        } else {
            rvTop10Playlist.setVisibility(View.GONE);
            context.findViewById(R.id.tv_top_10_playlist).setVisibility(View.GONE);
            context.findViewById(R.id.tv_top_100_playlist).setVisibility(View.GONE);
            Top100PlaylistAdapter top100PlaylistAdapter = new Top100PlaylistAdapter(context, songs, position -> {
                try {
                    ((IYoutubeVideo)context).startVideo(songs.get(position));
                } catch (ClassCastException ignored) {}
            });
            rvTop100Playlist.setAdapter(top100PlaylistAdapter);
        }
    }

    private void setTopPlaylist(List<YoutubeVideo> songs, RecyclerView recyclerView) {
        switch (recyclerView.getId()) {
            case (R.id.rv_top_10_playlist):
                Top10PlaylistAdapter top10PlaylistAdapter = new Top10PlaylistAdapter(context, songs, position -> {
                    try {
                        ((IYoutubeVideo)context).startVideo(songs.get(position));
                    } catch (ClassCastException ignored) {}
                });
                rvTop10Playlist.setAdapter(top10PlaylistAdapter);
                break;
            case (R.id.rv_top_100_playlist):
                Top100PlaylistAdapter top100PlaylistAdapter = new Top100PlaylistAdapter(context, songs, position -> {
                    try {
                        ((IYoutubeVideo)context).startVideo(songs.get(position));
                    } catch (ClassCastException ignored) {}
                });
                rvTop100Playlist.setAdapter(top100PlaylistAdapter);
                break;
        }

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            if (keyEvent == null || !keyEvent.isShiftPressed()) {
                searchSong(textView.getText().toString());
                return true;
            }
        }
        return false;
    }
}