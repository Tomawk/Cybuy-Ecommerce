package lsmd.group17.cybuy.persistence;

import com.mongodb.ConnectionString;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.Product;
import lsmd.group17.cybuy.model.User;
import lsmd.group17.cybuy.persistence.MongoDB.MongoDBDriver;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) {
        ConnectionString uri = new ConnectionString("mongodb+srv://Federica:Federica@lsmd.jyn0m.mongodb.net/cybuy?retryWrites=true&w=majority");
        MongoDBDriver mongoDBDriver = new MongoDBDriver(uri);
        /*mongoDBDriver.putOrdersUsernames();*/
        //mongoDBDriver.changeOrderStatus();
        //mongoDBDriver.putSellerUsername();
        // #### getBestSellingProduct test ###

        //mongoDBDriver.insertAnalyticsDocument();
        /*
        User test = mongoDBDriver.getUserFromId("5fdf8787a8335e0908c1e157");
        Product mostSelledProduct = mongoDBDriver.getBestSellingProduct(test);
        System.out.println("ObjectId: " + mostSelledProduct.getObjectId() + "\n");
        System.out.println("description: " + mostSelledProduct.getDescription() + "\n");
        System.out.println("price: " + mostSelledProduct.getPrice() + "\n");
        System.out.println("image: " + mostSelledProduct.getImage() + "\n");
        */

        /* #### TotalSales test ####
        User test = mongoDBDriver.getUserFromId("5fdf8787a8335e0908c1e143");
        int totalSales = mongoDBDriver.getTotalSales(test);
        System.out.println(totalSales);*/

        //mongoDBDriver.insertAverageReview();

     /*   User tommy = mongoDBDriver.getUserFromId("5fef4603c93ea608c9ef8ead");
        Product prova = mongoDBDriver.getProductFromId("5fe9a410845df004b4aceb6c");
        Product prova2 = mongoDBDriver.getProductFromId("5fe9a410845df004b4aceb6d");
        Product prova3 = mongoDBDriver.getProductFromId("5fe9a410845df004b4aceb6e");*/

        /*levelDB.addProductToCart(tommy, prova);
        levelDB.addProductToCart(tommy, prova2);
        levelDB.addProductToCart(tommy, prova2);
        levelDB.addProductToCart(tommy, prova3);*/
        //System.out.println(levelDB.getValue("cart:user:5fef4603c93ea608c9ef8ead:product:5fdb8ed70e189329746b49d9:quantity"));
        //System.out.println(levelDB.getValue("product:5fdb8ed70e189329746b49d9:description"));

        /*
        /* For Testing addProductWishlist
        levelDB.putValue("wishlist:user:1:product:1:date", "20/12/2020");
        levelDB.putValue("wishlist:user:1:product:2:date", "20/12/2020");
        levelDB.putValue("wishlist:user:2:product:1:date", "20/12/2020");
        levelDB.putValue("wishlist:user:2:product:2:date", "20/12/2020");
        System.out.println("\n\nStampa database prima della addWishlist\n");
        levelDB.printDb();
        System.out.println("\n\nStampa database dopo addWishlist\n");
        levelDB.addProductWishlist(test_user,test_product);
        levelDB.printDb();
        levelDB.closeDB();
        */
    }
}
