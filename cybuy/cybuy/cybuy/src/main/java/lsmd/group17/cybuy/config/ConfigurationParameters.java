package lsmd.group17.cybuy.config;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Class containing the application configuration parameters, to be read from an XML file
 */
public class ConfigurationParameters {
    private final String uriMongoDB;
    private final ArrayList<Pair<String, Integer>> levelDBservers;

    /**
     * Creates a ConfigurationParameters instance with the specified parameters
     * @param uriMongoDB    connection string for MongoDB
     * @param levelDBServers    address-port pairs of the servers on which levelDB is deployed
     */
    public ConfigurationParameters(String uriMongoDB, ArrayList<Pair<String, Integer>> levelDBServers) {
        this.uriMongoDB = uriMongoDB;
        this.levelDBservers = levelDBServers;
    }

    public String getUriMongoDB() { return uriMongoDB; }

    public ArrayList<Pair<String, Integer>> getLevelDBservers() { return levelDBservers; }
}
