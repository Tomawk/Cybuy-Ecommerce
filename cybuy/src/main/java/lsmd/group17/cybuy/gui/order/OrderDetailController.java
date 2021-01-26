package lsmd.group17.cybuy.gui.order;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lsmd.group17.cybuy.gui.prototypes.Controller;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.Order;
import lsmd.group17.cybuy.model.OrderState;

import java.net.URL;

public class OrderDetailController extends Controller {
    @FXML protected  Text id, state, date, price;
    @FXML protected VBox details, stateContainer;
    @FXML protected HBox stateSeller, stateUser;
    @FXML protected StackPane binBtn;
    @FXML protected AnchorPane mainContainer;
    @FXML protected Button setStateBtn;
    @FXML protected MenuButton possibleStates;

    private  Session session;

    @FXML protected void initialize(){}

    public void setLink(Session s){
        this.session = s;
    }

    public void setOrder(Order o, OrderController parentController){

        //set the content of the scene
        id.setText(o.getObjectId());
        state.setText(o.getState().toString());
        possibleStates.setText(o.getState().toString());
        date.setText(o.getOrderDate().toString());
        String priceString = String.format("%.2f", o.getPrice());
        price.setText("$" + priceString);

        //set the event to remove the relative order
        binBtn.setOnMouseClicked((event) -> this.removeOrder(o, parentController));
        if(o.getState() == OrderState.arrived)
            binBtn.setVisible(false);

        //set possible states for an order this will be useful only for the seller
        for(OrderState itemState : OrderState.values()){
            MenuItem item = new MenuItem(itemState.toString());
            item.setOnAction((event) ->{
                possibleStates.setText(itemState.toString());
            });
            possibleStates.getItems().add(item);
        }

        //set the event to update the order
        setStateBtn.setOnMouseClicked((event) -> {
            OrderState newState = OrderState.valueOf(this.possibleStates.getText());
            EventHandler.getInstance().changeOrderState(o, newState);
            parentController.refreshOrders();
        });


        if(this.session.getUserLogged().getRole().equals("User")){
            stateContainer.getChildren().remove(stateSeller);
        }else{
            stateContainer.getChildren().remove(stateUser);
        }

        //for every Product in the order load its FXML file and its controller
        for(Order.ProductOrder product : o.getProductOrders()){
            FXMLLoader loader;

            try{
                loader = new FXMLLoader(new URL("file:./AddOn/FXML/OrderInterface/ProductDetail.fxml"));

                Parent parentRoot = loader.load();
                details.getChildren().add(parentRoot);
            }catch(Exception e){
                System.err.println("ERROR loading FXML file product order ");
                e.printStackTrace();
                continue;
            }

            //retrieve the controller and set its content
            OrderProductController subcontroller = loader.getController();
            subcontroller.setLink(session);
            subcontroller.setProduct(product);
        }
    }

    private void removeOrder(Order o, OrderController parent){
        boolean ret = EventHandler.getInstance().removeOrder(o);
        parent.refreshOrders();
    }
}
