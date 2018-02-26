/*
	Rexion MapleStory
	VMatrixRecord Table
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for vmatrix
-- ----------------------------
DROP TABLE IF EXISTS `vmatrix`;
CREATE TABLE `vmatrix` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `state` tinyint(1) NOT NULL DEFAULT '0',
  `coreid` int(11) NOT NULL DEFAULT '0',
  `skillid` int(11) NOT NULL DEFAULT '0',
  `skillid2` int(11) NOT NULL DEFAULT '0',
  `skillid3` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `masterlevel` int(11) NOT NULL DEFAULT '0',
  `experience` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `characterid` (`characterid`),
  CONSTRAINT `vmatrix_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of vmatrix
-- ----------------------------
