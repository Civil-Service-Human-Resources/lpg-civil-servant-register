SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `civil_servant`;
TRUNCATE TABLE `department`;
TRUNCATE TABLE `organisation`;
TRUNCATE TABLE `grade`;
TRUNCATE TABLE `identity`;

INSERT INTO grade (code, name, core) VALUES
  ('G6', 'Grade 6', 1),
  ('G7', 'Grade 7', 1);

INSERT INTO department (code, name) VALUES
  ('co', 'Cabinet Office');

INSERT INTO organisation (code, name, department_id) VALUES
  ('co', 'Cabinet Office', SELECT id FROM department WHERE code = 'co');

SET FOREIGN_KEY_CHECKS = 1;

