package com.gestionstages.util;

import com.gestionstages.model.Utilisateur;

public class SessionManager {
    private static Utilisateur currentUser;
    
    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }
    
    public static Utilisateur getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
