
DROP TABLE IF EXISTS `submissions`;
DROP TABLE IF EXISTS `urls`;
DROP TABLE IF EXISTS `tasks`;

CREATE TABLE `submissions` (
  `submission_id` int(11) NOT NULL,
  `source` varchar(20000) COLLATE utf8_unicode_ci NOT NULL,
  `status` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `language` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `problem` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `urls` (
  `url` varchar(100) NOT NULL,
  PRIMARY KEY (`url`)
);

CREATE TABLE `tasks` (
  `task_id` int NOT NULL AUTO_INCREMENT,
  `url` varchar(100) NOT NULL,
  `scraped` bool NOT NULL,
  PRIMARY KEY (`task_id`)
);
