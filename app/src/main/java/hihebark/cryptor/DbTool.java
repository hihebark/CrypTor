package hihebark.cryptor;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import java.util.ArrayList;

public class DbTool extends SQLiteOpenHelper {
    public static final String BDsql = "EvilCrypt.db";
    public DbTool(Context context) {
        super(context, BDsql, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Utilisateur (id INTEGER PRIMARY KEY, " +
                "username TEXT, " +
                "password TEXT," +
                "passwordR TEXT," +
                "mdpassword TEXT," +
                "passwordStrongbox)");
        db.execSQL("CREATE TABLE FileInformation(IdFile INTEGER PRIMARY KEY, " +
                "NomFile TEXT, " +
                "PathFile TEXT)");
        db.execSQL("CREATE TABLE SmsDatabase (idSMS INTEGER PRIMARY KEY," +
                "Message TEXT," +
                "Number TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Utilisateur");
        db.execSQL("DROP TABLE IF EXISTS FileInformation");
        db.execSQL("DROP TABLE IF EXISTS SmsDatabase");
        onCreate(db);
    }

    public boolean InsertCustomer(String username, String password, String password_recovery, String mdpassword) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("mdpassword", mdpassword);
        contentValues.put("passwordR", password_recovery);
        database.insert("Utilisateur", null, contentValues);
        database.close();
        return true;
    }

    public boolean DeleteCustomer(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("DELETE FROM Utilisateur WHERE id ='0' OR id ='1'", null);
        if(cursor.getCount()==0){
            return true;
        }
        return false;
    }

    public long GetCountTable(){
        SQLiteDatabase database = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(database, "Utilisateur");
    }

    public boolean isSpasswordEmpty(){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM Utilisateur", null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(5)==null || cursor.getString(5).isEmpty()){
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }
    //fix the db connection with the application with defirent version!
    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/sqlite/db/chinook.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public boolean GetMDPassword(String mdpassword){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM Utilisateur WHERE username='User'", null);
        if (res.moveToFirst()) {
            do {
                if(mdpassword.equals(res.getString(4))){
                    return true;
                }
            } while (res.moveToNext());
        }
        return false;
    }

    public boolean GetPasswordR(String passwordR){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM Utilisateur", null);
        if (cursor.moveToFirst()) {
            do {
                if(passwordR.equals(cursor.getString(3))){
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }
    public boolean UpdateUserPassword(String password){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Utilisateur SET password = '"+password+"' WHERE username = 'User'");
        return true;
    }
    public boolean UpdateUserpassStrongbox(String password){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE Utilisateur SET passwordStrongbox = '"+password+"' WHERE username = 'User'");
        return true;
    }
    public boolean VerifyPassword(String Password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM Utilisateur WHERE username='User'", null);
        if (res.moveToFirst()) {
            do {
                if(Password.equals(res.getString(2))){
                    return true;
                }
            } while (res.moveToNext());
        }
        return false;
    }

    public Boolean InsertFile(String NomFile, String PathFile){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NomFile", NomFile);
        contentValues.put("PathFile", PathFile);
        if (database.insert("FileInformation", null, contentValues) == -1) {
            database.close();
            return false;
        }
        database.close();
        return true;
    }

    public Boolean DeleteFile(String NomFile){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("DELETE FROM FileInformation WHERE NomFile ='"+NomFile+"'", null);
        if(cursor.getCount()==0){
            return true;
        }
        return false;
    }

    public String getPathFile(String NomFile){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM FileInformation", null);
        if (cursor.moveToFirst()) {
            do {
                if(NomFile.equals(cursor.getString(1))){
                    return cursor.getString(2);
                }
            } while (cursor.moveToNext());
        }
        return Environment.getExternalStorageDirectory().toString()+"/"+NomFile;
    }

    public Boolean InsertSms(String message, String number){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Message", message);
        contentValues.put("Number", number);
        if (database.insert("SmsDatabase", null, contentValues) == -1) {
            database.close();
            return false;
        }
        database.close();
        return true;
    }
    public ArrayList<String> getSms(String number){

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM SmsDatabase", null);
        ArrayList<String> SmsList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if(number.equals(cursor.getString(2))){
                    SmsList.add(cursor.getString(1));
                }
            } while (cursor.moveToNext());
        }
        return SmsList;

    }
    public long GetCountSms(){
        SQLiteDatabase database = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(database, "SmsDatabase");
    }
    public ArrayList<String> getAllSMSnumber(){

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM SmsDatabase", null);
        ArrayList<String> arrayList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do{
                if(!arrayList.contains(cursor.getString(2))){
                    arrayList.add(cursor.getString(2));
                }
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

}
