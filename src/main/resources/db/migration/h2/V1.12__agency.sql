CREATE TABLE IF NOT EXISTS `agency_token`
(
    `id` SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,
    `token` VARCHAR (10) NOT NULL UNIQUE,
    `domain`  VARCHAR(150) NOT NULL,
    `usage` SMALLINT(5) NOT NULL,
    `capacity` SMALLINT(5) NOT NULL,
    `organisational_unit_id` SMALLINT(5) DEFAULT NULL,
    PRIMARY KEY (`id`),
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `agency_token`
ADD CONSTRAINT `FK_civil_servant_organisational_unit_id` FOREIGN KEY (`organisational_unit_id`) REFERENCES `organisational_unit` (`id`);

SET FOREIGN_KEY_CHECKS=1;
