package vadim.volin.mobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;
import java.util.List;

import vadim.volin.mobile.R;
import vadim.volin.mobile.databinding.MovieItemLayoutBinding;
import vadim.volin.movie_api.entity.Movie;
import vadim.volin.mobile.ui.MovieInfoFragment;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context context;

    private int currentPosition;
    private LinkedList<Movie> initDataSet;


    public MovieAdapter(List<Movie> initDataSet) {
        this.initDataSet = (LinkedList<Movie>) initDataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        MovieItemLayoutBinding movieItemLayoutBinding = MovieItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(movieItemLayoutBinding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Movie currentMovie = initDataSet.get(position);
        holder.movieItemLayoutBinding.movieTitle.setText(currentMovie.getTitle());
        holder.movieItemLayoutBinding.actors.setText(currentMovie.getActors());
        holder.movieItemLayoutBinding.movieImageView.setContentDescription(currentMovie.getTitle());
        Glide.with(context)
                .load(currentMovie.getPoster())
                .error(R.drawable.ic_not_found_film_24)
                .centerCrop()
                .into(holder.movieItemLayoutBinding.movieImageView);
    }

    @Override
    public int getItemCount() {
        return initDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MovieItemLayoutBinding movieItemLayoutBinding;

        public ViewHolder(MovieItemLayoutBinding movieItemLayoutBinding) {
            super(movieItemLayoutBinding.getRoot());
            this.movieItemLayoutBinding = movieItemLayoutBinding;
            this.itemView.setOnClickListener(v -> {
                if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                    return;
                }

                currentPosition = getAdapterPosition();

                Bundle bundle = new Bundle();
                bundle.putSerializable(MovieInfoFragment.EXTRA_MOVIE_DATA, initDataSet.get(currentPosition).getImdbID());

                Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_movieInfoFragment2, bundle);

            });
        }
    }
}