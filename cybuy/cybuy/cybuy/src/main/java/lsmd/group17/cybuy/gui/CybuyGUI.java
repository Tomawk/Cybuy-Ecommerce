package lsmd.group17.cybuy.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import lsmd.group17.cybuy.gui.general.Session;
import lsmd.group17.cybuy.middleware.EventHandler;

/**
 * JavaFX CybuyGUI
 */

public class CybuyGUI extends Application {

    @Override
    public void start(Stage stage) {

        Session main = new Session();
        main.start(1200, 750);
    }

    @Override
    public void stop() {
        System.out.println("Closing connection to MongoDB cluster...");
        EventHandler.getInstance().closeMongoDBConnection();
    }

    public static void main(String[] args) {
        launch();
    }

}