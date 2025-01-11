package com.example.fauxly.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.FlashCardItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FlashcardDetailFragment extends Fragment {

    private static final String ARG_FLASHCARD_ID = "flashcardId";
    private static final String ARG_FLASHCARD_NAME = "flashcardName";

    private int flashCardId;
    private String flashCardName;

    private DatabaseRepository repository;
    private List<FlashCardItem> flashCardItems;

    private TextView wordTextView, pronunciationTextView, translationTextView, counterTextView;
    private Button nextButton, prevButton, addCardButton;
    private MaterialButton backButton;
    private int currentIndex = 0;

    public static FlashcardDetailFragment newInstance(int flashCardId, String flashCardName) {
        FlashcardDetailFragment fragment = new FlashcardDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FLASHCARD_ID, flashCardId);
        args.putString(ARG_FLASHCARD_NAME, flashCardName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flashCardId = getArguments().getInt(ARG_FLASHCARD_ID);
            flashCardName = getArguments().getString(ARG_FLASHCARD_NAME);
        }

        repository = new DatabaseRepository(requireContext());
        flashCardItems = repository.getFlashCardItemsByFlashCardId(flashCardId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard_detail, container, false);

        wordTextView = view.findViewById(R.id.word);
        pronunciationTextView = view.findViewById(R.id.pronunciation);
        translationTextView = view.findViewById(R.id.translation);
        counterTextView = view.findViewById(R.id.itemNumber);
        nextButton = view.findViewById(R.id.nextButton);
        prevButton = view.findViewById(R.id.prevButton);
        backButton = view.findViewById(R.id.backButton);
        addCardButton = view.findViewById(R.id.addCardButton);

        addCardButton.setOnClickListener(v -> showAddCardDialog());

        backButton.setOnClickListener(v -> {
            // Navigate to the previous page
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Display first flashcard item
        displayFlashCardItem();

        nextButton.setOnClickListener(v -> {
            if (currentIndex < flashCardItems.size() - 1) {
                currentIndex++;
                displayFlashCardItem();
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayFlashCardItem();
            }
        });

        return view;
    }

    private void showAddCardDialog() {
        // Create a dialog to get input from the user
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Flashcard");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_card_dialog, null);
        builder.setView(dialogView);

        EditText wordInput = dialogView.findViewById(R.id.inputWord);
        EditText pronunciationInput = dialogView.findViewById(R.id.inputPronunciation);
        EditText translationInput = dialogView.findViewById(R.id.inputTranslation);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String word = wordInput.getText().toString().trim();
            String pronunciation = pronunciationInput.getText().toString().trim();
            String translation = translationInput.getText().toString().trim();

            if (!word.isEmpty() && !translation.isEmpty()) {
                // Add the card to the database
                repository.insertFlashCardContent(flashCardId, word, pronunciation, translation, null);
                // Reload the data
                flashCardItems = repository.getFlashCardItemsByFlashCardId(flashCardId);
                displayFlashCardItem();
            } else {
                Toast.makeText(requireContext(), "Word and Translation are required", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void displayFlashCardItem() {
        if (flashCardItems == null || flashCardItems.isEmpty()) {
            // Handle empty state
            wordTextView.setText("No Data Available");
            pronunciationTextView.setText("");
            translationTextView.setText("");
            counterTextView.setText("0 / 0");

            prevButton.setEnabled(false);
            nextButton.setEnabled(false);

            android.util.Log.e("FlashcardDetailFragment", "No items available for this flashcard.");
            return;
        }

        FlashCardItem item = flashCardItems.get(currentIndex);

        // Populate fields
        wordTextView.setText(item.getWord());
        pronunciationTextView.setText(item.getPronunciation());
        translationTextView.setText(item.getTranslation());
        counterTextView.setText((currentIndex + 1) + " / " + flashCardItems.size());

        // Enable/disable navigation buttons
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < flashCardItems.size() - 1);
    }


}
