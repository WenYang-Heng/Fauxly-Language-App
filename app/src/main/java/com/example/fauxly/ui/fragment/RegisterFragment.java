package com.example.fauxly.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment {

    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private DatabaseRepository repository;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize views
        nameEditText = view.findViewById(R.id.nameInput);
        emailEditText = view.findViewById(R.id.emailInput);
        passwordEditText = view.findViewById(R.id.passwordInput);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordInput);
        MaterialButton backButton = view.findViewById(R.id.backButton);
        MaterialButton signUpButton = view.findViewById(R.id.signUpButton);

        repository = new DatabaseRepository(requireContext());

        // Back button logic
        backButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack(); // Navigate back to LoginFragment
        });

        // Sign-up button logic
        signUpButton.setOnClickListener(v -> handleRegistration());

        return view;
    }

    private void handleRegistration() {
        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
        String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
        String confirmPassword = confirmPasswordEditText.getText() != null ? confirmPasswordEditText.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            showToast("All fields are required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return;
        }

        if (repository.isEmailExists(email)) {
            showToast("Email is already registered");
            return;
        }

        long userId = repository.insertUser(name, email, password);
        if (userId > 0) {
            showToast("Registration successful");
            navigateToLogin();
        } else {
            showToast("Registration failed");
        }
    }

    private void navigateToLogin() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
