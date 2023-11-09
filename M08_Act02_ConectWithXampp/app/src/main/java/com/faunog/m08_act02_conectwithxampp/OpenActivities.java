package com.faunog.m08_act02_conectwithxampp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.widget.Toolbar;

public class OpenActivities {

    public static void databaseViewer(Context context) {
        ifActivityFinish(context);
        Intent intent = new Intent(context, DatabaseViewer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void failedAttemptsViewer(Context context) {
        ifActivityFinish(context);
        Intent intent = new Intent(context, FailedAttemptsViewer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void mainViewer(Context context) {
        ifActivityFinish(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void ifActivityFinish(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.finish();
        }
    }

    public static void toolbarGoToMainViewer(Toolbar toolbar, Context context) {
        toolbar.setNavigationOnClickListener(v -> mainViewer(context));
    }
}
