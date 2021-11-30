package com.example.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends DBActivity {

    protected EditText editName, editAge, editWeight, editHeight;
    protected Button  calcBtn, insBtn;
    protected TextView result;
    protected FloatingActionButton historyBtn;
    protected ListView simpleList;
    protected void FillListView() throws Exception{
        final ArrayList<String> listResults=new ArrayList<>();
        SelectSQL(
                    "SELECT * FROM BMItable",
                null,
                (ID, Name, Age, Weight, Height, Result)->{
                      listResults.add(ID + "\n" + Name + "\n" + Age + "\n" + Weight + " kg\n" + Height + " cm\n" + Result + " BMI\n") ;
                }
        );
        /*
        simpleList.clearChoices();
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.activity_list_view,
                R.id.tvEntries,
                listResults
        );
        simpleList.setAdapter(arrayAdapter);
        */

    }

    protected void clearFields(){
        editName.setText("");
        editAge.setText("");
        editWeight.setText("");
        editHeight.setText("");
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editName=findViewById(R.id.editName);
        editAge=findViewById(R.id.editAge);
        editWeight=findViewById(R.id.editWeight);
        editHeight=findViewById(R.id.editHeight);
        calcBtn=findViewById(R.id.calcBtn);
        insBtn=findViewById(R.id.insBtn);
        result=findViewById(R.id.result);
        historyBtn=findViewById(R.id.historyBtn);
        simpleList=findViewById(R.id.simpleList);



        try {
            initDB();
            FillListView();


        } catch (Exception e) {
            e.printStackTrace();
        }




        calcBtn.setOnClickListener(view -> {
            try {
                validation(editWeight, editHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
            float h = Float.parseFloat(editHeight.getText().toString()) / 100;

            float w = Float.parseFloat(editWeight.getText().toString());
            if(h<=0||w<=0){
                    Toast.makeText(getApplicationContext(),
                            "Insert failed: enter valid weight/height",
                            Toast.LENGTH_SHORT).show();

            }else {
                float res = (w / (h * h));
                result.setText(String.format("%.2f", res));
            }
        });

        historyBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        } );

        insBtn.setOnClickListener(view -> {

            try {
                validation(editWeight, editHeight);
                ExecSQL("INSERT INTO BMItable(Name, Age, Weight, Height, Result)" +
                                "VALUES (?, ?, ?, ?, ?)",
                        new Object[]{
                                editName.getText().toString(),
                                editAge.getText().toString(),
                                editWeight.getText().toString(),
                                editHeight.getText().toString(),
                                result.getText().toString(),
                        },
                        ()-> Toast.makeText(getApplicationContext(),
                                "Record inserted",
                                Toast.LENGTH_LONG).show()

                );
                //clearFields();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final StringBuilder jsonObject=new StringBuilder();
                        jsonObject.append("{");
                        jsonObject.append("'username': '"+editName.getText().toString()+"', ");
                        jsonObject.append("'name': '"+editName.getText().toString()+"', ");
                        jsonObject.append("'age': '"+editAge.getText().toString()+"', ");
                        jsonObject.append("'weight': '"+editWeight.getText().toString()+"', ");
                        jsonObject.append("'height': '"+editHeight.getText().toString()+"' ");
                        jsonObject.append("'result': '"+result.getText().toString()+"' ");
                        jsonObject.append("}");
                        final StringBuilder fin=new StringBuilder();

                        try {
                            fin.append(postData("SaveToFile",
                                    editName.getText().toString(),
                                    jsonObject.toString()
                            ));
                            JSONObject jo=(JSONObject) new JSONTokener(fin.toString())
                                    .nextValue();
                            final String message=jo.getString("message");
                            if(message==null){
                                throw new Exception("SERVER FAULT: "+fin.toString());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            });
                        }catch (final Exception e){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Exception: "+e.getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                t.start();

                FillListView();
                //Intent intent = new Intent(this, History.class);
                //startActivity(intent);
            }catch(Exception e){
                Toast.makeText(getApplicationContext(),
                        "Insert failed: " + e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }
}