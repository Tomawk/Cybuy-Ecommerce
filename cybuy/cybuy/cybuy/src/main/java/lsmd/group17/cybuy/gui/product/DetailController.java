package lsmd.group17.cybuy.gui.product;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lsmd.group17.cybuy.gui.prototypes.Controller;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.model.Product;

public class DetailController extends Controller {
    @FXML protected Label key, value;
    @FXML protected AnchorPane mainContainer, deleteBtn;
    @FXML protected HBox boxContainer, deleteBtnContainer;

    @FXML protected void initialize(){};
    private boolean isSeller = false;

    public void setLink(Session s){
        if(s.getUserLogged() != null && !s.getUserLogged().getRole().equals("User"))
            isSeller = true;
    }

    public void setDetail(String key, String value, VBox parent, Product product){
        if(key.equals("Type") || !isSeller) {
            boxContainer.getChildren().remove(deleteBtnContainer);
        }else{
            this.deleteBtn.setOnMouseClicked((event) ->{
                parent.getChildren().remove(mainContainer);
                product.removeDetail(key);
            });
        }

        this.key.setText(key);
        this.value.setText(value);
    }
}
