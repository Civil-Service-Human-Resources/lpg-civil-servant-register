CREATE TABLE IF NOT EXISTS `agency_token`
(
    `id`    SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,
    `token` VARCHAR (10) NOT NULL UNIQUE,
    `domain`  char(20) NOT NULL,
    `tokens_used` smallint(5) NOT NULL,
    `capacity` smallint(5) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `organisational_unit`
ADD COLUMN agency_token_id SMALLINT(5) UNSIGNED;

ALTER TABLE `organisational_unit`
ADD CONSTRAINT `FK_organisational_unit_agency_token` FOREIGN KEY (`agency_token_id`) REFERENCES `agency_token` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS=1;