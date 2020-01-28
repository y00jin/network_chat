package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatServerThread extends Thread {

	private String nickname;
	private Socket socket;
	public static List<PrintWriter> listPrintWriter = new ArrayList<PrintWriter>();
	private Map<String, PrintWriter> userMap;
	private PrintWriter pw;
	private BufferedReader br;

	public ChatServerThread(Socket socket, Map<String, PrintWriter> userMap) {
		this.socket = socket;
		this.userMap = userMap;
	}

	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

			while (true) {
				String request = br.readLine();
				if (request == null) {
					log("클라이언트로부터 연결이 끊겼습니다.");
					doQuit(pw);
					break;
				}
				String[] tokens = request.split(">");
				if ("join".equals(tokens[0])) {
					doJoin(tokens[1], pw);
				} else if ("message".equals(tokens[0])) {
					doMessage(tokens[1]);
				} else if ("quit".equals(tokens[0])) {
					pw.println(tokens[0]);
					doQuit(pw);
					break;
				} else if (tokens[0].equals("to")) {
					doWhisper(tokens[1], tokens[2]);
				} else if (tokens[0].equals("error")) {
					doError(tokens[1]);
				}else
					log("ERROR : 알수 없는 요청 (" + tokens[0] + ")");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void doError(String name) {
		PrintWriter printWriter = userMap.get(name);
		printWriter.println("※잘못된 입력입니다※");
		printWriter.flush();
	}

	private void doWhisper(String receiver, String msg) {
		String message = nickname + " (귓속말) >" + msg;
		PrintWriter printWriter = userMap.get(receiver);
		printWriter.println(message);
		printWriter.flush();
	}

	private void doQuit(Writer writer) {
		removeWriter(writer);
		String data = nickname + "님이 퇴장하였습니다.";
		broadcast(data);
	}

	private void removeWriter(Writer writer) {
		synchronized (listPrintWriter) {
			listPrintWriter.remove(writer);
		}
	}

	private void doMessage(String message) {
		String msg = nickname + " > " + message;
		broadcast(msg);
	}

	private void doJoin(String nickName, PrintWriter writer) {
		this.nickname = nickName;
		pw.println("200 ok!");
		addWriter(writer);
		String data = nickName + "님이 입장하였습니다.";
		broadcast(data);
		userMap.put(nickname, pw);
	}

	private void broadcast(String data) {
		synchronized (listPrintWriter) {
			for (PrintWriter writer : listPrintWriter) {
				PrintWriter printWriter = (PrintWriter) writer;
				printWriter.println(data);
				printWriter.flush();
			}
		}
	}

	private void addWriter(Writer writer) {
		synchronized (listPrintWriter) {
			listPrintWriter.add((PrintWriter) writer);
		}
	}

	public static void log(String log) {
		System.out.println("[server#" + Thread.currentThread().getId() + " ] " + log);
	}

}
