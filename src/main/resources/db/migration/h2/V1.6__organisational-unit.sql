DROP TABLE IF EXISTS `organisational_unit`;

CREATE TABLE `organisational_unit` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) unsigned DEFAULT NULL,
  `code` varchar(10) NOT NULL,
  `abbreviation` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `payment_methods` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_name` (`name`),
  UNIQUE KEY `unique_code` (`code`),
  CONSTRAINT `fk_organisational_unit_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `organisational_unit` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `organisational_unit` (`id`, `code`, `name`, `payment_methods`)
  SELECT `id`, `code`, `name`, GROUP_CONCAT(`payment_method`) AS `payment_methods`
  FROM `department`
    JOIN `department_payment_methods` `dpm`
      ON `dpm`.`department_id` = `department`.`id`
  GROUP BY id;
