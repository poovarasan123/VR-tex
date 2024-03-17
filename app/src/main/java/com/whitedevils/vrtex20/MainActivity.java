package com.whitedevils.vrtex20;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    TextInputEditText invoice, gst, mobile, address;
    EditText address1;
    EditText address2;
    EditText address3;
    Spinner productSpinner;
    EditText price, qty;
    Button addButton, printButton;
    ListView recyclerView;
    TextView total;
    int totalQty = 0;
    int tempTotal = 0;
    int productTotal;
    int count = 0;
    ArrayList<productModel> productList = new ArrayList<>();
    ListAdapter adapter;

    private static final int PICK_FILE_REQUEST_CODE = 123;

    Bitmap godBitmap;
    Bitmap back;
    long currentTimestamp;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        godBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.murugan2);
        back = BitmapFactory.decodeResource(getResources(), R.drawable.vr_back);
        currentTimestamp = System.currentTimeMillis();

//        for(int i = 1; i <= 32; i++){
//            productList.add(new productModel("product", String.valueOf(i), String.valueOf(i)));
//        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
            }, PackageManager.PERMISSION_GRANTED);
        }

        invoice = findViewById(R.id.invoiceno_editview);
        gst = findViewById(R.id.gstno_editview);
        mobile = findViewById(R.id.mobile_editview);
        //address = findViewById(R.id.address_editview);

        address1 = findViewById(R.id.address_editview_line_1);
        address2 = findViewById(R.id.address_editview_line_2);
        address3 = findViewById(R.id.address_editview_line_3);

        productSpinner = findViewById(R.id.product_spinner);
        price = findViewById(R.id.price_editview);
        qty = findViewById(R.id.quantity_editview);
        addButton = findViewById(R.id.add_button);
        recyclerView = findViewById(R.id.recyclerview);
        total = findViewById(R.id.total_textview);
        printButton = findViewById(R.id.print_button);


        printButton.setOnClickListener(v -> {
            genbill();
            count = 0;
        });

        addButton.setOnClickListener(v -> add());

        recyclerView.setOnItemLongClickListener((parent, view, position, id) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Do you want to delete item ? ");
            builder.setTitle("Delete Item");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (productList.size() != 0) {
                        productList.remove(position);
                        tempTotal = 0;
                        totalQty = 0;
                        for (int i = 0; i < productList.size(); i++) {
                            productTotal = Integer.parseInt(productList.get(i).getQty()) * Integer.parseInt(productList.get(i).getPrice());
                            totalQty += Integer.parseInt(productList.get(i).getQty());
                            tempTotal += productTotal;
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

    private boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }



    private void clearFields() {
        invoice.setText("");
        gst.setText("");
        mobile.setText("");
        //address.setText("");
        address1.setText("");
        address2.setText("");
        address3.setText("");
        productList.clear();
        adapter.notifyDataSetChanged();
        total.setText("0");
        productTotal = 0;
        totalQty = 0;
        tempTotal = 0;
    }

    private void add() {
        String product;
        String cPrice = price.getText().toString();
        String cQty = qty.getText().toString();
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
        price.setText("");
        qty.setText("");
    }

    public String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    private void genbill() {

        String invoiceno;
        String gstno;
        String mobileno;
        String customerAddress;
        String customerAddress2;
        String customerAddress3;

        if (invoice.getText() != null && mobile.getText() != null && address1.getText() != null && address2.getText() != null && address3.getText() != null) {
            invoiceno = invoice.getText().toString();
            gstno = gst.getText().toString();
            mobileno = mobile.getText().toString();

            customerAddress = address1.getText().toString();
            customerAddress2 = address2.getText().toString();
            customerAddress3 = address3.getText().toString();

            if (!TextUtils.isEmpty(invoiceno) && !TextUtils.isEmpty(mobileno) && !TextUtils.isEmpty(customerAddress)) {

                PdfDocument pdfDocument = new PdfDocument();
                Paint myPaint = new Paint();


                ArrayList<PdfDocument.PageInfo> pageInfo = new ArrayList<>();
                ArrayList<PdfDocument.Page> myPage = new ArrayList<>();
                ArrayList<Canvas> canvas = new ArrayList<>();

                float n = (float) productList.size() / 16;
                int nInInteger = productList.size() / 16;
                Log.d("main", "genbill: " + n);
                Log.d("main", "genbill: " + nInInteger);
                Log.d("main", "genbill: " + (nInInteger < n));
                if (nInInteger < n) {
                    nInInteger++;
                }
                for (int pages = 0; pages < nInInteger; pages++) {
                    pageInfo.add(new PdfDocument.PageInfo.Builder(2480, 3508, pages).create());
                    myPage.add(pdfDocument.startPage(pageInfo.get(pages)));
                    canvas.add(myPage.get(pages).getCanvas());

                    canvas.get(pages).drawBitmap(godBitmap, 600, 150, null);
                    canvas.get(pages).drawBitmap(back, 585, 1358, null);

                    myPaint.setStrokeWidth(5f);
                    canvas.get(pages).drawLine(80, 80, 2450, 80, myPaint); // top of the border
                    canvas.get(pages).drawLine(80, 80, 80, 3343, myPaint); // left side of the border
                    canvas.get(pages).drawLine(2450, 80, 2450, 3343, myPaint); // right side of the border
                    canvas.get(pages).drawLine(80, 3343, 2450, 3343, myPaint); // bottom of the border

                    myPaint.setTextSize(90f);


                    myPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    canvas.get(pages).drawText("Quotation", 1086, 332, myPaint);              // Quotation title
                    canvas.get(pages).drawLine(80, 414, 2450, 414, myPaint);
                    myPaint.setTextSize(70f);
                    canvas.get(pages).drawText("Ph :", 1791, 160, myPaint);
                    canvas.get(pages).drawText("7200053683", 1950, 160, myPaint);
                    canvas.get(pages).drawText("7200053685", 1950, 270, myPaint);
                    canvas.get(pages).drawText("Invoice No", 102, 490, myPaint);
                    canvas.get(pages).drawText("Invoice No", 102, 490, myPaint);
                    canvas.get(pages).drawText(invoiceno, 450, 490, myPaint);                    // Invoice no from user
                    canvas.get(pages).drawText("Invoice Date", 1623, 490, myPaint);
                    canvas.get(pages).drawText(getDate(), 2050, 490, myPaint);             // Invoice Date from user
                    canvas.get(pages).drawLine(80, 536, 2450, 536, myPaint);
                    canvas.get(pages).drawLine(1331, 536, 1331, 1010, myPaint);
                    canvas.get(pages).drawLine(80, 1010, 2450, 1010, myPaint);
                    canvas.get(pages).drawLine(80, 1132, 2450, 1132, myPaint);
                    canvas.get(pages).drawLine(80, 3071, 2450, 3071, myPaint);
                    canvas.get(pages).drawLine(242, 1010, 242, 3071, myPaint);
                    canvas.get(pages).drawLine(1250, 1010, 1250, 3071, myPaint);
                    canvas.get(pages).drawLine(1561, 1010, 1561, 3071, myPaint);
                    canvas.get(pages).drawLine(1896, 1010, 1896, 3071, myPaint);
                    canvas.get(pages).drawLine(80, 3193, 2450, 3193, myPaint);

                    //canvas.drawText("PREPARED BY", 151, 3285, myPaint);
                    //
                    if (pages == nInInteger - 1) {
                        double subtotal = tempTotal;
                        double gst = calculateGST(Double.parseDouble(String.valueOf(tempTotal)));
                        double finalTotal = tempTotal + gst;
                        DecimalFormat df = new DecimalFormat("#0.00");

                        canvas.get(pages).drawText(NumberToText.convertToIndianCurrency(String.valueOf(finalTotal)), 151, 3152, myPaint);

                        canvas.get(pages).drawText("SUB TOTAL", 860, 2830, myPaint);
                        canvas.get(pages).drawText("GST ( 5% )", 900, 2920, myPaint);
                        canvas.get(pages).drawText("TOTAL", 1000, 3010, myPaint);
                        canvas.get(pages).drawText(String.valueOf(totalQty), 1337, 3010, myPaint);                                          // total quantity
                        canvas.get(pages).drawText(df.format(subtotal), 2040, 2830, myPaint);
                        canvas.get(pages).drawText(df.format(gst), 2040, 2920, myPaint);
                        canvas.get(pages).drawText(df.format(finalTotal), 2040, 3010, myPaint);
                        canvas.get(pages).drawText(String.valueOf(pages + 1), 1250, 3285, myPaint);
                        // total amount
                    } else {
                        canvas.get(pages).drawText("Continue", 1150, 3152, myPaint);
                        canvas.get(pages).drawText(String.valueOf(pages + 1), 1250, 3285, myPaint);
                    }
                    canvas.get(pages).drawText("S.no", 90, 1090, myPaint);
                    canvas.get(pages).drawText("Product Name", 546, 1090, myPaint);
                    canvas.get(pages).drawText("Qty", 1369, 1090, myPaint);
                    canvas.get(pages).drawText("Rate", 1677, 1090, myPaint);
                    canvas.get(pages).drawText("Amount", 2025, 1090, myPaint);

                    //String address = customerAddress;

                    canvas.get(pages).drawText(customerAddress, 150.0f, 620.0f, myPaint);
                    canvas.get(pages).drawText(customerAddress2, 150.0f, 690.0f, myPaint);
                    canvas.get(pages).drawText(customerAddress3, 150.0f, 760.0f, myPaint);

//                    char wordArr[] = address.toCharArray();
//
//                    ArrayList<String> list1 = new ArrayList<>();
//                    String a = "";
//                    int wordsize = Math.min(address.length(), 280);
//                    int limit = 20;
//                    for (int i = 0; i < wordsize; i++) {
//                        if (i == limit) {
//                            list1.add(a);
//                            limit += 20;
//                            a = "";
//                            a += wordArr[i];
//                        } else {
//                            a += wordArr[i];
//                        }
//                        if (i == address.length() - 1) {
//                            list1.add(a);
//                        }
//                    }
//                    int y = 600;
//                    for (String temp : list1) {
//                        canvas.get(pages).drawText(temp, 121, y, myPaint);
//                        y += 80;
//                    }

                    canvas.get(pages).drawText("Mob No : ", 1416, 600, myPaint);
                    canvas.get(pages).drawText(mobileno, 1695, 600, myPaint);

                    canvas.get(pages).drawText("GST No : ", 1416, 664, myPaint);
                    canvas.get(pages).drawText(gstno, 1695, 664, myPaint);

                    int productY = 1200;

                    int printLimit = (productList.size() <= ((pages + 1) * 16)) ? productList.size() : ((pages + 1) * 16);

                    Log.d("count", "genbill: " + count);

                    for (int i = count; i < printLimit; i++) {
                        canvas.get(pages).drawText("" + (i + 1), 128, productY, myPaint);
                        canvas.get(pages).drawText(productList.get(i).getProduct(), 274, productY, myPaint);
                        canvas.get(pages).drawText(productList.get(i).getQty(), 1350, productY, myPaint);
                        canvas.get(pages).drawText(productList.get(i).getPrice(), 1657, productY, myPaint);
                        canvas.get(pages).drawText(String.valueOf(Integer.parseInt(productList.get(i).getQty()) * Integer.parseInt(productList.get(i).getPrice())), 2038, productY, myPaint);
                        canvas.get(pages).drawLine(80, productY + 30, 2450, productY + 30, myPaint);
                        productY += 100;
                    }
                    count = printLimit;

                    pdfDocument.finishPage(myPage.get(pages));
                }
                save(pdfDocument);
                pdfDocument.close();
                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(MainActivity.this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/vr.pdf");
                printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
                clearFields();
            } else {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static double calculateGST(double price) {
        //Calculate GST amount (5% of the price)
        return price * 0.05;
    }

    private void save(PdfDocument pdfDocument) {
        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
        if (!file.isDirectory()) {
            file.mkdir();
        }
        File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "/vr_" + currentTimestamp +".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(myFile));

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "save: " + e.getMessage());
        }
    }

    private void printPdf(Uri pdfUri) {
        // Create a PdfRenderer instance
        PdfRenderer renderer = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                File file = new File(Environment.getExternalStorageDirectory(), "/vr_" + currentTimestamp +".pdf");
                ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                renderer = new PdfRenderer(descriptor);
                Log.e(TAG, "printPdf: renderer: " + renderer.getPageCount() );
            }else{
                Toast.makeText(this, "else", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the total number of pages in the PDF document
        int pageCount = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            pageCount = renderer.getPageCount();
        }

        // Create a PrintAttributes object
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
        builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

        // Create a PrintDocumentAdapter instance
        PdfRenderer finalRenderer = renderer;
        int finalPageCount = pageCount;
        PrintDocumentAdapter adapter = new PrintDocumentAdapter() {
            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                // No layout work to do
                callback.onLayoutFinished(new PrintDocumentInfo.Builder("print_output.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build(), false);
            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                // Iterate through all the pages and write them to the destination
                for (int i = 0; i < finalPageCount; i++) {
                    if (PageRange.ALL_PAGES.equals(pages[0])) {
                        renderPageToPdf(destination, finalRenderer, i);
                    } else {
                        if (containsPage(pages, i)) {
                            renderPageToPdf(destination, finalRenderer, i);
                        }
                    }
                }
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            }
        };
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        printManager.print("print_job", adapter, builder.build());


//         Create a PrintJob instance and print the PDF document
//        PrintJob printJob = getPrintManager().print("print_job", adapter, builder.build());


    }


    private boolean containsPage(PageRange[] pages, int page) {
        for (PageRange pageRange : pages) {
            if (page >= pageRange.getStart() && page <= pageRange.getEnd()) {
                return true;
            }
        }
        return false;
    }

    private void renderPageToPdf(ParcelFileDescriptor destination, PdfRenderer renderer, int pageIndex) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageIndex + 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Bitmap bitmap = Bitmap.createBitmap(595, 842, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            renderer.openPage(pageIndex).render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        }
        page.getCanvas().drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        try {
            document.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }

}