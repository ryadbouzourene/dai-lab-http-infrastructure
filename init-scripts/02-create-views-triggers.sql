--VIEWS--

-- vue de la table patient
CREATE OR REPLACE VIEW personne_vue AS
SELECT personne.noss,
       nom,
       prenom,
       datenaissance,
       personne.sexe,
       utilisateur.email
FROM personne
         LEFT JOIN utilisateur
                   ON utilisateur.noss = personne.noss;

CREATE OR REPLACE VIEW patient_vue AS
SELECT patient.noss,
       nom,
       prenom,
       datenaissance,
       personne_vue.sexe,
       personne_vue.email,
       noss_dieteticien,
       patient.dateAdmission
FROM patient
         INNER JOIN personne_vue
                    ON patient.noss = personne_vue.noss;


-- vue de la table employé
CREATE OR REPLACE VIEW employe_vue AS
SELECT personne_vue.noss,
       personne_vue.nom,
       personne_vue.prenom,
       personne_vue.datenaissance,
       personne_vue.sexe,
       personne_vue.email,
       employe.id_service,
       service.nom AS nom_service,
       employe.dateembauche,
       employe.statut
FROM suivi_dietetique.employe
         INNER JOIN personne_vue
                    ON employe.noss = personne_vue.noss
         INNER JOIN suivi_dietetique.service
                    ON employe.id_service = service.id;

-- vue de la table infirmier
CREATE OR REPLACE VIEW infirmier_vue AS
SELECT ev.noss,
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

-- vue de la table diététicien
CREATE OR REPLACE VIEW dieteticien_vue AS
SELECT ev.noss,
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

-- vue des objectifs hebdomadaires
CREATE OR REPLACE VIEW objectif_hebdomadaire AS
SELECT nom, prenom, commentaire, reussi
FROM patient_vue p
         INNER JOIN suivi_dietetique.objectif o
                    ON p.noss = o.noss_patient
GROUP BY nom, prenom, reussi, commentaire;

DROP VIEW IF EXISTS suivi_dietetique.consommables_repas;

