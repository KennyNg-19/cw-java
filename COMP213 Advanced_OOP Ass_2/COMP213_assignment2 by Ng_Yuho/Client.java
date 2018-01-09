
import java.io.*;
import java.net.*;

/**
 * The class represents a start of one client instance.
 *
 * @author Yuhao Wu
 */
public class Client {

    /**
     * Starts a client program.
     *
     * @param args Arguments referenced by the format specifiers in the format
     * string.
     * @throws java.io.IOException if an I/O error occurs while running the
     * client program
     */
    public static void main(String[] args) throws IOException {
        ClientInstance client = new ClientInstance();
        client.start();
    }

    /**
     * Creates a new client object.
     */
    public Client() {

    }
}

/**
 * This class implements a client intance connecting to a server and sending
 * data back and forth. If anything goes wrong with the connection, for example
 * the host is not there or is listening on a different port, an exception is
 * thrown.
 *
 * @author Yuhao Wu
 */
class ClientInstance {

    // the initial values below should be the same in the server program
    /**
     * The port number is a way to identify a specific process to which an
     * Internet or other network message is to be forwarded when it arrives at a
     * server.
     */
    private final int PORT_NUMBER = 5555;

    /**
     * The string asks client to input a username.
     */
    private final String WELCOME = "Please type your username.";

    /**
     * The String replies to user that the username has not been used and is
     * accpeted.
     */
    private final String ACCEPTED = "Your username is accepted.";

    /**
     * The reduced connection response timeout.
     */
    private final int REDUCED_TIMEOUT = 500;

    /**
     * A Socket for a client instance.
     */
    private Socket socket = null;

    /**
     * A BufferReader for a client instance.
     */
    private BufferedReader in;

    /**
     * A PrintWriter for a client instance.
     */
    private PrintWriter out;

    /**
     * Whether the client is allowed to chat in the room after profile setup.
     */
    private boolean isAllowedToChat = false;

    /**
     * A continuous state whether the client is connected with the server.
     */
    private boolean isServerConnected = false;

    /**
     * Starts running the client.
     */
    public void start() {

        establishConnection();
        // two threads for I/O 
        handleOutgoingMessages();
        handleIncomingMessages();
    }

    /**
     * Builds a connection with the server.
     */
    private void establishConnection() {

        System.out.println("What is the address of the server that you wish to connect to?");
        String serverAddress;
        boolean validIpAddress = false;
        while (!validIpAddress) {
            serverAddress = getClientInput(null);
            //Step 1: check whether input is in correct literal Ipv4 form.
            while (true) {
                if (!validIP(serverAddress)) {
                    serverAddress = getClientInput("[Error] Sorry, your input is not in the valid Ipv4 literal format, please input again:");
                } else {
                    break;
                }
            }

            //Step 2: check whether the input address exists in networks: if so, go on; if not, catch an exeception and ask for input again.
            try {
                // Use the Socket() constructor, and connect(SocketAddress endpoint, int timeout) method to reduce the timeout.
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverAddress, PORT_NUMBER), REDUCED_TIMEOUT);
                validIpAddress = true;
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                isServerConnected = true;
            } catch (IOException e) {
                System.err.println("[Exception] Sorry, cannot find the address in networks, please input again:");
            }
        }
        System.out.println("[Found] Your IP address is found.");
        //Connection is built.
        handleProfileSetUp();
    }

    /**
     * Returns whether the input IP is correct in the Ipv4 format.
     *
     * @param ip the IP address the user input
     * @return {@code true} if the given ip address is in valid format to this
     * string, {@code false} otherwise
     */
    public static boolean validIP(String ip) {

        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Sets up the profile of the client.
     */
    private void handleProfileSetUp() {
        System.out.println("----------------------------Profile Setup-----------------------------");
        String line = null;
        while (!isAllowedToChat) {
            try {
                line = in.readLine();
            } catch (IOException e) {
                System.err.println("[Exception] Sorry, a server error occurs when setting up the profile (Ctrl+C to quit).");
            }
            if (line.startsWith(WELCOME)) {
                //message sent: the server is asking for a name input
                System.out.println("(do not use ctrl+c).");
                out.println(getClientInput(WELCOME));
            } else if (line.startsWith(ACCEPTED)) {
                //message sent: the server has accpeted your name
                isAllowedToChat = true;
                System.out.println("[Accepted] " + ACCEPTED + " You can type messages.");
                System.out.println("[Hints] To see a list of commands, type \\help.");
                System.out.println("----------------------------Chatroom Starts-----------------------------");
            } else {
                System.out.println(line);
            }
        }
    }

    /**
     * Sends the messages input by the client to the server by a sender Thread.
     */
    private void handleOutgoingMessages() {

        Thread senderThread = new Thread(new Runnable() {
            public void run() {
                while (isServerConnected) {
                    //null: no hint on the client screen is needed when waiting for user input                
                    out.println(getClientInput(null));
                }
            }
        });
        senderThread.start();
    }

    /**
     * Returns a message input by the client.
     *
     * @param hint the hint message to be outprint on Client screen
     * @return a message input by the client
     */
    private String getClientInput(String hint) {

        String message = null;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));//Standard input from keyboard

            if (hint != null) {
                System.out.println(hint); //Ask clients for the IP address, or send WELCOME message
            }
            message = reader.readLine();//Read a command from keyboard, later pass it to the server                    
        } catch (IOException e) {
            System.err.println("[Exception] Sorry, an error occurs when getting your input.");
        }
        return message;
    }

    /**
     * Receives the messages from the server by a listener thread.
     */
    private void handleIncomingMessages() { // Listener thread

        Thread listenerThread = new Thread(new Runnable() {
            public void run() {
                while (isServerConnected) {
                    String line = null;
                    try {
                        //reads the stream from the server
                        line = in.readLine();

                        //Case 1: client: "\quit" ->server: null -> back
                        if (line == null) {
                            isServerConnected = false;
                            System.out.println("\n-----------------------------Chatroom Ends---------------------------");
                            System.err.println("[Quit] Successfully, you have requested to disconnect from the server");
                            closeConnection();
                            break;
                        }

                        //Case 2: server: "\shutdown" command
                        if (line.equals("\\shutdown")) {
                            isServerConnected = false;
                            System.out.println("\n------------------------------------Chatroom Ends------------------------------------");
                            System.err.println("[Quit] Sorry, the administrator has shut down the server and forced you to disconnect.");
                            closeConnection();
                            break;
                        }
                        System.out.println(line);

                    } catch (IOException e) {
                        //Case 3: Server: Ctrl + c
                        isServerConnected = false;
                        System.out.println("\n---------------------------------Chatroom Ends-------------------------------");
                        System.err.println("[Quit] Sorry, the server is shut down, so you have been forced to disconnect.");
                        break;
                    }
                }
            }
        });
        listenerThread.start();
    }

    /**
     * Closes the client connection from the server by shuting down the client
     * sockets
     */
    void closeConnection() {

        try {
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.err.println("[Exception] Sorry, an error occurs when closing the Client socket");
            System.err.println(e.getMessage());
        }
    }

}
