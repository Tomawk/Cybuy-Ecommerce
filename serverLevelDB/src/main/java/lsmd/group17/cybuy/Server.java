package lsmd.group17.cybuy;

import lsmd.group17.cybuy.model.Product;
import lsmd.group17.cybuy.persistence.levelDB.LevelDB;
import lsmd.group17.cybuy.persistence.levelDB.Message;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private static LevelDB db;
    private static final String firstServerAddress = "172.16.3.138";
    private static final int firstServerPort = 1234;
    private static final String secondServerAddress = "172.16.3.139";
    private static final int secondServerPort = 1234;

    public static void main(String[] args) {
        db = LevelDB.getInstance();
        db.openDB();

        ServerSocket server = null;

        try {
            // server is listening on port 1234
            server = new ServerSocket(1234);
            server.setReuseAddress(true);
            System.out.println("SERVER STARTED ON PORT " + server.getLocalPort() + " ..."
            + "\nReplicas: " + firstServerAddress + " " + secondServerAddress);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                Socket client = server.accept();

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(client);

                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                    db.closeDB();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                // get the input stream from the connected socket
                InputStream inputStream = clientSocket.getInputStream();
                // create a DataInputStream so we can read data from it.
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                Message messageReceived = (Message) objectInputStream.readObject();

                String messageReceivedType = messageReceived.getType();

                System.out.println("New request from client " + clientSocket.getInetAddress()
                        + ". Type: " + messageReceivedType);

                switch (messageReceivedType) {
                    // Cart
                    case "cart_update":
                        updateProductQuantityCart(messageReceived);
                        break;
                    case "add_cart":
                        addProductToCart(messageReceived);
                        break;
                    case "remove_cart":
                        removeProductFromCart(messageReceived);
                        break;
                    case "get_cart":
                        getProductsInCart(messageReceived);
                        break;
                    case "flush_cart":
                        flushCart(messageReceived);
                        break;
                    // Wishlist
                    case "add_wishlist":
                        addProductToWishlist(messageReceived);
                        break;
                    case "remove_wishlist":
                        removeProductFromWishlist(messageReceived);
                        break;
                    case "get_wishlist":
                        getProductsInWishlist(messageReceived);
                        break;
                    case "flush_wishlist":
                        flushWishlist(messageReceived);
                        break;
                    // Product
                    case "remove_product":
                        removeProduct(messageReceived);
                        break;
                    default:
                        Message message = new Message("ERROR",null, null);
                        sendResponse(message);
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        //
        // ------ CART ------
        //
        private void getProductsInCart(Message messageReceived) {
            ArrayList<Product> products;
            Message message = null;
            try {
                products = db.getProductsInCart(messageReceived.getUserId());
                message = new Message("OK",null, products);
                System.out.println("Successfully got products in user "
                        + messageReceived.getUserId() + " cart.");
            } catch (IOException e) {
                e.printStackTrace();
                message = new Message("ERROR", messageReceived.getUserId(), null);
            } finally {
                sendResponse(message);
            }
        }

        private void addProductToCart(Message messageReceived) {
            Product product = messageReceived.getProducts().get(0);
            Message message;
            int updatedQuantity;
            updatedQuantity = db.addProductToCart(messageReceived.getUserId(), product);
            if (updatedQuantity > 0) {
                System.out.println("Successfully added product " + product.getObjectId()
                        + " to user " + messageReceived.getUserId() + " cart.");
                message = new Message("OK",null, null);
            } else {
                message = new Message("ERROR", messageReceived.getUserId(), messageReceived.getProducts());
            }
            sendResponse(message);

            // HANDLE REDUNDANCIES
            product.setQuantityAvailable(updatedQuantity);
            ArrayList<Product> products = new ArrayList<>();
            products.add(product);
            Message replicaMessage = new Message("cart_update", messageReceived.getUserId(), products);

            sendReplicaMessages(replicaMessage);
        }

        private void removeProductFromCart(Message messageReceived) {
            Product product = messageReceived.getProducts().get(0);
            Message message;
            int updatedQuantity;
            updatedQuantity = db.removeProductCart(messageReceived.getUserId(), product);
            if(updatedQuantity > -1) {
                System.out.println("Successfully removed product " + product.getObjectId()
                        + " from user " + messageReceived.getUserId() + " cart.");
                message = new Message("OK", null, null);
            } else {
                message = new Message("ERROR", messageReceived.getUserId(), messageReceived.getProducts());
            }
            sendResponse(message);

            // HANDLE REDUNDANCIES
            product.setQuantityAvailable(updatedQuantity);
            ArrayList<Product> products = new ArrayList<>();
            products.add(product);
            Message replicaMessage = new Message("cart_update", messageReceived.getUserId(), products);
            sendReplicaMessages(replicaMessage);
        }

        private void flushCart(Message messageReceived) {
            Message message;
            boolean flushed;
            flushed = db.emptyCart(messageReceived.getUserId());

            if (flushed) {
                message = new Message("OK", null, null);
                System.out.println("Successfully emptied user " + messageReceived.getUserId() + " cart.");
            } else {
                message = new Message("ERROR",null, null);
            }

            sendResponse(message);

            handleReplicas(messageReceived, flushed);
        }

        private void updateProductQuantityCart(Message messageReceived) {
            Message message;
            Product product = messageReceived.getProducts().get(0);

            if(db.updateProductQuantityCart(messageReceived.getUserId(), product)) {
                message = new Message("OK", null, null);
                System.out.println("Successfully modified quantity of product " + product.getObjectId() +
                        " in user " + messageReceived.getUserId() + " cart.");
            } else {
                message = new Message("ERROR",null, null);
            }
            sendResponse(message);
        }

        //
        // ------ WISHLIST ------
        //
        private void getProductsInWishlist(Message messageReceived) {
            ArrayList<Product> products = null;
            Message message;
            boolean error = true;

            try {
                products = db.getProductsInWishlist(messageReceived.getUserId());
                error = false;
            } catch (IOException e) {
                    e.printStackTrace();
            }

            if(!error) {
                message = new Message("OK", null, products);
                System.out.println("Successfully got products in user "
                        + messageReceived.getUserId() + " wishlist.");
            } else {
                message = new Message("ERROR", messageReceived.getUserId(), null);
            }

            sendResponse(message);
        }

        private void addProductToWishlist(Message messageReceived) {
            Product product = messageReceived.getProducts().get(0);
            Message message;
            boolean add;

            add = db.addProductWishlist(messageReceived.getUserId(), product);

            if (add) {
                System.out.println("Successfully added product " + product.getObjectId()
                        + " to user " + messageReceived.getUserId() + " wishlist.");
                message = new Message("OK", null, null);
            } else {
                message = new Message("ERROR", messageReceived.getUserId(), messageReceived.getProducts());
            }

            sendResponse(message);

            handleReplicas(messageReceived, add);
        }

        private void removeProductFromWishlist(Message messageReceived) {
            Product product = messageReceived.getProducts().get(0);
            Message message;
            boolean remove;

            remove = db.removeProductWishlist(messageReceived.getUserId(), product);

            if (remove) {
                System.out.println("Successfully removed product " + product.getObjectId()
                        + " from user " + messageReceived.getUserId() + " wishlist.");
                message = new Message("OK",null, null);
            } else {
                message = new Message("ERROR", messageReceived.getUserId(), messageReceived.getProducts());
            }

            sendResponse(message);

            handleReplicas(messageReceived, remove);
        }

        private void flushWishlist(Message messageReceived) {
            Message message;
            boolean flushed;

            flushed = db.emptyWishlist(messageReceived.getUserId());

            if (flushed) {
                System.out.println("Successfully emptied user "
                        + messageReceived.getUserId() + " wishlist.");
                message = new Message("OK", null, null);
            } else {
                message = new Message("ERROR", messageReceived.getUserId(), messageReceived.getProducts());
            }

            sendResponse(message);

            handleReplicas(messageReceived, flushed);
        }

        //
        // ------ PRODUCT ------
        //
        private void removeProduct(Message messageReceived) {
            Message message;
            Product product = messageReceived.getProducts().get(0);

            boolean removed;

            removed = db.removeProduct(product);

            if(removed) {
                System.out.println("Successfully removed product "
                        + product.getObjectId() + " from the db.");
                message = new Message("OK", null, null);
            } else {
                message = new Message("ERROR", messageReceived.getUserId(), messageReceived.getProducts());
            }
            sendResponse(message);

            handleReplicas(messageReceived, removed);
        }




        // UTILITIES
        private void sendResponse(Message message) {
            OutputStream outputStream;
            try {
                outputStream = clientSocket.getOutputStream();
                ObjectOutput objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleReplicas(Message messageReceived, boolean success) {
            if(clientSocket.getInetAddress().getHostAddress().equals(firstServerAddress) ||
                    clientSocket.getInetAddress().getHostAddress().equals(secondServerAddress)) {
                //if the message is coming from another server I do not need to handle replicas
                return;
            }

            if(success) {
                sendReplicaMessages(messageReceived);
            }
        }

        private void sendReplicaMessages(Message replicaMessage) {
            // create a new thread object
            try {
                ReplicaHandler replicaSock = new ReplicaHandler(firstServerAddress, firstServerPort, replicaMessage);
                // This thread will handle the client
                // separately
                new Thread(replicaSock).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // create a new thread object
            try {
                ReplicaHandler replicaSock = new ReplicaHandler(secondServerAddress, secondServerPort, replicaMessage);
                // This thread will handle the client
                // separately
                new Thread(replicaSock).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
