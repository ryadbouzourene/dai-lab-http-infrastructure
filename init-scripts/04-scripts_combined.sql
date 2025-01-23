DROP SCHEMA IF EXISTS suivi_dietetique CASCADE;
CREATE SCHEMA suivi_dietetique;
COMMENT ON SCHEMA suivi_dietetique IS 'Projet BDR - Bouzourène, Christen, Haye';

-- Insertion des tables
-------------------------------------------------------------------------------------------------------------------------------------------
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

-- Insertion des valeurs
------------------------------------------------------------------------------------------------------------------
-- ==========================================
-- 1) PERSONNE
-- ==========================================
INSERT INTO suivi_dietetique.personne (noSS, nom, prenom, dateNaissance, sexe)
VALUES (100000001, 'Dupont', 'Jean', '1985-04-12', 'Homme'),
       (100000002, 'Durand', 'Sophie', '1990-06-25', 'Femme'),
       (100000003, 'Martin', 'Alice', '1975-09-15', 'Femme'),
       (100000004, 'Morel', 'Luc', '1982-11-03', 'Homme'),
       (100000005, 'Bourquin', 'Emma', '2000-03-10', 'Femme'),
       (100000006, 'Vuille', 'Marc', '1995-08-22', 'Homme'),
       (100000007, 'Rossi', 'Carla', '1989-07-18', 'Femme'),
       (100000008, 'Muller', 'Johann', '1978-12-02', 'Homme'),
       (100000009, 'Lombard', 'Chloe', '1998-01-01', 'Femme'),
       (100000010, 'Germain', 'Eric', '1970-02-14', 'Homme'),
       (100000011, 'Tissot', 'Jeanne', '1988-09-01', 'Femme'),
       (100000012, 'Berger', 'Pauline', '1999-11-23', 'Femme');

-- ==========================================
-- 2) SERVICE
-- ==========================================
INSERT INTO suivi_dietetique.service (id, nom)
VALUES (1, 'Service Diététique A'),
       (2, 'Service Diététique B'),
       (3, 'Service Diététique C'),
       (4, 'Service Diététique D'),
       (5, 'Service Diététique E'),
       (6, 'Service Diététique F'),
       (7, 'Service Diététique G'),
       (8, 'Service Diététique H');

-- ==========================================
-- 3) EMPLOYE
-- ==========================================
INSERT INTO suivi_dietetique.employe (noSS, id_service, dateEmbauche, statut)
VALUES (100000001, 1, '2020-01-01', 'Actif'),
       (100000002, 2, '2021-03-10', 'Actif'),
       (100000003, 3, '2015-06-15', 'Actif'),
       (100000004, 1, '2018-11-20', 'Actif'),
       (100000007, 4, '2022-01-05', 'Actif'),
       (100000008, 5, '2016-04-12', 'Actif'),
       (100000009, 6, '2023-02-14', 'Actif'),
       (100000010, 7, '2010-09-01', 'Actif'),
       (100000011, 8, '2019-07-01', 'Actif');

-- ==========================================
-- 4) DIETETICIEN
-- ==========================================
INSERT INTO suivi_dietetique.dieteticien (noSS, specialites)
VALUES (100000001, '{Nutrition sportive}'::suivi_dietetique.type_specialite[]),
       (100000003, '{Diabète}'::suivi_dietetique.type_specialite[]);

-- ==========================================
-- 5) INFIRMIER
-- ==========================================
INSERT INTO suivi_dietetique.infirmier (noSS, certificats)
VALUES (100000002, '{"Soins généraux","BLS-AED"}'::suivi_dietetique.type_certificat[]),
       (100000004, '{"Soins généraux"}'::suivi_dietetique.type_certificat[]);
-- ==========================================
-- 6) PATIENT
-- ==========================================
INSERT INTO suivi_dietetique.patient (noSS, noSS_dieteticien, dateadmission)
VALUES (100000005, 100000001, '2024-01-01'),
       (100000006, 100000003, '2024-01-01'),
       (100000007, 100000001, '2024-09-15'),
       (100000008, 100000003, '2024-11-01'),
       (100000009, 100000001, '2024-12-01'),
       (100000010, 100000001, '2024-10-10'),
       (100000011, 100000003, '2024-07-05'),
       (100000012, 100000003, '2024-08-25');

-- ==========================================
-- 7) DONNEES_SANTE
-- ==========================================
INSERT INTO suivi_dietetique.donnees_sante
(noSS_patient, date, taille, poids, tourDeTaille, niveauActivitePhysique)
VALUES (100000005, '2024-01-01 10:00:00', 165, 60, 70, 'Modérément actif'),
       (100000005, '2024-11-30 10:00:00', 165, 58, 68, 'Modérément actif'),
       (100000006, '2024-01-01 10:00:00', 180, 80, 85, 'Légèrement actif'),
       (100000007, '2024-09-15 09:00:00', 170, 65, 75, 'Modérément actif'),
       (100000008, '2024-11-01 10:30:00', 175, 85, 90, 'Très actif'),
       (100000009, '2024-12-01 09:30:00', 168, 62, 72, 'Légèrement actif'),
       (100000010, '2024-10-10 08:15:00', 172, 78, 82, 'Très actif'),
       (100000011, '2024-07-05 14:00:00', 160, 55, 65, 'Légèrement actif'),
       (100000012, '2024-08-25 09:45:00', 169, 59, 70, 'Modérément actif');

-- ==========================================
-- 8) ALIMENT_ALLERGENE
-- ==========================================
INSERT INTO suivi_dietetique.aliment_allergene (nom)
VALUES ('Gluten'),
       ('Lactose'),
       ('Arachides'),
       ('Fruits de mer'),
       ('Soja'),
       ('Noix'),
       ('Céleri'),
       ('Sésame');

-- ==========================================
-- 9) REPAS
-- ==========================================
INSERT INTO suivi_dietetique.repas
    (noSS_patient, noSS_infirmier, date_consommation, type, remarque)
