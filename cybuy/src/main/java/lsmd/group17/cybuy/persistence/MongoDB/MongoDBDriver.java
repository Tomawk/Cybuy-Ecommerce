package lsmd.group17.cybuy.persistence.MongoDB;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import javafx.util.Pair;
import lsmd.group17.cybuy.middleware.PasswordUtils;
import lsmd.group17.cybuy.model.*;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.*;


public class MongoDBDriver {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> productsCollection;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> ordersCollection;
    private final MongoCollection<Document> analyticsCollection;

    /**
     * Default constructor
     *
     * @param uri ConnectionString
     */
    public MongoDBDriver(ConnectionString uri) {
        mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("cybuy")
                .withReadPreference(ReadPreference.primaryPreferred());

        usersCollection = database.getCollection("users")
                .withWriteConcern(WriteConcern.W3).withReadConcern(ReadConcern.LOCAL);

        productsCollection = database.getCollection("products")
                .withWriteConcern(WriteConcern.W2).withReadConcern(ReadConcern.MAJORITY);

        ordersCollection = database.getCollection("orders")
                .withWriteConcern(WriteConcern.W2).withReadConcern(ReadConcern.MAJORITY);

        analyticsCollection = database.getCollection("analytics")
                .withWriteConcern(WriteConcern.W3).withReadConcern(ReadConcern.LOCAL);
    }


    //
    // UTILITIES
    //
    public void closeConnection() {
        mongoClient.close();
    }

    private boolean executeTransaction(ClientSession clientSession, TransactionBody<String> txnFunc) {
        String message = "";
        try (clientSession) {
            message = clientSession.withTransaction(txnFunc);
        } catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }

        System.out.println(message);

