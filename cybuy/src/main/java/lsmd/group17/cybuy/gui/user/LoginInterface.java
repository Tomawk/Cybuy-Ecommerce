package lsmd.group17.cybuy.gui.user;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lsmd.group17.cybuy.gui.prototypes.GraphicInterface;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.User;

public class LoginInterface extends GraphicInterface {

    private final AnchorPane containerBox;
    private final StackPane cart_btn,signin_btn,search_btn;
    private final TextField searchBar,username_input;
    private final PasswordField password_input;
    private final Label register,home,error_label,success_label;
    private final EventHandler eventHandler;

    public LoginInterface(Session s){
        super(s);

        containerBox = new AnchorPane();
        cart_btn = new StackPane();
        signin_btn = new StackPane();
        search_btn = new StackPane();
        searchBar = new TextField();
        username_input = new TextField();
        password_input = new PasswordField();
        register = new Label("Register");
        home = new Label("Home");
        error_label = new Label("");
        success_label = new Label("Login correctly done!");

        eventHandler = EventHandler.getInstance();

        containerBox.getChildren().addAll(searchBar,register,home);

        if(session.getUserLogged() != null){
            containerBox.getChildren().add(cart_btn);
        }


        //root element is an element of the GraphicsInterface parent class
        root = containerBox;
        root.getStylesheets().add("file:./AddOn/CSS/loginInterface.css");

        //##### CLICK EVENTS ######

        //Signin btn event
        signin_btn.setOnMouseClicked(event -> SigninClick());

        //Click label register event
        register.setOnMouseClicked(event -> session.getRegisterInterface().setScene());

        //Click label home event
        home.setOnMouseClicked(event -> {
            session.setKeyword("");
            session.getPrimaryInterface().setScene();
                });

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

        //SearchBar
        searchBar.setPromptText("Search...");
        searchBar.setFocusTraversable(false);
        searchBar.getStyleClass().add("search-bar");
        searchBar.setLayoutX(200); searchBar.setLayoutY(5);
        searchBar.setPrefWidth(500);

        //Register | Home
        register.getStyleClass().add("nav_signin");
        register.setLayoutX(850); register.setLayoutY(13);
        Label separator = new Label("|");
        separator.getStyleClass().add("nav_signin_separator");
        separator.setLayoutX(912); separator.setLayoutY(13);
        home.getStyleClass().add("nav_signin");
        home.setLayoutX(920); home.setLayoutY(13);
        containerBox.getChildren().add(separator);

        //Home Icon
        ImageView home_icon = new ImageView("file:/../AddOn/Images/home_icon.png");
        home_icon.setLayoutX(825); home_icon.setLayoutY(16);
        home_icon.setPreserveRatio(true);
        home_icon.setFitWidth(20);
        home_icon.setFitHeight(20);
        containerBox.getChildren().add(home_icon);

        ImageView cart_icon = new ImageView("file:/../AddOn/Images/cart_icon.png");
        Rectangle cart_rect = new Rectangle(1072,0,50,50);

        cart_btn.setLayoutX(1060); cart_btn.setLayoutY(0);
        cart_btn.getStyleClass().add("cart_btn");
        cart_rect.setFill(Color.TRANSPARENT);
        cart_icon.setPreserveRatio(true);
        cart_icon.setFitWidth(35);
        cart_icon.setFitHeight(35);
        cart_btn.getChildren().addAll(cart_rect, cart_icon);

        //Main logo

        ImageView main_logo = new ImageView("file:/../AddOn/Images/cybuy_logo.png");
        main_logo.setLayoutX(560);  main_logo.setLayoutY(70);
        main_logo.setFitWidth(100); main_logo.setFitHeight(100);
        main_logo.setPreserveRatio(true);
        containerBox.getChildren().add(main_logo);

        //Main label
        Label main_label = new Label("Sign in to Cybuy");
        main_label.setLayoutX(520); main_label.setLayoutY(150);
        main_label.setStyle("-fx-font-weight: bold; -fx-font-size:22px");
        containerBox.getChildren().add(main_label);

        //signin box
        AnchorPane signinBox = new AnchorPane();
        signinBox.setLayoutX(430); signinBox.setLayoutY(200);
        Rectangle signin_rect = new Rectangle(0,0,350,350);
        signin_rect.setStyle("-fx-fill: #f4f4f4; -fx-stroke: #d0d0d0; -fx-stroke-width: 3;");

            //Username
        Label username_label = new Label("Username");
        username_label.setLayoutX(20); username_label.setLayoutY(30);
        username_label.setStyle("-fx-font-weight: bold; -fx-font-size:16px");
        username_input.setLayoutX(20); username_input.setLayoutY(62);
        username_input.getStyleClass().add("input");
        username_input.setPromptText("Username");
        username_input.setPrefSize(310, 40);

            //Password
        Label password_label = new Label("Password");
        password_label.setLayoutX(20); password_label.setLayoutY(110);
        password_label.setStyle("-fx-font-weight: bold; -fx-font-size:16px");
        password_input.setLayoutX(20); password_input.setLayoutY(142);
        password_input.getStyleClass().add("input");
        password_input.setPromptText("Password");
        password_input.setPrefSize(310, 40);

            //Signin btn
        signin_btn.setLayoutX(20); signin_btn.setLayoutY(222);
        signin_btn.getStyleClass().add("signin_btn");
        Rectangle signin_btn_rect = new Rectangle(0,0,310,40);
        signin_btn_rect.styleProperty().bind(Bindings.when(signin_btn.hoverProperty())
                .then("-fx-fill: #e5e5e5; -fx-stroke: #cccccc; -fx-stroke-width: 2;")
                .otherwise("-fx-fill: white; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;"));
        Label signin_btn_label = new Label("Sign in");
        signin_btn_label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px");
        signin_btn.getChildren().addAll(signin_btn_rect,signin_btn_label);

        signinBox.getChildren().addAll(signin_rect,username_label,username_input,password_label,password_input,signin_btn);
        containerBox.getChildren().add(signinBox);
    }

