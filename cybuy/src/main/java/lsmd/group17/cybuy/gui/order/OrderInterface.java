package lsmd.group17.cybuy.gui.order;

import lsmd.group17.cybuy.gui.prototypes.GraphicInterface;
import lsmd.group17.cybuy.gui.general.Session;

public class OrderInterface extends GraphicInterface {

    protected OrderController controller;

    public OrderInterface(Session s){
        super(s, "file:./AddOn/FXML/OrderInterface/Interface.fxml");
    }

}
