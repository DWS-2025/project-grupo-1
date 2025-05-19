package es.codeurjc.web.security.jwt;

/**
 * Represents a response for authentication requests, encapsulating the status,
 * message, and optional error details.
 * <p>
 * The {@code AuthResponse} class is typically used to communicate the result of
 * authentication operations, such as login attempts, to the client.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * AuthResponse response = new AuthResponse(AuthResponse.Status.SUCCESS, "Login successful");
 * </pre>
 *
 * <p>
 * The {@link Status} enum indicates whether the authentication was successful or failed.
 * </p>
 *
 * @author Grupo 1
 */
public class AuthResponse {

	private Status status;
	private String message;
	private String error;

	public enum Status {
		SUCCESS, FAILURE
	}

	public AuthResponse() {
	}

	public AuthResponse(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	public AuthResponse(Status status, String message, String error) {
		this.status = status;
		this.message = message;
		this.error = error;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "LoginResponse [status=" + status + ", message=" + message + ", error=" + error + "]";
	}

}