    /**
     * Display an error depending on the error_code received
     * @param error_code number of error to handle
     */

    private void SigninError(int error_code){
        //Error message
        error_label.getStyleClass().add("error");

        if(error_code == 1){
            error_label.setText("Error, please fill all the fields.");
            error_label.setLayoutX(500); error_label.setLayoutY(580);
        }
        if(error_code == 2){
            error_label.setText("Error, incorrect username or password.");
            error_label.setLayoutX(490); error_label.setLayoutY(580);
        }
        containerBox.getChildren().remove(success_label);
        containerBox.getChildren().remove(error_label);
        containerBox.getChildren().add(error_label);
    }

    /**
     * Function that show a Success message after a successful sign in
     * @param user actual user after the login phase
     */

    private void SigninSuccess(User user){

        //Reset inputs
        username_input.setText("");
        password_input.setText("");

        //Confirm message
        success_label.getStyleClass().add("success");
        success_label.setLayoutX(530); success_label.setLayoutY(580);
        containerBox.getChildren().remove(error_label);
        containerBox.getChildren().remove(success_label);
        containerBox.getChildren().add(success_label);

        //Wait for 1 second and then redirect the user to the homepage already logged
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished( event -> homeRedirect(user) );
        delay.play();

    }

    /**
     * function that redirect the now logged user to the homepage
     * @param user_logged, user now logged in
     */

    private void homeRedirect(User user_logged){
        session.setUserLogged(user_logged);
        session.setKeyword("");
        session.getPrimaryInterface().setScene();
    }

    /**
     * Function that is called when the user click on the "Sign in" button
     */

    private void SigninClick(){
        String username_received = username_input.getText();
        String password_received = password_input.getText();

        try {
            User new_user = eventHandler.loginFormSignInAction(username_received,password_received);
            SigninSuccess(new_user);
        } catch (Exception e) {
            SigninError(Integer.parseInt(e.getMessage()));
        }
    }

    /**
     * Function called when the user search for a product in the searchbar on the nav bar
     */

    private void searchProducts(){
        session.setKeyword(searchBar.getText());
        session.getPrimaryInterface().setScene();
    }

    public AnchorPane getContainerBox() { return containerBox; }
    public Label getHome() { return home; }
    public Label getRegister() { return register; }
    public StackPane getCart_btn() { return cart_btn; }
}
