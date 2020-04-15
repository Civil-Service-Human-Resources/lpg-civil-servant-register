DROP TABLE IF EXISTS `civil_servant_organisation_reporting_permission`;

CREATE TABLE `civil_servant_organisation_reporting_permission` (
  `civil_servant_id` mediumint(8) unsigned NOT NULL,
  `organisation_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`civil_servant_id`,`organisation_id`),
  KEY `FK_civil_servant_organisation_id` (`organisation_id`),
  CONSTRAINT `FK_civil_servant_organisation_id_civil_servant` FOREIGN KEY (`civil_servant_id`) REFERENCES `civil_servant` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_civil_servant_organisation_id_profession` FOREIGN KEY (`organisation_id`) REFERENCES `organisational_unit` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8