package lsmd.group17.cybuy.gui.user.standard;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lsmd.group17.cybuy.gui.prototypes.GraphicInterface;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.middleware.Utilities;
import lsmd.group17.cybuy.model.Product;
import lsmd.group17.cybuy.model.User;

import java.util.ArrayList;


public class WishlistInterface extends GraphicInterface {

    private final AnchorPane containerBox,wishlistBox;
    private final StackPane cart_btn, flush_btn, search_btn;
    private final ScrollPane scrollpane;
    private final TextField searchBar;
    private final Label register,user;
    private final EventHandler eventHandler;

    //wishlist infos
    private int num_product_wishlist;
    private ArrayList<Product> products_infos;


    public WishlistInterface(Session s){
        super(s);
        containerBox = new AnchorPane();
        wishlistBox = new AnchorPane();
        cart_btn = new StackPane();
        flush_btn = new StackPane();
        search_btn = new StackPane();
        scrollpane = new ScrollPane();
        searchBar = new TextField();
        register = new Label("Register");
        user = new Label("");

        eventHandler = EventHandler.getInstance();

        products_infos = new ArrayList<>();

        /* MANUAL USER LOGGING FOR DEBUG */
      /*  User test_user = new User("1","Paolo","Neri","PaolinoXx","paolo@xxxx.xxx",
                "123","Male", "Italy",23,"User",null);*/
        User test_user = session.getUserLogged();

        /* DEBUG PRODUCTS */
/*

*/
        /* MANUAL INSERT FOR DEBUG PURPOSE */
/*
        eventHandler.WishlistAdd(test_user,test_product_1);
        eventHandler.WishlistAdd(test_user,test_product_2);
        eventHandler.WishlistAdd(test_user,test_product_3);
*/
//        eventHandler.printLevelDbDatabase();

        //get all the ids of  the products that are in the userLogged wishlist
        products_infos = eventHandler.getProductsInWishlist(session.getUserLogged());

        num_product_wishlist = products_infos.size(); //# of products to be displayed in the wishlist

        user.setText(session.getUserLogged().getUsername()); //set the username from the loggedUser in the main_label

        containerBox.getChildren().addAll(searchBar,register,user,cart_btn,scrollpane);

        //root element is an element of the GraphicsInterface parent class
        root = containerBox;
        root.getStylesheets().add("file:./AddOn/CSS/wishlistInterface.css");


        //##### CLICK EVENTS #####

        //Click on the flush btn
        flush_btn.setOnMouseClicked(event -> RemoveAllProducts());
        //Click label home event
        register.setOnMouseClicked(event -> session.getRegisterInterface().setScene());
        //Click user label event
        user.setOnMouseClicked(event -> session.getUserInterface().setScene());
        //Click cart btn event
        cart_btn.setOnMouseClicked(event -> session.getCartInterface().setScene());
        //Click on the search_btn
        search_btn.setOnMouseClicked(event -> searchProducts());
        //Press Enter in the search bar
        searchBar.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                searchProducts();
            }
        });


    }

    public void initialize(){

        //Navbar
        Rectangle navbar = new Rectangle(0,0,1200,50);
        navbar.setFill(Color.web("#d0d0d0"));
        containerBox.getChildren().add(navbar);
        navbar.toBack();

        //LOGO
        ImageView logo = new ImageView("file:/../AddOn/Images/cybuy_logo.png");
        logo.setLayoutX(30); logo.setLayoutY(5);
        logo.setPreserveRatio(true);
        logo.setFitWidth(50);
        logo.setFitHeight(50);
        containerBox.getChildren().add(logo);

        //Logo text
        ImageView logo_text = new ImageView("file:/../AddOn/Images/cybuy_text.png");
        logo_text.setLayoutX(65); logo_text.setLayoutY(8);
        logo_text.setPreserveRatio(true);
        logo_text.setFitWidth(120);
        logo_text.setFitHeight(60);
        containerBox.getChildren().add(logo_text);

        //SearchBar
        searchBar.setPromptText("Search...");
        searchBar.getStyleClass().add("search-bar");
        searchBar.setLayoutX(200); searchBar.setLayoutY(5);
        searchBar.setPrefWidth(500);

        //Search Icon
        search_btn.setLayoutX(630); search_btn.setLayoutY(8);
        search_btn.getStyleClass().add("search_btn");
        Rectangle search_btn_rect = new Rectangle(0,0,30,30);
        search_btn_rect.setStyle("-fx-fill: white; -fx-stroke: #bfbfbf; -fx-stroke-width: 1;");
        ImageView search_icon = new ImageView("file:/../AddOn/Images/search-icon.jpg");
        search_icon.setPreserveRatio(true);
        search_icon.setFitWidth(25);
        search_icon.setFitHeight(25);
        search_btn.getChildren().addAll(search_btn_rect,search_icon);
        containerBox.getChildren().add(search_btn);

        //Register | Login
        register.getStyleClass().add("nav_signin");
        register.setLayoutX(850); register.setLayoutY(13);
        Label separator = new Label ("|");
        separator.getStyleClass().add("nav_signin_separator");
        separator.setLayoutX(912); separator.setLayoutY(13);
        user.getStyleClass().add("nav_signin");
        user.setLayoutX(920); user.setLayoutY(13);
        user.setWrapText(true);
        user.setPrefWidth(100);
        user.setPrefHeight(20);
        containerBox.getChildren().add(separator);

        //User Icon
        ImageView user_icon = new ImageView("file:/../AddOn/Images/user.png");
        user_icon.setLayoutX(818); user_icon.setLayoutY(16);
        user_icon.setPreserveRatio(true);
        user_icon.setFitWidth(20);
        user_icon.setFitHeight(20);
        containerBox.getChildren().add(user_icon);

        //Cart Icon

        ImageView cart_icon = new ImageView("file:/../AddOn/Images/cart_icon.png");
        Rectangle cart_rect = new Rectangle(1072,0,50,50);

        cart_btn.setLayoutX(1060); cart_btn.setLayoutY(0);
        cart_btn.getStyleClass().add("cart_btn");
        cart_rect.setFill(Color.TRANSPARENT);
        cart_icon.setPreserveRatio(true);
        cart_icon.setFitWidth(35);
        cart_icon.setFitHeight(35);
        cart_btn.getChildren().addAll(cart_rect, cart_icon);

        //Main Label
        StackPane main_label_box = new StackPane();
        main_label_box.setLayoutY(75);
        Rectangle main_label_rect = new Rectangle(0,0,1200,50);
        main_label_rect.setFill(Color.TRANSPARENT);
        //main_label_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
        Label main_label = new Label ("");
        String username = session.getUserLogged().getUsername();
        String label = username +"'s Wishlist";
        main_label.setText(label);
        main_label.setStyle("-fx-font-weight: bold; -fx-font-size: 22px;");
        main_label_box.getChildren().addAll(main_label_rect,main_label);
        containerBox.getChildren().add(main_label_box);

        //Flush btn
        flush_btn.setLayoutX(875); flush_btn.setLayoutY(75);
        flush_btn.getStyleClass().add("flush_btn");
        Rectangle flush_rect = new Rectangle(0,0,200,50);
        flush_rect.setStyle("-fx-fill: #dfdfdf; -fx-stroke: #858585; -fx-stroke-width: 2;");
        Label flush_label = new Label ("Remove all");
        flush_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        flush_btn.getChildren().addAll(flush_rect,flush_label);
        containerBox.getChildren().add(flush_btn);

        //Scrollpane

        scrollpane.setLayoutX(50); scrollpane.setLayoutY(140);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(true);
        scrollpane.setPrefViewportWidth(1080);
        scrollpane.setPrefViewportHeight(560);
        setWishlist();
    }

    private void setWishlist(){

            //Get number of elements to show
            int num_products = num_product_wishlist;

            if(num_products == 0){
                AnchorPane emptyBox = new AnchorPane();
                Rectangle empty_box_rect = new Rectangle(0,0,1094,574);
                empty_box_rect.setStyle("-fx-fill: white; -fx-stroke: white; -fx-stroke-width: 2;");
                ImageView empty_wishlist_img = new ImageView("file:/../AddOn/Images/wishlist.png");
                empty_wishlist_img.setLayoutX(485); empty_wishlist_img.setLayoutY(185);
                empty_wishlist_img.setPreserveRatio(true);
                empty_wishlist_img.setFitWidth(150);
                empty_wishlist_img.setFitHeight(150);
                Label empty_label = new Label("Your Wishlist is empty!");
                empty_label.setStyle("-fx-font-weight: bold; -fx-font-size: 32px;");
                empty_label.setLayoutX(390); empty_label.setLayoutY(120);
                StackPane start_shopping_btn = new StackPane();
                start_shopping_btn.getStyleClass().add("start_shopping_btn");
                start_shopping_btn.setLayoutX(457); start_shopping_btn.setLayoutY(370);
                Rectangle shopping_rect = new Rectangle(0,0,200,50);
                shopping_rect.styleProperty().bind(Bindings.when(start_shopping_btn.hoverProperty())
                        .then("-fx-fill: #e5e5e5; -fx-stroke: #cccccc; -fx-stroke-width: 2;")
                        .otherwise("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;"));
                Label shopping_label = new Label("START SHOPPING");
                start_shopping_btn.getChildren().addAll(shopping_rect,shopping_label);
                emptyBox.getChildren().addAll(empty_box_rect,empty_wishlist_img,empty_label,start_shopping_btn);
                wishlistBox.getChildren().add(emptyBox);

                //Click start shopping btn
                start_shopping_btn.setOnMouseClicked(event -> {
                    session.setKeyword("");
                    session.getPrimaryInterface().setScene();
                });
            }

            int coordinate_y = 10;
            int coordinate_x = 10;

            for(int i=0;i<num_products;i++){
                AnchorPane articleBox = new AnchorPane();
                articleBox.setLayoutX(coordinate_x); articleBox.setLayoutY(coordinate_y);
                Rectangle article_rect = new Rectangle(0,0, 1060,150);
                article_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");

                //ImageBox
                StackPane imageBox = new StackPane();
                imageBox.setLayoutX(25);  imageBox.setLayoutY(25);
                Rectangle image_rect = new Rectangle(0,0,101,101);
                image_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
                // String image_url = products_infos.get(i).getImage();
                ImageView image_view = new ImageView(Utilities.correctImage(products_infos.get(i)));
                image_view.setFitWidth(100); image_view.setFitHeight(100);
                image_view.setPreserveRatio(true);
                imageBox.getChildren().addAll(image_rect,image_view);

                //Description
                String description = products_infos.get(i).getDescription();
                String final_string;
                if(description.length() > 71) {
                    String substring = description.substring(0, 71);
                    final_string = substring + " ...";
                } else final_string = description;
                String outputString = capitalizeString(final_string);
                Label article_description = new Label(outputString);
                article_description.getStyleClass().add("description");
                article_description.setLayoutX(150); article_description.setLayoutY(40);

                //Price

                Label price = new Label();
                String priceString = String.format("%.2f", products_infos.get(i).getPrice());
                price.setText("$" + priceString);
                price.getStyleClass().add("price");
                price.setLayoutX(950); price.setLayoutY(115);

                //recycle bin

                StackPane bin_btn = new StackPane();
                bin_btn.getStyleClass().add("bin_btn");
                bin_btn.setLayoutX(160); bin_btn.setLayoutY(95);
                Rectangle bin_rect = new Rectangle(0,0,30,30);
                bin_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
                ImageView image_bin = new ImageView("file:/../AddOn/Images/trash_bin1.png");
                image_bin.setFitHeight(30); image_bin.setFitWidth(30);
                bin_btn.getChildren().addAll(bin_rect,image_bin);

                //Add to cart btn

                StackPane add_cart_btn = new StackPane();
                add_cart_btn.setLayoutX(800); add_cart_btn.setLayoutY(40);
                add_cart_btn.getStyleClass().add("add_cart_btn");
                Rectangle add_cart_rect = new Rectangle(0,0,220,50);
                add_cart_rect.styleProperty().bind(Bindings.when(add_cart_btn.hoverProperty())
                        .then("-fx-fill: #e5e5e5; -fx-stroke: #cccccc; -fx-stroke-width: 2;")
                        .otherwise("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;"));
                Label add_cart_label = new Label("ADD TO CART");
                add_cart_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                add_cart_label.setAlignment(Pos.CENTER);
                add_cart_btn.getChildren().addAll(add_cart_rect,add_cart_label);

                Product this_product = products_infos.get(i);

                //Click event on the trash_bin
                bin_btn.setOnMouseClicked(event -> removeSelectedArticle(event,this_product)); //TODO click pulsante elimina

                //Click event on add to cart btn
                add_cart_btn.setOnMouseClicked(event -> addSelectedArticleToCart(event,this_product));

                articleBox.getChildren().addAll(article_rect,imageBox,article_description,price,bin_btn,add_cart_btn);
                wishlistBox.getChildren().add(articleBox);

                coordinate_y += 160;
            }

            scrollpane.setContent(wishlistBox);


    }

    private void RemoveAllProducts(){
        if(eventHandler.flushWishlist(session.getUserLogged()))
            session.getWishlistInterface().setScene();
    }

    private void removeSelectedArticle(MouseEvent event, Product product){
        if(eventHandler.removeProductFromWishlist(session.getUserLogged(),product))
            session.getWishlistInterface().setScene();
    }

    private void addSelectedArticleToCart(MouseEvent event, Product product){
        eventHandler.removeProductFromWishlist(session.getUserLogged(),product);
        eventHandler.addProductToCart(session.getUserLogged(), product);
        session.getWishlistInterface().setScene();
    }

    private void searchProducts(){
        session.setKeyword(searchBar.getText());
        session.getPrimaryInterface().setScene();
    }

        //utils

        public String capitalizeString(String string) {
            char[] chars = string.toLowerCase().toCharArray();
            boolean found = false;
            for (int i = 0; i < chars.length; i++) {
                if (!found && Character.isLetter(chars[i])) {
                    chars[i] = Character.toUpperCase(chars[i]);
                    found = true;
                } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                    found = false;
                }
            }
            return String.valueOf(chars);
        }

    public AnchorPane getContainerBox() { return containerBox; }
}
