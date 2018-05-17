SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO grade (code, name, organisation_id) VALUES
  ('G6', 'Grade 6', null),
  ('G7', 'Grade 7', null);

INSERT INTO department (code, name) VALUES
  ('co', 'Cabinet Office');

INSERT INTO organisation (code, name, department_id) VALUES
  ('co', 'Cabinet Office', (SELECT id FROM department WHERE code = 'co'));

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

SELECT id INTO @profession FROM profession WHERE name = 'Commercial';

INSERT INTO job_role (name, profession_id) VALUES
  ('Strategy and Policy Development', @profession),
  ('Business Needs and Sourcing', @profession),
  ('Procurement', @profession),
  ('Contract and Supplier Management', @profession),
  ('Category Management', @profession);
  
SELECT id INTO @parent FROM job_role WHERE name = 'Strategy and Policy Development';

INSERT INTO job_role (name, parent_id) VALUES
  ('Commercial Strategy', @parent),
  ('Market Maker & Supplier Engagement', @parent),
  ('Commercial Risk and Assurance Specialist', @parent),
  ('Commerical Policy Advisor', @parent);
  
SELECT id INTO @parent FROM job_role WHERE name = 'Commercial Strategy';
  
INSERT INTO job_role (name, parent_id) VALUES
  ('Commercial Support', @parent),
  ('Associate Commercial Practitioner', @parent),
  ('Commercial Practitioner', @parent),
  ('Commercial Lead', @parent),
  ('Associate Commercial Specialist', @parent),
  ('Commercial Specialist', @parent),
  ('Senior Commercial Specialist', @parent);

SET FOREIGN_KEY_CHECKS = 1;

