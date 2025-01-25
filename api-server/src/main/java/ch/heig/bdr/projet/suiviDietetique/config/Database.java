package ch.heig.bdr.projet.suiviDietetique.config;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Classe de configuration de la base de données.
 * Gère la connexion à la base de données PostgreSQL pour l'application de suivi diététique.
 * Utilise un schéma dédié 'suivi_dietetique' dans la base de données 'bdr'.
 */
public class Database {
    /** URL de connexion à la base de données PostgreSQL */
    private static final String URL = "jdbc:postgresql://localhost:5432/bdr?currentSchema=suivi_dietetique";
    
    /** Nom d'utilisateur pour la connexion à la base de données */
    private static final String USER = "bdr";
    
    /** Mot de passe pour la connexion à la base de données */
    private static final String PASSWORD = "bdr";

    /**
     * Établit et retourne une connexion à la base de données.
     * 
     * @return Une connexion active à la base de données PostgreSQL
     * @throws Exception Si la connexion à la base de données échoue
     */
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}