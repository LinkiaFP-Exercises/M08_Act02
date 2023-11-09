package com.faunog.m08_act02_conectwithxampp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.widget.Toolbar;

/**
 * Clase que gestiona la apertura de diferentes actividades en la aplicación.
 * Esta clase proporciona métodos para abrir actividades específicas y controlar la finalización de actividades anteriores.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public class OpenActivities {

    /**
     * Abre la actividad de visor de base de datos.
     *
     * @param context El contexto de la aplicación.
     * @see OpenActivities#ifActivityFinish(Context)
     */
    public static void databaseViewer(Context context) {
        ifActivityFinish(context);
        Intent intent = new Intent(context, DatabaseViewer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Abre la actividad de visor de intentos fallidos.
     *
     * @param context El contexto de la aplicación.
     * @see OpenActivities#ifActivityFinish(Context)
     */
    public static void failedAttemptsViewer(Context context) {
        ifActivityFinish(context);
        Intent intent = new Intent(context, FailedAttemptsViewer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Abre la actividad principal.
     *
     * @param context El contexto de la aplicación.
     * @see OpenActivities#ifActivityFinish(Context)
     */
    public static void mainViewer(Context context) {
        ifActivityFinish(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Finaliza la actividad actual si es una instancia de Activity.
     *
     * @param context El contexto de la aplicación.
     */
    private static void ifActivityFinish(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.finish();
        }
    }

    /**
     * Establece la funcionalidad de navegación del toolbar para ir al visor principal.
     *
     * @param toolbar El toolbar que se utilizará.
     * @param context El contexto de la aplicación.
     * @see OpenActivities#mainViewer(Context)
     */
    public static void toolbarGoToMainViewer(Toolbar toolbar, Context context) {
        toolbar.setNavigationOnClickListener(v -> mainViewer(context));
    }
}
