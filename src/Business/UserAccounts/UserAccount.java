package Business.UserAccounts;

import Business.Profiles.Profile;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Associates credentials with a university role profile.
 * Passwords are salted and hashed (PBKDF2), never stored in plaintext.
 * This is a classroom, in-memory credential store, NOT a production IAM service.
 * Midterm implementation: Shai Rashada-Parasram.
 */
public class UserAccount {
    private final Profile profile;
    private String username;
    private byte[] salt;
    private byte[] passwordHash;
    private boolean enabled = true;
    private long lastLoginAt;
    private long lastUpdatedAt;

    public UserAccount(Profile profile, String username, String password) {
        if (profile == null || profile.getPerson() == null) {
            throw new IllegalArgumentException("A person profile is required.");
        }
        this.profile = profile;
        changeUsername(username);
        changePassword(password);
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 128) {
            throw new IllegalArgumentException("Password must contain between 8 and 128 characters.");
        }
    }

    public final void changeUsername(String newName) {
        if (newName == null || !newName.trim().matches("[A-Za-z0-9._-]{3,30}")) {
            throw new IllegalArgumentException("Username must contain 3–30 letters, numbers, dots, underscores, or hyphens.");
        }
        username = newName.trim();
        lastUpdatedAt = System.currentTimeMillis();
    }

    /** Hash with a new random salt on every password change. */
    public final void changePassword(String password) {
        validatePassword(password);
        byte[] newSalt = new byte[16];
        new SecureRandom().nextBytes(newSalt);
        byte[] newHash = derive(password, newSalt);
        salt = newSalt;
        passwordHash = newHash;
        lastUpdatedAt = System.currentTimeMillis();
    }

    private static byte[] derive(String password, byte[] salt) {
        char[] characters = password.toCharArray();
        KeySpec spec = new PBEKeySpec(characters, salt, 120000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception error) {
            throw new IllegalStateException("Password hashing is unavailable.", error);
        } finally {
            Arrays.fill(characters, '\0');
            ((PBEKeySpec) spec).clearPassword();
        }
    }

    public boolean IsValidUser(String input, String password) {
        if (input == null || password == null || !enabled) return false;
        if (!username.equalsIgnoreCase(input.trim())) return false;
        byte[] candidate = derive(password, salt);
        return java.security.MessageDigest.isEqual(passwordHash, candidate);
    }

    public String getPersonId() { return profile.getPerson().getPersonId(); }
    public String getUserLoginName() { return username; }
    public boolean isMatch(String id) { return getPersonId().equalsIgnoreCase(id); }
    public String getRole() { return profile.getRole(); }
    public Profile getAssociatedPersonProfile() { return profile; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean newValue) { enabled = newValue; lastUpdatedAt = System.currentTimeMillis(); }
    public void markLogin() { lastLoginAt = System.currentTimeMillis(); }
    public long getLastLoginAt() { return lastLoginAt; }
    public long getLastUpdatedAt() { return lastUpdatedAt; }
    @Override public String toString() { return username; }
}
