package app_utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "APP_DB_MANAGER";

    //DataBaseHelper table name
    private static final String TABLE_RECENT = "DataBaseHelper";

    //DataBaseHelper table name (2nd table for all tab)
    private static final String REGISTER_TABLE = "REGISTER_TABLE";

    private static final String USER_VEHICLE_TABLE = "USER_VEHICLE_TABLE";

    //recyclerManager Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_TAGID = "tag_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_NUMBER = "phone_number";
    private static final String KEY_EMAILID = "email_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_LAST_SEEN_TIME = "last_seen";
    private static final String KEY_MAJOR = "major";
    private static final String KEY_MINOR = "minor";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_RSSI = "rssi";

    private static final String KEY_BRAND_NAME = "BRAND_NAME";
    private static final String KEY_BRAND_ID = "BRAND_ID";
    private static final String KEY_MODEL_NAME = "MODEL_NAME";
    private static final String KEY_MODEL_ID = "MODEL_ID";

    private static final String KEY_LICENSE_PLATE = "LICENSE_PLATE";
    private static final String KEY_IMAGE_BASE64 = "IMAGE_BASE64";
    private static final String KEY_MODEL_YEAR = "MODEL_YEAR";
    private static final String KEY_VEHICLE_ID_ODOO = "VEHICLE_ID_ODOO";

    private static final String KEY_INSURANCE = "INSURANCE_HISTORY";
    private static final String KEY_EMISSION = "EMISSION_HISTORY";

    private static final String KEY_EMISSION_ID = "INSURANCE_ID";
    private static final String KEY_INSURANCE_ID = "EMISSION_ID";
    //private static final String KEY_MODEL_NAME = "model_name";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    /*
    leaving gap between "CREATE TABLE" & TABLE_RECENT gives error watch out!
    Follow the below format
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        /*String CREATE_ALL_TABLE = "CREATE TABLE " + REGISTER_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TAGID + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_DESIGNATION + " TEXT, "
                + KEY_NUMBER + " TEXT, "
                + KEY_EMAILID + " TEXT, "
                + KEY_DATE + " TEXT, "
                + KEY_TIME + " TEXT, "
                + KEY_LAST_SEEN_TIME + " TEXT, "
                + KEY_MAJOR + " TEXT, "
                + KEY_MINOR + " TEXT, "
                + KEY_UUID + " TEXT, "
                + KEY_RSSI + " TEXT)";*/

        String TABLE_BRANDS = "CREATE TABLE " + REGISTER_TABLE + "("
                + KEY_BRAND_NAME + " TEXT PRIMARY KEY, "
                + KEY_BRAND_ID + " TEXT, "
                + KEY_MODEL_NAME + " TEXT, "
                + KEY_MODEL_ID + " TEXT)";

        String TABLE_USER_LIST = "CREATE TABLE " + USER_VEHICLE_TABLE + "("
                + KEY_VEHICLE_ID_ODOO + " INTEGER PRIMARY KEY, "
                + KEY_BRAND_NAME + " TEXT, "
                + KEY_BRAND_ID + " INTEGER, "
                + KEY_MODEL_NAME + " TEXT, "
                + KEY_MODEL_ID + " INTEGER, "
                + KEY_LICENSE_PLATE + " TEXT, "
                + KEY_IMAGE_BASE64 + " TEXT, "
                + KEY_MODEL_YEAR + " TEXT)";

        String TABLE_VEHICLE_HISTORY= "CREATE TABLE " + USER_VEHICLE_TABLE + "("
                + KEY_VEHICLE_ID_ODOO + " INTEGER PRIMARY KEY, "
                + KEY_INSURANCE + " TEXT, "
                + KEY_INSURANCE_ID + " INTEGER, "
                + KEY_EMISSION + " TEXT, "
                + KEY_EMISSION_ID + " INTEGER)";
        //+ KEY_MODEL_ID + " TEXT)";

        db.execSQL(TABLE_BRANDS);
        db.execSQL(TABLE_USER_LIST);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + REGISTER_TABLE);

        // Create tables again
        onCreate(db);
    }

    // Adding new data
    public void addData(DataBaseHelper dataBaseHelper) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BRAND_NAME, dataBaseHelper.get_brand_name());
        values.put(KEY_BRAND_ID, dataBaseHelper.get_brand_id()); // Contact Phone
        values.put(KEY_MODEL_NAME, dataBaseHelper.get_model_name());
        values.put(KEY_MODEL_ID, dataBaseHelper.get_model_id());

        // Inserting Row
        //db.insert(TABLE_RECENT, null, values);
        db.insert(REGISTER_TABLE, null, values);

        db.close(); // Closing database connection
    }

    // Adding new data to user vehicle list
    public void addDataToUserVehicle(DataBaseHelper dataBaseHelper) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE_ID_ODOO, dataBaseHelper.get_vehicle_id());
        values.put(KEY_BRAND_NAME, dataBaseHelper.get_brand_name());
        values.put(KEY_BRAND_ID, dataBaseHelper.get_brand_id_no());
        values.put(KEY_MODEL_NAME, dataBaseHelper.get_model_name());
        values.put(KEY_MODEL_ID, dataBaseHelper.get_model_id_no());
        values.put(KEY_LICENSE_PLATE, dataBaseHelper.get_license_plate());
        values.put(KEY_IMAGE_BASE64, dataBaseHelper.get_image_base64());
        values.put(KEY_MODEL_YEAR, dataBaseHelper.get_model_year());
        // Inserting Row
        //db.insert(TABLE_RECENT, null, values);
        db.insert(USER_VEHICLE_TABLE, null, values);

        db.close(); // Closing database connection
    }
    /*
    this returns the modelName or modelID using Brand Name has key in a string
     */
    /*public String getIdForStringTabAll(String str) {
        String res;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(REGISTER_TABLE, new String[]{KEY_MODEL_NAME,
                }, KEY_BRAND_NAME + "=?",
                new String[]{str}, null, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            res = cursor.getString(cursor.getColumnIndex(KEY_MODEL_NAME));
        } else {
            res = null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }*/

    /*
    this returns the modelName or modelID using Brand Name as key in a arrayList format
     */
    public ArrayList<String> getIdForStringTabAll(String str) {
        ArrayList<String> alData = new ArrayList<>();
        String res;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(REGISTER_TABLE, new String[]{KEY_MODEL_NAME,
                }, KEY_BRAND_NAME + "=?",
                new String[]{str}, null, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            res = cursor.getString(cursor.getColumnIndex(KEY_MODEL_NAME));
            String[] saData = res.split(",");
            alData.addAll(Arrays.asList(saData));
        } else {
            alData = null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return alData;
    }

    public int getModelIDFromSelectedModelName(String str, int position) {
        //ArrayList<String> alData = new ArrayList<>();
        String res;
        int ID = 9595959;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(REGISTER_TABLE, new String[]{KEY_MODEL_ID,
                }, KEY_BRAND_NAME + "=?",
                new String[]{str}, null, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            res = cursor.getString(cursor.getColumnIndex(KEY_MODEL_ID));
            String[] saData = res.split(",");
            ID = Integer.valueOf(saData[position]);
            //alData.addAll(Arrays.asList(saData));
        } else {
            //alData = null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return ID;
    }

    public int getBrandIDFromString(String str) {
        //ArrayList<String> alData = new ArrayList<>();
        String res;
        int ID = 9595959;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(REGISTER_TABLE, new String[]{KEY_BRAND_ID,
                }, KEY_BRAND_NAME + "=?",
                new String[]{str}, null, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            res = cursor.getString(cursor.getColumnIndex(KEY_BRAND_ID));
            //String[] saData = res.split(",");
            ID = Integer.valueOf(res);
            //alData.addAll(Arrays.asList(saData));
        } else {
            //alData = null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return ID;
    }
    /*public int getIdForStringRecentTab(String str) {
        int res;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECENT, new String[]{COLUMN_ID,
                }, KEY_TAGID + "=?",
                new String[]{str}, null, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            res = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        } else {
            res = -1;
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }*/

    //gets Name of index to check whether to perform update task in recyclerview or not
    public String getNameFromAllTab(int ID) {
        Cursor cursor = null;
        String sName;
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.query(REGISTER_TABLE, new String[]{KEY_NAME,
                }, KEY_ID + "=?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        //cursor = db.rawQuery("SELECT TABLEALL FROM last_seen WHERE _id" +" = "+ID +" ", new String[] {KEY_ID + ""});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            sName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        } else {
            sName = "";
        }
        /*if(sName==null){
            return "";
        }*/
        cursor.close();
        return sName;
    }

    //gets Name of index to check whether to perform update task in recyclerview or not
   /* public String getNameFromRecentTab(int ID) {
        Cursor cursor = null;
        String sName = "";
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.query(TABLE_RECENT, new String[]{KEY_NAME,
                }, KEY_ID + "=?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        //cursor = db.rawQuery("SELECT TABLEALL FROM last_seen WHERE _id" +" = "+ID +" ", new String[] {KEY_ID + ""});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            sName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        } else {
            sName = "";
        }
        *//*if(sName==null){
            return "";
        }*//*
        cursor.close();
        return sName;
    }*/


    // Getting data
    public List<DataBaseHelper> getRowData(String ID) {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + REGISTER_TABLE + " WHERE " + " BRAND_NAME " + " = " + ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            //do {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();

            dataBaseHelper.set_brand_name(cursor.getString(0));
            dataBaseHelper.set_brand_id(cursor.getString(1));
            dataBaseHelper.set_model_name(cursor.getString(2));
            dataBaseHelper.set_model_id(cursor.getString(3));
            // Adding data to list
            dataBaseHelperList.add(dataBaseHelper);
            //} while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }

    // Getting data
    /*public List<DataBaseHelper> getAllRecentData() {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();

                dataBaseHelper.setID(Integer.parseInt(cursor.getString(0)));
                dataBaseHelper.setTagID(cursor.getString(1));
                dataBaseHelper.setName(cursor.getString(2));
                dataBaseHelper.setDesignation(cursor.getString(3));
                dataBaseHelper.setPhoneNumber(cursor.getString(4));
                dataBaseHelper.setEmailID(cursor.getString(5));
                dataBaseHelper.setDate(cursor.getString(6));
                dataBaseHelper.setTime(cursor.getString(7));
                dataBaseHelper.setLastSeen(cursor.getString(8));
                dataBaseHelper.setMajor(cursor.getString(9));
                dataBaseHelper.setMinor(cursor.getString(10));
                dataBaseHelper.setUUID(cursor.getString(11));
                dataBaseHelper.setRssi(cursor.getString(12));
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }*/
    public List<DataBaseHelper> getRowDataFromVehicleTable(int vehicleID) {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + USER_VEHICLE_TABLE + " WHERE " + KEY_VEHICLE_ID_ODOO + " = " + vehicleID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();
                dataBaseHelper.set_vehicle_id(cursor.getInt(0));
                dataBaseHelper.set_brand_name(cursor.getString(1));
                dataBaseHelper.set_brand_id_no(cursor.getInt(2));
                dataBaseHelper.set_model_name(cursor.getString(3));
                dataBaseHelper.set_model_id_no(cursor.getInt(4));
                dataBaseHelper.set_license_plate(cursor.getString(5));
                dataBaseHelper.set_image_base64(cursor.getString(6));
                dataBaseHelper.set_model_year(cursor.getString(7));
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }
        // return recent list
        return dataBaseHelperList;
    }

    public List<DataBaseHelper> getAllUserVehicleData() {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + USER_VEHICLE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();
                dataBaseHelper.set_vehicle_id(cursor.getInt(0));
                dataBaseHelper.set_brand_name(cursor.getString(1));
                dataBaseHelper.set_brand_id_no(cursor.getInt(2));
                dataBaseHelper.set_model_name(cursor.getString(3));
                dataBaseHelper.set_model_id_no(cursor.getInt(4));
                dataBaseHelper.set_license_plate(cursor.getString(5));
                dataBaseHelper.set_image_base64(cursor.getString(6));
                dataBaseHelper.set_model_year(cursor.getString(7));
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }
        // return recent list
        return dataBaseHelperList;
    }

    // Getting data
    public List<DataBaseHelper> getAllTabData() {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + REGISTER_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();

                dataBaseHelper.set_brand_name(cursor.getString(0));
                dataBaseHelper.set_brand_id(cursor.getString(1));
                dataBaseHelper.set_model_name(cursor.getString(2));
                dataBaseHelper.set_model_id(cursor.getString(3));
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }

    public List<String> getAllBrands() {
        //List<String> dataBaseHelperList = new ArrayList<>();
        ArrayList<String> alData = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  " + KEY_BRAND_NAME + " FROM " + REGISTER_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();

                dataBaseHelper.set_brand_name(cursor.getString(0));
                alData.add(dataBaseHelper.get_brand_name());
                //uncomment below if we want to get all these details
                //dataBaseHelper.set_brand_id(cursor.getString(1));
                //dataBaseHelper.set_model_name(cursor.getString(2));
                //dataBaseHelper.set_model_id(cursor.getString(3));
                // Adding data to list
                //dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }

        // return recent list
        return alData;
    }

    //this gets only the brand name without any problem
    /*public List<DataBaseHelper> getAllBrands() {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  "+KEY_BRAND_NAME+ " FROM " + REGISTER_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();

                dataBaseHelper.set_brand_name(cursor.getString(0));
                //uncomment below if we want to get all these details
                //dataBaseHelper.set_brand_id(cursor.getString(1));
                //dataBaseHelper.set_model_name(cursor.getString(2));
                //dataBaseHelper.set_model_id(cursor.getString(3));
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }*/

    public ArrayList<DataBaseHelper> getAllModels(String KEY) {
        ArrayList<DataBaseHelper> modelsList = new ArrayList<>();

        //this query will get back the no. of columns and their name from db
        /*Cursor cursor = db.query(REGISTER_TABLE, null, null, null, null, null, null,null);
        String[] columnNames = cursor.getColumnNames();*/

        String selectQuery = "SELECT  * FROM " + REGISTER_TABLE + " WHERE " + " BRAND_NAME " + " = " + KEY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            //do {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();
            dataBaseHelper.set_brand_name(cursor.getString(0));
            // Adding data to list
            modelsList.add(dataBaseHelper);
            //} while (cursor.moveToNext());
        }
        return modelsList;
    }


    // Updating single data
    /*public int updateMultipleDataList(DataBaseHelper dataBaseHelper, String KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        values.put(KEY_BRAND_NAME, dataBaseHelper.get_brand_name());
        values.put(KEY_BRAND_ID, dataBaseHelper.get_brand_id());
        values.put(KEY_MODEL_NAME, dataBaseHelper.get_model_name());
        values.put(KEY_MODEL_ID, dataBaseHelper.get_model_id());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(TABLE_RECENT, values, "BRAND_NAME" + " = " + KEY_ID, null);
        *//*ContentValues data=new ContentValues();
        data.put("Field1","bob");
        DB.update(Tablename, data, "_id=" + id, null);*//*
    }*/

    // Updating single data in all tab
    /*public int updateMultipleData(DataBaseHelper dataBaseHelper, String KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        //values.put(KEY_NAME, dataBaseHelper.getName());
        //values.put(KEY_NUMBER, dataBaseHelper.getPhoneNumber());
        //values.put(KEY_BRAND_NAME, dataBaseHelper.get_brand_name());
        //values.put(KEY_BRAND_ID, dataBaseHelper.get_brand_id());
        values.put(KEY_MODEL_NAME, dataBaseHelper.get_model_name());
        values.put(KEY_MODEL_ID, dataBaseHelper.get_model_id());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(REGISTER_TABLE, values, "BRAND_NAME" + " = " + KEY_ID, null);
        *//*ContentValues data=new ContentValues();
        data.put("Field1","bob");
        DB.update(Tablename, data, "_id=" + id, null);*//*
    }*/

    public void updateRowDataByVehicleID(DataBaseHelper dataBaseHelper, int VEHICLE_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String sLicensePlate = dataBaseHelper.get_license_plate();
        String sBase64Image = dataBaseHelper.get_image_base64();
        String sModelYear = dataBaseHelper.get_model_year();
        if (sLicensePlate != null && !sLicensePlate.equals(""))
            values.put(KEY_LICENSE_PLATE, dataBaseHelper.get_license_plate());
        if (sBase64Image != null && !sBase64Image.equals(""))
            values.put(KEY_IMAGE_BASE64, dataBaseHelper.get_image_base64());
        if (sModelYear != null && !sModelYear.equals(""))
            values.put(KEY_MODEL_YEAR, dataBaseHelper.get_model_year());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        if (values.size() >= 1)
            db.update(USER_VEHICLE_TABLE, values, KEY_VEHICLE_ID_ODOO + " = " + VEHICLE_ID, null);
    }

    // Updating single data
    /*public int updateSingleDataList(DataBaseHelper dataBaseHelper, int KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        //values.put(KEY_NAME, dataBaseHelper.getName());
        //values.put(KEY_NUMBER, dataBaseHelper.getPhoneNumber());
        values.put(KEY_LAST_SEEN_TIME, dataBaseHelper.getLastSeen());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(TABLE_RECENT, values, "_id" + " = " + KEY_ID, null);
        *//*ContentValues data=new ContentValues();
        data.put("Field1","bob");
        DB.update(Tablename, data, "_id=" + id, null);*//*
    }*/

    // Updating single data in all tab
    public int updateSingleDataListAllTab(DataBaseHelper dataBaseHelper, int KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        //values.put(KEY_NAME, dataBaseHelper.getName());
        //values.put(KEY_NUMBER, dataBaseHelper.getPhoneNumber());
        values.put(KEY_MODEL_ID, dataBaseHelper.get_model_id());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(REGISTER_TABLE, values, "_id" + " = " + KEY_ID, null);
        /*ContentValues data=new ContentValues();
        data.put("Field1","bob");
        DB.update(Tablename, data, "_id=" + id, null);*/
    }

    public void clearDatabase(String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM " + TABLE_NAME;
        db.execSQL(clearDBQuery);
    }

    // Deleting single data
    public void deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_RECENT, KEY_ID + " = ?", new String[] { String.valueOf(recent.getID()) });
        db.delete(REGISTER_TABLE, KEY_ID + " = " + id, null);
        db.close();
    }

    public void deleteVehicleData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_RECENT, KEY_ID + " = ?", new String[] { String.valueOf(recent.getID()) });
        db.delete(USER_VEHICLE_TABLE, KEY_VEHICLE_ID_ODOO + " = " + id, null);
        db.close();
    }

    // Getting recent Count
    public int getRecordsCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + REGISTER_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);


        if (cursor != null && !cursor.isClosed()) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    /*public boolean CheckIsDataAlreadyInDBorNot(String TableName,String dbfield, String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_ALL + " where " + dbfield + "="
                + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount<=0){
            return false;
        }
        return true;
    }*/
}
