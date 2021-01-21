package lsmd.group17.cybuy.gui.user.seller.admin;

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
import lsmd.group17.cybuy.model.User;

import java.util.ArrayList;

public class AdminPanelInterface extends GraphicInterface {
    private final AnchorPane containerBox, bestBox, worstBox;
    private final StackPane home_btn,search_btn, search_user_btn;
    private final Label register,user_label;
    private final TextField searchBar, searchUsersBar;
    private final ScrollPane bestScrollPane, worstScrollPane;

    private final EventHandler eventHandler;

    private String username = "";

    public AdminPanelInterface(Session s) {
        super(s);

        eventHandler = EventHandler.getInstance();

        bestScrollPane = new ScrollPane();
        worstScrollPane = new ScrollPane();

        containerBox = new AnchorPane();

        bestBox = new AnchorPane();
        worstBox = new AnchorPane();

        searchBar = new TextField();
        searchBar.setPromptText("Search...");
        searchUsersBar = new TextField();
        searchUsersBar.setPromptText("Enter a username...");

        home_btn = new StackPane();
        search_btn = new StackPane();
        search_user_btn = new StackPane();
        register = new Label("Register");
        user_label = new Label(session.getUserLogged().getUsername());

        containerBox.getChildren().addAll(searchBar, searchUsersBar, register,user_label,home_btn, bestScrollPane, worstScrollPane);

        //root element is an element of the GraphicsInterface parent class
        root = containerBox;
        root.getStylesheets().add("file:./AddOn/CSS/adminInterface.css");

        //Click label register event
        register.setOnMouseClicked(event -> s.getRegisterInterface().setScene());
        //Click label login event
        user_label.setOnMouseClicked(event -> s.getUserInterface().setScene());
        //Click home btn event
        home_btn.setOnMouseClicked(event -> {
            s.setKeyword("");
            s.getPrimaryInterface().setScene();
        });

        //Click on the search_btn
        search_btn.setOnMouseClicked(event -> searchProducts());

        // Press enter in the search bar
        searchBar.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                searchProducts();
            }
        });
    }

    public void initialize() {
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

        //Search Sellers Bar
        searchUsersBar.getStyleClass().add("search-bar");
        searchUsersBar.setStyle("-fx-border-color: #d0d0d0");
        searchUsersBar.setLayoutX(50); searchUsersBar.setLayoutY(80);
        searchUsersBar.setPrefWidth(375);

        // Search Sellers Button
        search_user_btn.setLayoutX(450); search_user_btn.setLayoutY(79);

        Rectangle search_user_btn_rect = new Rectangle(0,0,130,40);
        Label search_user_btn_label = new Label("SEARCH");
        search_user_btn_label.setAlignment(Pos.CENTER);

        search_user_btn_rect.styleProperty().bind(Bindings.when(search_user_btn.hoverProperty())
                    .then("-fx-fill: #e5e5e5; -fx-stroke: #cccccc; -fx-cursor: hand;")
                    .otherwise("-fx-fill: white; -fx-stroke: #d0d0d0; "));
        search_user_btn_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        search_user_btn.getChildren().addAll(search_user_btn_rect,search_user_btn_label);
        containerBox.getChildren().add(search_user_btn);

        //Checkout btn event
        search_user_btn.setOnMouseClicked(this::searchSellers);

        // Labels
        Label best_label = new Label("Top Sellers:");
        best_label.getStyleClass().add("main_label");
        best_label.setLayoutX(50); best_label.setLayoutY(140);

        Label worst_label = new Label("Worst Sellers:");
        worst_label.getStyleClass().add("main_label");
        worst_label.setLayoutX(615); worst_label.setLayoutY(140);

        containerBox.getChildren().addAll(best_label, worst_label);

        // ScrollPane
        bestScrollPane.setLayoutX(50); bestScrollPane.setLayoutY(180);
        bestScrollPane.setFitToHeight(true); bestScrollPane.setFitToWidth(true);
        bestScrollPane.setPrefViewportWidth(515); bestScrollPane.setPrefViewportHeight(520);

        worstScrollPane.setLayoutX(615); worstScrollPane.setLayoutY(180);
        worstScrollPane.setFitToHeight(true); worstScrollPane.setFitToWidth(true);
        worstScrollPane.setPrefViewportWidth(515); worstScrollPane.setPrefViewportHeight(520);

        // Fill in ScrollPanes
        setBestScrollPane();
        setWorstScrollPane();
    }

    private void searchSellers(MouseEvent mouseEvent) {
        username = searchUsersBar.getText();
        setBestScrollPane();
        setWorstScrollPane();
    }

    private void setWorstScrollPane() {
        AnchorPane box = setScrollPane(worstBox, 1);
        worstScrollPane.setContent(box);
    }

    private void setBestScrollPane() {
        AnchorPane box = setScrollPane(bestBox, -1);
        bestScrollPane.setContent(box);
    }

    private AnchorPane setScrollPane(AnchorPane anchorPane, int sort) {
        anchorPane.getChildren().clear();
        ArrayList<User> sellers = eventHandler.getSellersRanking(username, sort);

        int coordinate_y = 10;
        int coordinate_x = 10;

        for(User seller : sellers) {
            AnchorPane userBox = new AnchorPane();
            userBox.setLayoutX(coordinate_x); userBox.setLayoutY(coordinate_y);
            Rectangle user_rect = new Rectangle(0,0, 495,110);
            user_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");

            //Username
            Label seller_username = new Label(seller.getUsername());
            seller_username.getStyleClass().add("username");
            seller_username.setLayoutX(20); seller_username.setLayoutY(20);

            // Average Review
            String average = String.format("%.2f", seller.getAverageReview());
            Label average_review = new Label("Average Review: " + average);
            average_review.getStyleClass().add("details");
            average_review.setLayoutX(20); average_review.setLayoutY(50);

            // Total reviews
            Label total_reviews = new Label("Total Reviews: " + seller.getTotalReviews());
            total_reviews.getStyleClass().add("details");
            total_reviews.setLayoutX(20); total_reviews.setLayoutY(70);

            // Delete user btn
            StackPane delete_user_btn = new StackPane();
            delete_user_btn.setLayoutX(340); delete_user_btn.setLayoutY(45);
            delete_user_btn.getStyleClass().add("add_cart_btn");
            Rectangle delete_user_rect = new Rectangle(0,0,130,40);
            delete_user_rect.styleProperty().bind(Bindings.when(delete_user_btn.hoverProperty())
                    .then("-fx-fill: #e5e5e5; -fx-stroke: #cccccc; -fx-stroke-width: 2;")
                    .otherwise("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;"));
            Label delete_user_label = new Label("DELETE USER");
            delete_user_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            delete_user_label.setAlignment(Pos.CENTER);
            delete_user_btn.getChildren().addAll(delete_user_rect,delete_user_label);

            //Click event on delete user btn
           // delete_user_btn.setOnMouseClicked(event -> cancella user);

            userBox.getChildren().addAll(user_rect,seller_username,average_review, total_reviews, delete_user_btn);
            anchorPane.getChildren().add(userBox);

            coordinate_y += 120;
        }
        return anchorPane;
    }


    private void searchProducts() {
        session.setKeyword(searchBar.getText());
        session.getPrimaryInterface().setScene();
    }
}
