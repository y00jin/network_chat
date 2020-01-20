package chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClientApp {
	private static final String SERVER_IP = "0.0.0.0";
	private static final int PORT = 7777;

	public static void main(String[] args) {
		String name = null;
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("대화명을 입력하세요.");
			System.out.print(">>> ");
			name = scanner.nextLine();

			if (name.isEmpty() == false)
				break;

			System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
		}
		scanner.close();
		Socket socket = null;
		try {
			// 1. socket 생성
			socket = new Socket();
			// 2. connect to server
			socket.connect(new InetSocketAddress("127.0.0.1", 7777));
			// 3. iostream 생성
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
					true);
			// 4. join protocol 요청 및 처리
			pr.println("join:" + name);
			String joinCheck = br.readLine();
			// 5. join 프로토콜이 성공 응답을 받으면
			if ("200 ok!".equals(joinCheck)) {
				new ChatWindow(name, socket).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
