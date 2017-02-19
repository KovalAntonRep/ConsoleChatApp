package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Server {
    private List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());
    private ServerSocket server;
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            server = new ServerSocket(port);

            while (true) {
                Socket socket = server.accept();

                Connection connection = new Connection(socket);
                connections.add(connection);
                connection.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            server.close();

            synchronized(connections) {
                Iterator<Connection> iterator = connections.iterator();
                while(iterator.hasNext()) {
                    (iterator.next()).close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Connection extends Thread {
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;

        private String name = "";

        private Connection(Socket socket) {
            this.socket = socket;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        private void close() {
            try {
                socket.close();
                in.close();
                out.close();

                connections.remove(this);
                if (connections.size() == 0) {
                    Server.this.closeAll();
                    System.exit(0);
                }
            } catch (IOException e) {
                System.err.println("Threads are not closed");
            }
        }

        public void run() {
            try {
                name = in.readLine();

                synchronized(connections) {
                    Iterator<Connection> iter = connections.iterator();
                    while(iter.hasNext()) {
                        (iter.next()).out.println(name + " is here.");
                    }
                }

                String message;
                while (true) {
                    message = in.readLine();
                    if (message.equals("exit")) {
                        break;
                    }

                    synchronized(connections) {
                        Iterator<Connection> iter = connections.iterator();
                        while(iter.hasNext()) {
                            Connection next = iter.next();
                            if (!next.equals(this)) {
                                (next).out.println(name + ": " + message);
                            }
                        }
                    }
                }

                synchronized(connections) {
                    Iterator<Connection> iter = connections.iterator();
                    while(iter.hasNext()) {
                        (iter.next()).out.println(name + " has left.");
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }
}
