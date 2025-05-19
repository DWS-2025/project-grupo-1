package es.codeurjc.web.security.jwt;

/**
 * Represents a login request containing user credentials.
 * <p>
 * This class is used to transfer the username and password
 * from the client to the server during authentication.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     LoginRequest request = new LoginRequest("user", "pass");
 * </pre>
 * </p>
 *
 * @author Grupo 1
 */
public class LoginRequest {

	private String username;
	private String password;

	public LoginRequest() {
	}

	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginRequest [username=" + username + ", password=" + password + "]";
	}
}
