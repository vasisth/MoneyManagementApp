package com.example.bank.Authentication;

import androidx.core.text.PrecomputedTextCompat;

import com.example.bank.Models.User;

public interface RegisterUser {
    User doInBackground(PrecomputedTextCompat.Params... params);
}
