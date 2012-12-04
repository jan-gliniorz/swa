USE ${dbname.mysql};
SHOW WARNINGS;

DROP TABLE IF EXISTS hibernate_sequence;
CREATE TABLE hibernate_sequence(
	next_val BIGINT NOT NULL PRIMARY KEY
);

CREATE TABLE if not exists kunde (
	kundenNr INTEGER NOT NULL PRIMARY KEY, 
	nachname NVARCHAR(30) NOT NULL, 
	vorname NVARCHAR(30) NOT NULL, 
	email NVARCHAR(128) NOT NULL UNIQUE,
	passwort NVARCHAR(256) NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL
);

create unique index kunde_email ON kunde(email);

create table if not exists adresse (
	adresse_ID INTEGER NOT NULL PRIMARY KEY, 
	strasse NVARCHAR(30) NOT NULL, 
	hausNr NVARCHAR(10) NOT NULL, 
	plz CHAR(5) NOT NULL,
	ort NVARCHAR(30) NOT NULL,
	land NVARCHAR(50) NOT NULL,
	kunde_FID INTEGER NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL
);

create table if not exists rechnung (
	rechnung_ID INTEGER NOT NULL PRIMARY KEY, 
	auftrag_FID INTEGER NOT NULL,
	rechnungsdatum DATE NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL
);

create table if not exists auftrag (
	auftrag_ID INTEGER NOT NULL PRIMARY KEY, 
	kunde_FID INTEGER NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL
);

create table if not exists auftragsposition (
	auftragsposition_ID INTEGER NOT NULL PRIMARY KEY,
	auftrag_FID INTEGER NOT NULL,
	idx INTEGER NOT NULL,
	artikel_FID INTEGER NOT NULL,
	anzahl INTEGER NOT NULL,
	preis Decimal NOT NULL
);

create table if not exists artikel (
	artikel_ID INTEGER NOT NULL PRIMARY KEY,
	bezeichnung NVARCHAR(100) NOT NULL,
	beschreibung TEXT NULL,
	preis Decimal NOT NULL,
	bild NVARCHAR(64) NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL
);

create table if not exists lager (
	lager_ID INTEGER NOT NULL PRIMARY KEY,
	bezeichung NVARCHAR(100) NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL
);

create table if not exists lagerposition (
	lagerposition_ID INTEGER NOT NULL PRIMARY KEY,
	lager_FID INTEGER NOT NULL,
	artikel_FID INTEGER NOT NULL,
	anzahl INTEGER NOT NULL,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL
);

create table if not exists lieferung (
	lieferung_ID INTEGER NOT NULL PRIMARY KEY,
	bestelldatum DATE NOT NULL,
	lieferungsdatum DATE,
	erstellt_am TIMESTAMP NOT NULL,
	geaendert_am TIMESTAMP NOT NULL
);

create table if not exists lieferungsposition (
	lieferungsposition_ID INTEGER NOT NULL PRIMARY KEY,
	artikel_FID INTEGER NOT NULL,
	lieferung_FID INTEGER NOT NULL,
	anzahl INTEGER NOT NULL
);
