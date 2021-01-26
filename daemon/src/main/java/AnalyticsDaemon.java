import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import model.Analytics;
import model.DailyAnalytics;
import model.Product;
import model.User;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Projections.computed;
import static com.mongodb.client.model.Sorts.descending;

public class AnalyticsDaemon {
    private static MongoCollection<Document> usersCollection;
    private static MongoCollection<Document> productsCollection;
    private static MongoCollection<Document> ordersCollection;
    private static MongoCollection<Document> analyticsCollection;

    public static void main(String[] args) {
        ConnectionString uri = new ConnectionString("mongodb://172.16.3.138:27020,172.16.3.139:27020,172.16.3.140:27020");
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("cybuy");
        usersCollection = database.getCollection("users");
        productsCollection = database.getCollection("products");
        ordersCollection = database.getCollection("orders");
        analyticsCollection = database.getCollection("analytics")
                .withWriteConcern(WriteConcern.W3);

        updateAnalytics();

        mongoClient.close();
    }

    /**
     * Converts an Analytics instance into a Document
     * @param analytics instance to be converted
     * @return  Document
     */
    private static Document convertAnalyticsToDocument(Analytics analytics) {
        Document analyticsDocument = new Document("seller_username", analytics.getSellerUsername())
                .append("total_reviews", analytics.getTotalReviews())
                .append("avg_reviews", analytics.getAvgReviews())
                .append("avg_delivery", analytics.getAvgDelivery())
                .append("total_earnings", analytics.getTotalEarnings())
                .append("total_sales", analytics.getTotalSales())
                .append("month", analytics.getMonth());

        if(analytics.getBestSellingProduct() != null) {
            Document BestSelling_Product = new Document();
            BestSelling_Product.append("productId", analytics.getBestSellingProduct().getObjectId());
            BestSelling_Product.append("description", analytics.getBestSellingProduct().getDescription());
            analyticsDocument.append("best_selling_product", BestSelling_Product);
        }

        Document StarDistribution_doc = new Document();
        StarDistribution_doc.append("1", analytics.getStarsDistribution()[0]);
        StarDistribution_doc.append("2", analytics.getStarsDistribution()[1]);
        StarDistribution_doc.append("3", analytics.getStarsDistribution()[2]);
        StarDistribution_doc.append("4", analytics.getStarsDistribution()[3]);
        StarDistribution_doc.append("5", analytics.getStarsDistribution()[4]);
        analyticsDocument.append("star_distribution", StarDistribution_doc);

        ArrayList<DailyAnalytics> sales_by_day = analytics.getSalesByDay();

        if(sales_by_day != null) {
            ArrayList<Document> salesByDayDocs = new ArrayList<>();

            for (DailyAnalytics dailyAnalytics : sales_by_day) {
                if(dailyAnalytics != null) {
                    Document dailyAnalyticsDoc = convertDailyAnalyticsToDocument(dailyAnalytics);
                    salesByDayDocs.add(dailyAnalyticsDoc);
                }
            }

            analyticsDocument.append("sales_by_day", salesByDayDocs);
        }

        return analyticsDocument;
    }

