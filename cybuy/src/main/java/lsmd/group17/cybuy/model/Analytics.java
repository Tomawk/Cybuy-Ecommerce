package lsmd.group17.cybuy.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Analytics {
    private String objectId;
    private String sellerUsername;
    private int totalReviews;
    private double avgReviews;
    private int[] starsDistribution;
    private ArrayList<DailyAnalytics> salesByDay;
    private int avgDelivery;
    private double totalEarnings;
    private int totalSales;
    private Product bestSellingProduct;
    private int month;

    public static Analytics AnalyticsBuilder() { return new Analytics();}

    public Analytics build(){

        Analytics response = new Analytics();

        response.objectId = this.objectId;
        response.sellerUsername = this.sellerUsername;
        response.totalReviews = this.totalReviews;
        response.avgReviews = this.avgReviews;
        response.starsDistribution = this.starsDistribution;
        response.salesByDay = this.salesByDay;
        response.avgDelivery = this.avgDelivery;
        response.totalEarnings = this.totalEarnings;
        response.totalSales = this.totalSales;
        response.bestSellingProduct = this.bestSellingProduct;
        response.month = this.month;

        return response;
    }

    private Analytics(){

    }

    @Override
    public String toString() {
        return "Analytics{" +
                "objectId='" + objectId + '\'' +
                ", seller_username='" + sellerUsername + '\'' +
                ", total_reviews=" + totalReviews +
                ", avg_reviews=" + avgReviews +
                ", stars_distribution=" + Arrays.toString(starsDistribution) +
                ", avg_delivery=" + avgDelivery +
                ", total_earnings=" + totalEarnings +
                ", total_sales=" + totalSales +
                ", best_selling_product=" + bestSellingProduct.getObjectId() + " " + bestSellingProduct.getDescription() +
                ", date=" + month +
                '}';
    }

    public String getObjectId(){
        return objectId;
    }

    public Analytics setObjectId(String objectId){
        this.objectId = objectId;
        return this;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public Analytics setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
        return this;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public Analytics setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
        return this;
    }

    public double getAvgReviews() {
        return avgReviews;
    }

    public Analytics setAvgReviews(double avgReviews) {
        this.avgReviews = avgReviews;
        return this;
    }

    public int[] getStarsDistribution() {
        return starsDistribution;
    }

    public Analytics setStarsDistribution(int[] starsDistribution) {
        this.starsDistribution = starsDistribution;
        return this;
    }

    public int getAvgDelivery() {
        return avgDelivery;
    }

    public Analytics setAvgDelivery(int avgDelivery) {
        this.avgDelivery = avgDelivery;
        return this;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public Analytics setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
        return this;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public Analytics setTotalSales(int totalSales) {
        this.totalSales = totalSales;
        return this;
    }

    public Product getBestSellingProduct() {
        return bestSellingProduct;
    }

    public Analytics setBestSellingProduct(Product bestSellingProduct) {
        this.bestSellingProduct = bestSellingProduct;
        return this;
    }

    public int getMonth() {
        return month;
    }

    public Analytics setMonth(int month) {
        this.month =  month;
        return this;
    }

    public ArrayList<DailyAnalytics> getSalesByDay() {
        return salesByDay;
    }

    public Analytics setSalesByDay(ArrayList<DailyAnalytics> salesByDay) {
        this.salesByDay = salesByDay;
        return this;
    }
}
