SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `civil_servant`;
TRUNCATE TABLE `department`;
TRUNCATE TABLE `organisation`;
TRUNCATE TABLE `grade`;
TRUNCATE TABLE `identity`;

INSERT INTO grade (code, name) VALUES ('G6', 'Grade 6'), ('G7', 'Grade 7');

SET FOREIGN_KEY_CHECKS = 1;

