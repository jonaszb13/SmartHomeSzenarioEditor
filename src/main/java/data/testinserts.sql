INSERT INTO RAEUME (NAME)
VALUES ('Raum 1'),
       ('Raum 2'),
       ('Raum 3');
INSERT INTO GERAETE (NAME, RAUM, ART)
VALUES ('Sensor 1', 1, 'Sensor'),
       ('Sensor 2', 1, 'Sensor'),
       ('Heizung 1', 2, 'Heizung'),
       ('Lampe 1', 3, 'Lampe');
INSERT INTO GERAETE_WERTE (GERAET, SCHLUESSEL, WERT)
VALUES (1, 'eingeschaltet', 'true'),
       (2, 'eingeschaltet', 'false'),
       (1, 'ausschlag', 'true'),
       (2, 'ausschlag', 'false'),
       (3, 'zielTemp', '22.5'),
       (4, 'haelligkeit', '99.7'),
       (4, 'farbe', '#00FF88'),
       (4, 'eingeschaltet', 'true');
INSERT INTO SZENARIEN (NAME, BESCHREIBUNG)
VALUES ('Szenario 1', 'Testszenario'),
       ('Szenario 2', 'Testszenario');
INSERT INTO SZENARIEN_INHALT (AKTION, SZENARIO, GERAET, SCHLUESSEL, WERT, POSITION)
VALUES ('Sensor an',1,1,'eingeschaltet', 'true',1),
       ('Sensor an',1,2,'eingeschaltet', 'true',2),
       ('Sensor aus',2,1,'eingeschaltet', 'false',1),
       ('Sensor aus',2,2,'eingeschaltet', 'false',2);
