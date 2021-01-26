package model;

import java.util.ArrayList;

/*
{ "_id" : { "$oid" : "5fdf8787a8335e0908c1e143"},
  "role" : "Admin",
  "products_on_sale" : ["5fe9a420845df004b4acf204","5feb41a784937b2318c5590f","5feb41a784937b2318c55d0b"],
  "name" : "Emma",
  "surname" : "Hargrave",
  "email" : "patry_pax@xxxx.xxx",
  "password" : "picocwi3",
  "username" : "Lee4an",
  "gender" : "Female",
  "country" : "France",
  "age" : 42
  }
 */
public class User {
    private String objectId;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private String gender;
    private String country;
    private int age;
    private String role;
    private ArrayList<String> productsOnSale = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    private ArrayList<Review> reviews = new ArrayList<>();
    private int totalReviews = 0;
    private double averageReview = 0;

    public static User UserBuilder(){
        return new User();
    }

    public User build(){
        User response = new User();

        response.objectId       = this.objectId;
        response.name           = this.name;
        response.surname        = this.surname;
        response.username       = this.username;
        response.email          = this.email;
        response.password       = this.password;
        response.gender         = this.gender;
        response.country        = this.country;
        response.age            = this.age;
        response.role           = this.role;
        response.productsOnSale = this.productsOnSale;
        response.orders         = this.orders;
        response.reviews        = this.reviews;
        response.totalReviews   = this.totalReviews;
        response.averageReview  = this.averageReview;

        return  response;
    }

    private User(){

    }

    @Override
    public String toString(){
        String response = "{";
        response += "\n\tname: " + this.name;
        response += "\n\tsurname: " + this.surname;
        response += "\n\tusername: " + this.username;
        response += "\n\trole: " + this.role;
        response += "\n}";
        return response;
    }

    public String getObjectId() {
        return objectId;
    }

    public User setObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public User setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public User setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public User setCountry(String country) {
        this.country = country;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public String getRole() {
        return role;
    }

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    public ArrayList<String> getProductsOnSale() {
        return productsOnSale;
    }

    public User setProductsOnSale(ArrayList<String> productsOnSale) {
        this.productsOnSale = productsOnSale;
        return this;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public User setOrders(ArrayList<Order> orders) {
        this.orders = orders;
        return this;
    }

    public ArrayList<Review> getReviews() { return reviews;}

    public User setReviews(ArrayList<Review> reviews){
        this.reviews = reviews;
        return this;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public User setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
        return this;
    }

    public double getAverageReview() {
        return averageReview;
    }

    public User setAverageReview(double averageReview) {
        this.averageReview = averageReview;
        return this;
    }
}
