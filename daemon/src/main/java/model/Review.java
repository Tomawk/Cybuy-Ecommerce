package model;

public class Review {
    private String product;
    private int stars;

    public Review(String product, int stars) {
        this.product = product;
        this.stars = stars;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
