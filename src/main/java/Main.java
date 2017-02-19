import client.Client;
import server.Server;

import java.util.Scanner;

public class Main {
    public static final int PORT = 9807;

    public static void main(String[] arg) {
        Scanner in = new Scanner(System.in);

        System.out.println("Start an application as a server or a client? (s/c)");
        while (true) {
            char input = Character.toLowerCase(in.nextLine().charAt(0));
            if (input == 's') {
                new Server(PORT).start();
                break;
            } else if (input == 'c') {
                new Client(PORT).start();
                break;
            } else {
                System.out.println("Incorrect input. Try again.");
            }
        }
    }
}
