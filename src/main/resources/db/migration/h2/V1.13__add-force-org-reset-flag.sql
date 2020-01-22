ALTER TABLE `civil_servant` ADD `force_org_reset` bit(1) DEFAULT FALSE;

UPDATE `civil_servant` SET `force_org_reset` = FALSE WHERE `force_org_reset` = null;