package com.example.bank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.AsyncNotedAppOp;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bank.Database.DatabaseHelper;
import com.example.bank.Models.User;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddLoanActivity extends AppCompatActivity {
    private static final String TAG = "It is from the ADDLOANSACTIVITY";
    private EditText edtTextName, edtTxtInitAmount, edtTxtROI, edtTxtInitDate, edtTxtFinishDate, edtTxtMonthlyPayment;
    private Button btnInitDate, btnFinishDate, btnAddLoan;
    private TextView txtWarning;

    private Calendar initCalendar = Calendar.getInstance();
    private Calendar finishCalendar = Calendar.getInstance();

    private  Utils utils;
    private DatabaseHelper databaseHelper;

    private AddTransaactions addTransaactions;
    private AddLoan addLoan;

    private DatePickerDialog.OnDateSetListener initDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            initCalendar.set(Calendar.YEAR, i);
            initCalendar.set(Calendar.MONTH, i1);
            initCalendar.set(Calendar.DAY_OF_MONTH, i2);
            edtTxtInitDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(initCalendar.getTime()));
        }
    };

    private DatePickerDialog.OnDateSetListener finishDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            finishCalendar.set(Calendar.YEAR, i);
            finishCalendar.set(Calendar.MONTH, i1);
            finishCalendar.set(Calendar.DAY_OF_MONTH, i2);
            edtTxtFinishDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(finishCalendar.getTime()));
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loan);

        initViews();
        databaseHelper = new DatabaseHelper(this);
        utils = new Utils(this);

        setOnClickListeners();

    }

    private void setOnClickListeners(){

        btnInitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddLoanActivity.this, initDateSetListener,
                        initCalendar.get(Calendar.YEAR), initCalendar.get(Calendar.MONTH), initCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnFinishDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddLoanActivity.this, finishDateSetListener,
                        finishCalendar.get(Calendar.YEAR), finishCalendar.get(Calendar.MONTH), finishCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnAddLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData())
                {
                    txtWarning.setVisibility(View.GONE);
                    initAdding();
                }else{
                    txtWarning.setVisibility(View.VISIBLE);
                    txtWarning.setText("Please fill all the blanks");
                }
            }
        });
    }

    private void initAdding(){

    User user = utils.isUserLoggedIn();
    if(null != user)
    {
    addTransaactions = new AddTransaactions();
    addTransaactions.execute(user.get_id());


    }

    }

    private class AddTransaactions extends AsyncTask<Integer, Void, Integer>
    {
        private Double amount;
        private String name, date;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.amount = Double.valueOf(edtTxtInitAmount.getText().toString());
            this.name = edtTextName.getText().toString();
            this.date = edtTxtInitDate.getText().toString();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {


            try{
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("amount", amount);
                values.put("recipient", name);
                values.put("date", date);
                values.put("user_id", integers[0]);
                values.put("description","Received amount for "+ name+ " Loan.");
                values.put("type", "loan");
                long trasactionId = db.insert("transactions",null, values);
                db.close();
                return (int) trasactionId;
            }

            catch (SQLException e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(null != integer)
            {

                addLoan = new AddLoan();
                addLoan.execute(integer);
            }

        }
    }

    private class AddLoan extends AsyncTask<Integer, Void, Integer>{

        private int userId;
        private String name, initDate, finishDate;
        private Double initAmount, monthlyROI, monthlyPayment;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.initAmount = Double.valueOf(edtTxtInitAmount.getText().toString());
            this.name = edtTextName.getText().toString();
            this.initDate = edtTxtInitDate.getText().toString();
            this.finishDate = edtTxtFinishDate.getText().toString();
            this.monthlyROI = Double.valueOf(edtTxtROI.getText().toString());
            this.monthlyPayment= Double.valueOf(edtTxtMonthlyPayment.getText().toString());
            User user = utils.isUserLoggedIn();
            if(null != user)
            {
                this.userId = user.get_id();

            }
            else{
                this.userId =-1;
            }
        }

        @Override
        protected Integer doInBackground(Integer... integers) {

            if(userId != -1)
            {
                try{

                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("name", name);
                    values.put("init_date", initDate);
                    values.put("finish_date", finishDate);
                    values.put("init_amount", initAmount);
                    values.put("remained_amount", initAmount);
                    values.put("monthly_roi", monthlyROI);
                    values.put("monthly_payment", monthlyPayment);
                    values.put("user_id", userId);
                    values.put("transaction_id", integers[0]);

                 long loanId  = db.insert("loans", null, values);

                 if(loanId != -1) {

                    Cursor cursor = db.query("users", new String[] {"remained_amount"},"id=?", new String[]{String.valueOf(userId)},null,null,null);
                    if(null != cursor)
                    {
                        if(cursor.moveToFirst())
                        {
                            double currentRemainingAmount = cursor.getDouble(cursor.getColumnIndex("remained_amount"));
                            ContentValues newvalues = new ContentValues();

                            newvalues.put("remained_amount", currentRemainingAmount+ initAmount);
                            db.update("users", newvalues, "_id=?", new String[] {String.valueOf(userId)});
                            cursor.close();
                            db.close();
                            return (int) loanId;

                        }else{
                            cursor.close();
                            db.close();
                            return null;

                        }

                    }
                    else{
                        db.close();
                        cursor.close();
                        return null;

                    }

                 }
                else{
                    db.close();
                    return null;
                 }


                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
            else{
                return null;
            }

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(null != integer)
            {Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date initDate = sdf.parse(this.initDate);
                    calendar.setTime(initDate);
                    int initMonth = calendar.get(Calendar.YEAR)*12 + calendar.get(Calendar.MONTH);


                    Date finishDate = sdf.parse(this.finishDate);
                    calendar.setTime(finishDate);
                    int finishMonth = calendar.get(Calendar.YEAR)*12 + calendar.get(Calendar.MONTH);

                    int months = finishMonth - initMonth;

                    int days =0;
                    for(int i=0; i<months; i++)
                    {
                        days += 30;
                        Data data = new Data.Builder()
                                .putInt("loan_id",integer)
                                .putInt("user_id",userId)
                                .putDouble("monthly_payment", monthlyPayment)
                                .putString("name", name)
                                .build();
                        Constraints constraints = new Constraints.Builder()
                                .setRequiresBatteryNotLow(true)
                                .build();

                        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(LoanWorker.class)
                                .setInputData(data)
                                .setConstraints(constraints)
                                .addTag("loan_payment")
                                .build();
                        WorkManager.getInstance(AddLoanActivity.this).enqueue(request);

                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    }



    private boolean validateData(){
        if(edtTextName.getText().toString().equals(""))
        {
            return false;
        }
        if(edtTxtInitDate.getText().toString().equals(""))
        {
            return false;
        }
        if(edtTxtFinishDate.getText().toString().equals(""))
        {
            return false;
        }
        if(edtTxtROI.getText().toString().equals(""))
        {
            return false;
        }
        if(edtTxtInitAmount.getText().toString().equals(""))
        {
            return false;
        }

        if(edtTxtMonthlyPayment.getText().toString().equals(""))
        {
            return false;
        }
        return true;
    }


    private void initViews(){

        edtTextName = (EditText) findViewById(R.id.edtTxtName);
        edtTxtInitAmount = (EditText) findViewById(R.id.edtTxtInitAmount);
        edtTxtROI = (EditText) findViewById(R.id.edtTxtROI);
        edtTxtInitDate = (EditText) findViewById(R.id.edtTxtInitDate);
        edtTxtFinishDate = (EditText) findViewById(R.id.edtTxtFinishDate);
        edtTxtMonthlyPayment = (EditText) findViewById(R.id.edtTxtMonthlyPayment);

        btnAddLoan = (Button) findViewById(R.id.btnAddLoan);
        btnInitDate = (Button) findViewById(R.id.btnInitDate);
        btnFinishDate = (Button) findViewById(R.id.btnFinishDate);

        txtWarning = (TextView) findViewById(R.id.txtWarning);



    }
}