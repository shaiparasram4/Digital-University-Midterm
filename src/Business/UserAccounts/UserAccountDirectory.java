package Business.UserAccounts;

import Business.Profiles.Profile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Account registry adapted from the provided access-control skeleton.
 * Midterm implementation: Shai Rashada-Parasram. */
public class UserAccountDirectory {
    private final List<UserAccount> accounts = new ArrayList<>();

    public UserAccount newUserAccount(Profile profile, String name, String password) {
        if (findByUsername(name) != null) throw new IllegalArgumentException("Username already exists.");
        if (findUserAccount(profile.getPerson().getPersonId()) != null) {
            throw new IllegalArgumentException("This person already has a login account.");
        }
        UserAccount account = new UserAccount(profile, name, password);
        accounts.add(account);
        return account;
    }

    public UserAccount findUserAccount(String personId) {
        if (personId == null) return null;
        for (UserAccount item : accounts) if (item.isMatch(personId)) return item;
        return null;
    }

    public UserAccount findByUsername(String name) {
        if (name == null) return null;
        for (UserAccount account : accounts) {
            if (account.getUserLoginName().equalsIgnoreCase(name.trim())) return account;
        }
        return null;
    }

    public UserAccount AuthenticateUser(String name, String password) {
        if (name == null || password == null) return null;
        UserAccount account = findByUsername(name);
        if (account == null || !account.IsValidUser(name, password)) return null;
        account.markLogin();
        return account;
    }

    public boolean remove(UserAccount account) { return accounts.remove(account); }
    public ArrayList<UserAccount> getUserAccountList() { return new ArrayList<>(accounts); }
    public List<UserAccount> viewAccounts() { return Collections.unmodifiableList(accounts); }
}
