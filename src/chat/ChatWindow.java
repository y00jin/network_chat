package chat;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatWindow {

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private String name;

	private Socket socket;
	private PrintWriter pr;

	public ChatWindow(String name, Socket socket) {
		this.name = name;
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.socket = socket;
	}

	public void show() throws Exception {
		/*
		 * UI 초기화 브븐
		 * 
		 */
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER)
					sendMessage();
			}

		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
		frame.pack();

		/**
		 * 2. IOStream 초기화
		 */
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
		/**
		 * 3. thread 생성 작업
		 */
		new ChatClientThread(br).start();

	}

	private void sendMessage() {
		String message = textField.getText();
		String[] tokens = message.split(":");
		if ("quit".equals(tokens[0]) == true) {
			pr.println("quit:");
			return;
		} else {
			pr.println("message:" + tokens[0]);
		}
	}

	private class ChatClientThread extends Thread {
		private BufferedReader br;

		public ChatClientThread(BufferedReader br) {
			this.br = br;
		}

		@Override
		public void run() {
			try {
				while (true) {
					String data = br.readLine();
					if (data == null) {
						textArea.append("closed by Server.\n");
						textArea.append("채팅을 종료합니다.\n");
						break;
					}
					if (data.equals("quit")) {
						textArea.append("채팅을 종료합니다.\n");
						break;
					}
					textArea.append(data + "\n");
					textField.setText("");
					textField.requestFocus();
				}
				Thread.sleep(1000);
				System.exit(0);
			} catch (Exception e) {
				textArea.append("갑작스러운 오류로 서버가 연결 끊김!\n");
				System.out.println("갑작스러운 오류로 서버가 연결 끊김!\n" + e);
			}
		}

	}
}
