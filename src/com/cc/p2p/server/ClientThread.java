package com.cc.p2p.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.cc.p2p.bean.InfoBean;
import com.cc.p2p.bean.UserAndFile;
import com.cc.p2p.util.DatabaseHelper;

public class ClientThread extends Thread {

	// a variable to take the mainServer object for refreshing mainThread interface
	p2pServer mainServer;
	Socket socket;

	ObjectInputStream ois;
	ObjectOutputStream oos;

	InfoBean mb;

	private boolean isRun = true;
	private InfoBean currentUser;

	public InfoBean getCurrentUser() {
		return currentUser;
	}

	public ClientThread(p2pServer p2pServer, Socket socket) {
		this.mainServer = p2pServer;
		this.socket = socket;
		try {
			// remember the order is extremely important. due to this order, I waste a lot of time
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("client server error");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				mb = (InfoBean) ois.readObject();
				String content = mb.getMessage_content();
				if (content.equals("login")) {
					currentUser = new InfoBean(mb.getPort(), mb.getIp(), mb.getAccount(), mb.getPassword(),
							null, null);
					login();
				} else if (content.equals("register")) {
					register();
				} else if (content.equals("uploadfile")) {
					uploadFile();
				} else if (content.equals("refresh")) {
					refresh();
				} else if (content.equals("deletefile")) {
					deleteFile();
				} else if (content.equals("disconnect") || content.equals("exit")) {
					ois.close();
					oos.close();
					socket.close();
					isRun = false;
					mainServer.removeClient(this);
				} else if (content.equals("logout")) {
					ois.close();
					oos.close();
					socket.close();
					isRun = false;
					mainServer.removeClient(this);
					mainServer.toLog(currentUser.getAccount() + " logout(ip:" + currentUser.getIp() + "-port:"
							+ currentUser.getPort() + ")" + "........\n---------------------\n");
					notifySomeoneExit();
				} else if (content.equals("getPeerInfoForDownloading")) {
					sendPeerInfo();
				}
			} catch (Exception e) {
				isRun = false;
				mainServer.removeClient(this);
			}

		}

	}

	private void sendPeerInfo() {
		try {
			int port = 0;
			String host = null;
			ArrayList<ClientThread> clientThreads = mainServer.getClients();
			for (ClientThread clientThread : clientThreads) {
				if (mb.getAccount().equals(clientThread.getCurrentUser().getAccount())) {
					port = clientThread.getCurrentUser().getPort();
					host = clientThread.getCurrentUser().getIp();
					break;
				}
			}
			oos.writeObject(
					new InfoBean(port, host, mb.getAccount(), null, "peerInfoReturn", mb.getFilepath()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void deleteFile() {
		String respond_string = DatabaseHelper.deleteFile(mb);
		ArrayList<ClientThread> clientThreads = mainServer.getClients();
		for (ClientThread clientThread : clientThreads) {
			try {
				clientThread.oos.writeObject(
						new InfoBean(-1, null, mb.getAccount(), null, respond_string, mb.getFilepath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void notifySomeoneExit() {
		ArrayList<ClientThread> clientThreads = mainServer.getClients();
		if (clientThreads.size() == 0) {
			return;
		}
		for (ClientThread clientThread : clientThreads) {
			try {
				clientThread.oos.writeObject(new InfoBean(-1, null, mb.getAccount(), null, "userexit", null));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// to refresh the client window when someone logged in

	private void refresh() {
		List<UserAndFile> userAndFilesbundle = new ArrayList<UserAndFile>();
		ArrayList<ClientThread> clientThreads = mainServer.getClients();
		for (int i = 0; i < clientThreads.size(); i++) {
			ClientThread clientThread = clientThreads.get(i);
			String userName = clientThread.currentUser.getAccount();
			UserAndFile userAndFile = DatabaseHelper.searchFilePathsByName(userName);
			userAndFilesbundle.add(userAndFile);
		}
		for (ClientThread clientThread : clientThreads) {
			try {
				clientThread.oos.writeObject(new InfoBean("StartRefresh"));
				clientThread.oos.writeObject(userAndFilesbundle);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void uploadFile() {
		String respond_string = DatabaseHelper.uploadfile(mb);
		ArrayList<ClientThread> clientThreads = mainServer.getClients();
		for (ClientThread clientThread : clientThreads) {
			try {
				clientThread.oos.writeObject(
						new InfoBean(-1, null, mb.getAccount(), null, respond_string, mb.getFilepath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void register() {
		String respond_string = DatabaseHelper.Register(mb);
		try {
			oos.writeObject(new InfoBean(respond_string));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void login() {
		if (mainServer.isLogined(mb.getAccount())) {
			try {
				oos.writeObject(new InfoBean("loginedalready"));
				currentUser = null;
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		boolean isPass = DatabaseHelper.Login(mb);
		try {
			if (isPass) {
				oos.writeObject(new InfoBean("login_success"));
				mainServer.setTitle();
				mainServer.toLog(currentUser.getAccount() + " login(ip:" + currentUser.getIp() + "-port:"
						+ currentUser.getPort() + ")" + "........\n---------------------\n");

			} else {
				oos.writeObject(new InfoBean("login_error"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