VALUES
    /* =============================================================== */
    /* ============ PATIENT 100000005 ============ */
    /* =============================================================== */
    -- JOUR 1 (2025-01-16)
    (100000005, 100000004, '2025-01-16 08:00:00', 'Petit-déjeuner', 'Matin difficile, peu d appetit'),
    (100000005, NULL, '2025-01-16 12:30:00', 'Déjeuner', 'A eu du mal a digerer'),
    (100000005, 100000002, '2025-01-16 16:30:00', 'Collation', 'n a pas reussi a finir son snack'),
    (100000005, NULL, '2025-01-16 19:30:00', 'Dîner', 'Repas complet, dessert sucre'),

    -- JOUR 2 (2025-01-17)
    (100000005, NULL, '2025-01-17 08:00:00', 'Petit-déjeuner', 'Classique, mais peu d appetit'),
    (100000005, 100000004, '2025-01-17 12:30:00', 'Déjeuner', 'Tres copieux, un peu trop sale'),
    (100000005, NULL, '2025-01-17 16:30:00', 'Collation', 'Aucun souci particulier'),
    (100000005, 100000002, '2025-01-17 19:30:00', 'Dîner', 'Peu d appetit ce soir'),

    -- JOUR 3 (2025-01-18)
    (100000005, NULL, '2025-01-18 08:00:00', 'Petit-déjeuner', 'Matin normal, rien a signaler'),
    (100000005, 100000004, '2025-01-18 12:30:00', 'Déjeuner', 'Plat equilibre, manque un peu de sel'),
    (100000005, NULL, '2025-01-18 16:30:00', 'Collation', 'Fatigue, n a pas tout mange'),
    (100000005, 100000002, '2025-01-18 19:30:00', 'Dîner', 'Termine sans probleme'),

    /* =============================================================== */
    /* ============ PATIENT 100000006 ============ */
    /* =============================================================== */
    -- JOUR 1 (2025-01-16)
    (100000006, 100000002, '2025-01-16 08:00:00', 'Petit-déjeuner', 'Repas leger, cafe apprecie'),
    (100000006, NULL, '2025-01-16 12:30:00', 'Déjeuner', 'S est regale, plat un peu sucre'),
    (100000006, 100000004, '2025-01-16 16:30:00', 'Collation', 'Digestion lente'),
    (100000006, NULL, '2025-01-16 19:30:00', 'Dîner', 'n a pas reussi a finir son plat'),

    -- JOUR 2 (2025-01-17)
    (100000006, NULL, '2025-01-17 08:00:00', 'Petit-déjeuner', 'Classique, pain  fruit'),
    (100000006, 100000002, '2025-01-17 12:30:00', 'Déjeuner', 'Repas complet, aucun souci'),
    (100000006, NULL, '2025-01-17 16:30:00', 'Collation', 'Un peu fatigue, a bu beaucoup d eau'),
    (100000006, 100000004, '2025-01-17 19:30:00', 'Dîner', 'A eu du mal a digerer'),

    -- JOUR 3 (2025-01-18)
    (100000006, 100000002, '2025-01-18 08:00:00', 'Petit-déjeuner', 'Oeuf dur, bien passe'),
    (100000006, NULL, '2025-01-18 12:30:00', 'Déjeuner', 'Plat correct, manque de legumes'),
    (100000006, 100000004, '2025-01-18 16:30:00', 'Collation', 'Satisfait, legere fatigue'),
    (100000006, NULL, '2025-01-18 19:30:00', 'Dîner', 'n a pas voulu de dessert'),

    /* =============================================================== */
    /* ============ PATIENT 100000007 ============ */
    /* =============================================================== */
    -- JOUR 1
    (100000007, 100000004, '2025-01-16 08:00:00', 'Petit-déjeuner', 'Repas matinal classique'),
    (100000007, NULL, '2025-01-16 12:30:00', 'Déjeuner', 'Aucun probleme, appetit normal'),
    (100000007, 100000002, '2025-01-16 16:30:00', 'Collation', 'A un peu grignote, rien de plus'),
    (100000007, NULL, '2025-01-16 19:30:00', 'Dîner', 'n a pas reussi a tout finir'),

    -- JOUR 2
    (100000007, NULL, '2025-01-17 08:00:00', 'Petit-déjeuner', 'Petit-déjeuner rapide'),
    (100000007, 100000004, '2025-01-17 12:30:00', 'Déjeuner', 'Tres copieux, plat sale'),
    (100000007, NULL, '2025-01-17 16:30:00', 'Collation', 'Digestion difficile'),
    (100000007, 100000002, '2025-01-17 19:30:00', 'Dîner', 'A fini son assiette sans souci'),

    -- JOUR 3
    (100000007, 100000004, '2025-01-18 08:00:00', 'Petit-déjeuner', 'Peu d appetit, a juste bu un cafe'),
    (100000007, NULL, '2025-01-18 12:30:00', 'Déjeuner', 'Repas equilibre, a tout mange'),
    (100000007, 100000002, '2025-01-18 16:30:00', 'Collation', 'N a grignote que partiellement'),
    (100000007, NULL, '2025-01-18 19:30:00', 'Dîner', 'Termine sans probleme, un peu sale'),

    /* =============================================================== */
    /* ============ PATIENT 100000008 ============ */
    /* =============================================================== */
    -- JOUR 1
    (100000008, 100000002, '2025-01-16 08:00:00', 'Petit-déjeuner', 'Repas matinal leger'),
    (100000008, NULL, '2025-01-16 12:30:00', 'Déjeuner', 'Rien a signaler, un repas normal'),
    (100000008, 100000004, '2025-01-16 16:30:00', 'Collation', 'Petit gouter, a bien aime'),
    (100000008, NULL, '2025-01-16 19:30:00', 'Dîner', 'Repas complet, un peu fatigue'),

    -- JOUR 2
    (100000008, NULL, '2025-01-17 08:00:00', 'Petit-déjeuner', 'Manque d appetit ce matin'),
    (100000008, 100000002, '2025-01-17 12:30:00', 'Déjeuner', 'S est plaint de legeres nausees'),
    (100000008, NULL, '2025-01-17 16:30:00', 'Collation', 'A repris un peu de force'),
    (100000008, 100000004, '2025-01-17 19:30:00', 'Dîner', 'Plat apprecie, sauce un peu grasse'),

    -- JOUR 3
    (100000008, NULL, '2025-01-18 08:00:00', 'Petit-déjeuner', 'A termine son bol de cafe'),
    (100000008, 100000002, '2025-01-18 12:30:00', 'Déjeuner', 'Beaucoup de lentilles, un peu trop'),
    (100000008, NULL, '2025-01-18 16:30:00', 'Collation', 'Collation rapide, a bu beaucoup d eau'),
    (100000008, 100000004, '2025-01-18 19:30:00', 'Dîner', 'Plat equilibre, rien a ajouter'),

    /* =============================================================== */
    /* ============ PATIENT 100000009 ============ */
    /* =============================================================== */
    -- JOUR 1
    (100000009, 100000002, '2025-01-16 08:00:00', 'Petit-déjeuner', 'Trop presse, repas vite avale'),
    (100000009, NULL, '2025-01-16 12:30:00', 'Déjeuner', 'A tout mange, bon appetit'),
    (100000009, 100000004, '2025-01-16 16:30:00', 'Collation', 'Rien de special, juste une boisson'),
    (100000009, NULL, '2025-01-16 19:30:00', 'Dîner', 'n a pas reussi a finir son plat'),

    -- JOUR 2
    (100000009, 100000002, '2025-01-17 08:00:00', 'Petit-déjeuner', 'Classique, pain et boisson chaude'),
    (100000009, NULL, '2025-01-17 12:30:00', 'Déjeuner', 'Plat epice, digere sans souci'),
    (100000009, 100000004, '2025-01-17 16:30:00', 'Collation', 'A pris un dessert sucre'),
    (100000009, NULL, '2025-01-17 19:30:00', 'Dîner', 'Rien a signaler'),

    -- JOUR 3
    (100000009, 100000002, '2025-01-18 08:00:00', 'Petit-déjeuner', 'Oeuf dur, cafe, pas de probleme'),
    (100000009, NULL, '2025-01-18 12:30:00', 'Déjeuner', 'A fini son assiette rapidement'),
    (100000009, 100000004, '2025-01-18 16:30:00', 'Collation', 'Fatigue, collation incomplete'),
    (100000009, NULL, '2025-01-18 19:30:00', 'Dîner', 'Beaucoup de proteines, a apprecie'),

    /* =============================================================== */
    /* ============ PATIENT 100000010 ============ */
    /* =============================================================== */
    -- JOUR 1
    (100000010, 100000004, '2025-01-16 08:00:00', 'Petit-déjeuner', 'Matin normal, aucun souci'),
    (100000010, NULL, '2025-01-16 12:30:00', 'Déjeuner', 'Repas energetique'),
    (100000010, 100000002, '2025-01-16 16:30:00', 'Collation', 'Collation sucree, a apprecie'),
    (100000010, NULL, '2025-01-16 19:30:00', 'Dîner', 'Riche en fibres, a tout mange'),

    -- JOUR 2
    (100000010, NULL, '2025-01-17 08:00:00', 'Petit-déjeuner', 'Peu d appetit, a juste bu un cafe'),
    (100000010, 100000004, '2025-01-17 12:30:00', 'Déjeuner', 'Repas un peu trop gras'),
    (100000010, NULL, '2025-01-17 16:30:00', 'Collation', 'A grignote des biscuits'),
    (100000010, 100000002, '2025-01-17 19:30:00', 'Dîner', 'Plat a base de poisson, a bien digere'),

    -- JOUR 3
    (100000010, 100000004, '2025-01-18 08:00:00', 'Petit-déjeuner', 'Oeuf dur, pain complet'),
    (100000010, NULL, '2025-01-18 12:30:00', 'Déjeuner', 'Poulet  riz, satisfaisant'),
    (100000010, 100000002, '2025-01-18 16:30:00', 'Collation', 'n a pas termine son yaourt'),
    (100000010, NULL, '2025-01-18 19:30:00', 'Dîner', 'Pas d appetit ce soir'),

    /* =============================================================== */
    /* ============ PATIENT 100000011 ============ */
    /* =============================================================== */
    -- JOUR 1
    (100000011, 100000004, '2025-01-16 08:00:00', 'Petit-déjeuner', 'Cafe vite bu, rien d autre'),
    (100000011, NULL, '2025-01-16 12:30:00', 'Déjeuner', 'Plat classique, pas de remarque'),
    (100000011, 100000002, '2025-01-16 16:30:00', 'Collation', 'A voulu un fruit supplementaire'),
    (100000011, NULL, '2025-01-16 19:30:00', 'Dîner', 'Carottes et saumon, un peu trop cru'),

    -- JOUR 2
    (100000011, NULL, '2025-01-17 08:00:00', 'Petit-déjeuner', 'Banane  pain, correct'),
    (100000011, 100000004, '2025-01-17 12:30:00', 'Déjeuner', 'Lentilles, bon apport en fibres'),
    (100000011, NULL, '2025-01-17 16:30:00', 'Collation', 'Fromage blanc, a laisse la moitie'),
    (100000011, 100000002, '2025-01-17 19:30:00', 'Dîner', 'Digestion un peu compliquee'),

    -- JOUR 3
    (100000011, 100000004, '2025-01-18 08:00:00', 'Petit-déjeuner', 'Oeuf dur, cafe, s est depeche'),
    (100000011, NULL, '2025-01-18 12:30:00', 'Déjeuner', 'Poulet  riz, repas standard'),
    (100000011, 100000002, '2025-01-18 16:30:00', 'Collation', 'Petit gouter, rien de special'),
    (100000011, NULL, '2025-01-18 19:30:00', 'Dîner', 'Avocat, a bien apprecie'),

    /* =============================================================== */
    /* ============ PATIENT 100000012 ============ */
    /* =============================================================== */
    -- JOUR 1
    (100000012, 100000002, '2025-01-16 08:00:00', 'Petit-déjeuner', 'Matin rapide, cafe et pain'),
    (100000012, NULL, '2025-01-16 12:30:00', 'Déjeuner', 'Poulet  lentilles, satisfaisant'),
    (100000012, 100000004, '2025-01-16 16:30:00', 'Collation', 'Pomme et yaourt, rien d autre'),
    (100000012, NULL, '2025-01-16 19:30:00', 'Dîner', 'Saumon  riz, a tout mange'),

    -- JOUR 2
    (100000012, NULL, '2025-01-17 08:00:00', 'Petit-déjeuner', 'Banane  pain complet'),
    (100000012, 100000002, '2025-01-17 12:30:00', 'Déjeuner', 'Lentilles  carotte, plat un peu sec'),
    (100000012, 100000004, '2025-01-17 16:30:00', 'Collation', 'Fromage blanc  chocolat, a bien aime'),
    (100000012, NULL, '2025-01-17 19:30:00', 'Dîner', 'Riz et saumon, plat leger'),

    -- JOUR 3
    (100000012, 100000002, '2025-01-18 08:00:00', 'Petit-déjeuner', 'Oeuf dur, pain complet, OK'),
    (100000012, NULL, '2025-01-18 12:30:00', 'Déjeuner', 'Poulet  riz, eau a volonte'),
    (100000012, 100000004, '2025-01-18 16:30:00', 'Collation', 'Pomme  chocolat, petit plaisir'),
    (100000012, NULL, '2025-01-18 19:30:00', 'Dîner', 'Saumon  avocat, a tout termine');


