package com.faunog.m08_act02_conectwithxampp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * La clase SQLiteFailedAccounts proporciona métodos para manejar una base de datos local SQLite que almacena intentos fallidos de inicio de sesión.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @version 1.1
 * @since 18/10/2023
 */
public class SQLiteFailedAccounts extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "failed_accounts.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "failed_accounts";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_DATE = "datetime";
    private static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";
    private static final TimeZone SPAIN_TIME_ZONE = TimeZone.getTimeZone("Europe/Madrid");
    private static SimpleDateFormat DATE_FORMAT;


    /**
     * Crea una nueva instancia de SQLiteFailedAccounts.
     *
     * @param context El contexto de la aplicación.
     */
    public SQLiteFailedAccounts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT, "
                + COL_PASSWORD + " TEXT, "
                + COL_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(createTableQuery);
        DATE_FORMAT = new SimpleDateFormat(PATTERN_DATE, Locale.getDefault());
        DATE_FORMAT.setTimeZone(SPAIN_TIME_ZONE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Guarda un intento fallido de inicio de sesión en la base de datos.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña.
     * @return True si se guarda correctamente, de lo contrario False.
     */
    public boolean saveFailedAttempt(String username, String password) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_USERNAME, username);
            contentValues.put(COL_PASSWORD, password);
            contentValues.put(COL_DATE, DATE_FORMAT.format(new Date()));

            long result = db.insert(TABLE_NAME, null, contentValues);
            return result != -1;
        } catch (Exception e) {
            Log.e("SQLiteFailedAccounts", Objects.requireNonNull(e.getMessage()));
            return false;
        }
    }

    /**
     * Obtiene la lista de intentos de inicio de sesión fallidos de la base de datos.
     *
     * @return Una lista de objetos FailedLogin que representan los intentos de inicio de sesión fallidos.
     */
    public List<FailedLogin> getFailedAccounts() {
        List<FailedLogin> failedLogins = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                FailedLogin failedLogin = new FailedLogin(id, username, password, dateTime);
                failedLogins.add(failedLogin);
            }
        } catch (IllegalArgumentException e) {
            Log.e("SQLiteFailedAccounts", Objects.requireNonNull(e.getMessage()));
        }

        return failedLogins;
    }

    /**
     * Crea un nuevo administrador SQLiteFailedAccounts.
     *
     * @param context El contexto de la aplicación.
     * @return Una nueva instancia de SQLiteFailedAccounts.
     */
    public static SQLiteFailedAccounts createManager(Context context) {
        SQLiteFailedAccounts sqLiteFailedAccounts = new SQLiteFailedAccounts(context);
        sqLiteFailedAccounts.onCreate(sqLiteFailedAccounts.getWritableDatabase());
        return sqLiteFailedAccounts;
    }

    /**
     * Guarda un intento fallido de inicio de sesión si el usuario o la contraseña no son correctos.
     *
     * @param statusUserPass Un arreglo que contiene el estado, el nombre de usuario y la contraseña.
     * @param context        El contexto de la aplicación.
     */
    public void ifUserAndPassNotOkSaveFailedAttempt(String[] statusUserPass, Context context) {
        boolean success = saveFailedAttempt(statusUserPass[1], statusUserPass[2]);
        final String TAG_sqLiteFailedAccounts = "sqLiteFailedAccounts";

        if (success) Log.i(TAG_sqLiteFailedAccounts, "Failed Attempt Saved");
        else Log.i(TAG_sqLiteFailedAccounts, "Failed Attempt NOT Saved");

        OpenActivities.failedAttemptsViewer(context);
    }
}
