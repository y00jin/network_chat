package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientThread extends Thread {
	private BufferedReader br;
	Socket socket = null;
	
	public ChatClientThread(BufferedReader br) {
		this.br = br;
	}

	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			String receiveString;
			String[] split;

			while (br!=null) {
				receiveString = br.readLine();
				System.out.println(receiveString);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
