package ch.heig.bdr.projet.suiviDietetique.services;

import ch.heig.bdr.projet.suiviDietetique.dao.PersonneDAO;
import ch.heig.bdr.projet.suiviDietetique.dao.UserDAO;
import ch.heig.bdr.projet.suiviDietetique.models.User;

public class PersonneService {
    private final PersonneDAO personneDAO = new PersonneDAO();
    private final UserDAO userDAO = new UserDAO();
    private final UserService userService = new UserService();

    public boolean deleteOnePersonne(String noss) {
        boolean result = personneDAO.deletePersonne(noss);

        User user = userService.findByNoss(noss);

        if (result && user != null) {
            return userDAO.deleteUser(user.getUsername());
        }

        return result;
    }
}
