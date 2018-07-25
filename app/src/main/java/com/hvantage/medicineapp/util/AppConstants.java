package com.hvantage.medicineapp.util;

public class AppConstants {

    public static final String APP_NAME = "medicine_app";


    public class FIREBASE_KEY {
        public static final String USERS = "users";
        public static final String CART = "order_cart";
        public static final String PRESCRIPTION = "prescription ";
        public static final String CATEGORY = "category";
        public static final String VAULT = "vault";
        public static final String MY_FAMILY = "my_family";
        public static final String MY_DOCTORS = "my_doctors";
        public static final String MEDICINE = "medicine";
        public static final String CART_ITEMS = "cart_items";
        public static final String ADDRESS = "address";
        public static final String ORDERS = "orders";
    }

    public class CATEGORY {
        public static final String OTC = "OTC";
        public static final String PERSONAL_CARE = "Personal Care";
        public static final String BABY_AND_MOTHER = "Baby & Mother";
        public static final String WELLNESS = "Wellness";
    }

    public class ORDER_TYPE {
        public static final int ORDER_WITH_PRESCRIPTION = 1;
        public static final int ORDER_WITHOUT_PRESCRIPTION = 2;
    }
}
