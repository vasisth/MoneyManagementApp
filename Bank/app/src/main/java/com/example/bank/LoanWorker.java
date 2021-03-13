package com.example.bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.bank.Database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LoanWorker extends Worker {
private static final String TAG = "LoanWorker";
private DatabaseHelper databaseHelper;

    public LoanWorker(@NonNull Context context, @NonNull WorkerParameters workerParams, DatabaseHelper databaseHelper) {
        super(context, workerParams);
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
       Data data = getInputData();
       int loanId = data.getInt("loan_id", -1);
       int userId = data.getInt("user_id", -1);
       double monthlyPayment = data.getDouble("monthly_payment", 0.0);
       String name = data.getString("name");
       if(loanId == -1 || userId == -1 || monthlyPayment == 0.0)
       {
           return Result.failure();
       }
       try{
           SQLiteDatabase db = databaseHelper.getWritableDatabase();
           ContentValues transactionValue = new ContentValues();
           transactionValue.put("amount", -monthlyPayment);
           transactionValue.put("user_id", userId);
           transactionValue.put("type", "loan_payment");
           transactionValue.put("amount", "monthly payment for "+ name+ " Loan");
           transactionValue.put("recipient", name);

           Calendar calendar = Calendar.getInstance();
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
           String date = sdf.format(calendar.getTime());
           transactionValue.put("date",date);
           long transactionId = db.insert("transactions", null, transactionValue);

           if(transactionId == -1)
           {
               return Result.failure();
           }
           else{
               Cursor cursor = db.query("users", new String[] {"remained_amount"}, "_id=?", new String[] {String.valueOf(userId)}, null,null,null);
               if(null != cursor)
               {
                   if(cursor.moveToFirst())
                   {
                       double CurrentRemainedAmount = cursor.getDouble(cursor.getColumnIndex("remained_amount"));
                       ContentValues values = new ContentValues();
                       values.put("remained_amount", CurrentRemainedAmount - monthlyPayment);
                       db.update("users", values, "_id=?", new String[] {String.valueOf(userId)});
                        cursor.close();
                       Cursor secondCursor = db.query("loans", new String[] {"remained_amount"}, "_id=?", new String[] {String.valueOf(loanId)},null,null,null);
                        if(null != secondCursor)
                       {
                           if(secondCursor.moveToFirst())
                           {
                                double currentLoanAmount = secondCursor.getDouble(secondCursor.getColumnIndex("remained_amount"));
                               ContentValues secondvalues = new ContentValues();
                               secondvalues.put("remained_amount", currentLoanAmount - monthlyPayment);
                               db.update("users", secondvalues, "_id=?", new String[] {String.valueOf(loanId)});

                                cursor.close();
                                db.close();
                                return Result.success();


                           }else{
                               secondCursor.close();
                               db.close();
                               return Result.failure();
                           }
                       }else{
                            db.close();
                            return Result.failure();
                        }

                   }else{
                       cursor.close();
                       db.close();
                       return Result.failure();
                   }

               } else{
                   db.close();
                   return Result.failure();


               }
           }


       }catch (SQLException e)
       {
           e.printStackTrace();
           return Result.failure();
       }
    }
}
