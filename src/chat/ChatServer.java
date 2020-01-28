package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatServer {

	private static final int PORT = 7777;
	static Map<String, PrintWriter> userMap = null;

	public static void main(String[] args) {

		userMap = new LinkedHashMap<String, PrintWriter>();
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));

			while (true) {
				Socket socket = serverSocket.accept(); // blocking
				ChatServerThread serverThread = new ChatServerThread(socket, userMap);
				serverThread.start();
			}
		} catch (IOException e) {
			System.out.println("ChatServer > " + e);
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null && !serverSocket.isClosed())
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
