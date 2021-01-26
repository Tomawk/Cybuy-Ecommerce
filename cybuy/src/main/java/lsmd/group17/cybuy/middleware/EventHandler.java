package lsmd.group17.cybuy.middleware;

import com.mongodb.ConnectionString;
import lsmd.group17.cybuy.config.ConfigurationParameters;
import lsmd.group17.cybuy.model.*;
import lsmd.group17.cybuy.persistence.MongoDB.MongoDBDriver;
import lsmd.group17.cybuy.persistence.levelDB.LevelDBClient;
import lsmd.group17.cybuy.persistence.levelDB.Message;

import java.util.*;

import static lsmd.group17.cybuy.middleware.Utilities.getSelectedCategory;
import static lsmd.group17.cybuy.middleware.Utilities.getSelectedPlatform;

public class EventHandler {
    private static EventHandler singleInstance = null; // Singleton

    private final MongoDBDriver mongoDBDriver;
    private final LevelDBClient levelDBClient;

    /**
     * Private constructor for the class EventHandler
     */
    private EventHandler() {

        ConfigurationParameters config = ConfigurationParameters.getInstance();
        ConnectionString uri = new ConnectionString(config.getUriMongoDB());

        mongoDBDriver = new MongoDBDriver(uri);
        levelDBClient = new LevelDBClient(config.getLevelDBServers());
    }

    /**
     * If singleInstance has not been instantiated yet, it calls EventHandler constructor;
     * otherwise, it returns singleInstance
     * @return always the same EventHandler instance
     */
    public static EventHandler getInstance() {
        if (singleInstance == null) {
            synchronized (EventHandler.class) {
                if (singleInstance == null) {
                    singleInstance = new EventHandler();
                }
            }
        }
        return singleInstance;
    }


    //
    // ---------- REGISTER INTERFACE ----------
    //

    /**
     * Handles the click on the Sign Up button
     *
     * @param inputStrings Arraylist of strings got from the input fields
     * @param radioButtonSelectedValue Male or Female string
     * @param isASeller Boolean. True if its a seller, false otherwise
     * @return User, used to log in the user automatically after the registration
     * @throws Exception with message:
     *          "1" --> if a textfield is empty         "2"--> if the inserted passwords are different
     *          "3" --> if we have already a user with that username
     */
    public User registrationFormSignUpAction(ArrayList<String> inputStrings,
                                             String radioButtonSelectedValue, Boolean isASeller) throws Exception {
        User result;
        String role;

        for (String input_string : inputStrings) {
            if (input_string.equals("")) {
                throw new Exception("1"); // a textfield is empty, error_code = 1
            }
        }

        // inputStrings(0) -> Name | (1) -> Surname | (2) -> Password | (3) -> Confirm Password | (4) -> Email
        // inputStrings(5) -> Username | (6) -> Country | (7) -> Age

        if (inputStrings.get(2).equals(inputStrings.get(3))) {

            //Check if the username is already present
            User user_found = mongoDBDriver.findUser(inputStrings.get(5), null);
            if (user_found != null) {
                throw new Exception("3"); //we already have an user with that username, error_code = 3
            }

            if (isASeller) role = "Seller";
            else role = "User";

            String salt = PasswordUtils.getSalt(30);
            String mySecurePassword = PasswordUtils.generateSecurePassword(inputStrings.get(2), salt);

            User new_user = User.UserBuilder()
                    .setName(inputStrings.get(0))
                    .setSurname(inputStrings.get(1))
                    .setUsername(inputStrings.get(5))
                    .setEmail(inputStrings.get(4))
                    .setPassword(mySecurePassword)
                    .setSalt(salt)
                    .setGender(radioButtonSelectedValue)
                    .setCountry(inputStrings.get(6))
                    .setAge(Integer.parseInt(inputStrings.get(7)))
                    .setRole(role).build();

            String object_id = mongoDBDriver.insertUser(new_user);
            new_user.setObjectId(object_id);
            result = new_user;
        } else throw new Exception("2"); //the passwords are different, error code = 2
        return result;
    }


    //
    // ---------- LOGIN INTERFACE ----------
    //

