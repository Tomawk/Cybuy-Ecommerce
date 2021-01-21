package lsmd.group17.cybuy.gui.user.standard;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import lsmd.group17.cybuy.gui.prototypes.GraphicInterface;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.middleware.Utilities;
import lsmd.group17.cybuy.model.Product;

import java.util.ArrayList;

public class CartInterface extends GraphicInterface {

    private final AnchorPane containerBox,cartBox;
    private final StackPane home_btn,checkout_btn,search_btn;
    private final TextField searchBar;
    private final Label register,user_label;
    private final ScrollPane scrollpane;

    private Label total_amount, temporary_amount;
    private boolean emptyCart = true;
    private double temporaryAmount = 0;
    private double totalAmount = 0;

    private final EventHandler eventHandler;

    private ArrayList<Product> productArrayList = new ArrayList<>();

    public CartInterface(Session s) {
        super(s);

        containerBox = new AnchorPane();
        searchBar = new TextField();
        register = new Label("Register");
        user_label = new Label();
        user_label.setText(session.getUserLogged().getUsername());
        home_btn = new StackPane();
        checkout_btn = new StackPane();
        search_btn = new StackPane();
        cartBox = new AnchorPane();
        scrollpane = new ScrollPane();

        total_amount = new Label();
        temporary_amount = new Label();

        cartBox.getChildren().add(scrollpane);

        containerBox.getChildren().addAll(searchBar,register,user_label,home_btn,scrollpane);

        //root element is an element of the GraphicsInterface parent class
        root = containerBox;
        root.getStylesheets().add("file:./AddOn/CSS/cartInterface.css");

        //Click label register event
        register.setOnMouseClicked(event -> s.getRegisterInterface().setScene());
        //Click label login event
        user_label.setOnMouseClicked(event -> s.getUserInterface().setScene());
        //Click home btn event
        home_btn.setOnMouseClicked(event -> {
            s.setKeyword("");
            s.getPrimaryInterface().setScene();
        });

        eventHandler = EventHandler.getInstance();
    }

