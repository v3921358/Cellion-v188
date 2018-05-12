package handling.game;

import client.ClientSocket;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.world.MaplePartyCharacter;
import java.awt.Rectangle;
import server.MapleItemInformationProvider;
import server.StatEffect;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.objects.User;
import server.maps.objects.Pet;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.PetPacket;

/**
 * For all other stuff that's shared by multiple handlers
 *
 * @author
 */
public class _CommonPlayerOperationHandler {

    private static int[] dmgskinitem = {2431965,
        2431966,
        2432084,
        2431967,
        2432131,
        2432153,
        2432638,
        2432659,
        2432154,
        2432637,
        2432658,
        2432207,
        2432354,
        2432355,
        2432972,
        2432465,
        2432479,
        2432526,
        2432639,
        2432660,
        2432532,
        2432592,
        2432640,
        2432661,
        2432710,
        2432836,
        2432973,
        2433063,
        2433456,
        2433178,
        2433631,
        2433715,
        2433804,
        5680343,
        2433913,
        2433980,
        2433981,
        2433990,
        2432591,
        2433267,
        2433268,
        2432803,
        2432804,
        2432846,
        2433081,
        2433901,
        2433113,
        2433049,
        2433038,
        2433165,
        2433197,
        2433195,
        2433182,
        2433183,
        2433184,
        2433214,
        2433182,
        2433236,
        2433900,
        2433902,
        2433588,
        2433907,
        2433906,
        2433905,
        2433904,
        2433903,
        2433777,
        2434796,
        2433775,
        2433776,
        2433828,
        2433829,
        2433830,
        2433831,
        2433832,
        2433833,
        2433883,
        2434004,
        2434147,
        2434157,
        2434375,
        2434601,
        2434533,
        2434534,
        2434544,
        2434545,
        2434570,
        2434619,
        2434662,
        2434663,
        2434664,
        2434868,
        3801002,
        2434817,
        2434818,
        2435194,
        2435195,
        2435193,
        2435213,
        5680343,
        2433260,
        2433269,
        2433270,
        2433252,
        2433251,
        2433708,
        2433709,
        2434374,
        2434499,
        2433262,
        2433263,
        2433265,
        2433266,
        2433897,
        2433362,
        2433113,
        2434274,
        2434289,
        2434289,
        2434390,
        2434391,
        2433261,
        2433264,
        2433261};

    private static int[] dmgskinnum = {0,
        1,
        1,
        2,
        3,
        4,
        4,
        4,
        5,//甜蜜餅乾字型
        5,//傳統韓果傷害字型
        5,//傳統韓果傷害字型
        6,//鐵壁城牆字型
        7,//聖誕快樂傷害字型
        8,//雪花飄落字型
        8,//雪花傷害肌膚
        9,//愛麗西亞的傷害字型
        10,//桃樂絲的傷害字型
        11,//鍵盤戰士字型
        11,//鍵盤戰士傷害字型
        11,//鍵盤戰士傷害字型
        12,//多彩春風字型
        13,//單身部隊傷害字型
        14,//雷咪納杉司傷害字型
        14,//雷咪納杉司傷害字型
        15,//菇菇寶貝傷害字型
        16,//王冠傷害字型
        17,//灰白傷害肌膚
        18,//明星星球傷害肌膚
        19,//韓文的傷害字型(效果是中文)
        20,//2014萬聖節傷害肌膚
        //21,//韓文的傷害字型
        22,//NENE雞的傷害字型
        23,//NENE雞的傷害字型
        24,
        25,
        26,
        27,
        28,//皮卡啾傷害字型
        29,
        1001,//GoldenDamageSkin
        1002,
        1003,
        1004,//濃姬傷害字型(30日)
        1004,//濃姬傷害字型(無限期)
        1005,//傑特字型交換卷
        1006,
        1007,
        1008,
        1009,//初音未來傷害字型
        1010,//皇家神獸學院字型
        1011,//俠客字型交換卷
        1012,//菲歐娜字型交換卷
        1013,//橘子節字型交換卷
        1014,//萬聖節南瓜字型交換卷
        1015,//萬聖節幽靈字型交換卷
        1016,//萬聖節掃把字型交換卷
        1017,
        1018,
        1019,
        1020,
        1021,
        1023,
        1024,
        1025,
        1026,
        1027,
        1028,
        1031,
        1031,
        1032,//殺人鯨傷害字型
        1033,//史烏傷害字型
        1034,
        1035,
        1036,
        1037,
        1038,
        1039,
        1040,
        1041,//小筱傷害字型
        1042,
        1043,
        1045,
        1050,
        1051,
        1052,
        1053,
        1054,
        1056,
        1057,
        1058,
        1059,
        1060,
        1062,
        1063,
        1068,
        1069,
        1075,
        1076,
        1077,
        1080,
        25,
        1,
        1001,
        1005,
        1011,
        1012,
        1029,
        1030,
        1048,
        1049,
        11,
        13,
        14,
        15,
        17,
        35,
        36,
        36,
        37,
        37,
        38,
        39,
        4,
        5,
        4};

