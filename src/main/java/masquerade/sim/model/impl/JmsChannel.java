package masquerade.sim.model.impl;

import java.io.OutputStream;

/**
 * A channel receiving requests from a JMS queue
 */
public class JmsChannel extends AbstractChannel {

	private String url;
	private String user;
	private String password;

	public JmsChannel(String name) {
		super(name);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		System.out.println("Would connect to " + user + ":" + password + "@" + url);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "JmsChannel: " + user + "@" + url;
	}

	@Override
	protected void marshalResponse(Object response, OutputStream responseOutput) {
		// TODO Auto-generated method stub		
	}
}
