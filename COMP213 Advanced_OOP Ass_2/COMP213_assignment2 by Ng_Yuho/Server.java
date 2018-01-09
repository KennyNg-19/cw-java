
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * The server class implements a server instance able to connect with multiple
 * clients and send data back and forth. If anything goes wrong with the
 * connection, for example a client is not there or is listening on a different
 * port, an exception is thrown.
 *
 * @author Yuhao Wu
 */
public class Server {

    // the initial values below should be the same in the client program
    /**
     * The port number is a way to identify a specific process to which an
     * Internet or other network message is to be forwarded when it arrives at a
     * server.
     */
    private final int PORT_NUMBER = 5555;

    /**
     * The String asks client to input a username.
     */
    private final String WELCOME = "Please type your username.";

    /**
     * The String replies to user that the username has not been used and is
     * accpeted.
     */
    private final String ACCEPTED = "Your username is accepted.";

    /**
     * The ratio of converting from milliseconds to an second.
     */
    private final int MILLISECOND_TO_SECOND = 1000;

    /**
     * The ratio of converting from seconds to an hour.
     */
    private final int SECOND_TO_HOUR = 3600;

    /**
     * The ratio of converting from seconds to a minute.
     */
    private final int SECOND_TO_MINUTE = 60;

    /**
     * The milliseconds of server waiting all clients to disconnect before its
     * final broadcast.
     */
    private final int SERVER_WAITING_TIME = 1000;

    /**
     * The number of current conncected clients.
     */
    private int connectedClientsNumber = 0;

    /**
     * The times of total conncetions built so far.
     */
    private int totalClientsNumber = 0;

    /**
     * The number of Exceptions have occurred.
     */
    private int exceptionsNumber = 0;

    /**
     * The commands available in the server program.
     */
    private ArrayList<String> commands;

    /**
     * The starting time of the server.
     */
    private static long serverStartTime;

    /**
     * A server socket waits for requests to come in over the network. It
     * performs some operation based on that request, and then possibly returns
     * a result to the requester.
     */
    private ServerSocket ss;

    /**
     * The Buffer reader for the administrator to input command in the server
     * program.
     */
    private BufferedReader serverReader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * The HashSet to store all the client names connected with the server,
     * avoiding duplicates.
     */
    private HashSet<String> clientNames = new HashSet<String>();

    /**
     * The HashSet to store PrintWriters for each client program, avoiding
     * duplicates.
     */
    private HashSet<PrintWriter> clientWriters = new HashSet<PrintWriter>();

