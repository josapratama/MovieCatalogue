package com.dicoding.moviecatalogue.presentation.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.dicoding.moviecatalogue.R
import com.dicoding.moviecatalogue.core.ui.MovieItem
import com.dicoding.moviecatalogue.core.utils.Resource
import com.dicoding.moviecatalogue.databinding.ActivityDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModel()

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1)
        if (movieId == -1) {
            finish()
            return
        }

        observeMovieDetail(movieId)
        observeDomainMovie()
    }

    private fun observeMovieDetail(movieId: Int) {
        viewModel.getMovieDetail(movieId).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { populateDetail(it) }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeDomainMovie() {
        viewModel.domainMovie.observe(this) { movie ->
            movie?.let {
                updateFavoriteButton(it.isFavorite)
            }
        }
    }

    private fun populateDetail(movie: MovieItem) {
        with(binding) {
            collapsingToolbar.title = movie.title
            tvTitle.text = movie.title
            tvOverview.text = movie.overview
            tvRating.text = movie.rating
            tvVoteCount.text = movie.voteCount
            tvReleaseDate.text = movie.releaseDate
            tvLanguage.text = movie.originalLanguage

            ivBackdrop.load(movie.backdropUrl) {
                crossfade(true)
                placeholder(com.dicoding.moviecatalogue.core.R.drawable.ic_placeholder)
                error(com.dicoding.moviecatalogue.core.R.drawable.ic_placeholder)
            }
            ivPoster.load(movie.posterUrl) {
                crossfade(true)
                placeholder(com.dicoding.moviecatalogue.core.R.drawable.ic_placeholder)
                error(com.dicoding.moviecatalogue.core.R.drawable.ic_placeholder)
            }

            updateFavoriteButton(movie.isFavorite)

            fabFavorite.setOnClickListener {
                val currentFavorite = viewModel.domainMovie.value?.isFavorite ?: movie.isFavorite
                val newFavoriteState = !currentFavorite
                viewModel.setFavoriteMovie(newFavoriteState)
                val msg = if (newFavoriteState)
                    getString(com.dicoding.moviecatalogue.core.R.string.added_to_favorite)
                else
                    getString(com.dicoding.moviecatalogue.core.R.string.removed_from_favorite)
                Toast.makeText(this@DetailActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val icon = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, icon))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
