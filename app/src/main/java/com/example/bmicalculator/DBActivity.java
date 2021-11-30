package com.example.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBActivity extends RESTActivity {

    protected interface OnQuerySuccess{
        public void OnSuccess();
    }
    protected interface OnSelectSuccess{
        public void OnElementSelected(
                String ID, String Name, String Age, String Weight, String Height, String Result
        );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_b);
    }
    protected boolean matchString(String string_, String regexp){
        final String regex = regexp;
        final String string = string_;

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            return true;
        }
        return false;
    }
    protected void validation(EditText editWeight, EditText editHeight) throws Exception {
        if(!matchString(editWeight.getText().toString(), "\\d")){
            throw new Exception("Invalid Weight");
        }
        if(!matchString(editHeight.getText().toString(), "\\d")){
            throw new Exception("Invalid Height");
        }
        if(editWeight.getText().toString()==null){
            throw new Exception("Invalid Weight");
        }
        if(editHeight.getText().toString()==null){
            throw new Exception("Invalid Height");
        }
    }

    protected void SelectSQL(String SelectQ, String[] args, OnSelectSuccess success)
            throws Exception
    {
        SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(getFilesDir().getPath()+"/BMI.db", null);
        Cursor cursor = db.rawQuery(SelectQ, args);
        while (cursor.moveToNext()){
            String ID=cursor.getString(cursor.getColumnIndex("ID"));
            String Name=cursor.getString(cursor.getColumnIndex("Name"));
            String Age=cursor.getString(cursor.getColumnIndex("Age"));
            String Weight=cursor.getString(cursor.getColumnIndex("Weight"));
            String Height=cursor.getString(cursor.getColumnIndex("Height"));
            String Result=cursor.getString(cursor.getColumnIndex("Result"));
            success.OnElementSelected(ID, Name, Age, Weight, Height, Result);
        }
        db.close();
    }
    protected void ExecSQL(String SQL, Object[] args, OnQuerySuccess success)
            throws Exception {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().getPath()+"/BMI.db", null);
        if (args != null)
            db.execSQL(SQL, args);
        else
            db.execSQL(SQL);

        db.close();
        success.OnSuccess();
    }

    protected void initDB() throws Exception{
        ExecSQL(
                "CREATE TABLE if not exists BMItable(" +
                        "ID integer PRIMARY KEY AUTOINCREMENT, " +
                        "Name text not null, " +
                        "Age text not null, " +
                        "Weight text not null, " +
                        "Height text not null, " +
                        "Result text not null " +
                        ")", null, ()-> Toast.makeText(getApplicationContext(),
                        "DB init successfull",
                        Toast.LENGTH_LONG).show()
        );
    }
}