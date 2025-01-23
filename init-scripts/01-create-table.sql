DROP SCHEMA IF EXISTS suivi_dietetique CASCADE;
CREATE SCHEMA suivi_dietetique;
COMMENT ON SCHEMA suivi_dietetique IS 'Projet BDR - Bouzourène, Christen, Haye';

-- Types ENUM nécessaires
CREATE TYPE suivi_dietetique.sexe AS ENUM ('Femme', 'Homme');
CREATE TYPE suivi_dietetique.statut_employe AS ENUM ('Actif', 'Congé', 'Retraité');
CREATE TYPE suivi_dietetique.niveau_activite AS ENUM ('Sédentaire', 'Légèrement actif', 'Modérément actif', 'Très actif', 'Extrêmement actif');
CREATE TYPE suivi_dietetique.type_repas AS ENUM ('Petit-déjeuner', 'Déjeuner', 'Dîner', 'Collation');
CREATE TYPE suivi_dietetique.type_consommable AS ENUM ('Aliment', 'Boisson');
CREATE TYPE suivi_dietetique.type_specialite AS ENUM ('Nutrition sportive', 'Diabète', 'Allaitement', 'Allergies et intolérances', 'Addictions', 'Alimentation végétalienne', 'Alimentation végétariennne', 'Oncologie');
CREATE TYPE suivi_dietetique.type_certificat AS ENUM ('Soins généraux', 'Soins intensifs', 'Soins anesthésie', 'Soins oncologique', 'BLS-AED', 'Diabétologie' );
CREATE TYPE suivi_dietetique.role_utilisateur AS ENUM ('Diététicien', 'Infirmier', 'Patient','Admin');

-- Table principale : Personne
CREATE TABLE suivi_dietetique.personne
(
    noSS          INTEGER PRIMARY KEY,
    nom           TEXT,
    prenom        TEXT,
    dateNaissance DATE,
    sexe          suivi_dietetique.sexe
);

-- Table Service
CREATE TABLE suivi_dietetique.service
(
    id  INTEGER PRIMARY KEY,
    nom TEXT
);

-- Table Employe
CREATE TABLE suivi_dietetique.employe
(
    noSS         INTEGER PRIMARY KEY,
    id_service   INTEGER,
    dateEmbauche DATE                            NOT NULL,
    statut       suivi_dietetique.statut_employe NOT NULL,
    FOREIGN KEY (noSS) REFERENCES suivi_dietetique.personne (noSS) ON DELETE CASCADE,
    FOREIGN KEY (id_service) REFERENCES suivi_dietetique.service (id) ON DELETE CASCADE
);

-- Table Dieteticien
CREATE TABLE suivi_dietetique.dieteticien
(
    noSS        INTEGER PRIMARY KEY,
    specialites suivi_dietetique.type_specialite[],
    FOREIGN KEY (noSS) REFERENCES suivi_dietetique.employe (noSS) ON DELETE CASCADE
);

-- Table Infirmier
CREATE TABLE suivi_dietetique.infirmier
(
    noSS        INTEGER PRIMARY KEY,
    certificats suivi_dietetique.type_certificat[],
    FOREIGN KEY (noSS) REFERENCES suivi_dietetique.employe (noSS) ON DELETE CASCADE
);

-- Table Patient
CREATE TABLE suivi_dietetique.patient
(
    noSS             INTEGER PRIMARY KEY,
    noSS_dieteticien INTEGER,
    dateAdmission    DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (noSS) REFERENCES suivi_dietetique.personne (noSS) ON DELETE CASCADE,
    FOREIGN KEY (noSS_dieteticien) REFERENCES suivi_dietetique.dieteticien (noSS) ON DELETE SET NULL
);

-- Table Donnees_sante
CREATE TABLE suivi_dietetique.donnees_sante
(
    noSS_patient           INTEGER,
    date                   timestamptz DEFAULT NOW(),
    taille                 INTEGER                          NOT NULL CHECK (taille > 0 AND taille < 300),
    poids                  INTEGER                          NOT NULL CHECK (poids > 0 AND poids < 1000),
    tourDeTaille           INTEGER                          NOT NULL CHECK (tourDeTaille > 0 AND tourDeTaille < 300),
    niveauActivitePhysique suivi_dietetique.niveau_activite NOT NULL,
    PRIMARY KEY (noSS_patient, date),
    FOREIGN KEY (noSS_patient) REFERENCES suivi_dietetique.patient (noSS) ON DELETE CASCADE
);

-- Table Aliment Allergene
CREATE TABLE suivi_dietetique.aliment_allergene
(
    nom TEXT PRIMARY KEY
);

