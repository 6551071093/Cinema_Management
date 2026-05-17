package com.cinema.service;

import com.cinema.dao.AccountDAO;
import com.cinema.entity.Account;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private AccountDAO accountDAO = new AccountDAO();
    private static Account loggedInAccount;

    public Account login(String username, String password) {
        Account account = accountDAO.findByUsername(username);
        if (account != null) {
            if (BCrypt.checkpw(password, account.getPassword())) {
                loggedInAccount = account;
                return account;
            }
        }
        return null;
    }

    public static Account getLoggedInAccount() {
        return loggedInAccount;
    }

    public static void logout() {
        loggedInAccount = null;
    }
}
