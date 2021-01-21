package lsmd.group17.cybuy.gui.general;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lsmd.group17.cybuy.gui.order.OrderInterface;
import lsmd.group17.cybuy.gui.product.ProductInterface;
import lsmd.group17.cybuy.gui.user.LoginInterface;
import lsmd.group17.cybuy.gui.user.RegisterInterface;
import lsmd.group17.cybuy.gui.user.seller.AddProductInterface;
import lsmd.group17.cybuy.gui.user.UserInterface;
import lsmd.group17.cybuy.gui.user.seller.AnalyticsInterface;
import lsmd.group17.cybuy.gui.user.seller.admin.AdminPanelInterface;
import lsmd.group17.cybuy.gui.user.standard.CartInterface;
import lsmd.group17.cybuy.gui.user.standard.WishlistInterface;
import lsmd.group17.cybuy.model.User;

public class Session {
    private Stage stage;

    private User userLogged;
    private String keyword;

    public Session(){

        //Initialize the window for the application
        stage = new Stage();
        stage.setTitle("cybuy");
        stage.getIcons().add(new Image("file:/../AddOn/Images/cybuy_logo.png"));

        userLogged = null;
        keyword = null;
    }

    // <start> method should starts the graphic part of the application W and H are the width and height of the window
    public void start(int W, int H){
        Scene s = new Scene(getPrimaryInterface().getRoot(), W, H); //primaryInterface
        stage.setScene(s);
        stage.show();
    }

    public void setUserLogged(User new_user){
        userLogged = new_user;
    }

    public User getUserLogged(){ return  userLogged;}

    public Stage getStage(){
        return stage;
    }

    public Scene getScene(){
        return stage.getScene();
    }

    public CartInterface getCartInterface() {
        CartInterface response = new CartInterface(this);
        response.initialize();
        return response;
    }

    public LoginInterface getLoginInterface() {
        LoginInterface response = new LoginInterface(this);
        response.initialize();
        return response;
    }

    public PrimaryInterface getPrimaryInterface() {
        PrimaryInterface response = new PrimaryInterface(this);
        response.initialize();
        return response;
    }

    public RegisterInterface getRegisterInterface() {
        RegisterInterface response = new RegisterInterface(this);
        response.initialize();
        return response;
    }

    public OrderInterface getOrderInterface() {
        OrderInterface response = new OrderInterface(this);
        response.initialize();
        return response;
    }

    public ProductInterface getProductInterface() {
        ProductInterface response = new ProductInterface(this);
        response.initialize();
        return response;
    }

    public UserInterface getUserInterface() {
        UserInterface response = new UserInterface(this);
        response.initialize();
        return response;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public WishlistInterface getWishlistInterface(){
        WishlistInterface response = new WishlistInterface(this);
        response.initialize();
        return response;
    }
    public AddProductInterface getAddProductInterface(){
        AddProductInterface response = new AddProductInterface(this);
        response.initialize();
        return response;
    }

    public AdminPanelInterface getAdminInterface() {
        AdminPanelInterface response = new AdminPanelInterface(this);
        response.initialize();
        return response;
    }

    public AnalyticsInterface getAnalyticsInterface() {
        AnalyticsInterface response = new AnalyticsInterface(this);
        response.initialize();
        return response;
    }
}
