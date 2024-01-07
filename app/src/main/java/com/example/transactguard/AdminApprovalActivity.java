package com.example.transactguard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

public class AdminApprovalActivity<TransactionAdapter> extends AppCompatActivity {

    ImageView btnLogout;
    EditText adminActionEditText;

    // RecyclerView, Adapter, and other necessary variables
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<SurfaceControl.Transaction> transactionList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approval);

        btnLogout = (ImageView) findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminApprovalActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get transactions from the backend

        getTransactionsFromModel();

        // Click listeners on RecyclerView items for approval/flagging
        setupClickListeners();
    }

    private List<CreditTransaction> getTransactionsFromModel() {
        // Retrieve transactions from the model response in the database
        DBHelper dbHelper = new DBHelper(this);
        List<CreditTransaction> modelTransactions = dbHelper.getTransactionsFromModel();

        // If transactions are retrieved, update the RecyclerView
        if (modelTransactions != null && !modelTransactions.isEmpty()) {
            // Update the RecyclerView with the transactions from the model response
            transactionAdapter = (TransactionAdapter) modelTransactions;
            recyclerView.setAdapter((RecyclerView.Adapter) transactionAdapter);
        }
        return modelTransactions;
    }

    private void setupClickListeners() {
        // Set up click listeners on RecyclerView items for approval/flagging
        transactionAdapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SurfaceControl.Transaction transaction) {
                // Handle item click (e.g., show details, perform approval/flagging)
                String adminAction = adminActionEditText.getText().toString();

                if ("approved".equalsIgnoreCase(adminAction)) {
                    // Send the message "approved" to the model_response column
                    sendToModelAndUpdateDatabase(transaction, "approved");
                } else if ("flagged".equalsIgnoreCase(adminAction)) {
                    // Send the message "flagged" to the model_response column
                    sendToModelAndUpdateDatabase(transaction, "flagged");
                }

                // Refresh the RecyclerView
                getTransactionsFromModel(); // Fetch transactions again;
            }
        });
    }

    private void sendToModelAndUpdateDatabase(CreditTransaction transaction, String adminAction) {
        // Make API call to send admin action to the backend and update the database
        // Use the credit card number to uniquely identify the row
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.updateModelResponse((int) transaction.getId(), adminAction);
    }


}



