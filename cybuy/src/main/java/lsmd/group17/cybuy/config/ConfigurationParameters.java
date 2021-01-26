package lsmd.group17.cybuy.config;

import javafx.util.Pair;
import lsmd.group17.cybuy.middleware.Utilities;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class containing the application configuration parameters, to be read from an XML file
 */
public class ConfigurationParameters {
    private static ConfigurationParameters instance;
    private final String uriMongoDB;
    private final ArrayList<Pair<String, Integer>> levelDBServers;

    private ConfigurationParameters(String uriMongoDB, ArrayList<Pair<String, Integer>> levelDBServers) {
        this.uriMongoDB = uriMongoDB;
        this.levelDBServers = levelDBServers;
    }

    public static ConfigurationParameters getInstance(){
        if(instance == null)
        {
            synchronized (ConfigurationParameters.class)
            {
                if(instance==null)
                {
                    try {
                        Pair<String, ArrayList<Pair<String, Integer>>> parameters = Utilities.readParametersFromXML("config.xml");
                        instance = new ConfigurationParameters(parameters.getKey(), parameters.getValue());
                    } catch (IOException | SAXException | ParserConfigurationException e) {
                        e.printStackTrace();
                        System.out.println("FATAL ERROR: Impossible reading configuration file");
                        System.exit(-1);
                    }
                }
            }
        }
        return instance;
    }

    public String getUriMongoDB() { return uriMongoDB; }
    public ArrayList<Pair<String, Integer>> getLevelDBServers() { return levelDBServers; }
}
