# cybuy
Repository for the group project developed for Large-Scale and Multi-Structured Databases.

## The application
*cybuy* is an e-commerce application whose focus is on electronic products. It aims to make the purchase process easier for customers and to allow sellers to manage their products and their sales.
As previously stated, the application can be used both from customers and sellers.
Customers can use the application in order to browse the list of available products, see the details of a specific product, and decide whether to add it to their cart or to their wishlist. 
In addition, customers can place orders, keep track of their previous ones and they can decide to leave a review on the products they have already bought.
On the other hand, sellers can manage their inventory – by adding new products and modifying or deleting old ones – and they can visualize the list of orders and fulfill them.
cybuy also provides an analytics section, through which sellers can visualize sales reports and reviewing performance, and take business decisions accordingly.

## Repository Organization
The project repository is organized as follows:
* *AddOn.* This folder contains all the resources used in the cybuy module. In turn, it is organized into subfolders, containing the CSS, fonts, images and FXML needed to load the GUI.
* *cybuy.* This folder contains the code of the application to be run on the client side.
* *daemon.* This folder contains the code for a program that will run on the primary MongoDB server to update the analytics documents.
* *serverLevelDB.* This folder contains the code that will run on the three servers to manage queries and replicas on LevelDB.
* *Web Scraping.* This folder will contain some of the scripts written for web scraping.
* *Database Dump.* This folder will contain a dump of the Document Database.

A zipped version of the repository is also included (cybuy.zip).
**The dump of the database is not included in cybuy.zip but it can be found in /cybuy-group17/Database Dump/cybuy**