-- ==========================================
-- 10) CONSOMMABLE
-- ==========================================
INSERT INTO suivi_dietetique.consommable
(nom, type, calories, proteines, glucides, lipides, potassium, cholesterol, sodium, vit_A, vit_C, vit_D, calcium, fer)
VALUES ('Pomme', 'Aliment', 52, 0.3, ROW (14,10), ROW (0.2,0), 120, 0, 1, 1, 5, 0, 6, 0.1),                 --1
       ('Pain complet', 'Aliment', 250, 10, ROW (50,5), ROW (1,0.3), 250, 0, 400, 0, 0, 0, 20, 2),          --2
       ('Yaourt', 'Aliment', 100, 5, ROW (10,5), ROW (3,2), 200, 10, 50, 5, 5, 10, 100, 0.2),               --3
       ('Eau', 'Boisson', 0, 0, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 0),--4
       ('Banane', 'Aliment', 89, 1.1, ROW (23,12), ROW (0.3,0.1), 358, 0, 1, 3, 9, 0, 5, 0.3),--5
       ('Café', 'Boisson', 2, 0.1, NULL, NULL, 49, 0, 5, 0, 0, 0, 2, 0),--6
       ('Poulet grillé', 'Aliment', 165, 31, ROW (0,0), ROW (3.6,1), 256, 85, 74, 0, 0, 0, 12, 1),--7
       ('Saumon', 'Aliment', 200, 20, ROW (0,0), ROW (12,2.8), 320, 55, 45, 35, 0, 10, 10, 0.5),--8
       ('Lentilles cuites', 'Aliment', 116, 9, ROW (20,1.8), ROW (0.3,0.05), 369, 0, 2, 8, 1.5, 0, 19, 3.3),--9
       ('Riz complet cuit', 'Aliment', 111, 2.6, ROW (23,0.4), ROW (0.9,0.2), 43, 0, 5, 0, 0, 0, 10, 0.4),--10
       ('Avocat', 'Aliment', 160, 2, ROW (9,0.7), ROW (15,2.1), 485, 0, 7, 7, 10, 0, 12, 0.6),--11
       ('Chocolat noir 70%', 'Aliment', 600, 7.8, ROW (46,24), ROW (43,25), 715, 0, 20, 2, 0, 0, 73, 11.9),--12
       ('Lait de soja', 'Boisson', 54, 3.3, ROW (6,3), ROW (1.8,0.2), 118, 0, 50, 120, 0, 2, 25, 0.6),--13
       ('Fromage blanc 0%', 'Aliment', 45, 8, ROW (3,3), ROW (0.2,0.1), 140, 1, 50, 15, 0, 0, 80, 0.1),--14
       ('Oeuf dur', 'Aliment', 155, 13, ROW (1.1,1.1), ROW (11,3.3), 126, 373, 124, 149, 0, 2, 50, 1.2),--15
       ('Carotte', 'Aliment', 41, 0.9, ROW (10,4.7), ROW (0.2,0.04), 320, 0, 69, 835, 6, 0, 33, 0.3),--16
       ('Pâtes complètes', 'Aliment', 158, 6, ROW (31,2.5), ROW (1.1,0.2), 54, 0, 7, 1, 0, 0, 20, 1.2);
--17

-- ==========================================
-- 11) QUANTITE_REPAS_CONSOMMABLE
-- ==========================================
INSERT INTO suivi_dietetique.quantite_repas_consommable
    (id_consommable, noSS_patient, date_consommation, quantite)
