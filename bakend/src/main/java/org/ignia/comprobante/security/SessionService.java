package org.ignia.comprobante.security;

import org.ignia.comprobante.user.Role;
import org.ignia.comprobante.user.UserModel;
import org.springframework.stereotype.Component;

@Component
public class SessionService {

    private UserModel currentUser;

    public void login(UserModel user){
        this.currentUser = user;
    }

    public void logout(){
        this.currentUser = null;
    }

    public UserModel getCurrentUser(){
        return currentUser;
    }

    public Long getCurrentUserId(){
        return currentUser.getId();
    }

    public String getCurrentUserName(){
        return currentUser.getUserName();
    }

    public Role getCurrentUserRole(){
        return currentUser.getRole();
    }

    public boolean isLoggedIn(){
        return currentUser != null;
    }
}