    /**
     * Converts a document into an Analytics instance
     * @param analyticsDocument document to be converted
     * @return  Analytics instance
     */
    private static Analytics convertToAnalytics(Document analyticsDocument) {

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
    private static DailyAnalytics convertToDailyAnalytics(Document dailyAnalyticsDocument) {
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
    private static Document convertDailyAnalyticsToDocument(DailyAnalytics dailyAnalytics) {
        return new Document("day", dailyAnalytics.getDay_num())
                .append("all_sales", dailyAnalytics.getAll_sales())
                .append("male_sales", dailyAnalytics.getMale_sales())
                .append("female_sales", dailyAnalytics.getFemale_sales())
                .append("young_sales", dailyAnalytics.getYoung_sales())
                .append("old_sales", dailyAnalytics.getOld_sales());
    }

    /**
     * Sets the average review and the total number of reviews for the user passed as parameter
     *
     * @param user Seller of whom we want to get reviews analytics
     */
    public static void getReviewsAnalytics(User user) {
        String seller_username = user.getUsername();

        Bson match = match(eq("seller_username", seller_username));
        Bson firstSet = eq("$set", eq("sum_of_reviews", eq("$multiply", Arrays.asList("$average_review", "$total_reviews"))));
        Bson group = group("$seller_username", sum("total_seller_reviews", "$total_reviews"), sum("sum_of_seller_reviews", "$sum_of_reviews"));

        BsonArray cond = new BsonArray();
        BsonArray eq = new BsonArray();
        BsonArray divide = new BsonArray();
        eq.add(new BsonString("$total_seller_reviews"));
        eq.add(new BsonInt32(0));
        divide.add(new BsonString("$sum_of_seller_reviews"));
        divide.add(new BsonString("$total_seller_reviews"));
        cond.add(new BsonDocument("$eq", eq));
        cond.add(new BsonDouble(0.0));
        cond.add(new BsonDocument("$divide", divide));
        BasicDBObject fullCond = new BasicDBObject("$cond", cond);

        Bson secondSet = eq("$set", eq("average_seller_review", fullCond));

        AggregateIterable<Document> aggregation = productsCollection.aggregate(Arrays.asList(match, firstSet, group, secondSet));
        Document doc = aggregation.first();

        if (doc != null) {
            user.setTotalReviews(doc.getInteger("total_seller_reviews"));
            user.setAverageReview(doc.getDouble("average_seller_review"));
        }
    }

    /**
     * @param user the seller
     * @return Returns the distribution of stars between product
     */
    public static int[] getNumberOfStars(User user) {

        int[] res = {0, 0, 0, 0, 0};

        Bson matchSeller = match(eq("seller_username", user.getUsername()));

        Bson groupByUser = group("$user.username", Accumulators.push("orderedProduct", "$orderedProduct"));

        Bson lookupUser = lookup("users", "_id", "username", "user");

        Bson clean = project(
                fields(
                        include("orderedProduct"),
                        computed(
                                "reviews",
                                new Document("$arrayElemAt", Arrays.asList("$user.reviews", 0)
                                )
                        )
                )
        );

        Bson unwind1 = unwind("$reviews");

        Bson unwind2 = unwind("$orderedProduct");

        Bson compare = project(
                fields(
                        include("reviews"),
                        include("orderedProduct"),
                        computed("compare", new Document("$cmp", Arrays.asList("$reviews.productId", "$orderedProduct.productId")))
                )
        );

        Bson matchRight = match(eq("compare", 0));

        Bson groupByStars = group("$reviews.stars", sum("stars_number", 1));

        AggregateIterable<Document> aggregation = ordersCollection.aggregate(Arrays.asList(
                matchSeller,
                groupByUser,
                lookupUser,
                clean,
                unwind1,
                unwind2,
                unwind2,
                compare,
                matchRight,
                groupByStars
        ));

        aggregation.forEach((d) -> {
            int index = d.getInteger("_id") - 1;
            if (index >= 0 && index <= 4)
                res[index] = d.getInteger("stars_number");
        });


        return res;
    }

    /**
     * @param user the seller
     * @return Returns the average delivery time
     */
    public static int getAverageDeliveryTime(User user) {

        Bson matchSeller = match(eq("seller_username", user.getUsername()));

        Bson getDuration = project(
                fields(
                        include("seller_username"),
                        computed("delivery_duration",
                                new Document("$subtract", Arrays.asList("$delivery_date", "$order_date")))
                )
        );

        Bson groupByUser = group(
                "$seller_username",
                avg("average_duration", "$delivery_duration")
        );

        AggregateIterable<Document> aggregation = ordersCollection.aggregate(
                Arrays.asList(
                        matchSeller,
                        getDuration,
                        groupByUser
                )
        );

        if (aggregation.first() == null)
            return 0;

        double msTime = aggregation.first().getDouble("average_duration");

        return (int) (msTime / (1000 * 60 * 60 * 24));
    }

    /**
     * Returns the total earnings of the user passed as parameter
     *
     * @param user Seller of whom we want to get total earnings
     * @return total earnings
     */
    public static double getTotalEarnings(User user) {
        Bson match = match(eq("seller_username", user.getUsername()));
        Bson set = eq("$set", eq("product_earnings", eq("$multiply", Arrays.asList("$quantity_sold", "$price"))));
        Bson group = group("$seller_username", sum("total_earnings", "$product_earnings"));

        AggregateIterable<Document> aggregation = productsCollection.aggregate(Arrays.asList(match, set, group));
        Document doc = aggregation.first();

        if (doc == null) return 0.0;

        return doc.getDouble("total_earnings");
    }

    /**
     * Get the total number of sales of a target seller
     *
     * @param user we want to retrieve the total sales of this seller(user)
     * @return an int with the total number of sales
     */
    public static int getTotalSales(User user) {
        Bson match = match(eq("seller_username", user.getUsername()));
        Bson group = group("$seller_username", sum("totalSales", "$quantity_sold"));

        AggregateIterable<Document> aggregation = productsCollection.aggregate(Arrays.asList(match, group));
        Document doc = aggregation.first();

        if (doc == null) return 0;

        return doc.getInteger("totalSales");
    }

    /**
     * Get the most bought product of a target seller
     *
     * @param user we want to get the most bought product of this seller(user)
     * @return the most bought Product instance
     */
    public static Product getBestSellingProduct(User user) {
        Document doc = productsCollection.find(eq("seller_username", user.getUsername())).sort(descending("quantity_sold")).first();

        return documentToProduct(doc);
    }

    public static DailyAnalytics getDailyAnalytics(User user, int day, int month, int year) {
        DailyAnalytics daily;

        Bson firstMatch = match(eq("seller_username", user.getUsername()));
        Bson firstSet = (project(fields(include("seller_username", "user"), computed("year", eq("$year", "$delivery_date")), computed("month", eq("$month", "$delivery_date")), computed("day", eq("$dayOfMonth", "$delivery_date")))));
        Bson secondMatch = match(and(eq("year", year), eq("month", month), eq("day", day)));

        // Get male sales
        BsonArray cond = new BsonArray();
        BsonArray eq = new BsonArray();
        eq.add(new BsonString("$user.gender"));
        eq.add(new BsonString("Male"));
        cond.add(new BsonDocument("$eq", eq));
        cond.add(new BsonInt32(1));
        cond.add(new BsonInt32(0));
        BasicDBObject firstCond = new BasicDBObject("$cond", cond);

        // Get young sales (age <= 35)
        BsonArray cond2 = new BsonArray();
        BsonArray lte = new BsonArray();
        lte.add(new BsonString("$user.age"));
        lte.add(new BsonInt32(35));
        cond2.add(new BsonDocument("$lte", lte));
        cond2.add(new BsonInt32(1));
        cond2.add(new BsonInt32(0));
        BasicDBObject secondCond = new BasicDBObject("$cond", cond2);

        Bson firstGroup = group("$day", sum("all_sales", 1), sum("male_sales", firstCond), sum("young_sales", secondCond));

        AggregateIterable<Document> aggregate = ordersCollection.aggregate(Arrays.asList(firstMatch, firstSet, secondMatch, firstGroup));
        Document doc = aggregate.first();

        if (doc != null) {
            int all_sales = doc.getInteger("all_sales");
            int male_sales = doc.getInteger("male_sales");
            int female_sales = all_sales - male_sales;
            int young_sales = doc.getInteger("young_sales");
            int old_sales = all_sales - young_sales;

            daily = DailyAnalytics.DailyAnalyticsBuilder()
                    .setDay_num(day)
                    .setAll_sales(all_sales)
                    .setMale_sales(male_sales)
                    .setFemale_sales(female_sales)
                    .setYoung_sales(young_sales)
                    .setOld_sales(old_sales).build();
        } else {
            daily = DailyAnalytics.DailyAnalyticsBuilder()
                    .setDay_num(day)
                    .setAll_sales(0)
                    .setMale_sales(0)
                    .setFemale_sales(0)
                    .setYoung_sales(0)
                    .setOld_sales(0).build();
        }

        return daily;
    }

    /**
     * Updates the analytic documents for each seller (To be called once a day)
     */
    public static void updateAnalytics() {
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, -1); // The analytics will refer to yesterday
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int analyticsDay = cal.get(Calendar.DAY_OF_MONTH);
        int analyticsYear = cal.get(Calendar.YEAR);

        FindIterable<Document> documents = usersCollection.find(or(eq("role", "Admin"), eq("role", "Seller")));
        for (Document document : documents) {
            String username = document.getString("username");

            // Calculate all the analytics
            User seller = User.UserBuilder().setUsername(username);
            getReviewsAnalytics(seller);
            int totalReviews = seller.getTotalReviews();
            double avgReviews = seller.getAverageReview();
            int averageDeliveryTime = getAverageDeliveryTime(seller);
            double totalEarnings = getTotalEarnings(seller);
            int totalSales = getTotalSales(seller);
            Product bestSellingProduct = getBestSellingProduct(seller);
            int[] numberOfStars = getNumberOfStars(seller);
            DailyAnalytics dailyAnalytics = getDailyAnalytics(seller, analyticsDay, currentMonth, analyticsYear);

            // Search if the analytics document for this user is already present
            FindIterable<Document> analyticsDocument = analyticsCollection.find(and(eq("seller_username", username), eq("month", currentMonth)));

            Analytics analytics;

            if (analyticsDocument.first() == null) {

                ArrayList<DailyAnalytics> salesByDay = new ArrayList<>();
                for(int i = 1; i<32; i++) {
                    DailyAnalytics empty = DailyAnalytics.DailyAnalyticsBuilder()
                            .setDay_num(i).setAll_sales(0).setFemale_sales(0)
                            .setMale_sales(0).setOld_sales(0).setYoung_sales(0).build();
                    salesByDay.add(empty);
                }

                salesByDay.set(analyticsDay-1, dailyAnalytics);

                analytics = Analytics.AnalyticsBuilder()
                        .setMonth(currentMonth)
                        .setSellerUsername(username)
                        .setTotalReviews(totalReviews)
                        .setAvgReviews(avgReviews)
                        .setAvgDelivery(averageDeliveryTime)
                        .setTotalEarnings(totalEarnings)
                        .setTotalSales(totalSales)
                        .setBestSellingProduct(bestSellingProduct)
                        .setStarsDistribution(numberOfStars)
                        .setSalesByDay(salesByDay).build();

                Document newDoc = convertAnalyticsToDocument(analytics);
                System.out.println("New document: inserted analytics for seller " + username);
                analyticsCollection.insertOne(newDoc);

            } else {
                analytics = convertToAnalytics(analyticsDocument.first());
                ArrayList<DailyAnalytics> salesByDay = analytics.getSalesByDay();
                salesByDay.set(analyticsDay-1, dailyAnalytics);

                analytics.setTotalReviews(totalReviews)
                        .setAvgReviews(avgReviews)
                        .setAvgDelivery(averageDeliveryTime)
                        .setTotalEarnings(totalEarnings)
                        .setTotalSales(totalSales)
                        .setBestSellingProduct(bestSellingProduct)
                        .setStarsDistribution(numberOfStars)
                        .setSalesByDay(salesByDay).build();

                Document newDoc = convertAnalyticsToDocument(analytics);

                System.out.println("Document already present: updated analytics for seller " + username);
                analyticsCollection.replaceOne(eq("_id", new ObjectId(analytics.getObjectId())), newDoc);
            }
        }
    }

    /**
     * Converts a product Bson Document into a Product instance
     *
     * @param productDocument Bson Document describing a product
     * @return an instance of the Product Java Class
     */
    private static Product documentToProduct(Document productDocument) {
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
}
