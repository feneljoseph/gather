package cop_4331c.gather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cop_4331c.gather.R;
import cop_4331c.gather.music.Song;

/**
 * Created by ajariwinfield on 4/1/15.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.PlaylistViewHolder>
{
    private Song[] mSongs;
    private Context mContext;

    public HomeAdapter(Context context, Song[] songs)
    {
        mSongs = songs;
        mContext = context;
    }

    @Override
    public HomeAdapter.PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list_item, parent, false);

        PlaylistViewHolder viewHolder = new PlaylistViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomeAdapter.PlaylistViewHolder holder, int position)
    {
        holder.bindSong(mSongs[position]);
    }

    @Override
    public int getItemCount() {
        return mSongs.length;
    }

    public void refreshWithNewData(Song[] songs)
    {
        mSongs = songs;
        notifyDataSetChanged();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView mAlbumCover;
        public TextView mSongInfoLabel;


        public PlaylistViewHolder(View itemView) {
            super(itemView);

            mAlbumCover = (ImageView) itemView.findViewById(R.id.albumCoverImage);
            mSongInfoLabel = (TextView) itemView.findViewById(R.id.songInfoLabel);

        }


        public void bindSong(Song song)
        {
            Picasso.with(mContext).load(song.getAlbumCoverURL()).into(mAlbumCover);
            mSongInfoLabel.setText( song.getArtist() + " - " + song.getSongName());
        }
    }
}