package com.example.transactguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class CreditTransactionUserActivity<Interpreter> extends AppCompatActivity {

    private EditText etCreditCardNumber;
    private ImageView btnLogout;
    private EditText etAmount;
    private Spinner spinnerMerchants;
    private Button btnSubmitTransaction;

    private DBHelper dbHelper;
    private Interpreter interpreter;
    private static final float MAX_CREDIT_CARD_NUMBER = 999999999.0f; // Replace with your actual max credit card number
    private static final float MAX_AMOUNT = 100000.0f; // Replace with your actual max transaction amount

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_transaction_user);

        etCreditCardNumber = findViewById(R.id.etCreditCardNumber);
        btnLogout = findViewById(R.id.btnLogout);
        etAmount = findViewById(R.id.etAmount);
        spinnerMerchants = findViewById(R.id.spinnerMerchants);
        btnSubmitTransaction = findViewById(R.id.btnSubmitTransaction);

        CreditCardNumberUtils.setCreditCardNumberFilter(etCreditCardNumber);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreditTransactionUserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Populate the spinner with merchant IDs from the database
        List<String> merchantIds = dbHelper.getMerchantIdsFromDatabase();

        // Create an ArrayAdapter using the merchant IDs
        ArrayAdapter<String> merchantAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, merchantIds);

        merchantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMerchants.setAdapter(merchantAdapter);

        // Load the TensorFlow Lite model from the assets folder
        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately, e.g., show an error message to the user
        }

        btnSubmitTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve input values
                String creditCardNumber = etCreditCardNumber.getText().toString();
                String amount = etAmount.getText().toString();
                String selectedMerchant = spinnerMerchants.getSelectedItem().toString();

                // Insert data into the credit transaction table
                long generatedId = dbHelper.insertCreditTransactionData(creditCardNumber, selectedMerchant, Double.parseDouble(amount), "");

                if (generatedId !=-1) {
                    // Data inserted successfully, now send the data to the model
                    sendTransactionDetailsToModel(creditCardNumber, amount, selectedMerchant);
                } else {
                    // Handle insertion failure
                    Toast.makeText(CreditTransactionUserActivity.this, "Failed to insert data into the database", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Load the TensorFlow Lite model from the assets folder
    private MappedByteBuffer loadModelFile() throws IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("C:\\Users\\user\\AndroidStudioProjects\\TransactGuard\\app\\src\\main\\assets\\converted ANNmodel.tflite");
            FileChannel fileChannel = fileInputStream.getChannel();
            long startOffset = fileChannel.position();
            long declaredLength = fileChannel.size();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    // Send credit card details to the model for analysis
    private void sendTransactionDetailsToModel(String creditCardNumber, String amount, String selectedMerchant) {
        // Preprocess input data
        float[] inputData = preprocessInputData(creditCardNumber, amount);

        // Allocate space for the model's output
        float[][] outputData = new float[1][1];  // Modify based on your model's output shape

        // Run inference
        if (interpreter != null) {
            interpreter.run(inputData, outputData);
        } else {
            Log.e("INFERENCE_ERROR", "Interpreter is not initialized");
            return;
        }

        // Post-process the output data if needed
        float modelResponse = postprocessOutputData(outputData);

        // Store the model response in the credit transaction table
        dbHelper.updateModelResponse(Integer.parseInt(creditCardNumber), String.valueOf(modelResponse));

        // Handle the model response as needed
        Log.d("MODEL_RESPONSE", "Model response: " + modelResponse);
    }

    // Preprocess input data
    private float[] preprocessInputData(String creditCardNumber, String amount) {
        // Example: Convert credit card number and amount to a float array
        float[] processedInput = new float[2];

        // Normalize credit card number (replace with actual normalization logic)
        processedInput[0] = normalizeCreditCardNumber(creditCardNumber);

        // Normalize amount (replace with actual normalization logic)
        processedInput[1] = normalizeAmount(amount);

        return processedInput;
    }

    // Normalize credit card number (replace with actual normalization logic)
    private float normalizeCreditCardNumber(String creditCardNumber) {
        // Example: Convert the credit card number to a normalized float value
        // You might need to convert, scale, or apply any specific transformation based on your model
        return Float.parseFloat(creditCardNumber) / MAX_CREDIT_CARD_NUMBER;
    }

    // Normalize amount (replace with actual normalization logic)
    private float normalizeAmount(String amount) {
        // Example: Convert the amount to a normalized float value
        // You might need to convert, scale, or apply any specific transformation based on your model
        return Float.parseFloat(amount) / MAX_AMOUNT;
    }

    // Postprocess output data
    private float postprocessOutputData(float[][] outputData) {
        // Example: Extract and interpret the model's output
        // You might need to perform operations specific to your model's output format

        // For simplicity, assuming a model with a single output value
        return outputData[0][0];
    }

    // Ensure to properly release resources when the activity is destroyed
    @Override
    protected void onDestroy() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
        super.onDestroy();
    }
}
