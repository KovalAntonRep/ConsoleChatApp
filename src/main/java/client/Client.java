package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    public Client(int PORT) {
        Scanner scan = new Scanner(System.in);
        String ip = "127.0.0.1";

        try {
            socket = new Socket(ip, PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Enter your alias:");
            out.println(scan.nextLine());

            Resender resend = new Resender();
            resend.start();

            String message = "";
            while (!message.equals("exit")) {
                message = scan.nextLine();
                out.println(message);
            }
            resend.setStop();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Threads are not closed");
        }
    }

    private class Resender extends Thread {
        private boolean stopped;

        public void setStop() {
            stopped = true;
        }

        @Override
        public void run() {
            try {
                while (!stopped) {
                    String message = in.readLine();
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.err.println("Message receiving error!");
                e.printStackTrace();
            }
        }
    }
}
