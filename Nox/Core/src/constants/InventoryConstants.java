package constants;

import constants.ItemConstants;

public class InventoryConstants {

    public static final int PET_COME = 128;
    public static final int UNKNOWN_SKILL = 256;

    public static boolean idMatchesSlot(int itemid, short slot) {
        return ((slot != -11) || (isWeapon(itemid))) && ((slot != -18) || (isMount(itemid))) && ((slot != -6) || (isBottom(itemid))) && ((slot != -10) || (isShield(itemid)) || (itemid / 10000 == 135)) && ((slot != -49) || (itemid / 10000 == 114)) && ((slot != -7) || (isShoe(itemid))) && ((slot != -5) || (isTop(itemid)) || (isOverall(itemid))) && ((slot != -4) || (isEarring(itemid))) && ((slot != -1) || (isHat(itemid))) && ((slot != -8) || (isGlove(itemid))) && ((slot != -9) || (isCape(itemid))) && ((slot != -13) || (isFaceAccessory(itemid))) && ((slot == -12) || (slot == -13) || (slot == -14) || (slot == -15) || (slot == -112) || (slot == -113) || (slot == -114) || (slot == -115)) && (isRing(itemid)) && ((slot != -19) || (isSaddle(itemid))) && ((slot != -17) || (isNeckAccessory(itemid))) && ((slot != -50) || (isBelt(itemid))) && ((slot == -123) || (slot == -124) || (slot == -125)) && (isPetEquip(itemid)) && ((slot != 55) || (itemid == 1172000));
    }

    public static boolean isAmmo(int nItemId) {
        return (isThrowingStar(nItemId) || isBullet(nItemId) || isArrowForBow(nItemId) || isArrowForCrossBow(nItemId));
    }

    public static boolean isRechargeable(int itemid) {
        return (isThrowingStar(itemid)) || (isBullet(itemid));
    }

