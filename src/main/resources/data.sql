SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO grade (code, name, organisation_id) VALUES
	('AA', 'Administrative Assistant',null),
	('AO', 'Administrative Officer',null),
    ('EO', 'Executive Office',null),
   	('G6', 'Grade 6', null),
    ('G7', 'Grade 7', null),
    ('HEO','Higher Executive manager', null),
    ('SEO','Senior Executive Office', null),
    ('PB1', 'Senior Civil Service - Deputy Director', null),
    ('PB2', 'Senior Civil Service - Director', null),
    ('PB3', 'Senior Civil Service - Director General', null),
	('PS',  'Senior Civil Service - Permanent Secretary', null);


INSERT INTO department (code, name) VALUES
	('co', 'Cabinet Office'),
	('dh', 'Department of Health'),
	('hmrc', 'HM Revenue & Customs');

INSERT INTO organisation (code, name, department_id) VALUES
  ('co', 'Cabinet Office', SELECT id FROM department WHERE code = 'co'),
  ('dh', 'Department of Health', SELECT id FROM department WHERE code = 'dh'),
  ('hmrc', 'HM Revenue & Customs', SELECT id FROM department WHERE code = 'hmrc');

INSERT INTO profession (name) VALUES
  ('Analysis'),
  ('Commercial'),
  ('Communications'),
  ('Corporate finance'),
  ('Digital'),
  ('Finance'),
  ('Fraud, error, debt and grants'),
  ('Human resources'),
  ('Internal audit'),
  ('Legal'),
  ('Operational delivery'),
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

/*
21/05 MATT - adding identity and civil_servant inserts for local development of the notifications service.
As we are lazy loading Identities and Civil Servants into CSRS, this creates problems trying to notify users who have never signed-up/logged into LPG
These inserts probably shouldn't be run in any of the environments.
*/
INSERT INTO `identity` (uid) VALUES
('3c706a70-3fff-4e7b-ae7f-102c1d46f569'), ('8dc80f78-9a52-4c31-ac54-d280a70c18eb');

INSERT INTO `civil_servant` (identity_id, organisation_id, grade_id, profession_id, job_role_id, full_name) VALUES
(SELECT id from identity where uid = '3c706a70-3fff-4e7b-ae7f-102c1d46f569',
SELECT id from organisation where code = 'co',
SELECT id from grade where code = 'G7',
SELECT id from profession where name = 'Commercial',
SELECT id from job_role where name = 'Commercial Support',
'Learner'
),
(SELECT id from identity where uid = '8dc80f78-9a52-4c31-ac54-d280a70c18eb',
SELECT id from organisation where code = 'co',
SELECT id from grade where code = 'G6',
SELECT id from profession where name = 'Communications',
SELECT id from job_role where name = 'Commercial Strategy',
'Super'
)
;
SET FOREIGN_KEY_CHECKS = 1;