VALUES

    /* =============================================================== */
    /* ============ PATIENT 100000005 ============ */
    /* =============================================================== */
    -- JOUR 1 (2025-01-16)
    -- Petit-déjeuner (08:00)
    (2, 100000005, '2025-01-16 08:00:00', 60),   -- Pain complet
    (6, 100000005, '2025-01-16 08:00:00', 200),  -- Café

    -- Déjeuner (12:30)
    (7, 100000005, '2025-01-16 12:30:00', 150),  -- Poulet grillé
    (16, 100000005, '2025-01-16 12:30:00', 80),  -- Carotte
    (4, 100000005, '2025-01-16 12:30:00', 250),  -- Eau

    -- Collation (16:30)
    (1, 100000005, '2025-01-16 16:30:00', 120),  -- Pomme
    (12, 100000005, '2025-01-16 16:30:00', 30),  -- Chocolat noir

    -- Dîner (19:30)
    (8, 100000005, '2025-01-16 19:30:00', 140),  -- Saumon
    (10, 100000005, '2025-01-16 19:30:00', 120), -- Riz complet cuit
    (4, 100000005, '2025-01-16 19:30:00', 250),  -- Eau

    -- JOUR 2 (2025-01-17)
    -- Petit-déjeuner (08:00)
    (2, 100000005, '2025-01-17 08:00:00', 60),   -- Pain complet
    (13, 100000005, '2025-01-17 08:00:00', 200), -- Lait de soja
    (5, 100000005, '2025-01-17 08:00:00', 100),  -- Banane

    -- Déjeuner (12:30)
    (8, 100000005, '2025-01-17 12:30:00', 130),  -- Saumon
    (9, 100000005, '2025-01-17 12:30:00', 150),  -- Lentilles cuites
    (4, 100000005, '2025-01-17 12:30:00', 250),  -- Eau

    -- Collation (16:30)
    (14, 100000005, '2025-01-17 16:30:00', 100), -- Fromage blanc 0%
    (4, 100000005, '2025-01-17 16:30:00', 200),  -- Eau

    -- Dîner (19:30)
    (7, 100000005, '2025-01-17 19:30:00', 160),  -- Poulet grillé
    (10, 100000005, '2025-01-17 19:30:00', 120), -- Riz complet
    (4, 100000005, '2025-01-17 19:30:00', 250),  -- Eau

    -- JOUR 3 (2025-01-18)
    -- Petit-déjeuner (08:00)
    (15, 100000005, '2025-01-18 08:00:00', 55),  -- Oeuf dur (1 oeuf)
    (2, 100000005, '2025-01-18 08:00:00', 60),   -- Pain complet

    -- Déjeuner (12:30)
    (7, 100000005, '2025-01-18 12:30:00', 150),  -- Poulet grillé
    (9, 100000005, '2025-01-18 12:30:00', 150),  -- Lentilles cuites
    (16, 100000005, '2025-01-18 12:30:00', 60),  -- Carotte
    (4, 100000005, '2025-01-18 12:30:00', 250),  -- Eau

    -- Collation (16:30)
    (1, 100000005, '2025-01-18 16:30:00', 100),  -- Pomme
    (12, 100000005, '2025-01-18 16:30:00', 20),  -- Chocolat noir
    (4, 100000005, '2025-01-18 16:30:00', 150),  -- Eau

    -- Dîner (19:30)
    (8, 100000005, '2025-01-18 19:30:00', 160),  -- Saumon
    (11, 100000005, '2025-01-18 19:30:00', 50),  -- Avocat
    (10, 100000005, '2025-01-18 19:30:00', 120), -- Riz complet
    (4, 100000005, '2025-01-18 19:30:00', 250),  -- Eau


    /* =============================================================== */
    /* ============ PATIENT 100000006 ============ */
    /* =============================================================== */
    -- JOUR 1 (2025-01-16)
    -- Petit-déjeuner (08:00)
    (2, 100000006, '2025-01-16 08:00:00', 50),   -- Pain complet
    (6, 100000006, '2025-01-16 08:00:00', 200),  -- Café

    -- Déjeuner (12:30)
    (7, 100000006, '2025-01-16 12:30:00', 150),  -- Poulet grillé
    (10, 100000006, '2025-01-16 12:30:00', 120), -- Riz complet
    (4, 100000006, '2025-01-16 12:30:00', 250),  -- Eau

    -- Collation (16:30)
    (1, 100000006, '2025-01-16 16:30:00', 100),  -- Pomme
    (12, 100000006, '2025-01-16 16:30:00', 30),  -- Chocolat noir

    -- Dîner (19:30)
    (8, 100000006, '2025-01-16 19:30:00', 140),  -- Saumon
    (9, 100000006, '2025-01-16 19:30:00', 150),  -- Lentilles cuites
    (4, 100000006, '2025-01-16 19:30:00', 250),  -- Eau

    -- JOUR 2 (2025-01-17)
    -- Petit-déjeuner (08:00)
    (15, 100000006, '2025-01-17 08:00:00', 55),  -- Oeuf dur
    (5, 100000006, '2025-01-17 08:00:00', 100),  -- Banane

    -- Déjeuner (12:30)
    (8, 100000006, '2025-01-17 12:30:00', 160),  -- Saumon
    (16, 100000006, '2025-01-17 12:30:00', 50),  -- Carotte
    (4, 100000006, '2025-01-17 12:30:00', 250),  -- Eau

    -- Collation (16:30)
    (14, 100000006, '2025-01-17 16:30:00', 100), -- Fromage blanc
    (3, 100000006, '2025-01-17 16:30:00', 125),  -- Yaourt

    -- Dîner (19:30)
    (7, 100000006, '2025-01-17 19:30:00', 160),  -- Poulet grillé
    (9, 100000006, '2025-01-17 19:30:00', 200),  -- Lentilles cuites
    (10, 100000006, '2025-01-17 19:30:00', 100), -- Riz complet
    (4, 100000006, '2025-01-17 19:30:00', 250),  -- Eau

    -- JOUR 3 (2025-01-18)
    -- Petit-déjeuner (08:00)
    (2, 100000006, '2025-01-18 08:00:00', 50),   -- Pain complet
    (13, 100000006, '2025-01-18 08:00:00', 200), -- Lait de soja

    -- Déjeuner (12:30)
    (8, 100000006, '2025-01-18 12:30:00', 150),  -- Saumon
    (9, 100000006, '2025-01-18 12:30:00', 150),  -- Lentilles cuites
    (11, 100000006, '2025-01-18 12:30:00', 60),  -- Avocat
    (4, 100000006, '2025-01-18 12:30:00', 250),  -- Eau

    -- Collation (16:30)
    (1, 100000006, '2025-01-18 16:30:00', 120),  -- Pomme
    (12, 100000006, '2025-01-18 16:30:00', 30),  -- Chocolat noir
    (5, 100000006, '2025-01-18 16:30:00', 80),   -- Banane

    -- Dîner (19:30)
    (7, 100000006, '2025-01-18 19:30:00', 150),  -- Poulet grillé
    (16, 100000006, '2025-01-18 19:30:00', 70),  -- Carotte
    (4, 100000006, '2025-01-18 19:30:00', 250),  -- Eau


    /* =============================================================== */
    /* ============ PATIENT 100000007 ============ */
    /* =============================================================== */
    -- JOUR 1 (2025-01-16)
    -- Petit-déjeuner (08:00)
    (6, 100000007, '2025-01-16 08:00:00', 180),

    -- Déjeuner (12:30)
    (7, 100000007, '2025-01-16 12:30:00', 150),
    (10, 100000007, '2025-01-16 12:30:00', 120),
    (4, 100000007, '2025-01-16 12:30:00', 250),

    -- Collation (16:30)
    (1, 100000007, '2025-01-16 16:30:00', 100),
    (12, 100000007, '2025-01-16 16:30:00', 40),

    -- Dîner (19:30)
    (8, 100000007, '2025-01-16 19:30:00', 140),
    (9, 100000007, '2025-01-16 19:30:00', 150),
    (4, 100000007, '2025-01-16 19:30:00', 250),

    -- JOUR 2 (2025-01-17)
    -- Petit-déjeuner (08:00)
    (13, 100000007, '2025-01-17 08:00:00', 200),
    (5, 100000007, '2025-01-17 08:00:00', 120),

    -- Déjeuner (12:30)
    (7, 100000007, '2025-01-17 12:30:00', 160),
    (16, 100000007, '2025-01-17 12:30:00', 70),
    (4, 100000007, '2025-01-17 12:30:00', 250),

    -- Collation (16:30)
    (3, 100000007, '2025-01-17 16:30:00', 125),
    (12, 100000007, '2025-01-17 16:30:00', 20),

    -- Dîner (19:30)
    (8, 100000007, '2025-01-17 19:30:00', 150),
    (10, 100000007, '2025-01-17 19:30:00', 100),
    (4, 100000007, '2025-01-17 19:30:00', 250),

    -- JOUR 3 (2025-01-18)
    -- Petit-déjeuner (08:00)
    (6, 100000007, '2025-01-18 08:00:00', 200),

    -- Déjeuner (12:30)
    (7, 100000007, '2025-01-18 12:30:00', 150),
    (9, 100000007, '2025-01-18 12:30:00', 160),
    (4, 100000007, '2025-01-18 12:30:00', 250),

    -- Collation (16:30)
    (1, 100000007, '2025-01-18 16:30:00', 100),
    (14, 100000007, '2025-01-18 16:30:00', 90),

    -- Dîner (19:30)
    (8, 100000007, '2025-01-18 19:30:00', 130),
    (11, 100000007, '2025-01-18 19:30:00', 60),
    (4, 100000007, '2025-01-18 19:30:00', 250),


    /* =============================================================== */
    /* ============ PATIENT 100000008 ============ */
    /* =============================================================== */
    -- JOUR 1 (2025-01-16)
    -- Petit-déjeuner
    (2, 100000008, '2025-01-16 08:00:00', 50),
    (6, 100000008, '2025-01-16 08:00:00', 200),

    -- Déjeuner
    (7, 100000008, '2025-01-16 12:30:00', 140),
    (10, 100000008, '2025-01-16 12:30:00', 120),
    (4, 100000008, '2025-01-16 12:30:00', 200),

    -- Collation
    (1, 100000008, '2025-01-16 16:30:00', 100),
    (3, 100000008, '2025-01-16 16:30:00', 125),

    -- Dîner
    (8, 100000008, '2025-01-16 19:30:00', 150),
    (9, 100000008, '2025-01-16 19:30:00', 180),
    (4, 100000008, '2025-01-16 19:30:00', 250),

    -- JOUR 2 (2025-01-17)
    -- Petit-déjeuner
    (2, 100000008, '2025-01-17 08:00:00', 60),
    (5, 100000008, '2025-01-17 08:00:00', 120),
    (4, 100000008, '2025-01-17 08:00:00', 200),

    -- Déjeuner
    (7, 100000008, '2025-01-17 12:30:00', 150),
    (9, 100000008, '2025-01-17 12:30:00', 150),
    (16, 100000008, '2025-01-17 12:30:00', 70),

    -- Collation
    (14, 100000008, '2025-01-17 16:30:00', 110),
    (12, 100000008, '2025-01-17 16:30:00', 30),

    -- Dîner
    (8, 100000008, '2025-01-17 19:30:00', 150),
    (10, 100000008, '2025-01-17 19:30:00', 120),
    (4, 100000008, '2025-01-17 19:30:00', 250),

    -- JOUR 3 (2025-01-18)
    -- Petit-déjeuner
    (15, 100000008, '2025-01-18 08:00:00', 55),
    (2, 100000008, '2025-01-18 08:00:00', 50),

    -- Déjeuner
    (7, 100000008, '2025-01-18 12:30:00', 140),
    (9, 100000008, '2025-01-18 12:30:00', 200),
    (4, 100000008, '2025-01-18 12:30:00', 250),

    -- Collation
    (1, 100000008, '2025-01-18 16:30:00', 80),
    (3, 100000008, '2025-01-18 16:30:00', 125),

    -- Dîner
    (8, 100000008, '2025-01-18 19:30:00', 160),
    (11, 100000008, '2025-01-18 19:30:00', 70),
    (4, 100000008, '2025-01-18 19:30:00', 250),


    /* =============================================================== */
    /* ============ PATIENT 100000009 ============ */
    /* =============================================================== */
    -- JOUR 1
    (2, 100000009, '2025-01-16 08:00:00', 55),
    (6, 100000009, '2025-01-16 08:00:00', 180),

    (7, 100000009, '2025-01-16 12:30:00', 150),
    (9, 100000009, '2025-01-16 12:30:00', 150),
    (4, 100000009, '2025-01-16 12:30:00', 200),

    (1, 100000009, '2025-01-16 16:30:00', 100),
    (12, 100000009, '2025-01-16 16:30:00', 30),

    (8, 100000009, '2025-01-16 19:30:00', 150),
    (10, 100000009, '2025-01-16 19:30:00', 120),
    (4, 100000009, '2025-01-16 19:30:00', 250),

    -- JOUR 2
    (2, 100000009, '2025-01-17 08:00:00', 60),
    (13, 100000009, '2025-01-17 08:00:00', 200),
    (5, 100000009, '2025-01-17 08:00:00', 100),

    (7, 100000009, '2025-01-17 12:30:00', 140),
    (9, 100000009, '2025-01-17 12:30:00', 150),
    (4, 100000009, '2025-01-17 12:30:00', 250),

    (3, 100000009, '2025-01-17 16:30:00', 125),
    (12, 100000009, '2025-01-17 16:30:00', 20),

    (8, 100000009, '2025-01-17 19:30:00', 160),
    (10, 100000009, '2025-01-17 19:30:00', 120),
    (4, 100000009, '2025-01-17 19:30:00', 250),

    -- JOUR 3
    (15, 100000009, '2025-01-18 08:00:00', 55),
    (2, 100000009, '2025-01-18 08:00:00', 50),

    (8, 100000009, '2025-01-18 12:30:00', 130),
    (9, 100000009, '2025-01-18 12:30:00', 160),
    (4, 100000009, '2025-01-18 12:30:00', 250),

    (1, 100000009, '2025-01-18 16:30:00', 100),
    (12, 100000009, '2025-01-18 16:30:00', 20),

    (7, 100000009, '2025-01-18 19:30:00', 150),
    (10, 100000009, '2025-01-18 19:30:00', 120),
    (4, 100000009, '2025-01-18 19:30:00', 250),


    /* =============================================================== */
    /* ============ PATIENT 100000010 ============ */
    /* =============================================================== */
    -- JOUR 1
    (2, 100000010, '2025-01-16 08:00:00', 50),
    (6, 100000010, '2025-01-16 08:00:00', 200),

    (7, 100000010, '2025-01-16 12:30:00', 140),
    (10, 100000010, '2025-01-16 12:30:00', 120),
    (4, 100000010, '2025-01-16 12:30:00', 250),

    (1, 100000010, '2025-01-16 16:30:00', 120),
    (3, 100000010, '2025-01-16 16:30:00', 125),

    (8, 100000010, '2025-01-16 19:30:00', 150),
    (9, 100000010, '2025-01-16 19:30:00', 160),
    (4, 100000010, '2025-01-16 19:30:00', 200),

    -- JOUR 2
    (2, 100000010, '2025-01-17 08:00:00', 60),
    (5, 100000010, '2025-01-17 08:00:00', 80),
    (4, 100000010, '2025-01-17 08:00:00', 250),

    (7, 100000010, '2025-01-17 12:30:00', 140),
    (9, 100000010, '2025-01-17 12:30:00', 150),
    (16, 100000010, '2025-01-17 12:30:00', 70),

    (14, 100000010, '2025-01-17 16:30:00', 120),
    (12, 100000010, '2025-01-17 16:30:00', 40),

    (8, 100000010, '2025-01-17 19:30:00', 160),
    (10, 100000010, '2025-01-17 19:30:00', 120),
    (4, 100000010, '2025-01-17 19:30:00', 200),

    -- JOUR 3
    (15, 100000010, '2025-01-18 08:00:00', 55),
    (2, 100000010, '2025-01-18 08:00:00', 50),

    (7, 100000010, '2025-01-18 12:30:00', 140),
    (9, 100000010, '2025-01-18 12:30:00', 180),
    (4, 100000010, '2025-01-18 12:30:00', 250),

    (3, 100000010, '2025-01-18 16:30:00', 125),
    (12, 100000010, '2025-01-18 16:30:00', 20),

    (8, 100000010, '2025-01-18 19:30:00', 150),
    (10, 100000010, '2025-01-18 19:30:00', 120),
    (4, 100000010, '2025-01-18 19:30:00', 250),


    /* =============================================================== */
    /* ============ PATIENT 100000011 ============ */
    /* =============================================================== */
    -- JOUR 1
    (2, 100000011, '2025-01-16 08:00:00', 60),
    (6, 100000011, '2025-01-16 08:00:00', 200),

    (7, 100000011, '2025-01-16 12:30:00', 150),
    (10, 100000011, '2025-01-16 12:30:00', 120),
    (4, 100000011, '2025-01-16 12:30:00', 250),

    (1, 100000011, '2025-01-16 16:30:00', 100),
    (3, 100000011, '2025-01-16 16:30:00', 125),

    (8, 100000011, '2025-01-16 19:30:00', 150),
    (16, 100000011, '2025-01-16 19:30:00', 80),
    (4, 100000011, '2025-01-16 19:30:00', 250),

    -- JOUR 2
    (2, 100000011, '2025-01-17 08:00:00', 50),
    (5, 100000011, '2025-01-17 08:00:00', 100),
    (4, 100000011, '2025-01-17 08:00:00', 200),

    (7, 100000011, '2025-01-17 12:30:00', 140),
    (9, 100000011, '2025-01-17 12:30:00', 150),
    (4, 100000011, '2025-01-17 12:30:00', 250),

    (14, 100000011, '2025-01-17 16:30:00', 80),
    (12, 100000011, '2025-01-17 16:30:00', 30),

    (8, 100000011, '2025-01-17 19:30:00', 150),
    (9, 100000011, '2025-01-17 19:30:00', 160),
    (4, 100000011, '2025-01-17 19:30:00', 250),

    -- JOUR 3
    (15, 100000011, '2025-01-18 08:00:00', 55),
    (2, 100000011, '2025-01-18 08:00:00', 50),

    (7, 100000011, '2025-01-18 12:30:00', 140),
    (10, 100000011, '2025-01-18 12:30:00', 120),
    (4, 100000011, '2025-01-18 12:30:00', 250),

    (1, 100000011, '2025-01-18 16:30:00', 100),
    (12, 100000011, '2025-01-18 16:30:00', 25),

    (8, 100000011, '2025-01-18 19:30:00', 160),
    (11, 100000011, '2025-01-18 19:30:00', 70),
    (4, 100000011, '2025-01-18 19:30:00', 200),


    /* =============================================================== */
    /* ============ PATIENT 100000012 ============ */
    /* =============================================================== */
    -- JOUR 1
    (2, 100000012, '2025-01-16 08:00:00', 60),
    (6, 100000012, '2025-01-16 08:00:00', 180),

    (7, 100000012, '2025-01-16 12:30:00', 150),
    (9, 100000012, '2025-01-16 12:30:00', 150),
    (4, 100000012, '2025-01-16 12:30:00', 200),

    (1, 100000012, '2025-01-16 16:30:00', 120),
    (3, 100000012, '2025-01-16 16:30:00', 100),

    (8, 100000012, '2025-01-16 19:30:00', 160),
    (10, 100000012, '2025-01-16 19:30:00', 120),
    (4, 100000012, '2025-01-16 19:30:00', 200),

    -- JOUR 2
    (2, 100000012, '2025-01-17 08:00:00', 60),
    (5, 100000012, '2025-01-17 08:00:00', 100),
    (4, 100000012, '2025-01-17 08:00:00', 200),

    (7, 100000012, '2025-01-17 12:30:00', 150),
    (9, 100000012, '2025-01-17 12:30:00', 180),
    (16, 100000012, '2025-01-17 12:30:00', 70),

    (14, 100000012, '2025-01-17 16:30:00', 90),
    (12, 100000012, '2025-01-17 16:30:00', 20),

    (8, 100000012, '2025-01-17 19:30:00', 150),
    (10, 100000012, '2025-01-17 19:30:00', 120),
    (4, 100000012, '2025-01-17 19:30:00', 250),

    -- JOUR 3
    (15, 100000012, '2025-01-18 08:00:00', 55),
    (2, 100000012, '2025-01-18 08:00:00', 60),

    (7, 100000012, '2025-01-18 12:30:00', 160),
    (10, 100000012, '2025-01-18 12:30:00', 120),
    (4, 100000012, '2025-01-18 12:30:00', 250),

    (1, 100000012, '2025-01-18 16:30:00', 100),
    (12, 100000012, '2025-01-18 16:30:00', 25),

    (8, 100000012, '2025-01-18 19:30:00', 150),
    (11, 100000012, '2025-01-18 19:30:00', 60),
    (4, 100000012, '2025-01-18 19:30:00', 200);


