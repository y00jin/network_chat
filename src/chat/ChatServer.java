package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

	private static final int PORT = 7777;
	public static List<PrintWriter> listPrintWriter = null;

	public static void main(String[] args) {

		listPrintWriter = new ArrayList<PrintWriter>();
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));

			while (true) {
				Socket socket = serverSocket.accept(); // blocking
				ChatServerThread serverThread = new ChatServerThread(socket, listPrintWriter);
				serverThread.start();
			}
		} catch (IOException e) {
			System.out.println("ChatServer : " + e);
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
