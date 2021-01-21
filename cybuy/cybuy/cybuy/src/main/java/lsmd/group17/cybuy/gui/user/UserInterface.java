package lsmd.group17.cybuy.gui.user;

import lsmd.group17.cybuy.gui.prototypes.GraphicInterface;
import lsmd.group17.cybuy.gui.general.Session;

public class UserInterface extends GraphicInterface {

    protected UserController controller;

    public UserInterface(Session s){
        super(s, "file:./AddOn/FXML/UserInterface/Interface.fxml");
    }

}
