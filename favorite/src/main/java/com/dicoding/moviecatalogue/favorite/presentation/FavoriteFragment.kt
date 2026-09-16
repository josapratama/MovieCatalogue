package com.dicoding.moviecatalogue.favorite.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.moviecatalogue.core.ui.MovieAdapter
import com.dicoding.moviecatalogue.favorite.databinding.FragmentFavoriteBinding
import com.dicoding.moviecatalogue.favorite.di.favoriteModule
import com.dicoding.moviecatalogue.presentation.detail.DetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModel()

    // Nullable to break RecyclerView → Adapter → lambda → Fragment leak chain
    private var movieAdapter: MovieAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!moduleLoaded) {
            loadKoinModules(favoriteModule)
            moduleLoaded = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeFavorites()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
            val intent = Intent(requireActivity(), DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.id)
            }
            startActivity(intent)
        }
        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeFavorites() {
        viewModel.favoriteMovies.observe(viewLifecycleOwner) { movies ->
            if (movies.isNullOrEmpty()) {
                showEmpty(true)
            } else {
                showEmpty(false)
                movieAdapter?.submitList(movies)
            }
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvFavorites.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvFavorites.adapter = null
        movieAdapter = null
        _binding = null
    }

    companion object {
        @Volatile
        private var moduleLoaded = false
    }
}
