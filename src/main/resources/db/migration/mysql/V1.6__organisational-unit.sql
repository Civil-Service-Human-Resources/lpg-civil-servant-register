DROP TABLE IF EXISTS `organisational_unit`;

CREATE TABLE `organisational_unit` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` smallint(5) unsigned DEFAULT NULL,
  `code` varchar(10) NOT NULL,
  `abbreviation` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `payment_methods` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_name` (`name`),
  UNIQUE KEY `unique_code` (`code`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `FK_organisational_unit_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `organisational_unit` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `organisational_unit` (`id`, `code`, `name`, `payment_methods`)
  SELECT `id`, `code`, `name`, GROUP_CONCAT(`payment_method`) AS `payment_methods`
  FROM `department`
    JOIN `department_payment_methods` `dpm`
      ON `dpm`.`department_id` = `department`.`id`
  GROUP BY id;

ALTER TABLE `grade` DROP FOREIGN KEY `FK_grade_organisation`;
ALTER TABLE `grade` CHANGE `organisation_id` `organisational_unit_id` smallint(5) unsigned DEFAULT NULL;
ALTER TABLE `grade` ADD CONSTRAINT `FK_grade_organisational_unit_id` FOREIGN KEY (`organisational_unit_id`) REFERENCES `organisational_unit` (`id`);

ALTER TABLE `civil_servant` DROP FOREIGN KEY `FK_civil_servant_organisation`;
ALTER TABLE `civil_servant` CHANGE `organisation_id` `organisational_unit_id` smallint(5) unsigned DEFAULT NULL;
ALTER TABLE `civil_servant` ADD CONSTRAINT `FK_civil_servant_organisational_unit_id` FOREIGN KEY (`organisational_unit_id`) REFERENCES `organisational_unit` (`id`);

DROP TABLE `organisation`;
DROP TABLE `department`;
