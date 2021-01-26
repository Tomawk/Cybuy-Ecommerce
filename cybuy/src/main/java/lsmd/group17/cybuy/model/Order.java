package lsmd.group17.cybuy.model;

import java.util.ArrayList;
import java.util.Date;

public class Order {

    public class ProductOrder{
        private int ordered_quantity;
        private String productID;
        private String description;
        private String image;

        public int getOrdered_quantity() {
            return ordered_quantity;
        }

        public String getProductID() {
            return productID;
        }

        public String getDescription() {
            return description;
        }

        public String getImage() {
            return image;
        }

        public ProductOrder(int ordered_quantity, String productID, String description, String image) {
            this.ordered_quantity = ordered_quantity;
            this.productID = productID;
            this.description = description;
            this.image = image;
        }
    }

    private String objectId;
    private String user_username, seller_username;
    private ArrayList<ProductOrder> productOrders = new ArrayList<>();
    private OrderState state;
    private Date deliveryDate, orderDate;
    private double price;

    public static Order OrderBuilder(){
        return new Order();
    }


    public Order build(){
        Order response = new Order();

        response.objectId             = this.objectId;
        response.user_username        = this.user_username;
        response.seller_username      = this.seller_username;
        response.deliveryDate         = this.deliveryDate;
        response.orderDate            = this.orderDate;
        response.state                = this.state;
        response.productOrders        = this.productOrders;
        response.price                = this.price;

        return response;
    }

    private Order() {
    }

    public void insertProduct(int quantity, String productId, String description, String image){
        ProductOrder product_inserted = new ProductOrder(quantity, productId, description, image);
        productOrders.add(product_inserted);
    }

    public String getObjectId() {
        return objectId;
    }

    public Order setObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }

    public String getUser_username() { //#//
        return user_username;
    }

    public Order setUser_username(String user_username) { //#//
        this.user_username = user_username;
        return this;
    }

    public String getSeller_username() {
        return seller_username;
    } //#//

    public Order setSeller_username(String seller_username) {
        this.seller_username = seller_username; //#//
        return this;
    }

    public ArrayList<ProductOrder> getProductOrders() {
        return productOrders;
    }

    public Order setProductOrders(ArrayList<ProductOrder> productOrders) {
        this.productOrders = productOrders;
        return this;
    }

    public OrderState getState() {
        return state;
    }

    public Order setState(OrderState state) {
        this.state = state;
        return this;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public Order setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Order setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Order setPrice(double price) {
        this.price = price;
        return this;
    }
}
