package com.hashcode.eztop_up.DataRepository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import com.hashcode.eztop_up.Entities.Carrier;
import com.hashcode.eztop_up.R;
import com.hashcode.eztop_up.Utility.DbBitmapUtility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static String DB_PATH = " ";
    private static final String DB_NAME = "ezTopUp.db";
    private static final int DB_VERSION = 1;
    private Context context;
    private SQLiteDatabase db;
    private final String TAG = "DataBaseHelper";

    //Database Columns
    private String ID = "_id";
    private String NAME = "Name";
    private String USSD = "USSD";
    private String IMAGE = "IMG_ID";

    //Database Tables
    private String CARRIER = "Carrier";

    private String SQL_CREATE_ENTRIES = "CREATE TABLE " + CARRIER + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT," + USSD + " TEXT, " + IMAGE + " BLOB)";
    private String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Carrier";

    public DataBaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public synchronized void close()
    {
        if (db != null)
            db.close();
        super.close();
    }


    public void createDataBase() throws IOException
    {

        boolean dbExist = checkDataBase();

        if (!dbExist)
        {

            //overwrite the empty database with pre-populated database.
            this.getReadableDatabase();
            try
            {

                copyDataBase();

            } catch (IOException e)
            {

                throw new Error("Error copying database");

            }
        }

    }


    //Check if the database already exist to avoid re-copying the file each time app launches
    private boolean checkDataBase()
    {

        SQLiteDatabase db = null;

        try
        {
            String myPath = DB_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e)
        {
            //database does't exist yet.

        }

        if (db != null)
        {

            db.close();

        }

        return db != null;
    }

    //Copies the database from local assets-folder to the just created empty database in the
    //system folder, from where it can be accessed and handled.
    private void copyDataBase() throws IOException
    {

        //Open local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }


    public void openDataBase() throws SQLException
    {

        //Open the database
        String Path = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(Path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

    }

    /****
     *
     * CRUD Methods
     *
     *****/

    public void insertCarrier(String name, String ussd, Bitmap image)
    {
        try{
            ContentValues values = new ContentValues();

            values.put(NAME, name);
            values.put(USSD, ussd);
            values.put(IMAGE, DbBitmapUtility.getBytes(image));

            db.insert(CARRIER, null, values);
        }
        catch (IOException e)
        {
            Log.e(TAG,"IO Exception in insertCarrier() ");
        }

    }

    //returns all the records of carriers
    public ArrayList<Carrier> getAll()
    {

        ArrayList<Carrier> list = new ArrayList<>();


        Cursor cursor = db.query(CARRIER, null, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
                String name = cursor.getString(cursor.getColumnIndex(NAME));
                int id = cursor.getInt(cursor.getColumnIndex(ID));
                String ussd = cursor.getString(cursor.getColumnIndex(USSD));

                Bitmap image = DbBitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex(IMAGE)));

                Carrier carrier = new Carrier(id, name, ussd, image);
                list.add(carrier);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }


    public Carrier getCarrier(int id) throws NullPointerException
    {

        Carrier carrier = null;
        Cursor cursor = db.query(CARRIER, null, ID + " = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToFirst())
        {

            String name = cursor.getString(cursor.getColumnIndex(NAME));

            String ussd = cursor.getString(cursor.getColumnIndex(USSD));
            Bitmap image = DbBitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex(IMAGE)));

            carrier = new Carrier(id, name, ussd, image);


        }
        cursor.close();
        return carrier;
    }

    public int updateCarrier(Carrier carrier)
    {
        ContentValues values = new ContentValues();
        try
        {


            values.put(ID, carrier.getId());
            values.put(NAME, carrier.getName());
            values.put(USSD, carrier.getUssd());
            values.put(IMAGE, DbBitmapUtility.getBytes(carrier.getImage()));


        }
        catch (IOException e)
        {
            Log.e(TAG,"IO exception in updateCarrier() ");
        }
        return db.update(CARRIER, values, ID + " = ?", new String[]{Integer.toString(carrier.getId())});
    }

    public int deleteCarrier(Carrier carrier)
    {

        return db.delete(CARRIER, ID + " = ?", new String[]{Integer.toString(carrier.getId())});

    }

}
