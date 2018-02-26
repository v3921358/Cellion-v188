/*
	Rexion MapleStory
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for csequipment
-- ----------------------------
DROP TABLE IF EXISTS `csequipment`;
CREATE TABLE `csequipment` (
  `inventoryequipmentid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` bigint(20) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `str` int(6) NOT NULL DEFAULT '0',
  `dex` int(6) NOT NULL DEFAULT '0',
  `int` int(6) NOT NULL DEFAULT '0',
  `luk` int(6) NOT NULL DEFAULT '0',
  `hp` int(6) NOT NULL DEFAULT '0',
  `mp` int(6) NOT NULL DEFAULT '0',
  `watk` int(6) NOT NULL DEFAULT '0',
  `matk` int(6) NOT NULL DEFAULT '0',
  `wdef` int(6) NOT NULL DEFAULT '0',
  `mdef` int(6) NOT NULL DEFAULT '0',
  `acc` int(6) NOT NULL DEFAULT '0',
  `avoid` int(6) NOT NULL DEFAULT '0',
  `hands` int(6) NOT NULL DEFAULT '0',
  `speed` int(6) NOT NULL DEFAULT '0',
  `jump` int(6) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` mediumint(9) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potentialState` int(1) NOT NULL DEFAULT '0',
  `potentialBonusState` int(1) NOT NULL DEFAULT '0',
  `potential1` int(5) NOT NULL DEFAULT '0',
  `potential2` int(5) NOT NULL DEFAULT '0',
  `potential3` int(5) NOT NULL DEFAULT '0',
  `bonus_potential1` int(5) NOT NULL DEFAULT '0',
  `bonus_potential2` int(5) NOT NULL DEFAULT '0',
  `bonus_potential3` int(5) NOT NULL DEFAULT '0',
  `fusionAnvil` int(11) NOT NULL DEFAULT '0',
  `socket1` int(5) NOT NULL DEFAULT '-1',
  `socket2` int(5) NOT NULL DEFAULT '-1',
  `socket3` int(5) NOT NULL DEFAULT '-1',
  `incSkill` int(11) NOT NULL DEFAULT '-1',
  `charmEXP` int(6) NOT NULL DEFAULT '-1',
  `pvpDamage` int(6) NOT NULL DEFAULT '0',
  `enhanctBuff` int(3) NOT NULL DEFAULT '0',
  `reqLevel` int(3) NOT NULL DEFAULT '0',
  `yggdrasilWisdom` tinyint(2) NOT NULL DEFAULT '0',
  `finalStrike` tinyint(2) NOT NULL DEFAULT '0',
  `bossDamage` int(3) NOT NULL DEFAULT '0',
  `ignorePDR` int(3) NOT NULL DEFAULT '0',
  `totalDamage` int(3) NOT NULL DEFAULT '0',
  `allStat` int(3) NOT NULL DEFAULT '0',
  `karmaCount` int(3) NOT NULL DEFAULT '-1',
  `beta` tinyint(1) DEFAULT NULL,
  `starFlag` int(4) DEFAULT '256',
  `arcane` smallint(2) DEFAULT '1',
  `arcaneMaxLevel` smallint(2) DEFAULT '15',
  `arcaneExp` int(11) DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`,`inventoryitemid`),
  KEY `inventoryitemid` (`inventoryitemid`),
  CONSTRAINT `csequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `csitems` (`inventoryitemid`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