    public static boolean isThrowingStar(int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean isBullet(int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean isArrowForCrossBow(int itemId) {
        return itemId / 1000 == 2061;
    }

    public static boolean isArrowForBow(int itemId) {
        return itemId / 1000 == 2060;
    }

    public static boolean isOverall(int itemId) {
        return itemId / 10000 == 105;
    }

    public static boolean isHat(int itemid) {
        return itemid / 10000 == 100;
    }

    public static boolean isFaceAccessory(int itemid) {
        return itemid / 10000 == 101;
    }

    public static boolean isTop(int itemid) {
        return itemid / 10000 == 104;
    }

    public static boolean isBottom(int itemid) {
        return itemid / 10000 == 106;
    }

    public static boolean isShoe(int itemid) {
        return itemid / 10000 == 107;
    }

    public static boolean isGlove(int itemid) {
        return itemid / 10000 == 108;
    }

    public static boolean isCape(int itemid) {
        return itemid / 10000 == 110;
    }

    public static boolean isNeckAccessory(int itemid) {
        return itemid / 10000 == 112;
    }

    public static boolean isBelt(int itemid) {
        return itemid / 10000 == 113;
    }

    public static boolean isEarring(int itemid) {
        return itemid / 10000 == 103;
    }

    public static boolean isEyeAccessory(int itemid) {
        return itemid / 10000 == 102;
    }

    public static boolean isArmor(int itemid) {
        return (isTop(itemid)) || (isBottom(itemid)) || (isOverall(itemid)) || (isHat(itemid)) || (isGlove(itemid)) || (isShoe(itemid)) || (isCape(itemid));
    }

    public static boolean isAccessory(int id) {
        return (isFaceAccessory(id)) || (isNeckAccessory(id)) || (isEarring(id)) || (isEyeAccessory(id)) || (isBelt(id)) || (isMedal(id));
    }

    public static boolean isMedal(int id) {
        return id / 10000 == 114;
    }

    public static boolean isShield(int id) {
        return id / 10000 == 109;
    }

    public static boolean isRing(int id) {
        return id / 10000 == 111;
    }

    public static boolean isMount(int id) {
        return (id / 10000 == 190) || (id / 10000 == 193);
    }

    public static boolean isSaddle(int id) {
        return id / 10000 == 191;
    }

    public static boolean isTamingMob(int id) {
        return (isMount(id)) || (isSaddle(id));
    }

    public static boolean isPetEquip(int id) {
        return (id / 10000 >= 180) && (id / 10000 <= 183);
    }

    public static boolean isDragon(int id) {
        return (id / 10000 >= 194) && (id / 10000 <= 197);
    }

    public static boolean isMechanic(int id) {
        return (id / 10000 >= 161) && (id / 10000 <= 165);
    }

    public static boolean isMonsterBook(int id) {
        return id == ItemConstants.CRUSADER_CODEX;
    }

    public static boolean isAndroid(int id) {
        return (id / 10000 >= 166) && (id / 10000 <= 167);
    }

    public static boolean isWeapon(int id) {
        return ((id / 10000 >= 130) && (id / 10000 <= 153)) || (id / 10000 == 170) || (id / 10000 == 180);
    }

    public static boolean isFamiliar(int id) {
        return id / 10000 == 996;
    }

    public static boolean isConsume(int id) {
        return (id / 10000 >= 200) && (id / 10000 < 300);
    }

    public static boolean isEtc(int id) {
        return (id / 10000 >= 400) && (id / 10000 < 500);
    }

    public static boolean isPet(int id) {
        return id / 10000 == 500;
    }

    public static boolean isInstall(int id) {
        return (id / 10000 >= 300) && (id / 10000 < 400);
    }

    public static boolean isCashNotEquip(int id) {
        return id / 1000 >= 500;
    }

    public static boolean isSpecial(int id) {
        return (id / 10000 >= 900) && (id / 10000 < 1000);
    }

    public static boolean isJewelery(int itemId) {
        int type = itemId / 10000;
        return type == 101 || type == 102 || type == 103 || type == 118;
    }

    public static boolean isShoes(int itemId) {
        return itemId / 10000 == 107;
    }

    public static boolean isKatara(int itemId) {
        return itemId / 10000 == 134;
    }

    public static boolean isDagger(int itemId) {
        return itemId / 10000 == 133;
    }

    public static boolean isSecondaryWeapon(final int itemId) {
        return itemId / 10000 == 135;
    }

    public static boolean isBowgun(final int itemId) {
        return itemId >= 1522000 && itemId < 1523000;
    }

    public static boolean isCane(final int itemId) {
        return itemId >= 1362000 && itemId < 1363000;
    }

    public static boolean isMagicArrow(final int itemId) {
        return itemId >= 1352000 && itemId < 1352100;
    }

    public static boolean isCard(final int itemId) {
        return itemId >= 1352100 && itemId < 1352200;
    }

    public static boolean isGun(final int id) {
        return id >= 1492000 && id < 1500000;
    }

    public static boolean isSpecialShield(final int itemid) {
        return itemid / 1000 == 1098 || itemid / 1000 == 1099 || itemid / 10000 == 135;
    }

    public static boolean isCore(final int itemId) {
        return itemId >= 1352300 && itemId < 1352400;
    }

    public static boolean isMagicWeapon(final int itemId) {
        final int s = itemId / 10000;
        return s == 137 || s == 138; // wand, staff
    }

    public static boolean isFriendshipRing(int itemid) {
        switch (itemid) {
            case 1112800:
            case 1112801:
            case 1112802:
            case 1112810:
            case 1112811:
            case 1112816:
            case 1112817:
                return true;
            case 1112803:
            case 1112804:
            case 1112805:
            case 1112806:
            case 1112807:
            case 1112808:
            case 1112809:
            case 1112812:
            case 1112813:
            case 1112814:
            case 1112815:
        }
        return false;
    }

    public static boolean isCrushRing(int itemid) {
        switch (itemid) {
            case 1112001:
            case 1112002:
            case 1112003:
            case 1112005:
            case 1112006:
            case 1112007:
            case 1112012:
            case 1112015:
                return true;
            case 1112004:
            case 1112008:
            case 1112009:
            case 1112010:
            case 1112011:
            case 1112013:
            case 1112014:
        }
        return false;
    }

    public static boolean isWeddingRing(int itemid) {
        switch (itemid) {
            case 1112300:
            case 1112301:
            case 1112302:
            case 1112303:
            case 1112304:
            case 1112305:
            case 1112306:
            case 1112307:
            case 1112308:
            case 1112309:
            case 1112310:
            case 1112311:
            case 1112315:
            case 1112316:
            case 1112317:
            case 1112318:
            case 1112319:
            case 1112320:
            case 1112803:
            case 1112806:
            case 1112807:
            case 1112808:
            case 1112809:
                return true;
        }
        return false;
    }

    public static boolean is1hWeapon(int id) {
        return id / 10000 < 140;
    }

    public static boolean is2hWeapon(int id) {
        return !is1hWeapon(id);
    }

    public static boolean isTyrant(final int itemId) {
        switch (itemId) {
            //Boots
            case 1072743:
            case 1072744:
            case 1072745:
            case 1072746:
            case 1072747:
            //Capes    
            case 1102481:
            case 1102482:
            case 1102483:
            case 1102484:
            case 1102485:
            //Belts
            case 1132174:
            case 1132175:
            case 1132176:
            case 1132177:
            case 1132178:
            case 1082543:
            case 1082544:
            case 1082545:
            case 1082546:
            case 1082547:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNovaGear(final int itemId) {
        switch (itemId) {
            //Boots
            case 1072737: // Nova Hyades Boots
            case 1072738: // Nova Hermes Boots
            case 1072739: // Nova Charon Boots
            case 1072740: // Nova Lycaon Boots
            case 1072741: // Nova Altair Boots

            //Cape
            case 1102476: // Nova Hyades Cloak
            case 1102477: // Nova Hermes Cloak
            case 1102478: // Nova Charon Cloak
            case 1102479: // Nova Lycaon Cloak
            case 1102480: // Nova Altair Cloak

            //Belt
            case 1132169: // Nova Hyades Belt
            case 1132170: // Nova Hermes Belt
            case 1132171: // Nova Charon Belt
            case 1132172: // Nova Lycaon Belt
            case 1132173: // Nova Altair Belt
                return true;
            default:
                return false;
        }
    }

    public static boolean optionTypeFits(final int optionType, final int itemId) {
        switch (optionType) {
            case 10: // weapons
                return isWeapon(itemId);
            case 11: // all equipment except weapons
                return !isWeapon(itemId);
            case 20: // all armors
                return !isAccessory(itemId) && !isWeapon(itemId);
            case 40: // accessories
                return isAccessory(itemId);
            case 51: // hat
                return itemId / 10000 == 100;
            case 52: // top and overall
                return itemId / 10000 == 104 || itemId / 10000 == 105;
            case 53: // bottom and overall
                return itemId / 10000 == 106 || itemId / 10000 == 105;
            case 54: // glove
                return itemId / 10000 == 108;
            case 55: // shoe
                return itemId / 10000 == 107;
            default:
                return true;
        }
    }

}
