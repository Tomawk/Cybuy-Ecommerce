package lsmd.group17.cybuy.gui.user;

import javafx.animation.PauseTransition;
import javafx.scene.control.*;
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

import java.util.ArrayList;

public class RegisterInterface extends GraphicInterface {

    private final AnchorPane containerBox, register_form;
    private final StackPane cart_btn,signup_btn,search_btn;
    private final TextField searchBar;
    private final Label home,login,error_label,success_label;
    private final RadioButton male_gender,female_gender;
    private final ToggleGroup gender_selection;
    private final CheckBox seller_check;
    private final EventHandler eventHandler;


    private final ArrayList<TextField> inputs;

    public RegisterInterface(Session s){
        super(s);

        home = new Label("Home");
        login = new Label();
        error_label = new Label();
        success_label = new Label("Congratulations, your account has been successfully created.");

        containerBox = new AnchorPane();
        register_form = new AnchorPane();

        cart_btn = new StackPane();
        signup_btn = new StackPane();
        search_btn = new StackPane();

        inputs = new ArrayList<>();

        gender_selection = new ToggleGroup();

        male_gender = new RadioButton("Male");
        female_gender = new RadioButton("Female");

        seller_check = new CheckBox();

        searchBar = new TextField();

        eventHandler = EventHandler.getInstance();

        containerBox.getChildren().addAll(searchBar,home,login,register_form);

        if(session.getUserLogged() == null){
            login.setText("Login");
            //Click label login event
            login.setOnMouseClicked(event -> session.getLoginInterface().setScene());
        }else{
            login.setText(session.getUserLogged().getUsername());
            //Click label user event
            login.setOnMouseClicked(event -> session.getUserInterface().setScene());
            if(session.getUserLogged().getRole().equals("User")) containerBox.getChildren().add(cart_btn);
        }

        //root element is an element of the GraphicsInterface parent class
        root = containerBox;
        root.getStylesheets().add("file:./AddOn/CSS/registerInterface.css");

        //##### CLICK EVENTS #####

        //Signup btn event (Register form)
        signup_btn.setOnMouseClicked(event -> SignupClick());
        //Click label home event
        home.setOnMouseClicked(event -> {
            s.setKeyword("");
            s.getPrimaryInterface().setScene();
        });
        //Click cart btn event
        cart_btn.setOnMouseClicked(event -> s.getCartInterface().setScene());
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
        home.getStyleClass().add("nav_signin");
        home.setLayoutX(863); home.setLayoutY(13);
        Label separator = new Label("|");
        separator.getStyleClass().add("nav_signin_separator");
        separator.setLayoutX(912); separator.setLayoutY(13);
        login.getStyleClass().add("nav_signin");
        login.setLayoutX(920); login.setLayoutY(13);
        login.setWrapText(true);
        login.setPrefWidth(100);
        login.setPrefHeight(20);
        containerBox.getChildren().add(separator);

        //Home Icon
        ImageView home_icon = new ImageView("file:/../AddOn/Images/home_icon.png");
        home_icon.setLayoutX(835); home_icon.setLayoutY(16);
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

        //Main label

        Label main_label = new Label("Register your account !");
        main_label.getStyleClass().add("main_label");
        main_label.setLayoutX(430); main_label.setLayoutY(70);
        containerBox.getChildren().add(main_label);

        //Register Form
        Rectangle reg_form_rect = new Rectangle(0,0,800,550);
        reg_form_rect.setStyle("-fx-fill: #f4f4f4; -fx-stroke: #d0d0d0; -fx-stroke-width: 2;");
        register_form.setLayoutX(200); register_form.setLayoutY(130);
        register_form.getChildren().add(reg_form_rect);


        AnchorPane first_row = new AnchorPane();
        first_row.setLayoutY(60);
        Rectangle first_row_rect = new Rectangle(0,0,800,55);
        first_row_rect.setFill(Color.TRANSPARENT);
        //first_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
            //name input
        TextField name_input = new TextField();
        name_input.setPromptText("Name");
        name_input.setFocusTraversable(false);
        name_input.getStyleClass().add("input");
        name_input.setPrefSize(325, 40);
        name_input.setLayoutX(50); name_input.setLayoutY(5);
        inputs.add(name_input);
            //surname input
        TextField surname_input = new TextField();
        surname_input.setPromptText("Surname");
        surname_input.setFocusTraversable(false);
        surname_input.getStyleClass().add("input");
        surname_input.setPrefSize(325, 40);
        surname_input.setLayoutX(425); surname_input.setLayoutY(5);
        inputs.add(surname_input);
        first_row.getChildren().addAll(first_row_rect,name_input,surname_input);
        register_form.getChildren().add(first_row);


        AnchorPane second_row = new AnchorPane();
        second_row.setLayoutY(145);
        Rectangle second_row_rect = new Rectangle(0,0,800,55);
        second_row_rect.setFill(Color.TRANSPARENT);
        //second_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
            //Password input
        PasswordField password_input = new PasswordField();
        password_input.setPromptText("Password");
        password_input.setFocusTraversable(false);
        password_input.getStyleClass().add("input");
        password_input.setPrefSize(325, 40);
        password_input.setLayoutX(50); password_input.setLayoutY(5);
        inputs.add(password_input);
            //Password Confirm input
        PasswordField confirm_input = new PasswordField();
        confirm_input.setPromptText("Confirm Password");
        confirm_input.setFocusTraversable(false);
        confirm_input.getStyleClass().add("input");
        confirm_input.setPrefSize(325, 40);
        confirm_input.setLayoutX(425); confirm_input.setLayoutY(5);
        inputs.add(confirm_input);
        second_row.getChildren().addAll(second_row_rect,password_input,confirm_input);
        register_form.getChildren().add(second_row);

        AnchorPane third_row = new AnchorPane();
        third_row.setLayoutY(230);
        Rectangle third_row_rect = new Rectangle(0,0,800,55);
        third_row_rect.setFill(Color.TRANSPARENT);
        //third_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
            //Email
        TextField email_input = new TextField();
        email_input.setPromptText("Email");
        email_input.setFocusTraversable(false);
        email_input.getStyleClass().add("input");
        email_input.setPrefSize(600, 40);
        email_input.setLayoutX(100); email_input.setLayoutY(5);
        inputs.add(email_input);
        third_row.getChildren().addAll(third_row_rect,email_input);
        register_form.getChildren().add(third_row);

        AnchorPane fourth_row = new AnchorPane();
        fourth_row.setLayoutY(315);
        Rectangle fourth_row_rect = new Rectangle(0,0,800,55);
        fourth_row_rect.setFill(Color.TRANSPARENT);
        //fourth_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
            //Email
        TextField username_input = new TextField();
        username_input.setPromptText("Username");
        username_input.setFocusTraversable(false);
        username_input.getStyleClass().add("input");
        username_input.setPrefSize(400, 40);
        username_input.setLayoutX(50); username_input.setLayoutY(5);
        inputs.add(username_input);

        //radio btns

        male_gender.getStyleClass().add("radio_btn");
        male_gender.setToggleGroup(gender_selection);
        male_gender.setSelected(true);
        male_gender.setLayoutX(480); male_gender.setLayoutY(12); //550
        female_gender.getStyleClass().add("radio_btn");
        female_gender.setToggleGroup(gender_selection);
        female_gender.setLayoutX(555); female_gender.setLayoutY(12); //650

        seller_check.setText("I am a Seller");
        seller_check.getStyleClass().add("radio_btn");
        seller_check.setLayoutX(650); seller_check.setLayoutY(12); //550

        fourth_row.getChildren().addAll(fourth_row_rect,username_input,male_gender,female_gender,seller_check);
        register_form.getChildren().add(fourth_row);


        AnchorPane fifth_row = new AnchorPane();
        fifth_row.setLayoutY(400);
        Rectangle fifth_row_rect = new Rectangle(0,0,800,55);
        fifth_row_rect.setFill(Color.TRANSPARENT);
        //fifth_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
            //Country
        TextField geography_input = new TextField();
        geography_input.setPromptText("Country");
        geography_input.setFocusTraversable(false);
        geography_input.getStyleClass().add("input");
        geography_input.setPrefSize(400, 40);
        geography_input.setLayoutX(50); geography_input.setLayoutY(5);
        inputs.add(geography_input);
            //Age
        TextField age_input = new TextField();
        age_input.setPromptText("Age");
        age_input.setFocusTraversable(false);
        age_input.getStyleClass().add("input");
        age_input.setPrefSize(250, 40);
        age_input.setLayoutX(500); age_input.setLayoutY(5);
        inputs.add(age_input);
        fifth_row.getChildren().addAll(fifth_row_rect,geography_input,age_input);
        register_form.getChildren().add(fifth_row);

        AnchorPane six_row = new AnchorPane();
        six_row.setLayoutY(485);
        Rectangle six_row_rect = new Rectangle(0,0,800,55);
        six_row_rect.setFill(Color.TRANSPARENT);
        //six_row_rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        signup_btn.setLayoutX(100); signup_btn.setLayoutY(5);
        signup_btn.getStyleClass().add("signup_btn");
        Rectangle signup_btn_rect = new Rectangle(0,0,600,40);
        signup_btn_rect.setStyle("-fx-fill: #dfdfdf; -fx-stroke: #858585; -fx-stroke-width: 2;");
        Label signup_btn_label = new Label("Signup");
        signup_btn_label.getStyleClass().add("signup_label");
        signup_btn.getChildren().addAll(signup_btn_rect,signup_btn_label);
        six_row.getChildren().addAll(six_row_rect,signup_btn);
        register_form.getChildren().add(six_row);


        //Rights

        Label rights_label = new Label("© 2020 Signup Form. All rights reserved | Cybuy. All the fields are mandatory.");
        rights_label.getStyleClass().add("rights");
        rights_label.setLayoutX(200); rights_label.setLayoutY(700);
        containerBox.getChildren().add(rights_label);

    }

