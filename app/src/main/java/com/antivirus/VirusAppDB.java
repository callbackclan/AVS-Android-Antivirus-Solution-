package com.antivirus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.antivirus.FetchVirusModel;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class VirusAppDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "VirusAppDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_ID = "id";
    private static final String KEY_VIRUS_NAME = "virus_name";
    private static final String KEY_VIRUS_TAG = "virus_tag";
    private static final String TABLE_NAME = "Viruses";

    public VirusAppDB(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Viruses(id INTEGER PRIMARY KEY,virus_name TEXT,virus_tag TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Viruses");
        onCreate(db);
    }

    public void addVirus(FetchVirusModel model) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIRUS_NAME, model.getVirusName());
        values.put(KEY_VIRUS_TAG, model.getVirusTag());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<FetchVirusModel> getAllViruses() {
        List<FetchVirusModel> virusList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM Viruses", null);
        if (cursor.moveToFirst()) {
            do {
                FetchVirusModel model = new FetchVirusModel();
                model.setId(Integer.parseInt(cursor.getString(0)));
                model.setVirusName(cursor.getString(1));
                model.setVirusTag(cursor.getString(2));
                virusList.add(model);
            } while (cursor.moveToNext());
            return virusList;
        }
        return virusList;
    }

    public void deleteAllViruses() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}