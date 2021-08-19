package com.whitedevils.vrtex20;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class Report extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> date,invoice,total;
    DBhelper db;
    reportAdapter reportAdapter;

    ImageView imageView;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        imageView = findViewById(R.id.image);
        text = findViewById(R.id.textNodata);

        imageView.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db =  new DBhelper(this);
        date = new ArrayList<>();
        invoice = new ArrayList<>();
        total = new ArrayList<>();

        storeDataInArray();
        reportAdapter = new reportAdapter(this,date,invoice,total);
        Collections.reverse(date);
        Collections.reverse(invoice);
        Collections.reverse(total);
        recyclerView.setAdapter(reportAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.search:
                openGetReport();
                return true;

            case R.id.delete:
                deleteTeble();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTeble() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Warning!....");
        alert.setIcon(R.drawable.ic_error);
        alert.setMessage("Do you want to delete all datas from database!....");
        alert.setCancelable(false);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Report.this, "Under Construction!...", Toast.LENGTH_SHORT).show();

                db.deleteTable();
                recreate();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    private void openGetReport() {
        Intent i = new Intent(this,GetReport.class);
        startActivity(i);
        //Toast.makeText(this, "under construction!...", Toast.LENGTH_SHORT).show();
    }

    void storeDataInArray(){
        Cursor cursor = db.readAlldata();
        if (cursor.getCount() == 0){
            imageView.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
        }else{
            while(cursor.moveToNext()){
                date.add(cursor.getString(0));
                invoice.add(cursor.getString(1));
                total.add(cursor.getString(2));
            }
        }
    }



}