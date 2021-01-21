package lsmd.group17.cybuy.persistence.levelDB;

import lsmd.group17.cybuy.model.Product;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LevelDB {
    private static volatile LevelDB instance;
    private DB db;
    boolean dbOpen;

    //
    // ------------------------------ UTILITIES ------------------------------
    //
    /**
     * Default constructor
     */
    private LevelDB() {
        db = null;
        dbOpen = false;
    }

    public static LevelDB getInstance() {
        if (instance == null) {
            synchronized (LevelDB.class) {
                if (instance == null)
                    instance = new LevelDB();
            }
        }
        return instance;
    }

    /**
     * Opens the Key-Value database file. If not present, the file is created.
     */
    public void openDB() {
        if (dbOpen) {
            throw new IllegalStateException("Database is already open.");
        } else {
            Options options = new Options();
            options.createIfMissing(true);
            try {
                db = factory.open(new File("Database"), options);
                dbOpen = true;
            } catch (final IOException e) {
                System.out.println("Error opening the database. Closing.");
                e.printStackTrace();
                closeDB();
            }
        }
    }

    /**
     * Closes the Key-Value database file.
     */
     public void closeDB() {
        try {
            // check if the database has been instantiated
            if (db != null) {
                db.close();
                dbOpen = false;
            }
        } catch (final IOException e) {
            System.out.println("Error closing the database.");
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new entry for the given key-value pair in the database.
     *
     * @param key    the key for the new entry;
     * @param value  the value for the new entry;
     */
     public void putValue(final String key, final String value) {
        db.put(bytes(key), bytes(value));
    }

    /**
     * Retrieves the value for the given key.
     *
     * @param key  the key to be searched;
     *
     * @return the retrieved value.
     */
     public String getValue(final String key) {
        return asString(db.get(bytes(key)));
    }

    /**
     * Deletes the value for the given key.
     *
     * @param key  the key to be searched.
     */
     public void deleteValue(final String key) {
        db.delete(bytes(key));
    }

    /**
     * Deletes the values for the given keys
     *
     * @param keys  ArrayList of keys to be deleted
     * @return  true if the deletion was successful
     */
     private boolean multipleDelete(ArrayList<String> keys) {
        try (WriteBatch batch = db.createWriteBatch()) {
            for(String key : keys) {
                batch.delete(bytes(key));
            }
            db.write(batch);
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //
    // ------------------------------ PRODUCTS ------------------------------
    //
    // The structure of the key is the following:
    // product:$product_id:description = $description
    // product:$product_id:price = $price
    // product:$product_id:image = $image

    /**
     * Inserts redundancies regarding products
     * @param product   products whose redundancies we want to insert
     * @return  true if the insertion was successful
     */
    public boolean insertProductInfos(Product product) {
        if(!dbOpen) openDB();

        String id = product.getObjectId();

        String usageKey = "product:" + id + ":usage";
        String usageValue = getValue(usageKey);


        if(usageValue == null) {    // The redundancy is not present yet
            String descriptionKey = "product:" + id + ":description";
            String priceKey = "product:" + id + ":price";
            String imageKey = "product:" + id + ":image";

            String description = product.getDescription();
            String price = String.valueOf(product.getPrice());
            String image = product.getImage();

            try (WriteBatch batch = db.createWriteBatch()) {
                batch.put(bytes(descriptionKey), bytes(description));
                batch.put(bytes(priceKey), bytes(price));
                batch.put(bytes(imageKey), bytes(image));
                batch.put(bytes(usageKey), bytes("1"));
                db.write(batch);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {    // The redundancy is already present
            incrementUsage(id);
        }
        return true;
    }

    /**
     * Increment the usage value of the product whose id is passed as a parameter
     * @param product_id    id of the product whose usage has to be incremented
     */
     private void incrementUsage(String product_id){
        String usage_key = "product:" + product_id + ":usage";
        int usage = Integer.parseInt(getValue(usage_key));
        usage += 1;
        putValue(usage_key,String.valueOf(usage));
    }

    /**
     * Decrement the usage value of the product whose id is passed as a parameter
     *
     * @param product_id    id of the product whose usage has to be decremented
     */
     private void decrementUsage(String product_id){
        String usage_key = "product:" + product_id + ":usage";
        String usages = getValue(usage_key);
        int usage = 0;

        if(usages != null) {
             usage = Integer.parseInt(usages);
             usage -= 1;
        }

        if(usage > 0){
            putValue(usage_key,String.valueOf(usage));
        } else { // If the usage is 0 or below we can remove the redundancy
            cleanRedundancy(product_id);
        }
    }

    /**
     * Function that deletes the infos of the product whose id is passed as a parameter
     * @param product_id    id of the product whose infos have to be deleted
     */
     private void cleanRedundancy(String product_id) {
        String usageKey = "product:" + product_id + ":usage";
        String descriptionKey = "product:" + product_id + ":description";
        String priceKey = "product:" + product_id + ":price";
        String imageKey = "product:" + product_id + ":image";

        ArrayList<String> keys = new ArrayList<>();
        keys.add(usageKey);
        keys.add(descriptionKey);
        keys.add(priceKey);
        keys.add(imageKey);

        multipleDelete(keys);
    }

    /**
     * Get information about the product provided as an argument: price, url image, description
     * @param id objectId of the product we want to retrive the infos
     * @return a product instance
     */
     public Product getProductInfo(String id) {
        //Open the database
        if(!dbOpen) openDB();

        String key_1 = "product:" + id + ":description";
        String description = getValue(key_1);
        String key_2 = "product:" + id + ":image";
        String image = getValue(key_2);
        String key_3 = "product:" + id + ":price";
        String price = getValue(key_3);
        
        double priceDouble = 0;
        
        if(price != null)
            priceDouble = Double.parseDouble(price);

        return Product.ProductBuilder()
                .setObjectId(id)
                .setDescription(description)
                .setImage(image)
                .setPrice(priceDouble).build();
    }

    /**
     * Removes a product entirely from the db
     * @param product   product to be removed
     * @return  true if the deletion was successful
     */
     public boolean removeProduct(Product product){
        //open db
        if(!dbOpen) openDB();

        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());

                // check if it is a cart record
                if (key.startsWith("cart:")) {
                    String[] parts = key.split(":");
                    if (parts[4].equals(product.getObjectId())) {
                        String user_id = parts[2];
                        product.setQuantityAvailable(0);
                        if(!updateProductQuantityCart(user_id, product))
                            return false;
                    }
                // check if it is a wishlist record
                } else if (key.startsWith("wishlist:")) {
                    String[] parts = key.split(":");
                    if (parts[4].equals(product.getObjectId())) {
                        String user_id = parts[2];
                        if(!removeProductWishlist(user_id, product))
                            return false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //
    // ------------------------------ CART ------------------------------
    //
    // The structure of the key is the following:
    // cart:user:$user_id:product:$product_id:quantity = $quantity

    /**
     * Function that add a product to the user's cart
     * @param user_id user that added the product
     * @param product product that was added to the user's cart
     * @return the updated quantity
     */
     public int addProductToCart(String user_id, Product product){
        //open db
        if(!dbOpen) openDB();

        //If the product is already present in the cart, its quantity needs to be increased
        String product_id = product.getObjectId();

        String key = "cart:user:" + user_id + ":product:" + product_id +":quantity";
        String quantity = getValue(key);

        if(quantity == null){
            putValue(key,"1"); //item is not in the cart -> added the product with quantity = 1
            // I also need to handle the redundancies
            if(insertProductInfos(product))
                return 1;
            return 0;
        } else { // item already in the cart, update its quantity
            int quantity_int = Integer.parseInt(quantity);
            quantity_int += 1;
            String new_quantity_string = "" + quantity_int;
            putValue(key,new_quantity_string);
            return quantity_int;
        }
    }

     public ArrayList<Product> getProductsInCart(String user_id) throws IOException {
        ArrayList<Product> products = new ArrayList<>();

        //open db
        if(!dbOpen) openDB();

        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());

                // check if it is a cart record for the user passed as a parameter
                if (key.startsWith("cart:")) {
                    String[] parts = key.split(":");
                    if (parts[2].equals(user_id)) {
                        Product newProduct = getProductInfo(parts[4]);

                        // Since the quantityAvailable parameter of Product is unused, I can use it as a parameter for the quantity
                        newProduct.setQuantityAvailable(Integer.parseInt(getValue(key)));
                        products.add(newProduct);
                    }
                }
            }
        }

        return products;
    }

    /**
     * Function that remove a product to the user's cart
     * @param user_id user that had the product
     * @param product product that it wil be deleted from the user's cart
     * @return quantity after the remove
     */
     public int removeProductCart(String user_id, Product product){
        //open db
        if(!dbOpen) openDB();

        //Check if the product is already present in the cart -> i need to increase the quantity
        String product_id = product.getObjectId();
        String key = "cart:user:" + user_id + ":product:" + product_id +":quantity";
        String quantity = getValue(key);

        if(quantity == null){
            //nothing happen there is no such items (maybe something is wrong?)
            return -1;
        }else{
            int quantity_int = Integer.parseInt(quantity);

            //remove the previous value
            deleteValue(key);

            quantity_int--;

            //insert the updated value iff the quantity is greater than 0
            if(quantity_int > 0)
                putValue(key, String.valueOf(quantity_int));
            else {
                decrementUsage(product_id);
            }
            return quantity_int;
        }
    }

    /**
     * Function that empties the cart of the user passed as a parameter
     *
     * @param user_id  user whose cart has to be emptied
     * @return  true if the deletion was successful
     */
     public boolean emptyCart(String user_id) {
        ArrayList<String> userCartKeys = new ArrayList<>();
        ArrayList<String> productsIds = new ArrayList<>();

        //Open the database
        if(!dbOpen) openDB();

        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());

                // check if it is a cart record for the user passed as a parameter
                if (key.startsWith("cart:")) {
                    String[] parts = key.split(":");
                    if(parts[2].equals(user_id)) {
                        userCartKeys.add(key);
                        productsIds.add(parts[4]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean b = true;

        if(userCartKeys.size() > 0 && productsIds.size() > 0) {
            // By removing the product from the cart, I need to decrease its usage
            for (String id : productsIds) decrementUsage(id);
             b = multipleDelete(userCartKeys);
        }

        return b;
    }

     public boolean updateProductQuantityCart(String user_id, Product product) {
        //open db
        if(!dbOpen) openDB();

        //Check if the product is already present in the cart
        String product_id = product.getObjectId();
        String key = "cart:user:" + user_id + ":product:" + product_id +":quantity";
        String old_quantity = getValue(key);

        String quantity = String.valueOf(product.getQuantityAvailable());
        if(quantity.equals("0")) {
            if (old_quantity != null) {
                int oldQuantity = Integer.parseInt(old_quantity);
                while (oldQuantity != 0) {
                    oldQuantity = removeProductCart(user_id, product);
                }
            }
            return true;
        } else {
            if(old_quantity == null)
                insertProductInfos(product);
            putValue(key, quantity);
        }

        return true;
    }

    //
    // ------------------------------ WISHLIST ------------------------------
    // The structure of the key is the following:
    // wishlist:user:$user_id:product:$product_id:date = $date

    /**
     * Function that add a product to the user's wishlist
     * @param user_id user that added the product
     * @param product product that was added to the user's wishlist
     * @return true if the product is inserted correctly
     */

     public boolean addProductWishlist(String user_id, Product product){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String current_date = dtf.format(now); //format dd/MM/yyyy

        //open db
        if(!dbOpen) openDB();

        //Check if the product is already present in the wishlist
        String product_id = product.getObjectId();

        String key = "wishlist:user:" + user_id + ":product:" + product_id +":date";
        String date = getValue(key);

        if(date == null){ //item is not in the wishlist -> add a product with the current date
            putValue(key, current_date);
            // I also need to handle the redundancies
            insertProductInfos(product);
        } else { //item already in the wishlist
            return true;
        }
        return true;
    }

    /**
     * Function that remove a product to the user's wishlist
     * @param user_id user that want to remove the product from the wishlist
     * @param product product that it wil be deleted from the user's wishlist
     * @return true if the product was successfully removed
     */
     public boolean removeProductWishlist(String user_id, Product product){

        //open db
        if(!dbOpen) openDB();

        String product_id = product.getObjectId();
        String key = "wishlist:user:" + user_id + ":product:" + product_id +":date";

        String date = getValue(key);

        if(date != null) {
            decrementUsage(product_id);
            deleteValue(key);
        }

        return true;
    }

    /**
     * Function that empties the wishlist of the user passed as a parameter
     *
     * @param user_id  user whose wishlist has to be emptied
     * @return  true if the deletion was successful
     *
     * wishlist:user:$user_id:product:$product_id:date = $date
     */
     public boolean emptyWishlist(String user_id) {
         ArrayList<String> userWishlistKeys = new ArrayList<>();

         //Open the database
         if (!dbOpen) openDB();

         try (DBIterator iterator = db.iterator()) {
             for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                 String key = asString(iterator.peekNext().getKey());

                 // check if it is a cart record for the user passed as a parameter
                 if (key.startsWith("wishlist:")) {
                     String[] parts = key.split(":");
                     if (parts[2].equals(user_id)) {
                         userWishlistKeys.add(key);
                     }
                 }
             }
         } catch (IOException e) {
             e.printStackTrace();
         }

         boolean b = true;
         if (userWishlistKeys.size() > 0) {
             // Handling the redundancy
             for (String userWishlistKey : userWishlistKeys) {
                 String[] substrings = userWishlistKey.split(":");
                 decrementUsage(substrings[4]);
             }
         b = multipleDelete(userWishlistKeys);
     }

         return b;
    }

     public ArrayList<Product> getProductsInWishlist(String user_id) throws IOException {
        ArrayList<Product> products = new ArrayList<>();

        //open db
        if(!dbOpen) openDB();

        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());

                // check if it is a wishlist record for the user passed as a parameter
                if (key.startsWith("wishlist:")) {
                    String[] parts = key.split(":");
                    if (parts[2].equals(user_id)) {
                        Product newProduct = getProductInfo(parts[4]);

                        products.add(newProduct);
                    }
                }
            }
        }

        return products;
    }
}


