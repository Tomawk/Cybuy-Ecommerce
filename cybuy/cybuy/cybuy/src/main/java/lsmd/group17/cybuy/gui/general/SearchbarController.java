package lsmd.group17.cybuy.gui.general;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.model.User;

import java.util.Timer;

public class SearchbarController{

    @FXML protected Text registerBtn, loginBtn;
    @FXML protected AnchorPane anchorData, container;
    @FXML protected ImageView searchBtn;
    @FXML protected TextField searchBar;
    @FXML protected AnchorPane cartBtn, homeBtn;

    @FXML protected Text messageText;
    @FXML protected VBox containerBox;
    @FXML protected StackPane containerMsg;


    public SearchbarController(){
        GUIthread = new Timer();
    }

    @FXML private void initialize(){
        containerBox.getChildren().remove(containerMsg);
    }

    public enum message_type{ERROR, INFO, ACCOMPLISHMENT};
    private Timer GUIthread;
    public void logMessage(String msg, message_type type){

        GUIthread.cancel();
        GUIthread = new Timer();
        containerBox.getChildren().remove(containerMsg);
        containerBox.getChildren().add(containerMsg);
        messageText.setText(msg);

        containerMsg.getStyleClass().clear();

        containerMsg.getStyleClass().add(type.toString());

        GUIthread.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                                    containerBox.getChildren().remove(containerMsg);
                                }
                        );
                    }
                },
                5000
        );
    }

    public void setLink(Session s){
        registerBtn.setOnMouseClicked(event -> s.getRegisterInterface().setScene());
        cartBtn.setOnMouseClicked(    event -> s.getCartInterface().setScene());
        homeBtn.setOnMouseClicked(    event -> {
            s.setKeyword("");
            s.getPrimaryInterface().setScene();
        });


        User user = s.getUserLogged();

        if(user == null){
            loginBtn.setOnMouseClicked(event -> s.getLoginInterface().setScene());
        }else{

            cartBtn.setVisible(false);
            if(user.getRole().equals("User"))
                cartBtn.setVisible(true);

            loginBtn.setOnMouseClicked(event -> s.getUserInterface().setScene());
            loginBtn.setText(user.getUsername());
        }

        searchBtn.setOnMouseClicked((event) -> this.searchProducts(s, null));
        searchBar.setOnKeyPressed((event) -> this.searchProducts(s, event.getCode()));
    }

    private void searchProducts(Session s, KeyCode code) {

        if(code == null || code == KeyCode.ENTER) {
            s.setKeyword(searchBar.getText());
            s.getPrimaryInterface().setScene();
        }
    }

}