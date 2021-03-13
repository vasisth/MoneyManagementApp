package com.example.bank.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bank.Models.Loan;
import com.example.bank.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder> {
    public static final String TAG = "LoanAdapter";

    private Context context;
    private ArrayList<Loan> loans = new ArrayList<>();

    private TextView name, initDate, finishDate, roi, loss, amount, remained_amount;
    private CardView parent;



    public LoanAdapter(Context context) {
        this.context = context;
    }

    public LoanAdapter() { }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loans, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestmentAdapter.ViewHolder holder, int position) {

            holder.name.setText(loans.get(position).getName());
            holder.initDate.setText(loans.get(position).getInit_date());
            holder.finishDate.setText(loans.get(position).getFinish_date());
            holder.amount.setText(String.valueOf(loans.get(position).getMonthly_payment()));
            holder.roi.setText(String.valueOf(loans.get(position).getMonthly_roi()));
            holder.remained_amount.setText(String.valueOf(loans.get(position).getRemained_amount()));

    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    public void setLoans(ArrayList<Loan> loans) {
        this.loans = loans;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            name = (TextView) itemView.findViewById(R.id.LoanNametxt);
            initDate = (TextView) itemView.findViewById(R.id.InitDatetxt);
            finishDate = (TextView) itemView.findViewById(R.id.finishDatetxt);
            roi = (TextView) itemView.findViewById(R.id.ROItxt);
            amount= (TextView) itemView.findViewById(R.id.amounttxt);
            remained_amount = (TextView) itemView.findViewById(R.id.RemainedAmounttxt);
            parent = (CardView) itemView.findViewById(R.id.parent);

        }
    }


}
