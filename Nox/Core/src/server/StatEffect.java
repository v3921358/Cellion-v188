package server;

import enums.StatInfo;
import enums.Stat;
import client.*;
import client.Trait.MapleTraitType;
import client.inventory.Item;
import client.inventory.MapleInventory;
import enums.InventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import constants.SkillConstants;
import constants.skills.*;
import handling.world.MaplePartyCharacter;
import org.apache.log4j.Priority;
import provider.MapleData;
import provider.MapleDataTool;
import provider.wz.MapleDataType;
import server.MapleCarnivalFactory.MCSkill;
import server.Timer.BuffTimer;
import server.skills.effects.manager.EffectManager;
import server.life.LifeFactory;
import server.life.Mob;
import server.life.MonsterStats;
import server.life.MobSkill;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import enums.SummonMovementType;
import server.maps.objects.*;
import service.ChannelServer;
import tools.CaltechEval;
import tools.LogHelper;
import tools.Pair;
import tools.Triple;
import tools.packet.BuffPacket;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.WvsContext;
import tools.packet.JobPacket;
import tools.packet.JobPacket.PhantomPacket;

import java.awt.*;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import tools.Utility;
import tools.packet.CField.SummonPacket;

public class StatEffect implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    public Map<StatInfo, Integer> info;
    private Map<MapleTraitType, Integer> traits;
    private boolean overTime, partyBuff = true;
    public EnumMap<CharacterTemporaryStat, Integer> statups;
    private ArrayList<Pair<Integer, Integer>> availableMap;
    public EnumMap<MonsterStatus, Integer> monsterStatus;
    private Point lt, rb;
    private byte level;
    private List<Pair<Integer, Integer>> randomMorph;
    private List<Disease> cureDebuffs;
    private List<Integer> petsCanConsume, familiars, randomPickup;
    private List<Triple<Integer, Integer, Integer>> rewardItem;
    private byte fieldExpType, expR, familiarTarget, recipeUseCount, recipeValidDay, reqSkillLevel, slotCount, slotPerLine, effectedOnAlly, effectedOnEnemy, type, preventslip, immortal, bs;
    private short ignoreMob, mesoR, thaw, fatigueChange, lifeId, imhp, immp, inflation, useLevel, indiePdd, indieMdd, incPVPdamage, mobSkill, mobSkillLevel;
    private double hpR, mpR;
    private int sourceid, recipe, moveTo, moneyCon, morphId = 0, expinc, exp, consumeOnPickup, charColor, interval, rewardMeso, totalprob, cosmetic;
    private int weapon = 0;
    private int expBuff, itemup, mesoup, cashup, berserk, illusion, booster, berserk2, cp, nuffSkill, combo;

    public static StatEffect loadSkillEffectFromData(final MapleData source, final int skillid, final int level, final String variables) {
        return loadFromData(source, skillid, level, variables);
    }

    public static StatEffect loadItemEffectFromData(final MapleData source, final int itemid) {
        return loadFromData(source, itemid, 1, null);
    }

    private static StatEffect loadFromData(final MapleData source, final int sourceid, final int level, final String variables) {
        final StatEffect effect = new StatEffect();
        effect.sourceid = sourceid;
        effect.level = (byte) level;
        if (source == null) {
            return effect;
        }

        effect.info = new EnumMap<>(StatInfo.class);
        effect.statups = new EnumMap<>(CharacterTemporaryStat.class);

        for (final StatInfo i : StatInfo.values()) {
            final String propertyName = i.name();
            int value = parseEval(propertyName, source, -1, variables, level, i.isSpecial());
            if (i.getStat() != null && value != -1) {
                effect.statups.put(i.getStat(), value);
            }

            effect.info.put(i, value);
        }

        effect.hpR = parseEval("hpR", source, 0, variables, level) / 100.0;
        effect.mpR = parseEval("mpR", source, 0, variables, level) / 100.0;
        effect.ignoreMob = (short) parseEval("ignoreMobpdpR", source, 0, variables, level);
        effect.thaw = (short) parseEval("thaw", source, 0, variables, level);
        effect.interval = parseEval("interval", source, 0, variables, level);
        effect.expinc = parseEval("expinc", source, 0, variables, level);
        effect.exp = parseEval("exp", source, 0, variables, level);
        effect.morphId = parseEval("morph", source, 0, variables, level);
        effect.cp = parseEval("cp", source, 0, variables, level);
        effect.cosmetic = parseEval("cosmetic", source, 0, variables, level);
        effect.slotCount = (byte) parseEval("slotCount", source, 0, variables, level); // for extended inventory, bags
        effect.slotPerLine = (byte) parseEval("slotPerLine", source, 0, variables, level); // for extended inventory, bags
        effect.preventslip = (byte) parseEval("preventslip", source, 0, variables, level);
        effect.useLevel = (short) parseEval("useLevel", source, 0, variables, level);
        effect.nuffSkill = parseEval("nuffSkill", source, 0, variables, level);
        effect.familiarTarget = (byte) (parseEval("familiarPassiveSkillTarget", source, 0, variables, level) + 1);
        effect.immortal = (byte) parseEval("immortal", source, 0, variables, level);
        effect.type = (byte) parseEval("type", source, 0, variables, level);
        effect.bs = (byte) parseEval("bs", source, 0, variables, level);
        effect.indiePdd = (short) parseEval("indiePdd", source, 0, variables, level);
        effect.indieMdd = (short) parseEval("indieMdd", source, 0, variables, level);
        effect.expBuff = parseEval("expBuff", source, 0, variables, level);
        effect.cashup = parseEval("cashBuff", source, 0, variables, level);
        effect.itemup = parseEval("itemupbyitem", source, 0, variables, level);
        effect.mesoup = parseEval("mesoupbyitem", source, 0, variables, level);
        effect.berserk = parseEval("berserk", source, 0, variables, level);
        effect.berserk2 = parseEval("berserk2", source, 0, variables, level);
        effect.booster = parseEval("booster", source, 0, variables, level);
        effect.lifeId = (short) parseEval("lifeId", source, 0, variables, level);
        effect.inflation = (short) parseEval("inflation", source, 0, variables, level);
        effect.imhp = (short) parseEval("imhp", source, 0, variables, level);
        effect.immp = (short) parseEval("immp", source, 0, variables, level);
        effect.illusion = parseEval("illusion", source, 0, variables, level);
        effect.fieldExpType = (byte) parseEval("fieldExpType", source, -1, variables, level); // default = -1, 0 = COMBO_PARADE1, 1 = COMBO_PARADE2, 2 = COMBO_PARADE3
        effect.consumeOnPickup = parseEval("consumeOnPickup", source, 0, variables, level);
        if (effect.consumeOnPickup == 1) {
            if (parseEval("party", source, 0, variables, level) > 0) {
                effect.consumeOnPickup = 2;
            }
        }
        effect.recipe = parseEval("recipe", source, 0, variables, level);
        effect.recipeUseCount = (byte) parseEval("recipeUseCount", source, 0, variables, level);
        effect.recipeValidDay = (byte) parseEval("recipeValidDay", source, 0, variables, level);
        effect.reqSkillLevel = (byte) parseEval("reqSkillLevel", source, 0, variables, level);
        effect.effectedOnAlly = (byte) parseEval("effectedOnAlly", source, 0, variables, level);
        effect.effectedOnEnemy = (byte) parseEval("effectedOnEnemy", source, 0, variables, level);
        effect.incPVPdamage = (short) parseEval("incPVPDamage", source, 0, variables, level);
        effect.moneyCon = parseEval("moneyCon", source, 0, variables, level);
        effect.moveTo = parseEval("moveTo", source, -1, variables, level);

        effect.charColor = 0;
        String cColor = MapleDataTool.getString("charColor", source, null);
        if (cColor != null) {
            effect.charColor |= Integer.parseInt("0x" + cColor.substring(0, 2));
            effect.charColor |= Integer.parseInt("0x" + cColor.substring(2, 4) + "00");
            effect.charColor |= Integer.parseInt("0x" + cColor.substring(4, 6) + "0000");
            effect.charColor |= Integer.parseInt("0x" + cColor.substring(6, 8) + "000000");
        }
        effect.traits = new EnumMap<>(MapleTraitType.class);
        for (MapleTraitType t : MapleTraitType.values()) {
            int expz = parseEval(t.name() + "EXP", source, 0, variables, level);
            if (expz != 0) {
                effect.traits.put(t, expz);
            }
        }
        List<Disease> cure = new ArrayList<>(5);
        if (parseEval("poison", source, 0, variables, level) > 0) {
            cure.add(Disease.POISON);
        }
        if (parseEval("seal", source, 0, variables, level) > 0) {
            cure.add(Disease.SEAL);
        }
        if (parseEval("darkness", source, 0, variables, level) > 0) {
            cure.add(Disease.DARKNESS);
        }
        if (parseEval("weakness", source, 0, variables, level) > 0) {
            cure.add(Disease.WEAKEN);
        }
        if (parseEval("curse", source, 0, variables, level) > 0) {
            cure.add(Disease.CURSE);
        }
        effect.cureDebuffs = cure;
        effect.petsCanConsume = new ArrayList<>();
        for (int i = 0; true; i++) {
            final int dd = parseEval(String.valueOf(i), source, 0, variables, level);
            if (dd > 0) {
                effect.petsCanConsume.add(dd);
            } else {
                break;
            }
        }
        final MapleData mdd = source.getChildByPath("0");
        if (mdd != null && mdd.getChildren().size() > 0) {
            effect.mobSkill = (short) parseEval("mobSkill", mdd, 0, variables, level);
            effect.mobSkillLevel = (short) parseEval("level", mdd, 0, variables, level);
        } else {
            effect.mobSkill = 0;
            effect.mobSkillLevel = 0;
        }
        final MapleData pd = source.getChildByPath("randomPickup");
        if (pd != null) {
            effect.randomPickup = new ArrayList<>();
            for (MapleData p : pd) {
                effect.randomPickup.add(MapleDataTool.getInt(p));
            }
        }
        final MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            effect.lt = (Point) ltd.getData();
            effect.rb = (Point) source.getChildByPath("rb").getData();
        }
        final MapleData ltc = source.getChildByPath("con");
        if (ltc != null) {
            effect.availableMap = new ArrayList<>();
            for (MapleData ltb : ltc) {
                effect.availableMap.add(new Pair<>(MapleDataTool.getInt("sMap", ltb, 0), MapleDataTool.getInt("eMap", ltb, 999999999)));
            }
        }
        final MapleData ltb = source.getChildByPath("familiar");
        if (ltb != null) {
            effect.fatigueChange = (short) (parseEval("incFatigue", ltb, 0, variables, level) - parseEval("decFatigue", ltb, 0, variables, level));
            effect.familiarTarget = (byte) parseEval("target", ltb, 0, variables, level);
            final MapleData lta = ltb.getChildByPath("targetList");
            if (lta != null) {
                effect.familiars = new ArrayList<>();
                for (MapleData ltz : lta) {
                    effect.familiars.add(MapleDataTool.getInt(ltz, 0));
                }
            }
        } else {
            effect.fatigueChange = 0;
        }
        int totalprob = 0;
        final MapleData lta = source.getChildByPath("reward");
        if (lta != null) {
            effect.rewardMeso = parseEval("meso", lta, 0, variables, level);
            final MapleData ltz = lta.getChildByPath("case");
            if (ltz != null) {
                effect.rewardItem = new ArrayList<>();
                for (MapleData lty : ltz) {
                    effect.rewardItem.add(new Triple<>(MapleDataTool.getInt("id", lty, 0), MapleDataTool.getInt("count", lty, 0), MapleDataTool.getInt("prop", lty, 0))); // todo: period (in minutes)
                    totalprob += MapleDataTool.getInt("prob", lty, 0);
                }
            }
        } else {
            effect.rewardMeso = 0;
        }
        effect.totalprob = totalprob;

        // start of server calculated stuffs
        final int priceUnit = effect.info.get(StatInfo.priceUnit); // Guild skills
        if (priceUnit > 0) {
            final int price = effect.info.get(StatInfo.price);
            final int extendPrice = effect.info.get(StatInfo.extendPrice);
            effect.info.put(StatInfo.price, price * priceUnit);
            effect.info.put(StatInfo.extendPrice, extendPrice * priceUnit);
        }
        switch (sourceid) {
            case 1100002:
            case 1200002:
            case 1300002:
            case 3100001:
            case 3200001:
            case 11101002:
            case 51100002:
            case 13101002:
            case 2111007:
            case 2211007:
            case 2311007:
            case 32111010:
            case 22161005:
            case 12111007:
            case 33100009:
            case 22150004:
            case 22181004: //All Final Attack
            case 1120013:
            case 3120008:
            case 23100006:
            case 23120012:
                effect.info.put(StatInfo.mobCount, 6);
                break;
            case 35121005:
            case 35111004:
            case 35121013:
            case 35121054: //reactive armor mech
                effect.info.put(StatInfo.attackCount, 6);
                effect.info.put(StatInfo.bulletCount, 6);
                break;
            case 24121000:
                effect.info.put(StatInfo.attackCount, 6);
                break;
            case 24100003: // TODO: for now, or could it be card stack? (1 count)
            case 24120002:
                effect.info.put(StatInfo.attackCount, 15);
                break;
        }

        if (GameConstants.isNoDelaySkill(sourceid)) {
            effect.info.put(StatInfo.mobCount, 6);
        }

        effect.info.put(StatInfo.time, (effect.info.get(StatInfo.time) * 1000)); // items have their times stored in ms, of course
        effect.info.put(StatInfo.subTime, (effect.info.get(StatInfo.subTime) * 1000));
        effect.overTime = effect.info.get(StatInfo.time) > -1;
        effect.monsterStatus = new EnumMap<>(MonsterStatus.class);

        //Fetch effect handler
        boolean bHasEffect = EffectManager.SetEffect(effect, sourceid);
        if (!bHasEffect && effect.info.get(StatInfo.time) > -1) {
            LogHelper.GENERAL_EXCEPTION.get().log(Priority.DEBUG, "Skill :" + sourceid + " has an over-time property but no effects.");
        }

        if (effect.info.get(StatInfo.time) > -1) {
            effect.overTime = true;
        }

        if (effect.isPoison()) {
            effect.monsterStatus.put(MonsterStatus.POISON, 1);
        }
        return effect;
    }

    public static int parseEval(String path, MapleData source, int def, String variables, int level) {
        return parseEval(path, source, def, variables, level, false);
    }

    private static int parseEval(String path, MapleData source, int def, String variables, int level, boolean specialData) {
        if (variables == null && !specialData) {
            return MapleDataTool.getIntConvert(path, source, def);
        } else {
            final MapleData data = source.getChildByPath(path);
            if (data == null) {
                return def;
            }
            if (data.getType() != MapleDataType.STRING) {
                return MapleDataTool.getIntConvert(path, source, def);
            }
            String stringData = MapleDataTool.getString(data);
            stringData = stringData.toLowerCase();
            if (stringData.startsWith("log")) {
                stringData = stringData.substring(0, 3)
                        + stringData.substring(stringData.lastIndexOf("("), stringData.indexOf(")") + 1)
                        + "/log(" + stringData.substring(3, 5) + ")";
            }
            if (stringData.endsWith("u") || stringData.endsWith("y")) {
                stringData = stringData.substring(0, stringData.length() - 1) + "x";
            }
            stringData = stringData.replace(variables, String.valueOf(level));
            if (stringData.substring(0, 1).equals("-")) { //-30+3*x
                if (stringData.substring(1, 2).equals("u") || stringData.substring(1, 2).equals("d")) { //-u(x/2)
                    //The n indicates that there is a negative infront of the equation
                    stringData = "n(" + stringData.substring(1, stringData.length()) + ")"; //n(u(x/2))
                } else {
                    stringData = "n" + stringData.substring(1, stringData.length()); //n30+3*x
                }

            } else if (stringData.substring(0, 1).equals("=")) { //lol nexon and their mistakes
                stringData = stringData.substring(1, stringData.length());
            }
            stringData = stringData.replace("%", ""); // bug in Skill.wz 8001348 >> <string name="x" value="5+10*(x-1)%"/> 
            return (int) (new CaltechEval(stringData).evaluate());
        }
    }

    /**
     * @param applyto
     * @param obj
     */
    public final void applyPassive(final User applyto, final MapleMapObject obj) {
        if (makeChanceResult() && !GameConstants.isDemonSlayer(applyto.getJob())) { // demon can't heal mp
            switch (sourceid) { // MP eater
                case 2100000:
                case 2200000:
                case 2300000:
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    final Mob mob = (Mob) obj; // x is absorb percentage
                    if (!mob.getStats().isBoss()) {
                        final long absorbMp = Math.min((int) (mob.getMobMaxMp() * (getX() / 100.0)), mob.getMp());
                        if (absorbMp > 0) {
                            mob.setMp(mob.getMp() - absorbMp);
                            applyto.getStat().setMp((int) (applyto.getStat().getMp() + absorbMp), applyto);
                            applyto.getClient().SendPacket(EffectPacket.showOwnBuffEffect(sourceid, EffectPacket.SkillUse, applyto.getLevel(), level));
                            applyto.getMap().broadcastPacket(applyto, EffectPacket.showBuffEffect(applyto.getId(), sourceid, EffectPacket.SkillUse, applyto.getLevel(), level), false);
                        }
                    }
                    break;
            }
        }
    }

    public final boolean applyTo(User chr) {
        return applyTo(chr, chr, true, null, info.get(StatInfo.time));
    }

    public final boolean applyTo(User chr, Point pos) {
        return applyTo(chr, chr, true, pos, info.get(StatInfo.time));
    }

    public final boolean applyTo(final User applyfrom, final User applyto, final boolean primary, final Point pos, int newDuration) {
        if (isHeal() && (applyfrom.getMapId() == 749040100 || applyto.getMapId() == 749040100)) {
            applyfrom.getClient().SendPacket(WvsContext.enableActions());
            return false; //z
        } else if ((isSoaring_Mount() && applyfrom.getBuffedValue(CharacterTemporaryStat.RideVehicle) == null) || (isSoaring_Normal() && !applyfrom.getMap().getSharedMapResources().needSkillForFly)) {
            applyfrom.getClient().SendPacket(WvsContext.enableActions());
            return false;
        } else if (sourceid == 4341006 && applyfrom.getBuffedValue(CharacterTemporaryStat.ShadowPartner) == null) {
            applyfrom.getClient().SendPacket(WvsContext.enableActions());
            return false;
        } else if (isShadow() && applyfrom.getJob() / 100 % 10 != 4) { //pirate/shadow = dc
            applyfrom.getClient().SendPacket(WvsContext.enableActions());
            return false;
        } else if (sourceid == 33101004 && applyfrom.getMap().getSharedMapResources().town) {
            applyfrom.dropMessage(5, "You may not use this skill in towns.");
            applyfrom.getClient().SendPacket(WvsContext.enableActions());
            return false;
        }

        if (statups.containsKey(CharacterTemporaryStat.RideVehicle)) {
            if (applyto.getBuffedValue(CharacterTemporaryStat.RideVehicle) != null) {
                return false;
            }
        }

        int hpchange = calcHPChange(applyfrom, primary);
        int mpchange = calcMPChange(applyfrom, primary);
        int powerchange = calcPowerChange(applyfrom);

        final PlayerStats stat = applyto.getStat();
        if (primary) {
            if (info.get(StatInfo.itemConNo) != 0 && !applyto.inPVP()) {
                if (!applyto.haveItem(info.get(StatInfo.itemCon), info.get(StatInfo.itemConNo), false, true)) {
                    applyto.getClient().SendPacket(WvsContext.enableActions());
                    return false;
                }
                MapleInventoryManipulator.removeById(applyto.getClient(), GameConstants.getInventoryType(info.get(StatInfo.itemCon)), info.get(StatInfo.itemCon), info.get(StatInfo.itemConNo), false, true);
            }
        } else if (!primary && isResurrection()) {
            hpchange = stat.getMaxHp();
            applyto.setStance(0); //TODO fix death bug, player doesnt spawn on other screen
        }
        if (isDispel() && makeChanceResult()) {
            applyto.dispelDebuffs();
        } else if (isHeroWill()) {
            applyto.dispelDebuffs();
        } else if (cureDebuffs.size() > 0) {
            for (final Disease debuff : cureDebuffs) {
                applyfrom.dispelDebuff(debuff);
            }
        } else if (isMPRecovery()) {
            final int toDecreaseHP = ((stat.getMaxHp() / 100) * 10);
            if (stat.getHp() > toDecreaseHP) {
                hpchange += -toDecreaseHP; // -10% of max HP
                mpchange += ((toDecreaseHP / 100) * getY());
            } else {
                hpchange = stat.getHp() == 1 ? 0 : stat.getHp() - 1;
            }
        }
        final Map<Stat, Long> hpmpupdate = new EnumMap<>(Stat.class);
        if (hpchange != 0) {
            if (hpchange < 0 && (-hpchange) > stat.getHp() && !applyto.hasDisease(Disease.ZOMBIFY)) {
                applyto.getClient().SendPacket(WvsContext.enableActions());
                return false;
            }
            stat.setHp(stat.getHp() + hpchange, applyto);
        }
        if (mpchange != 0) {
            if (mpchange < 0 && (-mpchange) > stat.getMp()) {
                applyto.getClient().SendPacket(WvsContext.enableActions());
                return false;
            }
            //short converting needs math.min cuz of overflow
            if ((mpchange < 0 && GameConstants.isDemonSlayer(applyto.getJob())) || !GameConstants.isDemonSlayer(applyto.getJob())) { // heal
                stat.setMp(stat.getMp() + mpchange, applyto);
            }
            hpmpupdate.put(Stat.MP, Long.valueOf(stat.getMp()));
        }
        hpmpupdate.put(Stat.HP, Long.valueOf(stat.getHp()));

        applyto.getClient().SendPacket(WvsContext.OnPlayerStatChanged(applyto, hpmpupdate));
        if (powerchange != 0) {
            if (applyto.getXenonSurplus() - powerchange < 0) {
                return false;
            }
            applyto.gainXenonSurplus((short) -powerchange);
        }
        if (expinc != 0) {
            applyto.gainExp(expinc, true, true, false);
            applyto.getClient().SendPacket(EffectPacket.showForeignEffect(EffectPacket.ExpItemConsumed));
        } else if (sourceid / 10000 == 238) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final int mobid = ii.getCardMobId(sourceid);
            if (mobid > 0) {
                final boolean done = applyto.getMonsterBook().monsterCaught(applyto.getClient(), mobid, LifeFactory.getMonsterStats(mobid).getName());
                applyto.getClient().SendPacket(WvsContext.getCard(done ? sourceid : 0, 1));
            }
        } else if (isReturnScroll()) {
            applyReturnScroll(applyto);
        } else if (useLevel > 0) {
            applyto.setExtractor(new Extractor(applyto, sourceid, useLevel * 50, 1440)); //no clue about time left
            applyto.getMap().spawnExtractor(applyto.getExtractor());
        } else if (isMistEruption()) {
            int i = info.get(StatInfo.y);
            for (Mist m : applyto.getMap().getAllMists()) {
                if (m.getOwnerId() == applyto.getId() && m.getSourceSkill().getId() == 2111003) {
                    if (m.getSchedule() != null) {
                        m.getSchedule().cancel(false);
                        m.setSchedule(null);
                    }
                    if (m.getPoisonSchedule() != null) {
                        m.getPoisonSchedule().cancel(false);
                        m.setPoisonSchedule(null);
                    }
                    applyto.getMap().broadcastPacket(CField.removeMist(m.getObjectId(), true));
                    applyto.getMap().removeMapObject(m);

                    i--;
                    if (i <= 0) {
                        break;
                    }
                }
            }
        } else if (cosmetic > 0) {
            if (cosmetic >= 30000) {
                applyto.setHair(cosmetic);
                applyto.updateSingleStat(Stat.Hair, cosmetic);
            } else if (cosmetic >= 20000) {
                applyto.setFace(cosmetic);
                applyto.updateSingleStat(Stat.Face, cosmetic);
            } else if (cosmetic < 100) {
                applyto.setSkinColor((byte) cosmetic);
                applyto.updateSingleStat(Stat.Skin, cosmetic);
            }
            applyto.equipChanged();
        } else if (recipe > 0) {
            if (applyto.getSkillLevel(recipe) > 0 || applyto.getProfessionLevel((recipe / 10000) * 10000) < reqSkillLevel) {
                return false;
            }
            applyto.changeSingleSkillLevel(SkillFactory.getCraft(recipe), Integer.MAX_VALUE, recipeUseCount, recipeValidDay > 0 ? (System.currentTimeMillis() + recipeValidDay * 24L * 60 * 60 * 1000) : -1L);
        } else if (isSpiritClaw()) {
            MapleInventory use = applyto.getInventory(InventoryType.USE);
            boolean itemz = false;
            for (int i = 0; i < use.getSlotLimit(); i++) { // impose order...
                Item item = use.getItem((byte) i);
                if (item != null) {
                    if (GameConstants.isRechargable(item.getItemId()) && item.getQuantity() >= 100) {
                        MapleInventoryManipulator.removeFromSlot(applyto.getClient(), InventoryType.USE, (short) i, (short) 100, false, true);
                        itemz = true;
                        break;
                    }
                }
            }
            if (!itemz) {
                return false;
            }
        } else if (isSpiritBlast()) {
            MapleInventory use = applyto.getInventory(InventoryType.USE);
            boolean itemz = false;
            for (int i = 0; i < use.getSlotLimit(); i++) { // impose order...
                Item item = use.getItem((byte) i);
                if (item != null) {
                    if (GameConstants.isBullet(item.getItemId()) && item.getQuantity() >= 100) {
                        MapleInventoryManipulator.removeFromSlot(applyto.getClient(), InventoryType.USE, (short) i, (short) 100, false, true);
                        itemz = true;
                        break;
                    }
                }
            }
            if (!itemz) {
                return false;
            }
        } else if (cp != 0 && applyto.getCarnivalParty() != null) {
            applyto.getCarnivalParty().addCP(applyto, cp);
            applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);

            for (User chr : applyto.getMap().getCharacters()) {
                chr.CPUpdate(true, applyto.getCarnivalParty().getAvailableCP(), applyto.getCarnivalParty().getTotalCP(), applyto.getCarnivalParty().getTeam());
            }
        } else if (nuffSkill != 0 && applyto.getParty() != null) {
            final MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(nuffSkill);
            if (skil != null) {
                final Disease dis = skil.getDisease();
                for (User chr : applyto.getMap().getCharacters()) {
                    if (applyto.getParty() == null || chr.getParty() == null || (chr.getParty().getId() != applyto.getParty().getId())) {
                        if (skil.targetsAll || Randomizer.nextBoolean()) {
                            if (dis == null) {
                                chr.dispel();
                            } else if (skil.getSkill() == null) {
                                MobSkill ms = new MobSkill(dis.getDisease(), 1);
                                ms.setDuration(30000);
                                ms.setX(1);
                                chr.giveDebuff(dis, ms);
                            } else {
                                chr.giveDebuff(dis, skil.getSkill());
                            }
                            if (!skil.targetsAll) {
                                break;
                            }
                        }
                    }
                }
            }
        } else if ((effectedOnEnemy > 0 || effectedOnAlly > 0) && primary && applyto.inPVP()) {
            final int eventType = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
            if (eventType > 0 || effectedOnEnemy > 0) {
                for (User chr : applyto.getMap().getCharacters()) {
                    if (chr.getId() != applyto.getId() && (effectedOnAlly > 0 ? (chr.getTeam() == applyto.getTeam()) : (chr.getTeam() != applyto.getTeam() || eventType == 0))) {
                        applyTo(applyto, chr, false, pos, newDuration);
                    }
                }
            }
        } else if (mobSkill > 0 && mobSkillLevel > 0 && primary && applyto.inPVP()) {
            if (effectedOnEnemy > 0) {
                final int eventType = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
                for (User chr : applyto.getMap().getCharacters()) {
                    if (chr.getId() != applyto.getId() && (chr.getTeam() != applyto.getTeam() || eventType == 0)) {
                        chr.disease(mobSkill, mobSkillLevel);
                    }
                }
            } else if (sourceid == 2910000 || sourceid == 2910001) { //red flag
                applyto.getClient().SendPacket(EffectPacket.showOwnBuffEffect(sourceid, EffectPacket.BuffItemEffect, applyto.getLevel(), level));
                applyto.getMap().broadcastPacket(applyto, EffectPacket.showBuffEffect(applyto.getId(), sourceid, EffectPacket.BuffItemEffect, applyto.getLevel(), level), false);

                applyto.getClient().SendPacket(EffectPacket.showWZUOLEffect("UI/UIWindow2.img/CTF/Effect", applyto.getDirection() == 1, -1, 0, 0));
                applyto.getMap().broadcastPacket(applyto, EffectPacket.showWZUOLEffect("UI/UIWindow2.img/CTF/Effect", applyto.getDirection() == 1, applyto.getId(), 0, 0), false);
                if (applyto.getTeam() == (sourceid - 2910000)) { //restore duh flag
                    if (sourceid == 2910000) {
                        applyto.getEventInstance().broadcastPlayerMsg(-7, "The Red Team's flag has been restored.");
                    } else {
                        applyto.getEventInstance().broadcastPlayerMsg(-7, "The Blue Team's flag has been restored.");
                    }
                    applyto.getMap().spawnAutoDrop(sourceid, applyto.getMap().getSharedMapResources().mcarnival.Guardian.get(sourceid - 2910000).pos);
                } else {
                    applyto.disease(mobSkill, mobSkillLevel);
                    if (sourceid == 2910000) {
                        applyto.getEventInstance().setProperty("redflag", String.valueOf(applyto.getId()));
                        applyto.getEventInstance().broadcastPlayerMsg(-7, "The Red Team's flag has been captured!");
                        applyto.getClient().SendPacket(EffectPacket.showWZUOLEffect("UI/UIWindow2.img/CTF/Tail/Red", applyto.getDirection() == 1, -1, 600000, 0));
                        applyto.getMap().broadcastPacket(applyto, EffectPacket.showWZUOLEffect("UI/UIWindow2.img/CTF/Tail/Red", applyto.getDirection() == 1, applyto.getId(), 600000, 0), false);
                    } else {
                        applyto.getEventInstance().setProperty("blueflag", String.valueOf(applyto.getId()));
                        applyto.getEventInstance().broadcastPlayerMsg(-7, "The Blue Team's flag has been captured!");
                        applyto.getClient().SendPacket(EffectPacket.showWZUOLEffect("UI/UIWindow2.img/CTF/Tail/Blue", applyto.getDirection() == 1, -1, 600000, 0));
                        applyto.getMap().broadcastPacket(applyto, EffectPacket.showWZUOLEffect("UI/UIWindow2.img/CTF/Tail/Blue", applyto.getDirection() == 1, applyto.getId(), 600000, 0), false);
                    }
                }
            } else {
                applyto.disease(mobSkill, mobSkillLevel);
            }
        } else if (randomPickup != null && randomPickup.size() > 0) {
            MapleItemInformationProvider.getInstance().getItemEffect(randomPickup.get(Randomizer.nextInt(randomPickup.size()))).applyTo(applyto);
        } else if (GameConstants.isReturnHQSkill(sourceid)) { // a beginner skill that returns the player to where it started, the HQ
            applyto.changeMap(info.get(StatInfo.x), 0);
        } else if (this.fieldExpType != -1) {
            // Calculate the base bonus
            int baseBonusPercentage = 0;
            switch (fieldExpType) {
                case 0:
                default:
                    baseBonusPercentage = 500;
                    break;
                case 1:
                    baseBonusPercentage = 700;
                    break;
                case 2:
                    baseBonusPercentage = 1000;
                    break;
            }

            // Aran combo skill bonus
            final Skill comboKillBlessingSkill = SkillFactory.getSkill(SkillConstants.getLinkSkillByJob(SkillConstants.COMBO_KILL_BLESSING, SkillConstants.L_COMBO_KILL_BLESSING, applyto.getJob()));
            final int comboKillLevel = applyto.getSkillLevel(comboKillBlessingSkill);
            if (comboKillLevel > 0) {
                baseBonusPercentage *= comboKillBlessingSkill.getEffect(comboKillLevel).getX() / 100f;
            }

            final MonsterStats lowestLevelMonster = applyto.getMap().getLowestLevelMonster();
            if (lowestLevelMonster != null) {
                double expRate_Server = applyto.getClient().getChannelServer().getExpRate(applyto.getWorld());
                final long totalEXPGained = (long) (((long) (expRate_Server * lowestLevelMonster.getExp())) * (baseBonusPercentage * 0.01f));

                if (totalEXPGained > 0) { // another safety check
                    applyto.gainExp(totalEXPGained, true, true, true);
                    applyto.getClient().SendPacket(CField.EffectPacket.showForeignEffect(-1, EffectPacket.FieldExpItemConsumed, (int) totalEXPGained));
                }
            }
        }

        for (Entry<MapleTraitType, Integer> t : traits.entrySet()) {
            applyto.getTrait(t.getKey()).addExp(t.getValue(), applyto);
        }
        final SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null && (sourceid != 32111006 || (applyfrom.getBuffedValue(CharacterTemporaryStat.REAPER) != null))) {
            int summId = sourceid;
            if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                System.out.println("Summon: " + summId);
            }
            if (sourceid == 3111002) {
                final Skill elite = SkillFactory.getSkill(3120012);
                if (applyfrom.getTotalSkillLevel(elite) > 0) {
                    return elite.getEffect(applyfrom.getTotalSkillLevel(elite)).applyTo(applyfrom, applyto, primary, pos, newDuration);
                }
            } else if (sourceid == 3211002) {
                final Skill elite = SkillFactory.getSkill(3220012);
                if (applyfrom.getTotalSkillLevel(elite) > 0) {
                    return elite.getEffect(applyfrom.getTotalSkillLevel(elite)).applyTo(applyfrom, applyto, primary, pos, newDuration);
                }
            }
            final Summon tosummon = new Summon(
                    applyfrom,
                    summId,
                    getLevel(),
                    new Point(pos == null ? applyfrom.getTruePosition() : pos),
                    summonMovementType,
                    newDuration
            );
            if (!tosummon.isPuppet()) {
                applyfrom.getCheatTracker().resetSummonAttack();
            }
            applyfrom.cancelEffect(this, true, -1, statups);
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.addSummon(tosummon);
            tosummon.addHP(info.get(StatInfo.x).shortValue());
            if (isBeholder()) {
                tosummon.addHP((short) 1);
            } else if (sourceid == 4341006) {
                applyfrom.cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowPartner);
            } else if (sourceid == 32111006) {
                return true; //no buff
            } else if (sourceid == Mechanic.ROCK_N_SHOCK) {
                for (Summon s : applyfrom.getSummonsReadLock()) {
                    if ((sourceid == 14121054) ? (s.getSkill() == 14121054) || (s.getSkill() == 14121055) || (s.getSkill() == 14121056) : (s.getSkill() == sourceid)) {
                        applyfrom.getMap().broadcastPacket(SummonPacket.removeSummon(s, true));
                        applyfrom.getMap().removeMapObject(s);
                        applyfrom.removeVisibleMapObject(s);
                        if (applyfrom.getSummons().get(s.getSkill()) != null) {
                            applyfrom.getSummons().remove(s.getSkill());
                        }
                    }
                }

                if (sourceid == 14121054) {
                    final Summon illusion = new Summon(applyfrom, this, applyfrom.getTruePosition(), summonMovementType, newDuration);
                    final Summon illusion2 = new Summon(applyfrom, this, applyfrom.getTruePosition(), summonMovementType, newDuration);
                    illusion.setPosition(pos);
                    illusion2.setPosition(pos);
                    applyfrom.getMap().spawnSummon(illusion);
                    applyfrom.getMap().spawnSummon(illusion2);
                    applyfrom.getSummons().put(14121055, illusion);
                    applyfrom.getSummons().put(14121056, illusion2);
                }

                if (sourceid == NightWalker.SHADOW_BAT) {
                    final Summon bat = new Summon(applyfrom, this, applyfrom.getTruePosition(), summonMovementType, newDuration);
                    bat.setPosition(pos);
                    applyfrom.getMap().spawnSummon(bat);
                    applyfrom.getSummons().put(NightWalker.SHADOW_BAT, bat);
                }

                if (sourceid == WildHunter.SUMMON_JAGUAR) {
                    final Summon pJaguar = new Summon(applyfrom, this, applyfrom.getTruePosition(), summonMovementType, newDuration);
                    pJaguar.setPosition(pos);
                    applyfrom.getMap().spawnSummon(pJaguar);
                    applyfrom.getSummons().put(WildHunter.SUMMON_JAGUAR, pJaguar);
                }

                List<Integer> count = new ArrayList<>();
                final List<Summon> ss = applyfrom.getSummonsReadLock();
                try {
                    for (Summon s : ss) {
                        if (s.getSkill() == sourceid) {
                            count.add(s.getObjectId());
                        }
                    }
                } finally {
                    applyfrom.unlockSummonsReadLock();
                }
                if (count.size() != 3) {
                    return true; //no buff until 3
                }
                applyfrom.addCooldown(sourceid, System.currentTimeMillis(), getCooldown(applyfrom));
                applyfrom.getMap().broadcastPacket(CField.teslaTriangle(applyfrom.getId(), count.get(0), count.get(1), count.get(2)));
            } else if (sourceid == NightWalker.SHADOW_ILLUSION) {
                /*final Summon illusion = new Summon(applyfrom, this, applyfrom.getTruePosition(), summonMovementType, newDuration);
                final Summon illusion2 = new Summon(applyfrom, this, applyfrom.getTruePosition(), summonMovementType, newDuration);
                illusion.setPosition(pos);
                illusion2.setPosition(pos);
                applyfrom.getMap().spawnSummon(illusion);
                applyfrom.getMap().spawnSummon(illusion2);
                applyfrom.getSummons().put(14121055, illusion);
                applyfrom.getSummons().put(14121056, illusion2);*/
            } else if (sourceid == NightWalker.SHADOW_BAT) {
                final Summon bat = new Summon(applyfrom, this, applyfrom.getTruePosition(), summonMovementType, newDuration);
                bat.setPosition(pos);
                applyfrom.getMap().spawnSummon(bat);
                applyfrom.getSummons().put(NightWalker.SHADOW_BAT, bat);
            } else if (sourceid == WildHunter.SUMMON_JAGUAR) {
                final Summon pJaguar = new Summon(applyfrom, this, applyfrom.getTruePosition(), summonMovementType, newDuration);
                pJaguar.setPosition(pos);
                applyfrom.getMap().spawnSummon(pJaguar);
                applyfrom.getSummons().put(WildHunter.SUMMON_JAGUAR, pJaguar);
            }
        } else if (isMechDoor()) {
            int newId = 0;
            boolean applyBuff = false;
            if (applyto.getMechDoors().size() >= 2) {
                final MechDoor remove = applyto.getMechDoors().remove(0);
                newId = remove.getId();
                applyto.getMap().broadcastPacket(CField.removeMechDoor(remove, true));
                applyto.getMap().removeMapObject(remove);
            } else {
                for (MechDoor d : applyto.getMechDoors()) {
                    if (d.getId() == newId) {
                        applyBuff = true;
                        newId = 1;
                        break;
                    }
                }
            }
            final MechDoor door = new MechDoor(applyto, new Point(pos == null ? applyto.getTruePosition() : pos), newId);
            applyto.getMap().spawnMechDoor(door);
            applyto.addMechDoor(door);
            applyto.getClient().SendPacket(WvsContext.mechPortal(door.getTruePosition()));
            if (!applyBuff) {
                return true; //do not apply buff until 2 doors spawned
            }
        }
        if (primary && availableMap != null) {
            for (Pair<Integer, Integer> e : availableMap) {
                if (applyto.getMapId() < e.left || applyto.getMapId() > e.right) {
                    applyto.getClient().SendPacket(WvsContext.enableActions());
                    return true;
                }
            }
        }

        if (overTime && !isEnergyCharged()) {
            applyBuffEffect(applyfrom, applyto, primary, newDuration);
        }

        removeMonsterBuff(applyfrom);

        if (primary) {
            if ((overTime || isHeal()) && !isEnergyCharged()) {
                applyBuff(applyfrom, newDuration);
            }
            if (isMonsterBuff()) {
                applyMonsterBuff(applyfrom);
            }
        }
        if (isMagicDoor()) { // Magic Door
            Door door = new Door(applyto, new Point(pos == null ? applyto.getTruePosition() : pos), sourceid); // Current Map door
            if (door.getTownPortal() != null) {

                applyto.getMap().spawnDoor(door);
                applyto.addDoor(door);

                Door townDoor = new Door(door); // Town door
                applyto.addDoor(townDoor);
                door.getTown().spawnDoor(townDoor);

                if (applyto.getParty() != null) { // update town doors
                    applyto.silentPartyUpdate();
                }
            } else {
                applyto.dropMessage(5, "You may not spawn a door because all doors in the town are taken.");
            }
        } else if (isMist()) {
            final Rectangle bounds = calculateBoundingBox(pos != null ? pos : applyfrom.getPosition(), applyfrom.isFacingLeft());
            final Mist mist = new Mist(bounds, applyfrom, this);
            applyfrom.getMap().spawnMist(mist, getDuration(), false);

        } else if (isTimeLeap()) { // Time Leap
            for (CoolDownValueHolder i : applyto.getCooldowns()) {
                if (i.skillId != 5121010) {
                    applyto.removeCooldown(i.skillId);
                    applyto.getClient().SendPacket(CField.skillCooldown(i.skillId, 0));
                }
            }
        }
        if (applyto.getJob() == 132) {
            if (applyto.getBuffedValue(CharacterTemporaryStat.IgnoreTargetDEF) != 1) { //Sacrifice is the only skill Dark Knights have that give Ignore Def hacky but works
                applyto.cancelTemporaryStats(CharacterTemporaryStat.Beholder);
                applyto.addCooldown(1321013, System.currentTimeMillis(), getCooldown(applyfrom));
                applyto.removeCooldown(1321013);
            }
        }
        if (fatigueChange != 0 && applyto.getSummonedFamiliar() != null && (familiars == null || familiars.contains(applyto.getSummonedFamiliar().getFamiliar()))) {
            applyto.getSummonedFamiliar().addFatigue(applyto, fatigueChange);
        }
        if (rewardMeso != 0) {
            applyto.gainMeso(rewardMeso, false);
        }
        if (rewardItem != null && totalprob > 0) {
            for (Triple<Integer, Integer, Integer> reward : rewardItem) {
                if (MapleInventoryManipulator.checkSpace(applyto.getClient(), reward.left, reward.mid, "") && reward.right > 0 && Randomizer.nextInt(totalprob) < reward.right) { // Total prob
                    if (GameConstants.getInventoryType(reward.left) == InventoryType.EQUIP) {
                        final Item item = MapleItemInformationProvider.getInstance().getEquipById(reward.left);
                        item.setGMLog("Reward item (effect): " + sourceid + " on " + LocalDateTime.now());
                        MapleInventoryManipulator.addbyItem(applyto.getClient(), item);
                    } else {
                        MapleInventoryManipulator.addById(applyto.getClient(), reward.left, reward.mid.shortValue(), "Reward item (effect): " + sourceid + " on " + LocalDateTime.now());
                    }
                }
            }
        }
        if (familiarTarget == 2 && applyfrom.getParty() != null && primary) { //to party
            for (MaplePartyCharacter mpc : applyfrom.getParty().getMembers()) {
                if (mpc.getId() != applyfrom.getId() && mpc.getChannel() == applyfrom.getClient().getChannel() && mpc.getMapid() == applyfrom.getMapId() && mpc.isOnline()) {
                    User mc = applyfrom.getMap().getCharacterById(mpc.getId());
                    if (mc != null) {
                        applyTo(applyfrom, mc, false, null, newDuration);
                    }
                }
            }
        } else if (familiarTarget == 3 && primary) {
            for (User mc : applyfrom.getMap().getCharacters()) {
                if (mc.getId() != applyfrom.getId()) {
                    applyTo(applyfrom, mc, false, null, newDuration);
                }
            }
        }

        return true;
    }

    public final boolean applyReturnScroll(final User applyto) {
        if (moveTo != -1) {

            if (applyto.getMap().getReturnMap().getId() != applyto.getMapId()
                    || sourceid == 2031010 || sourceid == 2030021 || sourceid == 20021110 || sourceid == 2030028 || sourceid == 20031203) {
                MapleMap target;
                if (moveTo == 999999999) {
                    target = applyto.getMap().getReturnMap();
                } else if (sourceid == 2030028 && moveTo == 103020000) {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                } else if (sourceid == 20031203 && moveTo == 150000000) {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                } else {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                    if (target.getId() / 10000000 != 60 && applyto.getMapId() / 10000000 != 61) {
                        if (target.getId() / 10000000 != 21 && applyto.getMapId() / 10000000 != 20) {
                            if (target.getId() / 10000000 != applyto.getMapId() / 10000000) {
                                applyto.dropMessage(5, "You can not teleport there as it is on a different continent.");
                                return false;
                            }
                        }
                    }
                }
                applyto.changeMap(target, target.getPortal(0));
                return true;
            }
        }
        return false;
    }

    private boolean isSoulStone() {
        return sourceid == Phantom.FINAL_FEINT;
    }

    private void applyBuff(final User applyfrom, int newDuration) {
        if (isSoulStone() && sourceid != 24111002) {
            if (applyfrom.getParty() != null) {
                int membrs = 0;
                for (User chr : applyfrom.getMap().getCharacters()) {
                    if (chr.getParty() != null && chr.getParty().getId() == applyfrom.getParty().getId() && chr.isAlive()) {
                        membrs++;
                    }
                }
                List<User> awarded = new ArrayList<>();
                while (awarded.size() < Math.min(membrs, info.get(StatInfo.y))) {
                    for (User chr : applyfrom.getMap().getCharacters()) {
                        if (chr != null && chr.isAlive() && chr.getParty() != null && chr.getParty().getId() == applyfrom.getParty().getId() && !awarded.contains(chr) && Randomizer.nextInt(info.get(StatInfo.y)) == 0) {
                            awarded.add(chr);
                        }
                    }
                }
                for (User chr : awarded) {
                    applyTo(applyfrom, chr, false, null, newDuration);
                    chr.getClient().SendPacket(EffectPacket.showOwnBuffEffect(sourceid, EffectPacket.SkillUseBySummoned, applyfrom.getLevel(), level));
                    chr.getMap().broadcastPacket(chr, EffectPacket.showBuffEffect(chr.getId(), sourceid, EffectPacket.SkillUseBySummoned, applyfrom.getLevel(), level), false);
                }
            }
        } else if (isPartyBuff() && (applyfrom.getParty() != null || isGmBuff() || applyfrom.inPVP())) {
            final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
            final List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));

            for (final MapleMapObject affectedmo : affecteds) {
                final User affected = (User) affectedmo;

                if (affected.getId() != applyfrom.getId() && (isGmBuff() || (applyfrom.inPVP() && affected.getTeam() == applyfrom.getTeam() && Integer.parseInt(applyfrom.getEventInstance().getProperty("type")) != 0) || (applyfrom.getParty() != null && affected.getParty() != null && applyfrom.getParty().getId() == affected.getParty().getId()))) {
                    if ((isResurrection() && !affected.isAlive()) || (!isResurrection() && affected.isAlive())) {
                        applyTo(applyfrom, affected, false, null, newDuration);
                        affected.getClient().SendPacket(EffectPacket.showOwnBuffEffect(sourceid, EffectPacket.SkillUseBySummoned, applyfrom.getLevel(), level));
                        affected.getMap().broadcastPacket(affected, EffectPacket.showBuffEffect(affected.getId(), sourceid, EffectPacket.SkillUseBySummoned, applyfrom.getLevel(), level), false);
                    }
                    if (isTimeLeap()) {
                        for (CoolDownValueHolder i : affected.getCooldowns()) {
                            if (i.skillId != 5121010) {
                                affected.removeCooldown(i.skillId);
                                affected.getClient().SendPacket(CField.skillCooldown(i.skillId, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeMonsterBuff(final User applyfrom) {
        List<MonsterStatus> cancel = new ArrayList<>();
        switch (sourceid) {
            case 1111007:
            case 51111005: //Mihile's magic crash
            case 1211009:
            case 1311007:
                cancel.add(MonsterStatus.WEAPON_DEFENSE_UP);
                cancel.add(MonsterStatus.MAGIC_DEFENSE_UP);
                cancel.add(MonsterStatus.WEAPON_ATTACK_UP);
                cancel.add(MonsterStatus.MAGIC_ATTACK_UP);
                break;
            default:
                return;
        }
        final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
        final List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (MonsterStatus stat : cancel) {
                    ((Mob) mo).cancelStatus(stat);
                }
            }
            i++;
            if (i >= info.get(StatInfo.mobCount)) {
                break;
            }
        }
    }

    public final void applyMonsterBuff(final User applyfrom) {
        final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
        final boolean pvp = applyfrom.inPVP();
        final MapleMapObjectType objType = pvp ? MapleMapObjectType.PLAYER : MapleMapObjectType.MONSTER;
        final List<MapleMapObject> affected = sourceid == 35111005 ? applyfrom.getMap().getMapObjectsInRange(applyfrom.getTruePosition(), Double.POSITIVE_INFINITY, Arrays.asList(objType)) : applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(objType));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (Map.Entry<MonsterStatus, Integer> stat : getMonsterStati().entrySet()) {
                    if (pvp) {
                        User chr = (User) mo;
                        Disease d = MonsterStatus.getLinkedDisease(stat.getKey());
                        if (d != null) {
                            MobSkill ms = new MobSkill(d.getDisease(), 1);
                            ms.setDuration(getDuration());
                            ms.setX(stat.getValue());
                            chr.giveDebuff(d, ms);
                        }
                    } else {
                        Mob mons = (Mob) mo;
                        if (sourceid == 35111005 && mons.getStats().isBoss()) {
                            break;
                        }
                        mons.applyStatus(applyfrom, new MonsterStatusEffect(stat.getKey(), stat.getValue(), sourceid, null, false), isPoison(), isSubTime(sourceid) ? getSubTime() : getDuration(), true, this);
                    }
                }
                if (pvp) {
                    User chr = (User) mo;
                    handleExtraPVP(applyfrom, chr);
                }
            }
            i++;
            if (i >= info.get(StatInfo.mobCount) && sourceid != 35111005) {
                break;
            }
        }
    }

    public final boolean isSubTime(final int source) {
        switch (source) {
            case 1201006: // threaten
            case 23111008: // spirits
            case 23111009:
            case 23111010:
            case 31101003:
            case 31121003:
            case 31121005:
                //case 1301013:
                return true;
        }
        return false;
    }

    public final void handleExtraPVP(User applyfrom, User chr) {
        if (sourceid == 2311005 || sourceid == 5121005 || sourceid == 1201006 || (GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 104)) { //doom, threaten, snatch
            final long starttime = System.currentTimeMillis();

            final int localsourceid = sourceid == 5121005 ? 90002000 : sourceid;
            final Map<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
            switch (sourceid) {
                case 2311005:
                    localstatups.put(CharacterTemporaryStat.Morph, 7);
                    break;
                case 1201006:
                    //localstatups.put(CharacterTemporaryStat.THREATEN_PVP, (int) level);
                    break;
                case 5121005:
                    //localstatups.put(CharacterTemporaryStat.SNATCH, 1);
                    break;
                default:
                    localstatups.put(CharacterTemporaryStat.Morph, info.get(StatInfo.x));
                    break;
            }
            for (Map.Entry<CharacterTemporaryStat, Integer> stat : statups.entrySet()) {
                chr.setBuffedValue(stat.getKey(), stat.getValue());
            }
            chr.getClient().SendPacket(BuffPacket.giveBuff(chr, localsourceid, getDuration(), localstatups, this));
            chr.registerEffect(this, starttime, BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime, localstatups), isSubTime(sourceid) ? getSubTime() : getDuration()), localstatups, false, getDuration(), applyfrom.getId());
        }
    }

    public final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft) {
        return calculateBoundingBox(posFrom, facingLeft, lt, rb, info.get(StatInfo.range));
    }

    public final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft, int addedRange) {
        return calculateBoundingBox(posFrom, facingLeft, lt, rb, info.get(StatInfo.range) + addedRange);
    }

    public static Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft, final Point lt, final Point rb, final int range) {
        if (lt == null || rb == null) {
            return new Rectangle((facingLeft ? (-200 - range) : 0) + posFrom.x, (-100 - range) + posFrom.y, 200 + range, 100 + range);
        }
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x - range, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x + range, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
    }

    public final double getMaxDistanceSq() { //lt = infront of you, rb = behind you; not gonna distanceSq the two points since this is in relative to player position which is (0,0) and not both directions, just one
        final int maxX = Math.max(Math.abs(lt == null ? 0 : lt.x), Math.abs(rb == null ? 0 : rb.x));
        final int maxY = Math.max(Math.abs(lt == null ? 0 : lt.y), Math.abs(rb == null ? 0 : rb.y));
        return (maxX * maxX) + (maxY * maxY);
    }

    public final void setDuration(int d) {
        this.info.put(StatInfo.time, d);
    }

    public final void silentApplyBuff(User chr, long starttime, int localDuration, Map<CharacterTemporaryStat, Integer> statup, int cid) {
        chr.registerEffect(this, starttime, BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime, statup),
                ((starttime + localDuration) - System.currentTimeMillis())), statup, true, localDuration, cid);
        final SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final Summon tosummon = new Summon(chr, this, chr.getTruePosition(), summonMovementType, localDuration);
            if (!tosummon.isPuppet()) {
                chr.getCheatTracker().resetSummonAttack();
                chr.getMap().spawnSummon(tosummon);
                chr.addSummon(tosummon);
                tosummon.addHP(info.get(StatInfo.x).shortValue());
                if (isBeholder()) {
                    tosummon.addHP((short) 1);
                }
            }
        }
    }

    public final void applyComboAttack(User applyto, short combo) {
        long starttime = System.currentTimeMillis();
        EnumMap stat = new EnumMap(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.ComboCounter, (int) combo);
        applyto.getClient().SendPacket(BuffPacket.giveBuff(applyto, this.sourceid, 99999, stat, this));
        applyto.registerEffect(this, starttime, null, applyto.getId());
    }

    public final void applyKaiserCombo(User applyto, short combo) {
        long starttime = System.currentTimeMillis();
        EnumMap stat = new EnumMap(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.SmashStack, (int) combo);
        applyto.registerEffect(this, starttime, null, stat, false, info.get(StatInfo.time), applyto.getId());
        applyto.getClient().SendPacket(BuffPacket.giveBuff(applyto, 0, 99999, stat, this));
    }

    public final void applyXenonCombo(User applyto, int combo) {
        long starttime = System.currentTimeMillis();
        EnumMap stat = new EnumMap(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.SurplusSupply, combo);
        applyto.registerEffect(this, starttime, null, stat, false, info.get(StatInfo.time), applyto.getId());
        applyto.getClient().SendPacket(BuffPacket.giveBuff(applyto, 0, 99999, stat, this));
    }

    public final void applyComboBuff(User applyto, short combo) {
        long starttime = System.currentTimeMillis();
        EnumMap stat = new EnumMap(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.ComboAbilityBuff, (int) combo);
        applyto.getClient().SendPacket(BuffPacket.giveBuff(applyto, this.sourceid, 99999, stat, this));
        applyto.registerEffect(this, starttime, null, applyto.getId());
    }

    public final void applyBlackBlessingBuff(User applyto, int combo) {
        long starttime = System.currentTimeMillis();
        EnumMap stat = new EnumMap(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.BlessOfDarkness, combo);
        applyto.registerEffect(this, starttime, null, stat, false, info.get(StatInfo.time), applyto.getId());
        applyto.getClient().SendPacket(BuffPacket.giveBuff(applyto, this.sourceid, 99999, stat, this));
    }

    public final void applyLunarTideBuff(User applyto) {
        final long starttime = System.currentTimeMillis();
        EnumMap stat = new EnumMap(CharacterTemporaryStat.class);
        double hpx = applyto.getStat().getMaxHp() / applyto.getStat().getHp();
        double mpx = applyto.getStat().getMaxMp() / applyto.getStat().getMp();
        stat.put(CharacterTemporaryStat.LifeTidal, hpx >= mpx ? 2 : 1); //for now
        applyto.registerEffect(this, starttime, null, stat, false, info.get(StatInfo.time), applyto.getId());
        applyto.getClient().SendPacket(BuffPacket.giveBuff(applyto, this.sourceid, 99999999, stat, this));
    }

    public final void applyEnergyBuff(final User applyto, int buffid, int targets) {
        final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.EnergyCharged, 1000);
        applyto.registerEffect(this, System.currentTimeMillis(), null, stat, false, -1, applyto.getId());
        applyto.getClient().SendPacket(BuffPacket.giveEnergyCharged(applyto, 1000, buffid, -1));
    }

    /*
    *   Expire Buffs
    *   @purpose Force specified buffstats to expire after a certain time.
    **/
    public void setBuffExpireRate(User oPlayer, long nDuration, EnumMap<CharacterTemporaryStat, Integer> selectStats) {
        Map<CharacterTemporaryStat, Integer> temporaryStats = selectStats;
        final long nStartTime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(oPlayer, this, nStartTime, temporaryStats);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, nDuration);
        oPlayer.registerEffect(this, nStartTime, schedule, temporaryStats, false, (int) nDuration, oPlayer.getId());
    }

    public void applyBuffEffect(final User applyfrom, final User applyto, final boolean primary, final int newDuration) {
        int localDuration = newDuration;
        if (primary) {
            localDuration = Math.max(newDuration, alchemistModifyVal(applyfrom, localDuration, false));
        }
        Map<CharacterTemporaryStat, Integer> localstatups = statups;
        int maskedDuration = 0;

        handleExtraEffect(applyfrom, applyto, localstatups, localDuration);

        if (localstatups.size() > 0 && !applyto.isHidden()) {
            applyto.getMap().broadcastPacket(applyto, EffectPacket.showBuffEffect(applyto.getId(), sourceid, EffectPacket.SkillUse, applyto.getLevel(), level), false);
        }
        if (!isMechDoor() && getSummonMovementType() == null) {
            applyto.cancelEffect(this, true, -1, localstatups);
        }
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime, localstatups);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, maskedDuration > 0 ? maskedDuration : localDuration);
        applyto.registerEffect(this, starttime, schedule, localstatups, false, localDuration, applyfrom.getId());

        if (localstatups.size() > 0) {//I rather write AFTER applying the damn thing. tyvm.
            applyto.getClient().SendPacket(BuffPacket.giveBuff(applyto, sourceid, localDuration, localstatups, this));
            applyto.getMap().broadcastPacket(BuffPacket.giveForeignBuff(applyto));
        }
    }

    private void handleExtraEffect(final User applyfrom, final User applyto, Map<CharacterTemporaryStat, Integer> effects, int localDuration) {
        switch (sourceid) {
            case 2022963: // Ryden's Poison
                effects.put(CharacterTemporaryStat.IndieSpeed, info.get(StatInfo.indieSpeed));
                break;
            case 21101003: // Body Pressure
            case Aran.BODY_PRESSURE:
                effects.put(CharacterTemporaryStat.BodyPressure, info.get(StatInfo.x));
                break;
            case 21100005: // Combo Drain
            case 32101004: // Combo Drain
            case Fighter.COMBO_ATTACK:
            case Aran.DRAIN:
                effects.put(CharacterTemporaryStat.ComboDrain, info.get(StatInfo.x));
                break;
            case DawnWarrior.FALLING_MOON:
                effects.put(CharacterTemporaryStat.ComboDrain, info.get(StatInfo.x));
                break;
            case 32111012:
            case 32121017:
            case 32121018:
            case 32101009:
            case 32001016: //Battle Mage Auras
                if (applyto.isHidden()) {
                    break;
                }
                /*  if (sourceid == BattleMage.DARK_AURA || sourceid == BattleMage.DARK_AURA_BOSS_RUSH) { 
                    effects.put(CharacterTemporaryStat.DamR, info.get(MapleStatInfo.indieDamR));
                }*/
                final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.BMageAura, (int) getLevel());
                break;
            case Kaiser.TEMPEST_BLADES:
            case Kaiser.ADVANCED_TEMPEST_BLADES:
                if (applyfrom.getInventory(InventoryType.EQUIPPED).getItem((short) -11) == null) {
                    break;
                } else {
                    this.statups.put(CharacterTemporaryStat.StopForceAtomInfo, applyfrom.getSkillLevel(sourceid));
                    this.weapon = applyfrom.getInventory(InventoryType.EQUIPPED).getItem((short) -11).getItemId();
                    int index = (sourceid == 61101002) ? 1 : 2;
                    int effectCount = (sourceid == 61101002) ? 3 : 5;
                    applyto.setStopForceAtoms(index, effectCount, this.weapon);
                    break;
                }
            case Kanna.HAKU_REBORN:
                if (applyto.getHaku() != null) {
                    applyto.getHaku().sendStats();
                    applyto.getMap().broadcastPacket(applyto, CField.transformHaku(applyto.getId(), applyto.getHaku().getStats()), true);
                }
                break;
            case Xenon.EMERGENCY_RESUPPLY: ///xenon emergency by @Mally
            {
                applyto.gainXenonSurplus((short) 10);
                applyfrom.getClient().SendPacket(WvsContext.enableActions());
                break;
            }
            case Luminous.CHANGE_LIGHTDARK_MODE:
            case Luminous.EQUALIZE: {
                applyto.changeLuminousMode();
                break;
            }
            case CannonBlaster.BARREL_ROULETTE: {
                final int zz = Randomizer.nextInt(4) + 1;
                applyto.getMap().broadcastPacket(applyto, CField.EffectPacket.showDiceEffect(applyto.getId(), sourceid, zz, -1, level), false);
                applyto.getClient().SendPacket(CField.EffectPacket.showOwnDiceEffect(sourceid, zz, -1, level));
                break;
            }
            case Mihile.SOUL_LINK:
                if (applyfrom.getStatForBuff(CharacterTemporaryStat.MichaelSoulLink) == null) {
                    break;
                }
                applyfrom.cancelEffectFromTemporaryStat(CharacterTemporaryStat.MichaelSoulLink);
                break;
            case Mechanic.ROLL_OF_THE_DICE:
            case Marauder.ROLL_OF_THE_DICE_5:
            case CannonBlaster.LUCK_OF_THE_DIE:
            case Outlaw.ROLL_OF_THE_DICE_2: {
                final int diceRoll = Randomizer.nextInt(6) + 1;
                applyto.getMap().broadcastPacket(applyto, EffectPacket.showDiceEffect(applyto.getId(), sourceid, diceRoll, -1, level), false);
                applyto.getClient().SendPacket(EffectPacket.showOwnDiceEffect(sourceid, diceRoll, -1, level));

                if (diceRoll <= 1) {
                    return;
                } // No buff if the player rolled a one on both dice.

                applyto.dropMessage(-6, "[Lucky Die] You tried your luck on the die!");
                statups = new EnumMap<>(CharacterTemporaryStat.class);
                statups.put(CharacterTemporaryStat.Dice, diceRoll);
                applyto.getClient().SendPacket(BuffPacket.giveDice(diceRoll, sourceid, localDuration, statups));
                break;
            }
            case Buccaneer.DOUBLE_DOWN_1:
            case Corsair.DOUBLE_DOWN_4:
            case CannonMaster.DOUBLE_DOWN:
            case Mechanic.DOUBLE_DOWN_3: {//dice
                final int diceRoll = Randomizer.nextInt(6) + 1;
                final int diceRoll2 = makeChanceResult() ? (Randomizer.nextInt(6) + 1) : 0;
                applyto.getMap().broadcastPacket(applyto, EffectPacket.showDiceEffect(applyto.getId(), sourceid, diceRoll, diceRoll2 > 0 ? -1 : 0, level), false);
                applyto.getClient().SendPacket(EffectPacket.showOwnDiceEffect(sourceid, diceRoll, diceRoll2 > 0 ? -1 : 0, level));

                if (diceRoll <= 1 && diceRoll2 <= 1) {
                    return;
                } // No buff if the player rolled a one on both dice.

                final int buffid = diceRoll == diceRoll2 ? (diceRoll * 100) : (diceRoll <= 1 ? diceRoll2 : (diceRoll2 <= 1 ? diceRoll : (diceRoll * 10 + diceRoll2)));
                if (buffid >= 100) { //just because of animation lol
                    applyto.dropMessage(-6, "[Double Lucky Dice] You have rolled a Double Down! (" + (buffid / 100) + ")");
                } else if (buffid >= 10) {
                    applyto.dropMessage(-6, "[Double Lucky Dice] You have rolled two dice. (" + (buffid / 10) + " and " + (buffid % 10) + ")");
                }
                statups = new EnumMap<>(CharacterTemporaryStat.class);
                statups.put(CharacterTemporaryStat.Dice, buffid);
                applyto.getClient().SendPacket(BuffPacket.giveDice(diceRoll, sourceid, localDuration, statups));
                return;
            }
            case Phantom.JUDGMENT_DRAW_4:
            case Phantom.JUDGMENT_DRAW_5:
                int zz = Randomizer.nextInt(this.sourceid == 20031209 ? 2 : 5) + 1;
                int skillid = 24100003;
                if (applyto.getSkillLevel(24120002) > 0) {
                    skillid = 24120002;
                }
                applyto.setCardStack((byte) 0);
                applyto.resetRunningStack();
                applyto.addRunningStack(skillid == 24100003 ? 5 : 10);
                applyto.getMap().broadcastPacket(applyto, PhantomPacket.gainCardStack(applyto.getId(), applyto.getRunningStack(), skillid == 24120002 ? 2 : 1, skillid, 0, skillid == 24100003 ? 5 : 10), true);
                applyto.getMap().broadcastPacket(applyto, CField.EffectPacket.showDiceEffect(applyto.getId(), this.sourceid, zz, -1, this.level), false);
                applyto.getClient().SendPacket(CField.EffectPacket.showOwnDiceEffect(this.sourceid, zz, -1, this.level));
                effects = new EnumMap(CharacterTemporaryStat.class);
                effects.put(CharacterTemporaryStat.Judgement, zz);
                if (zz == 5) {
                    applyfrom.getClient().SendPacket(WvsContext.enableActions());
                }
                break;
            case WhiteKnight.LIGHTNING_CHARGE_1:
                if (applyto.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null && applyto.getBuffSource(CharacterTemporaryStat.WeaponCharge) != sourceid) {
                    effects.put(CharacterTemporaryStat.AssistCharge, 1);
                } else if (!applyto.isHidden()) {
                    effects.put(CharacterTemporaryStat.WeaponCharge, 1);
                }
            case Bishop.ADVANCED_BLESSING: //Advanced Bless
                applyto.cancelEffectFromTemporaryStat(CharacterTemporaryStat.Bless);
                break;
            case Magician.MAGIC_GUARD_2:
            case Luminous.STANDARD_MAGIC_GUARD:
            case Evan.MAGIC_GUARD_3:
                applyto.cancelEffectFromTemporaryStat(CharacterTemporaryStat.MagicGuard);
                this.setDuration(Integer.MAX_VALUE);
                break;
            case Kaiser.FINAL_FORM: // 3job kaiser By Mixtamal6 
            case Kaiser.FINAL_FORM_1: // 4job kaiser
            case Kaiser.FINAL_TRANCE: // hyper kaiser
                applyto.resetKaiserCombo();
                break;
            case Hero.ENRAGE: // Enrage
                applyto.handleOrbconsume(10);
                break;
            case DemonAvenger.OVERLOAD_RELEASE: {
                applyto.setExceed((short) 0);
                applyto.addHP((int) ((applyto.getStat().getCurrentMaxHp() * (level / 100.0D)) * (getX() / 100.0D)));
                applyfrom.getClient().SendPacket(WvsContext.enableActions());
                applyto.getClient().SendPacket(JobPacket.AvengerPacket.cancelExceed()); //Set Exceed to 0
                break;
            }
            case DarkKnight.SACRIFICE_1: { //Sacrifice
                info.put(StatInfo.time, 40000);
                effects.put(CharacterTemporaryStat.IgnoreTargetDEF, info.get(StatInfo.ignoreMobpdpR));
                effects.put(CharacterTemporaryStat.BDR, info.get(StatInfo.indieBDR));
                applyto.addHP((int) ((applyto.getStat().getCurrentMaxHp() * (level / 100.0D)) * (getX() / 100.0D)));
                applyfrom.getClient().SendPacket(WvsContext.enableActions());
                break;
            }
        }
    }

    private int calcHPChange(final User applyfrom, final boolean primary) {
        int hpchange = 0;
        if (info.get(StatInfo.hp) != 0) {
            if (primary) {
                hpchange += alchemistModifyVal(applyfrom, info.get(StatInfo.hp), true);
            } else {
                hpchange += info.get(StatInfo.hp);
            }
            if (applyfrom.hasDisease(Disease.ZOMBIFY)) {
                hpchange /= 2;
            }
        }
        if (hpR != 0) {
            hpchange += (int) (applyfrom.getStat().getCurrentMaxHp() * hpR) / (applyfrom.hasDisease(Disease.ZOMBIFY) ? 2 : 1);
        }
        // actually receivers probably never get any hp when it's not heal but whatever
        if (primary) {
            if (info.get(StatInfo.hpCon) != 0) {
                hpchange -= info.get(StatInfo.hpCon);
            }
        }
        switch (this.sourceid) {
            case 4211001: // Chakra
                final PlayerStats stat = applyfrom.getStat();
                int v42 = getY() + 100;
                int v38 = Randomizer.rand(1, 100) + 100;
                hpchange = (int) ((v38 * stat.getLuk() * 0.033 + stat.getDex()) * v42 * 0.002);
                hpchange += makeHealHP(getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
                break;
        }
        return hpchange;
    }

    private static int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
    }

    private int calcMPChange(final User applyfrom, final boolean primary) {
        int mpchange = 0;
        if (info.get(StatInfo.mp) != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, info.get(StatInfo.mp), false); // recovery up doesn't apply for mp
            } else {
                mpchange += info.get(StatInfo.mp);
            }
        }
        if (mpR != 0) {
            mpchange += (int) (applyfrom.getStat().getCurrentMaxMp(applyfrom.getJob()) * mpR);
        }
        if (GameConstants.isDemonSlayer(applyfrom.getJob())) {
            mpchange = 0;
        }
        if (primary) {
            if (info.get(StatInfo.mpCon) != 0 && !GameConstants.isDemonSlayer(applyfrom.getJob())) {
                boolean free = true; // Should be false, but handling elsewhere to fix mana issues.
                if (applyfrom.getJob() == 411 || applyfrom.getJob() == 412) {
                    final Skill expert = SkillFactory.getSkill(4110012);
                    if (applyfrom.getTotalSkillLevel(expert) > 0) {
                        final StatEffect eff = expert.getEffect(applyfrom.getTotalSkillLevel(expert));
                        if (eff.makeChanceResult()) {
                            free = true;
                        }
                    }
                }
                if (applyfrom.getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
                    mpchange = 0;
                } else if (!free) { // MP hack fix? kinda.
                    double mpcalc = (info.get(StatInfo.mpCon) - (info.get(StatInfo.mpCon) * applyfrom.getStat().mpconReduce / 100)) * (applyfrom.getStat().mpconPercent / 100.0);
                    double finalMPCalc = mpcalc * 0.65; //for now just as a balance, so some skills cost a bit more, but the 2x ones are almost correct.
                    mpchange -= finalMPCalc;
                    //mpchange -= (info.get(MapleStatInfo.mpCon) - (info.get(MapleStatInfo.mpCon) * applyfrom.getStat().mpconReduce / 100)) * (applyfrom.getStat().mpconPercent / 100.0);
                }
            } else if (info.get(StatInfo.forceCon) != 0) {
                if (applyfrom.hasBuff(CharacterTemporaryStat.InfinityForce)) {
                    mpchange = 0;
                } else {
                    int nFuryCost = info.get(StatInfo.forceCon);
                    if (applyfrom.hasSkill(DemonSlayer.BLUE_BLOOD)) {
                        mpchange -= (nFuryCost * 0.8);
                    } else {
                        mpchange = -nFuryCost;
                    }
                }
            }
        }
        return mpchange;
    }

    public final int alchemistModifyVal(final User chr, final int val, final boolean withX) {
        return (val * (100 + (withX ? chr.getStat().RecoveryUP : (chr.getStat().BuffUP_Skill + (getSummonMovementType() == null ? 0 : chr.getStat().BuffUP_Summon)))) / 100);
    }

    public final int calcPowerChange(final User applyfrom) {
        int powerchange = 0;
        if (info.get(StatInfo.powerCon) != 0 && GameConstants.isXenon(applyfrom.getJob())) {
            if (applyfrom.getBuffedValue(CharacterTemporaryStat.AmaranthGenerator) != null) {
                powerchange = 0;
            } else {
                powerchange = info.get(StatInfo.powerCon);
            }
        }
        return powerchange;
    }

    public final int calcPsychicPowerChange(final User applyfrom) {
        int powerchange = 0;
        if (GameConstants.isKinesis(applyfrom.getJob())) {
            if (info.get(StatInfo.ppCon) != 0) {
                powerchange = info.get(StatInfo.ppCon) * -1;
            } else if (info.get(StatInfo.ppRecovery) != 0) {
                powerchange = info.get(StatInfo.ppRecovery);
            }
        }
        return powerchange;
    }

    public final void setSourceId(final int newid) {
        sourceid = newid;
    }

    public final boolean isGmBuff() {
        switch (sourceid) {
            case 10001075: //Empress Prayer
            case 9001000: // GM dispel
            case 9001001: // GM haste
            case 9001002: // GM Holy Symbol
            case 9001003: // GM Bless
            case 9001005: // GM resurrection
            case 9001008: // GM Hyper body

            case 9101000:
            case 9101001:
            case 9101002:
            case 9101003:
            case 9101005:
            case 9101008:
                return true;
            default:
                return GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1005;
        }
    }

    public final boolean isInflation() {
        return inflation > 0;
    }

    public final int getInflation() {
        return inflation;
    }

    public final boolean isEnergyCharged() {
        return (sourceid == 5100015 || sourceid == 15100004);
    }

    public boolean isMonsterBuff() {
        switch (sourceid) {
            case 1211013: // Threaten
            case 1201006: // threaten
            case 2101003: // fp slow
            case 2201003: // il slow
            case 5011002:
            case 12101001: // cygnus slow
            case 2211004: // il seal
            case 2111004: // fp seal
            case 12111002: // cygnus seal
            case 2311005: // doom
            case 4111003: // shadow web
            case 14111001: // cygnus web
            case 4121004: // Ninja ambush
            case 4221004: // Ninja ambush
            case 22151001:
            case 22121000:
            case 22161002:
            case 4321002:
            case 4341003:
            case 90001002:
            case 90001003:
            case 90001004:
            case 90001005:
            case 90001006:
            case 1111007:
            case 51111005: //Mihile's magic crash
            case 1211009:
            case 1311007:
            case 35111005:
            case 32120000:
            case 32120001:
                return true;
        }
        return false;
    }

    public final void setPartyBuff(boolean pb) {
        this.partyBuff = pb;
    }

    private boolean isPartyBuff() {
        switch (sourceid) {
            case Hero.MAPLE_WARRIOR_10:
            case Paladin.MAPLE_WARRIOR_4:
            case DarkKnight.MAPLE_WARRIOR_100_10_1:
            case Aran.MAPLE_WARRIOR_70_7:
            case BattleMage.MAPLE_WARRIOR_6:
            case Bishop.MAPLE_WARRIOR_1000_100_10:
            case DualBlade.MAPLE_WARRIOR_200_20_2:
            case Bowmaster.MAPLE_WARRIOR_800_80_8:
            case Buccaneer.MAPLE_WARRIOR_30_3:
            case CannonMaster.MAPLE_WARRIOR_8:
            case Corsair.MAPLE_WARRIOR_60_6:
            case DemonAvenger.MAPLE_WARRIOR_2:
            case DemonSlayer.MAPLE_WARRIOR_900_90_9:
            case Evan.MAPLE_WARRIOR_100_10:
            case Evan.MAPLE_WARRIOR_90_9:
            case FirePoisonArchMage.MAPLE_WARRIOR_5:
            case Jett.MAPLE_WARRIOR_400_40_4:
            case Kanna.DAWNS_WARRIOR_MAPLE_WARRIOR:
            case Luminous.MAPLE_WARRIOR_300_30_3:
            case Marksman.MAPLE_WARRIOR_20_2:
            case Mechanic.MAPLE_WARRIOR_600_60_6:
            case Mercedes.MAPLE_WARRIOR_500_50_5:
            case NightLord.MAPLE_WARRIOR_1:
            case Shade.MAPLE_WARRIOR_7:
            case Shadower.MAPLE_WARRIOR_700_70_7:
            case WildHunter.MAPLE_WARRIOR_50_5:
            case Xenon.MAPLE_WARRIOR_9:
            case DawnWarrior.CALL_OF_CYGNUS_3:
            case BlazeWizard.CALL_OF_CYGNUS_5:
            case WindArcher.CALL_OF_CYGNUS_4:
            case NightWalker.CALL_OF_CYGNUS_2:
            case ThunderBreaker.CALL_OF_CYGNUS:
            case Mihile.CALL_OF_CYGNUS_1:
            case Aran.MAHA_BLESSING:
            case BattleMage.HASTY_AURA:
            case BattleMage.DRAINING_AURA:
            case BattleMage.BLUE_AURA:
            case BattleMage.DARK_AURA:
            case BattleMage.WEAKENING_AURA:
            case Spearman.IRON_WILL_2:
            case Spearman.HYPER_BODY_1:
            case FirePoisonWizard.MEDITATION_1:
            case IceLightningWizard.MEDITATION_2:
            case Luminous.PHOTIC_MEDITATION:
            case Cleric.HEAL:
            case Cleric.BLESS:
            case Priest.DISPEL:
            case Priest.HOLY_SYMBOL_1:
            case Bishop.RESURRECTION_2:
            case Bishop.HOLY_MAGIC_SHELL_EXTRA_GUARD:
            case Bowmaster.SHARP_EYES_3:
            case Marksman.SHARP_EYES:
            case WindArcher.SHARP_EYES_2:
            case WildHunter.SHARP_EYES_1:
            case Beginner.DECENT_ADVANCED_BLESSING_70_7:
            case Beginner.DECENT_COMBAT_ORDERS_70_7:
            case Beginner.DECENT_SPEED_INFUSION_70_7:
            case Beginner.DECENT_HYPER_BODY_70_7:
            case Beginner.DECENT_MYSTIC_DOOR_70_7:
            case Beginner.DECENT_SHARP_EYES_70_7:
            case Citizen.DECENT_ADVANCED_BLESSING_80_8:
            case Citizen.DECENT_COMBAT_ORDERS_80_8:
            case Citizen.DECENT_HYPER_BODY_80_8:
            case Citizen.DECENT_MYSTIC_DOOR_80_8:
            case Citizen.DECENT_SHARP_EYES_80_8:
            case Citizen.DECENT_SPEED_INFUSION_80_8:
            case Evan.DECENT_ADVANCED_BLESSING_50_5:
            case Evan.DECENT_COMBAT_ORDERS_50_5:
            case Evan.DECENT_HYPER_BODY_50_5:
            case Evan.DECENT_MYSTIC_DOOR_50_5:
            case Evan.DECENT_SHARP_EYES_50_5:
            case Evan.DECENT_SPEED_INFUSION_50_5:
            case Kinesis.DECENT_HASTE_7:
            case Kinesis.DECENT_ADVANCED_BLESSING_8:
            case Kinesis.DECENT_HYPER_BODY_8:
            case Kinesis.DECENT_MYSTIC_DOOR_8:
            case Kinesis.DECENT_SHARP_EYES_8:
            case Kinesis.DECENT_COMBAT_ORDERS_8:
            case Kinesis.DECENT_SPEED_INFUSION_8:
            case Shade.DECENT_ADVANCED_BLESSING:
            case Shade.DECENT_COMBAT_ORDERS:
            case Shade.DECENT_HASTE:
            case Shade.DECENT_HYPER_BODY:
            case Shade.DECENT_MYSTIC_DOOR:
            case Shade.DECENT_SHARP_EYES:
            case Shade.DECENT_SPEED_INFUSION:
            case Thief.HASTE_4:
            case NightWalker.HASTE:
            case ChiefBandit.MESO_MASTERY_1://MesoUp
            case DualBlade.THORNS:
            case DualBlade.THORNS_1:
            case Buccaneer.TIME_LEAP:
            case Aran.HEROS_WILL_70_7:
            case BattleMage.HEROS_WILL_6:
            case Bishop.HEROS_WILL_900_90_9:
            case Bowmaster.HEROS_WILL_800_80_8:
            case Buccaneer.HEROS_WILL_20_2:
            case CannonMaster.HEROS_WILL_7:
            case Corsair.HEROS_WILL_50_5:
            case DarkKnight.HEROS_WILL_100_10:
            case Evan.HEROS_WILL_80_8:
            case Evan.HEROS_WILL_90_9:
            case FirePoisonArchMage.HEROS_WILL_5:
            case Hero.HEROS_WILL_9:
            case IceLightningArchMage.HEROS_WILL_10:
            case Kanna.BLOSSOMING_DAWN_HEROS_WILL:
            case Luminous.HEROS_WILL_200_20_2:
            case Marksman.HEROS_WILL_10_1:
            case Mechanic.HEROS_WILL_600_60_6:
            case Mercedes.HEROS_WILL_500_50_5:
            case NightLord.HEROS_WILL_1:
            case Paladin.HEROS_WILL_3:
            case Shade.HEROS_WILL_4:
            case Shadower.HEROS_WILL_700_70_7:
            case WildHunter.HEROS_WILL_40_4:
            case Xenon.HEROS_WILL_8:
            case CannonBlaster.MONKEY_FURY:
            case Evan.MAGIC_RESISTANCE:
            case ThunderBreaker.SPEED_INFUSION:
            case Buccaneer.SPEED_INFUSION_1:
            case DarkKnight.HYPER_FURY_80_8:
            case DarkKnight.HYPER_HEALTH_80_8:
            case DarkKnight.HYPER_MANA_80_8:
                return true;
            default:
                return false;
        }
    }

    public final boolean isArcane() {
        return (sourceid == 2320011 || sourceid == 2220010 || sourceid == 2120010);
    }

    public final boolean isHayatoStance() {
        return (sourceid == 41001001 || sourceid == 41110008);
    }

    public final boolean isHeal() {
        return (sourceid == 2301002 || sourceid == 9101000 || sourceid == 9001000);
    }

    public final boolean isResurrection() {
        return (sourceid == 9001005 || sourceid == 9101005 || sourceid == 2321006);
    }

    public final boolean isTimeLeap() {
        return sourceid == 5121010;
    }

    public final int getHp() {
        return info.get(StatInfo.hp);
    }

    public final int getMp() {
        return info.get(StatInfo.mp);
    }

    public final int getDOTStack() {
        return info.get(StatInfo.dotSuperpos);
    }

    public final double getHpR() {
        return hpR;
    }

    public final double getMpR() {
        return mpR;
    }

    public final int getMastery() {
        return info.get(StatInfo.mastery);
    }

    public final int getWatk() {
        return info.get(StatInfo.pad);
    }

    public final int getMatk() {
        return info.get(StatInfo.mad);
    }

    public final int getWdef() {
        return info.get(StatInfo.pdd);
    }

    public final int getMdef() {
        return info.get(StatInfo.mdd);
    }

    public final int getAcc() {
        return info.get(StatInfo.acc);
    }

    /*public final int getAccR() {
        return info.get(MapleStatInfo.ar);
    }*/

    public final int getAvoid() {
        return info.get(StatInfo.eva);
    }

    public final int getSpeed() {
        return info.get(StatInfo.speed);
    }

    public final int getJump() {
        return info.get(StatInfo.jump);
    }

    public final int gettargetPlus() {
        return info.get(StatInfo.targetPlus);
    }

    /*public final int getSpeedMax() {
        return info.get(MapleStatInfo.speedMax);
    }*/

    public final int getPassiveSpeed() {
        return info.get(StatInfo.psdSpeed);
    }

    public final int getPassiveJump() {
        return info.get(StatInfo.psdJump);
    }

    public final int getDuration() {
        return info.get(StatInfo.time);
    }

    public final int getSubTime() {
        return info.get(StatInfo.subTime);
    }

    public final boolean isOverTime() {
        return overTime;
    }

    public final Map<CharacterTemporaryStat, Integer> getStatups() {
        return statups;
    }

    public final boolean sameSource(final StatEffect effect) {
        boolean sameSrc = this.sourceid == effect.sourceid;
        switch (this.sourceid) { // All these are passive skills, will have to cast the normal ones.
            case 32120000: // Advanced Dark Aura
                sameSrc = effect.sourceid == 32001003;
                break;
            case 32110000: // Advanced Blue Aura
                sameSrc = effect.sourceid == 32111012;
                break;
            case 32120001: // Advanced Yellow Aura
                sameSrc = effect.sourceid == 32101003;
                break;
            case 35120000: // Extreme Mech
                sameSrc = effect.sourceid == 35001002;
                break;
            case 41110008:
                sameSrc = effect.sourceid == 41001001;
                break;
        }
        return effect != null && sameSrc;
    }

    public final int getCr() {
        return info.get(StatInfo.cr);
    }

    public final int getT() {
        return info.get(StatInfo.t);
    }

    public final int getU() {
        return info.get(StatInfo.u);
    }

    public final int getV() {
        return info.get(StatInfo.v);
    }

    public final int getW() {
        return info.get(StatInfo.w);
    }

    public final int getPPRecovery() {
        return info.get(StatInfo.ppRecovery);
    }

    public final int getPPCon() {
        return info.get(StatInfo.ppCon);
    }

    public final int getX() {
        return info.get(StatInfo.x);
    }

    public final int getY() {
        return info.get(StatInfo.y);
    }

    public final int getZ() {
        return info.get(StatInfo.z);
    }

    public final int getS() {
        return info.get(StatInfo.s);
    }

    public final int getDamage() {
        return info.get(StatInfo.damage);
    }

    public final int getPVPDamage() {
        return info.get(StatInfo.PVPdamage);
    }

    public final int getAttackCount() {
        return info.get(StatInfo.attackCount);
    }

    public final int getBulletCount() {
        return info.get(StatInfo.bulletCount);
    }

    public final int getBulletConsume() {
        return info.get(StatInfo.bulletConsume);
    }

    public final int getOnActive() {
        return info.get(StatInfo.onActive);
    }

    public final int getMobCount() {
        return info.get(StatInfo.mobCount);
    }

    public final int getMoneyCon() {
        return moneyCon;
    }

    public final int getCooltimeReduceR() {
        return info.get(StatInfo.coolTimeR);
    }

    public final int getMesoAcquisition() {
        return info.get(StatInfo.mesoR);
    }

    public final int getCooldown(final User chra) {
        if (chra.getStat().coolTimeR > 0) {
            return Math.max(0, ((info.get(StatInfo.cooltime) * (100 - (chra.getStat().coolTimeR / 100))) - chra.getStat().reduceCooltime));
        }
        return Math.max(0, (info.get(StatInfo.cooltime) - chra.getStat().reduceCooltime));
    }

    public final Map<MonsterStatus, Integer> getMonsterStati() {
        return monsterStatus;
    }

    public final int getBerserk() {
        return berserk;
    }

    public final boolean isHide() {
        return (sourceid == 9001004 || sourceid == 9101004);
    }

    public final boolean isDragonBlood() {
        return sourceid == 1311008;
    }

    public final boolean isRecovery() {
        return (sourceid == 1001 || sourceid == 10001001 || sourceid == 20001001 || sourceid == 20011001 || sourceid == 20021001 || sourceid == 11001 || sourceid == 35121005);
    }

    public final boolean isBerserk() {
        return sourceid == 1320006;
    }

    public final boolean isBeholder() {
        return sourceid == 1321007 || sourceid == 1301013 || sourceid == 1311013;
    }

    public final boolean isMPRecovery() {
        return sourceid == 5101005;
    }

    public final boolean isInfinity() {
        return (sourceid == 2121004 || sourceid == 2221004 || sourceid == 2321004);
    }

    public final boolean isMagicDoor() {
        return (sourceid == Priest.MYSTIC_DOOR || sourceid % 10000 == 8001);
    }

    public final boolean isMesoGuard() {
        return sourceid == 4211005;
    }

    public final boolean isMechDoor() {
        return sourceid == Mechanic.OPEN_PORTAL_GX9;
    }

    /*public final boolean isComboRecharge() {
        return skill && sourceid == Aran.COMBO_RECHARGE;
    }

    public final boolean isDragonBlink() {
        return skill && sourceid == Evan.DRAGON_BLINK;
    }*/
    public final boolean isCharge() {
        switch (sourceid) {
            case 1211003:
            case 1211008:
            case 11111007:
            case 51111003: // Mihile's Radiant Charge
            case 12101005:
            case 15101006:
            case 21111005:
                return true;
        }
        return false;
    }

    public final boolean isPoison() {
        return info.get(StatInfo.dot) > 0 && info.get(StatInfo.dotTime) > 0;
    }

    public boolean isMist() {
        return (sourceid == 2111003 || sourceid == 4221006 || sourceid == 12111005 || sourceid == 14111006 || sourceid == 22161003 || sourceid == 32121006 || sourceid == 1076 || sourceid == 11076 || sourceid == 2311011 || sourceid == 4121015 || sourceid == 42111004 || sourceid == 42121005); // poison mist, smokescreen and flame gear, recovery aura
    }

    private boolean isSpiritClaw() {
        return sourceid == 4111009 || sourceid == 14111007 || sourceid == 5201008;
    }

    private boolean isSpiritBlast() {
        return sourceid == 5201008;
    }

    private boolean isDispel() {
        return (sourceid == 2311001 || sourceid == 9001000 || sourceid == 9101000 || sourceid == BeastTamer.MEOW_CURE);
    }

    private boolean isHeroWill() {
        switch (sourceid) {
            case 1121011:
            case 1221012:
            case 1321010:
            case 2121008:
            case 2221008:
            case 2321009:
            case 3121009:
            case 3221008:
            case 4121009:
            case 4221008:
            case 5121008:
            case 5221010:
            case 21121008:
            case 22171004:
            case 4341008:
            case 32121008:
            case 33121008:
            case 35121008:
            case 5321008:
            case 23121008:
            case 24121009:
            case 5721002:
                return true;
        }
        return false;
    }

    public final boolean isAranCombo() {
        return sourceid == 21000000;
    }

    public final boolean isCombo() {
        switch (sourceid) {
            case 1111002:
            case 11111001: // Combo
            case 1101013:
                return true;
        }
        return false;
    }

    public final boolean isDivineBody() {
        return GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1010;
    }

    public final boolean isDivineShield() {
        switch (sourceid) {
            case 1220013:
                return true;
        }
        return false;
    }

    public final boolean isBerserkFury() {
        return GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1011;
    }

    public final byte getLevel() {
        return level;
    }

    public final SummonMovementType getSummonMovementType() {
        switch (sourceid) {
            case 3211002: // puppet sniper
            case 3111002: // puppet ranger
            case 3221014: // Arrow Illusion
            case 33111003:
            case 13111024: // Emerald Flower
            case 13111004: // puppet cygnus
            case 5211001: // octopus - pirate
            case 5220002: // advanced octopus - pirate
            case 4341006:
            case 35111002:
            case 35111005:
            case 35111011:
            case 35121009:
            case 35121010:
            case 35121011:
            case 4111007: //dark flare
            case 4211007: //dark flare
            case 14111010: //dark flare
            case 33101008:
            case 35121003:
            case 3120012:
            case 3220012:
            case 5321003:
            case 5321004:
            case 5320011:
            case 5211014:
            case 5711001: // turret
            case 42100010:
            case 61111002: //Stone Dragon
            //    case 3121013:
            case 36121002:
            case 36121013:
            case 36121014:
            case 42111003:
                return SummonMovementType.STATIONARY;
            case 14111024: // Dark Servant
            case 14121054: // Shadow Illusion
            case 14121055:
            case 14121056:
                return SummonMovementType.SHADOW_SERVANT;
            case 3211005: // golden eagle
            case 3111005: // golden hawk
            case 3101007:
            case 3201007:
            case 33111005:
            case 3221005: // frostprey
            case 3121006: // phoenix
            case 23111008:
            case 23111009:
            case 23111010:
                return SummonMovementType.CIRCLE_FOLLOW;
            case 5211002: // bird - pirate
                return SummonMovementType.CIRCLE_STATIONARY;
            case 32111006: //reaper
            case 5211011:
            case 5211015:
            case 5211016:
                return SummonMovementType.WALK_STATIONARY;
            case 1321007: // beholder
            case 1301013: // Evil Eye
            case 1311013: // Evil Eye of Domination
            case 2121005: // elquines
            case 2221005: // ifrit
            case 2321003: // bahamut
            case 12111004: // Ifrit
            case 11001004: // soul
            case 12001004: // flame
            case 13001004: // storm
            case 14001005: // darkness
            case 15001004: // lightning
            case 35111001:
            case 35111010://satelite 2
            case 35111009: // satellite 1
            case 42101021: // Foxfire
            case 42121021: // Foxfire
            case WildHunter.SUMMON_JAGUAR:
            case WildHunter.SUMMON_JAGUAR_1:
            case WildHunter.SUMMON_JAGUAR_2:
            case WildHunter.SUMMON_JAGUAR_3:
            case WildHunter.SUMMON_JAGUAR_4:
            case WildHunter.SUMMON_JAGUAR_5:
            case WildHunter.SUMMON_JAGUAR_6:
            case WildHunter.SUMMON_JAGUAR_7:
            case WildHunter.SUMMON_JAGUAR_8:
            case WildHunter.SUMMON_JAGUAR_9:
            case WildHunter.SUMMON_JAGUAR_10:
                return SummonMovementType.FOLLOW;
        }
        if (isAngel()) {
            return SummonMovementType.FOLLOW;
        }
        return null;
    }

    public void addHpR(double hpR) {
        this.hpR += hpR;
    }

    public void setHpR(double hpR) {
        this.hpR = hpR;
    }

    public void addMpR(double mpR) {
        this.mpR += mpR;
    }

    public void setMpR(double mpR) {
        this.mpR = mpR;
    }

    public final boolean isAngel() {
        return GameConstants.isAngel(sourceid);
    }

    public final int getSourceId() {
        return sourceid;
    }

    public final boolean isIceKnight() {
        return GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1105;
    }

    public final boolean isSoaring() {
        return isSoaring_Normal() || isSoaring_Mount();
    }

    public final boolean isSoaring_Normal() {
        return GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1026;
    }

    public final boolean isSoaring_Mount() {
        return ((GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1142) || sourceid == 80001089 || sourceid == 112111000);
    }

    public final boolean isFinalAttack() {
        switch (sourceid) {
            case 13101002:
            case 11101002:
            case 51100002:
                return true;
        }
        return false;
    }

    public final boolean isMistEruption() {
        switch (sourceid) {
            case 2121003:
                return true;
        }
        return false;
    }

    public final boolean isShadow() {
        switch (sourceid) {
            case 4111002: // shadowpartner
            case 14111000: // cygnus
            case 4211008:
            case NightWalker.DARK_SERVANT:
            case 4331002:// Mirror Image
                return true;
        }
        return false;
    }

    /**
     *
     * @return true if the effect should happen based on it's probablity, false otherwise
     */
    public final boolean makeChanceResult() {
        return info.get(StatInfo.prop) >= 100 || Randomizer.nextInt(100) < info.get(StatInfo.prop);
    }

    public final int getProb() {
        return info.get(StatInfo.prop);
    }

    public final short getIgnoreMob() {
        return ignoreMob;
    }

    public final int getEnhancedHP() {
        return info.get(StatInfo.emhp);
    }

    public final int getEnhancedMP() {
        return info.get(StatInfo.emmp);
    }

    public final int getEnhancedWatk() {
        return info.get(StatInfo.epad);
    }

    public final int getEnhancedWdef() {
        return info.get(StatInfo.pdd);
    }

    public final int getEnhancedMatk() {
        return info.get(StatInfo.emad);
    }

    public final int getEnhancedMdef() {
        return info.get(StatInfo.emdd);
    }

    public final int getDOT() {
        return info.get(StatInfo.dot);
    }

    public final int getDOTTime() {
        return info.get(StatInfo.dotTime);
    }

    public final int getCriticalMax() {
        return info.get(StatInfo.criticaldamageMax);
    }

    public final int getCriticalMin() {
        return info.get(StatInfo.criticaldamageMin);
    }

    public final int getASRRate() {
        return info.get(StatInfo.asrR);
    }

    public final int getTERRate() {
        return info.get(StatInfo.terR);
    }

    public final int getDAMRate() {
        return info.get(StatInfo.damR);
    }

    public final int getHpToDamage() {
        return info.get(StatInfo.mhp2damX);
    }

    public final int getMpToDamage() {
        return info.get(StatInfo.mmp2damX);
    }

    public final int getLevelToDamage() {
        return info.get(StatInfo.lv2damX);
    }

    public final int getLevelToWatk() {
        return info.get(StatInfo.lv2pdX);
    }

    public final int getLevelToMatk() {
        return info.get(StatInfo.lv2mdX);
    }

    public final int getEXPLossRate() {
        return info.get(StatInfo.expLossReduceR);
    }

    public final int getBuffTimeRate() {
        return info.get(StatInfo.bufftimeR);
    }

    public final int getSuddenDeathR() {
        return info.get(StatInfo.suddenDeathR);
    }

    public final int getPercentAcc() {
        return info.get(StatInfo.accR);
    }

    public final int getPercentAvoid() {
        return info.get(StatInfo.evaR);
    }

    public final int getSummonTimeInc() {
        return info.get(StatInfo.summonTimeR);
    }

    public final int getMPConsumeEff() {
        return info.get(StatInfo.mpConEff);
    }

    public final short getMesoRate() {
        return mesoR;
    }

    public final int getEXP() {
        return exp;
    }

    public final int getAttackX() {
        return info.get(StatInfo.padX);
    }

    public final int getMagicX() {
        return info.get(StatInfo.madX);
    }

    public final int getPercentHP() {
        return info.get(StatInfo.mhpR);
    }

    public final int getPercentMP() {
        return info.get(StatInfo.mmpR);
    }

    public final int getConsume() {
        return consumeOnPickup;
    }

    public final int getSelfDestruction() {
        return info.get(StatInfo.selfDestruction);
    }

    public final int getCharColor() {
        return charColor;
    }

    public final List<Integer> getPetsCanConsume() {
        return petsCanConsume;
    }

    public final boolean isReturnScroll() {
        return (sourceid == 80001040 || sourceid == 20021110 || sourceid == 20031203);
    }

    public final boolean isMechChange() {
        switch (sourceid) {

            case 35121054:
            case 35111004: //siege
            case 35001001: //flame
            case 35101009:
            case 35121013:
            case 35121005:
            case 35100008:
                return true;
        }
        return false;
    }

    public final boolean isAnimalMode() {
        return (sourceid == 110001501 || sourceid == 110001502 || sourceid == 110001503 || sourceid == 110001504);
    }

    public final int getRange() {
        return info.get(StatInfo.range);
    }

    public final int getER() {
        return info.get(StatInfo.er);
    }

    public final int getPrice() {
        return info.get(StatInfo.price);
    }

    public final int getExtendPrice() {
        return info.get(StatInfo.extendPrice);
    }

    public final int getPeriod() {
        return info.get(StatInfo.period);
    }

    public final int getReqGuildLevel() {
        return info.get(StatInfo.reqGuildLevel);
    }

    public final byte getEXPRate() {
        return expR;
    }

    public final short getLifeID() {
        return lifeId;
    }

    public final short getUseLevel() {
        return useLevel;
    }

    /**
     * For extended inventory, bags
     *
     * @return
     */
    public final byte getSlotCount() {
        return slotCount;
    }

    /**
     * For extended inventory, bags
     *
     * @return
     */
    public final byte getSlotPerLine() {
        return slotPerLine;
    }

    public final int getStr() {
        return info.get(StatInfo.str);
    }

    public final int getStrX() {
        return info.get(StatInfo.strX);
    }

    public final int getStrFX() {
        return info.get(StatInfo.strFX);
    }

    public final int getDex() {
        return info.get(StatInfo.dex);
    }

    public final int getDexX() {
        return info.get(StatInfo.dexX);
    }

    public final int getDexFX() {
        return info.get(StatInfo.dexFX);
    }

    public final int getInt() {
        return info.get(StatInfo.int_);
    }

    public final int getIntX() {
        return info.get(StatInfo.intX);
    }

    public final int getIntFX() {
        return info.get(StatInfo.intFX);
    }

    public final int getLuk() {
        return info.get(StatInfo.luk);
    }

    public final int getLukX() {
        return info.get(StatInfo.lukX);
    }

    public final int getLukFX() {
        return info.get(StatInfo.lukFX);
    }

    public final int getMaxHpX() {
        return info.get(StatInfo.mhpX);
    }

    public final int getMaxMpX() {
        return info.get(StatInfo.mmpX);
    }

    public final int getMaxDemonFury() {
        return info.get(StatInfo.MDF);
    }

    public final int getAccX() {
        return info.get(StatInfo.accX);
    }

    public final int getMPConReduce() {
        return info.get(StatInfo.mpConReduce);
    }

    public final int getIndieMHp() {
        return info.get(StatInfo.indieMhp);
    }

    public final int getIndieMMp() {
        return info.get(StatInfo.indieMmp);
    }

    public final int getIndieAllStat() {
        return info.get(StatInfo.indieAllStat);
    }

    public final byte getType() {
        return type;
    }

    public int getBossDamage() {
        return info.get(StatInfo.bdR);
    }

    public int getInterval() {
        return interval;
    }

    public ArrayList<Pair<Integer, Integer>> getAvailableMaps() {
        return availableMap;
    }

    public int getPDDX() {
        return info.get(StatInfo.pddX);
    }

    public int getMDDX() {
        return info.get(StatInfo.mddX);
    }

    public int getPDDRate() {
        return info.get(StatInfo.pddR);
    }

    public int getMDDRate() {
        return info.get(StatInfo.mddR);
    }

    public int getMaxHpPerLevel() {
        return info.get(StatInfo.lv2mhp);
    }

    public int getMaxMpPerLevel() {
        return info.get(StatInfo.lv2mmp);
    }

    public Map<StatInfo, Integer> getAllStatInfo() {
        return Collections.unmodifiableMap(info);
    }

    public int getWeapon() {
        return weapon;
    }

    public void moveTo(int moveTo) {
        this.moveTo = moveTo;
    }

    public int getValue(StatInfo mapleStatInfo, int slv) {
        int result = 0;
        String value = mapleStatInfo.toString();
        if (value == null) {
            return 0;
        }
        if (Utility.isNumber(value)) {
            result = Integer.parseInt(value);
        } else {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            try {
                value = value.replace("u", "Math.ceil");
                value = value.replace("d", "Math.floor");
                Object res = engine.eval(value.replace("x", slv + ""));
                if (res instanceof Integer) {
                    result = (Integer) res;
                } else if (res instanceof Double) {
                    result = ((Double) res).intValue();
                }
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static class CancelEffectAction implements Runnable {

        private final StatEffect effect;
        private final WeakReference<User> target;
        private final long startTime;
        private final Map<CharacterTemporaryStat, Integer> statup;

        public CancelEffectAction(final User target, final StatEffect effect, final long startTime, final Map<CharacterTemporaryStat, Integer> statup) {
            this.effect = effect;
            this.target = new WeakReference<>(target);
            this.startTime = startTime;
            this.statup = statup;
        }

        @Override
        public void run() {
            final User realTarget = target.get();
            if (realTarget != null) {
                realTarget.cancelEffect(effect, false, startTime, statup);
            }
        }
    }

    public final boolean isUnstealable() {
        for (CharacterTemporaryStat b : statups.keySet()) {
            if (b == CharacterTemporaryStat.BasicStatUp) {
                return true;
            }
        }
        return sourceid == 4221013;
    }

}
