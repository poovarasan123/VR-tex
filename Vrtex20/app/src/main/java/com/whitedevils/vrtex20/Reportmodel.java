package com.whitedevils.vrtex20;

import java.util.ArrayList;

public class Reportmodel {

    String date,invoceNo,gtotal;



    // constructor for activity
    public Reportmodel(String date, String invoceNo, String gtotal) {
        this.date = date;
        this.invoceNo = invoceNo;
        this.gtotal = gtotal;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvoceNo() {
        return invoceNo;
    }

    public void setInvoceNo(String invoceNo) {
        this.invoceNo = invoceNo;
    }

    public String getGtotal() {
        return gtotal;
    }

    public void setGtotal(String gtotal) {
        this.gtotal = gtotal;
    }
}
