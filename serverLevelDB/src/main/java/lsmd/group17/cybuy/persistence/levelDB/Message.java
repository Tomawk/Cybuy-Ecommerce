package lsmd.group17.cybuy.persistence.levelDB;

import lsmd.group17.cybuy.model.Product;

import java.io.Serializable;
import java.util.ArrayList;
public class Message implements Serializable {
    private String type;
    private String userId;
    private ArrayList<Product> products;

    public Message(String type, String userId, ArrayList<Product> products) {
        this.type = type;
        this.userId = userId;
        this.products = products;
    }

    // Getter methods
    public String getType() { return type; }
    public String getUserId() { return userId; }
    public ArrayList<Product> getProducts() { return products; }

    // Setter methods
    public void setType(String type) { this.type = type; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setProducts(ArrayList<Product> products) { this.products = products; }
}