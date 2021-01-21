package lsmd.group17.cybuy.gui.prototypes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lsmd.group17.cybuy.gui.general.Session;

import java.net.URL;

//<GraphicInterface> should be the prototype for every interface
public abstract class GraphicInterface {

    protected Session session;
    protected URL FXMLurl;
    protected Controller controller;

    //<root> should be the root of the skeleton of the interface every implementation of <GraphicInterface> should assign it directly
    protected Parent root;


    public GraphicInterface(Session s){
        //simply loads the session so it is possible to link different interface
        session = s;
    }

    public GraphicInterface(Session s, String path){
        session = s;

        try {
            if(path != null)
                FXMLurl = new URL(path);
            else
                FXMLurl = null;
        }catch(Exception e){
            System.out.println("ERROR: malformed URL " + e);
        }

    }

    public void initialize(){
        FXMLLoader loader;

        if(FXMLurl == null)
            return;

        try{
            loader = new FXMLLoader(FXMLurl);

            root = loader.load();
        }catch(Exception e){
            System.out.println("ERROR: impossible to load FXML file");
            e.printStackTrace();
            return;
        }

        controller = loader.getController();
        controller.setLink(session);
    }


    //<setScene> method load the current interface
    public void setScene(){
        session.getScene().setRoot(root);
    }

    public Parent getRoot(){
        return root;
    }
}
