package com.whitedevils.vrtex20;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //SQLite Database

    DBhelper db;

    //TextInputEditText invoice, gst, mobile;
    Spinner productSpinner;
    EditText price, qty;  //, address1, address2, address3;
    TextInputEditText user,pass;
    Button login;
    Button addButton, printButton;
    ListView recyclerView;
    TextView total;
    int totalQty = 0;
    int tempTotal = 0;
    int productTotal;
    int count = 0;
    ArrayList<productModel> productList = new ArrayList<>();
    ListAdapter adapter;


    String invoiceno = "INV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBhelper(this);

        //invoice = findViewById(R.id.invoiceno_editview);
        //invoice.setEnabled(false);

        //invoice.setText(invoiceno + getInvoiceMonth() + getInvoiceYaer() + "D" + getInvoiceDate() + "T" + getTime() );

//        gst = findViewById(R.id.gstno_editview);
//        mobile = findViewById(R.id.mobile_editview);
//        address1 = findViewById(R.id.address_editview_line_1);
//        address2 = findViewById(R.id.address_editview_line_2);
//        address3 = findViewById(R.id.address_editview_line_3);

        productSpinner = findViewById(R.id.product_spinner);
        price = findViewById(R.id.price_editview);
        qty = findViewById(R.id.quantity_editview);
        addButton = findViewById(R.id.add_button);
        recyclerView = findViewById(R.id.recyclerview);
        total = findViewById(R.id.total_textview);
        printButton = findViewById(R.id.print_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PackageManager.PERMISSION_GRANTED);
        }

        printButton.setOnClickListener(v -> {
            genbill();
            count=0;
        });

        addButton.setOnClickListener(v -> add());

        recyclerView.setOnItemLongClickListener((parent, view, position, id) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Do you want to delete item ? ");
            builder.setTitle("Delete Item");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(productList.size()!=0){
                        productList.remove(position);
                        tempTotal=0;
                        totalQty =0;
                        for(int i=0;i<productList.size();i++){
                            productTotal = Integer.parseInt(productList.get(i).getQty()) * Integer.parseInt(productList.get(i).getPrice());
                            totalQty += Integer.parseInt(productList.get(i).getQty());
                            tempTotal+=productTotal;
                            total.setText(String.valueOf(tempTotal));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    Toast.makeText(MainActivity.this, "item deleted", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        });
    }



    private void clearFields() {
//        invoice.setText("");
//        gst.setText("");
//        mobile.setText("");
//        address1.setText("");
//        address2.setText("");
//        address3.setText("");
        productList.clear();
        adapter.notifyDataSetChanged();
        total.setText("0");
        productTotal =0;
        totalQty=0;
        tempTotal=0;
    }

    private void add() {
        String product;
        String cPrice = price.getText().toString();
        String cQty = qty.getText().toString();

        String[] price = cPrice.split(".");

        for (String pri: price){
            Log.d("TAG", "add: one" + pri);
        }


        if (!productSpinner.getSelectedItem().toString().equals("==== Select Item ====") && !TextUtils.isEmpty(cPrice) && !TextUtils.isEmpty(cQty)) {
            product = productSpinner.getSelectedItem().toString();
            productTotal = Integer.parseInt(cPrice) * Integer.parseInt(cQty);
            totalQty += Integer.parseInt(cQty);
            tempTotal += productTotal;
            total.setText(String.valueOf(tempTotal));
            productList.add(new productModel(product, cPrice, cQty));
            adapter = new ListAdapter(MainActivity.this, R.layout.items_adapter, productList);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Please Select Product Item", Toast.LENGTH_SHORT).show();
        }
//        price.setText("");
//        qty.setText("");
    }

    public String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    public String getTime(){
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        return time;
    }

    public String getInvoiceDate(){
        DateFormat todayFormat = new SimpleDateFormat("dd");
        Date today = new Date();
        return todayFormat.format(today);
    }

    public String getInvoiceYaer(){
        DateFormat yearFormat = new SimpleDateFormat("yyyy");
        Date year = new Date();
        return yearFormat.format(year);
    }

    public String getInvoiceMonth(){
        DateFormat monthFormat = new SimpleDateFormat("MM");
        Date month = new Date();
        return monthFormat.format(month);
    }

    private void genbill() {

//        String gstno;
//        String mobileno;
//        String customerAddress1;
//        String customerAddress2;
//        String customerAddress3;
//        invoiceno = invoice.getText().toString();
//        gstno = gst.getText().toString();
//        mobileno = mobile.getText().toString();
//        customerAddress1 = address1.getText().toString();
//        customerAddress2 = address2.getText().toString();
//        customerAddress3 = address3.getText().toString();



        String invoice = invoiceno + getInvoiceMonth() + getInvoiceYaer() + "D" + getInvoiceDate() + "T" + getTime();

        PdfDocument pdfDocument = new PdfDocument();
        Paint myPaint = new Paint();

        ArrayList<PdfDocument.PageInfo> pageInfo = new ArrayList<>();
        ArrayList<PdfDocument.Page> myPage = new ArrayList<>();
        ArrayList<Canvas> canvas = new ArrayList<>();

        float n =(float) productList.size()/24;
        int nInInteger = productList.size()/24;

        Log.d("main", "genbill: "+n);
        Log.d("main", "genbill: "+nInInteger);
        Log.d("main", "genbill: "+(nInInteger<n));

        if(nInInteger<n){

            nInInteger++;

        }

        for (int pages=0;pages<nInInteger;pages++) {


            switch (productList.size()){
                case 1:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 2:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 3:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 4:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 5:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 6:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 7:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 8:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 9:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 10:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 11:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 12:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 13:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 14:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 15:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 16:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 17:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 18:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 19:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 20:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 21:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 22:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 23:
                    Log.d("size", "check list size" + productList.size());
                    break;
                case 24:
                    Log.d("size", "check list size" + productList.size());
                    break;
            }

            //pageInfo.add(new PdfDocument.PageInfo.Builder(2480, 3279, pages).create());

            pageInfo.add(new PdfDocument.PageInfo.Builder(2480, 3279, pages).create());
            myPage.add(pdfDocument.startPage(pageInfo.get(pages)));
            canvas.add(myPage.get(pages).getCanvas());

//                    myPaint.setStrokeWidth(5f);
//                    canvas.get(pages).drawLine(80, 80, 2450, 80, myPaint); // top of the border
//                    canvas.get(pages).drawLine(80, 80, 80, 3343, myPaint); // left side of the border
//                    canvas.get(pages).drawLine(2450, 80, 2450, 3343, myPaint); // right side of the border
//                    canvas.get(pages).drawLine(80, 3343, 2450  , 3343, myPaint); // bottom of the border

            myPaint.setTextSize(90f);

            myPaint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.get(pages).drawText("MEMO", 1086, 332, myPaint);              // Quotion title
            canvas.get(pages).drawLine(80, 414, 2450, 414, myPaint);        // TODO #### memo below line ####2
            myPaint.setTextSize(70f);
            canvas.get(pages).drawText("Date:", 1760, 160, myPaint);
            canvas.get(pages).drawText(getDate(), 1950, 160, myPaint);
            canvas.get(pages).drawText("Time:", 1750, 270, myPaint);
            canvas.get(pages).drawText(getTime(), 1950, 270, myPaint);
            canvas.get(pages).drawText("Invoice No: ", 102, 160, myPaint);
            canvas.get(pages).drawText(invoice, 470, 160, myPaint);                    // Invoice no from user
            //canvas.get(pages).drawText("Invoice Date", 1623, 490, myPaint);
            //canvas.get(pages).drawText(getDate(), 2050, 490, myPaint);             // Invoice Date from user
            canvas.get(pages).drawLine(80, 536, 2450, 536, myPaint);        // TODO #### invNo invdate below line
            //canvas.get(pages).drawLine(1331, 536, 1331, 1010, myPaint);     // TODO #### address mobile between line
            //canvas.get(pages).drawLine(80, 550, 2450, 550, myPaint);      // TODO #### title top line
            //canvas.get(pages).drawLine(80, 672, 2450, 672, myPaint);      // TODO #### title below line
            canvas.get(pages).drawLine(80, 3071, 2450, 3071, myPaint);      // TODO #### continue top line ####
            canvas.get(pages).drawLine(242, 536, 242, 3071, myPaint);     // TODO #### sno proname between line
            canvas.get(pages).drawLine(1250, 536, 1250, 3071, myPaint);   // TODO #### proname qty between line
            canvas.get(pages).drawLine(1561, 536, 1561, 3071, myPaint);   // TODO #### qty rate between line
            canvas.get(pages).drawLine(1896, 536, 1896, 3071, myPaint);   // TODO #### rate amount between line
            canvas.get(pages).drawLine(80, 3193, 2450, 3193, myPaint);  // TODO #### continue below line ####

            //canvas.drawText("PREPARED BY", 151, 3285, myPaint);

            Log.d("size alter", "genbill: " + productList.size());


            if(pages == nInInteger-1) {
                //canvas.get(pages).drawText(NumberToText.convertToIndianCurrency(String.valueOf(tempTotal)), 151, 3152, myPaint);
                canvas.get(pages).drawText("TOTAL", 1000, 3050, myPaint);
                canvas.get(pages).drawText(String.valueOf(totalQty), 1337, 3050, myPaint);                                          // total quantity
                canvas.get(pages).drawText(String.valueOf(tempTotal), 2100, 3050, myPaint);
                canvas.get(pages).drawText(String.valueOf(pages+1), 1250, 3285, myPaint);
                // total amount
            }else{
                canvas.get(pages).drawText("Continue", 1150, 3152, myPaint);
                canvas.get(pages).drawText(String.valueOf(pages+1), 1250, 3285, myPaint);
            }

            addlist(invoice, tempTotal);

//                    Boolean checkdetails = db.chkdetails(invoiceno);
//                    if (checkdetails==true){
//                        Boolean linsert = db.listinsert(getDate(),invoiceno,String.valueOf(tempTotal));
//                        if (linsert==true){
//                            Toast.makeText(this, "Updated into DB!....", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(this, "Update failed!...", Toast.LENGTH_SHORT).show();
//                        }
//                    }else{
//                        Toast.makeText(this, "Already Exists!....", Toast.LENGTH_SHORT).show();
//                    }

            canvas.get(pages).drawText("S.no", 90, 490, myPaint);
            canvas.get(pages).drawText("Product Name", 546, 490, myPaint);
            canvas.get(pages).drawText("Qty", 1369, 490, myPaint);
            canvas.get(pages).drawText("Rate", 1677, 490, myPaint);
            canvas.get(pages).drawText("Amount", 2025, 490, myPaint);

                    /*String address = customerAddress;

                    char wordArr[] = address.toCharArray();

                    ArrayList<String> list1 = new ArrayList<>();
                    String a = "";
                    int wordsize = Math.min(address.length(), 280);
                    int limit = 20;
                    for (int i = 0; i < wordsize; i++) {
                        if (i == limit) {
                            list1.add(a);
                            limit += 20;
                            a = "";
                            a += wordArr[i];
                        } else {
                            a += wordArr[i];
                        }
                        if (i == address.length() - 1) {
                            list1.add(a);
                        }
                    }
                    int y = 600;
                    for (String temp : list1) {
                        canvas.get(pages).drawText(temp, 121, y, myPaint);
                        y += 80;
                    }
                     */

//                    canvas.get(pages).drawText(customerAddress1, 150, 620, myPaint);
//                    canvas.get(pages).drawText(customerAddress2, 150, 690, myPaint);
//                    canvas.get(pages).drawText(customerAddress3, 150, 760, myPaint);

//                    canvas.get(pages).drawText("Mob No : ", 1416, 600, myPaint);
//                    canvas.get(pages).drawText(mobileno, 1695, 600, myPaint);
//
//                    canvas.get(pages).drawText("GST No : ", 1416, 664, myPaint);
//                    canvas.get(pages).drawText(gstno, 1695, 664, myPaint);

            int productY = 620;

            int printLimit = (productList.size()<=((pages+1)*24))?productList.size():((pages+1)*24);

            Log.d("count", "genbill: "+count);

            for (int i = count; i < printLimit; i++) {
                canvas.get(pages).drawText("" + (i + 1), 128, productY, myPaint);
                canvas.get(pages).drawText(productList.get(i).getProduct(), 274, productY, myPaint);
                canvas.get(pages).drawText(productList.get(i).getQty(), 1350, productY, myPaint);
                canvas.get(pages).drawText(productList.get(i).getPrice(), 1657, productY, myPaint);
                canvas.get(pages).drawText(String.valueOf(Integer.parseInt(productList.get(i).getQty())*Integer.parseInt(productList.get(i).getPrice())), 2038, productY, myPaint);
                canvas.get(pages).drawLine(80, productY + 30, 2450, productY + 30, myPaint);
                productY += 100;
            }
            count = printLimit;

            pdfDocument.finishPage(myPage.get(pages));
        }
        save(pdfDocument);
        pdfDocument.close();
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(MainActivity.this, Environment.getExternalStorageDirectory() + "/temp/vr.pdf");
        printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        clearFields();
    }

    private void addlist(String invoiceno, int tempTotal) {

        String Date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        Boolean linsert = db.listinsert(Date,invoiceno,String.valueOf(tempTotal));
        if (linsert==true){
            Toast.makeText(this, "Update Success!....", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Update failed!...", Toast.LENGTH_SHORT).show();
        }

//        Boolean checkdetails = db.chkdetails(invoiceno);
//            if (checkdetails==true){
//
//
//
//            }else{
//                Toast.makeText(this, "Already Exists!....", Toast.LENGTH_SHORT).show();
//            }

    }

    private void save(PdfDocument pdfDocument) {
        File file = new File(Environment.getExternalStorageDirectory() + "/temp");
        if (!file.mkdir()) {
            file.mkdir();
        }
        File myFile = new File(Environment.getExternalStorageDirectory(), "/temp/vr.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(myFile));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.list:
                listDialog();
                return true;



                /*
            case R.id.add:
                openRegister();
                return true;
                 */

        }
        return super.onOptionsItemSelected(item);
    }


    private void listDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View v =getLayoutInflater().inflate(R.layout.login_list_box, null);

        user = v.findViewById(R.id.text_username);
        pass = v.findViewById(R.id.text_password);

        dialog.setView(v)
                .setPositiveButton("login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String Mail = user.getText().toString();
                        String Password = pass.getText().toString();

                        Log.d("Input", "mail: " + Mail);
                        Log.d("Input", "mail: " + Password);

                        if (!Mail.equals("") && !Password.equals("")){
                            Boolean checkmailpass = db.mailpassword(Mail,Password);
                            if (checkmailpass==true){
                                Intent i = new Intent(MainActivity.this, Report.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(MainActivity.this, "Mail ID does not exist!....", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(MainActivity.this, "Please Check the Mail and Password!....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    /*
    private void openRegister() {

        EditText name,mail,mob,pass;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View v =getLayoutInflater().inflate(R.layout.register_dialog_box, null);

        name = v.findViewById(R.id.text_username);
        mail = v.findViewById(R.id.text_mail_id);
        mob = v.findViewById(R.id.text_mobile);
        pass = v.findViewById(R.id.text_password);

        dialog.setView(v);

        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String Name = name.getText().toString();
                String Mail = mail.getText().toString();
                String Mobile = mob.getText().toString();
                String Pass = pass.getText().toString();

                if (!Name.equals("") && !Mail.equals("") && !Mobile.equals("") && !Pass.equals("")){
                    Boolean chkmail = db.chkmail(Mail);
                    if (chkmail==true){
                        Boolean insert = db.insert(Name,Mail,Mobile,Pass);
                        if (insert==true){
                            Toast.makeText(MainActivity.this, "Register Successfully!....", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Register unsuccessfull!...", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Mail ID Already exists!....", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainActivity.this, "Please fill all details!......", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }
     */

}