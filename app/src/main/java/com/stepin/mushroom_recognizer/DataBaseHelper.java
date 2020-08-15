package com.stepin.mushroom_recognizer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DataBaseHelper extends SQLiteOpenHelper {

    // путь к базе данных вашего приложенияSQLiteDatabase: /data/user/0/com.example.application_from_contentprovider/databases/MyDB     "/data/data/com.example.application_from_contentprovider/databases/"
    private static String DB_PATH = "/data/user/0/com.stepin.mushroom_recognizer/databases/";
    private static String DB_NAME = "MyDB2";
    private SQLiteDatabase myDataBase;
    private final Context mContext;

    /**
     * Конструктор
     * Принимает и сохраняет ссылку на переданный контекст для доступа к ресурсам приложения
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if(dbExist){
            //ничего не делать - база уже есть
        }else{
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();
            Log.d("LOG", "создаем пустую базу");

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Ошибка копирования базы");
            }
        }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;
        try{
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteCantOpenDatabaseException ee)
        {
            Log.d("LOG", "База еще не существует");
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
       //return false;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
    private void copyDataBase() throws IOException{
        myDataBase = this.getReadableDatabase();
        String outFileName = myDataBase.getPath();
        myDataBase.close();
        //Открываем локальную БД как входящий поток
        InputStream myInput = (mContext.getAssets()).open(DB_NAME);
        Log.d("LOG", "Открываем локальную БД как входящий поток");
        //Путь ко вновь созданной БД
        //String outFileName = DB_PATH + DB_NAME;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);
        Log.d("LOG", "Открываем пустую базу данных как исходящий поток");

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //открываем БД
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        //Cursor c = myDataBase.query("android_metadata", null,null,null,null,null,null);
       // Cursor c = myDataBase.query("EMP_TABLE", null,null,null,null,null,null);
      //  Cursor c = myDataBase.query("MUSHROOMS", null,null,null,null,null,null);
     //   c.moveToFirst();
      //  Log.d("LOG", c.getString(c.getColumnIndex("name")));
      //  do {
///            Log.d("LOG", c.getString(c.getColumnIndex("name")));
      //  } while (c.moveToNext());
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    public  Cursor cursorReturn_id(int id)
    {
        return myDataBase.query("MUSHROOMS", null, "_id = ?", new String[]{String.valueOf(id)}, null,null,null);
    }

    public Cursor cursorReturn()
    {
        String[] parameters_names_array = new String[]{"body_shape LIKE ?", "mushroom_pulp LIKE ?", "hat_surface LIKE ?", "hymenophore_type LIKE ?", "hat_shape LIKE ?",
                "connection_hat_leg LIKE ?", "leg_surface LIKE ?", "position_hat_leg LIKE ?", "leg_shape LIKE ?"};
        String selection = null;
        String[] selection_args = null;

        int not_null = 0;
        for (int i = 0; i < activity_parameter_selection.PARAMETERS_COUNT; i++)
        {
            if (MainActivity.parameters_map.get(i) != 0)
            {
                if (selection == null) {selection = new String(); selection = "";}
                if (selection.equals(""))
                selection+=parameters_names_array[i];
                else selection+= " AND " + parameters_names_array[i];
                not_null++;
            }
        }
        if (not_null > 0)
            selection_args = new String[not_null];
        not_null = 0;

        for (int i = 0; i < activity_parameter_selection.PARAMETERS_COUNT; i++)
        {
            if (MainActivity.parameters_map.get(i) != 0)
            {
                selection_args[not_null] = "%$" + String.valueOf(MainActivity.parameters_map.get(i))+"$%";
                not_null++;
            }
        }
        Cursor c = myDataBase.query("MUSHROOMS",null,null,null,null,null,null);
        return myDataBase.query("MUSHROOMS", null, selection, selection_args,null,null,"name");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // Здесь можно добавить вспомогательные методы для доступа и получения данных из БД
    // вы можете возвращать курсоры через "return myDataBase.query(....)", это облегчит их использование
    // в создании адаптеров для ваших view
}