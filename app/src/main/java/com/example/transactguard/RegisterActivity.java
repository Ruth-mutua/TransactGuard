package com.example.transactguard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity {

    EditText username, creditCardNo, email, password, repassword;
    Button register;
    Spinner userTypeSpinner;
    DBHelper DB;
    TextView signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username =(EditText) findViewById(R.id.inputUsername);
        creditCardNo = (EditText) findViewById(R.id.inputCreditCardNo);
        email =(EditText) findViewById(R.id.inputEmail);
        password =(EditText) findViewById(R.id.inputPassword);
        repassword = (EditText) findViewById(R.id.repassword);
        register =(Button) findViewById(R.id.btnRegister);
        userTypeSpinner = (Spinner) findViewById(R.id.userTypeSpinner);
        signin= (TextView) findViewById(R.id.textViewSignup);
        DB = new DBHelper(this);

        // Set up the input filters for the credit card number
        CreditCardNumberUtils.setCreditCardNumberFilter(creditCardNo);


        // Create an ArrayAdapter for the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        // Add the OnItemSelectedListener for the userTypeSpinner
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selection here
                String selectedUserType = userTypeSpinner.getSelectedItem().toString();
                // You can use the selectedUserType for further actions or store it in a variable.
                // For example, if you want to show a Toast message with the selected user type:
                Toast.makeText(RegisterActivity.this, "Selected User Type: " + selectedUserType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case when nothing is selected (if needed)
                Toast.makeText(RegisterActivity.this, "Please select a User Type", Toast.LENGTH_SHORT).show();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String credit_card_number = creditCardNo.getText().toString();
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();
                String userType = userTypeSpinner.getSelectedItem().toString();

                if (user.isEmpty() || userType.isEmpty() || mail.isEmpty() || pass.isEmpty() || repass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    return; // Exit the method if any field is empty
                }

                if (!pass.equals(repass)) {
                    Toast.makeText(RegisterActivity.this, "Password not matching", Toast.LENGTH_SHORT).show();
                    return; // Exit the method if passwords don't match
                }

                // Check if 'pass' is not null before calling the method
                if (pass != null) {
                    Boolean checkemailpassword = DB.checkemailpassword(mail, pass);
                    if (!checkemailpassword) {
                        Boolean insert = DB.insertUserData(user, credit_card_number, mail, pass, repass, userType);
                        if (insert) {
                            // Send a welcome email in the background
                            EmailSender.sendWelcomeEmailInBackground(email.getText().toString(), new EmailSender.EmailCallback() {
                                @Override
                                public void onEmailSent(boolean success) {
                                    if (success) {
                                        // Handle success, if needed
                                        Toast.makeText(RegisterActivity.this, "Welcome email sent successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Handle failure, if needed
                                        Toast.makeText(RegisterActivity.this, "Failed to send welcome email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "User already exists! Please login", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where 'pass' is null
                    Toast.makeText(RegisterActivity.this, "Password is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView signin = findViewById(R.id.textViewSignup);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
}