    public void initialize(){
        //Navbar
        Rectangle navbar = new Rectangle(0,0,1200,50);
        navbar.setFill(Color.web("#d0d0d0"));
        containerBox.getChildren().add(navbar);
        navbar.toBack();

        //Logo
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
        searchBar.setFocusTraversable(false);
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

        //Register | User
        register.getStyleClass().add("nav_signin");
        register.setLayoutX(850); register.setLayoutY(13);
        Label separator = new Label("|");
        separator.getStyleClass().add("nav_signin_separator");
        separator.setLayoutX(912); separator.setLayoutY(13);
        user_label.getStyleClass().add("nav_signin");
        user_label.setLayoutX(920); user_label.setLayoutY(13);
        user_label.setWrapText(true);
        user_label.setPrefWidth(100);
        user_label.setPrefHeight(20);
        containerBox.getChildren().add(separator);

        //User Icon
        ImageView user_icon = new ImageView("file:/../AddOn/Images/user.png");
        user_icon.setLayoutX(818); user_icon.setLayoutY(16);
        user_icon.setPreserveRatio(true);
        user_icon.setFitWidth(20);
        user_icon.setFitHeight(20);
        containerBox.getChildren().add(user_icon);

        //Home btn
        ImageView home_icon = new ImageView("file:/../AddOn/Images/home_icon.png");
        Rectangle cart_rect = new Rectangle(1072,0,50,50);

        home_btn.setLayoutX(1060); home_btn.setLayoutY(0);
        home_btn.getStyleClass().add("home_btn");
        cart_rect.setFill(Color.TRANSPARENT);
        home_icon.setPreserveRatio(true);
        home_icon.setFitWidth(35);
        home_icon.setFitHeight(35);
        home_btn.getChildren().addAll(cart_rect, home_icon);

        //Main label
        Label main_label = new Label("Shopping Cart");
        main_label.getStyleClass().add("main_label");
        main_label.setLayoutX(475); main_label.setLayoutY(70);
        containerBox.getChildren().add(main_label);

        //setting the cart
        scrollpane.setLayoutX(50); scrollpane.setLayoutY(140);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(true);
        scrollpane.setPrefViewportWidth(810);
        scrollpane.setPrefViewportHeight(560);
        setCart();

        //Recap order
        AnchorPane recapBox = new AnchorPane();
        recapBox.setLayoutX(920); recapBox.setLayoutY(145);
        Rectangle recap_rect = new Rectangle(0,0,230,310);
        recap_rect.setStyle("-fx-fill: #f4f4f4; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
        Label recap_label = new Label("Recap Order");
        recap_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px");
        recap_label.setLayoutX(65); recap_label.setLayoutY(20);

        temporary_amount.setStyle("-fx-font-size: 14px;");
        getTemporaryAmount();
        temporary_amount.setLayoutX(10); temporary_amount.setLayoutY(65);
        Label shipping = new Label("Shipping                          Free");
        shipping.setStyle("-fx-font-size: 14px;");
        shipping.setLayoutX(10); shipping.setLayoutY(95);

        Separator hor_separator = new Separator();
        hor_separator.setLayoutX(10); hor_separator.setLayoutY(135);
        hor_separator.setOrientation(Orientation.HORIZONTAL);
        hor_separator.setPrefWidth(202);

        getTotalAmount();
        total_amount.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        total_amount.setLayoutX(10); total_amount.setLayoutY(150);

        //Checkout btn
        initializeCheckoutButton();

        recapBox.getChildren().addAll(recap_rect,recap_label,temporary_amount,shipping,hor_separator,total_amount,checkout_btn);
        containerBox.getChildren().add(recapBox);

        //Click on the search_btn
        search_btn.setOnMouseClicked(event -> searchProducts());

        // Press enter in the search bar
        searchBar.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                searchProducts();
            }
        });

    }

    private void initializeCheckoutButton() {
        checkout_btn.setLayoutX(14); checkout_btn.setLayoutY(230);
        checkout_btn.getStyleClass().add("checkout_btn");
        Rectangle checkout_rect = new Rectangle(0,0,200,50);
        Label checkout_label = new Label("CHECKOUT");
        checkout_label.setAlignment(Pos.CENTER);

        if(emptyCart) {
            checkout_rect.styleProperty().bind(Bindings.when(checkout_btn.hoverProperty())
                    .then("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;")
                    .otherwise("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;"));
            checkout_label.setStyle("-fx-font-weight: normal; -fx-font-size: 16px; -fx-font-color: grey;");

            //Checkout btn event
            checkout_btn.setOnMouseClicked(null);

        } else {
            checkout_rect.styleProperty().bind(Bindings.when(checkout_btn.hoverProperty())
                    .then("-fx-fill: #e5e5e5; -fx-stroke: #cccccc; -fx-stroke-width: 2; -fx-cursor: hand;")
                    .otherwise("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;"));
            checkout_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            //Checkout btn event
            checkout_btn.setOnMouseClicked(this::CheckoutAction);
        }

        checkout_btn.getChildren().addAll(checkout_rect,checkout_label);
    }

    private void searchProducts() {
        session.setKeyword(searchBar.getText());
        session.getPrimaryInterface().setScene();
    }


    public void setCart() {
        cartBox.getChildren().clear();
        temporaryAmount = 0;
        totalAmount = 0;
        int coordinate_y = 10;
        int coordinate_x = 10;

        productArrayList = eventHandler.getProductsInCart(session.getUserLogged());

        if (productArrayList.size() == 0) {
            emptyCart = true;
            emptyCartMessage();
        } else {
            emptyCart = false;
            for (Product product : productArrayList) {
                AnchorPane articleBox = new AnchorPane();
                articleBox.setLayoutX(coordinate_x);
                articleBox.setLayoutY(coordinate_y);
                Rectangle article_rect = new Rectangle(0, 0, 790, 150);
                article_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");

                //ImageBox
                StackPane imageBox = new StackPane();
                imageBox.setLayoutX(25);
                imageBox.setLayoutY(25);
                Rectangle image_rect = new Rectangle(0, 0, 101, 101);
                image_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
                ImageView image = new ImageView(Utilities.correctImage(product));
                image.setFitWidth(100);
                image.setFitHeight(100);
                image.setPreserveRatio(true);
                imageBox.getChildren().addAll(image_rect, image);

                //Description
                String description = product.getDescription();
                Label article_description = new Label(description);
                article_description.getStyleClass().add("description");
                article_description.setWrapText(true);
                article_description.setPrefWidth(520);
                article_description.setPrefHeight(50);
                article_description.setLayoutX(150);
                article_description.setLayoutY(40);

                //Price
                String priceString = String.format("%.2f", product.getPrice());
                Label price = new Label("$"+ priceString);
                price.getStyleClass().add("price");
                price.setLayoutX(700);
                price.setLayoutY(115);

                //Buttons
                StackPane bt1_low = new StackPane();
                bt1_low.setLayoutX(675);
                bt1_low.setLayoutY(40);
                Rectangle bt1_low_rect = new Rectangle(0, 0, 25, 25);
                bt1_low_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
                Label bt1_low_label = new Label("-");
                bt1_low_label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                bt1_low.getStyleClass().add("quantity_btn");
                bt1_low.getChildren().addAll(bt1_low_rect, bt1_low_label);

                StackPane bt2_show = new StackPane();
                bt2_show.setLayoutX(700);
                bt2_show.setLayoutY(40);
                Rectangle bt2_show_rect = new Rectangle(0, 0, 25, 25);
                bt2_show_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
                Label bt2_show_label = new Label("" + product.getQuantityAvailable());
                bt2_show_label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
                bt2_show.getChildren().addAll(bt2_show_rect, bt2_show_label);

                StackPane bt3_up = new StackPane();
                bt3_up.setLayoutX(725);
                bt3_up.setLayoutY(40);
                Rectangle bt3_up_rect = new Rectangle(0, 0, 25, 25);
                bt3_up_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
                Label bt3_up_label = new Label("+");
                bt3_up_label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                bt3_up.getStyleClass().add("quantity_btn");
                bt3_up.getChildren().addAll(bt3_up_rect, bt3_up_label);

                //Quantity
                int quantity = product.getQuantityAvailable();
                Label label_under_quantity = new Label();
                if(quantity == 1) {
                    label_under_quantity.setText("(Note, 1 piece)");
                } else {
                    label_under_quantity.setText("(Note, "+ quantity +" pieces)");
                }

                label_under_quantity.setLayoutX(675);
                label_under_quantity.setLayoutY(70);

                double totalPrice = product.getPrice()*quantity;

                temporaryAmount = temporaryAmount + totalPrice;
                totalAmount = totalAmount + totalPrice;

                //recycle bin
                StackPane bin_btn = new StackPane();
                bin_btn.getStyleClass().add("bin_btn");
                bin_btn.setLayoutX(160);
                bin_btn.setLayoutY(95);
                Rectangle bin_rect = new Rectangle(0, 0, 30, 30);
                bin_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
                ImageView image_bin = new ImageView("file:/../AddOn/Images/trash_bin1.png");
                image_bin.setFitHeight(30);
                image_bin.setFitWidth(30);
                bin_btn.getChildren().addAll(bin_rect, image_bin);

                //Click event on the trash_bin
                bin_btn.setOnMouseClicked(event -> removeSelectedArticle(product));

                //click events on the quantity btns
                bt1_low.setOnMouseClicked(event -> decreaseQuantity(product));
                bt3_up.setOnMouseClicked(event -> increaseQuantity(product));


                articleBox.getChildren().addAll(article_rect, imageBox, article_description, price, bt1_low, bt2_show, bt3_up, label_under_quantity, bin_btn);
                cartBox.getChildren().add(articleBox);

                coordinate_y += 160;
                }
            }

        scrollpane.setContent(cartBox);
    }

    private void emptyCartMessage() {
        // Label
        Label empty_cart = new Label("Your cart is empty!");
        empty_cart.setStyle("-fx-text-fill: black; -fx-font-size: 26px; -fx-font-weight: bold");
        empty_cart.setLayoutX(300); empty_cart.setLayoutY(60);

        //Img
        ImageView empty_cart_img = new ImageView("file:/../AddOn/Images/empty_cart.png");
        empty_cart_img.setLayoutY(120); empty_cart_img.setLayoutX(180);
        empty_cart_img.setFitWidth(405); empty_cart_img.setFitHeight(280);
        empty_cart_img.setPreserveRatio(true);

        // Start shopping button
        StackPane start_shopping_btn = new StackPane();
        start_shopping_btn.setLayoutX(310); start_shopping_btn.setLayoutY(450);
        Rectangle shopping_rect = new Rectangle(0,0,200,50);
        shopping_rect.styleProperty().bind(Bindings.when(start_shopping_btn.hoverProperty())
                .then("-fx-fill: #e5e5e5; -fx-stroke: #cccccc; -fx-stroke-width: 2; -fx-cursor: hand;")
                .otherwise("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;"));
        Label shopping_label = new Label("START SHOPPING");
        start_shopping_btn.getChildren().addAll(shopping_rect,shopping_label);

        cartBox.getChildren().addAll(empty_cart_img, empty_cart, start_shopping_btn);

        //Click start shopping btn
        start_shopping_btn.setOnMouseClicked(event -> {
            session.setKeyword("");
            session.getPrimaryInterface().setScene();
        });
    }

    private void getTemporaryAmount(){
        String temporary = String.format("%.2f", temporaryAmount);
        temporary_amount.setText("Temporary amount         $" + temporary);
    }

    private void getTotalAmount(){
        String total = String.format("%.2f", totalAmount);
        total_amount.setText("Total amount             $" + total);
    }

    private void increaseQuantity(Product product){
        eventHandler.addProductToCart(session.getUserLogged(), product);
        setCart();
        getTemporaryAmount();
        getTotalAmount();
        initializeCheckoutButton();
    }

    private void decreaseQuantity(Product product){
        eventHandler.decreaseProductQuantity(session.getUserLogged(), product);
        setCart();
        getTemporaryAmount();
        getTotalAmount();
        initializeCheckoutButton();
    }

    private void removeSelectedArticle(Product product) {
        if(eventHandler.removeProductFromCart(session.getUserLogged(), product)) {
            setCart();
            getTemporaryAmount();
            getTotalAmount();
            initializeCheckoutButton();
        }
    }

    private void CheckoutAction(MouseEvent event){
        if(eventHandler.placeOrder(session.getUserLogged(), productArrayList)) {
            if(!eventHandler.flushCart(session.getUserLogged())) {
                //TODO dai errore
            }
        } else {
            // TODO DAI ERRORE
        }
        setCart();
        getTemporaryAmount();
        getTotalAmount();
        initializeCheckoutButton();
    }

    public AnchorPane getContainerBox() { return containerBox; }
    public StackPane getHome_btn() { return home_btn; }
    public Label getRegister() { return register; }
    public Label getUser_label() { return user_label; }
}
