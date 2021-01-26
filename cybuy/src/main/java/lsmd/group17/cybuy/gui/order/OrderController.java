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
        //When the FXML is loaded set the history to Empty
        mainContainer.setVisible(false);
        emptyText.setVisible(true);

        //add an event to go to the past orders or active orders
        changeTypeOrder.setOnMouseClicked((mouseEvent -> {
            arePast = !arePast;
            changeTypeOrder.setText(!arePast ? "View past orders" : "View active orders");
            this.refreshOrders();
        }));

        //add the event to refresh the order history
        refreshBtn.setOnMouseClicked((event) -> this.refreshOrders());
    }

    public void refreshOrders(){

        //reload the user structure
        User user = this.session.getUserLogged();

        //clean the order scene
        orders.getChildren().clear();

        //array that will contain every order
        ArrayList<Order> OrderArray = (arePast) ? EventHandler.getInstance().getPastOrders(user) : EventHandler.getInstance().getActiveOrders(user);

        if(OrderArray == null){
            navbarController.logMessage("Somehing gone wrong...", SearchbarController.message_type.ERROR);
            return;
        }
        //if the orders array is empty shows the empty scene
        if(OrderArray.size() > 0) {
            emptyText.setVisible(false);
            mainContainer.setVisible(true);
        }else{
            emptyText.setVisible(true);
            mainContainer.setVisible(false);
        }

        //shows every order
        for(Order o : OrderArray){
            FXMLLoader loader;

            //try to load the FXML file containing the skeleton of the order
            try {
                loader = new FXMLLoader(new URL("file:./AddOn/FXML/OrderInterface/OrderDetail.fxml"));
                Parent parentRoot = loader.load();

                orders.getChildren().add(parentRoot);
            }catch(Exception e){
                System.err.println("ERROR loading FXML file order detail.");
                e.printStackTrace();
                continue;
            }

            //load the controller for the FXML file and update its content to fit the order
            OrderDetailController subcontroller = loader.getController();
            subcontroller.setLink(session);
            subcontroller.setOrder(o, this);
        }
    }

    public void setLink(Session s){
        this.session = s;
        navbarController.setLink(s);

        User user = s.getUserLogged();

        if(user == null){
            System.err.println("ERROR the user is not logged this interface should not comes up");
        }else{
            this.refreshOrders();
        }
    }

}
