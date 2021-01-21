package lsmd.group17.cybuy.persistence.levelDB;

import lsmd.group17.cybuy.model.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {
    private String type;
    private String userId;
    private ArrayList<Product> products;

    public Message(String type, String userId, ArrayList<Product> products) {
        this.type = type;
        this.userId = userId;
        this.products = products;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
