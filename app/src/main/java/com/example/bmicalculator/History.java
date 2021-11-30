package com.example.bmicalculator;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class History extends DBActivity {
    protected EditText editName, editAge, editWeight, editHeight;
    protected Button calcBtn, insBtn;
    protected TextView result;
    protected FloatingActionButton historyBtn;
    protected ListView simpleList;


    protected void FillListView() throws Exception{
        final ArrayList<String> listResults=new ArrayList<>();
        SelectSQL(
                "SELECT * FROM BMItable",
                null,
                (ID, Name, Age, Weight, Height, Result)->{
                    listResults.add(ID + "\n" + Name + "\n" + Age + "\n"  + Weight + " kg\n" + Height + " cm\n" + Result + " BMI\n") ;
                }
        );
        simpleList.clearChoices();
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.activity_list_view,
                R.id.tvEntries,
                listResults
        );
        simpleList.setAdapter(arrayAdapter);
    }

    @Override
    @CallSuper
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        try {
            FillListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        editName=findViewById(R.id.editName);
        editAge=findViewById(R.id.editAge);
        editWeight=findViewById(R.id.editWeight);
        editHeight=findViewById(R.id.editHeight);
        calcBtn=findViewById(R.id.calcBtn);
        insBtn=findViewById(R.id.insBtn);
        result=findViewById(R.id.result);
        historyBtn=findViewById(R.id.historyBtn);
        simpleList=findViewById(R.id.simpleList);


        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clickedText=view.findViewById(R.id.tvEntries);
                String selected = clickedText.getText().toString();
                String[] elements=selected.split("\n");
                String ID=elements[0];
                try{
                    ExecSQL("DELETE FROM BMItable WHERE " +
                                    "ID = ?",
                            new Object[]{ID},
                            ()-> Toast.makeText(getApplicationContext(),
                                    "Delete Successful", Toast.LENGTH_LONG).show()


                    );
                    initDB();
                    FillListView();

                }catch (Exception exception){
                    Toast.makeText(getApplicationContext(),
                            "Delete Error: "+exception.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });





        try {
            initDB();
            FillListView();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}