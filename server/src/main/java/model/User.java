package model;

import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.HashMap;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public class User implements Serializable {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHashed() {
        return passwordHashed;
    }

    public void setPasswordHashed(String passwordHashed) {
        this.passwordHashed = passwordHashed;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public HashMap<String, String> getUserAliases() {
        return userAliases;
    }

    public void setUserAliases(HashMap<String, String> userAliases) {
        this.userAliases = userAliases;
    }

    public boolean isExpectedPassword(String assumedPassword) {
        return sha256Hex(assumedPassword + passwordSalt).equals(getPasswordHashed());
    }

    public void setSaltedPassword(String password) {
        setPasswordHashed(sha256Hex(password + passwordSalt));
    }

    @Id
    private String username;
    private String passwordHashed;
    private String passwordSalt;
    private HashMap<String, String> userAliases;
}
