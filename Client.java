import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            InputStream input;
            try {
                input = new FileInputStream("server.dat");
                properties.load(input);
            } catch (FileNotFoundException e) {
                properties.setProperty("serverAddress", "127.0.0.1");
                properties.setProperty("port", "12345");
            }

            Scanner scanner = new Scanner(System.in);

            while (true) {
                String serverAddress = properties.getProperty("serverAddress");
                int port = Integer.parseInt(properties.getProperty("port"));

                Socket socket = new Socket(serverAddress, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                System.out.print("수식을 입력하세요 (예: 2 + 3, 'q' 입력하면 종료): ");
                String expression = scanner.nextLine();

                if (expression.equals("q")) {
                    out.println(expression);
                    socket.close();
                    break;
                }

                out.println(expression);

                String result = in.readLine();
                if (result != null) {
                    System.out.println("결과: " + result);
                } else {
                    System.out.println("서버로부터 유효하지 않은 응답을 받았습니다.");
                }

                socket.close(); // 연결을 닫습니다.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
