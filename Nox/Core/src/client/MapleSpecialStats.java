package client;

import constants.skills._HyperStatSkills;
import server.maps.objects.MapleCharacter;

/**
 * Defines hyper stat, honor, and hyper skills
 *
 * @author Lloyd Korn
 */
public class MapleSpecialStats {

    public static enum MapleSpecialStatUpdateType {
        Skill_9200("92000000"),
        Skill_9201("92010000"),
        UpdateHonor("honorLeveling"),
        UpdateHyperSkills("hyper"),
        GetHyperStatDistributionPerLevel("incHyperStat"), // Gets the current hyper stat level
        RequiredHyperStatNext("needHyperStatLv"), // Required AP to increase this hyper stat

        Unknown(""),;
        private final String str;

        private MapleSpecialStatUpdateType(String str) {
            this.str = str;
        }

        public String getString() {
            return str;
        }

        public static MapleSpecialStatUpdateType getFromString(String st) {
            for (MapleSpecialStatUpdateType type : values()) {
                if (type.getString().equals(st)) {
                    return type;
                }
            }
            return Unknown;
        }
    }

    public static class MapleHyperStats {

        public static int getRemainingHyperStat(MapleCharacter chr) {
            int usedHyperStatAmount = 0;
            for (int hyperSkill : _HyperStatSkills.ALL_HYPER_STATS) {
                int hyperSkillLevel = chr.getSkillLevel(hyperSkill);

                int requiredStatForThisLevel = 0;
                while (hyperSkillLevel >= 0) {
                    //       chr.dropMessage(5, "Skill lv: "+hyperSkillLevel+" req: " + (getRequiredHyperStatSP(hyperSkillLevel - 1)));
                    requiredStatForThisLevel += getRequiredHyperStatSP(hyperSkillLevel - 1);

                    hyperSkillLevel--;
                }
                usedHyperStatAmount += requiredStatForThisLevel;
            }

            int totalHyperStatForThisLevel = 0;
            int charLevel = chr.getLevel();
            int i = 0;
            while ((i = getHyperStatDistribution(charLevel)) != 0) {
                totalHyperStatForThisLevel += i;

                charLevel--;
            }
            //   chr.dropMessage(5, "Used hyper stat: "+usedHyperStatAmount+", total hyper stat: "+totalHyperStatForThisLevel+", remaining: ("+(totalHyperStatForThisLevel - usedHyperStatAmount)+"");
            return totalHyperStatForThisLevel - usedHyperStatAmount;
        }

        public static int getHyperStatDistribution(int currentLevel) {
            if (currentLevel < 140) {
                return 0;
            }
            if (currentLevel <= 149) {
                return 3/* * ((currentLevel - 140) + 1)*/;
            } else if (currentLevel <= 159) {
                return 4/* * ((currentLevel - 150) + 1)*/;
            } else if (currentLevel <= 169) {
                return 5/* * ((currentLevel - 160) + 1)*/;
            } else if (currentLevel <= 179) {
                return 6/* * ((currentLevel - 170) + 1)*/;
            } else if (currentLevel <= 189) {
                return 7/* * ((currentLevel - 180) + 1)*/;
            } else if (currentLevel <= 199) {
                return 8/* * ((currentLevel - 190) + 1)*/;
            } else if (currentLevel <= 209) {
                return 9/* * ((currentLevel - 200) + 1)*/;
            } else if (currentLevel <= 219) {
                return 10/* * ((currentLevel - 210) + 1)*/;
            } else if (currentLevel <= 229) {
                return 11/* * ((currentLevel - 220) + 1)*/;
            } else if (currentLevel <= 239) {
                return 12/* * ((currentLevel - 230) + 1)*/;
            } else if (currentLevel <= 249) {
                return 13/* * ((currentLevel - 240) + 1)*/;
            }
            return 14;
        }

        public static int getRequiredHyperStatSP(int currentLevel) {
            switch (currentLevel) {
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 4;
                case 3:
                    return 8;
                case 4:
                    return 10;
                case 5:
                    return 15;
                case 6:
                    return 20;
                case 7:
                    return 25;
                case 8:
                    return 30;
                case 9:
                    return 35;
            }
            return -1;
        }
    }
}
