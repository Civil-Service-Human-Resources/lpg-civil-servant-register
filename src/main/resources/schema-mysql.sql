/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for csrs
DROP DATABASE IF EXISTS `csrs`;
CREATE DATABASE IF NOT EXISTS `csrs` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `csrs`;

-- Dumping structure for table csrs.civil_servant
DROP TABLE IF EXISTS `civil_servant`;
CREATE TABLE IF NOT EXISTS `civil_servant` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `identity_id` mediumint(8) unsigned NOT NULL,
  `organisation_id` smallint(5) unsigned DEFAULT NULL,
  `grade_id` smallint(5) unsigned DEFAULT NULL,
  `profession_id` smallint(5) unsigned DEFAULT NULL,
  `job_role_id` smallint(5) unsigned DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `line_manager_id` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_civil_servant_identity` (`identity_id`),
  KEY `FK_civil_servant_organisation` (`organisation_id`),
  KEY `FK_civil_servant_grade` (`grade_id`),
  KEY `FK_civil_servant_profession` (`profession_id`),
  KEY `FK_civil_servant_job_role` (`job_role_id`),
  KEY `FK_civil_servant_line_manager` (`line_manager_id`),
  CONSTRAINT `FK_civil_servant_grade` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `FK_civil_servant_identity` FOREIGN KEY (`identity_id`) REFERENCES `identity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_civil_servant_job_role` FOREIGN KEY (`job_role_id`) REFERENCES `job_role` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `FK_civil_servant_organisation` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `FK_civil_servant_profession` FOREIGN KEY (`profession_id`) REFERENCES `profession` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
  CONSTRAINT `FK_civil_servant_line_manager` FOREIGN KEY (`line_manager_id`) REFERENCES `civil_servant` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.civil_servant: ~0 rows (approximately)
/*!40000 ALTER TABLE `civil_servant` DISABLE KEYS */;
/*!40000 ALTER TABLE `civil_servant` ENABLE KEYS */;

-- Dumping structure for table csrs.civil_servant_other_areas_of_work
DROP TABLE IF EXISTS `civil_servant_other_areas_of_work`;
CREATE TABLE IF NOT EXISTS `civil_servant_other_areas_of_work` (
  `civil_servant_id` mediumint(8) unsigned NOT NULL,
  `other_areas_of_work_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`civil_servant_id`,`other_areas_of_work_id`),
  KEY `FK_civil_servant_other_areas_of_work_profession` (`other_areas_of_work_id`),
  CONSTRAINT `FK_civil_servant_other_areas_of_work_civil_servant` FOREIGN KEY (`civil_servant_id`) REFERENCES `civil_servant` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_civil_servant_other_areas_of_work_profession` FOREIGN KEY (`other_areas_of_work_id`) REFERENCES `profession` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.civil_servant_other_areas_of_work: ~0 rows (approximately)
/*!40000 ALTER TABLE `civil_servant_other_areas_of_work` DISABLE KEYS */;
/*!40000 ALTER TABLE `civil_servant_other_areas_of_work` ENABLE KEYS */;

-- Dumping structure for table csrs.department
DROP TABLE IF EXISTS `department`;
CREATE TABLE IF NOT EXISTS `department` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `code` char(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.department: ~0 rows (approximately)
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
/*!40000 ALTER TABLE `department` ENABLE KEYS */;

-- Dumping structure for table csrs.department_organisations
DROP TABLE IF EXISTS `department_organisations`;
CREATE TABLE IF NOT EXISTS `department_organisations` (
  `department_id` smallint(6) unsigned NOT NULL,
  `organisations_id` smallint(6) unsigned NOT NULL,
  PRIMARY KEY (`department_id`,`organisations_id`),
  KEY `FK_department_organisations_department` (`department_id`),
  KEY `FK_department_organisations_organisation` (`organisations_id`),
  CONSTRAINT `FK_department_organisations_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_department_organisations_organisation` FOREIGN KEY (`organisations_id`) REFERENCES `organisation` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.department_organisations: ~0 rows (approximately)
/*!40000 ALTER TABLE `department_organisations` DISABLE KEYS */;
/*!40000 ALTER TABLE `department_organisations` ENABLE KEYS */;

-- Dumping structure for table csrs.grade
DROP TABLE IF EXISTS `grade`;
CREATE TABLE IF NOT EXISTS `grade` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `organisation_id` smallint(5) unsigned DEFAULT NULL,
  `code` char(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `FK_grade_organisation` (`organisation_id`),
  CONSTRAINT `FK_grade_organisation` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.grade: ~0 rows (approximately)
/*!40000 ALTER TABLE `grade` DISABLE KEYS */;
/*!40000 ALTER TABLE `grade` ENABLE KEYS */;

-- Dumping structure for table csrs.identity
DROP TABLE IF EXISTS `identity`;
CREATE TABLE IF NOT EXISTS `identity` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `uid` char(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.identity: ~0 rows (approximately)
/*!40000 ALTER TABLE `identity` DISABLE KEYS */;
/*!40000 ALTER TABLE `identity` ENABLE KEYS */;

-- Dumping structure for table csrs.job_role
DROP TABLE IF EXISTS `job_role`;
CREATE TABLE IF NOT EXISTS `job_role` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` smallint(5) unsigned DEFAULT NULL,
  `profession_id` smallint(5) unsigned DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_job_role_job_role` (`parent_id`),
  KEY `FK_job_role_profession` (`profession_id`),
  CONSTRAINT `FK_job_role_job_role` FOREIGN KEY (`parent_id`) REFERENCES `job_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_job_role_profession` FOREIGN KEY (`profession_id`) REFERENCES `profession` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.job_role: ~0 rows (approximately)
/*!40000 ALTER TABLE `job_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `job_role` ENABLE KEYS */;

-- Dumping structure for table csrs.organisation
DROP TABLE IF EXISTS `organisation`;
CREATE TABLE IF NOT EXISTS `organisation` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `department_id` smallint(5) unsigned DEFAULT NULL,
  `code` char(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `FK_organisation_department` (`department_id`),
  CONSTRAINT `FK_organisation_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.organisation: ~0 rows (approximately)
/*!40000 ALTER TABLE `organisation` DISABLE KEYS */;
/*!40000 ALTER TABLE `organisation` ENABLE KEYS */;

-- Dumping structure for table csrs.profession
DROP TABLE IF EXISTS `profession`;
CREATE TABLE IF NOT EXISTS `profession` (
  `id` smallint(6) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table csrs.profession: ~0 rows (approximately)
/*!40000 ALTER TABLE `profession` DISABLE KEYS */;
/*!40000 ALTER TABLE `profession` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
