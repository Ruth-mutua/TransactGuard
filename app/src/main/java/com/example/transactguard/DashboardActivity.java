package com.example.transactguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    CardView cardProfile;
    CardView cardTransact;
    CardView cardMessages;
    ImageView btnLogout;

    @Override
    protected  void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_dashboard);
         cardProfile= (CardView) findViewById(R.id.cardProfile);
        cardTransact= (CardView) findViewById(R.id.cardTransact);
        cardMessages= (CardView) findViewById(R.id.cardMessages);
         btnLogout=(ImageView) findViewById(R.id.btnLogout);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



        cardProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent( DashboardActivity.this,ProfileActivity.class);
            }
        });

        cardTransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                DBHelper dbHelper = null;  // Instantiate your DBHelper
                String userType = "Admin";  // Replace this with the actual user type, perhaps fetched from the database

                if (dbHelper.isAdmin(userType)) {
                    // Admin user
                    intent = new Intent(DashboardActivity.this, AdminApprovalActivity.class);
                } else {
                    // Normal user
                    intent = new Intent(DashboardActivity.this, CreditTransactionUserActivity.class);
                }
                startActivity(intent);
            }
        });

        cardMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, MessagesActivity.class);
                startActivity(intent);

            }
        });
    }
}