    public static boolean isAllowedPotentialStat(Equip eqq, int opID) { //For now
        //if (GameConstants.isWeapon(eqq.getItemId())) {
        //    return !(opID > 60000) || (opID >= 1 && opID <= 4) || (opID >= 9 && opID <= 12) || (opID >= 10001 && opID <= 10006) || (opID >= 10011 && opID <= 10012) || (opID >= 10041 && opID <= 10046) || (opID >= 10051 && opID <= 10052) || (opID >= 10055 && opID <= 10081) || (opID >= 10201 && opID <= 10291) || (opID >= 210001 && opID <= 20006) || (opID >= 20011 && opID <= 20012) || (opID >= 20041 && opID <= 20046) || (opID >= 20051 && opID <= 20052) || (opID >= 20055 && opID <= 20081) || (opID >= 20201 && opID <= 20291) || (opID >= 30001 && opID <= 30006) || (opID >= 30011 && opID <= 30012) || (opID >= 30041 && opID <= 30046) || (opID >= 30051 && opID <= 30052) || (opID >= 30055 && opID <= 30081) || (opID >= 30201 && opID <= 30291) || (opID >= 40001 && opID <= 40006) || (opID >= 40011 && opID <= 40012) || (opID >= 40041 && opID <= 40046) || (opID >= 40051 && opID <= 40052) || (opID >= 40055 && opID <= 40081) || (opID >= 40201 && opID <= 40291);
        //}
        return opID < 60000;
    }

