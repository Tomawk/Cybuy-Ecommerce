package model;

import java.io.Serializable;
import java.util.HashMap;

    /* Document example
    { "_id" : { "$oid" : "5fdb8ed70e189329746b49d9" },
      "description" : "*MISSING STAND/SCREWS* VIZIO 24\" Class HD LED Smart TV D-Series D24h-G9",
      "url" : "https://www.ebay.com/itm/MISSING-STAND-SCREWS-VIZIO-24-Class-HD-LED-Smart-TV-D-Series-D24h-G9/254787497460?hash=item3b5284adf4:g:pnYAAOSw0axfvAvv",
      "product_type" : "television",
      "image" : "https://i.ebayimg.com/thumbs/images/g/pnYAAOSw0axfvAvv/s-l225.jpg",
      "price" : "$71.45",
      "details" : { "Brand" :"VIZIO"
                    },
      "seller" : "5fdf8787a8335e0908c1e143",
      "total_reviews" : 0,
      "average_review": 0,
      "quantity_available" : 40,
      "quantity_sold" : 0
      }
     */

public class Product implements Serializable {
    private String objectId;
    private String description;
    private String productType;
    private String productPlatform;
    private String image;
    private double price;
    private HashMap<String, String> details = new HashMap<>();
    private String sellerUsername;
    private int totalReviews = 0;
    private double averageReview = 0;
    private int quantityAvailable;
    private int quantitySold = 0;


    public static Product ProductBuilder(){
        return new Product();
    }

    public Product build(){
        Product response = new Product();

        response.objectId          = this.objectId;
        response.description       = this.description;
        response.productType       = this.productType;
        response.productPlatform   = this.productPlatform;
        response.image             = this.image;
        response.price             = this.price;
        response.details           = this.details;
        response.sellerUsername = this.sellerUsername;
        response.totalReviews      = this.totalReviews;
        response.averageReview     = this.averageReview;
        response.quantityAvailable = this.quantityAvailable;
        response.quantitySold      = this.quantitySold;

        return response;
    }

    private Product(){

    }

    public Product insertDetail(String key, String value){
        this.details.put(key, value);
        return this;
    }

    public String getObjectId() {
        return objectId;
    }

    public Product setObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getProductType() {
        return productType;
    }

    public Product setProductType(String productType) {
        this.productType = productType;
        return this;
    }

    public String getProductPlatform() {
        return productPlatform;
    }

    public Product setProductPlatform(String productPlatform) {
        this.productPlatform = productPlatform;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Product setImage(String image) {
        this.image = image;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }

    public HashMap<String, String> getDetails() {
        return details;
    }

    public Product setDetails(HashMap<String, String> details) {
        this.details = details;
        return this;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public Product setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
        return this;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public Product setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
        return this;
    }

    public double getAverageReview() {
        return averageReview;
    }

    public Product setAverageReview(double averageReview) {
        this.averageReview = averageReview;
        return this;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public Product setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
        return this;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public Product setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
        return this;
    }
}
