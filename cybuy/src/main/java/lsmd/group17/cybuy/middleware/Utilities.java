package lsmd.group17.cybuy.middleware;

import javafx.util.Pair;
import lsmd.group17.cybuy.model.Order;
import lsmd.group17.cybuy.model.Product;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Utilities {

    private Utilities() { } // empty constructor

    /**
     * Returns the correct URL for the product passed as a parameter
     * @param product   Product whose image has to be retrieved
     * @return  URL
     */
    public static String correctImage(Product product) {
        String s;
        if(product.getImage() == null) {
            s = "file:/../AddOn/Images/default.jpg";
        } else if (product.getImage().endsWith("jpg") || product.getImage().endsWith("png")) {
            s = product.getImage();
        } else {
            s = "file:/../AddOn/Images/default.jpg";
        }
        return s;
    }

    public static String correctImage(Order.ProductOrder product) {
        String s;
        if(product.getImage().endsWith("jpg") || product.getImage().endsWith("png"))
            s = product.getImage();
        else {
            s = "file:/../AddOn/Images/default.jpg";
        }
        return s;
    }

    /**
     * Returns he category as used in MongoDB given the category as used in the GUI
     * @param category  category used in the GUI
     * @return  category used in MongoDB
     */
    public static String getSelectedCategory(String category) {
        String selectedCategory;

        switch (category) {
            case "Televisions":
                selectedCategory = "television";
                break;
            case "Data Storage":
                selectedCategory = "data storage";
                break;
            case "Laptops":
                selectedCategory = "laptop";
                break;
            case "Smartphones":
                selectedCategory = "smartphone";
                break;
            case "Smartwatches":
                selectedCategory = "smartwatch";
                break;
            case "Digital Cameras":
                selectedCategory = "digital camera";
                break;
            case "Monitors":
                selectedCategory = "monitor";
                break;
            case "CDs and Vinils":
                selectedCategory = "cd";
                break;
            case "Tablets":
                selectedCategory = "tablet";
                break;
            case "Printers":
                selectedCategory = "printer";
                break;
            case "Headphones":
                selectedCategory = "headphone";
                break;
            case "Speakers":
                selectedCategory = "speaker";
                break;
            case "Videogames PC":
            case "Videogames PS4":
            case "Videogames Xbox One":
            case "Videogames Nintendo Switch":
                selectedCategory = "videogame";
                break;
            case "Videogames ⯈":
            case "Videogames ⯆":
                selectedCategory = "videogame";
                break;
            default:
                selectedCategory = null;
        }
        return selectedCategory;
    }

    /**
     * Returns the platform as used in MongoDB given the platform as used in the GUI
     * @param platform  platform used in the GUI
     * @return  platform used in MongoDB
     */
    public static String getSelectedPlatform(String platform) {
        String selectedPlatform;

        switch (platform) {
            case "PS4 Games":
            case "Videogames PS4":
                selectedPlatform = "ps4";
                break;
            case "PC Games":
            case "Videogames PC":
                selectedPlatform = "pc";
                break;
            case "Xbox One Games":
            case "Videogames Xbox One":
                selectedPlatform = "xbox one";
                break;
            case "Nintendo Switch Games":
            case "Videogames Nintendo Switch":
                selectedPlatform = "nintendo switch";
                break;
            default:
                selectedPlatform = null;

        }

        return selectedPlatform;
    }

    /**
     * Returns a ConfigurationParameters instance created from the XML file whose path is passed as parameter
     * @param xmlPath   path to the XML file
     * @return  ConfigurationParameters instance
     */
    public static Pair<String, ArrayList<Pair<String, Integer>>> readParametersFromXML(String xmlPath) throws ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File(xmlPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;
        try {
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException | IOException e) {
            String path = "cybuy/" + xmlPath;
            fXmlFile = new File(path);
            doc = dBuilder.parse(fXmlFile);
        }
        doc.getDocumentElement().normalize();

        String mongoDBuri = doc.getElementsByTagName("uriMongoDB").item(0).getTextContent();
        ArrayList<Pair<String, Integer>> levelDBServers = new ArrayList<>();

        Node serversNode = doc.getElementsByTagName("levelDBservers").item(0);
        Element serverElem = (Element) serversNode;

        NodeList addresses = serverElem.getElementsByTagName("address");

        for (int temp = 0; temp < addresses.getLength(); temp++) {
            Node nNode = addresses.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String address = eElement.getTextContent();
                int port = Integer.parseInt(eElement.getAttribute("port"));

                Pair<String, Integer> p = new Pair<>(address, port);
                levelDBServers.add(p);
            }
        }

        return new Pair<>(mongoDBuri, levelDBServers);
    }
}
