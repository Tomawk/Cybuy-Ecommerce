package lsmd.group17.cybuy.gui.general;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import lsmd.group17.cybuy.gui.prototypes.GraphicInterface;
import lsmd.group17.cybuy.middleware.Utilities;
import lsmd.group17.cybuy.gui.product.ProductInterface;
import lsmd.group17.cybuy.model.Product;
import lsmd.group17.cybuy.middleware.EventHandler;

import java.util.ArrayList;

public class PrimaryInterface extends GraphicInterface {

    private final AnchorPane containerBox,sorting_div, side_bar, articles, page_selection;
    private final TextField searchBar;
    private final Label register, login, user;
    private final StackPane cart_btn, total_pages, previous_btn, next_btn,
            firstPageButton, secondPageButton, thirdPageButton, search_btn;
    private final ArrayList<Label> videogames_subcategories;

    private ArrayList<Label> categories;
    private Label display_results;
    private final Menu sort_menu;
    private final StackPane dots;

    private final EventHandler eventHandler;

    private boolean isLogged = false;

    private boolean show_subsection = false;

    private boolean previous_page = false; //if there is a previous page
    private boolean next_page = true; //if there is a next page

    private int firstButton = 1;
    private int selectedButton = 1;
    private int pageSelected = 1;

    private long totalResults = 0;
    private long totalPages = 0;

    private String selectedCategory = null;
    private String selectedSubCategory = null;
    private int sort = 0;
    private String keyword;

    public PrimaryInterface(Session s) {
        super(s);

        containerBox = new AnchorPane();
        sorting_div = new AnchorPane();
        side_bar = new AnchorPane();
        articles = new AnchorPane();
        page_selection = new AnchorPane();

        searchBar = new TextField();
        searchBar.setPromptText("Search...");
        searchBar.setFocusTraversable(false);

        register = new Label("Register ");
        login = new Label("Login");
        user = new Label("");

        cart_btn = new StackPane();
        total_pages = new StackPane();
        previous_btn = new StackPane();
        next_btn = new StackPane();
        firstPageButton = new StackPane();
        secondPageButton = new StackPane();
        thirdPageButton = new StackPane();
        search_btn = new StackPane();
        dots = new StackPane();

        eventHandler = EventHandler.getInstance();

        videogames_subcategories = new ArrayList<>();

        sort_menu = new Menu("Sort by:                              ");

        if(session.getUserLogged() != null) isLogged = true;

        keyword = session.getKeyword();

        //root element is an element of the GraphicsInterface parent class
        root = containerBox;
        root.getStylesheets().add("file:./AddOn/CSS/primaryInterface.css");
    }

