package Business;

import Business.UserAccounts.UserAccount;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Session authority. Every privileged Business operation calls requireAdmin
 * rather than trusting the UI to hide forbidden buttons.
 * Midterm implementation: Shai Rashada-Parasram.
 */
public final class AuthService {
    private final Business business;
    private final Map<String, UserAccount> activeSessions = new HashMap<>();

    public AuthService(Business business) { this.business = business; }

    /** Return a random session token or null for invalid credentials. */
    public synchronized String login(String name, String password) {
        UserAccount account = business.getUserAccountDirectory().AuthenticateUser(name, password);
        if (account == null) return null;
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, account);
        return token;
    }

    public synchronized void logout(String token) {
        if (token != null) activeSessions.remove(token);
    }

    public synchronized UserAccount requireUser(String token) {
        UserAccount account = token == null ? null : activeSessions.get(token);
        if (account == null || !account.isEnabled()
                || business.getUserAccountDirectory().findByUsername(account.getUserLoginName()) != account) {
            activeSessions.remove(token);
            throw new SecurityException("You must log in with an active account.");
        }
        return account;
    }

    public UserAccount requireAdmin(String token) {
        UserAccount account = requireUser(token);
        if (!"Admin".equalsIgnoreCase(account.getRole())) {
            throw new SecurityException("Administrator access required.");
        }
        return account;
    }

    public void invalidate(UserAccount account) {
        synchronized (this) { activeSessions.values().removeIf(item -> item == account); }
    }
}
