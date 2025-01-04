package com.example.fauxly.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.FlashCard;
import com.example.fauxly.ui.adapter.FlashCardAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FlashCardFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";

    private String userId;
    private RecyclerView recyclerView;
    private ImageButton addButton;
    private MaterialButton backButton;
    private FlashCardAdapter adapter;
    private DatabaseRepository repository;
    private List<FlashCard> flashCardList;

    public static FlashCardFragment newInstance(String userId) {
        FlashCardFragment fragment = new FlashCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
        repository = new DatabaseRepository(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard, container, false);

        recyclerView = view.findViewById(R.id.flashCardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showAddFlashCardDialog());

        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        loadFlashCards();

        return view;
    }

    private void loadFlashCards() {
        // Fetch flashcards for the current user
        flashCardList = repository.getFlashCardsByUserId(Integer.parseInt(userId));

        // Pass correct parameters to adapter's click listener
        adapter = new FlashCardAdapter(
                flashCardList,                      // Passes the list of FlashCard objects
                (flashcardId, flashcardName) -> {   // Lambda matching OnFlashCardClickListener
                    navigateToFlashCardDetail(flashcardId, flashcardName);
                },
                repository                          // DatabaseRepository instance
        );
        recyclerView.setAdapter(adapter);


        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
    }



    private void showAddFlashCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Flashcard");

        // Input field for flashcard name
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Positive button to save flashcard
        builder.setPositiveButton("Add", (dialog, which) -> {
            String flashCardName = input.getText().toString().trim();
            if (!flashCardName.isEmpty()) {
                repository.insertFlashCard(flashCardName, Integer.parseInt(userId));
                loadFlashCards(); // Refresh RecyclerView
            }
        });

        // Negative button to cancel
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void navigateToFlashCardDetail(int flashcardId, String flashcardName) {
        FlashcardDetailFragment detailFragment = FlashcardDetailFragment.newInstance(flashcardId, flashcardName);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, detailFragment)
                .addToBackStack(null)
                .commit();
    }


}
