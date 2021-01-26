package lsmd.group17.cybuy.gui.prototypes;

import javafx.fxml.FXML;
import lsmd.group17.cybuy.gui.general.Session;

public abstract class Controller {

    public Controller(){

    }

    @FXML
    protected abstract void initialize();

    public abstract void setLink(Session s);
}
