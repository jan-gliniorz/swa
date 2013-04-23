DROP SEQUENCE hibernate_sequence;
CREATE Sequence hibernate_sequence
    START WITH 1000 INCREMENT BY 1 NOMAXVALUE NOCYCLE NOCACHE;

DROP TABLE kunde;
CREATE TABLE kunde (
	kundenNr INTEGER NOT NULL PRIMARY KEY, 
	nachname VARCHAR2(30) NOT NULL, 
	vorname VARCHAR2(30) NOT NULL, 
	email VARCHAR2(128) NOT NULL UNIQUE,
	passwort VARCHAR2(256) NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE kunde_rolle;
CREATE TABLE kunde_rolle (
	kunde_FID INTEGER NOT NULL,
	rolle_FID INTEGER NOT NULL,
	PRIMARY KEY (rolle_FID,kunde_FID)
);

DROP TABLE rolle;
CREATE TABLE rolle (
	id INTEGER NOT NULL PRIMARY KEY,
	name VARCHAR2(30) NOT NULL
);

DROP TABLE adresse;
CREATE table adresse (
	adresse_ID INTEGER NOT NULL PRIMARY KEY, 
	strasse VARCHAR2(30) NOT NULL, 
	hausNr VARCHAR(10) NOT NULL, 
	plz CHAR(5) NOT NULL,
	ort VARCHAR2(30) NOT NULL,
	land VARCHAR2(50) NOT NULL,
	kunde_FID INTEGER NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE rechnung;
CREATE TABLE rechnung (
	rechnung_ID INTEGER NOT NULL PRIMARY KEY, 
	auftrag_FID INTEGER NOT NULL,
	rechnungsdatum DATE NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE auftrag;
CREATE TABLE auftrag (
	auftrag_ID INTEGER NOT NULL PRIMARY KEY, 
	kunde_FID INTEGER NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE auftragsposition;
CREATE TABLE auftragsposition (
	auftragsposition_ID INTEGER NOT NULL PRIMARY KEY,
	auftrag_FID INTEGER NOT NULL,
	idx INTEGER NOT NULL,
	artikel_FID INTEGER NOT NULL,
	anzahl INTEGER NOT NULL,
	preis NUMBER(8,2) NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE artikel;
CREATE TABLE artikel (
	artikel_ID INTEGER NOT NULL PRIMARY KEY,
	bezeichnung VARCHAR2(100) NOT NULL,
	beschreibung VARCHAR2(4000) NULL,
	preis NUMBER(8,2) NOT NULL,
	bild VARCHAR2(64) NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE lager;
CREATE TABLE lager (
	lager_ID INTEGER NOT NULL PRIMARY KEY,
	bezeichnung VARCHAR2(100) NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE lagerposition;
CREATE TABLE lagerposition (
	lagerposition_ID INTEGER NOT NULL PRIMARY KEY,
	lager_FID INTEGER NOT NULL,
	idx INTEGER NOT NULL,
	artikel_FID INTEGER NOT NULL,
	anzahl INTEGER NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE lieferung;
CREATE TABLE lieferung (
	lieferung_ID INTEGER NOT NULL PRIMARY KEY,
	bestelldatum DATE NOT NULL,
	lieferungsdatum DATE,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL,
	version NUMBER(8,0) NOT NULL
);

DROP TABLE lieferungsposition;
CREATE TABLE lieferungsposition (
	lieferungsposition_ID INTEGER NOT NULL PRIMARY KEY,
	artikel_FID INTEGER NOT NULL,
	lieferung_FID INTEGER NOT NULL,
	anzahl INTEGER NOT NULL,
	version NUMBER(8,0) NOT NULL
);
