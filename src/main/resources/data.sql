SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `civil_servant`;
TRUNCATE TABLE `department`;
TRUNCATE TABLE `organisation`;
TRUNCATE TABLE `grade`;
TRUNCATE TABLE `identity`;

INSERT INTO grade (code, name) VALUES
  ('G6', 'Grade 6'),
  ('G7', 'Grade 7');

INSERT INTO department (code, name) VALUES
  ('co', 'Cabinet Office');

INSERT INTO organisation (code, name, department_id) VALUES
  ('org', 'Org 1', SELECT id FROM department WHERE code = 'co');

INSERT INTO organisation_grades VALUES
  (SELECT id FROM organisation WHERE code = 'org', SELECT id FROM grade WHERE code = 'G6'),
  (SELECT id FROM organisation WHERE code = 'org', SELECT id FROM grade WHERE code = 'G7');


SET FOREIGN_KEY_CHECKS = 1;

