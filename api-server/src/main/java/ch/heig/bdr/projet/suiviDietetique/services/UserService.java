package ch.heig.bdr.projet.suiviDietetique.services;


import ch.heig.bdr.projet.suiviDietetique.dao.UserDAO;

import ch.heig.bdr.projet.suiviDietetique.models.User;
import ch.heig.bdr.projet.suiviDietetique.security.Role;;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public void createUser(String email, String hashedPassword, Role role){
        userDAO.insert(email,hashedPassword,role);
    }

    public boolean deleteOneUser(String email){
        return userDAO.deleteUser(email);
    }

    public User findByNoss(String noss) {
        return userDAO.findByNoss(noss);
    }
}
