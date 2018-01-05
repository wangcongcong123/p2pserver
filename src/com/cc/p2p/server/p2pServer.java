package com.cc.p2p.server;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * 
 * @author wangcongcong
 *
 */
public class p2pServer extends JFrame implements Runnable, ActionListener {

	private static final long serialVersionUID = 1L;
	private JScrollPane jsp = new JScrollPane();
	private TextArea ta = new TextArea();
	private JPanel menuPanel = new JPanel();
	private JLabel ipLabel = new JLabel("                                    IP:");
	private JTextField ipTextField = new JTextField(18);
	private JButton startButton = new JButton("start server");
	private JLabel portLabel = new JLabel("                               Port:");
	private JTextField portTextField = new JTextField(18);
	private JButton stopButton = new JButton("stop server");
	private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	private Socket socket;
	private ServerSocket serverSocket;
	private boolean isOpen;
	final static String ip = "127.0.0.1";
	final static int port = 9999;

	public p2pServer()

	{
		menuPanel.setLayout(new GridLayout(2, 3));
		menuPanel.add(ipLabel);
		menuPanel.add(ipTextField);
		menuPanel.add(startButton);
		menuPanel.add(portLabel);
		menuPanel.add(portTextField);
		menuPanel.add(stopButton);
		jsp.getViewport().add(ta, BorderLayout.CENTER);
		ta.setEditable(false);
		ta.append("-------------------------below is the log on server side-------------------------\n");
		ipTextField.setEditable(false);
		portTextField.setEditable(false);
		ipTextField.setText(ip);
		portTextField.setText(String.valueOf(port));
		startButton.addActionListener(this);
		stopButton.addActionListener(this);
		stopButton.setEnabled(false);
		startButton.setEnabled(true);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		this.add(menuPanel, BorderLayout.NORTH);
		this.add(jsp, BorderLayout.CENTER);
		this.setTitle("p2pServer");
		this.setSize(600, 600);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				int option = JOptionPane.showConfirmDialog((p2pServer) e.getSource(), "are you sure to exit server?", "sure to exit",
						JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
				super.windowClosing(e);
			}
		});

	}

	@Override
	public void run() {

		while (isOpen) {

			try {
				socket = serverSocket.accept();
				ClientThread ct = new ClientThread(this, socket);
				ct.start();
				clients.add(ct);
			} catch (IOException e) {
				isOpen = false;
			}

		}

	}

	public static void main(String[] args) {
		new p2pServer();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startButton) {
			startServer();
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
		} else if (e.getSource() == stopButton) {
			isOpen = false;
			try {
				serverSocket.close();
				for (ClientThread clientThread : clients) {
					clientThread.socket.close();
				}
				clients = null;
				ta.append("server stoped successfully... \n");
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void startServer() {
		try {
			serverSocket = new ServerSocket(port);
			isOpen = true;
			new Thread(this).start();
			ta.append("Start successfully, here is p2p server, welcome! \nhost:" + InetAddress.getLocalHost() + "(or" + ip + " for test) port:"
					+ serverSocket.getLocalPort() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void removeClient(ClientThread st) {
		clients.remove(st);
		setTitle("Current online clients:" + clients.size());
	}

	public void setTitle() {
		setTitle("Current online clients:" + clients.size());
	}

	public void toLog(String str) {
		ta.append(str);
	}

	public ArrayList<ClientThread> getClients() {
		return clients;
	}

	public boolean isLogined(String user) {
		for (int i = 0; i < clients.size() - 1; i++) {
			if (clients.get(i).getCurrentUser().getAccount().equals(user)) {
				return true;
			}
		}
		return false;
	}
}
