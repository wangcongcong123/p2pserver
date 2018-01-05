package com.cc.p2p.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserAndFile implements Serializable {

	/**
	 * this is a very important class used for staying real-time updated data in user list and file list
	 * 
	 * @author wangcongcong
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String user;
	private List<String> filepathes = new ArrayList<String>();

	/**
	 * a constructor two params needed
	 * 
	 * @param user
	 * @param filepaths
	 */
	public UserAndFile(String user, List<String> filepaths) {
		this.user = user;
		this.filepathes = filepaths;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public List<String> getFilepathes() {
		return filepathes;
	}

	public void setFilepathes(List<String> filepathes) {
		this.filepathes = filepathes;
	}

}
