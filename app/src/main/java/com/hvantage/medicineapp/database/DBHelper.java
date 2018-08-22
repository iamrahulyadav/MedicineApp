package com.hvantage.medicineapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.ProductModel;

import java.util.ArrayList;

/**
 * Created by RK on 12/1/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    //database name
    private static final String DATABASE_NAME = "medicinedb.db";

    //column keys
    private static final String KEY_ID = "id";
    private static final String KEY_FB = "fb_key";
    private static final String KEY_NAME = "name";
    private static final String KEY_MANUFACTURER = "manufacturer";
    private static final String KEY_PRODUCT_TYPE = "product_type";
    private static final String KEY_POWER = "power";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_SUB_CATEGORY = "sub_category";
    private static final String KEY_QTY = "qty";
    private static final String KEY_PRICE = "price";
    private static final String KEY_DESCIPTION = "desciption";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PRESCRIPTION_REQUIRED = "prescription_required";
    private static final String KEY_TOTAL_AVAILABLE = "total_available";

    //cart
    private static final String KEY_ITEM_ID = "KEY_ITEM_ID";
    private static final String KEY_ITEM_NAME = "KEY_ITEM_NAME";
    private static final String KEY_ITEM_QTY = "KEY_ITEM_QTY";
    private static final String KEY_ITEM_PRICE = "KEY_ITEM_PRICE";
    private static final String KEY_ITEM_TOTAL_PRICE = "KEY_ITEM_TOTAL_PRICE";
    private static final String KEY_ITEM_IMG = "KEY_ITEM_IMG";

    //tables
    private static final String TABLE_MEDICINE = "medicine";
    private static final String TABLE_CART = "cart";

    private static final String TAG = "DBHelper";
    private static final int DATABASE_VERSION = 1;


    //create table structers
    String CREATE_TABLE_MEDICINE = "CREATE TABLE " + TABLE_MEDICINE +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_FB + " TEXT, "
            + KEY_NAME + " TEXT, "
            + KEY_MANUFACTURER + " TEXT, "
            + KEY_PRODUCT_TYPE + " TEXT, "
            + KEY_POWER + " TEXT, "
            + KEY_CATEGORY + " TEXT, "
            + KEY_SUB_CATEGORY + " TEXT, "
            + KEY_QTY + " TEXT, "
            + KEY_PRICE + " REAL, "
            + KEY_DESCIPTION + " TEXT, "
            + KEY_IMAGE + " TEXT, "
            + KEY_PRESCRIPTION_REQUIRED + " TEXT,"
            + KEY_TOTAL_AVAILABLE + " INTEGER "
            + ")";
    String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ITEM_ID + " TEXT, "
            + KEY_ITEM_NAME + " TEXT, "
            + KEY_ITEM_QTY + " INTEGER, "
            + KEY_ITEM_PRICE + " REAL, "
            + KEY_ITEM_TOTAL_PRICE + " REAL, "
            + KEY_ITEM_IMG + " TEXT "
            + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_MEDICINE);
        sqLiteDatabase.execSQL(CREATE_TABLE_CART);
    }


    public void saveMedicine(ProductModel modal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FB, modal.getKey());
        values.put(KEY_NAME, modal.getName());
        values.put(KEY_MANUFACTURER, modal.getManufacturer());
        values.put(KEY_PRODUCT_TYPE, modal.getProduct_type());
        values.put(KEY_POWER, modal.getPower());
        values.put(KEY_CATEGORY, modal.getCategory_name());
        values.put(KEY_QTY, modal.getQty());
        values.put(KEY_PRICE, modal.getPrice());
        values.put(KEY_DESCIPTION, modal.getDescription());
        values.put(KEY_PRESCRIPTION_REQUIRED, String.valueOf(modal.isPrescription_required()));
        values.put(KEY_TOTAL_AVAILABLE, String.valueOf(modal.getTotal_available()));
        values.put(KEY_SUB_CATEGORY, modal.getSub_category_name());
        values.put(KEY_IMAGE, modal.getImage());
        Log.d(TAG, "saveMedicine: values >> " + values.toString());
        boolean bb = db.insert(TABLE_MEDICINE, null, values) > 0;
        if (bb) {
            Log.d("saveMedicine : ", "Inserted");
        } else {
            Log.d("saveMedicine : ", "Not inserted");
        }
        db.close();
    }

    public int deleteMedicineData() {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_MEDICINE, "1", null);
        Log.e(TAG, "deleteMedicineData: rowCount" + rowCount);
        db.close();
        return rowCount;
    }

    public ArrayList<ProductModel> getMedicines() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ProductModel> list = null;
        String query = "SELECT * FROM " + TABLE_MEDICINE;
//        String query = "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + KEY_PROJECT_TYPE + "=" + type_id + " ORDER BY ID DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<ProductModel>();
            do {
                ProductModel d = new ProductModel();
                d.setKey(cursor.getString(cursor.getColumnIndex(KEY_FB)));
                d.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                d.setManufacturer(cursor.getString(cursor.getColumnIndex(KEY_MANUFACTURER)));
                d.setProduct_type(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_TYPE)));
                d.setPower(cursor.getString(cursor.getColumnIndex(KEY_POWER)));
                d.setCategory_name(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                d.setQty(cursor.getString(cursor.getColumnIndex(KEY_QTY)));
                d.setSub_category_name(cursor.getString(cursor.getColumnIndex(KEY_SUB_CATEGORY)));
                d.setImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
                d.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
                d.setTotal_available(cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_AVAILABLE)));
                d.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCIPTION)));
                d.setPrescription_required(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(KEY_PRESCRIPTION_REQUIRED))));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getMedicinesSearch() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = null;
        String query = "SELECT * FROM " + TABLE_MEDICINE;
//        String query = "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + KEY_PROJECT_TYPE + "=" + type_id + " ORDER BY ID DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<String>();
            do {
                list.add(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getMedicinesAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = null;
//        String query = "SELECT * FROM " + TABLE_MEDICINE;
//        String query = "SELECT * FROM " + TABLE_MEDICINE + " WHERE " + KEY_CATEGORY + "=" + param;
        String query = "SELECT * FROM medicine where category='Prescription'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<String>();
            do {
                list.add(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            } while (cursor.moveToNext());
        }
        return list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINE);
        onCreate(db);
    }

    public boolean addToCart(CartData modal) {
        boolean isInserted = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d(TAG, "addToCart: modal >> " + modal.toString());
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + KEY_ITEM_ID + "=" + modal.getItem_id(), null);
        if (c.moveToFirst()) {
            values.put(KEY_ITEM_NAME, modal.getItem());
            values.put(KEY_ITEM_QTY, modal.getQty());
            values.put(KEY_ITEM_PRICE, modal.getItem_price());
            values.put(KEY_ITEM_TOTAL_PRICE, modal.getItem_total_price());
            values.put(KEY_ITEM_IMG, modal.getImage());
            if (db.update(TABLE_CART, values, KEY_ITEM_ID + "=" + modal.getItem_id(), null) > 0) {
                isInserted = true;
                Log.e("addToCart : ", "Updated");
            } else {
                isInserted = false;
                Log.e("addToCart : ", "Not Updated");
            }
        } else {
            values.put(KEY_ITEM_ID, modal.getItem_id());
            values.put(KEY_ITEM_NAME, modal.getItem());
            values.put(KEY_ITEM_QTY, modal.getQty());
            values.put(KEY_ITEM_PRICE, modal.getItem_price());
            values.put(KEY_ITEM_TOTAL_PRICE, modal.getItem_total_price());
            values.put(KEY_ITEM_IMG, modal.getImage());
            boolean bb = db.insert(TABLE_CART, null, values) > 0;
            if (bb) {
                isInserted = true;
                Log.e("addToCart : ", "Inserted");
            } else {
                isInserted = false;
                Log.e("addToCart : ", "Not inserted");
            }
        }
        db.close();
        return isInserted;
    }

    public ArrayList<CartData> getCartData() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CartData> list = null;
        String query = "SELECT * FROM " + TABLE_CART;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<CartData>();
            do {
                CartData d = new CartData();
                d.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                d.setItem_id(cursor.getString(cursor.getColumnIndex(KEY_ITEM_ID)));
                d.setItem(cursor.getString(cursor.getColumnIndex(KEY_ITEM_NAME)));
                d.setImage(cursor.getString(cursor.getColumnIndex(KEY_ITEM_IMG)));
                d.setItem_price(cursor.getDouble(cursor.getColumnIndex(KEY_ITEM_PRICE)));
                d.setQty(cursor.getInt(cursor.getColumnIndex(KEY_ITEM_QTY)));
                d.setItem_total_price(cursor.getDouble(cursor.getColumnIndex(KEY_ITEM_TOTAL_PRICE)));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public boolean updateCartItem(CartData modal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_QTY, modal.getQty());
        values.put(KEY_ITEM_TOTAL_PRICE, modal.getItem_total_price());
        return db.update(TABLE_CART, values, KEY_ID + "=" + modal.getId(), null) > 0;
    }

    public int deleteCartItem(String item_id) {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_CART, KEY_ITEM_ID + "=?", new String[]{item_id});
        Log.e(TAG, "deleteCartItem: rowCount" + rowCount);
        db.close();
        return rowCount;
    }

}