    /**
     * Starts the server programs.
     *
     * @param args Arguments referenced by the format specifiers in the format
     * string.
     * @throws IOException if an I/O error occurs when running the server
     * program
     */
    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.start();
    }

    /**
     * Constructs a new Server with several commands.
     */
    public Server() {

        commands = new ArrayList<String>();
        commands.add("\\n: the number of connected clients, how many connected clients at the moment");
        commands.add("\\t: total times of connections, till now how many clients connect to the server.");
        commands.add("\\s: the server running time, how long is the server running?");
        commands.add("\\c: the client chatting time, how long am I in the chat room?");
        commands.add("\\i: the server's IP address, what is the IP address of the server?");
        commands.add("\\e: number of exceptions occur, how many exceptions have been thrown?");
        commands.add("\\q: quit the chat room, if you want to disconnect from the server.");
    }

    /**
     * Starts the server instance program.
     *
     * @throws IOException if an I/O error occurs when running the server
     * program
     */
    void start() throws IOException {

        ss = new ServerSocket(PORT_NUMBER);
        serverStartTime = System.currentTimeMillis(); //set server's start time
        System.out.println("The chat server at "
                + InetAddress.getLocalHost() + " is waiting for connections ...\n");
        System.out.println("As the server Administrator, you could type the following command if needed: \n"
                + "[Hints]: \\shut: shutdown the server and then all client(s) will be disconnected.\n");
        System.out.println("------------------------------Chatroom Server Running------------------------------");
        handleAdministratorInput();
        Socket socket;
        Thread thread;
        try {
            while (true) {
                //one for each client
                socket = ss.accept();
                connectedClientsNumber++;
                totalClientsNumber++;
                thread = new Thread(new HandleSession(socket));
                thread.start();
            }
        } catch (IOException e) {
            exceptionsNumber++;
        }
    }

    /**
     * Keeps receiving input from the keyboard from the administrator.
     */
    private void handleAdministratorInput() {

        Thread listenerThread = new Thread(new Runnable() {
            public void run() {
                // keep a continuous state
                while (true) {
                    String line = null;
                    try {
                        //read input from the keyboard
                        line = serverReader.readLine();

                        if ((line == null) || (line.equals("\\shut")) || (line.equals("null"))) {
                            serverBroadcast("\\shutdown");
                            //suspend for a sencond to let all clients exit first
                            Thread.sleep(SERVER_WAITING_TIME);
                            shutDown();
                            break;
                        }
                    } catch (IOException e) {
                        System.err.println("[Exception] Sorry, an error occurs when administrator inputs, please input again:");
                        break;
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        listenerThread.start();
    }

    /**
     * Shuts down the server and closes all connections meanwhile.
     */
    public void shutDown() {

        try {
            ss.close();
            System.out.println("----------------------------Chatroom Server Ends-----------------------------");
            System.out.println("[" + getCurrentTime() + " End] The administrator has shut down the server successfully.");
        } catch (IOException e) {
            exceptionsNumber++;
            System.err.println("[Exception] Sorry, problem shutting down the server.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Boradcast the messages input by the server administrator.
     *
     * @param message the message to be broadcasted
     */
    private void serverBroadcast(String message) {

        for (PrintWriter writer : clientWriters) {
            writer.println(message);
            writer.flush();
        }
    }

    /**
     * Returns the current time in a customized format.
     *
     * @return the current time in the "hour:minute:second" format
     */
    private String getCurrentTime() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    /**
     * The inner class implements a client requests handler including several
     * threads.
     *
     * @author Yuhao Wu
     */
    class HandleSession implements Runnable {

        /**
         * The starting time of the client program,
         */
        private long clientStartTime;

        /**
         * A socket is an endpoint for communication between two programs.
         */
        private Socket socket;

        /**
         * The client name input by the use in the client program.
         */
        private String clientName;

        /**
         * A buffer reader for reading messages from the stream.
         */
        BufferedReader in = null;

        /**
         * A print writter for passing messages to the stream.
         */
        PrintWriter out = null;

        /**
         * Creates a new HandleSession instance.
         *
         * @param socket the socket for communication with the server.
         */
        HandleSession(Socket socket) {
            this.socket = socket;

        }

        /**
         * Implements the run() method from the Runnable() interface. Starts the
         * threads needed in the HandleSession Class.
         */
        @Override
        public void run() {

            try {
                clientStartTime = System.currentTimeMillis();
                createStreams();
                getClientUserName();
                listenForClientMessages();
            } catch (IOException e) {
                exceptionsNumber++;
                System.out.println(e);
            } finally {
                closeConnection(clientName);
            }
        }

        /**
         * Creates I/O streams for the request handler.
         */
        private void createStreams() {

            try {
                synchronized (clientWriters) {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    clientWriters.add(out);
                }
                System.out.println();
            } catch (IOException e) {
                exceptionsNumber++;
                System.err.println("[Exception] Sorry, error occurs in creating your request handler streams.");
            }
        }

        /**
         * Gets and checks duplications of the name input by the clients.
         */
        private void getClientUserName() {

            while (true) {
                out.println(WELCOME);
                out.flush();
                String name = null;
                try {
                    name = in.readLine(); //get name from user BufferReaderStream
                } catch (IOException e) {
                    exceptionsNumber++;
                    System.err.println("[Exception] Sorry, an error occurs when getting the client's username: " + e);
                }

                if (name == null) {//cannot handle if a client press Ctrl+C when typing a name
                    return; //String is undefined, no response
                }
                //protects the critical section by monitors
                synchronized (clientNames) {
                    if (!clientNames.contains(name)) {
                        clientName = name;
                        clientNames.add(name);
                        break;
                    }
                }
                out.println("\n[Name Occupied]Sorry, this username is unavailable.");
                out.flush(); // continue the loop
            }

            out.println(ACCEPTED + "Please type messages.");
            out.flush();
            broadcast("----One connection is built,[" + getCurrentTime() + "] User \"" + clientName + "\" has joined the chat----");

        }

        /**
         * 3
         * 4
         *
         * Keeps listening for the client messages.
         *
         * @throws IOException if an I/O excpetion occurs when reading streams
         */
        private void listenForClientMessages() throws IOException {

            String line; // input from a remote client
            while (in != null) {
                try {
                    line = in.readLine(); // from the client
                    //case: if invalid input occurs from the client
                    if ((line == null) || (line.equals("null"))) {
                        break;
                    } else if (line.startsWith("\\")) { // avoid a command

                        // false only if the client quit,
                        // true, client input a "help" to check the commands list,                  
                        if (!processClientRequest(line)) {
                            return;
                        }
                    } else {
                        out.println("[" + getCurrentTime() + "] You: " + line);//echo
                        out.flush();
                        broadcast("[" + getCurrentTime() + "] " + clientName + ": " + line);
                    }
                } catch (IOException e) {
                    //Client without inputting a name: Ctrl + C
                    System.out.println("[" + getCurrentTime() + " Quit] Sorry, the user \"" + clientName + "\" uses Ctrl+C to abnormally quit.");
                    break;
                }
            }
        }

        /**
         * Processes the command from the client program.
         *
         * @param rawCommand the command read from the client
         * @return {@code true} if the command is not "quit", {@code false} if
         * the command is invalid or "quit" command
         * @throws UnknownHostException if the local host name could not be
         * resolved into an address.
         */
        boolean processClientRequest(String rawCommand) throws UnknownHostException {

            String command = rawCommand.trim();
            if (command.equals("\\q")) {
                return false; //Only the message to the server, Not to other clients
            }
            if (command.equals("\\help")) { //Only the message to the server, Not to other clients
                for (String c : commands) {
                    out.println("Command " + c);
                    out.flush();
                }
                return true;
            }

            //other hints
            switch (command) {
                case "\\n":
                    out.println(getConnectedClientsNumber());
                    break;
                case "\\t":
                    out.println(getTotalClientsNumber());
                    break;
                case "\\e":
                    out.println(getExceptionsNumber());
                    break;
                case "\\s":
                    out.println(getServerRunTime());
                    break;
                case "\\c":
                    out.println(getClientChatTime());
                    break;
                case "\\i":
                    out.println(getIPAddress());
                    break;
                default:
                    //confusing case: not a command but start with a "\"
                    out.println("[" + getCurrentTime() + "] You:" + command);
                    broadcast("[" + getCurrentTime() + "] " + clientName + ": " + command);
            }
            out.flush();
            return true;
        }

        /**
         * broadcast the messages from one client to all but that originated
         * one.
         *
         * @param message the message from th client who is chatting
         */
        private void broadcast(String message) {

            for (PrintWriter writer : clientWriters) {
                if (!(out == writer)) {
                    writer.println(message); //read by all clients BufferReaders
                    writer.flush();
                }
            }
            System.out.println(message); // shows on the server screen
        }

        /**
         * Returns the running time of the server.
         *
         * @return the server's total running time so far
         */
        private String getServerRunTime() {

            long totalSeconds = (System.currentTimeMillis() - serverStartTime) / MILLISECOND_TO_SECOND;
            long hours = totalSeconds / SECOND_TO_HOUR;
            long minutes = (totalSeconds - hours * SECOND_TO_HOUR) / SECOND_TO_MINUTE;
            long seconds = totalSeconds - hours * SECOND_TO_HOUR - minutes * SECOND_TO_MINUTE;
            return "[System] The server has been running for " + hours + "h " + minutes + "m " + seconds + "s.";
        }

        /**
         * Returns the connection time of a client in the chat room.
         *
         * @return a system reply about the connection time
         */
        private String getClientChatTime() {

            long totalSeconds = (System.currentTimeMillis() - clientStartTime) / MILLISECOND_TO_SECOND;
            long hours = totalSeconds / SECOND_TO_HOUR;
            long minutes = (totalSeconds - hours * SECOND_TO_HOUR) / SECOND_TO_MINUTE;
            long seconds = totalSeconds - hours * SECOND_TO_HOUR - minutes * SECOND_TO_MINUTE;
            return "[System] You have been in the chat room for " + hours + "h " + minutes + "m " + seconds + "s.";
        }

        /**
         * Returns the number of currently connected clients.
         *
         * @return a system reply of the number
         */
        private String getConnectedClientsNumber() {
            return "[System] Now " + connectedClientsNumber + " clients are currently connected to the chat room.";
        }

        /**
         * Returns the number of total connections so far.
         *
         * @return a system reply of the number
         */
        private String getTotalClientsNumber() {
            return "[System] So far, " + totalClientsNumber + " clients have been connected to the chat room.";
        }

        /**
         * Returns the number of the exceptions have benn thrown.
         *
         * @return a system reply of the number of the thrown exceptions
         */
        private String getExceptionsNumber() {
            return "[System] " + exceptionsNumber + " Exception(s) have been thorwn.";
        }

        /**
         * Returns the server's IP address.
         *
         * @return a system reply about the address
         * @throws UnknownHostException if the local host name could not be
         * resolved into an address.
         */
        private String getIPAddress() throws UnknownHostException {
            return "[System] The server's IP address is " + InetAddress.getLocalHost().getHostAddress();
        }

        /**
         * Closes the connection of the request handler from the server by
         * shuting down the socket.
         *
         * @param name the client name used by the server to notify all others
         */
        void closeConnection(String name) {

            try {
                socket.close();
                if (name != null) {
                    broadcast("----One connection is closed, [" + getCurrentTime() + "] User \"" + name + "\" has left the chat----");
                    clientNames.remove(name);
                }
                if (out != null) {
                    clientWriters.remove(out);
                }
                connectedClientsNumber--;
                System.out.println();
            } catch (IOException e) {
                exceptionsNumber++;
                System.err.println("[Exception] Sorry, an error occurs when closing the Server socket.");
                System.err.println(e.getMessage());
            }
        }

    }

}
