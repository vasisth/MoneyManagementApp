package com.example.bank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bank.Database.DatabaseHelper;
import com.example.bank.MainActivity;
import com.example.bank.Models.User;
import com.example.bank.R;
import com.example.bank.Utils;
import com.example.bank.WebsiteActivity;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText edtTxtEmail, edtTxtPassword, edtTxtAddress, edtTxtName;
    private TextView txtWarning, txtLogin, txtLicence;
    private ImageView firstImage, secondImage, thirdImage, forthImage, fifthImage;
    private Button btnRegister;

    private String image_url;

    private DatabaseHelper databaseHelper;

    private DoesUserExist doesUserExist;
    private RegisterUser registerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        databaseHelper = new DatabaseHelper(this);

        image_url = "first";
        handleImageUrl();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initRegister();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        txtLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, WebsiteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleImageUrl() {
        Log.d(TAG, "handleImageUrl: started");
        firstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_url = "first";
            }
        });

        secondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_url ="second";
            }
        });

        thirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_url = "third";
            }
        });

        forthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_url = "forth";
            }
        });

        fifthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_url = "fifth";
            }
        });
    }

    private void initRegister() {
        Log.d(TAG, "initRegister: started");
        String email = edtTxtEmail.getText().toString();
        String password = edtTxtPassword.getText().toString();

        if (email.equals("") || password.equals("")) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Please enter the password and Email");
        }else {
            txtWarning.setVisibility(View.GONE);

            doesUserExist = new DoesUserExist();
            doesUserExist.execute(email);

        }
    }

    private class DoesUserExist extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.query("users",new String[] {"_id", "email"}, "email=?",
                new String[] {strings[0]}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        if (cursor.getString(cursor.getColumnIndex("email")).equals(strings[0])) {
                            cursor.close();
                            db.close();
                            return true;
                        }else {
                            cursor.close();
                            db.close();
                            return false;
                        }
                    }else {
                        cursor.close();
                        db.close();
                        return false;
                    }
                }else {
                    db.close();
                    return true;
                }
            }catch (SQLException e) {
                e.printStackTrace();

                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("There is user with this email, please try another email");
            }else {
                txtWarning.setVisibility(View.GONE);
                registerUser = new RegisterUser();
                registerUser.execute();
            }
        }
    }

    private class RegisterUser extends AsyncTask<Void, Void, User>
    {

        private String email;
        private String password;
        private String address;
        private String first_name;
        private String last_name;




        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            String email = edtTxtEmail.getText().toString();
            String password = edtTxtPassword.getText().toString();
            String address = edtTxtAddress.getText().toString();
            String name = edtTxtName.getText().toString();

            this.email = email;
            this.password = password;
            this.address = address;
            //the user can enter their full name and that will be used to find their last name.
            String[] fullName = name.split("" );
            if (fullName.length >= 1) {
                this.first_name = fullName[0];
                for (int i=1; i<fullName.length; i++) {
                    if (i>1) {
                        last_name += " " + fullName[i];
                    }else {
                        last_name += fullName[i];
                    }
                }
            }else {
                this.first_name = fullName[0];
            }

        }





        @Override
        protected User doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("email", this.email);
                values.put("password", this.password);
                values.put("address", this.address);
                values.put("first_name", this.first_name);
                values.put("last_name", this.last_name);
                values.put("remained_amount", 0.0);
                //TODO: select an image from the user and upload it to the server and then add the link to the image url.
                values.put("image_url", image_url);

                long userId = db.insert("users", null, values);
                Log.d(TAG, "doInBackground: userId");

                Cursor cursor = db.query("users", null, "_id=?",
                        new String[] {String.valueOf(userId)}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        User user = new User();
                        user.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                        user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                        user.setFirst_name(cursor.getString(cursor.getColumnIndex("first_name")));
                        user.setLast_name(cursor.getString(cursor.getColumnIndex("last_name")));
                        user.setImage_url(cursor.getString(cursor.getColumnIndex("image_url")));
                        user.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                        user.setRemained_amount(cursor.getDouble(cursor.getColumnIndex("remained_amount")));

                        cursor.close();
                        db.close();
                        return user;
                    }else {
                        cursor.close();
                        db.close();
                        return null;
                    }
                }else {
                    db.close();
                    return null;
                }

            }catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }





        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            if (null != user) {
                Toast.makeText(RegisterActivity.this, "User " + user.getEmail() + " registered successfully", Toast.LENGTH_SHORT).show();
                Utils utils = new Utils(RegisterActivity.this);
                utils.addUserToSharedPreferences(user);
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                Toast.makeText(RegisterActivity.this, "Was not able to register, please try again later", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews: called");

        edtTxtEmail = (EditText) findViewById(R.id.edtTxtEmail);
        edtTxtPassword = (EditText) findViewById(R.id.edtTxtPassword);
        edtTxtAddress = (EditText) findViewById(R.id.edtTxtAddress);
        edtTxtName = (EditText) findViewById(R.id.edtTxtName);

        txtWarning = (TextView) findViewById(R.id.txtWarning);
        txtLogin  = (TextView) findViewById(R.id.txtLogin);
        txtLicence = (TextView) findViewById(R.id.txtLicense);

        firstImage = (ImageView) findViewById(R.id.firstImage);
        secondImage = (ImageView) findViewById(R.id.secondImage);
        thirdImage = (ImageView) findViewById(R.id.thirdImage);

        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != doesUserExist) {
            if (!doesUserExist.isCancelled()) {
                doesUserExist.cancel(true);
            }
        }

        if (null != registerUser) {
            if (!registerUser.isCancelled()) {
                registerUser.cancel(true);
            }
        }
    }
}