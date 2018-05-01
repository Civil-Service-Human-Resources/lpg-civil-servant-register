SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `civil_servant`;
TRUNCATE TABLE `department`;
TRUNCATE TABLE `organisation`;
TRUNCATE TABLE `grade`;
TRUNCATE TABLE `identity`;

INSERT INTO grade (code, name, organisation_id) VALUES
  ('G6', 'Grade 6', null),
  ('G7', 'Grade 7', null);

INSERT INTO department (code, name) VALUES
  ('co', 'Cabinet Office');

INSERT INTO organisation (code, name, department_id) VALUES
  ('co', 'Cabinet Office', SELECT id FROM department WHERE code = 'co');

SET FOREIGN_KEY_CHECKS = 1;

