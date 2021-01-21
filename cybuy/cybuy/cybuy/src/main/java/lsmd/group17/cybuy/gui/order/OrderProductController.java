package lsmd.group17.cybuy.gui.order;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lsmd.group17.cybuy.gui.prototypes.Controller;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.Utilities;
import lsmd.group17.cybuy.gui.product.ProductInterface;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.Order;
import lsmd.group17.cybuy.model.Product;
import lsmd.group17.cybuy.model.User;

public class OrderProductController extends Controller {

    @FXML protected ImageView productImage;
    @FXML protected Label description;
    @FXML protected Text quantity;

    private Session session;

    @FXML
    protected void initialize() {

    }

    public void setLink(Session s) {
        this.session = s;
    }

    public void setProduct(Order.ProductOrder product){
        User user = this.session.getUserLogged();

        if(user.getRole().equals("User")) {

            Image img;

            if(product.getImage() == null){
                description.setText(product.getProductID());
                img = new Image("file:./AddOn/Images/default.jpg");
            }else {
                description.setText(product.getDescription());
                img = new Image(Utilities.correctImage(product));
            }

            description.setOnMouseClicked((event) -> {
                ProductInterface newInterface = this.session.getProductInterface();
                newInterface.setProduct(EventHandler.getInstance().getProductById(product.getProductID()));
                newInterface.setScene();
            });

            quantity.setText(String.valueOf(product.getOrdered_quantity()));

            productImage.setImage(img);
        }else{
            Product p = EventHandler.getInstance().getProductById(product.getProductID() );

            description.setText(p.getDescription());
            description.setOnMouseClicked((event) -> {
                ProductInterface newInterface = this.session.getProductInterface();
                newInterface.setProduct(p);
                newInterface.setScene();
            });

            quantity.setText(String.valueOf(product.getOrdered_quantity()));

            Image img = new Image(p.getImage());
            productImage.setImage(img);
        }
    }
}
