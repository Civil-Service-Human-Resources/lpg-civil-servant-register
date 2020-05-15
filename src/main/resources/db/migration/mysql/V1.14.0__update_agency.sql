ALTER TABLE `agency_token` ADD COLUMN `uid` char(36) UNIQUE;

ALTER TABLE `agency_token` DROP COLUMN `capacity_used`;
