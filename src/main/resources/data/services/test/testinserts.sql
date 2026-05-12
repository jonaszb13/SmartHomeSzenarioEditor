INSERT INTO RAEUME (ID, NAME)
VALUES ('9bf21849-af67-4c50-ba0d-6e991850ceb4','Raum 1'),
       ('0d481ee5-8528-42e0-bf14-e224e3d84ab0','Raum 2'),
       ('04c18f4e-36db-4f65-816c-475577a044a2','Raum 3');
INSERT INTO GERAETE (ID, NAME, RAUM, ART)
VALUES ('c216e129-1541-4455-804c-411b17dd015b','Sensor 1', '9bf21849-af67-4c50-ba0d-6e991850ceb4', 'Sensor'),
       ('ffe1118c-440c-40d4-bfc4-dadbfa5db831','Sensor 2', '9bf21849-af67-4c50-ba0d-6e991850ceb4', 'Sensor'),
       ('eb08c72e-20f1-466a-b024-ced0d8a32703','Heizung 1', '0d481ee5-8528-42e0-bf14-e224e3d84ab0', 'Heizung'),
       ('fe4dacb0-7ef9-405f-be51-b739a4b6cd29','Lampe 1', '04c18f4e-36db-4f65-816c-475577a044a2', 'Lampe');
INSERT INTO GERAETE_WERTE (ID, GERAET, SCHLUESSEL, WERT)
VALUES (random_uuid(),'c216e129-1541-4455-804c-411b17dd015b', 'eingeschaltet', 'true'),
       (random_uuid(),'ffe1118c-440c-40d4-bfc4-dadbfa5db831', 'eingeschaltet', 'false'),
       (random_uuid(),'c216e129-1541-4455-804c-411b17dd015b', 'ausschlag', 'true'),
       (random_uuid(),'ffe1118c-440c-40d4-bfc4-dadbfa5db831', 'ausschlag', 'false'),
       (random_uuid(),'eb08c72e-20f1-466a-b024-ced0d8a32703', 'zielTemp', '22.5'),
       (random_uuid(),'fe4dacb0-7ef9-405f-be51-b739a4b6cd29', 'haelligkeit', '99.7'),
       (random_uuid(),'fe4dacb0-7ef9-405f-be51-b739a4b6cd29', 'farbe', '#00FF88'),
       (random_uuid(),'fe4dacb0-7ef9-405f-be51-b739a4b6cd29', 'eingeschaltet', 'true');
INSERT INTO SZENARIEN (ID, NAME, BESCHREIBUNG, STATUS)
VALUES ('c06ecee9-65c3-4444-aa82-e1148badfc0d','Szenario 1', 'Testszenario','false'),
       ('22710be6-8c78-404f-b89e-3d47b20c1db6','Szenario 2', 'Testszenario','false');
INSERT INTO SZENARIEN_INHALT (ID, AKTION, SZENARIO, GERAET, SCHLUESSEL, WERT, POSITION)
VALUES (random_uuid(),'Sensor an', 'c06ecee9-65c3-4444-aa82-e1148badfc0d', 'c216e129-1541-4455-804c-411b17dd015b', 'eingeschaltet', 'true', 1),
       (random_uuid(),'Sensor an', 'c06ecee9-65c3-4444-aa82-e1148badfc0d', 'ffe1118c-440c-40d4-bfc4-dadbfa5db831', 'eingeschaltet', 'true', 2),
       (random_uuid(),'Sensor aus', '22710be6-8c78-404f-b89e-3d47b20c1db6', 'c216e129-1541-4455-804c-411b17dd015b', 'eingeschaltet', 'false', 1),
       (random_uuid(),'Sensor aus', '22710be6-8c78-404f-b89e-3d47b20c1db6', 'ffe1118c-440c-40d4-bfc4-dadbfa5db831', 'eingeschaltet', 'false', 2);
