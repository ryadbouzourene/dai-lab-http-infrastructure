package ch.heig.bdr.projet.suiviDietetique.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe représentant un allergène dans le système.
 * Cette classe est utilisée pour modéliser les allergènes auxquels les patients
 * peuvent être allergiques. Elle est sérialisable en JSON pour l'API.
 */
public class Allergene {
    /** Nom de l'allergène, tel que stocké dans la base de données */
    protected String nom_allergene;

    /**
     * Constructeur pour créer un nouvel allergène.
     * 
     * @param nom_allergene Le nom de l'allergène
     */
    public Allergene(String nom_allergene) {
        this.nom_allergene = nom_allergene;
    }

    /**
     * Récupère le nom de l'allergène.
     * 
     * @return Le nom de l'allergène
     */
    public String getNomAllergene() {
        return this.nom_allergene;
    }
}
