package lsmd.group17.cybuy.gui.product;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lsmd.group17.cybuy.gui.prototypes.Controller;
import lsmd.group17.cybuy.gui.general.SearchbarController;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.Utilities;
import lsmd.group17.cybuy.middleware.EventHandler;
import lsmd.group17.cybuy.model.Product;
import lsmd.group17.cybuy.model.Review;
import lsmd.group17.cybuy.model.User;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProductController extends Controller {

    private int nDetails;
    private Product relativeProduct;

    @FXML protected Parent navbar;
    @FXML protected SearchbarController navbarController;

    //------ USER INTERFACE -------
    @FXML protected Button addCart;
    @FXML protected Text addWishlist, quantity;
    @FXML protected Label productNameUser;
    @FXML protected HBox priceUser, quantityUser;

    //------ SELLER INTERFACE ------
    @FXML protected TextArea productNameSeller;
    @FXML protected  Text removeProduct, applyChangesBtn;
    @FXML protected TextField urlImage, priceInput, quantityInput, newKey, newValue;
    @FXML protected HBox priceSeller, quantitySeller;
    @FXML protected AnchorPane addNewDetail, addDetailContainer;


    //------ SHARED INTERFACE ---------
    @FXML protected ImageView productImage, starImage;
    @FXML protected Text productPrice, numberReview;
    @FXML protected AnchorPane starsAnchor;
    @FXML protected VBox leftList, rightList, infoContainer, productInfoContainer;

    @FXML protected void initialize(){}

    private Session session;
    public void setLink(Session s){
        this.session = s;

        navbarController.setLink(s);

        hideShowObjects(s.getUserLogged());
    }

    // this method insert a new Detail into the detail Pane
    private void insertDetail(String key, String value){
        FXMLLoader loader;
        Parent root;
        try {
            //load the detail graphic
            loader = new FXMLLoader(new URL("file:./AddOn/FXML/ProductInterface/Detail.fxml"));
            root = loader.load();

        }catch(Exception e){
            System.out.println("ERROR Impossible to load FXML\n");
            e.printStackTrace();
            return;
        }

        VBox parent = leftList.getChildren().size() <= rightList.getChildren().size() ? leftList : rightList;
        DetailController controller = loader.getController();
        controller.setLink(this.session);
        controller.setDetail(key, value, parent, this.relativeProduct);
        AnchorPane.setTopAnchor(root, nDetails * 40.0);

        parent.getChildren().add(root);
        nDetails++;
    }

    //this method change the scene to show the attribute of the Product p
    public void setProduct(Product p){
        relativeProduct = p;

        productNameUser.setText(p.getDescription());
        productNameSeller.setText(p.getDescription());
        productNameSeller.setPromptText(p.getDescription());

        quantity.setText("" + p.getQuantityAvailable());
        quantityInput.setText("" + p.getQuantityAvailable());
        quantityInput.setPromptText("" + p.getQuantityAvailable());

        priceInput.setText("" + p.getPrice());
        priceInput.setPromptText("" + p.getPrice());
        productPrice.setText(" $" + String.format("%.2f",p.getPrice()));


        //try to load the image
        try {
            productImage.setImage(new Image(Utilities.correctImage(p)));
            urlImage.setText(Utilities.correctImage(p));
            urlImage.setPromptText(Utilities.correctImage(p));
        }catch(Exception e){
            System.out.println("ERROR image not found: " + p.getImage());
        }

        nDetails = 0;
        //clear every detail from the previous Product
        leftList.getChildren().clear();
        rightList.getChildren().clear();

        //insert the type as detail
        insertDetail("Type", p.getProductType());

        HashMap<String, String> details = p.getDetails();

        //insert  every detail of the product
        Iterator<Map.Entry<String, String>> itr = details.entrySet().iterator();

        int counter = 1;
        while(itr.hasNext()){
            Map.Entry<String, String> element = itr.next();
            insertDetail(element.getKey(), element.getValue());
            counter++;
        }

        setStars(p.getAverageReview());
        numberReview.setText(Integer.toString(p.getTotalReviews()));

        hideShowObjects(this.session.getUserLogged());

    }

    //method that it's use to hide some content to the user or to the seller
    private void hideShowObjects(User user){
        if(user != null){
            if(relativeProduct != null && user.getUsername().equals(relativeProduct.getSellerUsername())) {
                removeProduct.setVisible(true);
                removeProduct.setOnMouseClicked((event) -> {
                    boolean removed = EventHandler.getInstance().removeProduct(session.getUserLogged(), relativeProduct);
                    if(removed) {
                        navbarController.logMessage("Product successfully removed", SearchbarController.message_type.ACCOMPLISHMENT);
                    } else {
                        navbarController.logMessage("Something went wrong, try again", SearchbarController.message_type.ERROR);
                    }
                });
            }

            if(user.getRole().equals("User")) {
                //---- remove seller interface --------
                applyChangesBtn.setVisible(false);
                productNameSeller.setVisible(false);
                urlImage.setVisible(false);
                removeProduct.setVisible(false);
                productInfoContainer.getChildren().remove(addDetailContainer);
                infoContainer.getChildren().remove(priceSeller);
                infoContainer.getChildren().remove(quantitySeller);

                // --- set events for the user input ---
                starsAnchor.setOnMouseClicked(this::sendReview);
                starsAnchor.setOnMouseMoved(this::changeReview);
                starsAnchor.setOnMouseExited(this::resetReview);

                addCart.setOnMouseClicked(this::addToCart);

                addWishlist.setOnMouseClicked((event) -> {
                    boolean inserted = EventHandler.getInstance().addProductToWishlist(session.getUserLogged(), relativeProduct);
                    if(inserted) {
                        navbarController.logMessage("Product successfully added to your wishlist", SearchbarController.message_type.ACCOMPLISHMENT);
                    } else {
                        navbarController.logMessage("Something went wrong, try again", SearchbarController.message_type.ERROR);
                    }
                });
            }else{
                //---- remove user interface ----------
                productNameUser.setVisible(false);
                infoContainer.getChildren().remove(addCart);
                infoContainer.getChildren().remove(addWishlist);
                infoContainer.getChildren().remove(priceUser);
                infoContainer.getChildren().remove(quantityUser);

                // --- set events for the seller input ------
                applyChangesBtn.setOnMouseClicked(this::applyChanges);
                addNewDetail.setOnMouseClicked((event) -> {
                    this.insertDetail(newKey.getText(), newValue.getText());
                    this.relativeProduct.insertDetail(newKey.getText(), newValue.getText());
                    newKey.setText("");
                    newValue.setText("");
                });
            }
        }else{
            productInfoContainer.getChildren().remove(addDetailContainer);
            infoContainer.getChildren().remove(addCart);
            infoContainer.getChildren().remove(addWishlist);
            applyChangesBtn.setVisible(false);
            productNameSeller.setVisible(false);
            urlImage.setVisible(false);
            infoContainer.getChildren().remove(priceSeller);
            infoContainer.getChildren().remove(quantitySeller);
            removeProduct.setVisible(false);

        }
    }

    @FXML private void addToCart(Event e){
        boolean inserted = EventHandler.getInstance().addProductToCart(session.getUserLogged(), this.relativeProduct);
        if(inserted) {
            navbarController.logMessage("Product successfully added to your cart", SearchbarController.message_type.ACCOMPLISHMENT);
        } else {
            navbarController.logMessage("Something went wrong, try again", SearchbarController.message_type.ERROR);
        }
    }

    @FXML private void sendReview(MouseEvent e){
        int review_value = (int)(1 + 5 * (e.getX() - starImage.getX()) / starImage.getFitWidth());

        Review review = new Review(this.relativeProduct.getObjectId(), review_value);

        if(this.session.getUserLogged() == null){
            navbarController.logMessage("You can't leave a review if you are not logged in", SearchbarController.message_type.ERROR);
        }else {
            EventHandler.getInstance().sendReview(this.session.getUserLogged(), review);
        }
    }

    //this function is a callback for the mouse when it's moved inside the review box
    @FXML private void changeReview(MouseEvent e){
        int review = (int)(1 + 5 * (e.getX() - starImage.getX()) / starImage.getFitWidth());
        setStars(review);
    }

    //this function is a call back for the mouse when it leaves the review box
    @FXML private void resetReview(Event e){
        setStars(relativeProduct.getAverageReview());
    }


    private void applyChanges(Event e){

        try {
            double prc = Double.parseDouble(this.priceInput.getText());
            int qty = Integer.parseInt(this.quantityInput.getText());

            this.relativeProduct
                    .setPrice(prc)
                    .setDescription(this.productNameSeller.getText())
                    .setImage(this.urlImage.getText())
                    .setQuantityAvailable(qty);

            this.setProduct(this.relativeProduct);

            boolean response = EventHandler.getInstance().modifyProduct(this.relativeProduct);

            if(!response) {
                navbarController.logMessage("ERROR it is impossible to modify the current product, retry", SearchbarController.message_type.ERROR);
            }

        }catch(Exception err){
            navbarController.logMessage("ERROR", SearchbarController.message_type.ERROR);
           System.err.println("ERROR price is not a numerical value or quantity is not a integer value.");
        }
    }

    //utility function to set the star with input a rating in the range 0..5
    private void setStars(double rating){
        Rectangle rect = new Rectangle(0, 0, starImage.getFitWidth() * (rating / 5.0), starImage.getFitHeight());
        starImage.setClip(rect);
    }

}