    /**
     * Function called when the user click on the "Sign Up" button
     */

    private void SignupClick() {
        User new_user;

        //Checkbox
        Boolean isASeller = seller_check.selectedProperty().getValue();

        //Radio btn
        RadioButton selectedRadioButton = (RadioButton) gender_selection.getSelectedToggle();
        String radioButtonSelectedValue = selectedRadioButton.getText();

        //Inputs
        ArrayList<String> input_strings = new ArrayList<>();


        for (TextField input : inputs) {
            String input_text = input.getText(); //retrive text from inputs
            input_strings.add(input_text);
        }

        try {
            new_user = eventHandler.registrationFormSignUpAction(input_strings,radioButtonSelectedValue,isASeller);
            SubmitSuccess(new_user);
        } catch (Exception e) {
            SubmitError(Integer.parseInt(e.getMessage()));
        }
    }

    /**
     * Display a success message if the registration phase is completed with no errors
     * @param user the new user just registered
     */

    private void SubmitSuccess(User user) {

        //reset inputs
        inputs.get(0).setText("");
        inputs.get(1).setText("");
        inputs.get(2).setText("");
        inputs.get(3).setText("");
        inputs.get(4).setText("");
        inputs.get(5).setText("");
        inputs.get(6).setText("");
        inputs.get(7).setText("");
        male_gender.setSelected(true);

        //Confirm message
        success_label.getStyleClass().add("success");
        success_label.setLayoutX(365); success_label.setLayoutY(720);
        containerBox.getChildren().remove(error_label);
        containerBox.getChildren().remove(success_label);
        containerBox.getChildren().add(success_label);

        //wait for 1 second and then redirect the user to the home page already logged
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished( event -> homeRedirect(user) );
        delay.play();

    }

