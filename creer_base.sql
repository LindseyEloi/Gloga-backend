-- ============================================================
-- Script de creation de la base "centre_medical"
-- A executer avec psql ou un outil comme pgAdmin / DBeaver
-- (Facultatif : Spring Boot peut creer les tables automatiquement
--  grace a spring.jpa.hibernate.ddl-auto=update)
-- ============================================================

-- 1) Se connecter en tant que superutilisateur puis creer la base :
-- CREATE DATABASE centre_medical;

-- 2) Se connecter a la base "centre_medical" puis executer :

CREATE TABLE IF NOT EXISTS medecin (
    codemed VARCHAR(10) PRIMARY KEY,
    nom     VARCHAR(50) NOT NULL,
    prenom  VARCHAR(50),
    grade   VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS patient (
    codepat VARCHAR(10) PRIMARY KEY,
    nom     VARCHAR(50) NOT NULL,
    prenom  VARCHAR(50),
    sexe    CHAR(1),
    adresse VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS visiter (
    codemed     VARCHAR(10) NOT NULL REFERENCES medecin(codemed) ON DELETE CASCADE,
    codepat     VARCHAR(10) NOT NULL REFERENCES patient(codepat) ON DELETE CASCADE,
    date_visite DATE NOT NULL,
    PRIMARY KEY (codemed, codepat, date_visite)
);

-- Quelques donnees de test (facultatif)
INSERT INTO medecin (codemed, nom, prenom, grade) VALUES
    ('MED001', 'Rakoto', 'Jean', 'Généraliste'),
    ('MED002', 'Rabe', 'Marie', 'Cardiologue')
ON CONFLICT DO NOTHING;

INSERT INTO patient (codepat, nom, prenom, sexe, adresse) VALUES
    ('PAT001', 'Randria', 'Paul', 'M', 'Antananarivo'),
    ('PAT002', 'Ravao', 'Sophie', 'F', 'Antsirabe')
ON CONFLICT DO NOTHING;
