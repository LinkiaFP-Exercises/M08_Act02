package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class FailedLoginAdapter extends ArrayAdapter<FailedLogin> {

    public FailedLoginAdapter(Context context, List<FailedLogin> failedLogins) {
        super(context, 0, failedLogins);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        FailedLogin failedLogin = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_failed_login, parent, false);
        }

        TextView usernameTextView = convertView.findViewById(R.id.username);
        TextView passwordTextView = convertView.findViewById(R.id.password);
        TextView dateTimeTextView = convertView.findViewById(R.id.dateTime);

        assert failedLogin != null;
        usernameTextView.setText(failedLogin.username());
        passwordTextView.setText(failedLogin.password());
        dateTimeTextView.setText(failedLogin.dateTime());

        return convertView;
    }
}

