import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {
        try {
            int port = 12345;
            ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

            System.out.println("Server started. Listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                Runnable clientHandler = new ClientHandler(clientSocket);
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String expression = in.readLine();
            Double result = evaluateExpression(expression);

            if (result == null) {
                out.println("Error: Invalid expression");
            } else if (result == 0.0) {
                out.println("Error: Divided by zero.");
            } else {
                out.println(result);
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Double evaluateExpression(String expression) {
        String[] tokens = expression.split("\\s+");
        if (tokens.length != 3) {
            return null; // Invalid expression
        }

        double operand1;
        double operand2;
        try {
            operand1 = Double.parseDouble(tokens[0]);
            operand2 = Double.parseDouble(tokens[2]);
        } catch (NumberFormatException e) {
            return null; // Invalid operands
        }

        String operator = tokens[1];

        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                try {
                    if (operand2 != 0) {
                        return operand1 / operand2;
                    } else {
                        return 0.0;
                    }
                } catch (ArithmeticException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            default:
                return null; // Invalid operator
        }
    }
}