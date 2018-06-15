SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO grade (code, name, organisation_id) VALUES
	('AA', 'Administrative Assistant', null),
	('AO', 'Administrative Officer', null),
  ('EO', 'Executive Officer', null),
  ('G6', 'Grade 6', null),
  ('G7', 'Grade 7', null),
  ('HEO','Higher Executive Officer', null),
  ('SEO','Senior Executive Officer', null),
  ('PB1', 'Senior Civil Service - Deputy Director', null),
  ('PB2', 'Senior Civil Service - Director', null),
  ('PB3', 'Senior Civil Service - Director General', null),
	('PS',  'Senior Civil Service - Permanent Secretary', null);


INSERT INTO department (code, name) VALUES
	('co', 'Cabinet Office'),
	('dh', 'Department of Health & Social Care'),
	('hmrc', 'HM Revenue & Customs');

INSERT INTO organisation (code, name, department_id) SELECT 'co', 'Cabinet Office', id FROM department WHERE code = 'co';
INSERT INTO organisation (code, name, department_id) SELECT 'dh', ' Department of Health & Social Care', id FROM department WHERE code = 'co';
INSERT INTO organisation (code, name, department_id) SELECT 'hmrc', 'HM Revenue & Customs', id FROM department WHERE code = 'co';

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

INSERT INTO job_role (name, profession_id) SELECT 'Strategy and Policy Development', id FROM profession WHERE name = 'Commercial';
INSERT INTO job_role (name, profession_id) SELECT 'Business Needs and Sourcing', id FROM profession WHERE name = 'Commercial';
INSERT INTO job_role (name, profession_id) SELECT 'Procurement', id FROM profession WHERE name = 'Commercial';
INSERT INTO job_role (name, profession_id) SELECT 'Contract and Supplier Management', id FROM profession WHERE name = 'Commercial';
INSERT INTO job_role (name, profession_id) SELECT 'Category Management', id FROM profession WHERE name = 'Commercial';

INSERT INTO job_role (name, parent_id) SELECT 'Commercial Strategy', id FROM job_role WHERE name = 'Strategy and Policy Development';
INSERT INTO job_role (name, parent_id) SELECT 'Market Maker & Supplier Engagement', id FROM job_role WHERE name = 'Strategy and Policy Development';
INSERT INTO job_role (name, parent_id) SELECT 'Commercial Risk and Assurance Specialist', id FROM job_role WHERE name = 'Strategy and Policy Development';
INSERT INTO job_role (name, parent_id) SELECT 'Commercial Policy Advisor', id FROM job_role WHERE name = 'Strategy and Policy Development';

INSERT INTO job_role (name, parent_id) SELECT 'Commercial Support', id FROM job_role WHERE name = 'Commercial Strategy';
INSERT INTO job_role (name, parent_id) SELECT 'Associate Commercial Practitioner', id FROM job_role WHERE name = 'Commercial Strategy';
INSERT INTO job_role (name, parent_id) SELECT 'Commercial Practitioner', id FROM job_role WHERE name = 'Commercial Strategy';
INSERT INTO job_role (name, parent_id) SELECT 'Commercial Lead', id FROM job_role WHERE name = 'Commercial Strategy';
INSERT INTO job_role (name, parent_id) SELECT 'Associate Commercial Specialist', id FROM job_role WHERE name = 'Commercial Strategy';
INSERT INTO job_role (name, parent_id) SELECT 'Commercial Specialist', id FROM job_role WHERE name = 'Commercial Strategy';
INSERT INTO job_role (name, parent_id) SELECT 'Senior Commercial Specialist', id FROM job_role WHERE name = 'Commercial Strategy';

SET FOREIGN_KEY_CHECKS = 1;
