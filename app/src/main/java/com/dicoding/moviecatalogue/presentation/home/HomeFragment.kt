package com.dicoding.moviecatalogue.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.moviecatalogue.core.ui.MovieAdapter
import com.dicoding.moviecatalogue.core.utils.Resource
import com.dicoding.moviecatalogue.databinding.FragmentHomeBinding
import com.dicoding.moviecatalogue.presentation.detail.DetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()

    // Keep adapter as a nullable field so we can null it out in onDestroyView,
    // breaking the reference chain: RecyclerView → Adapter → click lambda → Fragment
    private var movieAdapter: MovieAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
            val intent = Intent(requireActivity(), DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.id)
            }
            startActivity(intent)
        }

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeMovies() {
        viewModel.movies.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    val movies = resource.data
                    if (movies.isNullOrEmpty()) {
                        showEmpty(true)
                    } else {
                        showEmpty(false)
                        movieAdapter?.submitList(movies)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showEmpty(true)
                    Toast.makeText(
                        requireContext(),
                        resource.message ?: getString(com.dicoding.moviecatalogue.core.R.string.error_loading),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.shimmerLoading.apply {
            if (isLoading) {
                visibility = View.VISIBLE
                startShimmer()
            } else {
                stopShimmer()
                visibility = View.GONE
            }
        }
        binding.rvMovies.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showEmpty(isEmpty: Boolean) {
        binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvMovies.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Null out the adapter before nulling the binding.
        // This breaks the reference chain:
        //   RecyclerView → Adapter → ViewHolder → click lambda → Fragment context
        // Without this, the RecyclerView's RecycledViewPool holds onto the Adapter,
        // which holds the lambda, which captures the Fragment — causing the leak
        // reported by LeakCanary (ZipOfWithArg3ListArray → RecyclerView.Adapter).
        binding.rvMovies.adapter = null
        movieAdapter = null
        _binding = null
    }
}
