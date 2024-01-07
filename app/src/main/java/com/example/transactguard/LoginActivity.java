package com.example.transactguard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView noAccount, forgotPass;
    Spinner userTypeSpinner;
    DBHelper DB;
    boolean isAdmin;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email =(EditText) findViewById(R.id.inputEmail);
        password=(EditText) findViewById(R.id.inputPassword);
        login=(Button)findViewById(R.id.btn_login);
        userTypeSpinner = (Spinner) findViewById(R.id.userTypeSpinner);
        noAccount=(TextView) findViewById(R.id.textView3);
        forgotPass=(TextView)findViewById(R.id.forgotPass);
        DB= new DBHelper(this);

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
                Toast.makeText(LoginActivity.this, "Selected User Type: " + selectedUserType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case when nothing is selected (if needed)
                Toast.makeText(LoginActivity.this, "Please select a User Type", Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail= email.getText().toString();
                String pass= password.getText().toString();
                String userType = userTypeSpinner.getSelectedItem().toString();

                if (mail.equals("") || pass.equals(""))
                    Toast.makeText(LoginActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else {
                    // Check if the user is admin
                    isAdmin = DB.isAdmin(userType);

                    Boolean checkEmailPassword = DB.checkemailpassword(mail, pass);
                    if (checkEmailPassword) {
                        Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                        Intent intent;

                        if (isAdmin) {
                            // Redirect to admin tab
                            intent = new Intent(getApplicationContext(), AdminApprovalActivity.class);
                        } else {
                            // Redirect to normal user tab
                            intent = new Intent(getApplicationContext(), CreditTransactionUserActivity.class);
                        }

                        startActivity(intent);
                        DBHelper.setLoggedInUserEmail(String.valueOf(email));
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

       noAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }

        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ChangePswdActivity.class);
                startActivity(intent);

            }
        });
    }
}