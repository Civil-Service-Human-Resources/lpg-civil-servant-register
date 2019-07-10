CREATE TABLE IF NOT EXISTS `agency_token`
(
    `id`    SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,
    `token` VARCHAR (10) NOT NULL UNIQUE,
    `domain`  char(20) NOT NULL,
    `tokens_used` smallint(5) NOT NULL,
    `capacity` smallint(5) NOT NULL,
    `organisational_unit_id` smallint(5) unsigned,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `agency_token`
ADD CONSTRAINT `FK_agency_organisational_unit_id` FOREIGN KEY (`organisational_unit_id`) REFERENCES `organisational_unit` (`id`) on DELETE CASCADE;

SET FOREIGN_KEY_CHECKS=1;

INSERT INTO agency_token values
(1, 'token123', 'domain.com', 100, 10, 1),
(2, 'token2', 'example.com', 200, 15, 1),
(3, 'token3', 'kainos.com', 300, 0, 1)
;
