package com.example.fundimtaa;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.text.Editable; // Add import for Editable
import android.text.TextWatcher; // Add import for TextWatcher
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewApplicants extends AppCompatActivity {

    private RecyclerView recyclerViewApplicants;
    private WorkerAdapter workerAdapter;
    private List<Worker> workerList;

    private FirebaseFirestore db;

    private String jobId; // Job ID received from Intent extra

    private ImageView imageViewFilter;
    private List<String> suggestionsList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applicants);

        // Initialize views

        imageViewFilter = findViewById(R.id.imageViewFilter);

        // Set up search suggestions

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Fetch worker names based on the input text
                fetchWorkerNamesStartingWith(newText.trim());
                return true;
            }
        });
        // Set up filter dialog
        imageViewFilter.setOnClickListener(v -> showFilterDialog());

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the job ID from Intent extra
        jobId = getIntent().getStringExtra("jobId");

        // Initialize RecyclerView
        recyclerViewApplicants = findViewById(R.id.recyclerWorkerViewApplicants);
        recyclerViewApplicants.setHasFixedSize(true);
        recyclerViewApplicants.setLayoutManager(new LinearLayoutManager(this));

        // Initialize worker list
        workerList = new ArrayList<>();

        // Initialize adapter
        workerAdapter = new WorkerAdapter(workerList);

        // Set adapter to RecyclerView
        recyclerViewApplicants.setAdapter(workerAdapter);

        // Load workers for the specified job
        loadWorkers();
    }

    private void showFilterDialog() {
        // Create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Find views in the dialog layout
        TextView textViewName = dialogView.findViewById(R.id.textViewName);
        TextView textViewExperience = dialogView.findViewById(R.id.textViewExperience);
        TextView textViewClose = dialogView.findViewById(R.id.textViewClose);

        // Set up name filter click listener
        textViewName.setOnClickListener(v -> {
            // Sort workers by name in ascending order
            Collections.sort(workerList, (worker1, worker2) -> worker1.getName().compareTo(worker2.getName()));
            // Notify adapter of data change
            workerAdapter.notifyDataSetChanged();
            dialog.dismiss(); // Dismiss the dialog after performing the action
        });

        // Set up experience filter click listener
        textViewExperience.setOnClickListener(v -> {
            // Sort workers by experience in descending order
            Collections.sort(workerList, (worker1, worker2) -> {
                // Parse experience strings into integers
                int experience1 = parseExperience(worker1.getExperience());
                int experience2 = parseExperience(worker2.getExperience());
                // Compare the parsed experience values
                return Integer.compare(experience2, experience1);
            });
            // Notify adapter of data change
            workerAdapter.notifyDataSetChanged();
            dialog.dismiss(); // Dismiss the dialog after performing the action
        });
        textViewClose.setOnClickListener(v -> dialog.dismiss());
        // Show dialog
        dialog.show();
    }
    // Helper method to parse experience strings into integers
    private int parseExperience(String experience) {
        // Remove non-numeric characters and parse the remaining string as an integer
        return Integer.parseInt(experience.replaceAll("[^0-9]", ""));
    }

    private void fetchWorkerNamesStartingWith(String searchText) {
        // Query Firestore to fetch worker names
        db.collection("job_applications")
                .whereEqualTo("jobId", jobId)
                .whereGreaterThanOrEqualTo("name", searchText)
                .whereLessThan("name", searchText + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        workerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String workerId = document.getString("workerId");
                            String name = document.getString("name");
                            String dateOfApplication = document.getString("dateOfApplication");
                            String experience = document.getString("experience");
                            Worker worker = new Worker(workerId, name, dateOfApplication, experience);
                            workerList.add(worker);
                        }
                        workerAdapter.notifyDataSetChanged(); // Notify adapter that data set has changed
                    } else {
                        Toast.makeText(ViewApplicants.this, "Failed to fetch workers: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadWorkers() {
        // Query Firestore to fetch workers for the specified job ID
        db.collection("job_applications")
                .whereEqualTo("jobId", jobId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            workerList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String workerId = document.getString("workerId");
                                String name = document.getString("name");
                                String dateOfApplication = document.getString("dateOfApplication");
                                String experience = document.getString("experience");
                                Worker worker = new Worker(workerId, name, dateOfApplication, experience);
                                workerList.add(worker);
                            }
                            workerAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ViewApplicants.this, "Failed to load workers: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private class WorkerAdapter extends RecyclerView.Adapter<WorkerViewHolder> {

        private List<Worker> workerList;

        public WorkerAdapter(List<Worker> workerList) {
            this.workerList = workerList;
        }

        @Override
        public WorkerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_view_applicants, parent, false);
            return new WorkerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WorkerViewHolder holder, int position) {
            Worker worker = workerList.get(position);
            holder.textViewWorkerName.setText("Name: " + worker.getName());
            holder.textViewDateOfApplication.setText("Applied on: " + worker.getDateOfApplication());
            holder.textViewExperience.setText("Experience: " + worker.getExperience());

            // Set OnClickListener for the "View Profile" button
            holder.buttonViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle view profile button click
                    // Implement the logic to view worker's profile
                    Toast.makeText(ViewApplicants.this, "View profile clicked for worker: " + worker.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            // Set OnClickListener for the "Apply" button
            holder.buttonApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle apply button click
                    // Implement the logic to apply the job to the worker
                    Toast.makeText(ViewApplicants.this, "Apply clicked for worker: " + worker.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return workerList.size();
        }
    }

    private static class WorkerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWorkerName;
        TextView textViewDateOfApplication;
        TextView textViewExperience;
        Button buttonViewProfile;
        Button buttonApply;

        public WorkerViewHolder(View itemView) {
            super(itemView);
            textViewWorkerName = itemView.findViewById(R.id.textViewName);
            textViewDateOfApplication = itemView.findViewById(R.id.textViewDateOfApplication);
            textViewExperience = itemView.findViewById(R.id.textViewExperience);
            buttonViewProfile = itemView.findViewById(R.id.buttonViewProfile);
            buttonApply = itemView.findViewById(R.id.buttonApply);
        }
    }
}
