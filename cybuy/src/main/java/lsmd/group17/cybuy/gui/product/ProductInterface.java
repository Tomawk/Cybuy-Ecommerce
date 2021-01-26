package lsmd.group17.cybuy.gui.product;

import lsmd.group17.cybuy.gui.prototypes.GraphicInterface;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.model.Product;

public class ProductInterface extends GraphicInterface {

    public ProductInterface(Session s) {
        super(s, "file:./AddOn/FXML/ProductInterface/Interface.fxml");
    }

    public void setProduct(Product p){
        ((ProductController)this.controller).setProduct(p);
    }
}
