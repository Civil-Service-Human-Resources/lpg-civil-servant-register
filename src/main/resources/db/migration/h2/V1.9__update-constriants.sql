ALTER TABLE `civil_servant` DROP CONSTRAINT `fk_grade_id`;
ALTER TABLE `civil_servant` ADD CONSTRAINT `fk_grade_id` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `civil_servant` ADD CONSTRAINT `fk_line_manager_id` FOREIGN KEY (`line_manager_id`) REFERENCES `civil_servant` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `civil_servant` DROP CONSTRAINT `fk_organisational_unit_id`;
ALTER TABLE `civil_servant` ADD CONSTRAINT `fk_organisational_unit_id` FOREIGN KEY (`organisational_unit_id`) REFERENCES `organisational_unit` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `civil_servant` DROP CONSTRAINT `fk_profession_id`;
ALTER TABLE `civil_servant` ADD CONSTRAINT `fk_profession_id` FOREIGN KEY (`profession_id`) REFERENCES `profession` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;