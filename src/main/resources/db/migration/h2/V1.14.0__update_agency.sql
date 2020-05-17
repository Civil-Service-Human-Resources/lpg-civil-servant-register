ALTER TABLE `agency_token` ADD COLUMN `uid` char(36) UNIQUE;

ALTER TABLE `agency_token` DROP COLUMN `capacity_used`;

UPDATE `agency_token` SET `uid` = 'UID123' where id = 1;
UPDATE `agency_token` SET `uid` = 'UID456' where id = 2;
UPDATE `agency_token` SET `uid` = 'UID789' where id = 3;
