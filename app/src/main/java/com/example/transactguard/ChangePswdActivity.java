package com.example.transactguard;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.transactguard.databinding.ActivityChangePswdBinding;

public class ChangePswdActivity extends AppCompatActivity {

        EditText oldPassword, newPassword, confirmPassword;
        Button btnChangePassword;
        ImageView btnLogout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_change_pswd);

            oldPassword = findViewById(R.id.oldPassword);
            btnLogout = (ImageView) findViewById(R.id.btnLogout);
            newPassword = findViewById(R.id.newPassword);
            confirmPassword = findViewById(R.id.confirmPassword);
            btnChangePassword = findViewById(R.id.btnChangePassword);

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChangePswdActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            btnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePassword();
                }
            });
        }

        private void changePassword() {
            String oldPasswordStr = oldPassword.getText().toString();
            String newPasswordStr = newPassword.getText().toString();
            String confirmPasswordStr = confirmPassword.getText().toString();

            if (oldPasswordStr.isEmpty() || newPasswordStr.isEmpty() || confirmPasswordStr.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            } else if (!newPasswordStr.equals(confirmPasswordStr)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Assuming you have a DBHelper class to interact with the database
                // Replace DBHelper with your actual database handling class
                DBHelper dbHelper = new DBHelper(this);

                // Check if the old password is correct
                if (dbHelper.checkPassword(oldPasswordStr)) {
                    // Update the password in the database
                    dbHelper.updatePassword(newPasswordStr);

                    // Notify the user that the password has been changed
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();

                   finish(); // Finish the activity or navigate to another screen
                } else {
                    Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }
