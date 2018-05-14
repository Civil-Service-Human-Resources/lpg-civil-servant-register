SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO grade (code, name, organisation_id) VALUES
  ('G6', 'Grade 6', null),
  ('G7', 'Grade 7', null);

INSERT INTO department (code, name) VALUES
  ('co', 'Cabinet Office');

INSERT INTO organisation (code, name, department_id) VALUES
  ('co', 'Cabinet Office', SELECT id FROM department WHERE code = 'co');

INSERT INTO profession (name) VALUES
  ('Commercial'),
  ('Communications'),
  ('Corporate finance'),
  ('Digital, Data and Technology'),
  ('Finance'),
  ('Fraud, error, debt and grants'),
  ('Human resources'),
  ('Internal audit'),
  ('Legal'),
  ('Project delivery'),
  ('Property'),
  ('Other');

INSERT INTO job_role (name, profession_id) VALUES
  ('Strategy and Policy Development', SELECT id FROM profession WHERE name = 'Commercial'),
  ('Business Needs and Sourcing', SELECT id FROM profession WHERE name = 'Commercial'),
  ('Procurement', SELECT id FROM profession WHERE name = 'Commercial'),
  ('Contract and Supplier Management', SELECT id FROM profession WHERE name = 'Commercial'),
  ('Category Management', SELECT id FROM profession WHERE name = 'Commercial');

INSERT INTO job_role (name, parent_id) VALUES
  ('Commercial Strategy', SELECT id FROM job_role WHERE name = 'Strategy and Policy Development'),
  ('Market Maker & Supplier Engagement', SELECT id FROM job_role WHERE name = 'Strategy and Policy Development'),
  ('Commercial Risk and Assurance Specialist', SELECT id FROM job_role WHERE name = 'Strategy and Policy Development'),
  ('Commerical Policy Advisor', SELECT id FROM job_role WHERE name = 'Strategy and Policy Development');

INSERT INTO job_role (name, parent_id) VALUES
  ('Commercial Support', SELECT id FROM job_role WHERE name = 'Commercial Strategy'),
  ('Associate Commercial Practitioner', SELECT id FROM job_role WHERE name = 'Commercial Strategy'),
  ('Commercial Practitioner', SELECT id FROM job_role WHERE name = 'Commercial Strategy'),
  ('Commercial Lead', SELECT id FROM job_role WHERE name = 'Commercial Strategy'),
  ('Associate Commercial Specialist', SELECT id FROM job_role WHERE name = 'Commercial Strategy'),
  ('Commercial Specialist', SELECT id FROM job_role WHERE name = 'Commercial Strategy'),
  ('Senior Commercial Specialist', SELECT id FROM job_role WHERE name = 'Commercial Strategy');

SET FOREIGN_KEY_CHECKS = 1;