    /**
     * Handles the click on the login button
     * @param username_received String got from the username textfield
     * @param password_received String got from the password textfield
     * @return User who has logged
     * @throws Exception with message:
     *      "1" --> if a textfield is empty
     *      "2" --> if username of password are incorrect
     */
    public User loginFormSignInAction(String username_received, String password_received) throws Exception{

        if(username_received.equals("") || password_received.equals("")){
            throw new Exception("1"); // One of the fields is empty, error_code = 1
        } else {
            User user_found = mongoDBDriver.findUser(username_received, password_received);
            if(user_found == null){
                throw new Exception("2"); // incorrect username or password, error_code = 2
            } else {
                return user_found;
            }
        }
    }


    //
    // ---------- WISHLIST INTERFACE ----------
    //

    /**
     * Handles the click on the "REMOVE ALL" button
     * @param user actual logged user
     * @return true if the wishlist was correctly emptied
     */
    public boolean flushWishlist(User user){
        Message message = new Message("flush_wishlist", user.getObjectId(), null);
        Message response = null;
        try {
            response = levelDBClient.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(response == null) return false;
        return response.getType().equals("OK");
    }

    /**
     * Removes the product passed as parameter from the target user's wishlist
     * @param user user whose wishlist we want to modify
     * @param product   product to be removed
     * @return  true if the remove was successful
     */
    public boolean removeProductFromWishlist(User user, Product product){
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        Message message = new Message("remove_wishlist", user.getObjectId(), products);
        Message response = null;
        try {
            response = levelDBClient.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(response == null) return false;
        return response.getType().equals("OK");
    }

    /**
     * Return an arraylist of products present in the wishlist of the user passed as parameter
     * @param user actual logged user
     * @return an arraylist of products
     */
    public ArrayList<Product> getProductsInWishlist(User user){
        Message message = new Message("get_wishlist", user.getObjectId(), null);
        Message response;
        try {
            response = levelDBClient.sendMessage(message);
            return response.getProducts();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    //
    // ---------- PRIMARY INTERFACE ----------
    //

    /**
     * Function that returns a list of Products given the following parameters
     * @param page  number of page we are displaying
     * @param displayedQuantity quantity of products displayed for page
     * @param keyword   keyword to be contained in the description of the product
     * @param category  category to which the product must belong
     * @param platform  platform to which the product must belong
     * @param sellerUsername    username of the seller whose products we want to retrieve
     * @param sort  type of sort to be performed
     *              sort = 0 -> no sort (descending quantity sold)
     *              sort = 1 -> price high to low   sort = 2 -> price low to high
     *              sort = 3 -> stars high to low   sort = 4 -> stars low to high
     * @return  the list of products
     */
    public ArrayList<Product> getProducts(int page, int displayedQuantity, String keyword, String category, String platform, String sellerUsername, int sort) {
        int skip = (page - 1) * 12;
        ArrayList<Product> productArrayList;
        String selected_category = (category != null) ? getSelectedCategory(category) : null;
        String selected_platform = (platform != null) ? getSelectedPlatform(platform) : null;

        productArrayList = mongoDBDriver.getProductsList(keyword, selected_category, selected_platform, skip, displayedQuantity, sort, sellerUsername);

        return productArrayList;
    }

    /**
     * Returns the total number of products given the following parameters
     *
     * @param keyword   keyword to be contained in the description of the products
     * @param category  category to which the products must belong
     * @param platform  platform to which the products must belong
     * @param sellerUsername    seller to which the products must belong
     * @return  the number of products
     */
     public long getProductsQuantity(String keyword, String category, String platform, String sellerUsername) {
        String selected_category = (category != null) ? getSelectedCategory(category) : null;
        String selected_platform = (platform != null) ? getSelectedPlatform(platform) : null;

        return mongoDBDriver.getNumberOfProducts(keyword, selected_category, selected_platform, sellerUsername);
    }


    //
    // ---------- CART INTERFACE ----------
    //

    /**
     * Returns the list of products the user passed as a parameter has in the cart
     *
     * @param user  User whose products we want to retrieve
     * @return  the list of products
     *
     * NOTE: The field quantityAvailable in the Product is used to state the quantity in the cart
     */
    public ArrayList<Product> getProductsInCart(User user) {
        Message message = new Message("get_cart", user.getObjectId(), null);
        Message response;
        try {
            response = levelDBClient.sendMessage(message);
            return response.getProducts();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Remove a product from the cart of the user passed as parameter
     *
     * @param user  User whose cart we want to modify
     * @param product   Product to be removed from the cart
     * @return  true if the deletion was successful
     */
    public boolean removeProductFromCart(User user, Product product) {
        boolean ret = true;
        for(int i = 0; i<product.getQuantityAvailable(); i++) {
            ret = decreaseProductQuantity(user, product);
            if(!ret) break;
        }
        return ret;
    }

    /**
     * Decreases the quantity of a product already in the cart of the user passed as parameter
     *
     * @param user  User whose cart we want to modify
     * @param product   Product whose quantity has to be decreased
     * @return  true if the modification was successful
     */
    public boolean decreaseProductQuantity(User user, Product product) {
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        Message message = new Message("remove_cart", user.getObjectId(), products);
        Message response = null;
        try {
            response = levelDBClient.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(response == null) return false;

        return response.getType().equals("OK");
    }

    /**
     * Deletes of the products in the cart of the user passed as parameter
     *
     * @param user  User whose cart we want to modify
     * @return  true if the deletion was successful
     */
    public boolean flushCart(User user) {
        Message message = new Message("flush_cart", user.getObjectId(), null);
        Message response = null;
        try {
            response = levelDBClient.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(response == null) return false;

        return response.getType().equals("OK");
    }

    /**
     * Places a new order
     * @param user  User who is placing the order
     * @param products  products he wants to order
     * @return  true if the orders were placed correctly
     */
    public boolean placeOrder(User user, ArrayList<Product> products) {
        ArrayList<Order> orders = new ArrayList<>();
        boolean orderFound;

        for (Product product : products) {
            Product orderedProduct = mongoDBDriver.getProductFromId(product.getObjectId());
            orderFound = false;
            for (Order order : orders) {
                if (order.getSeller_username().equals(orderedProduct.getSellerUsername())) {
                    order.insertProduct(product.getQuantityAvailable(), product.getObjectId(), product.getDescription(), product.getImage());
                    double price = order.getPrice() + orderedProduct.getPrice()*product.getQuantityAvailable();
                    order.setPrice(price);
                    orderFound = true;
                    break;
                }
            }
            if (!orderFound) {
                Order newOrder = Order.OrderBuilder()
                        .setUser_username(user.getUsername())
                        .setSeller_username(orderedProduct.getSellerUsername())
                        .setState(OrderState.pending)
                        .setOrderDate(new Date()).build();

                newOrder.insertProduct(product.getQuantityAvailable(), product.getObjectId(), product.getDescription(), product.getImage());
                double price =  orderedProduct.getPrice()*product.getQuantityAvailable();
                newOrder.setPrice(price);
                orders.add(newOrder);
            }
        }

        for (int i = 0; i< orders.size(); i++) {
            if(!mongoDBDriver.insertOrder(orders.get(i))) {
                for(int j = 0; j < i; j++) {
                    mongoDBDriver.deleteOrder(orders.get(j), true);
                }
                return false;
            }
        }

        return true;
    }


    //
    // ---------- PRODUCT INTERFACE ----------
    //

    /**
     * Adds a product to the cart of the user passed as a parameter
     * @param user  User whose cart we want to modify
     * @param product   Product to be added to the cart
     * @return  true if the addition was successful
     */
    public boolean addProductToCart(User user, Product product) {
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        Message message = new Message("add_cart", user.getObjectId(), products);
        Message response = null;
        try {
            response = levelDBClient.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(response == null) return false;
        return response.getType().equals("OK");
    }

    /**
     * Adds a product to the wishlist of the user passed as a parameter
     * @param user User whose wishlist we want to modify
     * @param product   Product to be added to the wishlist
     * @return  true if the addition was successful
     */
    public boolean addProductToWishlist(User user, Product product){
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        Message message = new Message("add_wishlist", user.getObjectId(), products);
        Message response = null;
        try {
            response = levelDBClient.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(response == null) return false;
        return response.getType().equals("OK");
    }

    public void sendReview(User user, Review review){
        mongoDBDriver.insertReview(user, review);
    }

    public boolean modifyProduct(Product product){
        return mongoDBDriver.modifyProduct(product.getObjectId(), product);
    }

    /**
     * Deletes a product from the database
     * @param seller    owner of the product
     * @param p product to be deleted
     * @return true if the deletion was successful
     */
    public boolean removeProduct(User seller, Product p){
        ArrayList<Product> products = new ArrayList<>();
        products.add(p);
        Message message = new Message(
                "remove_product",
                seller.getObjectId(),
                products
        );

        try {
            levelDBClient.sendMessage(message);
        }catch(Exception e){
            e.printStackTrace();
        }

        return mongoDBDriver.deleteProduct(seller, p);
    }


    //
    // ---------- ORDER INTERFACE ----------
    //

    /**
     * Changes the state of an order
     * @param o order whose state has to be modified
     * @param newState  new state
     */
    public void changeOrderState(Order o, OrderState newState){
        mongoDBDriver.changeOrderState(o, newState);
    }

    /**
     * Deletes a order
     * @param order order to be deleted
     * @return  true if the deletion was successful
     */
    public boolean removeOrder(Order order){
        //TODO c'è il controllo sul fatto che l'ordine non sia ancora stato spedito?
        return mongoDBDriver.deleteOrder(order, true);
    }

    /**
     * Returns a Product instance for the given id
     * @param id    id of the product to be retrieved
     * @return  Product instance
     */
    public Product getProductById(String id){
        return mongoDBDriver.getProductFromId(id);
    }


    public ArrayList<Order> getPastOrders(User user){
        return mongoDBDriver.viewPastOrders(user);
    }

    public ArrayList<Order> getActiveOrders(User user){
        return mongoDBDriver.viewActiveOrders(user);
    }

    //
    // ---------- ADD PRODUCT INTERFACE ----------
    //

    /**
     * Adds a product to the database
     * @param product   product to be inserted
     * @return true if the addition was successful
     */
    public boolean addProduct(Product product){
        String s = mongoDBDriver.insertProduct(product);
        return !s.equals("ERROR");
    }


    //
    // ---------- ADMIN PANEL INTERFACE ----------
    //

    /**
     * Returns a list of sellers sorted by their average review
     * @param username  username of the seller
     * @param sort  sort to be performed (-1: descending, 1: ascending)
     * @return  list of sellers
     */
    public ArrayList<User> getSellersRanking(String username, int sort) {
        return mongoDBDriver.getSellerRanking(username, sort);
    }


    //
    // ---------- USER INTERFACE ----------
    //

    /**
     * Deletes a user from the DB
     * @param user  user to be deleted
     * @return  true if the deletion was successful
     */
    public boolean deleteUser(User user){
        if(user.getRole() == null) { //get the user
            user = mongoDBDriver.getUserFromUsername(user.getUsername());
        }
        String role = user.getRole();
        String user_id = user.getObjectId();
        ArrayList<String> seller_products = user.getProductsOnSale();
        boolean user_deleted = mongoDBDriver.deleteUser(user);
        if(!user_deleted) return false;
        if(role.equals("User")) {
            boolean flushed_cart = flushCart(user);
            boolean flushed_wishlist = flushWishlist(user);
            return flushed_cart && flushed_wishlist;
        } else { //admin or seller
            ArrayList<Product> products = new ArrayList<>();
            for(String id : seller_products){
                Product new_product = Product.ProductBuilder().setObjectId(id).build();
                products.add(new_product);
            }

            Message message = new Message(
                    "remove_product",
                    user_id,
                    products
            );

            try {
                levelDBClient.sendMessage(message);
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
            return true;

        }

    }


    //
    // ---------- ANALYTICS INTERFACE ----------
    //

    /**
     * Retrieves analytics for the user passed as parameter
     * @param user  user whose analytics we want to retrieve
     * @return  analytics of the given user
     */
    public Analytics retrieveAnalytics(User user){
        return mongoDBDriver.getSellerAnalytics(user);
    }

    //
    // ---------- GENERAL ----------
    //

    /**
     * Closes the connection with MongoDB
     */
    public void closeMongoDBConnection() {
        mongoDBDriver.closeConnection();
    }
}