        return message.equals("OK");
    }

    //
    // ------------------------------ PRODUCTS ------------------------------
    //

    // ## CONVERSIONS ##

    /**
     * Converts a product Bson Document into a Product instance
     *
     * @param productDocument Bson Document describing a product
     * @return an instance of the Product Java Class
     */
    private Product documentToProduct(Document productDocument) {
        if (productDocument == null)
            return null;

        String objectId = productDocument.get("_id").toString();
        String description = productDocument.getString("description");
        Double price = productDocument.getDouble("price");
        String image = productDocument.getString("image");
        String productType = productDocument.getString("product_type");
        String productPlatform = productDocument.getString("platform");
        String seller_username = productDocument.getString("seller_username");
        Integer totalReviews = productDocument.getInteger("total_reviews");
        double averageReview = productDocument.getDouble("average_review");
        Integer quantityAvailable = productDocument.getInteger("quantity_available");
        Integer quantitySold = productDocument.getInteger("quantity_sold");

        HashMap<String, String> details = new HashMap<>();
        Document documentDetails = (Document) productDocument.get("details");
        if (documentDetails != null) {
            for (String key : documentDetails.keySet()) {
                details.put(key, documentDetails.get(key).toString());
            }
        }

        return Product.ProductBuilder()
                .setObjectId(objectId)
                .setDescription(description)
                .setProductType(productType)
                .setProductPlatform(productPlatform)
                .setImage(image)
                .setPrice(price)
                .setDetails(details)
                .setSellerUsername(seller_username)
                .setTotalReviews(totalReviews)
                .setAverageReview(averageReview)
                .setQuantityAvailable(quantityAvailable)
                .setQuantitySold(quantitySold).build();
    }

    /**
     * Converts a Product instance into a BSON document
     *
     * @param product Product to be converted
     * @return relative Document
     */
    private Document ProductToDocument(Product product) {
        Document productDocument = new Document("description", product.getDescription())
                .append("price", product.getPrice())
                .append("image", product.getImage())
                .append("product_type", product.getProductType())
                .append("quantity_available", product.getQuantityAvailable())
                .append("seller_username", product.getSellerUsername())
                .append("total_reviews", product.getTotalReviews())
                .append("average_review", product.getAverageReview())
                .append("quantity_sold", product.getQuantitySold());
        if (product.getProductPlatform() != null) {
            productDocument.append("platform", product.getProductPlatform());
        }

        Document details = new Document();
        HashMap<String, String> detailsHM = product.getDetails();
        if (detailsHM != null) {
            for (String key : detailsHM.keySet()) {
                details.append(key, detailsHM.get(key));
            }
        }
        productDocument.append("details", details);

        return productDocument;
    }

    // ## CRUD OPERATIONS ##

    /**
     * Inserts a product in the products collection and its id in the products_on_sale array of the user
     *
     * @param newProduct product to be inserted
     * @return the ObjectId of the inserted product
     */
    public String insertProduct(Product newProduct) {
        Document productDocument = ProductToDocument(newProduct);

        ClientSession clientSession = mongoClient.startSession();

        TransactionBody<String> txnFunc = () -> {
            BsonValue id = productsCollection.insertOne(clientSession, productDocument).getInsertedId();
            if (id == null) {
                return "ERROR inserting the product";
            }

            String productId = id.asObjectId().getValue().toHexString();
            newProduct.setObjectId(productId);

            long modifiedCount = usersCollection.updateOne(clientSession, eq("username", newProduct.getSellerUsername()),
                    addToSet("products_on_sale", newProduct.getObjectId())).getModifiedCount();
            if (modifiedCount == 0) {
                return "ERROR inserting the product in the seller's document";
            }

            return "OK";
        };

        if (executeTransaction(clientSession, txnFunc)) {
            return newProduct.getObjectId();
        }

        return "ERROR";
    }

    /**
     * function that modify a certain product
     * P.S. possible things to be edit: image, description, details, quantity_available, price
     * @param ProductId the id of the product that you need to modify
     * @param newProduct a product class that contains the new attributes
     * @return true if the product was correctly modified
     */
    public boolean modifyProduct(String ProductId, Product newProduct){

        Bson query = eq("_id", new ObjectId(ProductId));
        Bson update = Updates.combine(
                Updates.set("image", newProduct.getImage()),
                Updates.set("description", newProduct.getDescription()),
                Updates.set("quantity_available", newProduct.getQuantityAvailable()),
                Updates.set("price", newProduct.getPrice()),
                Updates.set("details", newProduct.getDetails())
        );

        UpdateResult response = productsCollection.updateOne(query, update);

        return response.getModifiedCount() == 1;
    }

    /**
     * Function that deletes a product
     *
     * @param product Product to be deleted
     * @return true if the deletion was successful
     */
    public boolean deleteProduct(User user, Product product) {
        if (product.getObjectId() == null ||
                !user.getUsername().equals(product.getSellerUsername())) return false;

        ClientSession clientSession = mongoClient.startSession();

        TransactionBody<String> txnFunc = () -> {
            long deletedCount = productsCollection.deleteOne(clientSession, eq("_id", new ObjectId(product.getObjectId()))).getDeletedCount();
            if (deletedCount == 0) {
                return "ERROR: There is no such product";
            }

            long modifiedCount = usersCollection.updateOne(clientSession, eq("username", product.getSellerUsername()), pull("products_on_sale", product.getObjectId())).getModifiedCount();
            if (modifiedCount == 0) {
                return "ERROR deleting the product from seller's document";
            }

            return "OK";
        };

        return executeTransaction(clientSession, txnFunc);
    }

    // ## QUERIES ##

    /**
     * Function that returns a list of products, given some search parameters
     *
     * @param keyword     string to be contained in the description of products to be returned
     * @param productType type of the products to be returned
     * @param platform    platform of the products to be returned
     * @param skip        number of products to be skipped before returning
     * @param limit       number of products to be returned
     * @param sort        indication of the sort to be performed
     *                    sort = 0 -> no sort (sort descending by quantity_sold)
     *                    sort = 1 -> price high to low   sort = 2 -> price low to high
     *                    sort = 3 -> stars high to low   sort = 4 -> stars low to high
     * @return a list of products, given the previous parameters
     */
    public ArrayList<Product> getProductsList(String keyword, String productType, String platform, int skip, int limit, int sort, String seller_username) {
        final ArrayList<Product> productsList = new ArrayList<>();

        FindIterable<Document> productsDocuments;

        Bson findByDescription = text("\""+keyword+"\"");

        Bson query;
        if (keyword != null && !keyword.equals("")) {
            if (productType != null) {
                if (platform != null) {
                    if (seller_username != null) {
                        query = and(eq("product_type", productType), eq("seller_username", seller_username), eq("platform", platform), findByDescription);
                    } else {
                        query = (and(ne("quantity_available", 0),eq("product_type", productType), eq("platform", platform), findByDescription));
                    }
                } else {
                    if (seller_username != null) {
                        query = (and(eq("product_type", productType), eq("seller_username", seller_username), findByDescription));
                    } else {
                        query = (and(ne("quantity_available", 0), eq("product_type", productType), findByDescription));
                    }
                }
            } else {
                if (platform != null) {
                    if (seller_username != null) {
                        query = (and(eq("platform", platform), eq("seller_username", seller_username), findByDescription));
                    } else {
                        query = (and(ne("quantity_available", 0), eq("platform", platform), findByDescription));
                    }
                } else {
                    if (seller_username != null) {
                        query = (and(eq("seller_username", seller_username), findByDescription));
                    } else {
                        query = (and(ne("quantity_available", 0), findByDescription));
                    }
                }
            }
        } else {
            if (productType != null) {
                if (platform != null) {
                    if (seller_username != null) {
                        query = (and(eq("seller_username", seller_username), eq("product_type", productType), eq("platform", platform)));
                    } else {
                        query = (and(ne("quantity_available", 0),eq("product_type", productType), eq("platform", platform)));
                    }
                } else {
                    if (seller_username != null) {
                        query = (and(eq("seller_username", seller_username), eq("product_type", productType)));
                    } else {
                        query = (and(ne("quantity_available", 0),eq("product_type", productType)));
                    }
                }
            } else {
                if (platform != null) {
                    if (seller_username != null) {
                        query = (and(eq("seller_username", seller_username), eq("platform", platform)));
                    } else {
                        query = (and(ne("quantity_available", 0),eq("platform", platform)));
                    }
                } else {
                    if (seller_username != null) {
                        query = (eq("seller_username", seller_username));
                    } else {
                        query = (ne("quantity_available", 0));
                    }
                }
            }
        }

        productsDocuments = productsCollection.find(query);

        switch (sort) {
            case 0: // Descending quantity sold
                productsDocuments.sort(new BasicDBObject("quantity_sold", -1));
                break;
            case 1: // Price high to low
                productsDocuments.sort(new BasicDBObject("price", -1));
                break;
            case 2: // Price low to high
                productsDocuments.sort(new BasicDBObject("price", 1));
                break;
            case 3: // Stars high to low
                productsDocuments.sort(new BasicDBObject("average_review", -1));
                break;
            case 4: //Stars low to high
                productsDocuments.sort(new BasicDBObject("average_review", 1));
                break;
            default:
                break;
        }
        productsDocuments.skip(skip).limit(limit);

        for (Document product : productsDocuments) {
            final Product newProduct = documentToProduct(product);
            productsList.add(newProduct);
        }

        return productsList;
    }

    /**
     * Function that returns the number of products that respect the criteria
     *
     * @param keyword         the string to be present in the 'description' field of products to be counted
     * @param productType     the type of products to be counted
     * @param platform        the platform of products to be counted
     * @param seller_username the seller of products to be counted
     * @return the number of products in the database, given the previous parameters
     */
    public long getNumberOfProducts(String keyword, String productType, String platform, String seller_username) {
        long numberOfProducts;

        Bson findByDescription = text("\"" + keyword + "\"");
        if(keyword != null)
            findByDescription = regex("description", keyword, "i");

        if (keyword != null && !keyword.equals("")) {
            if (productType != null) {
                if (platform != null) {
                    if (seller_username != null) {
                        numberOfProducts = productsCollection.countDocuments(and(eq("product_type", productType), eq("seller_username", seller_username), findByDescription, eq("platform", platform)));
                    } else {
                        numberOfProducts = productsCollection.countDocuments(and(eq("product_type", productType), findByDescription, eq("platform", platform)));
                    }
                } else {
                    if (seller_username != null) {
                        numberOfProducts = productsCollection.countDocuments(and(eq("product_type", productType), eq("seller_username", seller_username), findByDescription));
                    } else {
                        numberOfProducts = productsCollection.countDocuments(and(eq("product_type", productType), findByDescription));
                    }
                }
            } else {
                if (platform != null) {
                    if (seller_username != null) {
                        numberOfProducts = productsCollection.countDocuments(and(findByDescription, eq("seller_username", seller_username), eq("platform", platform)));
                    } else {
                        numberOfProducts = productsCollection.countDocuments(and(findByDescription, eq("platform", platform)));
                    }
                } else {
                    if (seller_username != null) {
                        numberOfProducts = productsCollection.countDocuments(and(eq("seller_username", seller_username), findByDescription));
                    } else {
                        numberOfProducts = productsCollection.countDocuments(findByDescription);
                    }
                }
            }
        } else {
            if (productType != null) {
                if (platform != null) {
                    if (seller_username != null) {
                        numberOfProducts = productsCollection.countDocuments(and(eq("product_type", productType), eq("seller_username", seller_username), eq("platform", platform)));
                    } else {
                        numberOfProducts = productsCollection.countDocuments(and(eq("product_type", productType), eq("platform", platform)));
                    }
                } else {
                    if (seller_username != null) {
                        numberOfProducts = productsCollection.countDocuments(and(eq("seller_username", seller_username), eq("product_type", productType)));
                    } else {
                        numberOfProducts = productsCollection.countDocuments(eq("product_type", productType));
                    }
                }
            } else {
                if (platform != null) {
                    if (seller_username != null) {
                        numberOfProducts = productsCollection.countDocuments(and(eq("seller_username", seller_username), eq("platform", platform)));
                    } else {
                        numberOfProducts = productsCollection.countDocuments(eq("platform", platform));
                    }
                } else {
                    if (seller_username != null) {
                        numberOfProducts = productsCollection.countDocuments(eq("seller_username", seller_username));
                    } else {
                        numberOfProducts = productsCollection.countDocuments();
                    }
                }

            }
        }


        return numberOfProducts;
    }

    /**
     * Function that returns an instance of the Product Java Class with the ObjectId passed as parameter
     *
     * @param id ObjectId of the Product to be returned
     * @return the Product
     */
    public Product getProductFromId(String id) {

        FindIterable<Document> productsDocuments = productsCollection.find(eq("_id", new ObjectId(id)));

        if (productsDocuments.first() == null) return null;
        return documentToProduct(productsDocuments.first());
    }


    //
    // ------------------------------ USERS ------------------------------
    //

    // ## CONVERTIONS ##

    /**
     * Converts a product Bson Document into a User instance
     *
     * @param userDocument Bson Document describing a user
     * @return an instance of the User Java Class
     */
    private User documentToUser(Document userDocument) {
        String objectId = userDocument.get("_id").toString();
        String name = userDocument.getString("name");
        String surname = userDocument.getString("surname");
        String username = userDocument.getString("username");
        String email = userDocument.getString("email");
        String gender = userDocument.getString("gender");
        String country = userDocument.getString("country");
        int age = userDocument.getInteger("age");
        String role = userDocument.getString("role");

        Document password_info = (Document) userDocument.get("password_info");
        String password = password_info.getString("password");
        String salt = password_info.getString("salt");

        ArrayList<Order> orders = documentsToOrders(username, (ArrayList<Document>) userDocument.get("orders"));

        ArrayList<String> productsOnSale = (ArrayList<String>) userDocument.get("products_on_sale");

        ArrayList<Document> reviews = (ArrayList<Document>) userDocument.get("reviews");
        ArrayList<Review> reviewArrayList = null;
        if (reviews != null) {
            reviewArrayList = new ArrayList<>();
            for (Document review : reviews) {
                reviewArrayList.add(documentToReview(review));
            }
        }

        return User.UserBuilder()
                .setObjectId(objectId)
                .setName(name)
                .setSurname(surname)
                .setUsername(username)
                .setEmail(email)
                .setAge(age)
                .setPassword(password)
                .setSalt(salt)
                .setCountry(country)
                .setGender(gender)
                .setRole(role)
                .setProductsOnSale(productsOnSale)
                .setOrders(orders)
                .setReviews(reviewArrayList)
                .build();
    }

    // ## CRUD OPERATIONS ##

    /**
     * Adds a new user document
     *
     * @param newUser user to be added
     */
    public String insertUser(User newUser) {
        Document user = new Document("name", newUser.getName())
                .append("surname", newUser.getSurname())
                .append("username", newUser.getUsername())
                .append("email", newUser.getEmail())
                .append("gender", newUser.getGender())
                .append("country", newUser.getCountry())
                .append("age", newUser.getAge())
                .append("role", newUser.getRole());

        Document password_doc = new Document();
        password_doc.append("password", newUser.getPassword());
        password_doc.append("salt", newUser.getSalt());

        user.append("password_info", password_doc);

        BsonValue id = usersCollection.insertOne(user).getInsertedId();


        if (id == null) return null;
        return id.asObjectId().getValue().toHexString();
    }

    /**
     * Deletes the user passed as a parameter
     *
     * @param user User to be deleted
     * @return true if the deletion was successful
     */
    public boolean deleteUser(User user) {
        boolean success;

        ClientSession clientSession = mongoClient.startSession();

        if(user.getRole().equals("User")){
            success = usersCollection.deleteOne(eq("_id", new ObjectId(user.getObjectId()))).wasAcknowledged();
        } else { //is a Seller or an Admin
            ArrayList<String> seller_products = user.getProductsOnSale();
            ArrayList<Order> seller_orders = user.getOrders();
            TransactionBody<String> txnFunc = () -> {
                for (String seller_product : seller_products) { //delete all the products of a seller
                    Product product = Product.ProductBuilder().setObjectId(seller_product)
                                                              .setSellerUsername(user.getUsername()).build();
                    boolean deleted_product = deleteProduct(user, product);
                    if(!deleted_product) {
                        return "Error deleting the product";
                    }
                }
                for (Order seller_order : seller_orders) { //delete all active orders
                    boolean deleted_order = deleteOrder(seller_order, true);
                    if(!deleted_order){
                        return "Error deleting the order";
                    }
                }
                //Delete analytics document
                boolean deleted_analytics = analyticsCollection.deleteMany(eq("seller_username", user.getUsername())).wasAcknowledged();
                if(!deleted_analytics){
                    return "Error deleting the analytics of the user";
                }
                boolean deleted_user = usersCollection.deleteOne(eq("_id", new ObjectId(user.getObjectId()))).wasAcknowledged();
                if(!deleted_user){
                    return "Error deleting the user";
                }
                return "OK";
            };
            success = executeTransaction(clientSession, txnFunc);
        }
        return success;
    }

    // ## QUERIES ##

    /**
     * Find a user with a given username and password
     *
     * @return null if the user is not found or username not specified, an user instance otherwise
     */
    public User findUser(String username, String password) {
        Document userDocument;

        if (username == null) { //we are not interested in finding all user with a specific password
            return null;
        } else { //Username not null
            if (password == null) { //we are interested in finding an user with this username
                userDocument = usersCollection.find(eq("username", username)).first();
                if(userDocument == null) return null;
                    else return documentToUser(userDocument);
            } else { //login (i want to find exactly the document with the user and the password specified in the fields
                userDocument = usersCollection.find(eq("username", username)).first();
                if(userDocument == null) return null;
                    else{
                    Document password_doc = (Document) userDocument.get("password_info");
                    String salt = password_doc.getString("salt");
                    String encoded_password = password_doc.getString("password");
                    boolean passwordMatch = PasswordUtils.verifyUserPassword(password, encoded_password, salt);
                    if(!passwordMatch) return null;
                        else return documentToUser(userDocument);
                }
            }
        }
    }

    /**
     * Gets a list of Sellers that match the username passed as parameter
     *
     * @param username username to be matched
     * @return list of Users (sellers)
     */
    public ArrayList<User> getSellersFromUsername(String username) {
        ArrayList<User> sellers = new ArrayList<>();

        FindIterable<Document> usersDocument = usersCollection.find(and(eq("role", "Seller"), regex("username", username, "i")));

        for (Document user : usersDocument) {
            sellers.add(documentToUser(user));
        }


        return sellers;
    }

    public User getUserFromUsername(String username) {
        User user = null;

        Document userDoc = usersCollection.find(eq("username", username)).first();

        if (userDoc != null)
            user = documentToUser(userDoc);


        return user;
    }

    //
    // ------------------------------ REVIEWS ------------------------------
    //

    // ## CONVERTIONS ##
    private Review documentToReview(Document review) {
        String product = review.getString("productId");
        int stars = review.getInteger("stars");

        return new Review(product, stars);
    }

    // ## CRUD OPERATIONS ##

    /**
     * Inserts a review for a Product
     *
     * @param user   User who is leaving the review
     * @param review Rating
     * @return true if the insert was successful
     */
    public boolean insertReview(User user, Review review) {
        if (review.getProduct() == null || user == null ||
                !user.getRole().equals("User")) return false;

        String productId = review.getProduct();
        ObjectId userId = new ObjectId(user.getObjectId());

        ClientSession clientSession = mongoClient.startSession();

        TransactionBody<String> txnFunc = () -> {
            Document userDoc = usersCollection.find(clientSession, and(eq("_id", userId), eq("reviews.productId", productId))).projection(include("reviews.$")).first();

            if (userDoc == null) {
                return "ERROR: User cannot place the review, the product was not previously ordered";
            }

            ArrayList<Document> reviews = (ArrayList<Document>) userDoc.get("reviews");
            int previousStars = reviews.get(0).getInteger("stars");

            if (previousStars != -1) {
                return "ERROR: User cannot place the review, the product has already been reviewed";
            }

            long updateCount = usersCollection.updateOne(clientSession, and(eq("_id", userId), eq("reviews.productId", productId)), set("reviews.$.stars", review.getStars())).getModifiedCount();

            if (updateCount == 0) {
                return "ERROR: no modification";
            }

            Bson query = eq("_id", new ObjectId(productId));

            FindIterable<Document> productsDocuments = productsCollection.find(clientSession, query);
            Document productDoc = productsDocuments.first();

            if (productDoc == null)
                return "ERROR: Non-existing product";

            int totalReviews = productDoc.getInteger("total_reviews");
            double averageReview = productDoc.getDouble("average_review");

            int newTotal = totalReviews + 1;
            double newAverage = ((averageReview * totalReviews) + review.getStars()) / newTotal;

            long modifiedCount = productsCollection.updateOne(clientSession, query, set("total_reviews", newTotal)).getModifiedCount();
            if (modifiedCount == 0)
                return "ERROR updating total reviews";

            boolean modified = productsCollection.updateOne(clientSession, query, set("average_review", newAverage)).wasAcknowledged();
            if (!modified)
                return "ERROR updating average review";

            return "OK";
        };

        return executeTransaction(clientSession, txnFunc);
    }


    //
    // ------------------------------ ORDERS ------------------------------
    //

    private enum orderType {user, seller, collection}

    // ## CONVERTIONS ##

    /**
     * Utility method to convert a document into an Order class
     *
     * @param orderDocument the document that you want to convert
     * @return the Order class
     */
    private Order documentToOrder(String username_usr, Document orderDocument, boolean pastOrder) {
        String client_username, seller_username, objectId;
        OrderState state;
        double price;
        Date deliveryDate, orderDate;

        if (pastOrder) {
            client_username = ((Document) orderDocument.get("user")).getString("username");
            state = OrderState.arrived;
        } else {
            client_username = orderDocument.getString("user_username");
            state = OrderState.valueOf(orderDocument.getString("state"));
        }

        seller_username = orderDocument.getString("seller_username");
        objectId = orderDocument.getObjectId("_id").toString();

        deliveryDate = orderDocument.getDate("delivery_date");
        orderDate = orderDocument.getDate("order_date");

        price = orderDocument.getDouble("price");

        Order response = Order.OrderBuilder()
                .setUser_username(client_username)
                .setSeller_username(seller_username)
                .setState(state)
                .setOrderDate(orderDate)
                .setDeliveryDate(deliveryDate)
                .setPrice(price)
                .setObjectId(objectId).build();


        ArrayList<Document> orderedProducts = (ArrayList<Document>) orderDocument.get("orderedProduct");

        for (Document temp : orderedProducts) {
            response.insertProduct(
                    temp.getInteger("ordered_quantity"),
                    temp.getString("productId"),
                    temp.getString("description"),
                    temp.getString("image")
            );
        }

        if (!pastOrder) {
            if (orderDocument.getString("user_username") == null)
                response.setUser_username(username_usr);
            else
                response.setSeller_username(username_usr);
        }

        return response;
    }

    /**
     * Utility method to convert an Order Class to its relative Document class
     *
     * @param order the Order class you want to convert
     * @param type  enum defining where you want to put the document (inside a user or seller class or inside a colleciton)
     * @return the relative Document
     */
    private Document orderToDocument(Order order, orderType type) {
        List<Document> productOrderedDocument;
        productOrderedDocument = new ArrayList<>();
        ArrayList<Order.ProductOrder> productOrdered = order.getProductOrders();

        //for every product inside the order
        for (Order.ProductOrder obj : productOrdered) {
            Document temp;

            temp = new Document()
                    .append("productId", obj.getProductID())
                    .append("ordered_quantity", obj.getOrdered_quantity());

            //only the user has the redundancy for the image and description
            if (type == orderType.user) {
                temp
                        .append("description", obj.getDescription())
                        .append("image", obj.getImage());
            }
            productOrderedDocument.add(temp);
        }

        //shared attributes between user seller and collection
        Document orderDocument = new Document()
                .append("_id", new ObjectId(order.getObjectId()))
                .append("price", order.getPrice());

        //shared attribute between user and collection
        if (type == orderType.user || type == orderType.collection)
            orderDocument.append("seller_username", order.getSeller_username());

        if (type == orderType.seller)
            orderDocument.append("user_username", order.getUser_username());

        //shared attribute between seller and user
        if (type == orderType.seller || type == orderType.user) {
            orderDocument.append("state", order.getState().toString());
            orderDocument.append("order_date", order.getOrderDate());
        }

        if (type == orderType.collection) {
            User user = getUserFromUsername(order.getUser_username());
            if (user == null) {  // SOMETHING WENT WRONG
                orderDocument.append("user_username", order.getUser_username());
            } else {
                Document userDoc = new Document()
                        .append("username", user.getUsername())
                        .append("age", user.getAge())
                        .append("gender", user.getGender());

                orderDocument.append("user", userDoc);
            }
            orderDocument.append("delivery_date", order.getDeliveryDate());
            orderDocument.append("order_date", order.getOrderDate());
        }

        orderDocument.append("orderedProduct", productOrderedDocument);

        return orderDocument;
    }

    /**
     * Utility method to convert an array of document the represent an array of Order into this array of Order
     *
     * @param orderDocuments an Array of document
     * @return an Array of the corresponding Order
     */
    private ArrayList<Order> documentsToOrders(String user_username, ArrayList<Document> orderDocuments) {
        ArrayList<Order> response = new ArrayList<>();

        if (orderDocuments == null)
            return response;

        for (int i = orderDocuments.size() - 1; i>= 0; i--) {
            Document document = orderDocuments.get(i);
            response.add(documentToOrder(user_username, document, false));
        }


        return response;
    }

    // ## CRUD OPERATIONS ##

    /**
     * Method to insert an order into the user and seller document
     *
     * @param order the order that needs to be added
     * @return true if every thing is Ok false if the insertion get some errors
     */
    public boolean insertOrder(Order order) {
        Pair<Bson, Bson> insertUser, insertSeller;

        order.setObjectId(ObjectId.get().toString());

        //retrieve the query for the user
        insertUser = insertOrderUser(order);

        //retrieve the query for the seller
        insertSeller = insertOrderSeller(order);

        ClientSession clientSession = mongoClient.startSession();

        //Transaction should guarantee ACID properties (in this case it's necessary)
        TransactionBody<String> txnFunc = () -> {

            //insert order into the user and the seller
            UpdateResult response;
            response = usersCollection.updateOne(clientSession, insertUser.getKey(), insertUser.getValue());
            if (response.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return "ERROR there is no such client";
            }

            response = usersCollection.updateOne(clientSession, insertSeller.getKey(), insertSeller.getValue());
            if (response.getModifiedCount() == 0) {
                clientSession.abortTransaction();
                return "ERROR there is no such seller";
            }

            for (Order.ProductOrder product : order.getProductOrders()) {
                Bson query = eq("_id", new ObjectId(product.getProductID())), update, increment;

                //get product document to control availability
                Document productDocument = productsCollection.find(clientSession, query).first();

                //update the product quantity sold member
                increment = new Document()
                        .append("quantity_sold", product.getOrdered_quantity())
                        .append("quantity_available", -(product.getOrdered_quantity()));

                update = new Document("$inc", increment);
                productsCollection.updateOne(clientSession, query, update);

                //if there is no such product return an error
                if (productDocument == null) {
                    clientSession.abortTransaction();
                    return "ERROR there is no such product";
                }

                int quantity_available;
                quantity_available = productDocument.getInteger("quantity_available");

                //if I ask for to many product return an error
                if (quantity_available < product.getOrdered_quantity()) {
                    clientSession.abortTransaction();
                    return "error";
                }
            }

            return "OK";
        };

        String errorMessage = "";
        try {
            errorMessage = clientSession.withTransaction(txnFunc);
        } catch (Exception e) {
            System.out.println("ERROR");
        } finally {
            clientSession.close();
        }

        return errorMessage.equals("OK");
    }

    /**
     * needs to get the query and the update to insert a new order into the user document
     *
     * @param order represent the orders the need to be inserted
     * @return a pair containing as a key the query and as a value the update
     */
    private Pair<Bson, Bson> insertOrderUser(Order order) {
        Document orderDocument = orderToDocument(order, orderType.user);

        Bson query, update;

        query = eq("username", order.getUser_username());
        update = Updates.addToSet("orders", orderDocument);

        return new Pair<>(query, update);
    }

    /**
     * needs to get the query and the update to insert a new order into the seller document
     *
     * @param order represent the orders the need to be inserted
     * @return a pair containing as a key the query and as a value the update
     */
    private Pair<Bson, Bson> insertOrderSeller(Order order) {

        Document orderDocument = orderToDocument(order, orderType.seller);

        Bson query, update;

        query = eq("username", order.getSeller_username());
        update = Updates.addToSet("orders", orderDocument);

        return new Pair<>(query, update);
    }

    /**
     * Delete an order
     *
     * @param order               Order to be delete
     * @param restoreAvailability true if you want to restore the quantity_sold as if the orders was never made
     * @return true if the deletion was successful
     */
    public boolean deleteOrder(Order order, boolean restoreAvailability) {
        //retrieve IDs of user seller and order
        ObjectId orderId;
        String user_username = order.getUser_username();
        String seller_username = order.getSeller_username();
        orderId = new ObjectId(order.getObjectId());

        ClientSession clientSession = mongoClient.startSession();

        //this transaction should be ACID
        TransactionBody<String> txn = () -> {
            Bson query, update;
            update = Updates.pull("orders", new Document("_id", orderId));

            query = or(
                    eq("username", user_username),
                    eq("username", seller_username)
            );

            UpdateResult response = usersCollection.updateMany(clientSession, query, update);
            if (response.getModifiedCount() != 2) {
                clientSession.abortTransaction();
                return "ERROR";
            }
            return "OK";
        };

        //launch transaction and wait for response
        String transactionOK;
        try {
            transactionOK = clientSession.withTransaction(txn);
        } catch (Exception e) {
            System.out.println("Error");
            transactionOK = "ERROR";
        } finally {
            clientSession.close();
        }

        //if the transaction was OK and i want to restore the availability I start restoring
        if (transactionOK.equals("OK") && restoreAvailability) {

            for (Order.ProductOrder product : order.getProductOrders()) {
                Bson query, update, increment;
                UpdateResult response;

                query = eq("_id", new ObjectId(product.getProductID()));

                increment = new Document()
                        .append("quantity_sold", -(product.getOrdered_quantity()))
                        .append("quantity_available", product.getOrdered_quantity());

                update = new Document("$inc", increment);

                response = productsCollection.updateOne(query, update);


            }
        }

        return true;
    }

    /**
     * Change the state of an order and then if the new state is equal to "arrived" then move the order into the orders collection
     * and update the reviews member of the user
     *
     * @param order    the target order
     * @param newState new state of the order
     * @return true if che change was successful
     */
    public boolean changeOrderState(Order order, OrderState newState) {

        //change order state
        changeOrderStateKernel(order, newState);

        //check if the order needs to be inserted into the order collection
        if (newState == OrderState.arrived) {
            deleteOrder(order, false);
            order.setDeliveryDate(new Date());
            insertOrderCollection(order);

            //time to check if exists an user review for that product if not create a review object
            //check for every product
            String user_username = order.getUser_username();
            for (Order.ProductOrder product : order.getProductOrders()) {

                Document response;
                Bson query = and(
                        eq("username", user_username),
                        eq("reviews.productId", product.getProductID())
                );

                response = usersCollection.find(query).first();

                //if review not exist create a new empty review
                if (response == null) {
                    Document emptyReview = new Document()
                            .append("productId", product.getProductID())
                            .append("stars", -1);
                    usersCollection.updateOne(eq("username", user_username), Updates.addToSet("reviews", emptyReview));
                }
            }

        }

        return true;
    }

    /**
     * Change the state of an order
     *
     * @param order Target Order
     * @return true if the order was found
     */
    private boolean changeOrderStateKernel(Order order, OrderState newState) {
        //retrieve user order and product id
        ObjectId orderId;
        String user_username = order.getUser_username();
        String seller_username = order.getSeller_username();
        orderId = new ObjectId(order.getObjectId());

        Bson query = and(
                or(
                        eq("username", user_username),
                        eq("username", seller_username)
                ),
                eq("orders._id", orderId)
        );

        Bson update = Updates.set("orders.$.state", newState.toString());

        UpdateResult response = usersCollection.updateMany(query, update);

        return response.getModifiedCount() == 2;
    }

    public void insertOrderCollection(Order order) {
        Document orderDocument = orderToDocument(order, orderType.collection);

        ordersCollection.insertOne(orderDocument);

    }

    public void deleteOrderCollectionById(String orderID) {
        ordersCollection.deleteOne(eq("_id", new ObjectId(orderID)));

    }

    // ## QUERIES ##
    public ArrayList<Order> viewActiveOrders(User user){
        Document userDocument = usersCollection.find(eq("username", user.getUsername())).first();

        if(userDocument == null)
            return null;
        return documentToUser(userDocument).getOrders();
    }

    public ArrayList<Order> viewPastOrders(User user) {
        ArrayList<Order> pastOrders = new ArrayList<>();

        FindIterable<Document> documents;
        if (user.getRole().equals("User")) {
            documents = ordersCollection.find(eq("user.username", user.getUsername())).sort(descending("delivery_date"));
        } else {
            documents = ordersCollection.find(eq("seller_username", user.getUsername())).sort(descending("delivery_date"));
        }

        for (Document document : documents) {
            Order order = documentToOrder(user.getUsername(), document, true);
            pastOrders.add(order);
        }

        return pastOrders;
    }

    //
    // ------------------------------ ANALYTICS ------------------------------
    //

    // ## CONVERTIONS ##
    /**
     * Converts a document into an Analytics instance
     * @param analyticsDocument document to be converted
     * @return  Analytics instance
     */
    private Analytics convertToAnalytics(Document analyticsDocument) {
        String objectId = analyticsDocument.get("_id").toString();
        String seller_username = analyticsDocument.getString("seller_username");
        int total_reviews = analyticsDocument.getInteger("total_reviews");
        double avg_reviews = analyticsDocument.getDouble("avg_reviews");
        int avg_delivery = analyticsDocument.getInteger("avg_delivery");
        double total_earnings = analyticsDocument.getDouble("total_earnings");
        int total_sales = analyticsDocument.getInteger("total_sales");
        int month = analyticsDocument.getInteger("month");

        ArrayList<String> productDetails = new ArrayList<>();
        Product newProduct = null;
        Document bestSellingProduct_doc = (Document) analyticsDocument.get("best_selling_product");
        if (bestSellingProduct_doc != null) {
            for (String key : bestSellingProduct_doc.keySet()) {
                productDetails.add(bestSellingProduct_doc.get(key).toString());
            }
             newProduct = Product.ProductBuilder()
                    .setObjectId(productDetails.get(0))
                    .setDescription(productDetails.get(1)).build();
        }

        int[] star_distribution_arr = {0, 0, 0, 0, 0};
        Document starDistribution_doc = (Document) analyticsDocument.get("star_distribution");
        int i = 0;
        if (starDistribution_doc != null) {
            for (String key : starDistribution_doc.keySet()) {
                star_distribution_arr[i] = Integer.parseInt(starDistribution_doc.get(key).toString());
                i++;
            }
        }

        ArrayList<Document> analyticsDays = (ArrayList<Document>) analyticsDocument.get("sales_by_day");
        ArrayList<DailyAnalytics> sales_by_day = null;
        if (analyticsDays != null) {
            sales_by_day = new ArrayList<>();
            for (Document analyticDay : analyticsDays) {
                sales_by_day.add(convertToDailyAnalytics(analyticDay));
            }
        }

        return Analytics.AnalyticsBuilder()
                .setObjectId(objectId)
                .setSellerUsername(seller_username)
                .setTotalReviews(total_reviews)
                .setAvgReviews(avg_reviews)
                .setStarsDistribution(star_distribution_arr)
                .setAvgDelivery(avg_delivery)
                .setTotalEarnings(total_earnings)
                .setTotalSales(total_sales)
                .setBestSellingProduct(newProduct)
                .setSalesByDay(sales_by_day)
                .setMonth(month).build();
    }

    /**
     * Converts a document into a DailyAnalytics instance
     * @param dailyAnalyticsDocument    document to be converted
     * @return  a DailyAnalytics instance
     */
    private DailyAnalytics convertToDailyAnalytics(Document dailyAnalyticsDocument) {
        int day_num = dailyAnalyticsDocument.getInteger("day");
        int all_sales = dailyAnalyticsDocument.getInteger("all_sales");
        int male_sales = dailyAnalyticsDocument.getInteger("male_sales");
        int female_sales = dailyAnalyticsDocument.getInteger("female_sales");
        int young_sales = dailyAnalyticsDocument.getInteger("young_sales");
        int old_sales = dailyAnalyticsDocument.getInteger("old_sales");

        return DailyAnalytics.DailyAnalyticsBuilder()
                .setDay_num(day_num)
                .setAll_sales(all_sales)
                .setMale_sales(male_sales)
                .setFemale_sales(female_sales)
                .setYoung_sales(young_sales)
                .setOld_sales(old_sales).build();
    }

    /**
     * Converts a DailyAnalytics instance into a document
     * @param dailyAnalytics    instance to be converted
     * @return  document
     */
    private Document convertDailyAnalyticsToDocument(DailyAnalytics dailyAnalytics) {
        return new Document("day", dailyAnalytics.getDay_num())
                .append("all_sales", dailyAnalytics.getAll_sales())
                .append("male_sales", dailyAnalytics.getMale_sales())
                .append("female_sales", dailyAnalytics.getFemale_sales())
                .append("young_sales", dailyAnalytics.getYoung_sales())
                .append("old_sales", dailyAnalytics.getOld_sales());
    }

    // ## QUERIES ##
    /**
     * Retrieves the analytics of the seller passed as parameter
     * @param user  seller whose analytics we want to retrieve
     * @return  analytics
     */
    public Analytics getSellerAnalytics(User user) {
        Document analyticsDocument;
        Analytics newAnalytics = Analytics.AnalyticsBuilder();

        if(user == null || user.getRole().equals("User") || user.getUsername() == null)
            return newAnalytics;

        String seller_username = user.getUsername();

        analyticsDocument = analyticsCollection.find(eq("seller_username", seller_username)).first();

        if (analyticsDocument != null) {
            newAnalytics = convertToAnalytics(analyticsDocument);

        }

        return newAnalytics;
    }

    /**
     * Returns a list of sellers ordered by their average review
     *
     * @param username if not null is a string that must be contained in the usernames of the sellers
     * @param sorting  which sort to perform
     *                 sorting = 1 -> ascending    sorting = 0 -> descending
     * @return list of sellers
     */
    public ArrayList<User> getSellerRanking(String username, int sorting) {
        ArrayList<User> sellers = new ArrayList<>();

        FindIterable<Document> sellersDocs;

        if(username != null) {
            sellersDocs = analyticsCollection.find(regex("seller_username", username));
        } else {
            sellersDocs = analyticsCollection.find();
        }

        if(sorting == 1) {
            sellersDocs = sellersDocs.sort(ascending("avg_reviews"));
        } else {
            sellersDocs = sellersDocs.sort(descending("avg_reviews"));
        }

        sellersDocs.limit(10);

        for(Document doc : sellersDocs) {
            String seller_username = doc.getString("seller_username");
            double avg_reviews = doc.getDouble("avg_reviews");
            int total_reviews = doc.getInteger("total_reviews");
            User seller = User.UserBuilder().setUsername(seller_username).setAverageReview(avg_reviews).setTotalReviews(total_reviews).build();
            sellers.add(seller);
        }

        return sellers;
    }
}