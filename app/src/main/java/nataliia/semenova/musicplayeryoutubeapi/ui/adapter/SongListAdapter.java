package nataliia.semenova.musicplayeryoutubeapi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    public List<Song> songs;

    private Callback callback;

    public SongListAdapter(Context context, List<Song> songs, Callback callback) {
        this.songs = songs;
        this.inflater = LayoutInflater.from(context);
        this.callback = callback;
    }

    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_song_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongListAdapter.ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        Glide.with(inflater.getContext())
                .load(songs.get(position)
                .getCoverUri())
                .apply(RequestOptions.placeholderOf(R.drawable.song_cover).centerInside())
                .into(holder.ivCover);

        holder.itemView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onSongClick(position);
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
            tvTitle = view.findViewById(R.id.tv_song_title);
            tvArtist = view.findViewById(R.id.tv_song_artist);
            ivCover = view.findViewById(R.id.iv_song_cover);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public interface Callback {
        void onSongClick(int position);
    }
}