-- ==========================================
-- 12) EST_ALLERGIQUE
-- ==========================================
INSERT INTO suivi_dietetique.est_allergique (noSS_patient, nom_allergene)
VALUES (100000005, 'Lactose'),
       (100000006, 'Arachides'),
       (100000007, 'Gluten'),
       (100000008, 'Fruits de mer'),
       (100000012, 'Noix');

-- ==========================================
-- 13) PEUT_CONTENIR
-- ==========================================
INSERT INTO suivi_dietetique.peut_contenir (id_consommable, nom_allergene)
VALUES (3, 'Lactose'),
       (2, 'Gluten'),
       (13, 'Soja'),
       (17, 'Gluten');

-- ==========================================
-- 14) OBJECTIF
-- ==========================================
INSERT INTO suivi_dietetique.objectif
(noSS_patient, noSS_dieteticien, dateDebut, dateFin, titre, reussi, commentaire)
VALUES (100000005, 100000001, '2024-01-01', '2024-06-01', 'Perdre 2kg pour un mariage', NULL, ''),
       (100000006, 100000003, '2024-02-01', '2024-12-31', 'Augmenter la masse musculaire', NULL, ''),
       (100000007, 100000001, '2024-03-01', '2024-09-01', 'Réduire la consommation de sucre', NULL, ''),
       (100000008, 100000003, '2024-06-01', '2024-10-01', 'Stabiliser le poids', TRUE, 'Bravo !'),
       (100000009, 100000001, '2024-09-01', '2025-01-01', 'Améliorer l´équilibre alimentaire', NULL, ''),
       (100000012, 100000001, '2024-10-01', '2025-03-01', 'Maintenir un poids stable et sain', NULL, '');

