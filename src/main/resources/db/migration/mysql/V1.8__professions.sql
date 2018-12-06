DROP TABLE IF EXISTS `profession_new`;

CREATE TABLE `profession_new` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` smallint(5) unsigned DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_profession_profession` FOREIGN KEY (`parent_id`) REFERENCES `profession_new` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `profession_new` (`parent_id`,`name`) VALUES
(NULL,"Analysis"),
(NULL,"Commercial"),
(NULL,"Communications"),
(NULL,"Corporate finance"),
(NULL,"Digital"),
(NULL,"Finance"),
(NULL,"Fraud, error, debt and grants"),
(NULL,"Human resources"),
(NULL,"Internal audit"),
(NULL,"Legal"),
(NULL,"Operational delivery"),
(NULL,"Project delivery"),
(NULL,"Property"),
(NULL,"I don\'t know"),
(2,"Strategy and Policy Development"),
(2,"Business Needs and Sourcing"),
(2,"Procurement"),
(2,"Contract and Supplier Management"),
(2,"Category Management"),
(15,"Commercial Strategy"),
(15,"Market Maker & Supplier Engagement"),
(15,"Commercial Risk and Assurance Specialist"),
(15,"Commercial Policy Advisor"),
(20,"Commercial Support"),
(20,"Associate Commercial Practitioner"),
(20,"Commercial Practitioner"),
(20,"Commercial Lead"),
(20,"Associate Commercial Specialist"),
(20,"Commercial Specialist"),
(20,"Senior Commercial Specialist");

DROP TABLE IF EXISTS `civil_servant_new`;

CREATE TABLE IF NOT EXISTS `civil_servant_new` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `identity_id` mediumint(8) unsigned NOT NULL,
  `organisational_unit_id` smallint(5) unsigned DEFAULT NULL,
  `grade_id` smallint(5) unsigned DEFAULT NULL,
  `profession_id` smallint(5) unsigned DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `line_manager_id` mediumint(8) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT FOREIGN KEY (`identity_id`) REFERENCES `identity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FOREIGN KEY (`line_manager_id`) REFERENCES `civil_servant` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT FOREIGN KEY (`organisational_unit_id`) REFERENCES `organisational_unit` (`id`),
  CONSTRAINT FOREIGN KEY (`profession_id`) REFERENCES `profession_new` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `civil_servant_new` (`id`, `identity_id`, `organisational_unit_id`, `grade_id`, `profession_id`, `full_name`, `line_manager_id`)
SELECT `a`.`id`, `a`.`identity_id`, `a`.`organisational_unit_id`, `a`.`grade_id`, `c`.`id`, `a`.`full_name`, `a`.`line_manager_id`
FROM `civil_servant` `a`
JOIN `profession` `b` ON `a`.`profession_id` = `b`.`id`
JOIN `profession_new` `c` ON `c`.`name` = `b`.`name`
UNION
SELECT `a`.`id`, `a`.`identity_id`, `a`.`organisational_unit_id`, `a`.`grade_id`, `c`.`id`, `a`.`full_name`, `a`.`line_manager_id`
FROM `civil_servant` `a`
JOIN `job_role` `b` ON `a`.`job_role_id` = `b`.`id`
JOIN `profession_new` `c` ON `c`.`name` = `b`.`name`;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE `civil_servant`;
DROP TABLE `job_role`;
DROP TABLE `profession`;

RENAME TABLE `profession_new` TO `profession`;
RENAME TABLE `civil_servant_new` TO `civil_servant`;

SET FOREIGN_KEY_CHECKS=1;