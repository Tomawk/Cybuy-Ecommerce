package lsmd.group17.cybuy.gui.user;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lsmd.group17.cybuy.gui.prototypes.Controller;
import lsmd.group17.cybuy.gui.general.SearchbarController;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.User;

public class UserController extends Controller {

    @FXML protected Text username, password, name, surname;

    @FXML protected VBox btnRoot;
    @FXML protected Text deleteAccount, logoutBtn, orderBtn, cartBtn, browseBtn, wishlistBtn, addProduct, adminBtn, analyticsBtn;

    @FXML protected ImageView userImage;

    @FXML protected Parent navbar;
    @FXML protected SearchbarController navbarController;

    @FXML protected void initialize(){
    }

    public void setLink(Session s){
        User user = s.getUserLogged();

        if(user == null){
            navbarController.logMessage("There is no  logged user...", SearchbarController.message_type.ERROR);
        }else{
            username.setText(user.getUsername());
            name.setText(user.getName());
            surname.setText(user.getSurname());
            password.setText("\u2022\u2022\u2022");


            deleteAccount.setOnMouseClicked((event) -> this.deleteAccount(user, s));
            logoutBtn.setOnMouseClicked((event) -> this.logout(s));

            orderBtn.setOnMouseClicked((event) -> s.getOrderInterface().setScene());
            cartBtn.setOnMouseClicked((event) -> s.getCartInterface().setScene());
            browseBtn.setOnMouseClicked((event) -> {
                s.setKeyword("");
                s.getPrimaryInterface().setScene();
            });
            wishlistBtn.setOnMouseClicked((event) -> s.getWishlistInterface().setScene());
            adminBtn.setOnMouseClicked((event) -> s.getAdminInterface().setScene());
            addProduct.setOnMouseClicked((event) -> s.getAddProductInterface().setScene());
            analyticsBtn.setOnMouseClicked((event) -> s.getAnalyticsInterface().setScene());

            if(user.getRole().equals("User")){
                btnRoot.getChildren().remove(analyticsBtn);
                btnRoot.getChildren().remove(addProduct);
                btnRoot.getChildren().remove(adminBtn);
            }else if(user.getRole().equals("Admin")){
                btnRoot.getChildren().remove(cartBtn);
                btnRoot.getChildren().remove(wishlistBtn);
            }else{
                btnRoot.getChildren().remove(cartBtn);
                btnRoot.getChildren().remove(wishlistBtn);
                btnRoot.getChildren().remove(adminBtn);
            }
        }
        navbarController.setLink(s);

    }

    private void deleteAccount(User u, Session s){
        EventHandler.getInstance().deleteUser(u);

        s.setUserLogged(null);
        s.setKeyword("");
        s.getPrimaryInterface().setScene();
    }

    private void logout(Session s){
        s.setUserLogged(null);
        s.setKeyword("");
        s.getPrimaryInterface().setScene();
    }
}
