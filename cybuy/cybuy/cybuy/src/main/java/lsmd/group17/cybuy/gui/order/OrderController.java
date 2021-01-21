package lsmd.group17.cybuy.gui.order;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lsmd.group17.cybuy.gui.prototypes.Controller;
import lsmd.group17.cybuy.gui.general.SearchbarController;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.Order;
import lsmd.group17.cybuy.model.User;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class OrderController extends Controller {
    @FXML protected AnchorPane mainContainer;
    @FXML protected Text emptyText;
    @FXML protected VBox orders;
    @FXML protected AnchorPane refreshBtn;

    @FXML protected Parent navbar;
    @FXML protected SearchbarController navbarController;

    @FXML protected Button changeTypeOrder;

    private Session session;
    private boolean arePast = false;

    @FXML protected void initialize(){
        //When the XML is loaded set the history to Empty
        mainContainer.setVisible(false);
        emptyText.setVisible(true);

        changeTypeOrder.setOnMouseClicked((mouseEvent -> {
            arePast = !arePast;
            changeTypeOrder.setText(arePast ? "View past orders" : "View active orders");
            this.refreshOrders();
        }));
    }

    public void refreshOrders(){

        //reload the user structure
        User user = this.session.getUserLogged();


        try{
            user = EventHandler.getInstance().loginFormSignInAction(user.getUsername(), user.getPassword());
        }catch (Exception e){
            System.err.println("FATAL ERROR");
            return;
        }

        this.session.setUserLogged(user);

        //remove every children
        orders.getChildren().clear();


        ArrayList<Order> OrderArray = (arePast) ? EventHandler.getInstance().getPastOrders(user) : user.getOrders();

        if(OrderArray.size() > 0) {
            emptyText.setVisible(false);
            mainContainer.setVisible(true);
        }else{
            emptyText.setVisible(true);
            mainContainer.setVisible(false);
        }

        for(Order o : OrderArray){
            FXMLLoader loader;

            try {
                loader = new FXMLLoader(new URL("file:./AddOn/FXML/OrderInterface/OrderDetail.fxml"));
                Parent parentRoot = loader.load();

                orders.getChildren().add(parentRoot);
            }catch(Exception e){
                System.err.println("ERROR loading FXML file order detail.");
                e.printStackTrace();
                continue;
            }

            OrderDetailController subcontroller = loader.getController();
            subcontroller.setLink(session);
            subcontroller.setOrder(o, this);
        }
    }

    public void setLink(Session s){
        this.session = s;
        navbarController.setLink(s);

        User user = s.getUserLogged();

        //TODO add the refresh button

        if(user == null){
            System.err.println("ERROR the user is not logged this interface should not comes up");
        }else{

            this.refreshOrders();

            refreshBtn.setOnMouseClicked((event) -> this.refreshOrders());
        }
    }

}
