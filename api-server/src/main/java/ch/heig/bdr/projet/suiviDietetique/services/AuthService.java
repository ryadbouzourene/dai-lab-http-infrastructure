package ch.heig.bdr.projet.suiviDietetique.services;

import ch.heig.bdr.projet.suiviDietetique.dao.UserDAO;
import ch.heig.bdr.projet.suiviDietetique.models.User;
import ch.heig.bdr.projet.suiviDietetique.security.Role;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * Service gérant l'authentification et l'inscription des utilisateurs.
 * Fournit des méthodes pour :
 * - L'authentification des utilisateurs
 * - L'inscription de nouveaux utilisateurs
 * - Le hachage sécurisé des mots de passe
 */
public class AuthService {
    /** DAO pour l'accès aux données des utilisateurs */
    private final UserDAO userDAO = new UserDAO();

    /**
     * Authentifie un utilisateur avec son email et son mot de passe.
     * Le mot de passe est vérifié en le comparant avec le hash stocké en base.
     * 
     * @param email L'adresse email de l'utilisateur
     * @param password Le mot de passe en clair
     * @return L'utilisateur authentifié ou null si l'authentification échoue
     */
    public User authenticate(String email, String password) {
        // Rechercher l'utilisateur par son email
        User user = userDAO.findByEmail(email);

        if (user != null) {
            // Vérifier le mot de passe et retourner l'utilisateur si correct
            return verifyPassword(password, user.getHashedPassword()) ? user : null;
        }

        return null;
    }

    /**
     * Inscrit un nouvel utilisateur dans le système.
     * Le mot de passe est haché avant d'être stocké en base.
     * 
     * @param email L'adresse email du nouvel utilisateur
     * @param password Le mot de passe en clair
     * @param role Le rôle à attribuer à l'utilisateur
     * @return true si l'inscription réussit, false si l'email existe déjà
     */
    public boolean register(String email, String password, Role role) {
        // Vérifier si l'email n'est pas déjà utilisé
        if (userDAO.findByEmail(email) != null) {
            return false; // L'utilisateur existe déjà
        }
        
        // Créer le nouvel utilisateur avec le mot de passe haché
        userDAO.insert(email, hashPassword(password), role);
        return true;
    }

    /**
     * Hache un mot de passe en utilisant l'algorithme SHA-256.
     * Le résultat est encodé en Base64 pour le stockage.
     * 
     * @param password Le mot de passe à hacher
     * @return Le hash du mot de passe encodé en Base64
     * @throws RuntimeException Si l'algorithme SHA-256 n'est pas disponible
     */
    public static String hashPassword(String password) {
        try {
            // Créer un hash SHA-256 du mot de passe
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            
            // Encoder le hash en Base64 pour le stockage
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur : Algorithme SHA-256 non trouvé.", e);
        }
    }

    /**
     * Vérifie si un mot de passe correspond à son hash stocké.
     * 
     * @param password Le mot de passe en clair à vérifier
     * @param hashedPassword Le hash du mot de passe stocké
     * @return true si le mot de passe correspond, false sinon
     */
    private static boolean verifyPassword(String password, String hashedPassword) {
        return Objects.equals(hashPassword(password), hashedPassword);
    }
}