package com.whitedevils.vrtex20;

public class productModel {
    String product,price,qty;

    public productModel(String product, String price, String qty) {
        this.product = product;
        this.price = price;
        this.qty = qty;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