-- ADMIN password : pwd
INSERT
INTO suivi_dietetique.utilisateur
VALUES ('test@com', 'oRWenfNnDVSdBFJFMmKfVHfOt97sm0XkfowAlQbsssg=', 'Admin');

-- ADMIN
-- password : pwd
INSERT
INTO suivi_dietetique.utilisateur(email, mdphache, role)
VALUES ('admin@test.com', 'oRWenfNnDVSdBFJFMmKfVHfOt97sm0XkfowAlQbsssg=', 'Admin');

-- DIETETICIEN
-- password : pwd
INSERT
INTO suivi_dietetique.utilisateur(email, mdphache, role, noss)
VALUES ('dieteticien@test.com', 'oRWenfNnDVSdBFJFMmKfVHfOt97sm0XkfowAlQbsssg=', 'Diététicien', 100000001);

-- INFIRMIER
-- password : pwd
INSERT
INTO suivi_dietetique.utilisateur(email, mdphache, role, noss)
VALUES ('infirmier@test.com', 'oRWenfNnDVSdBFJFMmKfVHfOt97sm0XkfowAlQbsssg=', 'Infirmier', 100000002);

-- DIETETICIEN
-- password : pwd
INSERT
INTO suivi_dietetique.utilisateur(email, mdphache, role, noss)
VALUES ('patient@test.com', 'oRWenfNnDVSdBFJFMmKfVHfOt97sm0XkfowAlQbsssg=', 'Patient', 100000005);

-- Création vues, triggers, fonctions
---------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------
-- VIEWS
------------------------------------------------------------------------------------

-- Vue de la table personne
CREATE OR REPLACE VIEW personne_vue AS
SELECT
    p.noss,
    p.nom,
    p.prenom,
    p.datenaissance,
    p.sexe,
    u.email
FROM suivi_dietetique.personne p
LEFT JOIN suivi_dietetique.utilisateur u
       ON u.noss = p.noss;

-- Vue de la table patient
CREATE OR REPLACE VIEW patient_vue AS
SELECT
    pat.noss,
    pv.nom,
    pv.prenom,
    pv.datenaissance,
    pv.sexe,
    pv.email,
    pat.noss_dieteticien,
    pat.dateAdmission
FROM suivi_dietetique.patient pat
INNER JOIN personne_vue pv
        ON pat.noss = pv.noss;

-- Vue de la table employé
CREATE OR REPLACE VIEW employe_vue AS
SELECT
    pv.noss,
    pv.nom,
    pv.prenom,
    pv.datenaissance,
    pv.sexe,
    pv.email,
    e.id_service,
    s.nom AS nom_service,
    e.dateembauche,
    e.statut
FROM suivi_dietetique.employe e
INNER JOIN personne_vue pv
        ON e.noss = pv.noss
INNER JOIN suivi_dietetique.service s
        ON e.id_service = s.id;

-- Vue de la table infirmier
CREATE OR REPLACE VIEW infirmier_vue AS
SELECT
    ev.noss,
    ev.nom,
    ev.prenom,
    ev.datenaissance,
    ev.sexe,
    ev.email,
    ev.id_service,
    ev.nom_service,
    ev.dateembauche,
    ev.statut,
    i.certificats
FROM suivi_dietetique.infirmier i
INNER JOIN employe_vue ev
        ON i.noss = ev.noss;

-- Vue de la table diététicien
CREATE OR REPLACE VIEW dieteticien_vue AS
SELECT
    ev.noss,
    ev.nom,
    ev.prenom,
    ev.datenaissance,
    ev.sexe,
    ev.email,
    ev.id_service,
    ev.nom_service,
    ev.dateembauche,
    ev.statut,
    d.specialites
FROM suivi_dietetique.dieteticien d
INNER JOIN employe_vue ev
        ON d.noss = ev.noss;

-- Vue des objectifs hebdomadaires
CREATE OR REPLACE VIEW objectif_hebdomadaire AS
SELECT
    pv.nom,
    pv.prenom,
    o.commentaire,
    o.reussi
FROM patient_vue pv
INNER JOIN suivi_dietetique.objectif o
        ON pv.noss = o.noss_patient
