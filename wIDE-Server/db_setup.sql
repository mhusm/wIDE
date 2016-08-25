CREATE DATABASE IF NOT EXISTS wIDE;
CREATE USER IF NOT EXISTS 'wIDE-cache'@'localhost' IDENTIFIED BY 'P99~mSZ~Dx:q9nN';
GRANT UPDATE ON wIDE.* TO 'wIDE-cache'@'localhost';
GRANT SELECT ON wIDE.* TO 'wIDE-cache'@'localhost';
GRANT INSERT ON wIDE.* TO 'wIDE-cache'@'localhost';


-- -----------------------------------------------------
-- CACHE
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cache` (
	`KEY`			      VARCHAR(50)			NOT NULL,
	`LANG`    		  VARCHAR(10)		  NOT NULL,
	`TYPE`			    VARCHAR(50) 	  NOT NULL,
	`COMPATIBILITY`	MEDIUMTEXT,
	`DOCUMENTATION`	LONGTEXT,
	`parent`        VARCHAR(50),
  `timestamp`     DATE            NOT NULL
);

DROP INDEX `cache_index`;

CREATE UNIQUE INDEX cache_index ON `cache` (`key`, `lang`, `type`, `parent`);


-- -----------------------------------------------------
-- BROWSERS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `browsers` (
  `NAME`          VARCHAR(50)   NOT NULL,
  `MIN_VERSION`   INT(5)        NOT NULL,
  `MAX_VERSION`   INT(5)        NOT NULL
);

-- -----------------------------------------------------
-- BROWSER MARKET-SHARE
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `browser_versions` (
  `NAME`         VARCHAR(50)    NOT NULL,
  `VERSION`      INT(5)         NOT NULL,
  `MARKET_SHARE` DOUBLE         NOT NULL
);