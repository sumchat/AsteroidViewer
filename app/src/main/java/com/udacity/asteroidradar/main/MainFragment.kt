package com.udacity.asteroidradar.main

import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.displaySnackbar
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.domain.Asteroid
import timber.log.Timber

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun adapterOnClick(asteroid: Asteroid) {

        this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        val _asteroidAdapter = AsteroidListAdapter(AsteroidListAdapter.AsteroidListener{ asteroid -> adapterOnClick(asteroid)})
        binding.asteroidRecycler.adapter = _asteroidAdapter

        viewModel.asteroids.observe(
            viewLifecycleOwner
        ) { asteroids ->
            if (asteroids !== null)
                _asteroidAdapter.submitList(asteroids)
            binding.statusLoadingWheel.visibility = View.GONE
        }

        // Observe daily picture data
        // Load picture by picasso. Bind it to view.
        viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer {
            it?.let { dailyPicture ->
                Timber.i("Observer of dailyPictureData: ${dailyPicture.url}")
                Picasso.get().load(dailyPicture.url).into(binding.activityMainImageOfTheDay)
               binding.activityMainImageOfTheDay.contentDescription = dailyPicture.title
                binding.textView.text = dailyPicture.title

            }
        })

        viewModel.displaySnackbarEvent.observe(viewLifecycleOwner, { displaySnackbarEvent ->
            if (displaySnackbarEvent) {
                displaySnackbar(
                    getString(R.string.error_in_displaying_data),
                    requireView()
                )

            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.view_week_asteroids -> viewModel.onViewWeekAsteroidsClicked()
            R.id.view_today_asteroids -> viewModel.onTodayAsteroidsClicked()
            R.id.view_saved_asteroids -> viewModel.onSavedAsteroidsClicked()
        }
        return true
    }
}