-- Table Repas

CREATE TABLE suivi_dietetique.repas
(
    noSS_patient      INTEGER,
    noSS_infirmier    INTEGER,
    date_consommation timestamptz DEFAULT NOW(),
    type              suivi_dietetique.type_repas NOT NULL,
    remarque          TEXT,
    PRIMARY KEY (noSS_patient, date_consommation),
    FOREIGN KEY (noSS_patient) REFERENCES suivi_dietetique.patient (noSS) ON DELETE CASCADE,
    FOREIGN KEY (noSS_infirmier) REFERENCES suivi_dietetique.infirmier (noSS) ON DELETE SET NULL
);

-- Type Composite pour Glucides
CREATE TYPE suivi_dietetique.type_glucides AS
(
    fibres FLOAT,
    sucre  FLOAT
);

-- Type Composite pour Lipides
CREATE TYPE suivi_dietetique.type_lipides AS
(
    total        FLOAT,
    gras_satures FLOAT
);

-- Table Consommable
-- Valeurs nutritionnelles pour 100g || 100 ml
CREATE TABLE suivi_dietetique.consommable
(
    id          SERIAL PRIMARY KEY,
    nom         TEXT                              NOT NULL,
    type        suivi_dietetique.type_consommable NOT NULL,
    calories    INTEGER,
    proteines   FLOAT,
    glucides    suivi_dietetique.type_glucides,
    lipides     suivi_dietetique.type_lipides,
    potassium   FLOAT,
    cholesterol FLOAT,
    sodium      FLOAT,
    vit_A       FLOAT,
    vit_C       FLOAT,
    vit_D       FLOAT,
    calcium     FLOAT,
    fer         FLOAT
);

-- Table Quantité Repas-Consommable
CREATE TABLE suivi_dietetique.quantite_repas_consommable
(
    id_consommable    SERIAL,
    noSS_patient      INTEGER,
    date_consommation timestamptz,
    quantite          INTEGER NOT NULL CHECK (quantite > 0),
    PRIMARY KEY (id_consommable, noSS_patient, date_consommation),
    FOREIGN KEY (id_consommable) REFERENCES suivi_dietetique.consommable (id) ON DELETE RESTRICT,
    FOREIGN KEY (noSS_patient, date_consommation)
        REFERENCES suivi_dietetique.repas (noSS_patient, date_consommation) ON DELETE CASCADE
);

-- Table Est_Allergique
CREATE TABLE suivi_dietetique.est_allergique
(
    noSS_patient  INTEGER,
    nom_allergene TEXT,
    PRIMARY KEY (noSS_patient, nom_allergene),
    FOREIGN KEY (noSS_patient) REFERENCES suivi_dietetique.patient (noSS) ON DELETE CASCADE,
    FOREIGN KEY (nom_allergene) REFERENCES suivi_dietetique.aliment_allergene (nom) ON DELETE RESTRICT
);

-- Table Peut_Contenir
CREATE TABLE suivi_dietetique.peut_contenir
(
    id_consommable SERIAL,
    nom_allergene  TEXT,
    PRIMARY KEY (id_consommable, nom_allergene),
    FOREIGN KEY (id_consommable) REFERENCES suivi_dietetique.consommable (id) ON DELETE CASCADE,
    FOREIGN KEY (nom_allergene) REFERENCES suivi_dietetique.aliment_allergene (nom) ON DELETE RESTRICT
);

-- Table Objectif
CREATE TABLE suivi_dietetique.objectif
(
    numero           SERIAL,
    noSS_patient     INTEGER,
    noSS_dieteticien INTEGER,
    dateDebut        DATE NOT NULL,
    dateFin          DATE NOT NULL CHECK (dateFin >= dateDebut),
    titre            TEXT NOT NULL,
    reussi           boolean,
    commentaire      TEXT,
    PRIMARY KEY (noSS_patient, numero),
    FOREIGN KEY (noSS_patient) REFERENCES suivi_dietetique.patient (noSS) ON DELETE CASCADE,
    FOREIGN KEY (noSS_dieteticien) REFERENCES suivi_dietetique.dieteticien (noSS) ON DELETE SET NULL
);

-- Table Utilisateur
CREATE TABLE suivi_dietetique.utilisateur
(
    email        TEXT,
    mdpHache     TEXT        NOT NULL,
    role         suivi_dietetique.role_utilisateur,
    dateCreation timestamptz NOT NULL DEFAULT CURRENT_DATE,
    noSS         INTEGER,
    PRIMARY KEY (email),
    FOREIGN KEY (noSS) REFERENCES suivi_dietetique.personne (noSS) ON DELETE CASCADE
);
