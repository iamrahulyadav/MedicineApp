package com.hvantage.medicineapp.util;

public class AppConstants {

    public static final String APP_NAME = "medicine_app";
    public static final String BASE_URL = "https://onlinemedicineapp.com/APP/";


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
        public static final String MY_PRESCRIPTIONS = "my_prescriptions";
        public static final String TEMP_PRESCRIPTION = "temp_prescription";
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

    public class ENDPOINT {
        public static final String REGISTER = "register.php";
        public static final String PRODUCT = "product.php";
        public static final String ORDER = "order.php";
        public static final String ADDRESS = "address.php";
    }

    public class METHODS {
        public static final String USER_SIGNUP = "user_signup";
        public static final String USER_LOGIN = "user_login";
        public static final String CHECK_PHONE_NO = "check_phone_no";
        public static final String GET_ALL_CATEGORIES = "getAllCategories";
        public static final String GET_ALL_SUBCATEGORIES = "getAllSubCategories";
        public static final String GET_SUBCAT_PRODUCTS = "get_subcat_products";
        public static final String ADD_PRESCRIPTION = "add_prescription";
        public static final String GET_MY_PRESCRIPTIONS = "get_my_prescriptions";
        public static final String ADD_ADDRESS = "add_address";
        public static final String GET_MY_ADDRESSES = "get_my_addresses";
        public static final String DELETE_ADDRESS = "delete_address";
    }

    public class NOTIFICATION_KEY {
        public static final String ORDER_PLACED = "order_placed";
        public static final String ORDER_UPDATED = "order_updated";
    }

    public class NOTIFICATION_ID {
        public static final int ORDER_PLACED_ID = 1;
    }
}
