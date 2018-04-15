package handling.game;

import static handling.world.PlayersHandler.inArea;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.CharacterTemporaryStat;
import client.MapleClient;
import client.MapleDisease;
import client.MonsterStatus;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.world.PlayerHandler;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.MobSkill;
import server.maps.objects.User;
import handling.world.AttackMonster;
import tools.Pair;
import tools.Triple;
import net.InPacket;
import server.maps.Map_MCarnival;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

public final class AttackPvpHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final Lock ThreadLock = new ReentrantLock();
        final User chr = c.getPlayer();
        final int trueSkill = iPacket.DecodeInt();
        int skillid = trueSkill;
        if (chr == null || chr.isHidden() || !chr.isAlive() || chr.hasBlockedInventory() || chr.getMap() == null || !chr.inPVP() || !chr.getEventInstance().getProperty("started").equals("1") || skillid >= 90000000) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        final int lvl = Integer.parseInt(chr.getEventInstance().getProperty("lvl"));
        final int type = Integer.parseInt(chr.getEventInstance().getProperty("type"));
        final int ice = Integer.parseInt(chr.getEventInstance().getProperty("ice"));
        final int ourScore = Integer.parseInt(chr.getEventInstance().getProperty(String.valueOf(chr.getId())));
        int addedScore = 0, skillLevel = 0, trueSkillLevel = 0, animation = -1, attackCount, mobCount = 1, fakeMastery = chr.getStat().passive_mastery(), ignoreDEF = chr.getStat().ignoreTargetDEF, critRate = chr.getStat().passive_sharpeye_rate(), skillDamage = 100;
        boolean magic = false, move = false, pull = false, push = false;

        double maxdamage = lvl == 3 ? chr.getStat().getCurrentMaxBasePVPDamageL() : chr.getStat().getCurrentMaxBasePVPDamage();
        MapleStatEffect effect = null;
        chr.checkFollow();
        Rectangle box;

        final Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
        final Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
        final boolean katara = shield != null && shield.getItemId() / 10000 == 134;
        final boolean aran = weapon != null && weapon.getItemId() / 10000 == 144 && GameConstants.isAran(chr.getJob());
        iPacket.Skip(1); //skill level
        int chargeTime = 0;
        if (GameConstants.isMagicChargeSkill(skillid)) {
            chargeTime = iPacket.DecodeInt();
        } else {
            iPacket.Skip(4);
        }
        boolean facingLeft = iPacket.DecodeByte() > 0;
        if (skillid > 0) {
            if (skillid == 3211006 && chr.getTotalSkillLevel(3220010) > 0) { //hack
                skillid = 3220010;
            }
            final Skill skil = SkillFactory.getSkill(skillid);
            if (skil == null || skil.isPVPDisabled()) {
                c.SendPacket(CWvsContext.enableActions());
                return;
            }
            magic = skil.isMagic();
            move = skil.isMovement();
            push = skil.isPush();
            pull = skil.isPull();
            if (chr.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(skillid)) <= 0) {
                if (!GameConstants.isIceKnightSkill(skillid) && chr.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(skillid)) <= 0) {
                    c.Close();
                    return;
                }
                if (GameConstants.isIceKnightSkill(skillid) && chr.getBuffSource(CharacterTemporaryStat.Morph) % 10000 != 1105) {
                    return;
                }
            }
            animation = skil.getAnimation();
            if (animation == -1 && !skil.isMagic()) {
                final String after = aran ? "aran" : (katara ? "katara" : (weapon == null ? "barehands" : MapleItemInformationProvider.getInstance().getAfterImage(weapon.getItemId())));
                if (after != null) {
                    final List<Triple<String, Point, Point>> p = MapleItemInformationProvider.getInstance().getAfterImage(after); //hack
                    if (p != null) {
                        ThreadLock.lock();
                        try {
                            while (animation == -1) {
                                final Triple<String, Point, Point> ep = p.get(Randomizer.nextInt(p.size()));
                                if (!ep.left.contains("stab") && (skillid == 4001002 || skillid == 14001002)) { //disorder hack
                                    continue;
                                } else if (ep.left.contains("stab") && weapon != null && weapon.getItemId() / 10000 == 144) {
                                    continue;
                                }
                                if (SkillFactory.getDelay(ep.left) != null) {
                                    animation = SkillFactory.getDelay(ep.left);
                                }
                            }
                        } finally {
                            ThreadLock.unlock();
                        }
                    }
                }
            } else if (animation == -1 && skil.isMagic()) {
                animation = SkillFactory.getDelay(Randomizer.nextBoolean() ? "dash" : "dash2");
            }
            if (skil.isMagic()) {
                fakeMastery = 0; //whoosh still comes if you put this higher than 0
            }
            skillLevel = chr.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(skillid));
            trueSkillLevel = chr.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(trueSkill));
            effect = skil.getPVPEffect(skillLevel);
            ignoreDEF += effect.getIgnoreMob();
            critRate += effect.getCr();

            skillDamage = (effect.getDamage() + chr.getStat().getDamageIncrease(skillid));
            box = effect.calculateBoundingBox(chr.getTruePosition(), facingLeft, chr.getStat().defRange);
            attackCount = Math.max(effect.getBulletCount(), effect.getAttackCount());
            mobCount = Math.max(1, effect.getMobCount());

            if (effect.getCooldown(chr) > 0) {
                if (chr.skillisCooling(skillid)) {
                    c.SendPacket(CWvsContext.enableActions());
                    return;
                }
                if ((skillid != 35111004 && skillid != 35121013) || chr.getBuffSource(CharacterTemporaryStat.Mechanic) != skillid) { // Battleship
                    chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown(chr));
                }
            }
            switch (chr.getJob()) {
                case 111:
                case 112:
                case 1111:
                case 1112:
                    if (PlayerHandler.isFinisher(skillid) > 0) { // finisher
                        if (chr.getBuffedValue(CharacterTemporaryStat.ComboCounter) == null || chr.getBuffedValue(CharacterTemporaryStat.ComboCounter) <= 2) {
                            return;
                        }
                        skillDamage *= (chr.getBuffedValue(CharacterTemporaryStat.ComboCounter) - 1) / 2;
                        chr.handleOrbconsume(PlayerHandler.isFinisher(skillid));
                    }
                    break;
            }
        } else {
            attackCount = (katara ? 2 : 1);
            Point lt = null, rb = null;
            final String after = aran ? "aran" : (katara ? "katara" : (weapon == null ? "barehands" : MapleItemInformationProvider.getInstance().getAfterImage(weapon.getItemId())));
            if (after != null) {
                final List<Triple<String, Point, Point>> p = MapleItemInformationProvider.getInstance().getAfterImage(after);
                if (p != null) {
                    ThreadLock.lock();
                    try {
                        while (animation == -1) {
                            final Triple<String, Point, Point> ep = p.get(Randomizer.nextInt(p.size()));
                            if (!ep.left.contains("stab") && (skillid == 4001002 || skillid == 14001002)) { //disorder hack
                                continue;
                            } else if (ep.left.contains("stab") && weapon != null && weapon.getItemId() / 10000 == 147) {
                                continue;
                            }
                            if (SkillFactory.getDelay(ep.left) != null) {
                                animation = SkillFactory.getDelay(ep.left);
                                lt = ep.mid;
                                rb = ep.right;
                            }
                        }
                    } finally {
                        ThreadLock.unlock();
                    }
                }
            }
            box = MapleStatEffect.calculateBoundingBox(chr.getTruePosition(), facingLeft, lt, rb, chr.getStat().defRange);
        }
        chr.getCheatTracker().checkPVPAttack(skillid);
        final MapleStatEffect shad = chr.getStatForBuff(CharacterTemporaryStat.ShadowPartner);
        final int originalAttackCount = attackCount;
        attackCount *= (shad != null ? 2 : 1);

        iPacket.Skip(4); //?idk
        final int speed = iPacket.DecodeByte();
        final int slot = iPacket.DecodeShort();
        final int csstar = iPacket.DecodeShort();
        int visProjectile = 0;
        if ((chr.getJob() >= 3500 && chr.getJob() <= 3512) || GameConstants.isJett(chr.getJob())) {
            visProjectile = 2333000;
        } else if (GameConstants.isCannoneer(chr.getJob())) {
            visProjectile = 2333001;
        } else if (!GameConstants.isMercedes(chr.getJob()) && chr.getBuffedValue(CharacterTemporaryStat.SoulArrow) == null && slot > 0) {
            Item ipp = chr.getInventory(MapleInventoryType.USE).getItem((short) slot);
            if (ipp == null) {
                return;
            }
            if (csstar > 0) {
                ipp = chr.getInventory(MapleInventoryType.CASH).getItem((short) csstar);
                if (ipp == null) {
                    return;
                }
            }
            visProjectile = ipp.getItemId();
        }
        maxdamage *= skillDamage / 100.0;
        maxdamage *= chr.getStat().dam_r / 100.0;
        final List<AttackMonster> ourAttacks = new ArrayList<>(mobCount);
        final boolean area = inArea(chr);
        boolean didAttack = false, killed = false;
        if (!area) {
            List<Pair<Long, Boolean>> attacks;
            for (User attacked : chr.getMap().getCharactersIntersect(box)) {
                if (attacked.getId() != chr.getId() && attacked.isAlive() && !attacked.isHidden() && (type == 0 || attacked.getTeam() != chr.getTeam())) {
                    double rawDamage = maxdamage / Math.max(1, ((magic ? attacked.getStat().mdef : attacked.getStat().wdef) * Math.max(1.0, 100.0 - ignoreDEF) / 100.0) * (type == 3 ? 0.2 : 0.5));
                    if (attacked.getBuffedValue(CharacterTemporaryStat.Invincible) != null || inArea(attacked)) {
                        rawDamage = 0;
                    }
                    rawDamage *= attacked.getStat().mesoGuard / 100.0;
                    rawDamage += (rawDamage * chr.getDamageIncrease(attacked.getId()) / 100.0);
                    rawDamage = attacked.modifyDamageTaken(rawDamage, attacked).left;
                    final double min = (rawDamage * chr.getStat().trueMastery / 100.0);
                    attacks = new ArrayList<>(attackCount);
                    int totalMPLoss = 0, totalHPLoss = 0;
                    ThreadLock.lock();
                    try {
                        for (int i = 0; i < attackCount; i++) {
                            boolean critical_ = false;
                            int mploss = 0;
                            double ourDamage = Randomizer.nextInt((int) Math.abs(Math.round(rawDamage - min)) + 2) + min;
                            if (attacked.getStat().avoidabilityRate > 0 && Randomizer.nextInt(100) < attacked.getStat().avoidabilityRate) {
                                ourDamage = 0;
                            } else if (attacked.hasDisease(MapleDisease.DARKNESS) && Randomizer.nextInt(100) < 50) {
                                ourDamage = 0;
                                //i dont think level actually matters or it'd be too op
                                //} else if (attacked.getLevel() > chr.getLevel() && Randomizer.nextInt(100) < (attacked.getLevel() - chr.getLevel())) {
                                //	ourDamage = 0;
                            } else if (attacked.getJob() == 122 && attacked.getTotalSkillLevel(1220006) > 0 && attacked.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10) != null) {
                                final MapleStatEffect eff = SkillFactory.getSkill(1220006).getEffect(attacked.getTotalSkillLevel(1220006));
                                if (eff.makeChanceResult()) {
                                    ourDamage = 0;
                                }
                            } else if (attacked.getJob() == 412 && attacked.getTotalSkillLevel(4120002) > 0) {
                                final MapleStatEffect eff = SkillFactory.getSkill(4120002).getEffect(attacked.getTotalSkillLevel(4120002));
                                if (eff.makeChanceResult()) {
                                    ourDamage = 0;
                                }
                            } else if (attacked.getJob() == 422 && attacked.getTotalSkillLevel(4220006) > 0) {
                                final MapleStatEffect eff = SkillFactory.getSkill(4220002).getEffect(attacked.getTotalSkillLevel(4220002));
                                if (eff.makeChanceResult()) {
                                    ourDamage = 0;
                                }
                            } else if (shad != null && i >= originalAttackCount) {
                                ourDamage *= shad.getX() / 100.0;
                            }
                            if (ourDamage > 0 && skillid != 4211006 && skillid != 3211003 && skillid != 4111004 && (skillid == 4221001 || skillid == 3221007 || skillid == 23121003 || skillid == 4341005 || skillid == 4331006 || skillid == 21120005 || Randomizer.nextInt(100) < critRate)) {
                                ourDamage *= (100.0 + (Randomizer.nextInt(Math.max(2, chr.getStat().passive_sharpeye_percent() - chr.getStat().passive_sharpeye_min_percent())) + chr.getStat().passive_sharpeye_min_percent())) / 100.0;
                                critical_ = true;
                            }
                            if (attacked.getBuffedValue(CharacterTemporaryStat.MagicGuard) != null) {
                                mploss = (int) Math.min(attacked.getStat().getMp(), (ourDamage * attacked.getBuffedValue(CharacterTemporaryStat.MagicGuard).doubleValue() / 100.0));
                            }
                            ourDamage -= mploss;
                            if (attacked.getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
                                mploss = 0;
                            }
                            attacks.add(new Pair<>((long) Math.floor(ourDamage), critical_));

                            totalHPLoss += Math.floor(ourDamage);
                            totalMPLoss += mploss;
                        }
                    } finally {
                        ThreadLock.unlock();
                    }
                    if (GameConstants.isDemonSlayer(chr.getJob())) {
                        chr.handleForceGain(attacked.getObjectId(), skillid);
                    }
                    addedScore += Math.min(attacked.getStat().getHp() / 100, (totalHPLoss / 100) + (totalMPLoss / 100)); //ive NO idea
                    attacked.addMPHP(-totalHPLoss, -totalMPLoss);
                    ourAttacks.add(new AttackMonster(null, attacked.getId(), attacked.getPosition(), attacks));
                    chr.onAttack(attacked.getStat().getCurrentMaxHp(), attacked.getStat().getCurrentMaxMp(attacked.getJob()), skillid, attacked.getObjectId(), totalHPLoss, 0);
                    attacked.getCheatTracker().setAttacksWithoutHit(false);
                    if (totalHPLoss > 0) {
                        didAttack = true;
                    }
                    if (attacked.getStat().getHPPercent() <= 20) {
                        SkillFactory.getSkill(PlayerStats.getSkillByJob(93, attacked.getJob())).getEffect(1).applyTo(attacked);
                    }
                    if (effect != null) {
                        if (effect.getMonsterStati().size() > 0 && effect.makeChanceResult()) {
                            ThreadLock.lock();
                            try {
                                for (Map.Entry<MonsterStatus, Integer> z : effect.getMonsterStati().entrySet()) {
                                    MapleDisease d = MonsterStatus.getLinkedDisease(z.getKey());
                                    if (d != null) {
                                        MobSkill skill = new MobSkill(d.getDisease(), 1);
                                        skill.setX(z.getValue());
                                        skill.setDuration(effect.getDuration());
                                        attacked.giveDebuff(d, skill);
                                    }
                                }
                            } finally {
                                ThreadLock.unlock();
                            }
                        }
                        effect.handleExtraPVP(chr, attacked);
                    }
                    if (chr.getJob() == 121 || chr.getJob() == 122 || chr.getJob() == 2110 || chr.getJob() == 2111 || chr.getJob() == 2112) { // WHITEKNIGHT
                        if (chr.getBuffSource(CharacterTemporaryStat.WeaponCharge) == 1211006 || chr.getBuffSource(CharacterTemporaryStat.WeaponCharge) == 21101006) {
                            final MapleStatEffect eff = chr.getStatForBuff(CharacterTemporaryStat.WeaponCharge);
                            if (eff.makeChanceResult()) {
                                MobSkill skill = new MobSkill(MapleDisease.FREEZE.getDisease(), 1);
                                skill.setX(1);
                                skill.setDuration(eff.getDuration());
                                attacked.giveDebuff(MapleDisease.FREEZE, skill);
                            }
                        }
                    } else if (chr.getBuffedValue(CharacterTemporaryStat.Slow) != null) {
                        final MapleStatEffect eff = chr.getStatForBuff(CharacterTemporaryStat.Slow);
                        if (eff != null && eff.makeChanceResult()) {
                            MobSkill skill = new MobSkill(MapleDisease.SLOW.getDisease(), 1);
                            skill.setX(100 - Math.abs(eff.getX()));
                            skill.setDuration(eff.getDuration());
                            attacked.giveDebuff(MapleDisease.SLOW, skill);
                        }
                    } else if (chr.getJob() == 412 || chr.getJob() == 422 || chr.getJob() == 434 || chr.getJob() == 1411 || chr.getJob() == 1412) {
                        int[] skills = {4120005, 4220005, 4340001, 14110004};
                        ThreadLock.lock();
                        try {
                            for (int i : skills) {
                                final Skill skill = SkillFactory.getSkill(i);
                                if (chr.getTotalSkillLevel(skill) > 0) {
                                    final MapleStatEffect venomEffect = skill.getEffect(chr.getTotalSkillLevel(skill));
                                    if (venomEffect.makeChanceResult()) {// THIS MIGHT ACTUALLY BE THE DOT
                                        MobSkill skil = new MobSkill(MapleDisease.POISON.getDisease(), 1);
                                        skil.setX(venomEffect.getDuration());
                                        skil.setDuration(venomEffect.getDuration());
                                        attacked.giveDebuff(MapleDisease.POISON, skil);
                                    }
                                    break;
                                }
                            }
                        } finally {
                            ThreadLock.unlock();
                        }
                    }
                    if ((chr.getJob() / 100) % 10 == 2) {//mage
                        int[] skills = {2000007, 12000006, 22000002, 32000012};
                        ThreadLock.lock();
                        try {
                            for (int i : skills) {
                                final Skill skill = SkillFactory.getSkill(i);
                                if (chr.getTotalSkillLevel(skill) > 0) {
                                    final MapleStatEffect venomEffect = skill.getEffect(chr.getTotalSkillLevel(skill));
                                    if (venomEffect.makeChanceResult()) {
                                        venomEffect.applyTo(attacked);
                                    }
                                    break;
                                }
                            }
                        } finally {
                            ThreadLock.unlock();
                        }
                    }
                    if (ice == attacked.getId()) {
                        //chr.getClient().write(CField.getPVPIceHPBar(attacked.getStat().getHp(), attacked.getStat().getCurrentMaxHp()));
                    } else {
                        chr.getClient().SendPacket(CField.getPVPHPBar(attacked.getId(), attacked.getStat().getHp(), attacked.getStat().getCurrentMaxHp()));
                    }

                    if (!attacked.isAlive()) {
                        addedScore += 5; //i guess
                        killed = true;
                    }
                    if (ourAttacks.size() >= mobCount) {
                        break;
                    }
                }
            }
        } else if (type == 3) {
            Map_MCarnival carnival = chr.getMap().getSharedMapResources().mcarnival;

            if (carnival == null) {
                return;
            }

            if (Integer.parseInt(chr.getEventInstance().getProperty("redflag")) == chr.getId()
                    && chr.getMap().getSharedMapResources().areas.get(1).contains(chr.getTruePosition())) {
                chr.getEventInstance().setProperty("redflag", "0");
                chr.getEventInstance().setProperty("blue", String.valueOf(Integer.parseInt(chr.getEventInstance().getProperty("blue")) + 1));
                chr.getEventInstance().broadcastPlayerMsg(-7, "Blue Team has scored a point!");
                chr.getMap().spawnAutoDrop(2910000, carnival.Guardian.get(0).pos);
                //chr.getEventInstance().broadcastPacket(CField.getCapturePosition(chr.getMap()));
                //chr.getEventInstance().broadcastPacket(CField.resetCapture());
                chr.getEventInstance().schedule("updateScoreboard", 1000);
            } else if (Integer.parseInt(chr.getEventInstance().getProperty("blueflag")) == chr.getId()
                    && chr.getMap().getSharedMapResources().areas.get(0).contains(chr.getTruePosition())) {
                chr.getEventInstance().setProperty("blueflag", "0");
                chr.getEventInstance().setProperty("red", String.valueOf(Integer.parseInt(chr.getEventInstance().getProperty("red")) + 1));
                chr.getEventInstance().broadcastPlayerMsg(-7, "Red Team has scored a point!");
                chr.getMap().spawnAutoDrop(2910001, carnival.Guardian.get(1).pos);
                //chr.getEventInstance().broadcastPacket(CField.getCapturePosition(chr.getMap()));
                //chr.getEventInstance().broadcastPacket(CField.resetCapture());
                chr.getEventInstance().schedule("updateScoreboard", 1000);
            }
        }
        if (chr.getEventInstance() == null) { //if the PVP ends 
            c.SendPacket(CWvsContext.enableActions());
            return;
        }

        if (killed || addedScore > 0) {
            chr.getEventInstance().addPVPScore(chr, addedScore);
            //chr.getClient().write(CField.getPVPScore(ourScore + addedScore, killed));
        }
        if (didAttack) {
            chr.afterAttack(ourAttacks.size(), attackCount, skillid);
            PlayerHandler.aranCombo(c, chr, ourAttacks.size() * attackCount);
            if (skillid > 0 && (ourAttacks.size() > 0 || (skillid != 4331003 && skillid != 4341002)) && !GameConstants.isNoDelaySkill(skillid)) {
                boolean applyTo = effect.applyTo(chr, chr.getTruePosition());
            } else {
                c.SendPacket(CWvsContext.enableActions());
            }
        } else {
            move = false;
            pull = false;
            push = false;
            c.SendPacket(CWvsContext.enableActions());
        }
        chr.getMap().broadcastMessage(CField.pvpAttack(chr.getId(), chr.getLevel(), trueSkill, trueSkillLevel, speed, fakeMastery, visProjectile, attackCount, chargeTime, animation, facingLeft ? 1 : 0, chr.getStat().defRange, skillid, skillLevel, move, push, pull, ourAttacks));
        if (addedScore > 0 && GameConstants.getAttackDelay(skillid, SkillFactory.getSkill(skillid)) >= 100) {
            final CheatTracker tracker = chr.getCheatTracker();

            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() > 1000) {
                tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
    }

}
