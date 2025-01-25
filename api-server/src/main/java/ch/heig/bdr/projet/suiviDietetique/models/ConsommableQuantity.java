package ch.heig.bdr.projet.suiviDietetique.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Extension de la classe Consommable qui ajoute une notion de quantité.
 * Utilisée pour représenter un consommable avec sa quantité dans un repas
 * ou une portion spécifique.
 */
public class ConsommableQuantity extends Consommable {
    /** Quantité du consommable en grammes ou millilitres */
    @JsonProperty("quantite")
    private int quantite;

    /** Constructeur par défaut nécessaire pour la désérialisation JSON */
    ConsommableQuantity() {};

    /**
     * Constructeur complet pour créer un consommable avec sa quantité.
     * Hérite de tous les attributs nutritionnels de la classe Consommable
     * et ajoute la notion de quantité.
     * 
     * @param id Identifiant unique
     * @param nom Nom du consommable
     * @param type Type du consommable
     * @param calories Valeur calorique
     * @param proteines Teneur en protéines
     * @param glucides Teneur en glucides
     * @param lipides Teneur en lipides
     * @param potassium Teneur en potassium
     * @param cholesterol Teneur en cholestérol
     * @param sodium Teneur en sodium
     * @param vit_A Teneur en vitamine A
     * @param vit_C Teneur en vitamine C
     * @param vit_D Teneur en vitamine D
     * @param calcium Teneur en calcium
     * @param fer Teneur en fer
     * @param quantite Quantité du consommable en grammes ou millilitres
     */
    public ConsommableQuantity(int id, String nom, String type, int calories, float proteines,
                               float glucides, float lipides, float potassium, float cholesterol, float sodium,
                               float vit_A, float vit_C, float vit_D, float calcium, float fer, int quantite) {
        super(id, nom, type, calories, proteines, glucides, lipides, potassium, cholesterol, sodium, vit_A, vit_C, vit_D, calcium, fer);
        this.quantite = quantite;
    }

    /**
     * Récupère la quantité du consommable.
     * 
     * @return La quantité en grammes ou millilitres
     */
    public int getQuantite(){
        return quantite;
    }

    /**
     * Retourne une représentation textuelle du consommable avec sa quantité
     * et toutes ses valeurs nutritionnelles.
     * 
     * @return Une chaîne de caractères décrivant le consommable et sa quantité
     */
    @Override
    public String toString() {
        return "Consommable{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type_consommable=" + (type_consommable != null ? type_consommable.name() : "null") +
                ", calories=" + calories +
                ", proteines=" + proteines +
                ", glucides=" + glucides +
                ", lipides=" + lipides +
                ", potassium=" + potassium +
                ", cholesterol=" + cholesterol +
                ", sodium=" + sodium +
                ", vit_A=" + vit_A +
                ", vit_C=" + vit_C +
                ", vit_D=" + vit_D +
                ", calcium=" + calcium +
                ", fer=" + fer +
                ", quantite=" + quantite +
                '}';
    }
}
