package lsmd.group17.cybuy.gui.user.seller;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lsmd.group17.cybuy.gui.prototypes.GraphicInterface;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static lsmd.group17.cybuy.middleware.Utilities.getSelectedCategory;
import static lsmd.group17.cybuy.middleware.Utilities.getSelectedPlatform;

public class AddProductInterface extends GraphicInterface {

    private final AnchorPane containerBox, product_form;
    private final StackPane search_btn, addProduct_btn,home_btn;
    private final TextField searchBar;
    private final Label register,login;
    private Menu category_menu;
    private TextArea description_input;
    private final ScrollPane scrollPane;
    private HashMap<TextField, TextField> detailMap;
    private final EventHandler eventHandler;

    private ArrayList<TextField> inputs; //(0) image url | (1) price | (2) quantity

    public AddProductInterface(Session s){
        super(s);

        containerBox = new AnchorPane();
        product_form = new AnchorPane();
        search_btn = new StackPane();
        addProduct_btn = new StackPane();
        home_btn = new StackPane();
        searchBar = new TextField();
        register = new Label("Register");
        login = new Label();
        scrollPane = new ScrollPane();

        inputs = new ArrayList<>();
        detailMap = new HashMap<>();
        description_input = new TextArea();

        eventHandler = EventHandler.getInstance();

        /*DEBUG*/ //TODO TOGLIERE
        /*User test_user = new User("1","Paolo","Neri","PaolinoXx","paolo@xxxx.xxx",
                "123","Male", "Italy",23,"User",null, null);
        session.setUserLogged(test_user);*/

        if(session.getUserLogged() == null){
            login.setText("Login");
        }else{
            login.setText(session.getUserLogged().getUsername());
        }

        category_menu = new Menu("Category:                                             ");

        containerBox.getChildren().addAll(searchBar,register,login,product_form,home_btn);

        root = containerBox;
        root.getStylesheets().add("file:./AddOn/CSS/addProductInterface.css");

        //Click home btn event
        home_btn.setOnMouseClicked(event -> {
            session.setKeyword("");
            session.getPrimaryInterface().setScene();
        });
        //Click register label event
        register.setOnMouseClicked(event -> session.getRegisterInterface().setScene());
        //Click login label event
        login.setOnMouseClicked(event -> session.getUserInterface().setScene());
        //Add Product btn click event
        addProduct_btn.setOnMouseClicked(event -> addProductClick());

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

        //Home | Login
        register.getStyleClass().add("nav_signin");
        register.setLayoutX(850); register.setLayoutY(13);
        Label separator = new Label("|");
        separator.getStyleClass().add("nav_signin_separator");
        separator.setLayoutX(912); separator.setLayoutY(13);
        login.getStyleClass().add("nav_signin");
        login.setLayoutX(920); login.setLayoutY(13);
        login.setWrapText(true);
        login.setPrefWidth(100);
        login.setPrefHeight(20);
        containerBox.getChildren().add(separator);

        // User Icon
        ImageView user_icon = new ImageView("file:/../AddOn/Images/user.png");
        user_icon.setLayoutX(818); user_icon.setLayoutY(16);
        user_icon.setPreserveRatio(true);
        user_icon.setFitWidth(20);
        user_icon.setFitHeight(20);
        containerBox.getChildren().add(user_icon);

        //Home btn

        ImageView home_icon = new ImageView("file:/../AddOn/Images/home_icon.png");
        Rectangle home_rect = new Rectangle(1072,0,50,50);

        home_btn.setLayoutX(1060); home_btn.setLayoutY(0);
        home_btn.getStyleClass().add("cart_btn");
        home_rect.setFill(Color.TRANSPARENT);
        home_icon.setPreserveRatio(true);
        home_icon.setFitWidth(35);
        home_icon.setFitHeight(35);
        home_btn.getChildren().addAll(home_rect, home_icon);


        //Main label

        Label main_label = new Label("Insert your product !");
        main_label.getStyleClass().add("main_label");
        main_label.setLayoutX(430); main_label.setLayoutY(70);
        containerBox.getChildren().add(main_label);

        //Product Form
        Rectangle product_form_rect = new Rectangle(0,0,800,570);
        product_form_rect.setStyle("-fx-fill: #f4f4f4; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
        product_form.setLayoutX(200); product_form.setLayoutY(130);
        product_form.getChildren().add(product_form_rect);


        AnchorPane first_row = new AnchorPane();
        first_row.setLayoutY(60);
        Rectangle first_row_rect = new Rectangle(0,0,800,110);
        first_row_rect.setFill(Color.TRANSPARENT);
        //first_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        //description TextArea
        description_input.setPrefRowCount(3);
        description_input.setWrapText(true);
        description_input.setPromptText("Insert your product description here");
        description_input.setFocusTraversable(false);
        description_input.getStyleClass().add("input");
        description_input.setPrefSize(700, 90);
        description_input.setLayoutX(50); description_input.setLayoutY(5);
        first_row.getChildren().addAll(first_row_rect, description_input);
        product_form.getChildren().add(first_row);


        AnchorPane second_row = new AnchorPane();
        second_row.setLayoutY(200);
        Rectangle second_row_rect = new Rectangle(0,0,800,55);
        second_row_rect.setFill(Color.TRANSPARENT);
        //second_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        //URL image Input
        TextField url_image_input = new TextField();
        url_image_input.setPromptText("Insert your image URL here");
        url_image_input.setFocusTraversable(false);
        url_image_input.getStyleClass().add("input");
        url_image_input.setPrefSize(700, 40);
        url_image_input.setLayoutX(50); url_image_input.setLayoutY(5);
        inputs.add(url_image_input);
        second_row.getChildren().addAll(second_row_rect, url_image_input);
        product_form.getChildren().add(second_row);

        AnchorPane third_row = new AnchorPane();
        third_row.setLayoutY(285);
        Rectangle third_row_rect = new Rectangle(0,0,800,55);
        third_row_rect.setFill(Color.TRANSPARENT);
        //third_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        //Price
        Label price_symbol = new Label("$");
        price_symbol.setStyle("-fx-font-size: 32px; -fx-text-fill: #858585");
        price_symbol.setLayoutX(60); price_symbol.setLayoutY(2);
        TextField price_input = new TextField();
        price_input.setPromptText("Price");
        price_input.setFocusTraversable(false);
        price_input.getStyleClass().add("input");
        price_input.setPrefSize(90, 40);
        price_input.setLayoutX(80); price_input.setLayoutY(5);
        inputs.add(price_input);

        // Menu in the sorting div
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        MenuItem category_cd = new MenuItem("CDs and Vinils");
        menuItems.add(category_cd);
        MenuItem category_storage = new MenuItem("Data Storage");
        menuItems.add(category_storage);
        MenuItem category_digitalc = new MenuItem("Digital Cameras");
        menuItems.add(category_digitalc);
        MenuItem category_headphone = new MenuItem("Headphones");
        menuItems.add(category_headphone);
        MenuItem category_laptop = new MenuItem("Laptops");
        menuItems.add(category_laptop);
        MenuItem category_monitor = new MenuItem("Monitors");
        menuItems.add(category_monitor);
        MenuItem category_printer = new MenuItem("Printers");
        menuItems.add(category_printer);
        MenuItem category_smartphone = new MenuItem("Smartphones");
        menuItems.add(category_smartphone);
        MenuItem category_smartwatch = new MenuItem("Smartwatches");
        menuItems.add(category_smartwatch);
        MenuItem category_speaker = new MenuItem("Speakers");
        menuItems.add(category_speaker);
        MenuItem category_tablet = new MenuItem("Tablets");
        menuItems.add(category_tablet);
        MenuItem category_television = new MenuItem("Televisions");
        menuItems.add(category_television);
        MenuItem category_videogamepc = new MenuItem("Videogames PC");
        menuItems.add(category_videogamepc);
        MenuItem category_videogameps4 = new MenuItem("Videogames PS4");
        menuItems.add(category_videogameps4);
        MenuItem category_videogamexbox = new MenuItem("Videogames Xbox One");
        menuItems.add(category_videogamexbox);
        MenuItem category_videogameswitch = new MenuItem("Videogames Nintendo Switch");
        menuItems.add(category_videogameswitch);
        category_menu.getItems().addAll(category_cd, category_storage, category_digitalc, category_headphone
                ,category_laptop,category_monitor,category_printer,category_smartphone,category_smartwatch
                ,category_speaker,category_tablet,category_television,category_videogamepc,category_videogameps4
                ,category_videogamexbox,category_videogameswitch);
        MenuBar category_menubar = new MenuBar(category_menu);
        category_menubar.setPrefSize(300,40);
        category_menubar.setLayoutX(230); category_menubar.setLayoutY(5);

        menuItem_setupEvents(menuItems);

        //Quantity
        TextField quantity_input = new TextField();
        quantity_input.setPromptText("Quantity available");
        quantity_input.setFocusTraversable(false);
        quantity_input.getStyleClass().add("input");
        quantity_input.setPrefSize(158, 40);
        quantity_input.setLayoutX(590); quantity_input.setLayoutY(5);
        inputs.add(quantity_input);

        third_row.getChildren().addAll(third_row_rect,price_symbol, price_input, category_menubar, quantity_input);
        product_form.getChildren().add(third_row);

        AnchorPane fourth_row = new AnchorPane();
        fourth_row.setLayoutY(370);
        Rectangle fourth_row_rect = new Rectangle(0,0,800,120);
        fourth_row_rect.setFill(Color.TRANSPARENT);
        //fourth_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");

        scrollPane.setLayoutX(15); scrollPane.setLayoutY(5);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportWidth(750);
        scrollPane.setPrefViewportHeight(96);
        AnchorPane fifth_row = new AnchorPane();
        setScrollPane(fifth_row);

        fourth_row.getChildren().addAll(fourth_row_rect,scrollPane);
        product_form.getChildren().add(fourth_row);

        AnchorPane six_row = new AnchorPane();
        six_row.setLayoutY(510);
        Rectangle six_row_rect = new Rectangle(0,0,800,55);
        six_row_rect.setFill(Color.TRANSPARENT);
        //six_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        addProduct_btn.setLayoutX(100); addProduct_btn.setLayoutY(5);
        addProduct_btn.getStyleClass().add("signup_btn");
        Rectangle signup_btn_rect = new Rectangle(0,0,600,40);
        signup_btn_rect.setStyle("-fx-fill: #dfdfdf; -fx-stroke: #858585; -fx-stroke-width: 2;");
        Label signup_btn_label = new Label("Add product");
        signup_btn_label.getStyleClass().add("signup_label");
        addProduct_btn.getChildren().addAll(signup_btn_rect,signup_btn_label);
        six_row.getChildren().addAll(six_row_rect, addProduct_btn);
        product_form.getChildren().add(six_row);


        //Rights

        Label rights_label = new Label("© 2020 Signup Form. All rights reserved | Cybuy. All the fields are mandatory.");
        rights_label.getStyleClass().add("rights");
        rights_label.setLayoutX(200); rights_label.setLayoutY(710);
        containerBox.getChildren().add(rights_label);

    }

    private void setScrollPane(AnchorPane fifth_row){
        int num_products = 1;

        fifth_row.setLayoutY(400);
        Rectangle fifth_row_rect = new Rectangle(5,5,740,46);
        fifth_row_rect.setFill(Color.TRANSPARENT);
        //fifth_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        //detail
        TextField detail_input = new TextField();
        detail_input.setPromptText("Detail title");
        detail_input.setFocusTraversable(false);
        detail_input.getStyleClass().add("input");
        detail_input.setPrefSize(200, 40);
        detail_input.setLayoutX(20); detail_input.setLayoutY(7);
        //value
        TextField value_input = new TextField();
        value_input.setPromptText("Detail value");
        value_input.setFocusTraversable(false);
        value_input.getStyleClass().add("input");
        value_input.setPrefSize(390, 40);
        value_input.setLayoutX(280); value_input.setLayoutY(7);

        detailMap.put(detail_input, value_input);
        //+ btn
        StackPane plus_btn = new StackPane();
        plus_btn.getStyleClass().add("search_btn");
        plus_btn.setLayoutX(700); plus_btn.setLayoutY(13);
        Rectangle plus_rect = new Rectangle(0,0,30,30);
        plus_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 1;");
        Label plus_label = new Label("+");
        plus_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px");
        plus_btn.getChildren().addAll(plus_rect,plus_label);
        plus_btn.setOnMouseClicked(event -> addDetailRect(fifth_row,5));
        fifth_row.getChildren().addAll(fifth_row_rect, detail_input, value_input, plus_btn);
        scrollPane.setContent(fifth_row);

    }

    private void addDetailRect(AnchorPane fifth_row, int y){
        int new_y = y+50;
        Rectangle fifth_row_rect = new Rectangle(5,new_y,740,46);
        fifth_row_rect.setFill(Color.TRANSPARENT);
        //fifth_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        //detail input
        TextField detail_input = new TextField();
        detail_input.setPromptText("Detail title");
        detail_input.setFocusTraversable(false);
        detail_input.getStyleClass().add("input");
        detail_input.setPrefSize(200, 40);
        detail_input.setLayoutX(20); detail_input.setLayoutY(new_y+2);
        //value input
        TextField value_input = new TextField();
        value_input.setPromptText("Detail value");
        value_input.setFocusTraversable(false);
        value_input.getStyleClass().add("input");
        value_input.setPrefSize(390, 40);
        value_input.setLayoutX(280); value_input.setLayoutY(new_y+2);

        detailMap.put(detail_input,value_input);
        //+ btn
        StackPane plus_btn = new StackPane();
        plus_btn.getStyleClass().add("search_btn");
        plus_btn.setLayoutX(700); plus_btn.setLayoutY(new_y+8);
        Rectangle plus_rect = new Rectangle(0,0,30,30);
        plus_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 1;");
        Label plus_label = new Label("+");
        plus_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px");
        plus_btn.getChildren().addAll(plus_rect,plus_label);
        plus_btn.setOnMouseClicked(event -> addDetailRect(fifth_row,new_y));
        fifth_row.getChildren().addAll(fifth_row_rect, detail_input, value_input, plus_btn);
        scrollPane.setContent(fifth_row);
    }

    private void addProductClick(){
        ArrayList<String> inputs_strings = new ArrayList<>();
        HashMap<String, String> details_strings_map = new HashMap<>();
        String category = category_menu.getText();
        System.out.println("Category: " + category + "\n");
        String description = description_input.getText();
        System.out.println("Description: " + description + "\n");

        for(int i=0; i<inputs.size(); i++){
            inputs_strings.add(inputs.get(i).getText());
            System.out.println("Input" + i + ": " + inputs_strings.get(i) + "\n");
        }

        for (Map.Entry<TextField, TextField> entry : detailMap.entrySet()) {
            String key = entry.getKey().getText();
            String value = entry.getValue().getText();
            details_strings_map.put(key,value);
        }

        for (Map.Entry<String, String> entry : details_strings_map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("Detail: " + key + "   Value: " + value + "\n");
        }

        String product_type = getSelectedCategory(category);
        String product_platform = getSelectedPlatform(category);

        Product new_product = Product.ProductBuilder()
                .setDescription(description)
                .setProductType(product_type)
                .setProductPlatform(product_platform)
                .setImage(inputs_strings.get(0))
                .setPrice(Double.parseDouble(inputs_strings.get(1)))
                .setDetails(details_strings_map)
                .setSellerUsername(session.getUserLogged().getUsername())
                .setQuantityAvailable(Integer.parseInt(inputs_strings.get(2)));


        if(eventHandler.addProduct(new_product))
            display_success();

    }

    private void display_success(){
        StackPane successBox = new StackPane();
        successBox.setLayoutX(1010); successBox.setLayoutY(350);
        Rectangle success_rect = new Rectangle(0,0,180,100);
        success_rect.setStyle("-fx-fill: #d1d1d1; -fx-stroke: green; -fx-stroke-width: 2;");
        Label success_label = new Label("Product correctly added!");
        success_label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-font-fill: green");
        successBox.getChildren().addAll(success_rect,success_label);
        containerBox.getChildren().add(successBox);
        //resetSuccess(); //TODO RITARDO NON FUNZIONANTE
    }

    private void resetSuccess(){

        try {
            Thread.sleep(2000);
            session.getAddProductInterface().setScene();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void menuItem_setupEvents(ArrayList<MenuItem> menuItems){
        for (MenuItem item : menuItems) {
            item.setOnAction(t -> category_menu.setText(item.getText()));
        }
    }


}


