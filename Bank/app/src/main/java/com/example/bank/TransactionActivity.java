package com.example.bank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.example.bank.Adapters.ItemsAdapter;
import com.example.bank.Adapters.TransactionAdapter;
import com.example.bank.Database.DatabaseHelper;
import com.example.bank.Models.Transaction;
import com.example.bank.Models.User;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {

    public static final String TAG ="TransactionActivity";

    private RadioGroup rgType;
    private Button btnSearch;
    private EditText edtTxtMin;
    private RecyclerView transactionRecView;
    private TextView txtNoTrasaction;
    private BottomNavigationView bottomNavigationView;
    private TransactionAdapter adapter;
    private DatabaseHelper databaseHelper;
    private GetTransactions getTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        initView();
        initBottomNavView();
        adapter = new TransactionAdapter();
        transactionRecView.setAdapter(adapter);
        transactionRecView.setLayoutManager(new LinearLayoutManager(this));
       databaseHelper = new DatabaseHelper(this);


       btnSearch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               initSearch();
           }
       });
       rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               initSearch();
           }
       });


    }

    private void initSearch(){

        Utils utils = new Utils(this);
        User user = utils.isUserLoggedIn();
        if(null != user){
            getTransactions = new GetTransactions();
            getTransactions.execute(user.get_id());
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != getTransactions)
        {
            if(!getTransactions.isCancelled())
            {
                getTransactions.cancel(true);
            }
        }
    }

    private class  GetTransactions extends AsyncTask<Integer, Void, ArrayList<Transaction>>{

        private String type = "all";
        private double min = 0.0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.min = Double.valueOf(edtTxtMin.getText().toString());
            switch(rgType.getCheckedRadioButtonId())
            {
                case R.id.rbInvestment:
                    type ="investment";
                    break;
                case R.id.rbLoanPayment:
                    type ="Loan Payment";
                    break;
                case R.id.rbProfits:
                    type ="Profits";
                    break;
                case R.id.rbShopping:
                    type ="Shopping";
                    break;
                case R.id.rbSend:
                    type ="send";
                    break;
                case R.id.rbReceive:
                    type ="Receive";
                    break;
                default:
                    type = "all";
                    break;
            }


        }

        @Override
        protected ArrayList<Transaction> doInBackground(Integer... integers) {
            try{
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor;
                if(type.equals("all")){
                    cursor = db.query("transactions", null, "user_id=?", new String[] {String.valueOf(integers[0])}, null, null,"date DESC");

                }else{
                    cursor = db.query("transactions", null, "type=? AND user_id=?", new String[] {type, String.valueOf(integers[0])}, null ,null,"date DESC");
                }
                if(null != cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Transaction> transactions = new ArrayList<>();
                        for(int i =0; i<cursor.getCount(); i++)
                        {
                            Transaction transaction = new Transaction();
                            transaction.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            transaction.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                            transaction.setType(cursor.getString(cursor.getColumnIndex("type")));
                            transaction.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            transaction.setRecipient(cursor.getString(cursor.getColumnIndex("recipient")));
                            transaction.setDate(cursor.getString(cursor.getColumnIndex("date")));
                            transaction.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));


                            double absoluteAmount = transaction.getAmount();
                            if(absoluteAmount<0)
                            {
                                absoluteAmount = -absoluteAmount;
                            }
                            if(absoluteAmount> this.min)
                            {
                                transactions.add(transaction);
                            }
                            cursor.moveToNext();

                        }
                        cursor.close();
                        db.close();
                        return transactions;

                    }else{
                        cursor.close();
                        db.close();
                        return null;
                    }

                }else{
                    cursor.close();
                    db.close();
                    return null;
                }


            }
            catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Transaction> transactions) {
            super.onPostExecute(transactions);
            if(null != transactions)
            {  txtNoTrasaction.setVisibility(View.GONE);
                adapter.setTransactions(transactions);


            }else{
                txtNoTrasaction.setVisibility(View.VISIBLE);
                adapter.setTransactions(new ArrayList<Transaction>());
            }


        }
    }


    private void initView()
    {
        rgType = (RadioGroup)findViewById(R.id.rgType);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        edtTxtMin = (EditText) findViewById(R.id.edtTxtMin);
        transactionRecView = (RecyclerView)findViewById(R.id.transactionRecView);
        txtNoTrasaction = (TextView) findViewById(R.id.txtNoTransaction);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavView);
    }

    private void initBottomNavView() {
        Log.d(TAG, "initBottomNavView: started");
        bottomNavigationView.setSelectedItemId(R.id.menu_item_transaction);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.menu_item_stats:
                        //TODO: complete this logic
                        break;
                    case R.id.menu_item_transaction:

                        break;
                    case R.id.menu_item_home:
                        Intent homeIntent = new Intent(TransactionActivity.this, MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);

                        break;
                    case R.id.menu_item_loan:
                        Intent loanIntent = new Intent(TransactionActivity.this, LoanActivity.class);
                        loanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loanIntent);
                        break;
                    case R.id.menu_item_investment:
                        Intent intent = new Intent(TransactionActivity.this, InvestmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }
}