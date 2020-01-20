package chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

	private static final String SERVER_IP = "127.0.0.1";
	private static final int SERVER_PORT = 7777;

	public static void main(String[] args) {
		Scanner scanner = null;
		Socket socket = null;

		try {
			// 키보드 연결
			scanner = new Scanner(System.in);
			// 소켓생성
			socket = new Socket();
			// 연결
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT)); // 상대편의 address
			log("connected");

			// reader / writer 생성
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

			// 5. join 프로토콜
			System.out.println("닉네임 >> ");
			String nickname = scanner.nextLine();
			pw.println("join:" + nickname);
			pw.flush();

			// chatClientReceiveThread시작
			ChatClientThread cct = new ChatClientThread(br);
			cct.setSocket(socket);
			cct.start();

			// 키보드 입력처리
			while (true) {
				String input = scanner.nextLine();
				if ("quit".equals(input) == true)
					break;
				else
					pw.println("message:" + input);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log("error : " + ex);
		} finally {
			try {
				if (scanner != null)
					scanner.close();
				if (socket != null && (!socket.isClosed()))
					socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void log(String log) {
		// thread 번호 가져오기
		System.out.println("[server#" + Thread.currentThread().getId() + " ] " + log);
	}
}
