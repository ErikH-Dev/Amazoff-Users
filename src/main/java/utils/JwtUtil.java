package utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

@ApplicationScoped
public class JwtUtil {
    private static final Logger LOG = Logger.getLogger(JwtUtil.class);

    @Inject
    JsonWebToken jwt;

    public String getCurrentKeycloakUserId() {
        if (jwt == null) {
            throw new SecurityException("No JWT token found");
        }
        
        String keycloakUserId = jwt.getSubject(); // This is the Keycloak user UUID
        LOG.debugf("Extracted Keycloak user ID from JWT: %s", keycloakUserId);
        return keycloakUserId;
    }

    public String getCurrentUserEmail() {
        if (jwt == null) {
            throw new SecurityException("No JWT token found");
        }
        
        return jwt.getClaim("email");
    }

    public String getCurrentUserName() {
        if (jwt == null) {
            throw new SecurityException("No JWT token found");
        }
        
        return jwt.getClaim("preferred_username");
    }

    public String getCurrentUserFirstName() {
        if (jwt == null) {
            throw new SecurityException("No JWT token found");
        }
        
        return jwt.getClaim("given_name");
    }

    public String getCurrentUserLastName() {
        if (jwt == null) {
            throw new SecurityException("No JWT token found");
        }
        
        return jwt.getClaim("family_name");
    }

    public String getCurrentStoreName() {
        if (jwt == null) {
            throw new SecurityException("No JWT token found");
        }

        return jwt.getClaim("store_name");
    }
}