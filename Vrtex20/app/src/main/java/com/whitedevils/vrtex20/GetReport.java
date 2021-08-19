package com.whitedevils.vrtex20;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class GetReport extends AppCompatActivity {

    TextView bills,bTotal,fdate,tdate;
    Button search,clear;
    ArrayList<String> date,invoice,total;
    RecyclerView recyclerView;
    DBhelper db;
    Context context;
    DatePickerDialog datePickerDialog;
    reportAdapter reportAdapter;
    int fTotal=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_report);


        fdate = findViewById(R.id.fromDate);
        tdate = findViewById(R.id.toDate);
        recyclerView = findViewById(R.id.recyclerview);
        search = findViewById(R.id.getreport);
        bills = findViewById(R.id.bills);
        bTotal = findViewById(R.id.total);
        clear = findViewById(R.id.clear);

        fdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int fyear = c.get(Calendar.YEAR); //current year
                int fmonth = c.get(Calendar.MONTH); //current month
                int fday = c.get(Calendar.DAY_OF_MONTH); //current day
                //date picker dialog
                datePickerDialog = new DatePickerDialog(GetReport.this,
                        (View, year, monthOfYear, dayOfmonth) -> {
                            fdate.setText(dayOfmonth + "/" + (monthOfYear) + "/" + year);
                        },fyear,fmonth,fday);
                datePickerDialog.show();
            }
        });

        tdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int fyear = c.get(Calendar.YEAR); //current year
                int fmonth = c.get(Calendar.MONTH); //current month
                int fday = c.get(Calendar.DAY_OF_MONTH); //current day
                //date picker dialog
                datePickerDialog = new DatePickerDialog(GetReport.this,
                        (View, year, monthOfYear, dayOfmonth) -> {
                            tdate.setText(dayOfmonth + "/" + (monthOfYear) + "/" + year);
                        },fyear,fmonth,fday);
                datePickerDialog.show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("fdate", "onClick: " + fdate);
                Log.d("tdate", "onClick: " + tdate);




                if (fdate != null ){
                    Log.d("date check", "onClick: " + fdate);
                    //db.search(dateT); // filter by string
                    //reportAdapter = new reportAdapter();

                    try {
//                        db =  new DBhelper(GetReport.this);
//                        date = new ArrayList<>();
//                        invoice = new ArrayList<>();
//                        total = new ArrayList<>();

                        storeDataInArray();
//                        reportAdapter = new reportAdapter(context,date,invoice,total);
//                        recyclerView.setAdapter(reportAdapter);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("TAG", "onClick: exception" + e.getMessage());
                    }
                }else{
                    Toast.makeText(GetReport.this, "Please enter the Invoice No!....", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GetReport.this, "under construction!...", Toast.LENGTH_SHORT).show();
            }
        });

//        db =  new DBhelper(this);
//        date = new ArrayList<>();
//        invoice = new ArrayList<>();
//        total = new ArrayList<>();
//
//        storeDataInArray();
//        reportAdapter = new reportAdapter(this,date,invoice,total);
//        Collections.reverse(date);
//        Collections.reverse(invoice);
//        Collections.reverse(total);
//        recyclerView.setAdapter(reportAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    void storeDataInArray(){
        Cursor cursor = db.search(String.valueOf(fdate));
        if (cursor.getCount() == 0){

        }else{
            while(cursor.moveToNext()){
                date.add(cursor.getString(0));
                invoice.add(cursor.getString(1));
                total.add(cursor.getString(2));

                Log.d("TAG", "storeDataInArray: " + total);

                bills.setText(String.valueOf(total.size()));


                db =  new DBhelper(GetReport.this);
                date = new ArrayList<>();
                invoice = new ArrayList<>();
                total = new ArrayList<>();

                reportAdapter = new reportAdapter(context,date,invoice,total);
                recyclerView.setAdapter(reportAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                Log.d("success", "storeDataInArray: " + cursor);

            }
        }
    }


}