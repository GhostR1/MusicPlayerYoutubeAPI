package nataliia.semenova.musicplayeryoutubeapi.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.BuildConfig;
import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.api.IYoutubeApi;
import nataliia.semenova.musicplayeryoutubeapi.data.SongHelper;
import nataliia.semenova.musicplayeryoutubeapi.data.model.ISongList;
import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;
import nataliia.semenova.musicplayeryoutubeapi.data.model.youtube.RequestResponse;
import nataliia.semenova.musicplayeryoutubeapi.data.model.youtube.YoutubeVideo;
import nataliia.semenova.musicplayeryoutubeapi.ui.MusicPlayerActivity;
import nataliia.semenova.musicplayeryoutubeapi.ui.adapter.SongListAdapter;
import nataliia.semenova.musicplayeryoutubeapi.ui.adapter.Top10PlaylistAdapter;
import nataliia.semenova.musicplayeryoutubeapi.utils.PermissionHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YoutubeFragment extends Fragment {
    private MusicPlayerActivity context;
    private IYoutubeApi iYoutubeApi;

    private RecyclerView rvTop10Playlist;

    private List<YoutubeVideo> top10List;

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
        getTop10Playlist();
       // ((EditText)view.findViewById(R.id.sv_device_songs)).addTextChangedListener(this);
    }

    private void getTop10Playlist() {
        String query = "Imagine Dragons";
        Call<RequestResponse> call = iYoutubeApi.searchQuery(
                BuildConfig.YOTUBE_DATA_TOKEN, 10, "relevance", "snippet", query, "video"
        );
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                if (response.code() == 200) {
                    if(response.body() != null) {
                        top10List = response.body().getItems();
                        setTop10Playlist(top10List);
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

    private void setTop10Playlist(List<YoutubeVideo> songs) {
        Top10PlaylistAdapter top10PlaylistAdapter = new Top10PlaylistAdapter(context, songs, position -> {
            try {
                //((ISongList)context).fragmentSongList(songs, position);
            } catch (ClassCastException ignored) {}
        });
        rvTop10Playlist.setAdapter(top10PlaylistAdapter);
    }
}