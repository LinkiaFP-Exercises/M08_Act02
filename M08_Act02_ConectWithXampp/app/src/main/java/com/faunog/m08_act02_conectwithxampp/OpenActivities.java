package com.faunog.m08_act02_conectwithxampp;

import android.content.Context;
import android.content.Intent;

public class OpenActivities {

    public static void databaseViewer(Context context) {
        Intent intent = new Intent(context, DatabaseViewer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static void failedAttemptsViewer(Context context) {
        Intent intent = new Intent(context, FailedAttemptsViewer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