    public void initialize(){
        //
        // ---------- DESIGN ----------
        //

        // Navbar
        Rectangle navbar = new Rectangle(0,0,1200,50);
        navbar.setFill(Color.web("#d0d0d0"));

        // Logo
        ImageView logo = new ImageView("file:/../AddOn/Images/cybuy_logo.png");
        logo.setLayoutX(30); logo.setLayoutY(5);
        logo.setPreserveRatio(true);
        logo.setFitWidth(50);
        logo.setFitHeight(50);

        // Logo text
        ImageView logo_text = new ImageView("file:/../AddOn/Images/cybuy_text.png");
        logo_text.setLayoutX(65); logo_text.setLayoutY(8);
        logo_text.setPreserveRatio(true);
        logo_text.setFitWidth(120);
        logo_text.setFitHeight(60);

        // SearchBar
        searchBar.getStyleClass().add("search-bar");
        searchBar.setText(keyword);
        searchBar.setLayoutX(200); searchBar.setLayoutY(5);
        searchBar.setPrefWidth(500);

        // Search Icon
        search_btn.setLayoutX(630); search_btn.setLayoutY(8);
        search_btn.getStyleClass().add("search_btn");
        Rectangle search_btn_rect = new Rectangle(0,0,30,30);
        search_btn_rect.setStyle("-fx-fill: white; -fx-stroke: #bfbfbf; -fx-stroke-width: 1;");
        ImageView search_icon = new ImageView("file:/../AddOn/Images/search-icon.jpg");
        search_icon.setPreserveRatio(true);
        search_icon.setFitWidth(25);
        search_icon.setFitHeight(25);
        search_btn.getChildren().addAll(search_btn_rect,search_icon);

        // Register | Login
        register.getStyleClass().add("nav_signin");
        register.setLayoutX(850); register.setLayoutY(13);
        Label separator = new Label ("|");
        separator.getStyleClass().add("nav_signin_separator");
        separator.setLayoutX(912); separator.setLayoutY(13);
        login.getStyleClass().add("nav_signin");
        login.setLayoutX(920); login.setLayoutY(13);

        // User
        user.getStyleClass().add("nav_signin");
        user.setLayoutX(920); user.setLayoutY(13);
        user.setWrapText(true);
        user.setPrefWidth(100);
        user.setPrefHeight(20);

        // User Icon
        ImageView user_icon = new ImageView("file:/../AddOn/Images/user.png");
        user_icon.setLayoutX(818); user_icon.setLayoutY(16);
        user_icon.setPreserveRatio(true);
        user_icon.setFitWidth(20);
        user_icon.setFitHeight(20);

        // Cart Icon
        ImageView cart_icon = new ImageView("file:/../AddOn/Images/cart_icon.png");
        Rectangle cart_rect = new Rectangle(1072,0,50,50);

        cart_btn.setLayoutX(1060); cart_btn.setLayoutY(0);
        cart_btn.getStyleClass().add("cart_btn");
        cart_rect.setFill(Color.TRANSPARENT);
        cart_icon.setPreserveRatio(true);
        cart_icon.setFitWidth(35);
        cart_icon.setFitHeight(35);
        cart_btn.getChildren().addAll(cart_rect, cart_icon);

        // Sorting div
        Rectangle div_rect = new Rectangle(0,0,1200,35);
        display_results = new Label(initializeResults());
        sorting_div.setLayoutX(0); sorting_div.setLayoutY(49);
        display_results.getStyleClass().add("results_label");
        display_results.setLayoutX(38); display_results.setLayoutY(7);
        div_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");

        // Menu in the sorting div
        MenuItem sortPriceHtL = new MenuItem("Price: High to low");
        MenuItem sortPriceLtH = new MenuItem("Price: Low to High");
        MenuItem sortStarsHtL = new MenuItem("Stars: High to Low");
        MenuItem sortStarsLtH = new MenuItem("Stars: Low to High");
        sort_menu.getItems().addAll(sortPriceHtL, sortPriceLtH, sortStarsHtL, sortStarsLtH);
        MenuBar sort_menubar = new MenuBar(sort_menu);
        sort_menubar.setLayoutX(910); sort_menubar.setLayoutY(3);

        // Sidebar
        Rectangle sidebar_rect = new Rectangle(0,0,400,666);
        Label title = new Label ("Shop by Category");
        title.getStyleClass().add("sidebar_title");
        categories = getCategories();
        title.setLayoutX(30); title.setLayoutY(30);
        side_bar.setLayoutX(0); side_bar.setLayoutY(84);
        sidebar_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");

        // Page selection
        page_selection.setLayoutX(600); page_selection.setLayoutY(720);

        // Number of pages
        initializeTotalPages();

        // Previous page button
        initializeNavigationButtons(previous_btn, "Previous");

        // Buttons for the first three pages
        initializePageButtons(firstPageButton, 1);
        initializePageButtons(secondPageButton, 2);
        initializePageButtons(thirdPageButton, 3);
        setSelectedButton();

        // Dots
        initializeDots();

        // Next page button
        initializeNavigationButtons(next_btn, "Next");

        //
        //  ---------- INTERFACE CREATION ----------
        //
        containerBox.getChildren().add(searchBar);

        if(!isLogged) {
            containerBox.getChildren().add(login);
        } else {
            user.setText(session.getUserLogged().getUsername());
            containerBox.getChildren().add(user);

            if(session.getUserLogged().getRole().equals("User"))
                containerBox.getChildren().add(cart_btn);
        }

        containerBox.getChildren().addAll(separator, register);

        containerBox.getChildren().addAll(sorting_div, side_bar,articles, page_selection, navbar, logo,
                logo_text,search_btn,user_icon);

        navbar.toBack();

        sorting_div.getChildren().addAll(div_rect, display_results,sort_menubar);

        side_bar.getChildren().addAll(sidebar_rect,title);
        for (Label label : categories) side_bar.getChildren().add(label);

        page_selection.getChildren().addAll(previous_btn, firstPageButton, secondPageButton, thirdPageButton,dots,total_pages, next_btn);

        //
        // ---------- EVENTS ---------
        //

        //Click label register event
        register.setOnMouseClicked(event -> session.getRegisterInterface().setScene());

        //Click label login event
        login.setOnMouseClicked(event -> session.getLoginInterface().setScene());

        //Click user label event
        user.setOnMouseClicked(event -> session.getUserInterface().setScene());

        //Click cart btn event
        cart_btn.setOnMouseClicked(event -> session.getCartInterface().setScene());

        // Sort menu events
        sortPriceHtL.setOnAction(t -> {
            sort_menu.setText("Sort by:  Price: High to Low");
            SortItems(1);
        });

        sortPriceLtH.setOnAction(t -> {
            sort_menu.setText("Sort by:  Price: Low to High");
            SortItems(2);
        });

        sortStarsHtL.setOnAction(t -> {
            sort_menu.setText("Sort by:  Stars: High to Low");
            SortItems(3);
        });

        sortStarsLtH.setOnAction(t -> {
            sort_menu.setText("Sort by:  Stars: Low to High");
            SortItems(4);
        });

        // Click category label event
        for(Label category : categories) {
            category.setOnMouseClicked(event -> selectCategory(category));
        }

        // Click on button for pages event
        firstPageButton.setOnMouseClicked(event -> goToSelectedPage(1));
        secondPageButton.setOnMouseClicked(event -> goToSelectedPage(2));
        thirdPageButton.setOnMouseClicked(event -> goToSelectedPage(3));

        // Click on search image
        search_btn.setOnMouseClicked(event -> searchProducts());

        // Press enter in the search bar
        searchBar.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                searchProducts();
            }
        });
    }

    private void initializeDots() {
        dots.getChildren().clear();

        if(totalPages > (firstButton + 3)) {
            Rectangle dots_rect = new Rectangle(0, 0, 25, 25);
            dots_rect.setFill(Color.TRANSPARENT);
            Label dots_label = new Label("...");
            dots.setLayoutX(195);
            dots.getChildren().addAll(dots_rect, dots_label);
        }
    }

    private void searchProducts() {
        deletePreviousChanges();

        keyword = searchBar.getText();

        initializeChanges();
    }

    private void deletePreviousChanges() {
        sort_menu.setText("Sort by:                              ");

        keyword = null;

        sort = 0;
        firstButton = 1;
        selectedButton = 1;
        pageSelected = 1;
    }

    private void selectCategory(Label category) {
        unsetBoldness(videogames_subcategories);
        unsetBoldness(categories);
        searchBar.setText("");
        deletePreviousChanges();

        if(category.getText().equals("Videogames ⯈") || category.getText().equals("Videogames ⯆")) {
            showSubsection(category);
        }

        selectedCategory = category.getText();
        selectedSubCategory = null;
        category.setStyle("-fx-font-weight: bold;");
        initializeChanges();
    }

    private void selectSubCategory(Label subcategory) {
        unsetBoldness(videogames_subcategories);
        unsetBoldness(categories);
        searchBar.setText("");
        deletePreviousChanges();

        selectedCategory = "Videogames ⯆";
        selectedSubCategory = subcategory.getText();
        subcategory.setStyle("-fx-font-weight: bold;");

        initializeChanges();
    }

    private ArrayList<Label> getCategories(){
        ArrayList<Label> output = new ArrayList<>();

        Label all_category = new Label ("All");
        all_category.getStyleClass().add("sidebar_categories");
        output.add(all_category);

        Label cds_category = new Label ("CDs and Vinils");
        cds_category.getStyleClass().add("sidebar_categories");
        output.add(cds_category);

        Label data_storage_category = new Label ("Data Storage");
        data_storage_category.getStyleClass().add("sidebar_categories");
        output.add(data_storage_category);

        Label cameras_category = new Label ("Digital Cameras");
        cameras_category.getStyleClass().add("sidebar_categories");
        output.add(cameras_category);

        Label headphone_category = new Label ("Headphones");
        headphone_category.getStyleClass().add("sidebar_categories");
        output.add(headphone_category);

        Label laptop_category = new Label ("Laptops");
        laptop_category.getStyleClass().add("sidebar_categories");
        output.add(laptop_category);

        Label monitors_category = new Label ("Monitors");
        monitors_category.getStyleClass().add("sidebar_categories");
        output.add(monitors_category);

        Label printer_category = new Label ("Printers");
        printer_category.getStyleClass().add("sidebar_categories");
        output.add(printer_category);

        Label smartphone_category = new Label ("Smartphones");
        smartphone_category.getStyleClass().add("sidebar_categories");
        output.add(smartphone_category);

        Label smartwatch_category = new Label ("Smartwatches");
        smartwatch_category.getStyleClass().add("sidebar_categories");
        output.add(smartwatch_category);

        Label speaker_category = new Label ("Speakers");
        speaker_category.getStyleClass().add("sidebar_categories");
        output.add(speaker_category);

        Label tablet_category = new Label ("Tablets");
        tablet_category.getStyleClass().add("sidebar_categories");
        output.add(tablet_category);

        Label televisions_category = new Label ("Televisions");
        televisions_category.getStyleClass().add("sidebar_categories");
        output.add(televisions_category);

        Label videogame_category = new Label ("Videogames ⯈");
        videogame_category.getStyleClass().add("sidebar_categories");
        output.add(videogame_category);

        int y_ = 30;
        for (Label label : output) {
            y_ = y_ + 20;
            label.setLayoutX(35);
            label.setLayoutY(y_);
        }

        return output;

    }

    private void showSubsection(Label category){
        if(!show_subsection){

            category.setText("Videogames ⯆");
            show_subsection = true;

            Label switch_subcategory = new Label ("Nintendo Switch Games");
            switch_subcategory.getStyleClass().add("sidebar_categories");
            switch_subcategory.setLayoutX(55);
            switch_subcategory.setLayoutY(330);
            videogames_subcategories.add(switch_subcategory);

            Label pc_subcategory = new Label ("PC Games");
            pc_subcategory.getStyleClass().add("sidebar_categories");
            pc_subcategory.setLayoutX(55);
            pc_subcategory.setLayoutY(350);
            videogames_subcategories.add(pc_subcategory);

            Label ps4_subcategory = new Label ("PS4 Games");
            ps4_subcategory.getStyleClass().add("sidebar_categories");
            ps4_subcategory.setLayoutX(55);
            ps4_subcategory.setLayoutY(370);
            videogames_subcategories.add(ps4_subcategory);

            Label xbox_subcategory = new Label ("Xbox One Games");
            xbox_subcategory.getStyleClass().add("sidebar_categories");
            xbox_subcategory.setLayoutX(55);
            xbox_subcategory.setLayoutY(390);
            videogames_subcategories.add(xbox_subcategory);

            for (Label videogames_subcategory : videogames_subcategories)
                side_bar.getChildren().add(videogames_subcategory);

            // Click on subcategory label event
            for(Label subcategory : videogames_subcategories) {
                subcategory.setOnMouseClicked(event -> selectSubCategory(subcategory));
            }

        } else {
            show_subsection = false;
            category.setText("Videogames ⯈");
            for (Label videogames_subcategory : videogames_subcategories)
                side_bar.getChildren().remove(videogames_subcategory);
            videogames_subcategories.clear();
        }
    }

    private void unsetBoldness(ArrayList<Label> categories) {
        for(Label category: categories)
            if (category.getText().equals(selectedCategory) || category.getText().equals(selectedSubCategory))
                category.setStyle("-fx-font-weight: normal");
    }

    private void initializePageButtons(StackPane button, int position) {
        button.getChildren().clear();
        if ((firstButton + (position - 1)) <= totalPages) {
            button.getStyleClass().add("pages_btn");
            Rectangle button_rect = new Rectangle(0, 0, 25, 25);
            button_rect.setStyle(" -fx-stroke: #424241; -fx-stroke-width: 1;");
            button_rect.setFill(Color.TRANSPARENT);
            button_rect.setArcWidth(5.0);
            button_rect.setArcHeight(5.0);
            Label label = new Label(String.valueOf(firstButton + (position - 1)));
            button.setLayoutX(105 + (position - 1) * 30);
            button.getChildren().addAll(button_rect, label);
        }
    }

    private void initializeNavigationButtons(StackPane button, String type) {
        button.getChildren().clear();
        button.getStyleClass().add("previous_btn");
        Rectangle button_rect = new Rectangle(0,0,100,25);
        button_rect.setFill(Color.TRANSPARENT);
        button_rect.setArcWidth(5.0);
        button_rect.setArcHeight(5.0);

        Label button_label = new Label();
        if (type.equals("Next")) {
            button.setLayoutX(255);
            button_label.setText("Next  ⟶");
        } else button_label.setText("⟵ Previous");

        button.setAlignment(Pos.CENTER);
        button.getChildren().addAll(button_rect,button_label);

        if(type.equals("Next")) {
            if (next_page) {
                button_rect.setStyle(" -fx-stroke: #424241; -fx-stroke-width: 1;");
                button_label.setStyle(" -fx-font-weight: bold");
                button.styleProperty().bind(Bindings.when(button.hoverProperty())
                        .then("-fx-background-color: #E9E9E9; -fx-cursor: hand;")
                        .otherwise("-fx-background-color: #F1F1F1"));
                button.setOnMouseClicked(this::goToNextPage);
            } else {
                button_rect.setStyle(null);
                button_label.setStyle(null);
                button.setOnMouseClicked(null);
                button.styleProperty().bind(Bindings.when(button.hoverProperty())
                        .then("-fx-background-color: #F1F1F1")
                        .otherwise("-fx-background-color: #F1F1F1"));
            }
        } else {
            if(previous_page) {
                button_rect.setStyle(" -fx-stroke: #424241; -fx-stroke-width: 1;");
                button_label.setStyle(" -fx-font-weight: bold");

                button.styleProperty().bind(Bindings.when(button.hoverProperty())
                        .then("-fx-background-color: #E9E9E9; -fx-cursor: hand;")
                        .otherwise("-fx-background-color: #F1F1F1"));
                button.setOnMouseClicked(this::goToPreviousPage);
            } else {
                button_rect.setStyle(null);
                button_label.setStyle(null);
                button.setOnMouseClicked(null);
                button.styleProperty().bind(Bindings.when(button.hoverProperty())
                        .then("-fx-background-color: #F1F1F1")
                        .otherwise("-fx-background-color: #F1F1F1"));
            }
        }
    }

    private void setSelectedButton() {
        if(selectedButton > totalPages) return;

        switch(selectedButton) {
            case 1:
                changeSelectedButtonStyle(firstPageButton);
                break;
            case 2:
                changeSelectedButtonStyle(secondPageButton);
                break;
            case 0:
                changeSelectedButtonStyle(thirdPageButton);
                break;
            default:
                break;
        }
    }

    private void changeSelectedButtonStyle(StackPane button) {
        button.setStyle(" -fx-background-color: white;");

        ObservableList<Node> children = button.getChildren();
        children.get(0).setStyle(" -fx-stroke: blue; -fx-stroke-width: 1;");
        children.get(1).setStyle("-fx-text-fill: #0000ff");
    }

    private void initializeTotalPages() {
        Rectangle total_pages_rect = new Rectangle(0,0,25,25);
        total_pages_rect.setFill(Color.TRANSPARENT);
        totalPages = (long) Math.ceil(totalResults/12.0);

        next_page = totalPages > 1;

        Label total_pages_label = new Label(Long.toString(totalPages));
        total_pages.setLayoutX(225);
        total_pages.getChildren().clear();

        if(totalPages > ( firstButton + 2))
            total_pages.getChildren().addAll(total_pages_rect,total_pages_label);
    }

    private void goToPreviousPage(MouseEvent event){
        pageSelected = pageSelected - 1;
        if(!next_page && pageSelected < totalPages) {
            next_page = true;
        }

        if(pageSelected == 1) previous_page = false;

        getButtonsNumber();

        initializeChanges();
    }

    private void goToNextPage(MouseEvent event){
        pageSelected = pageSelected + 1;
        if(!previous_page && pageSelected > 1) {
            previous_page = true;
        }
        if(pageSelected == totalPages) next_page = false;

        getButtonsNumber();

        initializeChanges();
    }

    private void getButtonsNumber() {
        selectedButton = pageSelected % 3;
        switch (selectedButton) {
            case 0:
                firstButton = pageSelected - 2;
                break;
            case 1:
                firstButton = pageSelected;
                break;
            case 2:
                firstButton = pageSelected - 1;
                break;
        }

    }

    private void goToSelectedPage(int position){
        pageSelected = firstButton + position - 1;
        selectedButton = position;

        if(selectedButton == 3) selectedButton = 0;

        previous_page = pageSelected != 1;

        next_page = pageSelected != totalPages;

        initializeChanges();
    }

    private void initializeChanges() {
        display_results.setText(initializeResults());
        initializeTotalPages();
        initializeDots();
        initializePageButtons(firstPageButton, 1);
        initializePageButtons(secondPageButton, 2);
        initializePageButtons(thirdPageButton, 3);
        setSelectedButton();

        if(pageSelected == 1) previous_page = false;

        initializeNavigationButtons(previous_btn, "Previous");
        initializeNavigationButtons(next_btn, "Next");
    }

    private String initializeResults(){
        int num_display_results = 12; // number of products displayed in a single page
        articles.getChildren().clear();

        // Returns a label like "1-12 of 50000 results"
        int first_result = ((pageSelected -1)*num_display_results) + 1;
        int final_result = pageSelected *num_display_results;

        ArrayList<Product> products;

        if(isLogged &&
                ((session.getUserLogged().getRole().equals("Admin")) || (session.getUserLogged().getRole().equals("Seller")))) {
            String username = session.getUserLogged().getUsername();
            totalResults = eventHandler.getProductsQuantity(keyword, selectedCategory, selectedSubCategory, username);
            products = eventHandler.getProducts(pageSelected, num_display_results, keyword, selectedCategory, selectedSubCategory, username, sort);
        } else {
            totalResults = eventHandler.getProductsQuantity(keyword, selectedCategory, selectedSubCategory, null);
            // Gets the list of products to be displayed
            products = eventHandler.getProducts(pageSelected, num_display_results, keyword, selectedCategory, selectedSubCategory, null, sort);
        }

        String output = first_result+ "-" + final_result +" of "+ totalResults + " results";

        int init_y = 30;
        articles.setLayoutX(400);articles.setLayoutY(84);
        for(int j=0; j<3; j++){
            int y = init_y;
            int init_x = 50;
            for(int i=0; i<4; i++){
                if(((j*4)+i) < products.size()) {
                    Product product = products.get((j*4)+i);
                    if(product.getQuantityAvailable() == 0) break;
                    int x = init_x;
                    init_x += 200;

                    //rectangle
                    StackPane image_stack = new StackPane();
                    image_stack.setLayoutX(x); image_stack.setLayoutY(y);
                    Rectangle image_article_rect = new Rectangle(0,0,100,100);
                    image_article_rect.setStyle("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
                    image_article_rect.setFill(Color.TRANSPARENT);
                    ImageView img_view = new ImageView(Utilities.correctImage(product));
                    img_view.setFitHeight(98);
                    img_view.setFitWidth(98);
                    img_view.setPreserveRatio(true);
                    img_view.setLayoutX(1); img_view.setLayoutY(1);
                    image_stack.getChildren().addAll(image_article_rect, img_view);

                    image_stack.setOnMouseClicked((event) -> {
                        ProductInterface newInterface = session.getProductInterface();
                        newInterface.setProduct(product);
                        newInterface.setScene();
                    });

                    //Description
                    StackPane description = new StackPane();
                    Rectangle description_rect = new Rectangle(0,0,160,49);
                    description_rect.setFill(Color.TRANSPARENT);
                    String description_string = product.getDescription();
                    Label description_label = new Label(description_string);
                    description_label.setWrapText(true);
                    description_label.setPrefWidth(150);
                    description_label.setPrefHeight(55);
                    description_label.setTextAlignment(TextAlignment.CENTER);
                    description_label.setAlignment(Pos.TOP_CENTER);
                    description_label.getStyleClass().add("article_description");
                    description.setLayoutX(x-30); description.setLayoutY(y+110);
                    description.getChildren().addAll(description_rect,description_label);


                    description.setOnMouseClicked(mouseEvent -> {
                        ProductInterface pi = session.getProductInterface();
                        pi.setProduct(product);
                        pi.setScene();
                    });

                    //price
                    StackPane price = new StackPane();
                    Rectangle price_rect = new Rectangle(0,0,80, 20);
                    price_rect.setFill(Color.TRANSPARENT);
                    String priceString = String.format("%.2f", product.getPrice());
                    Label price_label = new Label("$"+priceString);
                    price.setAlignment(Pos.TOP_CENTER);
                    price_label.getStyleClass().add("article_price");
                    price.setLayoutX(x+10); price.setLayoutY(y+160);
                    price.getChildren().addAll(price_rect,price_label);
                    articles.getChildren().addAll(image_stack,description,price);
                }

            }
            init_y += 200;
        }
        return output;
    }

    private void SortItems(int sort_filter){
        sort = sort_filter;
        pageSelected = 1;
        firstButton = 1;
        selectedButton = 1;
        previous_page = false;
        initializeChanges();
    }
}
