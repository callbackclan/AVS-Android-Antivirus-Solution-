/*
 * Copyright (C) 2021 Dr.NooB
 *
 * This file is a part of Data Monitor <https://github.com/itsdrnoob/DataMonitor>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.avsolution.networkUtils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.avsolution.dao.Application;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHandler.class.getSimpleName();
    private static final String DATABASE_NAME = "appDataUsage";
    private static final int DATABASE_VERSION = 12;
    private static int count;
    private Context context;
    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        count = 0;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "onCreate: ");
        String createTable = "CREATE TABLE app_data_usage(uid INTEGER PRIMARY KEY, app_name TEXT," +
                "package_name TEXT, system_app BOOLEAN, app_icon TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS app_data_usage");
        onCreate(db);
    }

    public void addData(Application model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Bitmap bitmap = getBitmapFromDrawable(model.getIcon());
        String encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);

        values.put("uid", model.getUid());
        values.put("app_name", model.getName());
        values.put("package_name", model.getPackage());
        values.put("system_app", model.isSystemApp());
        values.put("app_icon", encodedImage);
        count++;
        try {
            database.insertOrThrow("app_data_usage", null, values);
            Log.d("dataAdded", model.getName()+ " : "+model.getUid()+ " : "+count);
        }
        catch (Exception e) {
            Log.d("dataSkipped", model.getName()+ " : "+model.getUid()+ " : "+count + " : "+e.getMessage());
        }
        finally {
            database.close();
        }

    }

    @SuppressLint("Range")
    public List<Application> getUsageList() {
        List<Application> mList = new ArrayList<>();
        String selectQuery = "SELECT * FROM app_data_usage";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Application model = new Application();
                model.setUid(cursor.getInt(cursor.getColumnIndex("uid")));
                model.setName(cursor.getString(cursor.getColumnIndex("app_name")));
                model.setPackage(cursor.getString(cursor.getColumnIndex("package_name")));
                model.setSystemApp(cursor.getInt(cursor.getColumnIndex("system_app")) != 0);
                Bitmap bitmap = decodeBase64(cursor.getString(cursor.getColumnIndex("app_icon")));
                model.setIcon(new BitmapDrawable(context.getResources(), bitmap));
                mList.add(model);
            }
            while (cursor.moveToNext());
            cursor.close();
            database.close();

        }

        return mList;
    }


    public int updateData(Application model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        return database.update("app_data_usage", values, "uid =?",
                new String[]{String.valueOf(model.getUid())});
    }

    public void createAppDataMonitorList(Application model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", model.getUid());
        values.put("app_name", model.getName());
        values.put("package_name", model.getPackage());
        values.put("system_app", model.isSystemApp());

        try {
            database.insert("app_data_monitor_list", null, values);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            database.close();
        }
    }

//    public List<Application> getAppMonitorList() {
//        List<Application> mList = new ArrayList<>();
//        String selectQuery = "SELECT * FROM app_data_monitor_list";
//
//        SQLiteDatabase database = this.getWritableDatabase();
//        Cursor cursor = database.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                Application model = new Application();
//                model.setUid(cursor.getInt(cursor.getColumnIndex("uid")));
//                model.setName(cursor.getString(cursor.getColumnIndex("app_name")));
//                model.setPackage(cursor.getString(cursor.getColumnIndex("package_name")));
//                model.setSystemApp(cursor.getInt(cursor.getColumnIndex("system_app")) != 0);
//
//                mList.add(model);
//            }
//            while (cursor.moveToNext());
//            cursor.close();
//            database.close();
//
//        }
//
//        return mList;
//    }


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @NonNull
    static private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }
}
