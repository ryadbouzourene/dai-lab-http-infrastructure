package ch.heig.bdr.projet.suiviDietetique.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe représentant un aliment ou une boisson consommable.
 * Contient toutes les informations nutritionnelles d'un consommable,
 * y compris les macronutriments, micronutriments et vitamines.
 */
public class Consommable {
    /** Identifiant unique du consommable */
    @JsonProperty("id")
    protected int id;

    /** Nom du consommable */
    @JsonProperty("nom")
    protected String nom;

    /** Type du consommable (ex: ALIMENT, BOISSON) */
    @JsonProperty("type_consommable")
    protected TypeConsommable type_consommable;

    /** Valeur calorique en kcal */
    @JsonProperty("calories")
    protected int calories;

    /** Teneur en protéines en grammes */
    @JsonProperty("proteines")
    protected float proteines;

    /** Teneur en glucides en grammes */
    @JsonProperty("glucides")
    protected float glucides;

    /** Teneur en lipides en grammes */
    @JsonProperty("lipides")
    protected float lipides;

    /** Teneur en potassium en milligrammes */
    @JsonProperty("potassium")
    protected float potassium;

    /** Teneur en cholestérol en milligrammes */
    @JsonProperty("cholesterol")
    protected float cholesterol;

    /** Teneur en sodium en milligrammes */
    @JsonProperty("sodium")
    protected float sodium;

    /** Teneur en vitamine A en microgrammes */
    @JsonProperty("vit_A")
    protected float vit_A;

    /** Teneur en vitamine C en milligrammes */
    @JsonProperty("vit_C")
    protected float vit_C;

    /** Teneur en vitamine D en microgrammes */
    @JsonProperty("vit_D")
    protected float vit_D;

    /** Teneur en calcium en milligrammes */
    @JsonProperty("calcium")
    protected float calcium;

    /** Teneur en fer en milligrammes */
    @JsonProperty("fer")
    protected float fer;

    /** Constructeur par défaut nécessaire pour la désérialisation JSON */
    public Consommable() {}

    /**
     * Constructeur principal pour créer un consommable avec ses valeurs nutritionnelles de base.
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
     */
    public Consommable(int id, String nom, String type, int calories, float proteines,
                       float glucides, float lipides, float potassium, float cholesterol, float sodium,
                       float vit_A, float vit_C, float vit_D, float calcium,
                       float fer) {
        this.id = id;
        this.nom = nom;
        this.type_consommable = TypeConsommable.type(type);
        this.calories = calories;
        this.proteines = proteines;
        this.glucides = glucides;
        this.lipides = lipides;
        this.potassium = potassium;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.vit_A = vit_A;
        this.vit_C = vit_C;
        this.vit_D = vit_D;
        this.calcium = calcium;
        this.fer = fer;
    }

    /**
     * Constructeur alternatif permettant de spécifier séparément les fibres et le sucre.
     * Les glucides totaux sont calculés comme la somme des fibres et du sucre.
     * 
     * @param id Identifiant unique
     * @param nom Nom du consommable
     * @param type Type du consommable
     * @param calories Valeur calorique
     * @param proteines Teneur en protéines
     * @param fibres Teneur en fibres
     * @param sucre Teneur en sucre
     * @param lipidesTotal Teneur totale en lipides
     * @param grasSatures Teneur en graisses saturées (non utilisé actuellement)
     * @param potassium Teneur en potassium
     * @param cholesterol Teneur en cholestérol
     * @param sodium Teneur en sodium
     * @param vit_A Teneur en vitamine A
     * @param vit_C Teneur en vitamine C
     * @param vit_D Teneur en vitamine D
     * @param calcium Teneur en calcium
     * @param fer Teneur en fer
     */
    public Consommable(int id, String nom, String type, int calories, float proteines,
                       float fibres, float sucre, float lipidesTotal, float grasSatures,
                       float potassium, float cholesterol, float sodium, float vit_A,
                       float vit_C, float vit_D, float calcium, float fer) {
        this.id = id;
        this.nom = nom;
        this.type_consommable = TypeConsommable.type(type);
        this.calories = calories;
        this.proteines = proteines;
        this.glucides = fibres + sucre;  // Combine fibres et sucre pour les glucides totaux
        this.lipides = lipidesTotal;    // Total des lipides
        this.potassium = potassium;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.vit_A = vit_A;
        this.vit_C = vit_C;
        this.vit_D = vit_D;
        this.calcium = calcium;
        this.fer = fer;
    }

    /**
     * Récupère l'identifiant unique du consommable.
     * 
     * @return L'identifiant du consommable
     */
    public int getId(){
        return id;
    }

    /**
     * Retourne une représentation textuelle du consommable avec toutes ses valeurs nutritionnelles.
     * 
     * @return Une chaîne de caractères décrivant le consommable
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
                '}';
    }
}
