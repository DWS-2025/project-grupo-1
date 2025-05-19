package es.codeurjc.web.security.jwt;

import java.time.Duration;

/**
 * Enum representing the types of JWT tokens used in the application.
 * <p>
 * Each token type specifies its lifetime and the name of the cookie
 * in which it is stored.
 * </p>
 *
 * <ul>
 *   <li>{@link #ACCESS} - Short-lived access token, typically used for authentication.</li>
 *   <li>{@link #REFRESH} - Long-lived refresh token, used to obtain new access tokens.</li>
 * </ul>
 *
 * @author Grupo 1
 */
public enum TokenType {

    ACCESS(Duration.ofMinutes(5), "AuthToken"),
    REFRESH(Duration.ofDays(7), "RefreshToken");

    /**
     * Token lifetime in seconds
     */
    public final Duration duration;
    public final String cookieName;

    TokenType(Duration duration, String cookieName) {
        this.duration = duration;
        this.cookieName = cookieName;
    }
}