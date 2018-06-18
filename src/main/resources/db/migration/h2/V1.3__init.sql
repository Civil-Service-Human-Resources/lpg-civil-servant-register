CREATE TABLE IF NOT EXISTS `interest` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE IF NOT EXISTS `civil_servant_interests` (
  `civil_servant_id` mediumint(8) NOT NULL,
  `interests_id` smallint(5) NOT NULL,
  PRIMARY KEY (`civil_servant_id`,`interests_id`),
  CONSTRAINT `FK_civil_servant_interests_civil_servant` FOREIGN KEY (`civil_servant_id`) REFERENCES `civil_servant` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_civil_servant_interests_interest` FOREIGN KEY (`interests_id`) REFERENCES `interest` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);