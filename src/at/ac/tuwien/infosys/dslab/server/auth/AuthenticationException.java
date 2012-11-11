package at.ac.tuwien.infosys.dslab.server.auth;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
