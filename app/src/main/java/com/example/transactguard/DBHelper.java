package com.example.transactguard;

import static com.example.transactguard.KeyGenerator.generateRandomKey;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TransactGuard.db";
    private static final int DB_VERSION = 1;
    private static final String secretKey = generateRandomKey(128);

    private static String loggedInUserEmail;
    private String user;
    private String credit_card_number;
    private String mail;
    private String pass;
    private String userType;
    private String repass;

    public static void setLoggedInUserEmail(String email) {
        loggedInUserEmail = email;
    }

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_MERCHANTS = "merchants";
    private static final String TABLE_CREDIT_TRANSACTIONS = "credit_transactions";

    // Common column names
    private static final String COLUMN_ID = "id";

    // Users table columns
    private static final String COLUMN_FULLNAME = "user";
    private static final String COLUMN_CARD_NUMBER = "credit_card_number";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_REPASS="repass";
    private static final String COLUMN_USER_TYPE = "user_type"; // Add this for user type

    // Merchants table columns
    private static final String COLUMN_MERCHANT_NAME = "merchant_name";
    private static final String COLUMN_MERCHANT_ID = "merchant_id";
    private static final String COLUMN_AMOUNT_PAID = "amount_paid";

    // Credit Transactions table columns
    private static final String COLUMN_CREDIT_CARD_NUMBER = "credit_card_number";
    private static final String COLUMN_MERCHANT_ID_PAID_TO = "merchant_id_paid_to";
    private static final String COLUMN_TRANSACTION_AMOUNT = "transaction_amount";
    private static final String COLUMN_MODEL_RESPONSE = "model_response";
    private static final String COLUMN_TRANSACTION_STATUS = "transaction_status";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FULLNAME + " TEXT, "
                + COLUMN_CARD_NUMBER + " NUMBER, "
                + COLUMN_EMAIL + " EMAIL, "
                + COLUMN_PASSWORD + " PASSWORD, "
                + COLUMN_REPASS + "PASSWORD,"
                + COLUMN_USER_TYPE + " TEXT)";
        db.execSQL(createUsersTable);

        // Create Merchants table
        String createMerchantsTable = "CREATE TABLE " + TABLE_MERCHANTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MERCHANT_NAME + " TEXT, "
                + COLUMN_MERCHANT_ID + " TEXT, "
                + COLUMN_AMOUNT_PAID + " REAL)";
        db.execSQL(createMerchantsTable);

        // Insert initial data for 5 merchants
        String[] merchantNames = {"ABC Electronics", "XYZ Mart", "Tech Haven", "Fashion Emporium", "Super Grocery"};
        String[] merchantIds = {"M12345", "M67890", "M23456", "M78901", "M34567"};
        double[] amountsPaid = {0.0, 0.0, 0.0, 0.0, 0.0};

        for (int i = 0; i < merchantNames.length; i++) {
            insertMerchantData(db, merchantNames[i], merchantIds[i], amountsPaid[i]);
        }

        // Create Credit Transactions table
        String createCreditTransactionsTable = "CREATE TABLE " + TABLE_CREDIT_TRANSACTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CREDIT_CARD_NUMBER + " NUMBER, "
                + COLUMN_MERCHANT_ID_PAID_TO + " NUMBER, "
                + COLUMN_TRANSACTION_AMOUNT + " REAL, "
                + COLUMN_MODEL_RESPONSE + " TEXT ,"
                + COLUMN_TRANSACTION_STATUS + " TEXT)";

        db.execSQL(createCreditTransactionsTable);
    }

    private void insertMerchantData(SQLiteDatabase db, String merchantName, String merchantId, double amountPaid) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MERCHANT_NAME, merchantName);
        cv.put(COLUMN_MERCHANT_ID, merchantId);
        cv.put(COLUMN_AMOUNT_PAID, amountPaid);
        db.insert(TABLE_MERCHANTS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MERCHANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDIT_TRANSACTIONS);

        // Recreate the tables
        onCreate(db);
    }

    // Add methods for inserting, querying, and updating data as needed for each table

    // Example: Insert user data
    public boolean insertUserData(String user, String credit_card_number, String mail, String pass, String repass, String userType) {
        this.user = user;
        this.credit_card_number = credit_card_number;
        this.mail = mail;
        this.pass = pass;
        this.userType = userType;
        this.repass = repass;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FULLNAME, user);

        // Encrypt the credit card number before storing it
        MyAES myAES = new MyAES();
        String encryptedCreditCardNumber = myAES.encrypt( credit_card_number, secretKey);
        cv.put(COLUMN_CARD_NUMBER, encryptedCreditCardNumber);

        cv.put(COLUMN_EMAIL, mail);

        // Encrypt the password before storing it
        String encryptedPassword = myAES.encrypt(pass, secretKey);
        cv.put(COLUMN_PASSWORD, encryptedPassword);

        cv.put(COLUMN_USER_TYPE, userType);

        long result = db.insert(TABLE_USERS, null, cv);
        db.close(); // Close the database properly

        return result != -1;
    }


    // Example: Query users by email

    public Boolean checkemailpassword(String email, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        MyAES myAES = new MyAES();

        // Encrypt the password for comparison
        String encryptedPassword = myAES.encrypt(pass, secretKey);

        Cursor cursor = null;

        try {
            // Add null checks before executing the query
            if (email != null && encryptedPassword != null) {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                        new String[]{email, encryptedPassword});

                return cursor != null && cursor.getCount() > 0;
            } else {
                // Handle the case where email or encryptedPassword is null
                return false;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // Add similar methods for other tables
    // Example: Insert merchant data
    public boolean insertMerchantData(String merchantName, String merchantId, double amountPaid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MERCHANT_NAME, merchantName);
        cv.put(COLUMN_MERCHANT_ID, merchantId);
        cv.put(COLUMN_AMOUNT_PAID, amountPaid);

        long result = db.insert(TABLE_MERCHANTS, null, cv);
        db.close(); // Close the database properly

        return result != -1;
    }

    // Add this method in your DBHelper class
    public List<String> getMerchantIdsFromDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> merchantIds = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_MERCHANT_ID + " FROM " + TABLE_MERCHANTS, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String merchantId = cursor.getString(cursor.getColumnIndex(COLUMN_MERCHANT_ID));
                merchantIds.add(merchantId);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return merchantIds;
    }


    // Example: Insert credit transaction data
    public boolean insertCreditTransactionData(String creditCardNumber, String merchantId, double transactionAmount, String modelResponse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Encrypt the credit card number before storing it
        MyAES myAES = new MyAES();
        String encryptedCreditCardNumber = myAES.encrypt(creditCardNumber, secretKey);
        cv.put(COLUMN_CREDIT_CARD_NUMBER, encryptedCreditCardNumber);

        cv.put(COLUMN_MERCHANT_ID, merchantId);
        cv.put(COLUMN_TRANSACTION_AMOUNT, transactionAmount);
        cv.put(COLUMN_MODEL_RESPONSE, modelResponse);
        cv.put(COLUMN_TRANSACTION_STATUS, modelResponse);

        long result = db.insert(TABLE_CREDIT_TRANSACTIONS, null, cv);
        db.close(); // Close the database properly

        return result != -1;
    }

    // Example: Get credit transactions for a specific user
    public Cursor getCreditTransactionsForUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CREDIT_TRANSACTIONS +
                " WHERE " + COLUMN_CREDIT_CARD_NUMBER + " IN (SELECT " + COLUMN_CREDIT_CARD_NUMBER +
                " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?)";
        return db.rawQuery(query, new String[]{email});
    }

    public boolean checkPassword(String oldPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        MyAES myAES = new MyAES();
        String encryptedOldPassword = myAES.encrypt(oldPassword, secretKey);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{loggedInUserEmail, encryptedOldPassword});

        return cursor.getCount() > 0;
    }

    public void updatePassword(String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        MyAES myAES = new MyAES();
        String encryptedNewPassword = myAES.encrypt(newPassword, secretKey);

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, encryptedNewPassword);

        db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{loggedInUserEmail});
    }

    // Method to get user information by email
    public UserInfo getUserInfoByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});

        UserInfo userInfo = null;

        if (cursor.moveToFirst()) {
            // Assuming the columns in your database match the UserInfo constructor parameters
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_FULLNAME));
            @SuppressLint("Range") String cardNumber = cursor.getString(cursor.getColumnIndex(COLUMN_CREDIT_CARD_NUMBER));

            userInfo = new UserInfo(name, email, cardNumber);
        }
        cursor.close();
        return userInfo;
    }


    public void updateTransactionStatus(String transactionId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRANSACTION_STATUS, status);

        db.update(TABLE_CREDIT_TRANSACTIONS, cv, COLUMN_ID + " = ?", new String[]{transactionId});
    }

    // Method to get credit card number by transaction ID
    @SuppressLint("Range")
    public String getCreditCardNumber(String transactionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CREDIT_CARD_NUMBER +
                " FROM " + TABLE_CREDIT_TRANSACTIONS +
                " WHERE " + COLUMN_ID + " = ?", new String[]{transactionId});

        String creditCardNumber = null;

        if (cursor.moveToFirst()) {
            creditCardNumber = cursor.getString(cursor.getColumnIndex(COLUMN_CREDIT_CARD_NUMBER));
        }

        cursor.close();
        return creditCardNumber;
    }

    // Method to get merchant ID paid to by transaction ID
    @SuppressLint("Range")
    public String getMerchantIDPaidTo(String transactionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_MERCHANT_ID_PAID_TO +
                " FROM " + TABLE_CREDIT_TRANSACTIONS +
                " WHERE " + COLUMN_ID + " = ?", new String[]{transactionId});

        String merchantId = null;

        if (cursor.moveToFirst()) {
            merchantId = cursor.getString(cursor.getColumnIndex(COLUMN_MERCHANT_ID_PAID_TO));
        }

        cursor.close();
        return merchantId;
    }

    // Method to get amount paid by transaction ID
    @SuppressLint("Range")
    public double getAmountPaid(String transactionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TRANSACTION_AMOUNT +
                " FROM " + TABLE_CREDIT_TRANSACTIONS +
                " WHERE " + COLUMN_ID + " = ?", new String[]{transactionId});

        double amountPaid = 0.0;

        if (cursor.moveToFirst()) {
            amountPaid = cursor.getDouble(cursor.getColumnIndex(COLUMN_TRANSACTION_AMOUNT));
        }

        cursor.close();
        return amountPaid;
    }

    // Method to get transaction status by transaction ID
    @SuppressLint("Range")
    public String getTransactionStatus(String transactionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TRANSACTION_STATUS +
                " FROM " + TABLE_CREDIT_TRANSACTIONS +
                " WHERE " + COLUMN_ID + " = ?", new String[]{transactionId});

        String transactionStatus = null;

        if (cursor.moveToFirst()) {
            transactionStatus = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_STATUS));
        }

        cursor.close();
        return transactionStatus;
    }


    public boolean isAdmin(String userType) {
        return "Admin".equals(userType);
    }

    // Method to update the model_response column based on the transaction ID
    public void updateModelResponse(int transactionId, String modelResponse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MODEL_RESPONSE, modelResponse);

        // Update the model_response column for the specified transaction ID
        db.update(TABLE_CREDIT_TRANSACTIONS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(transactionId)});

        // Close the database properly
        db.close();
    }

    public List<CreditTransaction> getTransactionsFromModel() {
        // Retrieve transactions from the model response in the database
        // Example: SELECT * FROM TABLE_CREDIT_TRANSACTIONS;
        List<CreditTransaction> transactions = new ArrayList<>();

        // Populate transactions from the cursor

        return transactions;
    }


    // ... (Other methods)

}

