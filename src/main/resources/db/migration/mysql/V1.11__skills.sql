CREATE TABLE IF NOT EXISTS `choice`
(
    `id`    SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,
    `value` TEXT                  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `question`
(
    `id`    SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,
    `type`  VARCHAR(8),
    `value` TEXT                  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `question_answers`
(
    `question_id` SMALLINT(5) UNSIGNED NOT NULL,
    `answers_id`  SMALLINT(5) UNSIGNED NOT NULL,
    PRIMARY KEY (`question_id`, `answers_id`),
    CONSTRAINT `FK_question_answers_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_question_answers_answer` FOREIGN KEY (`answers_id`) REFERENCES `choice` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `question_choices`
(
    `question_id` SMALLINT(5) UNSIGNED NOT NULL,
    `choices_id`  SMALLINT(5) UNSIGNED NOT NULL,
    PRIMARY KEY (`question_id`, `choices_id`),
    CONSTRAINT `FK_question_choices_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_question_choices_choice` FOREIGN KEY (`choices_id`) REFERENCES `choice` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `quiz`
(
    `id` SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,
    `profession_id`  SMALLINT(5) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_quiz_profession` FOREIGN KEY (`profession_id`) REFERENCES `profession` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `quiz_questions`
(
    `quiz_id` SMALLINT(5) UNSIGNED NOT NULL,
    `questions_id`  SMALLINT(5) UNSIGNED NOT NULL,
    PRIMARY KEY (`quiz_id`, `questions_id`),
    CONSTRAINT `FK_quiz_questions_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_quiz_questions_question` FOREIGN KEY (`questions_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

UPDATE `organisational_unit` SET `payment_methods` = 'PURCHASE_ORDER';
ALTER TABLE `organisational_unit` ALTER `payment_methods`SET DEFAULT 'PURCHASE_ORDER';

