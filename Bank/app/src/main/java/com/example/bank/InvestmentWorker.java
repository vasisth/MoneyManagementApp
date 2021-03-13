package com.example.bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.bank.Database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InvestmentWorker extends Worker {
private static final String TAG = "InvestmentWorker";
    private DatabaseHelper databaseHelper ;


    public InvestmentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        databaseHelper = new DatabaseHelper(context);


    }


    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: called");
        Data data = getInputData();
        double amount = data.getDouble("amount", 0.0);
        String recipient =data.getString("recipient");
        String description = data.getString("description");
        int user_id = data.getInt("user_id", -1);
        String  type = "profit";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(calendar.getTime());

        ContentValues contentValues= new ContentValues();
        contentValues.put("amount",amount);
        contentValues.put("recipient",recipient);
        contentValues.put("description",description);
        contentValues.put("user_id",user_id);
        contentValues.put("type",type);
        contentValues.put("date",date);

        try{
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert("transactions", null, contentValues);

            if(id != -1)
            {
                Cursor cursor = db.query("users", new String[] {"remained_amount"}, "_id=?", new String[]{String.valueOf(user_id)},
                        null, null, null );
                if(null != cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        double currentRemainedAmount = cursor.getDouble(cursor.getColumnIndex("remained_amount"));
                        cursor.close();
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put("remained_amount", currentRemainedAmount-amount);
                        int affectedRows = db.update("users", contentValues1, "_id=?", new String[]{String.valueOf(user_id)});
                        Log.d(TAG, "doInBackground: updatedRows: "+ affectedRows);


                    }else{
                        cursor.close();
                    }
                }
            }
            db.close();
        }catch(SQLException e){
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }
}