GROUP BY
    pv.nom,
    pv.prenom,
    o.commentaire,
    o.reussi;

------------------------------------------------------------------------------------
-- Consommables par repas
------------------------------------------------------------------------------------

DROP VIEW IF EXISTS suivi_dietetique.consommables_repas;

CREATE OR REPLACE VIEW suivi_dietetique.consommables_repas AS
SELECT
    c.id,
    q.noSS_patient,
    q.date_consommation,
    c.nom  AS consommable_nom,
    c.type AS type_consommable,
    q.quantite,

    -- total_proteines
    ROUND(
        COALESCE((c.proteines / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_proteines,

    -- total_glucides : ((fibres + sucre) / 100) * quantite
    ROUND(
        COALESCE(
            (((c.glucides).fibres + (c.glucides).sucre) / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_glucides,

    -- total_lipides : (lipides.total / 100) * quantite
    ROUND(
        COALESCE(((c.lipides).total / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_lipides,

    -- total_calories : (calories / 100) * quantite
    ROUND(
        COALESCE((c.calories / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_calories,

    -- total_hydratation : si c.type = 'Boisson' alors q.quantite, sinon 0
    COALESCE(
        CASE WHEN c.type = 'Boisson' THEN q.quantite ELSE 0 END,
        0
    ) AS total_hydratation,

    -- total_potassium : (potassium / 100) * quantite
    ROUND(
        COALESCE((c.potassium / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_potassium,

    -- total_cholesterol : (cholesterol / 100) * quantite
    ROUND(
        COALESCE((c.cholesterol / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_cholesterol,

    -- total_sodium : (sodium / 100) * quantite
    ROUND(
        COALESCE((c.sodium / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_sodium,

    -- total_vit_A : (vit_A / 100) * quantite
    ROUND(
        COALESCE((c.vit_A / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_vit_A,

    -- total_vit_C : (vit_C / 100) * quantite
    ROUND(
        COALESCE((c.vit_C / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_vit_C,

    -- total_vit_D : (vit_D / 100) * quantite
    ROUND(
        COALESCE((c.vit_D / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_vit_D,

    -- total_calcium : (calcium / 100) * quantite
    ROUND(
        COALESCE((c.calcium / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_calcium,

    -- total_fer : (fer / 100) * quantite
    ROUND(
        COALESCE((c.fer / 100.0) * q.quantite, 0)::numeric,
        2
    ) AS total_fer

FROM suivi_dietetique.quantite_repas_consommable q
JOIN suivi_dietetique.consommable c
     ON q.id_consommable = c.id;

------------------------------------------------------------------------------------
-- Vue des statistiques de santé actuelles par patient
------------------------------------------------------------------------------------

CREATE OR REPLACE VIEW suivi_dietetique.stats_sante_actuelles AS
SELECT
    ds.noSS_patient,
    pw.nom    AS patient_nom,
    pw.prenom AS patient_prenom,
    ds.date,
    ds.taille,
    ds.poids,
    ds.tourDeTaille,
    ds.niveauActivitePhysique,
    ROUND(ds.poids / (ds.taille / 100.0)^2, 2) AS IMC
FROM suivi_dietetique.donnees_sante ds
JOIN patient_vue pw
     ON ds.noSS_patient = pw.noSS
WHERE ds.date = (
    SELECT MAX(date)
    FROM suivi_dietetique.donnees_sante d
    WHERE d.noSS_patient = ds.noSS_patient
);

------------------------------------------------------------------------------------
-- TRIGGERS
------------------------------------------------------------------------------------

-- Trigger permettant de vérifier les allergies d'un patient
CREATE OR REPLACE FUNCTION verifier_allergies()
RETURNS TRIGGER
AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM suivi_dietetique.est_allergique ea
        JOIN suivi_dietetique.peut_contenir pc
             ON ea.nom_allergene = pc.nom_allergene
        WHERE ea.noSS_patient = NEW.noSS_patient
          AND pc.id_consommable = NEW.id_consommable
    ) THEN
        RAISE EXCEPTION 'Patient % est allergique à un ingrédient de ce consommable.', NEW.noSS_patient;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tg_verifier_allergies
BEFORE INSERT
ON suivi_dietetique.quantite_repas_consommable
FOR EACH ROW
EXECUTE FUNCTION verifier_allergies();

-- Trigger permettant de valider l'utilisateur
CREATE OR REPLACE FUNCTION valider_utilisateur()
RETURNS TRIGGER
AS $$
BEGIN
    IF NEW.role = 'Diététicien'
       AND NOT EXISTS (
           SELECT 1
           FROM suivi_dietetique.dieteticien
           WHERE noSS = NEW.noSS
       ) THEN
        RAISE EXCEPTION 'NoSS % est associé à aucun diététicien', NEW.noSS;

    ELSIF NEW.role = 'Infirmier'
          AND NOT EXISTS (
              SELECT 1
              FROM suivi_dietetique.infirmier
              WHERE noSS = NEW.noSS
          ) THEN
        RAISE EXCEPTION 'NoSS % est associé à aucun infirmier', NEW.noSS;

    ELSIF NEW.role = 'Patient'
          AND NOT EXISTS (
              SELECT 1
              FROM suivi_dietetique.patient
              WHERE noSS = NEW.noSS
          ) THEN
        RAISE EXCEPTION 'NoSS % est associé à aucun patient', NEW.noSS;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tg_valider_utilisateur
BEFORE INSERT OR UPDATE
ON suivi_dietetique.utilisateur
FOR EACH ROW
EXECUTE FUNCTION valider_utilisateur();

------------------------------------------------------------------------------------
-- FONCTIONS
------------------------------------------------------------------------------------

-- Fonction permettant de retourner les statistiques nutritionnelles
-- de tous les patients dans un intervalle de jours
CREATE OR REPLACE FUNCTION get_stats(days_interval INT)
RETURNS TABLE
(
    noSS_patient      INTEGER,
    patient_nom       TEXT,
    patient_prenom    TEXT,
    total_proteines   NUMERIC,
    total_glucides    NUMERIC,
    total_lipides     NUMERIC,
    total_hydratation BIGINT,
    total_calories    BIGINT,
    moyenne_calories  NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.noSS_patient,
        p.nom AS patient_nom,
        p.prenom AS patient_prenom,
        ROUND(SUM(c.proteines * q.quantite / 100)::NUMERIC, 2) AS total_proteines,
        ROUND(
            SUM(
                ((c.glucides).fibres * q.quantite / 100)
                + ((c.glucides).sucre * q.quantite / 100)
            )::NUMERIC,
        2) AS total_glucides,
        ROUND(SUM((c.lipides).total * q.quantite / 100)::NUMERIC, 2) AS total_lipides,
        SUM(CASE WHEN c.type = 'Boisson' THEN q.quantite ELSE 0 END) AS total_hydratation,
        SUM(c.calories * q.quantite / 100) AS total_calories,
        ROUND(
            SUM(c.calories * q.quantite / 100)::NUMERIC / days_interval,
            2
        ) AS moyenne_calories
    FROM suivi_dietetique.repas r
    JOIN suivi_dietetique.quantite_repas_consommable q
         ON r.noSS_patient = q.noSS_patient
         AND r.date_consommation = q.date_consommation
    JOIN suivi_dietetique.consommable c
         ON q.id_consommable = c.id
    JOIN patient_vue p
         ON r.noSS_patient = p.noSS
    WHERE r.date_consommation >= CURRENT_DATE - (days_interval || ' days')::INTERVAL
    GROUP BY
        r.noSS_patient,
        p.nom,
        p.prenom;
END;
$$;

-- Exemples d'utilisation
SELECT * FROM get_stats(1);  -- Statistiques sur 1 jour
SELECT * FROM get_stats(7);  -- Statistiques sur 7 jours
SELECT * FROM get_stats(30); -- Statistiques sur 30 jours


------------------------------------------------------------------------------------
-- Fonction qui retourne les stats nutritionnelles d'un seul repas
------------------------------------------------------------------------------------

-- On supprime d'abord la fonction si elle existe déjà
DROP FUNCTION IF EXISTS get_meal_stats(p_noSS_patient INT, p_date_consommation TIMESTAMPTZ);

CREATE OR REPLACE FUNCTION get_meal_stats(
    p_noSS_patient INT,
    p_date_consommation TIMESTAMPTZ
)
RETURNS TABLE
(
    noSS_patient      INTEGER,
    date_consommation TIMESTAMPTZ,
    type              suivi_dietetique.type_repas,
    noss_infirmier    INTEGER,
    total_proteines   NUMERIC(10,2),
    total_glucides    NUMERIC(10,2),
    total_lipides     NUMERIC(10,2),
    total_hydratation INTEGER,
    total_calories    INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.noSS_patient,
        r.date_consommation,
        r.type,
        r.noss_infirmier,

        -- total_proteines
        ROUND(
            SUM((c.proteines / 100.0) * q.quantite)::numeric,
            2
        ) AS total_proteines,

        -- total_glucides
        ROUND(
            SUM(
                (
                    ((c.glucides).fibres + (c.glucides).sucre)
                    / 100.0
                ) * q.quantite
            )::numeric,
            2
        ) AS total_glucides,

        -- total_lipides
        ROUND(
            SUM(
                ((c.lipides).total / 100.0) * q.quantite
            )::numeric,
            2
        ) AS total_lipides,

        -- total_hydratation
        SUM(
            CASE
                WHEN c.type = 'Boisson' THEN q.quantite
                ELSE 0
            END
        )::integer AS total_hydratation,

        -- total_calories
        SUM(
            (c.calories / 100.0) * q.quantite
        )::integer AS total_calories

    FROM suivi_dietetique.repas r
    JOIN suivi_dietetique.quantite_repas_consommable q
         ON r.noSS_patient = q.noSS_patient
         AND r.date_consommation = q.date_consommation
    JOIN suivi_dietetique.consommable c
         ON q.id_consommable = c.id
    JOIN suivi_dietetique.patient p
         ON r.noSS_patient = p.noSS

    WHERE r.noSS_patient = p_noSS_patient
      AND r.date_consommation = p_date_consommation

    GROUP BY
        r.noSS_patient,
        r.date_consommation,
        r.type,
        r.noss_infirmier;
END;
$$;

------------------------------------------------------------------------------------
-- Fonction qui retourne la progression d'un patient sur un intervalle donné
------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION suivi_dietetique.progression_patient(
    _noSS INT,
    _date_debut TIMESTAMPTZ,
    _date_fin   TIMESTAMPTZ
)
RETURNS TABLE
(
    noSS_patient       INT,
    date_debut         TIMESTAMPTZ,
    date_fin           TIMESTAMPTZ,
    delta_poids        NUMERIC,
    delta_tourDeTaille NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        ds1.noSS_patient,
        ds1.date AS date_debut,
        ds2.date AS date_fin,
        (ds2.poids - ds1.poids)::NUMERIC AS delta_poids,
        (ds2.tourDeTaille - ds1.tourDeTaille)::NUMERIC AS delta_tourDeTaille
    FROM suivi_dietetique.donnees_sante ds1
    JOIN suivi_dietetique.donnees_sante ds2
         ON ds2.noSS_patient = ds1.noSS_patient
    WHERE ds1.noSS_patient = _noSS
      AND ds1.date = _date_debut
      AND ds2.date = _date_fin;
END;
$$;

-- Exemple d'utilisation
SELECT *
FROM suivi_dietetique.progression_patient(
    100000005,
    '2024-01-01 10:00:00'::timestamptz,
    '2024-11-30 10:00:00'::timestamptz
);

------------------------------------------------------------------------------------
-- Fonction qui retourne tous les repas d'un patient dans un intervalle de temps
------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION suivi_dietetique.getRepasFromPatient_Interval(
    _noSS INT,
    _interval VARCHAR
)
RETURNS TABLE
(
    date_consommation timestamptz,
    type_repas        suivi_dietetique.type_repas,
    remarque          TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.date_consommation,
        r.type,
        r.remarque
    FROM suivi_dietetique.repas r
    WHERE r.noss_patient = _noSS
      AND r.date_consommation >= CURRENT_DATE - (_interval::INTERVAL)
    ORDER BY r.date_consommation DESC;
END;
$$;

-- Exemple d'utilisation
SELECT *
FROM suivi_dietetique.getRepasFromPatient_Interval(100000005, '1 day');

------------------------------------------------------------------------------------
-- Retourne tous les repas d’un patient dans un intervalle donné,
-- accompagnés de leurs statistiques nutritionnelles
------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION suivi_dietetique.getRepasWithStats_Interval(
    _noSS INT,
    _interval VARCHAR
)
RETURNS TABLE
(
    date_consommation TIMESTAMPTZ,
    type_repas        suivi_dietetique.type_repas,
    remarque          TEXT,
    noSS_infirmier    INTEGER,
    total_proteines   NUMERIC(10,2),
    total_glucides    NUMERIC(10,2),
    total_lipides     NUMERIC(10,2),
    total_hydratation INTEGER,
    total_calories    INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.date_consommation,
        r.type,
        r.remarque,
        r.noss_infirmier,

        ROUND(
            SUM((c.proteines / 100.0) * q.quantite)::NUMERIC,
            2
        ) AS total_proteines,

        ROUND(
            SUM(
                (((c.glucides).fibres + (c.glucides).sucre) / 100.0)
                * q.quantite
            )::NUMERIC,
            2
        ) AS total_glucides,

        ROUND(
            SUM(
                ((c.lipides).total / 100.0) * q.quantite
            )::NUMERIC,
            2
        ) AS total_lipides,

        SUM(
            CASE WHEN c.type = 'Boisson' THEN q.quantite ELSE 0 END
        )::INTEGER AS total_hydratation,

        SUM(
            (c.calories / 100.0) * q.quantite
        )::INTEGER AS total_calories

    FROM suivi_dietetique.repas r
    JOIN suivi_dietetique.quantite_repas_consommable q
         ON r.noss_patient = q.noSS_patient
         AND r.date_consommation = q.date_consommation
    JOIN suivi_dietetique.consommable c
         ON q.id_consommable = c.id

    WHERE r.noss_patient = _noSS
      AND r.date_consommation >= CURRENT_DATE - (_interval::INTERVAL)

    GROUP BY
        r.date_consommation,
        r.type,
        r.remarque,
        r.noss_infirmier

    ORDER BY r.date_consommation DESC;
END;
$$;

-- Exemple d'utilisation
SELECT *
FROM suivi_dietetique.getRepasWithStats_Interval(100000005, '1 day');

------------------------------------------------------------------------------------
-- Fonction qui retourne tous les repas d'un patient avec leurs statistiques
------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION suivi_dietetique.getAllRepasWithStats(
    _noSS INT
)
RETURNS TABLE
(
    date_consommation TIMESTAMPTZ,
    type_repas        suivi_dietetique.type_repas,
    remarque          TEXT,
    noSS_infirmier    INTEGER,
    total_proteines   NUMERIC(10,2),
    total_glucides    NUMERIC(10,2),
    total_lipides     NUMERIC(10,2),
    total_hydratation INTEGER,
    total_calories    INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.date_consommation,
        r.type,
        r.remarque,
        r.noss_infirmier,

        ROUND(
            SUM((c.proteines / 100.0) * q.quantite)::NUMERIC,
            2
        ) AS total_proteines,

        ROUND(
            SUM(
                (((c.glucides).fibres + (c.glucides).sucre) / 100.0)
                * q.quantite
            )::NUMERIC,
            2
        ) AS total_glucides,

        ROUND(
            SUM(
                ((c.lipides).total / 100.0) * q.quantite
            )::NUMERIC,
            2
        ) AS total_lipides,

        SUM(
            CASE WHEN c.type = 'Boisson' THEN q.quantite ELSE 0 END
        )::INTEGER AS total_hydratation,

        SUM(
            (c.calories / 100.0) * q.quantite
        )::INTEGER AS total_calories

    FROM suivi_dietetique.repas r
    JOIN suivi_dietetique.quantite_repas_consommable q
         ON r.noss_patient = q.noSS_patient
         AND r.date_consommation = q.date_consommation
    JOIN suivi_dietetique.consommable c
         ON q.id_consommable = c.id

    WHERE r.noss_patient = _noSS

    GROUP BY
        r.date_consommation,
        r.type,
        r.remarque,
        r.noss_infirmier

    ORDER BY r.date_consommation DESC;
END;
$$;

-- Exemple d'utilisation
SELECT *
FROM suivi_dietetique.getAllRepasWithStats(100000005);