-- Consommables par repas
CREATE OR REPLACE VIEW suivi_dietetique.consommables_repas AS
SELECT
    c.id ,
    q.noSS_patient,
    q.date_consommation,
    c.nom  AS consommable_nom,
    c.type AS type_consommable,
    q.quantite,

    -- total_proteines : (protéines / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.proteines / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_proteines,

    -- total_glucides : ((fibres + sucre) / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (
                ((c.glucides).fibres + (c.glucides).sucre) / 100.0
            ) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_glucides,

    -- total_lipides : (lipides.total / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (
                (c.lipides).total / 100.0
            ) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_lipides,

    -- total_calories : (calories / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.calories / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_calories,

    -- total_hydratation : si c.type = 'Boisson' alors q.quantite, sinon 0 (gestion du NULL sur q.quantite)
    COALESCE(
        CASE WHEN c.type = 'Boisson'
             THEN q.quantite
             ELSE 0
        END,
        0
    ) AS total_hydratation,

    -- Total potassium : (potassium / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.potassium / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_potassium,

    -- Total cholesterol : (cholesterol / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.cholesterol / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_cholesterol,

    -- Total sodium : (sodium / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.sodium / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_sodium,

    -- Total vit_A : (vit_A / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.vit_A / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_vit_A,

    -- Total vit_C : (vit_C / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.vit_C / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_vit_C,

    -- Total vit_D : (vit_D / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.vit_D / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_vit_D,

    -- Total calcium : (calcium / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.calcium / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_calcium,

    -- Total fer : (fer / 100) * quantite, avec gestion du NULL
    ROUND(
        COALESCE(
            (c.fer / 100.0) * q.quantite,
            0
        )::numeric,
        2
    ) AS total_fer

FROM suivi_dietetique.quantite_repas_consommable q
JOIN suivi_dietetique.consommable c ON q.id_consommable = c.id;



-- Statistiques de santé actuelles par patient
CREATE OR REPLACE VIEW suivi_dietetique.stats_sante_actuelles AS
SELECT ds.noSS_patient,
       pw.nom    AS patient_nom,
       pw.prenom AS patient_prenom,
       ds.date,
       ds.taille,
       ds.poids,
       ds.tourDeTaille,
       ds.niveauActivitePhysique,
       ROUND(ds.poids / (ds.taille / 100.0)^2, 2) AS IMC
FROM suivi_dietetique.donnees_sante ds
         JOIN patient_vue pw ON ds.noSS_patient = pw.noSS
WHERE ds.date = (SELECT MAX(date)
                 FROM suivi_dietetique.donnees_sante d
                 WHERE d.noSS_patient = ds.noSS_patient);

------------------------------------------------------------------------------------
-- TRIGGERS
------------------------------------------------------------------------------------

-- Triggers permettant de vérifier les allergies d'un patient
CREATE OR REPLACE FUNCTION verifier_allergies()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM suivi_dietetique.est_allergique ea
                        JOIN suivi_dietetique.peut_contenir pc ON ea.nom_allergene = pc.nom_allergene
               WHERE ea.noSS_patient = NEW.noSS_patient
                 AND pc.id_consommable = NEW.id_consommable) THEN
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

CREATE OR REPLACE FUNCTION valider_utilisateur()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.role = 'Diététicien' AND NOT EXISTS (SELECT 1
                                                FROM suivi_dietetique.dieteticien
                                                WHERE noSS = NEW.noSS) THEN
        RAISE EXCEPTION 'NoSS % est associé à aucun diététicien', NEW.noSS;
    ELSIF NEW.role = 'Infirmier' AND NOT EXISTS (SELECT 1
                                                 FROM suivi_dietetique.infirmier
                                                 WHERE noSS = NEW.noSS) THEN
        RAISE EXCEPTION 'NoSS % est associé à aucun infirmier', NEW.noSS;
    ELSIF NEW.role = 'Patient' AND NOT EXISTS (SELECT 1
                                               FROM suivi_dietetique.patient
                                               WHERE noSS = NEW.noSS) THEN
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

-- Fonction permettant de retourner les statistiques nutritionnelles de tous les patients dans un interval
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
AS
$$
BEGIN
    RETURN QUERY
        SELECT r.noSS_patient,
               p.nom                                                           AS patient_nom,
               p.prenom                                                        AS patient_prenom,
               ROUND(SUM(c.proteines * q.quantite/100)::NUMERIC, 2)                AS total_proteines,
               ROUND(SUM((c.glucides).fibres * q.quantite/100 + (c.glucides).sucre * q.quantite/100)::NUMERIC,
                     2)                                                        AS total_glucides,
               ROUND(SUM((c.lipides).total * q.quantite/100)::NUMERIC, 2)          AS total_lipides,
               SUM(CASE WHEN c.type = 'Boisson' THEN q.quantite ELSE 0 END)    AS total_hydratation,
               SUM(c.calories * q.quantite/100)                                    AS total_calories,
               ROUND(SUM(c.calories * q.quantite/100)::NUMERIC / days_interval, 2) AS moyenne_calories
        FROM suivi_dietetique.repas r
                 JOIN
             suivi_dietetique.quantite_repas_consommable q
             ON r.noSS_patient = q.noSS_patient AND r.date_consommation = q.date_consommation
                 JOIN
             suivi_dietetique.consommable c
             ON q.id_consommable = c.id
                 JOIN
             patient_vue p
             ON r.noSS_patient = p.noSS
        WHERE r.date_consommation >= CURRENT_DATE - (days_interval || ' days')::INTERVAL
        GROUP BY r.noSS_patient, p.nom, p.prenom;
END;
$$;

-- Statistiques nutritionnelles par patient (jour actuel)
SELECT *
FROM get_stats(1);

-- Statistiques nutritionnelles par patient (7 derniers jours)
SELECT *
FROM get_stats(7);

-- Statistiques nutritionnelles par patient (30 derniers jours)
SELECT *
FROM get_stats(30);

-- Fonction qui retourne les stats nutritionnelles d'un seul repas d'un patient
CREATE OR REPLACE FUNCTION get_meal_stats(
    p_noSS_patient      INT,
    p_date_consommation TIMESTAMPTZ
)
    RETURNS TABLE
            (
                noSS_patient      INTEGER,
                date_consommation TIMESTAMPTZ,
                type              suivi_dietetique.type_repas,
                noSS_infirmier    INTEGER,
                total_proteines   NUMERIC(10,2),
                total_glucides    NUMERIC(10,2),
                total_lipides     NUMERIC(10,2),
                total_hydratation INTEGER,
                total_calories    INTEGER
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT
            r.noSS_patient,
            r.date_consommation,
            r.type,
            r.noss_infirmier,

            -- Protéines : pour 100 g => on divise par 100.0 puis * la quantite (en g)
            ROUND(
                SUM(
                    (c.proteines / 100.0) * q.quantite
                )::numeric
            , 2) AS total_proteines,

            -- Glucides : (fibres + sucre) / 100, multiplié par la quantité
            ROUND(
                SUM(
                    (
                        ((c.glucides).fibres + (c.glucides).sucre)
                        / 100.0
                    ) * q.quantite
                )::numeric
            , 2) AS total_glucides,

            -- Lipides : (lipides.total / 100), multiplié par la quantité
            ROUND(
                SUM(
                    (
                        (c.lipides).total
                        / 100.0
                    ) * q.quantite
                )::numeric
            , 2) AS total_lipides,

            -- Hydratation : SUM(...)::integer pour éviter le type BIGINT
            SUM(
                CASE
                    WHEN c.type = 'Boisson' THEN q.quantite
                    ELSE 0
                END
            )::integer                                                   AS total_hydratation,

            -- Calories : (calories / 100), multiplié par la quantité
            SUM(
                (c.calories / 100.0) * q.quantite
            )::integer                                                  AS total_calories

        FROM suivi_dietetique.repas r
        JOIN suivi_dietetique.quantite_repas_consommable q
             ON r.noSS_patient = q.noSS_patient
             AND r.date_consommation = q.date_consommation
        JOIN suivi_dietetique.consommable c
             ON q.id_consommable = c.id
        JOIN suivi_dietetique.patient_vue p
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

DROP FUNCTION get_meal_stats(p_noSS_patient INT, p_date_consommation TIMESTAMPTZ);

-- Fonction qui retourne la progression d'un patient sur un interval d'une date de début à une date de fin
CREATE OR REPLACE FUNCTION suivi_dietetique.progression_patient(
    _noSS INT,
    _date_debut TIMESTAMPTZ,
    _date_fin TIMESTAMPTZ
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
AS
$$
BEGIN
    RETURN QUERY
        SELECT ds1.noSS_patient,
               ds1.date                                       AS date_debut,
               ds2.date                                       AS date_fin,
               (ds2.poids - ds1.poids)::NUMERIC               AS delta_poids,
               (ds2.tourDeTaille - ds1.tourDeTaille)::NUMERIC AS delta_tourDeTaille
        FROM suivi_dietetique.donnees_sante ds1
                 JOIN suivi_dietetique.donnees_sante ds2
                      ON ds2.noSS_patient = ds1.noSS_patient
        WHERE ds1.noSS_patient = _noSS
          AND ds1.date = _date_debut
          AND ds2.date = _date_fin;
END;
$$;

-- Exemple
SELECT *
FROM suivi_dietetique.progression_patient(
        100000005,
        '2024-01-01 10:00:00'::timestamptz,
        '2024-11-30 10:00:00'::timestamptz
     );

-- Fonction qui permet de retourner tous les repas d'un patient dans un certain interval de temps
CREATE OR REPLACE FUNCTION suivi_dietetique.getRepasFromPatient_Interval(
    _noSS INT,
    _interval VARCHAR)
    RETURNS TABLE
            (
                date_consommation timestamptz,
                type_repas        suivi_dietetique.type_repas,
                remarque          TEXT
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT r.date_consommation,
               r.type,
               r.remarque
        FROM repas AS r
        WHERE r.noss_patient = _noSS
          AND r.date_consommation >= CURRENT_DATE - (_interval::INTERVAL)
        ORDER BY r.date_consommation DESC;
END;
$$;

-- Exemple utilisation
SELECT *
FROM getRepasFromPatient_Interval(100000005, '1 day');


-- Retourne tous les repas d’un patient dans un intervalle donné, accompagnés de leurs statistiques nutritionnelles
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
AS
$$
BEGIN
    RETURN QUERY
        SELECT
            r.date_consommation,
            r.type,
            r.remarque,
            r.noss_infirmier,

            -- Calcul des statistiques nutritionnelles
            ROUND(SUM((c.proteines / 100.0) * q.quantite)::NUMERIC, 2) AS total_proteines,
            ROUND(SUM((((c.glucides).fibres + (c.glucides).sucre) / 100.0) * q.quantite)::NUMERIC, 2) AS total_glucides,
            ROUND(SUM(((c.lipides).total / 100.0) * q.quantite)::NUMERIC, 2) AS total_lipides,
            SUM(CASE WHEN c.type = 'Boisson' THEN q.quantite ELSE 0 END)::INTEGER AS total_hydratation,
            SUM((c.calories / 100.0) * q.quantite)::INTEGER AS total_calories

        FROM suivi_dietetique.repas AS r
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

SELECT * FROM getRepasWithStats_Interval(100000005, '1 day');

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
AS
$$
BEGIN
    RETURN QUERY
        SELECT
            r.date_consommation,
            r.type,
            r.remarque,
            r.noss_infirmier,

            -- Calcul des statistiques nutritionnelles
            ROUND(SUM((c.proteines / 100.0) * q.quantite)::NUMERIC, 2) AS total_proteines,
            ROUND(SUM((((c.glucides).fibres + (c.glucides).sucre) / 100.0) * q.quantite)::NUMERIC, 2) AS total_glucides,
            ROUND(SUM(((c.lipides).total / 100.0) * q.quantite)::NUMERIC, 2) AS total_lipides,
            SUM(CASE WHEN c.type = 'Boisson' THEN q.quantite ELSE 0 END)::INTEGER AS total_hydratation,
            SUM((c.calories / 100.0) * q.quantite)::INTEGER AS total_calories

        FROM suivi_dietetique.repas AS r
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

SELECT * FROM getAllRepasWithStats(100000005);