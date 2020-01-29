CREATE TABLE IF NOT EXISTS `agency_domain`
(
    `id`    SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,
    `domain` VARCHAR(150) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


  CREATE TABLE IF NOT EXISTS `agency_token`
(
    `id`    SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,
    `token` VARCHAR (10) NOT NULL UNIQUE,
    `capacity` smallint(5) NOT NULL,
    `capacity_used` smallint(5) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


  CREATE TABLE `agency_token_agency_domains`
(
    `agency_token_id` SMALLINT(5) UNSIGNED NOT NULL,
    `agency_domains_id`  SMALLINT(5) UNSIGNED NOT NULL,
    PRIMARY KEY (`agency_token_id`, `agency_domains_id`),
    CONSTRAINT `FK_token_domain_token` FOREIGN KEY (`agency_token_id`) REFERENCES `agency_token` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_token_domain_domains` FOREIGN KEY (`agency_domains_id`) REFERENCES `agency_domain` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `organisational_unit`
ADD COLUMN agency_token_id SMALLINT(5) UNSIGNED;

ALTER TABLE `organisational_unit`
ADD CONSTRAINT `FK_organisational_unit_agency_token` FOREIGN KEY (`agency_token_id`) REFERENCES `agency_token` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS=1;


