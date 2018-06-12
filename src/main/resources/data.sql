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

INSERT INTO organisation (code, name, department_id) VALUES
  ('co', 'Cabinet Office', SELECT id FROM department WHERE code = 'co'),
  ('dh', ' Department of Health & Social Care', SELECT id FROM department WHERE code = 'dh'),
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


SET FOREIGN_KEY_CHECKS = 1;