    public static final boolean UseTeleRock(InPacket iPacket, ClientSocket c, int itemId) {
        boolean used = false;
        if (itemId == 5040004) {
            iPacket.DecodeByte();
        }
        if ((itemId == 5040004) || itemId == 5041001) {
            if (iPacket.DecodeByte() == 0) {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(iPacket.DecodeInt());
                if (target != null) { //Premium and Hyper rocks are allowed to go anywhere. Blocked maps are checked below. 
                    if (!FieldLimitType.VipRock.checkFlag(c.getPlayer().getMap()) && !FieldLimitType.VipRock.checkFlag(target) && !c.getPlayer().isInBlockedMap()) { //Makes sure this map doesn't have a forced return map
                        c.getPlayer().changeMap(target, target.getPortal(0));
                        if (itemId == 5041001) {
                            used = true;
                        }
                    } else {
                        c.getPlayer().dropMessage(1, "You cannot go to that place.");
                    }
                } else {
                    c.getPlayer().dropMessage(1, "The place you want to go to does not exist.");
                }
            } else {
                final String name = iPacket.DecodeString();
                final User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                if (victim != null && !victim.isIntern() && c.getPlayer().getEventInstance() == null && victim.getEventInstance() == null) {
                    if (!FieldLimitType.VipRock.checkFlag(c.getPlayer().getMap()) && !FieldLimitType.VipRock.checkFlag(c.getChannelServer().getMapFactory().getMap(victim.getMapId())) && !victim.isInBlockedMap() && !c.getPlayer().isInBlockedMap()) {
                        c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestPortal(victim.getTruePosition()));
                        if (itemId == 5041001) {
                            used = true;
                        }
                    } else {
                        c.getPlayer().dropMessage(1, "You cannot go to where that person is.");
                    }
                } else if (victim == null) {
                    c.getPlayer().dropMessage(1, "(" + name + ") is either offline or in a different channel.");
                } else {
                    c.getPlayer().dropMessage(1, "(" + name + ") is currently difficult to locate, so the teleport will not take place.");
                }
            }
        } else if (itemId == 5040004) {
            c.getPlayer().dropMessage(1, "You are not able to use this teleport rock.");
        }
        return used;
    }

    public static final boolean useItem(final ClientSocket c, final int id) {
        if (GameConstants.isUse(id)) { // TO prevent caching of everything, waste of mem
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final StatEffect eff = ii.getItemEffect(id);
            if (eff == null) {
                return false;
            }
            //must hack here for ctf
            if (id / 10000 == 291) {
                boolean area = false;
                for (Rectangle rect : c.getPlayer().getMap().getSharedMapResources().areas) {
                    if (rect.contains(c.getPlayer().getTruePosition())) {
                        area = true;
                        break;
                    }
                }
                if (!c.getPlayer().inPVP() || (c.getPlayer().getTeam() == (id - 2910000) && area)) {
                    return false; //dont apply the consume
                }
            }
            final int consumeval = eff.getConsume();

            if (consumeval > 0) {
                consumeItem(c, eff);
                consumeItem(c, ii.getItemEffectEX(id));
                c.SendPacket(WvsContext.InfoPacket.getShowItemGain(id, (byte) 1));
                return true;
            }
        }
        return false;
    }

    public static final void consumeItem(final ClientSocket c, final StatEffect eff) {
        if (eff == null) {
            return;
        }
        if (eff.getConsume() == 2) {
            if (c.getPlayer().getParty() != null && c.getPlayer().isAlive()) {
                for (final MaplePartyCharacter pc : c.getPlayer().getParty().getMembers()) {
                    final User chr = c.getPlayer().getMap().getCharacterById(pc.getId());
                    if (chr != null && chr.isAlive()) {
                        eff.applyTo(chr);
                    }
                }
            } else {
                eff.applyTo(c.getPlayer());
            }
        } else if (c.getPlayer().isAlive()) {
            eff.applyTo(c.getPlayer());
        }
    }

    public static final boolean UsePetFood(ClientSocket c, int itemId) {
        Pet pet = c.getPlayer().getPet(0);
        if (pet == null) {
            return false;
        }
        if (!pet.canConsume(itemId)) {
            pet = c.getPlayer().getPet(1);
            if (pet != null) {
                if (!pet.canConsume(itemId)) {
                    pet = c.getPlayer().getPet(2);
                    if (pet != null) {
                        if (!pet.canConsume(itemId)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        final byte petindex = c.getPlayer().getPetIndex(pet);
        pet.setFullness(100);
        if (pet.getCloseness() < 30000) {
            if (pet.getCloseness() + (100 * c.getChannelServer().getTraitRate()) > 30000) {
                pet.setCloseness(30000);
            } else {
                pet.setCloseness((int) (pet.getCloseness() + (100 * c.getChannelServer().getTraitRate())));
            }
            if (pet.getCloseness() >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                pet.setLevel(pet.getLevel() + 1);
                c.SendPacket(CField.EffectPacket.showOwnPetLevelUp(null, c.getPlayer().getPetIndex(pet)));
                c.getPlayer().getMap().broadcastPacket(PetPacket.showPetLevelUp(c.getPlayer(), petindex));
            }
        }
        // c.getPlayer().forceUpdateItem(pet.getItem());
        c.SendPacket(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getItem().getPosition()), false));
        c.getPlayer().getMap().broadcastPacket(c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte) 1, petindex, true, true), true);
        return true;
    }
}
