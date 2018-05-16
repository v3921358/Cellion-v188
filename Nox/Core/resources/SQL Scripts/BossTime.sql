-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.1.26-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win32
-- HeidiSQL Version:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for nox
CREATE DATABASE IF NOT EXISTS `nox` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `nox`;

-- Dumping structure for table nox.bosstime
CREATE TABLE IF NOT EXISTS `bosstime` (
  `nIncrementKey` int(11) NOT NULL AUTO_INCREMENT,
  `dwAccountID` int(11) NOT NULL DEFAULT '0',
  `tHilla` bigint(20) NOT NULL DEFAULT '0',
  `tZakum` bigint(20) NOT NULL DEFAULT '0',
  `tHorntail` bigint(20) NOT NULL DEFAULT '0',
  `tRanmaru` bigint(20) NOT NULL DEFAULT '0',
  `tCrimsonQueen` bigint(20) NOT NULL DEFAULT '0',
  `tPierre` bigint(20) NOT NULL DEFAULT '0',
  `tVonBon` bigint(20) NOT NULL DEFAULT '0',
  `tVellum` bigint(20) NOT NULL DEFAULT '0',
  `tLotus` bigint(20) NOT NULL DEFAULT '0',
  `tUrsus` bigint(20) NOT NULL DEFAULT '0',
  `tArkarium` bigint(20) NOT NULL DEFAULT '0',
  `tCygnus` bigint(20) NOT NULL DEFAULT '0',
  `tVonLeon` bigint(20) NOT NULL DEFAULT '0',
  `tPrincessNo` bigint(20) NOT NULL DEFAULT '0',
  `tGollux` bigint(20) NOT NULL DEFAULT '0',
  `tDamien` bigint(20) NOT NULL DEFAULT '0',
  `tPinkBean` bigint(20) NOT NULL DEFAULT '0',
  `tMagnus` bigint(20) NOT NULL DEFAULT '0',
  `tLucid` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nIncrementKey`),
  KEY `dwAccountID` (`dwAccountID`),
  CONSTRAINT `bosstime_ibfk_1` FOREIGN KEY (`dwAccountID`) REFERENCES `accounts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2582 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
