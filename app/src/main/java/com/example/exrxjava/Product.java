package com.example.exrxjava;

public class Product {
    int id ;
    String nameProduct ;

    public Product() {
    }

    public Product(int id, String nameProduct) {
        this.id = id;
        this.nameProduct = nameProduct;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", nameProduct='" + nameProduct + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }
}
