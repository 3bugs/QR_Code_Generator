package com.promlert.qrcodegenerator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Promlert on 12/16/2015.
 */
public class QRCodeDAO {

    private static final String TAG = "QRCodeDAO";

    private static final String DATABASE_NAME = "qrcode.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "qrcode";
    public static final String COL_ID = "_id";
    public static final String COL_TEXT = "text";
    public static final String COL_QR_CODE_BITMAP = "qr_code_bitmap";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEXT + " TEXT, "
            + COL_QR_CODE_BITMAP + " BLOB"
            + ")";

    private Context mContext;
    private static DatabaseHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    public QRCodeDAO(Context context) {
        context = context.getApplicationContext();
        mContext = context;

        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(context);
        }
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public long insert(String text, byte[] qrCodeImageByteArray) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TEXT, text);
        cv.put(COL_QR_CODE_BITMAP, qrCodeImageByteArray);
        long insertResult = mDatabase.insert(TABLE_NAME, null, cv);
        return insertResult;
    }

    public long insert(String text, Bitmap qrCodeImageBitmap) {
        return insert(text, convertBitmapToByteArray(qrCodeImageBitmap));
    }

    public long insert(QrItem qrItem) {
        return insert(qrItem.text, qrItem.qrCodeBitmap);
    }

    public ArrayList<QrItem> readAll() {
        ArrayList<QrItem> qrList = new ArrayList<>();

        Cursor cursor = mDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String text = cursor.getString(cursor.getColumnIndex(COL_TEXT));
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COL_QR_CODE_BITMAP));

                QrItem item = new QrItem(text, convertByteArrayToBitmap(bytes));
                qrList.add(item);
            }
            cursor.close();
        }
        return qrList;
    }

    public void deleteAll() {
        mDatabase.delete(TABLE_NAME, null, null);
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
/*
        int size = qrCodeBitmap.getRowBytes() * qrCodeBitmap.getHeight();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        qrCodeBitmap.copyPixelsToBuffer(buffer);

        byte[] bytes = new byte[size];
        buffer.get(bytes, 0, bytes.length);
*/

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap convertByteArrayToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private Context mContext;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            return;
        }
    }

    public static class QrItem {
        public final String text;
        public final Bitmap qrCodeBitmap;

        public QrItem(String text, Bitmap qrCodeBitmap) {
            this.text = text;
            this.qrCodeBitmap = qrCodeBitmap;
        }
    }
}
