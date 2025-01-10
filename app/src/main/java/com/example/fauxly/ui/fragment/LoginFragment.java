package com.example.fauxly.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.fauxly.MainActivity;
import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.utils.FragmentUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentUtils.setFragmentContainerMarginBottom(requireActivity(), true);
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextInputEditText emailEditText = view.findViewById(R.id.emailAddress);
        TextInputEditText passwordEditText = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.registerButton);

        DatabaseRepository repository = new DatabaseRepository(requireContext());

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText() != null ? emailEditText.getText().toString() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString() : "";

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please enter both email and password");
            } else {
                String userId = repository.getUserIdByEmailAndPassword(email, password);
                if (userId != null) {
                    navigateToHome(userId);
                } else {
                    showToast("Invalid email or password");
                }
            }
        });

        registerButton.setOnClickListener(v -> navigateToRegister());

        return view;
    }

    private void navigateToHome(String userId) {
        // Save userId to MainActivity or SharedPreferences
        ((MainActivity) requireActivity()).setUserId(userId);

        // Navigate to HomeFragment
        NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        navController.navigate(R.id.action_loginFragment_to_homeFragment, bundle);
    }



    private void navigateToRegister() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_loginFragment_to_registerFragment);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}