package nataliia.semenova.musicplayeryoutubeapi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.data.model.youtube.YoutubeVideo;

public class TopPlaylistAdapter extends RecyclerView.Adapter<TopPlaylistAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    public List<YoutubeVideo> songs;

    private final Callback callback;

    private final int layoutItemId;

    public TopPlaylistAdapter(Context context, List<YoutubeVideo> songs, int layoutItemId, Callback callback) {
        this.songs = songs;
        this.inflater = LayoutInflater.from(context);
        this.callback = callback;
        this.layoutItemId = layoutItemId;
    }

    @NonNull
    @Override
    public TopPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(layoutItemId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopPlaylistAdapter.ViewHolder holder, int position) {
        YoutubeVideo youtubeVideo = songs.get(position);

        holder.tvTitle.setText(youtubeVideo.getSnippet().getTitle());
        holder.tvArtist.setText(youtubeVideo.getSnippet().getChannelTitle());

        Glide.with(inflater.getContext())
                .load(youtubeVideo.getSnippet().getThumbnails().getMedium().getUrl())
                .apply(RequestOptions.placeholderOf(R.drawable.song_cover).centerInside())
                .into(holder.ivCover);

        holder.itemView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onVideoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvArtist;
        final ImageView ivCover;

        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_song_title_top);
            tvArtist = view.findViewById(R.id.tv_song_artist_top);
            ivCover = view.findViewById(R.id.iv_song_cover_top);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public interface Callback {
        void onVideoClick(int position);
    }
}
