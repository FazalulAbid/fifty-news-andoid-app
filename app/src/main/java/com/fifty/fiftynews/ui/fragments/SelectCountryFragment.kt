package com.fifty.fiftynews.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fifty.fiftynews.R
import com.fifty.fiftynews.databinding.FragmentSelectCountryBinding
import com.fifty.fiftynews.models.Country
import com.fifty.fiftynews.ui.NewsActivity
import com.fifty.fiftynews.ui.UserActivity
import com.fifty.fiftynews.ui.viewmodels.UserViewModel
import com.fifty.fiftynews.util.Constants
import com.fifty.fiftynews.util.UiState

class SelectCountryFragment : Fragment(R.layout.fragment_select_country) {

    private lateinit var binding: FragmentSelectCountryBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var spinnerAdapter: com.fifty.fiftynews.adapters.CountriesSpinnerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectCountryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view model from activity.
        viewModel = (activity as UserActivity).viewModel

        binding.btnSelectCountry.setOnClickListener {
            updateUserProfileData()
        }

        setupSelectCountrySpinner()
    }

    private fun setupSelectCountrySpinner() {
        spinnerAdapter = com.fifty.fiftynews.adapters.CountriesSpinnerAdapter(activity as UserActivity, Constants.COUNTRIES)
        binding.spinnerCountries.adapter = spinnerAdapter
    }

    private fun updateUserProfileData() {
        // Get spinner selected item.
        val position = binding.spinnerCountries.selectedItemPosition
        val selectedItem = binding.spinnerCountries.adapter.getItem(position) as Country
        val user = com.fifty.fiftynews.Session.user.apply {
            countryCode = selectedItem.value
        }

        binding.btnSelectCountry.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        viewModel.updateUserProfileData(user) {
            when (it) {
                is UiState.Failure -> {
                    Toast.makeText(activity, it.error, Toast.LENGTH_LONG).show()
                    binding.btnSelectCountry.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                }
                UiState.Loading -> {
                    binding.btnSelectCountry.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    com.fifty.fiftynews.Session.user = user
                    val intent = Intent(activity, NewsActivity::class.java)
                    startActivity(intent)
                    // Clear all previous activities
                    activity?.finishAffinity()
                }
            }
        }
    }
}