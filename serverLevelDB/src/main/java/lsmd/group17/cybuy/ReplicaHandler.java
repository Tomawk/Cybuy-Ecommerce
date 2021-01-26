package lsmd.group17.cybuy;

import lsmd.group17.cybuy.persistence.levelDB.Message;

import java.io.*;
import java.net.Socket;

public class ReplicaHandler implements Runnable {
    private final Socket replicaSocket;
    private final Message message;

    // Constructor
    public ReplicaHandler(String address, int port, Message message) throws IOException {
        this.replicaSocket = new Socket(address, port);
        this.message = message;
    }

    public void run() {
        // writing to server
        OutputStream outputStream;
        try {
            int tries = 0;
            while(tries <3) {
                outputStream = replicaSocket.getOutputStream();
                ObjectOutput objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(message);

                // reading from server
                // get the input stream from the connected socket
                InputStream inputStream = replicaSocket.getInputStream();
                // create a DataInputStream so we can read data from it.
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                Message messageReceived = (Message) objectInputStream.readObject();

                System.out.println("Ack from replica " + replicaSocket.getInetAddress() + " : " + messageReceived.getType());
                if(messageReceived.getType().equals("OK")) {
                    break;
                } else {
                    tries++;
                }
            }

            replicaSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
                replicaSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
