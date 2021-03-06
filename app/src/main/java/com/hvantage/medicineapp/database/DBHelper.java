package com.hvantage.medicineapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.CategoryData;
import com.hvantage.medicineapp.model.ProductData;
import com.hvantage.medicineapp.model.SubCategoryData;

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


    //product
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_CATEGORY_NAME = "category_name";
    private static final String KEY_SUB_CATEGORY_NAME = "sub_category_name";
    private static final String KEY_SHORT_DESCRIPTION = "short_description";
    private static final String KEY_LONG_DESCRIPTION = "long_description";
    private static final String KEY_PRICE_MRP = "price_mrp";
    private static final String KEY_PRICE_DISCOUNT = "price_discount";
    private static final String KEY_DISCOUNT_PERCENTAGE = "discount_percentage";
    private static final String KEY_DISCOUNT_TEXT = "discount_text";
    private static final String KEY_PACKAGING_CONTAIN = "packaging_contain";
    private static final String KEY_MANUFACTURER = "manufacturer";
    private static final String KEY_PRODUCT_TYPE = "product_type";
    private static final String KEY_POWER = "power";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PRESCRIPTION_REQUIRED = "prescription_required";
    private static final String KEY_TOTAL_AVAILABLE = "total_available";

    //cart
    private static final String KEY_ITEM_ID = "item_id";
    private static final String KEY_ITEM_NAME = "item_name";
    private static final String KEY_ITEM_QTY = "item_qty";
    private static final String KEY_ITEM_PRICE = "item_price";
    private static final String KEY_ITEM_TOTAL_PRICE = "item_total_price";
    private static final String KEY_ITEM_IMG = "item_img";
    private static final String KEY_ITEM_PRE_REQUIRED = "item_pre_required";

    //Category
    private static final String KEY_CAT_ID = "CAT_ID";
    private static final String KEY_CAT_NAME = "CAT_NAME";
    private static final String KEY_CAT_IMAGE = "CAT_IMAGE";

    //Category
    private static final String KEY_SUBCAT_ID = "SUBCAT_ID";
    private static final String KEY_SUBCAT_NAME = "SUBCAT_NAME";
    private static final String KEY_SUBCAT_IMAGE = "SUBCAT_IMAGE";

    //tables
    private static final String TABLE_MEDICINE = "medicine";
    private static final String TABLE_CART = "cart";
    private static final String TABLE_CATEGORY = "category ";
    private static final String TABLE_SUBCATEGORY = "subcategory ";
    private static final String TABLE_CAT_PRODUCTS = "cat_products";

    private static final String TAG = "DBHelper";
    private static final int DATABASE_VERSION = 1;


    //create table structers
    String CREATE_TABLE_MEDICINE = "CREATE TABLE " + TABLE_MEDICINE +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PRODUCT_ID + " TEXT, "
            + KEY_CATEGORY_NAME + " TEXT, "
            + KEY_SUB_CATEGORY_NAME + " TEXT, "
            + KEY_SHORT_DESCRIPTION + " TEXT, "
            + KEY_LONG_DESCRIPTION + " TEXT, "
            + KEY_IMAGE + " TEXT, "
            + KEY_MANUFACTURER + " TEXT, "
            + KEY_NAME + " TEXT, "
            + KEY_POWER + " TEXT, "
            + KEY_PRICE_MRP + " TEXT, "
            + KEY_PRICE_DISCOUNT + " TEXT, "
            + KEY_DISCOUNT_PERCENTAGE + " TEXT,"
            + KEY_DISCOUNT_TEXT + " TEXT,"
            + KEY_PRODUCT_TYPE + " TEXT,"
            + KEY_PACKAGING_CONTAIN + " TEXT,"
            + KEY_PRESCRIPTION_REQUIRED + " TEXT,"
            + KEY_TOTAL_AVAILABLE + " TEXT, "
            + KEY_SUBCAT_ID + " TEXT "
            + ")";

    String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ITEM_ID + " TEXT, "
            + KEY_ITEM_NAME + " TEXT, "
            + KEY_ITEM_QTY + " INTEGER, "
            + KEY_ITEM_PRICE + " REAL, "
            + KEY_ITEM_TOTAL_PRICE + " REAL, "
            + KEY_ITEM_IMG + " TEXT, "
            + KEY_ITEM_PRE_REQUIRED + " TEXT "
            + ")";

    String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CAT_ID + " TEXT, "
            + KEY_CAT_NAME + " TEXT, "
            + KEY_CAT_IMAGE + " TEXT "
            + ")";

    String CREATE_TABLE_SUBCATEGORY = "CREATE TABLE " + TABLE_SUBCATEGORY +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_SUBCAT_ID + " TEXT, "
            + KEY_SUBCAT_NAME + " TEXT, "
            + KEY_SUBCAT_IMAGE + " TEXT, "
            + KEY_CAT_ID + " TEXT "
            + ")";

    //create table structers
    String CREATE_TABLE_CAT_PRODUCTS = "CREATE TABLE " + TABLE_CAT_PRODUCTS +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PRODUCT_ID + " TEXT, "
            + KEY_CATEGORY_NAME + " TEXT, "
            + KEY_SUB_CATEGORY_NAME + " TEXT, "
            + KEY_SHORT_DESCRIPTION + " TEXT, "
            + KEY_LONG_DESCRIPTION + " TEXT, "
            + KEY_IMAGE + " TEXT, "
            + KEY_MANUFACTURER + " TEXT, "
            + KEY_NAME + " TEXT, "
            + KEY_POWER + " TEXT, "
            + KEY_PRICE_MRP + " TEXT, "
            + KEY_PRICE_DISCOUNT + " TEXT, "
            + KEY_DISCOUNT_PERCENTAGE + " TEXT,"
            + KEY_DISCOUNT_TEXT + " TEXT,"
            + KEY_PRODUCT_TYPE + " TEXT,"
            + KEY_PACKAGING_CONTAIN + " TEXT,"
            + KEY_PRESCRIPTION_REQUIRED + " TEXT,"
            + KEY_TOTAL_AVAILABLE + " TEXT, "
            + KEY_SUBCAT_ID + " TEXT "
            + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_MEDICINE);
        sqLiteDatabase.execSQL(CREATE_TABLE_CART);
        sqLiteDatabase.execSQL(CREATE_TABLE_CATEGORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_SUBCATEGORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_CAT_PRODUCTS);
    }

    public void saveMedicine(ProductData modal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_ID, modal.getProductId());
        values.put(KEY_CATEGORY_NAME, modal.getCategoryName());
        values.put(KEY_SUB_CATEGORY_NAME, modal.getSubCategoryName());
        values.put(KEY_SHORT_DESCRIPTION, modal.getShortDescription());
        values.put(KEY_LONG_DESCRIPTION, modal.getLongDescription());
        values.put(KEY_IMAGE, modal.getImage());
        values.put(KEY_MANUFACTURER, modal.getManufacturer());
        values.put(KEY_NAME, modal.getName());
        values.put(KEY_POWER, modal.getPower());
        values.put(KEY_PRICE_MRP, modal.getPriceMrp());
        values.put(KEY_PRICE_DISCOUNT, modal.getPriceDiscount() + "");
        values.put(KEY_DISCOUNT_PERCENTAGE, modal.getDiscountPercentage());
        values.put(KEY_DISCOUNT_TEXT, modal.getDiscountText());
        values.put(KEY_PRODUCT_TYPE, modal.getProductType());
        values.put(KEY_PACKAGING_CONTAIN, modal.getPackagingContain());
        values.put(KEY_PRESCRIPTION_REQUIRED, modal.getPrescriptionRequired() + "");
        values.put(KEY_TOTAL_AVAILABLE, modal.getTotalAvailable() + "");
        values.put(KEY_SUBCAT_ID, "");
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

    public int emptyCart() {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_CART, "1", null);
        Log.e(TAG, "emptyCart: rowCount" + rowCount);
        db.close();
        return rowCount;
    }

    public ArrayList<ProductData> getMedicines() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ProductData> list = null;
        String query = "SELECT * FROM " + TABLE_MEDICINE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<ProductData>();
            do {
                ProductData d = new ProductData();
                d.setProductId(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_ID)));
                d.setCategoryName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME)));
                d.setSubCategoryName(cursor.getString(cursor.getColumnIndex(KEY_SUB_CATEGORY_NAME)));
                d.setShortDescription(cursor.getString(cursor.getColumnIndex(KEY_SHORT_DESCRIPTION)));
                d.setLongDescription(cursor.getString(cursor.getColumnIndex(KEY_LONG_DESCRIPTION)));
                d.setImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
                d.setManufacturer(cursor.getString(cursor.getColumnIndex(KEY_MANUFACTURER)));
                d.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                d.setPower(cursor.getString(cursor.getColumnIndex(KEY_POWER)));
                d.setPriceMrp(cursor.getString(cursor.getColumnIndex(KEY_PRICE_MRP)));
                d.setPriceDiscount(cursor.getString(cursor.getColumnIndex(KEY_PRICE_DISCOUNT)));
                d.setDiscountPercentage(cursor.getString(cursor.getColumnIndex(KEY_DISCOUNT_PERCENTAGE)));
                d.setDiscountText(cursor.getString(cursor.getColumnIndex(KEY_DISCOUNT_TEXT)));
                d.setProductType(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_TYPE)));
                d.setPackagingContain(cursor.getString(cursor.getColumnIndex(KEY_PACKAGING_CONTAIN)));
                d.setPrescriptionRequired(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(KEY_PACKAGING_CONTAIN))));
                d.setTotalAvailable(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TOTAL_AVAILABLE))));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getMedicinesSearch() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = null;
        String query = "SELECT * FROM " + TABLE_MEDICINE;
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
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + KEY_ITEM_ID + "=" + modal.getItem_id(), null);
        if (c.moveToFirst()) {
            values.put(KEY_ITEM_NAME, modal.getItem());
            values.put(KEY_ITEM_QTY, modal.getQty());
            values.put(KEY_ITEM_PRICE, modal.getItem_price());
            values.put(KEY_ITEM_TOTAL_PRICE, modal.getItem_total_price());
            values.put(KEY_ITEM_IMG, modal.getImage());
            values.put(KEY_ITEM_PRE_REQUIRED, String.valueOf(modal.isIs_prescription_required()));
            Log.d(TAG, "addToCart: values >> " + values);

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
            values.put(KEY_ITEM_PRE_REQUIRED, String.valueOf(modal.isIs_prescription_required()));
            Log.d(TAG, "addToCart: values >> " + values);
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
                d.setIs_prescription_required(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(KEY_ITEM_PRE_REQUIRED))));
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

    public void saveCategory(CategoryData modal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CAT_ID, modal.getCatId());
        values.put(KEY_CAT_NAME, modal.getCatName());
        values.put(KEY_CAT_IMAGE, modal.getCatImage());
        Log.d(TAG, "saveCategory: values >> " + values.toString());
        boolean bb = db.insert(TABLE_CATEGORY, null, values) > 0;
        if (bb) {
            Log.d("saveCategory : ", "Inserted");
        } else {
            Log.d("saveCategory : ", "Not inserted");
        }
        db.close();
    }

    public ArrayList<CategoryData> getCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CategoryData> list = null;
        String query = "SELECT * FROM " + TABLE_CATEGORY;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }

        if (cursor.moveToFirst()) {
            list = new ArrayList<CategoryData>();
            do {
                CategoryData d = new CategoryData();
                d.setCatId(cursor.getString(cursor.getColumnIndex(KEY_CAT_ID)));
                d.setCatName(cursor.getString(cursor.getColumnIndex(KEY_CAT_NAME)));
                d.setCatImage(cursor.getString(cursor.getColumnIndex(KEY_CAT_IMAGE)));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<SubCategoryData> getSubCategory(String cat_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<SubCategoryData> list = null;
        String query = "SELECT * FROM " + TABLE_SUBCATEGORY + " WHERE " + KEY_CAT_ID + "=" + cat_id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }

        if (cursor.moveToFirst()) {
            list = new ArrayList<SubCategoryData>();
            do {
                SubCategoryData d = new SubCategoryData();
                d.setSubCatId(cursor.getString(cursor.getColumnIndex(KEY_SUBCAT_ID)));
                d.setSubCatName(cursor.getString(cursor.getColumnIndex(KEY_SUBCAT_NAME)));
                d.setSubCatImage(cursor.getString(cursor.getColumnIndex(KEY_SUBCAT_IMAGE)));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public int deleteCategory() {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_CATEGORY, "1", null);
        Log.e(TAG, "emptyCart: rowCount" + rowCount);
        db.close();
        return rowCount;
    }

    public void saveSubCategory(SubCategoryData modal, String cat_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SUBCAT_ID, modal.getSubCatId());
        values.put(KEY_SUBCAT_NAME, modal.getSubCatName());
        values.put(KEY_SUBCAT_IMAGE, modal.getSubCatImage());
        values.put(KEY_CAT_ID, cat_id);
        Log.d(TAG, "saveSubCategory: values >> " + values.toString());
        boolean bb = db.insert(TABLE_SUBCATEGORY, null, values) > 0;
        if (bb) {
            Log.d("saveSubCategory : ", "Inserted");
        } else {
            Log.d("saveSubCategory : ", "Not inserted");
        }
        db.close();
    }

    public int deleteSubCategory(String cat_id) {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_SUBCATEGORY, KEY_CAT_ID + "=?", new String[]{cat_id});
        Log.e(TAG, "emptyCart: rowCount" + rowCount);
        db.close();
        return rowCount;
    }

    public void saveCatProduct(ProductData modal, String subCatID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_ID, modal.getProductId());
        values.put(KEY_CATEGORY_NAME, modal.getCategoryName());
        values.put(KEY_SUB_CATEGORY_NAME, modal.getSubCategoryName());
        values.put(KEY_SHORT_DESCRIPTION, modal.getShortDescription());
        values.put(KEY_LONG_DESCRIPTION, modal.getLongDescription());
        values.put(KEY_IMAGE, modal.getImage());
        values.put(KEY_MANUFACTURER, modal.getManufacturer());
        values.put(KEY_NAME, modal.getName());
        values.put(KEY_POWER, modal.getPower());
        values.put(KEY_PRICE_MRP, modal.getPriceMrp());
        values.put(KEY_PRICE_DISCOUNT, modal.getPriceDiscount() + "");
        values.put(KEY_DISCOUNT_PERCENTAGE, modal.getDiscountPercentage());
        values.put(KEY_DISCOUNT_TEXT, modal.getDiscountText());
        values.put(KEY_PRODUCT_TYPE, modal.getProductType());
        values.put(KEY_PACKAGING_CONTAIN, modal.getPackagingContain());
        values.put(KEY_PRESCRIPTION_REQUIRED, modal.getPrescriptionRequired() + "");
        values.put(KEY_TOTAL_AVAILABLE, modal.getTotalAvailable() + "");
        values.put(KEY_SUBCAT_ID, subCatID);
        Log.d(TAG, "saveCatProduct: values >> " + values.toString());
        boolean bb = db.insert(TABLE_CAT_PRODUCTS, null, values) > 0;
        if (bb) {
            Log.d("saveCatProduct : ", "Inserted");
        } else {
            Log.d("saveCatProduct : ", "Not inserted");
        }
        db.close();
    }

    public ArrayList<ProductData> getCatProducts(String subcat_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ProductData> list = null;
        String query = "SELECT * FROM " + TABLE_CAT_PRODUCTS + " WHERE " + KEY_SUBCAT_ID + "=" + subcat_id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<ProductData>();
            do {
                ProductData d = new ProductData();
                d.setProductId(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_ID)));
                d.setCategoryName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME)));
                d.setSubCategoryName(cursor.getString(cursor.getColumnIndex(KEY_SUB_CATEGORY_NAME)));
                d.setShortDescription(cursor.getString(cursor.getColumnIndex(KEY_SHORT_DESCRIPTION)));
                d.setLongDescription(cursor.getString(cursor.getColumnIndex(KEY_LONG_DESCRIPTION)));
                d.setImage(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
                d.setManufacturer(cursor.getString(cursor.getColumnIndex(KEY_MANUFACTURER)));
                d.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                d.setPower(cursor.getString(cursor.getColumnIndex(KEY_POWER)));
                d.setPriceMrp(cursor.getString(cursor.getColumnIndex(KEY_PRICE_MRP)));
                d.setPriceDiscount(cursor.getString(cursor.getColumnIndex(KEY_PRICE_DISCOUNT)));
                d.setDiscountPercentage(cursor.getString(cursor.getColumnIndex(KEY_DISCOUNT_PERCENTAGE)));
                d.setDiscountText(cursor.getString(cursor.getColumnIndex(KEY_DISCOUNT_TEXT)));
                d.setProductType(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_TYPE)));
                d.setPackagingContain(cursor.getString(cursor.getColumnIndex(KEY_PACKAGING_CONTAIN)));
                d.setPrescriptionRequired(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(KEY_PACKAGING_CONTAIN))));
                d.setTotalAvailable(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TOTAL_AVAILABLE))));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public int deleteCatProducts(String subcat_id) {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_CAT_PRODUCTS, KEY_SUBCAT_ID + "=?", new String[]{subcat_id});
        Log.e(TAG, "deleteCatProducts: rowCount" + rowCount);
        db.close();
        return rowCount;
    }
}