    /**
     * Redirect the user to the homepage
     * @param logged_user the user to be set as the logged user
     */

    private void homeRedirect(User logged_user){
        session.setUserLogged(logged_user);
        session.setKeyword("");
        session.getPrimaryInterface().setScene();
    }

    /**
     * Dispay an error if the registration phase went wrong
     * @param error_code the code of the error to be handled
     */

    private void SubmitError(int error_code){
        //Error message
        error_label.getStyleClass().add("error");
        if(error_code == 1){
            error_label.setText("Error, please fill all the fields.");
            error_label.setLayoutX(500); error_label.setLayoutY(720);
        }
        if(error_code == 2){
            error_label.setText("Error, passwords are not the same.");
            error_label.setLayoutX(490); error_label.setLayoutY(720);
        }
        if(error_code == 3){
            error_label.setText("Error, username isn't available.");
            error_label.setLayoutX(490); error_label.setLayoutY(720);
        }
        containerBox.getChildren().remove(success_label);
        containerBox.getChildren().remove(error_label);
        containerBox.getChildren().add(error_label);
    }

    /**
     * Function that allows the user to search products from the searchbar in the nav bar
     */

    private void searchProducts(){
        session.setKeyword(searchBar.getText());
        session.getPrimaryInterface().setScene();
    }

    public AnchorPane getContainerBox() {
        return containerBox;
    }

    public Label getHomeLabel() {
        return home;
    }

    public Label getLogin() {
        return login;
    }

    public StackPane getCart_btn() { return cart_btn; }
}
