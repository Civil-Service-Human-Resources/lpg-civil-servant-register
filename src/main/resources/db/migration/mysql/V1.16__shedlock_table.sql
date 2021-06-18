CREATE TABLE `shedlock`
(
    `name`       VARCHAR(64)  NOT NULL,
    `lock_until` TIMESTAMP    NOT NULL,
    `locked_at`  TIMESTAMP    NOT NULL DEFAULT NOW(),
    `locked_by`  VARCHAR(255) NOT NULL,
    PRIMARY KEY (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
