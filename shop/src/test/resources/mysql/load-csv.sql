USE ${dbname.mysql};

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/hibernate_sequence.csv'
INTO TABLE hibernate_sequence
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/kunde.csv'
INTO TABLE kunde
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/adresse.csv'
INTO TABLE adresse
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/auftrag.csv'
INTO TABLE auftrag
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/artikel.csv'
INTO TABLE artikel
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/auftragsposition.csv'
INTO TABLE auftragsposition
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/lieferung.csv'
INTO TABLE lieferung
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/lieferungsposition.csv'
INTO TABLE lieferungsposition
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/rechnung.csv'
INTO TABLE rechnung
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/lager.csv'
INTO TABLE lager
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/swe/eclipse-workspace-git/swa17/shop/src/test/resources/mysql/lagerposition.csv'
INTO TABLE lagerposition
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;