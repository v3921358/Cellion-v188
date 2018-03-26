package server.life;

import client.*;
import client.MapleTrait.MapleTraitType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.EventConstants;
import constants.GameConstants;
import constants.ServerConstants;
import handling.jobs.Explorer;
import handling.jobs.Explorer.ShadowerHandler;
import handling.jobs.Sengoku;
import handling.jobs.Sengoku.HayatoHandler;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import net.Packet;
import scripting.EventInstanceManager;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.Timer.EtcTimer;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.MapleCharacter;
import server.messages.ExpGainTypes;
import service.ChannelServer;
import tools.ConcurrentEnumMap;
import tools.packet.CField;
import tools.packet.MobPacket;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapleMonster extends AbstractLoadedMapleLife {

    private MapleMonsterStats stats;
    private ForcedMonsterStats forcedStats = null;
    private long hp, mp, nextKill = 0, lastDropTime = 0;
    private byte carnivalTeam = -1;
    private MapleMap map;
    private WeakReference<MapleMonster> sponge = new WeakReference<>(null);
    private int linkoid = 0, lastNode = -1, highestDamageChar = 0, linkCID = 0; // Just a reference for monster EXP distribution after dead
    private int controller = -1;
    private boolean fake = false, dropsDisabled = false, controllerHasAggro = false, statChanged;
    private final Collection<AttackerEntry> attackers = new LinkedList<>();
    private EventInstanceManager eventInstance;
    private MonsterListener listener = null;
    private Packet reflectpack = null, nodepack = null;
    private final ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect> stati = new ConcurrentEnumMap<>(MonsterStatus.class);
    private final LinkedList<MonsterStatusEffect> poisons = new LinkedList<>();
    private final ReentrantReadWriteLock poisonsLock = new ReentrantReadWriteLock();
    private Map<Integer, Long> usedSkills;
    private int stolen = -1;
    private boolean shouldDropItem = false;
    private int triangulation = 0;
    private ChangeableStats ostats = null;
    private int bloodlessPartyBonus = 0;

    public MapleMonster(int id, MapleMonsterStats stats) {
        super(id);
        initWithStats(stats);
    }

    public MapleMonster(MapleMonster monster) {
        super(monster);
        initWithStats(monster.stats);
    }

    private void initWithStats(MapleMonsterStats stats) {
        setStance(5);
        this.stats = stats;
        hp = stats.getHp();
        mp = stats.getMp();

        if (!stats.getSkills().isEmpty()) {
            usedSkills = new HashMap<>();
        }
    }

    public final void buffedChangeLevel(final double newLevel, final int hpBuff, final int bossHpBuff) { //Buffed channels
        this.ostats = new ChangeableStats(stats, newLevel, hpBuff, bossHpBuff);
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }

    public ArrayList<AttackerEntry> getAttackers() {
        if (attackers == null || attackers.size() <= 0) {
            return new ArrayList<>();
        }
        ArrayList<AttackerEntry> ret = new ArrayList<>();
        attackers.stream().filter((e) -> (e != null)).forEach((e) -> {
            ret.add(e);
        });
        return ret;
    }

    public MapleMonsterStats getStats() {
        return stats;
    }

    public void disableDrops() {
        this.dropsDisabled = true;
    }

    public boolean dropsDisabled() {
        return dropsDisabled;
    }

    public void setSponge(MapleMonster mob) {
        sponge = new WeakReference<>(mob);
        if (linkoid <= 0) {
            linkoid = mob.getObjectId();
        }
    }

    public void setMap(MapleMap map) {
        this.map = map;
        startDropItemSchedule();
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public ForcedMonsterStats getChangedStats() {
        return forcedStats;
    }

    public long getMobMaxHp() {
        return stats.getHp();
    }

    public long getMp() {
        return mp;
    }

    public void setMp(long mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public long getMobMaxMp() {
        return stats.getMp();
    }

    public int getMobExp() {
        return stats.getExp();
    }

    public void changeLevel(int newLevel) {
        changeLevel(newLevel, true);
    }

    public void changeLevel(int newLevel, boolean pqMob) {
        if (!stats.isChangeable()) {
            return;
        }
        this.forcedStats = new ForcedMonsterStats(stats, newLevel, pqMob);
    }

    public void changeMob(int newLevel, boolean pqMob) {
        if (!stats.isChangeable()) {
            return;
        }
        this.forcedStats = new ForcedMonsterStats(stats, newLevel, pqMob);
        this.hp = forcedStats.getHp() * 100000;
        this.mp = forcedStats.getMp();
        this.forcedStats.setExp(forcedStats.getExp() * newLevel * 100);
    }

    public MapleMonster getSponge() {
        return sponge.get();
    }

    public void damage(MapleCharacter from, long damage, boolean updateAttackTime) {
        damage(from, damage, updateAttackTime, 0);
    }

    public void damage(MapleCharacter from, long damage, boolean updateAttackTime, int lastSkill) {
        if (from == null || damage <= 0 || !isAlive()) {
            return;
        }

        // Setup to instant kill bosses while testing/debugging.
        if (from.isDeveloper()) {
            switch (this.getId()) { 
                case 8880000: // Magnus
                case 8240099: // Lotus
                case 8930000: // Chaos Vellum
                    from.dropMessage(-1, "Developer Instant Kill");
                    damage = 2100000000;
                    break;
            }
        }
        
        // Lotus Buffed Damage Reduction
        if (this.getId() == 8240099) {
            damage /= 10;
        }
        
        //Buffed Bosses & Monsters
        boolean buffedMob = this.getId() == 8880000 //Magnus
                || this.getId() == 8880001;
        if (buffedMob && ServerConstants.BUFFED_BOSSES) {
            damage /= 5;
        }

        //Buffed Channel Monster Damage Reduction
        if (ServerConstants.BUFFED_SYSTEM && (from.getClient().getChannel() >= ServerConstants.START_RANGE) && (from.getClient().getChannel() <= ServerConstants.END_RANGE)) {
            if (from.getLevel() <= 30) {
                damage *= ((ServerConstants.DAMAGE_DEALT_PERCENTAGE * 1.5) / 100.0f);
            } else if (from.getLevel() <= 100) {
                damage *= ((ServerConstants.DAMAGE_DEALT_PERCENTAGE) / 100.0f);
            } else {
                damage *= ((ServerConstants.DAMAGE_DEALT_PERCENTAGE / 1.5) / 100.0f);
            }
        }

        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.err.println("[Damage Operation] Mob Damage Received (" + damage + ")");
        }

        AttackerEntry attacker;
        /*if (this.getId() == 9400551 && from.getMapId() == 109010104) {
            //this.getStats().setHp(damage + 1);
            OverrideMonsterStats newstat = new OverrideMonsterStats(damage + 1, this.getMp(), this.getMobExp());
            this.setOverrideStats(newstat);
        } */
        if (from.getParty() != null) {
            attacker = new PartyAttackerEntry(from.getParty().getId());
        } else {
            attacker = new SingleAttackerEntry(from);
        }
        boolean replaced = false;
        for (AttackerEntry aentry : getAttackers()) {
            if (aentry != null && aentry.equals(attacker)) {
                attacker = aentry;
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            attackers.add(attacker);
        }
        long rDamage = Math.max(0, Math.min(damage, hp));
        attacker.addDamage(from, rDamage, updateAttackTime);

        if (stats.getSelfD() != -1) {
            hp -= rDamage;
            if (hp > 0) {
                if (hp < stats.getSelfDHp()) { // HP is below the selfd level
                    map.killMonster(this, from, false, false, stats.getSelfD(), lastSkill);
                } else { // Show HP
                    for (AttackerEntry mattacker : getAttackers()) {
                        for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                            MapleCharacter chr = map.getCharacterById(cattacker.getAttacker());
                            if (chr == null) {
                                continue; // ref is gone. logged off.
                            }
                            if (chr.getMap() == from.getMap()) { // compare map object, in case the attacker is in an instanced map with the same mapid
                                if (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000) {
                                    chr.getClient().write(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                                }
                            }
                        }
                    }
                }
            } else { // Character killed it without exploding :(

                // Spawn revives - This must be called before broadcasting killMonster, otherwise the position will be incorrectly spawned
                spawnRevives(getMap());

                map.killMonster(this, from, true, false, (byte) 1, lastSkill);
            }
        } else {
            if (sponge.get() != null) {
                if (sponge.get().hp > 0) { // If it's still alive, dont want double/triple rewards
                    // Sponge are always in the same map, so we can use this.map
                    // The only mob that uses sponge are PB/HT
                    sponge.get().hp -= rDamage;

                    if (sponge.get().hp <= 0) {
                        map.broadcastMessage(MobPacket.showBossHP(sponge.get().getId(), -1, sponge.get().getMobMaxHp()));
                        map.killMonster(sponge.get(), from, true, false, (byte) 1, lastSkill);
                    } else {
                        map.broadcastMessage(MobPacket.showBossHP(sponge.get()));
                    }
                }
            }
            if (hp > 0) {
                hp -= rDamage;

                if (eventInstance != null) {
                    eventInstance.monsterDamaged(from, this, (int) rDamage);
                } else {
                    EventInstanceManager em = from.getEventInstance();
                    if (em != null) {
                        em.monsterDamaged(from, this, (int) rDamage);
                    }
                }
                if (sponge.get() == null && hp > 0) {
                    switch (stats.getHPDisplayType()) {
                        case BossHP:
                            map.broadcastMessage(MobPacket.showBossHP(this), this.getTruePosition());
                            break;
                        case FriendlyMonsterHP:
                            map.broadcastMessage(from, MobPacket.damageFriendlyMob(this, damage, true), false);
                            break;
                        case MulungDojoMonsterHP:
                            map.broadcastMessage(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                            from.mulungEnergyModifier(true);
                            break;
                        case RegularMonsterHP:
                            for (AttackerEntry mattacker : getAttackers()) {
                                if (mattacker != null) {
                                    for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                                        MapleCharacter chr = map.getCharacterById(cattacker.getAttacker());
                                        if (chr == null) {
                                            continue;
                                        }

                                        if (chr.getMap() == from.getMap()) { // compare map object, in case the attacker is in an instanced map with the same mapid
                                            if (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000) {
                                                chr.getClient().write(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case BossHPWithRegularHPBar:
                            map.broadcastMessage(MobPacket.showBossHP(this), this.getPosition());
                            map.broadcastMessage(MobPacket.showMonsterHP(getObjectId(), (int) Math.ceil((hp * 100.0) / getMobMaxHp())));
                            break;
                    }
                }

                if (hp <= 0) {
                    if (stats.getHPDisplayType() == MapleMonsterHpDisplayType.BossHP) {
                        map.broadcastMessage(MobPacket.showBossHP(getId(), -1, getMobMaxHp()), this.getTruePosition());
                    }
                    // Spawn revives - This must be called before broadcasting killMonster, otherwise the position will be incorrectly spawned
                    spawnRevives(getMap());

                    map.killMonster(this, from, true, false, (byte) 1, lastSkill);

                }
            }
        }
        startDropItemSchedule();
    }

    public int getHPPercent() {
        return (int) Math.ceil((hp * 100.0) / getMobMaxHp());
    }

    public void heal(int hp, long mp, boolean broadcast) {
        long TotalHP = getHp() + hp;
        long TotalMP = getMp() + mp;

        if (TotalHP >= getMobMaxHp()) {
            setHp(getMobMaxHp());
        } else {
            setHp(TotalHP);
        }
        if (TotalMP >= getMp()) {
            setMp(getMp());
        } else {
            setMp(TotalMP);
        }
        if (broadcast) {
            map.broadcastMessage(MobPacket.healMonster(getObjectId(), hp));
        } else if (sponge.get() != null) { // else if, since only sponge doesn't broadcast
            sponge.get().hp += hp;
        }
    }

    public void killed() {
        if (listener != null) {
            listener.monsterKilled();
        }
        listener = null;
    }

    /*
     *  Monster NX Drop System
     *  @author Mazen Massoud
     *
     *  @purpose Provide the player will NX upon killing a monster, 
     *  with the value varrying based on multiple factors.
     */
    public void monsterNxGainResult(MapleCharacter pPlayer, boolean bKiller) {
        
        long nMobHp = getMobMaxHp();
        short nMobLv = stats.getLevel();

        if (getMobMaxHp() > 175000000) { // Caps the the HP at this value for the calculation.
            nMobHp = 175000000;
        }

        int nMinRange = (int) (nMobHp * 0.00000105) + (nMobLv / 3) + 1; // NX Gain Formula
        int nMaxRange = (int) Math.round(nMinRange * 1.25); // Amount NX Gain can go up to.
        int nResultNx = (int) (nMinRange + (Math.random() * ((nMaxRange - nMinRange) + 1))); // Formula to produce a value between the specified range.

        int nGainChance = 60; // Base NX Drop Chance %

        // Paragon Level Bonus
        if (ServerConstants.PARAGON_SYSTEM) {
            if (pPlayer.getReborns() >= 5) { // Paragon Level 5+
                nGainChance += 20;
                nResultNx *= 1.20; // +10% Increased NX Gain
            }
        }
        
        // (Buffed) Bloodless Channel Bonus
        if ((ServerConstants.BUFFED_SYSTEM) && (ServerConstants.BUFFED_NX_GAIN) && (pPlayer.getClient().getChannel() >= ServerConstants.START_RANGE) && (pPlayer.getClient().getChannel() <= ServerConstants.END_RANGE)) {
            nGainChance += 20;
            nResultNx *= 2;
        }
        
        if (!bKiller) { // Leechers Gain
            nResultNx *= 0.4; // Cap at 40%
        }
        
        if (Randomizer.nextInt(100) < nGainChance) {
            pPlayer.modifyCSPoints(2, nResultNx, true);
        }
    }
    
    /**
     * Gives experience to a player after a monster has been killed. This also handles the additional EXP acquired through other variables
     * such as item buff, map, party bonus, etc
     *
     * @param attacker
     * @param lastskillID
     * @param baseEXP
     * @param partybonusMultiplier -- The party bonus in percentage to give to this character for being in a party
     * @param highestDamage
     * @param numExpSharers
     * @param PartyBonusPercentage
     * @param Class_Bonus_EXP_PERCENT
     * @param Premium_Bonus_EXP_PERCENT
     * @param burningFieldBonusEXPRate - The burning field bonus EXP in percentage (eg: 20%, 30%) [This parameter is pushed to avoid
     * multiple calls to MapleMap.getBurningFieldBonus() per character, since its all the same]
     * @param isKiller - Determines if this character receiving the EXP is the one who had the last hit.
     */
    private void giveExpToCharacter(MapleCharacter attacker, boolean isKiller, long baseEXP,
            int partybonusMultiplier, boolean highestDamage, int numExpSharers, byte partySize, int fieldPartyBonusPercentage,
            byte Class_Bonus_EXP_PERCENT, byte Premium_Bonus_EXP_PERCENT, int burningFieldBonusEXPRate,
            int lastskillID) {

        if (ServerConstants.MONSTER_CASH_DROP) {
            monsterNxGainResult(attacker, isKiller);
        }
        
        if (highestDamage) {
            if (eventInstance != null) {
                eventInstance.monsterKilled(attacker, this);
            } else {
                EventInstanceManager em = attacker.getEventInstance();
                if (em != null) {
                    em.monsterKilled(attacker, this);
                }
            }
            highestDamageChar = attacker.getId();
        }

        if (baseEXP > 0) {
            EnumMap<ExpGainTypes, Integer> expIncreaseStats = new EnumMap<>(ExpGainTypes.class);

            // Server EXP Rate
            double expRate_Server = ChannelServer.getInstance(map.getChannel()).getExpRate(attacker.getWorld());

            if (expRate_Server > 1) {
                baseEXP *= expRate_Server;
            }

            long totalEXPGained = baseEXP;
            long bonusEXPGained = 0; //bonus EXP do not get shown to the user via the main EXP gained.

            ///
            ///  Accounting for additional EXP gain here. These orders are according to ExpGainTypes, as acquired from the client.
            ///
            // Exp Coupons
            if (attacker.haveItem(GameConstants._200PercentExpBoost)) {
                totalEXPGained *= 2;
            } else if (attacker.haveItem(GameConstants._150PercentExpBoost)) {
                totalEXPGained *= 1.5;
            }

            // 2x coupons rate
            if (attacker.getStat().expMod > 1.0f) {
                totalEXPGained *= attacker.getStat().expMod;
            }
            // Double time rate
            if (EventConstants.DoubleTime) {
                totalEXPGained *= 2.0;
            }
            // Showdown skill
            MonsterStatusEffect ms = stati.get(MonsterStatus.SHOWDOWN);
            if (ms != null) {
                totalEXPGained += (int) (baseEXP * (ms.getX() / 100.0));

            }

            // Buffed Channel Bonus
            if (ServerConstants.BUFFED_SYSTEM && (attacker.getClient().getChannel() >= ServerConstants.START_RANGE) && (attacker.getClient().getChannel() <= ServerConstants.END_RANGE)) {
                int bonusAdditionalEXP = (int) (baseEXP * (ServerConstants.BUFFED_EXP_PERCENTAGE / 100.0f));
                bonusEXPGained += bonusAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.BaseAddExp, bonusAdditionalEXP);
            }

            // Bloodless Event - Party Exp Bonus
            if (ServerConstants.BLOODLESS_EVENT && ServerConstants.BLOODLESS_PARTY_BONUS && ServerConstants.BUFFED_SYSTEM && (attacker.getClient().getChannel() >= ServerConstants.START_RANGE) && (attacker.getClient().getChannel() <= ServerConstants.END_RANGE)) {
                if (bloodlessPartyBonus > 0) {
                    int bloodlessPartyAdditionalEXP = (int) (baseEXP * (bloodlessPartyBonus / 100.0f));
                    bonusEXPGained += bloodlessPartyAdditionalEXP;

                    expIncreaseStats.put(ExpGainTypes.PartyBonus, bloodlessPartyAdditionalEXP);
                }
            }

            // Party bonus
            if (partybonusMultiplier > 0) {
                int partyBonusAdditionalEXP = (int) (baseEXP * (partybonusMultiplier / 100f));
                bonusEXPGained += partyBonusAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.PartyBonus, partyBonusAdditionalEXP);
                expIncreaseStats.put(ExpGainTypes.PartyBonusPercentage, partybonusMultiplier);
            }

            // Holy symbol
            Integer holySymbol = attacker.getBuffedValue(CharacterTemporaryStat.HolySymbol);
            if (holySymbol != null) {
                double partybonusPercentage = (holySymbol.doubleValue() / 100.0f);
                int holySymbolAdditionalEXP = (int) (baseEXP * partybonusPercentage);
                bonusEXPGained += holySymbolAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.BaseAddExp, holySymbolAdditionalEXP);
            }

            // Equipment bonus EXP [fairy]
            if (attacker.getStat().equippedFairy > 0 && attacker.getFairyExp() > 0) {          //  Equipment_Bonus_EXP = (int) ((exp / 100.0) * attacker.getStat().equipmentBonusExp);
                int fairyBonusEXP = (int) ((baseEXP / 100.0) * attacker.getFairyExp());
                bonusEXPGained += fairyBonusEXP;

                expIncreaseStats.put(ExpGainTypes.ItemBonus, fairyBonusEXP);
            }

            // Exp buff bonus
            if (attacker.getStat().expBuff > 100) {
                int bonusAdditionalEXP = (int) (baseEXP * (attacker.getStat().expBuff / 100.0f));
                bonusEXPGained += bonusAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.ExpBuffBonus, bonusAdditionalEXP);
            }

            // Class bonus EXP
            if (Class_Bonus_EXP_PERCENT > 0) {
                int psdBonusExp = (int) ((baseEXP / 100.0) * Class_Bonus_EXP_PERCENT);
                bonusEXPGained += psdBonusExp;

                expIncreaseStats.put(ExpGainTypes.PsdBonus, psdBonusExp);
            }

            if (attacker.getStat().expMod_ElveBlessing >= 1.0) {
                int psdBonusExp = (int) (baseEXP * (attacker.getStat().expMod_ElveBlessing - 1.0f));
                bonusEXPGained += psdBonusExp;

                expIncreaseStats.put(ExpGainTypes.PsdBonus, psdBonusExp);
            }

            // premium bonus
            if (Premium_Bonus_EXP_PERCENT > 0) {
                int premiumBonusExp = (int) ((baseEXP / 100.0) * Premium_Bonus_EXP_PERCENT);
                bonusEXPGained += premiumBonusExp;

                expIncreaseStats.put(ExpGainTypes.PremiumIpBonus, premiumBonusExp);
            }

            // Indie EXP rate [rune]
            if (attacker.getStat().indieExpBuff > 100) {
                int indieExpAdditionalEXP = (int) (baseEXP * ((attacker.getStat().indieExpBuff - 100) / 100.0f));
                bonusEXPGained += indieExpAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.IndieBonus, indieExpAdditionalEXP);
            }

            // Burning field bonus EXP
            if (burningFieldBonusEXPRate > 0) {
                int burningFieldBonusEXP = (int) (baseEXP * (burningFieldBonusEXPRate / 100f));
                bonusEXPGained += burningFieldBonusEXP;

                expIncreaseStats.put(ExpGainTypes.RestFieldBonus, burningFieldBonusEXP);
            }

            // Map bonus EXP
            if (fieldPartyBonusPercentage >= 0) {
                int fieldPartyMapBonusEXP = (int) (baseEXP * (fieldPartyBonusPercentage / 100f));
                bonusEXPGained += fieldPartyMapBonusEXP;

                expIncreaseStats.put(ExpGainTypes.FieldBonus, fieldPartyMapBonusEXP);
            }

            //////////////// Cursed, put this last.
            if (attacker.hasDisease(MapleDisease.CURSE)) {
                totalEXPGained /= 2;
                bonusEXPGained /= 2;
            }
            attacker.gainExp(totalEXPGained, bonusEXPGained, true, highestDamage, false, burningFieldBonusEXPRate, expIncreaseStats);

            // Others - Trait
            attacker.getTrait(MapleTraitType.charisma).addExp(stats.getCharismaEXP(), attacker);

        }

        // For quest kills
        attacker.mobKilled(stats.getName(), getId(), lastskillID);
    }

    public int killBy(MapleCharacter killer, int lastSkill) {
        int totalBaseExp = getMobExp();
        AttackerEntry highest = null;
        long highdamage = 0;
        List<AttackerEntry> list = getAttackers();
        for (AttackerEntry attackEntry : list) {
            if (attackEntry != null && attackEntry.getDamage() > highdamage) {
                highest = attackEntry;
                highdamage = attackEntry.getDamage();
            }
        }

        // Give EXP
        int baseExp;
        for (AttackerEntry attackEntry : list) {
            if (attackEntry != null) {
                baseExp = (int) Math.ceil(totalBaseExp * ((double) attackEntry.getDamage() / getMobMaxHp()));
                attackEntry.killedMob(getMap(), baseExp, attackEntry == highest, lastSkill, killer.getId());
            }
        }

        MapleCharacter controll = getController();
        if (controll != null) { // this can/should only happen when a hidden gm attacks the monster
            if (GameConstants.isAzwanMap(killer.getMapId())) {
                controll.getClient().write(MobPacket.stopControllingMonster(this, true));
            } else {
                controll.getClient().write(MobPacket.stopControllingMonster(this, false));
            }
            controll.stopControllingMonster(this);
        }
        int achievement = 0;

        switch (getId()) {
            case 9400121:
                achievement = 12;
                break;
            case 8500002:
                achievement = 13;
                break;
            case 8510000:
            case 8520000:
                achievement = 14;
                break;
            default:
                break;
        }

        /*if (achievement != 0) {
            if (killer != null && killer.getParty() != null) {
                for (MaplePartyCharacter mp : killer.getParty().getMembers()) {
                    MapleCharacter mpc = killer.getMap().getCharacterById(mp.getId());
                    if (mpc != null) {
                        //   mpc.finishAchievement(achievement);
                    }
                }
            } else if (killer != null) {
                // killer.finishAchievement(achievement);
            } // I can't fix any skills cuz i don't get recv :( lol gm
        }//you cant fix the skills ;o? not until someone sniffs forme  wanna do  it on my cpu?;o hmm its hard because maple lags on tv
        if (killer != null && stats.isBoss()) {
            ///killer.finishAchievement(18);
        }*/
        if (eventInstance != null) {
            eventInstance.unregisterMonster(this);
            eventInstance = null;
        }
        if (killer != null && killer.getPyramidSubway() != null) {
            killer.getPyramidSubway().onKill(killer);
        }
        hp = 0;
        MapleMonster oldSponge = getSponge();
        sponge = new WeakReference<>(null);
        if (oldSponge != null && oldSponge.isAlive()) {
            boolean set = true;
            for (MapleMapObject mon : map.getAllMapObjects(MapleMapObjectType.MONSTER)) {
                MapleMonster mons = (MapleMonster) mon;
                if (mons.isAlive() && mons.getObjectId() != oldSponge.getObjectId() && mons.getStats().getLevel() > 1 && mons.getObjectId() != this.getObjectId() && (mons.getSponge() == oldSponge || mons.getLinkOid() == oldSponge.getObjectId())) { //sponge was this, please update
                    set = false;
                    break;
                }
            }
            if (set) { //all sponge monsters are dead, please kill off the sponge
                map.killMonster(oldSponge, killer, true, false, (byte) 1);
            }
        }

        // Cleanup memory
        reflectpack = null;
        nodepack = null;
        if (stati.size() > 0) {
            List<MonsterStatus> statuses = new LinkedList<>(stati.keySet());
            for (MonsterStatus ms : statuses) {
                cancelStatus(ms);
            }
            statuses.clear();
        }
        if (poisons.size() > 0) {
            List<MonsterStatusEffect> ps = new LinkedList<>();
            poisonsLock.readLock().lock();
            try {
                ps.addAll(poisons);
            } finally {
                poisonsLock.readLock().unlock();
            }
            for (MonsterStatusEffect p : ps) {
                cancelSingleStatus(p);
            }
            ps.clear();
        }
        //attackers.clear();
        cancelDropItem();

        int v1 = highestDamageChar;
        this.highestDamageChar = 0; //reset so we dont kill twice
        return v1;
    }

    public void spawnRevives(MapleMap map) {
        List<Integer> toSpawn = stats.getRevives();

        if (toSpawn == null || this.getLinkCID() > 0) {
            return;
        }
        MapleMonster spongy = null;
        switch (getId()) {
            case 8820002:
            case 8820003:
            case 8820004:
            case 8820005:
            case 8820006:
            case 8840000:
            case 6160003:
            case 8850011:
                break;
            case 8810118:
            case 8810119:
            case 8810120:
            case 8810121: //must update sponges
                for (int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);

                    mob.setPosition(getTruePosition());
                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810119:
                        case 8810120:
                        case 8810121:
                        case 8810122:
                            spongy = mob;
                            break;
                    }
                }
                if (spongy != null && map.getMonsterById(spongy.getId()) == null) {
                    map.spawnMonster(spongy, -2);
                    for (MapleMapObject mon : map.getAllMapObjects(MapleMapObjectType.MONSTER)) {
                        MapleMonster mons = (MapleMonster) mon;
                        if (mons.getObjectId() != spongy.getObjectId() && (mons.getSponge() == this || mons.getLinkOid() == this.getObjectId())) { //sponge was this, please update
                            mons.setSponge(spongy);
                        }
                    }
                }
                break;
            case 8810026:
            case 8810130:
            case 8820008:
            case 8820009:
            case 8820010:
            case 8820011:
            case 8820012:
            case 8820013: {
                List<MapleMonster> mobs = new ArrayList<>();

                for (int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);

                    mob.setPosition(getTruePosition());
                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810018: // Horntail Sponge
                        case 8810118:
                        case 8820009: // PinkBeanSponge0
                        case 8820010: // PinkBeanSponge1
                        case 8820011: // PinkBeanSponge2
                        case 8820012: // PinkBeanSponge3
                        case 8820013: // PinkBeanSponge4
                        case 8820014: // PinkBeanSponge5
                            spongy = mob;
                            break;
                        default:
                            mobs.add(mob);
                            break;
                    }
                }
                if (spongy != null && map.getMonsterById(spongy.getId()) == null) {
                    map.spawnMonster(spongy, -2);

                    for (MapleMonster i : mobs) {
                        map.spawnMonster(i, -2);
                        i.setSponge(spongy);
                    }
                }
                break;
            }
            case 8820014: {
                for (int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);

                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    mob.setPosition(getTruePosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.spawnMonster(mob, -2);
                }
                break;
            }
            default: {
                for (int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);

                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    mob.setPosition(getTruePosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.spawnRevives(mob, this.getObjectId());

                    if (mob.getId() == 9300216) {
                        map.broadcastMessage(CField.environmentChange("Dojang/clear", 5, 0));//was4
                        map.broadcastMessage(CField.environmentChange("dojang/end/clear", 12, 0));//was3
                    }
                }
                break;
            }
        }
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void setCarnivalTeam(byte team) {
        carnivalTeam = team;
    }

    public byte getCarnivalTeam() {
        return carnivalTeam;
    }

    public MapleCharacter getController() {
        if (controller != -1) {
            MapleCharacter chr = map.getCharacterById(controller);
            return chr;
        }
        return null;
    }

    public void setController(MapleCharacter chr) {
        if (chr != null) {
            this.controller = chr.getId();
        } else {
            this.controller = -1;
        }
    }

    public void switchController(MapleCharacter newController, boolean immediateAggro) {
        MapleCharacter controllers = getController();
        if (controllers == newController) {
            return;
        } else if (controllers != null) {
            controllers.stopControllingMonster(this);
            if (GameConstants.isAzwanMap(newController.getMapId())) {
                controllers.getClient().write(MobPacket.stopControllingMonster(this, true));
            } else {
                controllers.getClient().write(MobPacket.stopControllingMonster(this, false));
            }
            sendStatus(controllers.getClient());
        }
        newController.controlMonster(this, immediateAggro);
        setController(newController);
        if (immediateAggro) {
            setControllerHasAggro(true);
        }
    }

    public void addListener(MonsterListener listener) {
        this.listener = listener;
    }

    public boolean isControllerHasAggro() {
        return controllerHasAggro;
    }

    public void setControllerHasAggro(boolean controllerHasAggro) {
        this.controllerHasAggro = controllerHasAggro;
    }

    public void sendStatus(MapleClient client) {
        if (reflectpack != null) {
            client.write(reflectpack);
        }
        if (poisons.size() > 0) {
            poisonsLock.readLock().lock();
            try {
                client.write(MobPacket.applyMonsterStatus(this, poisons));
            } finally {
                poisonsLock.readLock().unlock();
            }
        }
    }

    public void setStatChanged(boolean d) {
        this.statChanged = d;
    }

    public boolean isStatChanged() {
        return statChanged;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (!isAlive()) {
            return;
        }
        if (GameConstants.isAzwanMap(client.getPlayer().getMapId())) {
            client.write(MobPacket.spawnMonster(this, fake && linkCID <= 0 ? -4 : -1, 0, true));
        } else {
            client.write(MobPacket.spawnMonster(this, fake && linkCID <= 0 ? -4 : -1, 0, false));
        }
        sendStatus(client);
        if (map != null && !stats.isEscort() && client.getPlayer() != null && client.getPlayer().getTruePosition().distanceSq(getTruePosition()) <= GameConstants.maxViewRangeSq_Half()) {
            map.updateMonsterController(this);
        }
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        if (stats.isEscort() && getEventInstance() != null && lastNode >= 0) { //shammos
            map.resetShammos(client);
        } else {
            if (GameConstants.isAzwanMap(client.getPlayer().getMapId())) {
                client.write(MobPacket.killMonster(getObjectId(), 0, true));
            } else {
                client.write(MobPacket.killMonster(getObjectId(), 0, false));
            }
            if (getController() != null && client.getPlayer() != null && client.getPlayer().getId() == getController().getId()) {
                client.getPlayer().stopControllingMonster(this);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(stats.getName());
        sb.append("(");
        sb.append(getId());
        sb.append(") (Level ");
        sb.append(stats.getLevel());
        sb.append(") at (X");
        sb.append(getTruePosition().x);
        sb.append("/ Y");
        sb.append(getTruePosition().y);
        sb.append(") with ");
        sb.append(getHp());
        sb.append("/ ");
        sb.append(getMobMaxHp());
        sb.append("hp, ");
        sb.append(getMp());
        sb.append("/ ");
        sb.append(getMobMaxMp());
        sb.append(" mp, oid: ");
        sb.append(getObjectId());
        sb.append(" || Controller : ");
        MapleCharacter chr = getController();
        sb.append(chr != null ? chr.getName() : "none");

        return sb.toString();
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.MONSTER;
    }

    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public int getStatusSourceID(MonsterStatus status) {
        if (status == MonsterStatus.POISON || status == MonsterStatus.VENOMOUS_WEAPON) {
            poisonsLock.readLock().lock();
            try {
                for (MonsterStatusEffect ps : poisons) {
                    if (ps != null) {
                        return ps.getSkill();
                    }
                }
                return -1;
            } finally {
                poisonsLock.readLock().unlock();
            }
        }
        MonsterStatusEffect effect = stati.get(status);
        if (effect != null) {
            return effect.getSkill();
        }
        return -1;
    }

    public ElementalEffectiveness getEffectiveness(Element e) {
        if (stati.size() > 0 && stati.containsKey(MonsterStatus.DOOM)) {
            return ElementalEffectiveness.NORMAL; // like blue snails
        }
        return stats.getEffectiveness(e);
    }

    public void applyStatus(MapleCharacter from, MonsterStatusEffect status, boolean poison, long duration, boolean checkboss, MapleStatEffect eff) {
        if (!isAlive() || getLinkCID() > 0) {
            return;
        }
        Skill skilz = SkillFactory.getSkill(status.getSkill());
        if (skilz != null) {
            switch (stats.getEffectiveness(skilz.getElement())) {
                case IMMUNE:
                case STRONG:
                    return;
                case NORMAL:
                case WEAK:
                    break;
                default:
                    return;
            }
        }
        // compos don't have an elemental (they have 2 - so we have to hack here...)
        int statusSkill = status.getSkill();
        switch (statusSkill) {
            case 2111006: { // FP compo
                switch (stats.getEffectiveness(Element.POISON)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            }
            case 2211006: { // IL compo
                switch (stats.getEffectiveness(Element.ICE)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            }
            case 4120005:
            case 4220005:
            case 14110004: {
                switch (stats.getEffectiveness(Element.POISON)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            }
        }
        if (duration >= 2000000000) {
            duration = 5000; //teleport master
        }
        MonsterStatus stat = status.getStati();
        if (stats.isNoDoom() && stat == MonsterStatus.DOOM) {
            return;
        }

        if (stats.isBoss()) {
            if (stat == MonsterStatus.STUN) {
                return;
            }
            if (checkboss && stat != (MonsterStatus.SPEED) && stat != (MonsterStatus.NINJA_AMBUSH) && stat != (MonsterStatus.WATK) && stat != (MonsterStatus.POISON) && stat != MonsterStatus.VENOMOUS_WEAPON && stat != (MonsterStatus.DARKNESS) && stat != (MonsterStatus.MAGIC_CRASH)) {
                return;
            }
            //hack: don't magic crash cygnus boss
            if (getId() == 8850011 && stat == MonsterStatus.MAGIC_CRASH) {
                return;
            }
        }
        if (stats.isFriendly() || isFake()) {
            if (stat == MonsterStatus.STUN || stat == MonsterStatus.SPEED || stat == MonsterStatus.POISON || stat == MonsterStatus.VENOMOUS_WEAPON) {
                return;
            }
        }
        if ((stat == MonsterStatus.VENOMOUS_WEAPON || stat == MonsterStatus.POISON) && eff == null) {
            return;
        }
        if (stati.containsKey(stat)) {
            cancelStatus(stat);
        }
        if (stat == MonsterStatus.POISON || stat == MonsterStatus.VENOMOUS_WEAPON) {
            int count = 0;
            poisonsLock.readLock().lock();
            try {
                for (MonsterStatusEffect mse : poisons) {
                    if (mse != null && (mse.getSkill() == eff.getSourceId() || mse.getSkill() == GameConstants.getLinkedAttackSkill(eff.getSourceId()) || GameConstants.getLinkedAttackSkill(mse.getSkill()) == eff.getSourceId())) {
                        count++;
                    }
                }
            } finally {
                poisonsLock.readLock().unlock();
            }
            if (count >= eff.getDOTStack()) {
                return;
            }
        }
        if (poison && getHp() > 1 && eff != null) {
            duration = Math.max(duration, eff.getDOTTime() * 1000);
        }
        duration += from.getStat().dotTime * 1000;
        long aniTime = duration;
        if (skilz != null) {
            aniTime += skilz.getAnimationTime();
        }
        status.setCancelTask(aniTime);
        if (poison && getHp() > 1) {
            status.setValue(status.getStati(), (int) ((eff.getDOT() + from.getStat().dot + from.getStat().getDamageIncrease(eff.getSourceId())) * from.getStat().getCurrentMaxBaseDamage() / 100.0));
            int dam = (int) (aniTime / 1000 * status.getX() / 2);
            status.setPoisonSchedule(dam, from);
            if (dam > 0) {
                if (dam >= hp) {
                    dam = (int) (hp - 1);
                }
                damage(from, dam, false);
            }
        } else if (statusSkill == 4111003 || statusSkill == 14111001) { // shadow web
            status.setValue(status.getStati(), (int) (getMobMaxHp() / 50.0 + 0.999));
            status.setPoisonSchedule(status.getX(), from);
        } else if (statusSkill == 4341003) { // monsterbomb
            status.setPoisonSchedule((int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0), from);

        } else if (statusSkill == 4121004 || statusSkill == 4221004) {
            status.setValue(status.getStati(), Math.min(Short.MAX_VALUE, (int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0)));
            int dam = (int) (aniTime / 1000 * status.getX() / 2);
            status.setPoisonSchedule(dam, from);
            if (dam > 0) {
                if (dam >= hp) {
                    dam = (int) (hp - 1);
                }
                damage(from, dam, false);
            }
        }
        MapleCharacter con = getController();
        if (stat == MonsterStatus.POISON || stat == MonsterStatus.VENOMOUS_WEAPON) {
            poisonsLock.writeLock().lock();
            try {
                poisons.add(status);
                if (con != null) {
                    map.broadcastMessage(con, MobPacket.applyMonsterStatus(this, poisons), getTruePosition());
                    con.getClient().write(MobPacket.applyMonsterStatus(this, poisons));
                } else {
                    map.broadcastMessage(MobPacket.applyMonsterStatus(this, poisons), getTruePosition());
                }
            } finally {
                poisonsLock.writeLock().unlock();
            }
        } else {
            stati.put(stat, status);
            if (con != null) {
                map.broadcastMessage(con, MobPacket.applyMonsterStatus(this, status), getTruePosition());
                con.getClient().write(MobPacket.applyMonsterStatus(this, status));
            } else {
                map.broadcastMessage(MobPacket.applyMonsterStatus(this, status), getTruePosition());
            }
        }
    }

    public void applyStatus(MonsterStatusEffect status) { //ONLY USED FOR POKEMONN, ONLY WAY POISON CAN FORCE ITSELF INTO STATI.
        if (stati.containsKey(status.getStati())) {
            cancelStatus(status.getStati());
        }
        stati.put(status.getStati(), status);
        map.broadcastMessage(MobPacket.applyMonsterStatus(this, status), getTruePosition());
    }

    public void dispelSkill(MobSkill skillId) {
        List<MonsterStatus> toCancel = new ArrayList<>();
        for (Entry<MonsterStatus, MonsterStatusEffect> effects : stati.entrySet()) {
            MonsterStatusEffect mse = effects.getValue();
            if (mse.getMobSkill() != null && mse.getMobSkill().getSkillId() == skillId.getSkillId()) { //not checking for level.
                toCancel.add(effects.getKey());
            }
        }
        for (MonsterStatus stat : toCancel) {
            cancelStatus(stat);
        }
    }

    public void applyMonsterBuff(Map<MonsterStatus, Integer> effect, int skillId, long duration, MobSkill skill, List<Integer> reflection) {
        for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
            if (stati.containsKey(z.getKey())) {
                cancelStatus(z.getKey());
            }
            MonsterStatusEffect effectz = new MonsterStatusEffect(z.getKey(), z.getValue(), 0, skill, true, reflection.size() > 0);
            effectz.setCancelTask(duration);
            stati.put(z.getKey(), effectz);
        }
        MapleCharacter con = getController();
        if (reflection.size() > 0) {
            this.reflectpack = MobPacket.applyMonsterStatus(getObjectId(), effect, reflection, skill);
            if (con != null) {
                map.broadcastMessage(con, reflectpack, getTruePosition());
                con.getClient().write(this.reflectpack);
            } else {
                map.broadcastMessage(reflectpack, getTruePosition());
            }
        } else {
            for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
                if (con != null) {
                    map.broadcastMessage(con, MobPacket.applyMonsterStatus(getObjectId(), z.getKey(), z.getValue(), skill), getTruePosition());
                    con.getClient().write(MobPacket.applyMonsterStatus(getObjectId(), z.getKey(), z.getValue(), skill));
                } else {
                    map.broadcastMessage(MobPacket.applyMonsterStatus(getObjectId(), z.getKey(), z.getValue(), skill), getTruePosition());
                }
            }
        }
    }

    public void setTempEffectiveness(Element e, long milli) {
        stats.setEffectiveness(e, ElementalEffectiveness.WEAK);

        EtcTimer.getInstance().schedule(() -> {
            stats.removeEffectiveness(e);
        }, milli);
    }

    public boolean isBuffed(MonsterStatus status) {
        if (status == MonsterStatus.POISON || status == MonsterStatus.VENOMOUS_WEAPON) {
            return poisons.size() > 0 || stati.containsKey(status);
        }
        return stati.containsKey(status);
    }

    public MonsterStatusEffect getBuff(MonsterStatus status) {
        return stati.get(status);
    }

    public int getStatiSize() {
        return stati.size() + (poisons.size() > 0 ? 1 : 0);
    }

    public ArrayList<MonsterStatusEffect> getAllBuffs() {
        ArrayList<MonsterStatusEffect> ret = new ArrayList<>();
        for (MonsterStatusEffect e : stati.values()) {
            ret.add(e);
        }
        poisonsLock.readLock().lock();
        try {
            for (MonsterStatusEffect e : poisons) {
                ret.add(e);
            }
        } finally {
            poisonsLock.readLock().unlock();
        }
        return ret;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public boolean isFake() {
        return fake;
    }

    public MapleMap getMap() {
        return map;
    }

    public long getLastSkillUsed(int skillId) {
        if (usedSkills.containsKey(skillId)) {
            return usedSkills.get(skillId);
        }
        return 0;
    }

    public void setLastSkillUsed(int skillId, long now, long cooltime) {
        switch (skillId) {
            case 140:
                usedSkills.put(skillId, now + (cooltime * 2));
                usedSkills.put(141, now);
                break;
            case 141:
                usedSkills.put(skillId, now + (cooltime * 2));
                usedSkills.put(140, now + cooltime);
                break;
            default:
                usedSkills.put(skillId, now + cooltime);
                break;
        }
    }

    public boolean isFirstAttack() {
        return stats.isFirstAttack();
    }

    public int getBuffToGive() {
        return stats.getBuffToGive();
    }

    public void doPoison(MonsterStatusEffect status, WeakReference<MapleCharacter> weakChr) {
        if ((status.getStati() == MonsterStatus.VENOMOUS_WEAPON || status.getStati() == MonsterStatus.POISON) && poisons.size() <= 0) {
            return;
        }
        if (status.getStati() != MonsterStatus.VENOMOUS_WEAPON && status.getStati() != MonsterStatus.POISON && !stati.containsKey(status.getStati())) {
            return;
        }
        if (weakChr == null) {
            return;
        }
        long damage = status.getPoisonSchedule();
        boolean shadowWeb = status.getSkill() == 4111003 || status.getSkill() == 14111001;
        MapleCharacter chr = weakChr.get();
        boolean cancel = damage <= 0 || chr == null || chr.getMapId() != map.getId();
        if (damage >= hp) {
            damage = hp - 1;
            cancel = !shadowWeb || cancel;
        }
        if (!cancel) {
            damage(chr, damage, false);
            if (shadowWeb) {
                map.broadcastMessage(MobPacket.damageMonster(getObjectId(), damage), getTruePosition());
            }
        }
    }

    public String getName() {
        return stats.getName();
    }

    public int getLinkOid() {
        return linkoid;
    }

    public void setLinkOid(int lo) {
        this.linkoid = lo;
    }

    public ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect> getStati() {
        return stati;
    }

    public void addEmpty() {
        for (MonsterStatus stat : MonsterStatus.values()) {
            if (stat.isEmpty()) {
                stati.put(stat, new MonsterStatusEffect(stat, 0, 0, null, false));
            }
        }
    }

    public int getStolen() {
        return stolen;
    }

    public void setStolen(int s) {
        this.stolen = s;
    }

    public void handleSteal(MapleCharacter chr) {
        double showdown = 100.0;
        MonsterStatusEffect mse = getBuff(MonsterStatus.SHOWDOWN);
        if (mse != null) {
            showdown += mse.getX();
        }

        Skill steal = SkillFactory.getSkill(4201004);
        final int level = chr.getTotalSkillLevel(steal);
        float chServerrate = ChannelServer.getInstance(chr.getClient().getChannel()).getDropRate(chr.getWorld());
        if (level > 0 && !getStats().isBoss() && stolen == -1 && steal.getEffect(level).makeChanceResult()) {
            MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
            List<MonsterDropEntry> de = mi.retrieveDrop(getId());
            if (de == null) {
                stolen = 0;
                return;
            }
            List<MonsterDropEntry> dropEntry = new ArrayList<>(de);
            Collections.shuffle(dropEntry);
            Item idrop;
            for (MonsterDropEntry d : dropEntry) { //set to 4x rate atm, 40% chance + 10x
                if (d.itemId > 0 && d.questid == 0 && d.itemId / 10000 != 238 && Randomizer.nextInt(999999) < (int) (10 * d.chance * chServerrate * chr.getDropMod() * chr.getStat().dropBuff / 100.0 * (showdown / 100.0))) { //kinda op
                    if (GameConstants.getInventoryType(d.itemId) == MapleInventoryType.EQUIP) {
                        Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(d.itemId);
                        idrop = MapleItemInformationProvider.getInstance().randomizeStats(eq);
                    } else {
                        idrop = new Item(d.itemId, (byte) 0, (short) (d.Maximum != 1 ? Randomizer.nextInt(d.Maximum - d.Minimum) + d.Minimum : 1), (byte) 0);
                    }
                    stolen = d.itemId;
                    map.spawnMobDrop(idrop, map.calcDropPos(getPosition(), getTruePosition()), this, chr, (byte) 0, (short) 0, false);
                    break;
                }
            }
        } else {
            stolen = 0; //failed once, may not go again
        }
    }

    public void setLastNode(int lastNode) {
        this.lastNode = lastNode;
    }

    public int getLastNode() {
        return lastNode;
    }

    public void cancelStatus(MonsterStatus stat) {
        if (stat == MonsterStatus.EMPTY || stat == MonsterStatus.SUMMON) {
            return;
        }
        MonsterStatusEffect mse = stati.get(stat);
        if (mse == null || !isAlive()) {
            return;
        }
        if (mse.isReflect()) {
            reflectpack = null;
        }
        mse.cancelPoisonSchedule(this);
        MapleCharacter con = getController();
        if (con != null) {
            map.broadcastMessage(con, MobPacket.cancelMonsterStatus(getObjectId(), stat), getTruePosition());
            con.getClient().write(MobPacket.cancelMonsterStatus(getObjectId(), stat));
        } else {
            map.broadcastMessage(MobPacket.cancelMonsterStatus(getObjectId(), stat), getTruePosition());
        }
        stati.remove(stat);
    }

    public void cancelSingleStatus(MonsterStatusEffect stat) {
        if (stat == null || stat.getStati() == MonsterStatus.EMPTY || stat.getStati() == MonsterStatus.SUMMON || !isAlive()) {
            return;
        }
        if (stat.getStati() != MonsterStatus.POISON && stat.getStati() != MonsterStatus.VENOMOUS_WEAPON) {
            cancelStatus(stat.getStati());
            return;
        }
        poisonsLock.writeLock().lock();
        try {
            if (!poisons.contains(stat)) {
                return;
            }
            poisons.remove(stat);
            if (stat.isReflect()) {
                reflectpack = null;
            }
            stat.cancelPoisonSchedule(this);
            MapleCharacter con = getController();
            if (con != null) {
                map.broadcastMessage(con, MobPacket.cancelPoison(this.getObjectId(), stat), getTruePosition());
                con.getClient().write(MobPacket.cancelPoison(this.getObjectId(), stat));
            } else {
                map.broadcastMessage(MobPacket.cancelPoison(this.getObjectId(), stat), getTruePosition());
            }
        } finally {
            poisonsLock.writeLock().unlock();
        }
    }

    public void cancelDropItem() {
        lastDropTime = 0;
    }

    public void startDropItemSchedule() {
        cancelDropItem();
        if (stats.getDropItemPeriod() <= 0 || !isAlive()) {
            return;
        }
        shouldDropItem = false;
        lastDropTime = System.currentTimeMillis();
    }

    public boolean shouldDrop(long now) {
        return lastDropTime > 0 && lastDropTime + (stats.getDropItemPeriod() * 1000) < now;
    }

    public void doDropItem(long now) {
        int itemId;
        switch (getId()) {
            case 9300061:
                itemId = 4001101;
                break;
            default: //until we find out ... what other mobs use this and how to get the ITEMID
                cancelDropItem();
                return;
        }
        if (isAlive() && map != null) {
            if (shouldDropItem) {
                map.spawnAutoDrop(itemId, getTruePosition());
            } else {
                shouldDropItem = true;
            }
        }
        lastDropTime = now;
    }

    public Packet getNodePacket() {
        return nodepack;
    }

    public void setNodePacket(Packet np) {
        this.nodepack = np;
    }

    public void registerKill(long next) {
        this.nextKill = System.currentTimeMillis() + next;
    }

    public boolean shouldKill(long now) {
        return nextKill > 0 && now > nextKill;
    }

    public int getLinkCID() {
        return linkCID;
    }

    public void setLinkCID(int lc) {
        this.linkCID = lc;
        if (lc > 0) {
            stati.put(MonsterStatus.HYPNOTIZE, new MonsterStatusEffect(MonsterStatus.HYPNOTIZE, 60000, 30001062, null, false));
        }
    }

    public void setTriangulation(int triangulation) {
        this.triangulation = triangulation;
    }

    public int getTriangulation() {
        return triangulation;
    }

    // <editor-fold defaultstate="visible" desc="Attacks & EXP Handling"> 
    private static class AttackingMapleCharacter {

        private final int attackerChrId;
        private long lastAttackTime;

        public AttackingMapleCharacter(int attackerChrId, long lastAttackTime) {
            super();
            this.attackerChrId = attackerChrId;
            this.lastAttackTime = lastAttackTime;
        }

        public long getLastAttackTime() {
            return lastAttackTime;
        }

        public void setLastAttackTime(long lastAttackTime) {
            this.lastAttackTime = lastAttackTime;
        }

        public int getAttacker() {
            return attackerChrId;
        }
    }

    private interface AttackerEntry {

        /**
         * Returns the list of attackers for this monster. Note: There is no guarantee that the character is in the map. You'll have to
         * check that!
         *
         * @return
         */
        List<AttackingMapleCharacter> getAttackers();

        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime);

        public long getDamage();

        public boolean contains(MapleCharacter chr);

        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill, int killerCharId);
    }

    private class SingleAttackerEntry implements AttackerEntry {

        private long damage = 0;
        private final int chrid;
        private long lastAttackTime;

        public SingleAttackerEntry(MapleCharacter from) {
            this.chrid = from.getId();
        }

        @Override
        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
            if (chrid == from.getId()) {
                this.damage += damage;
                if (updateAttackTime) {
                    lastAttackTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public List<AttackingMapleCharacter> getAttackers() {
            return Collections.singletonList(new AttackingMapleCharacter(chrid, lastAttackTime));
        }

        @Override
        public boolean contains(MapleCharacter chr) {
            return chrid == chr.getId();
        }

        @Override
        public long getDamage() {
            return damage;
        }

        @Override
        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill, int killerCharId) {
            MapleCharacter pPlayer = map.getCharacterById(chrid);
            if (pPlayer != null && pPlayer.isAlive()) {
                // Burning field bonus
                final int burningFieldBonusEXP = getMap().getBurningFieldBonusEXP();

                giveExpToCharacter(pPlayer, true, baseExp, 0, mostDamage, 1, (byte) 0, 0, (byte) 0, (byte) 0, burningFieldBonusEXP, lastSkill);
                
                if (GameConstants.isHayato(pPlayer.getJob())) {
                    HayatoHandler.handleBladeStance(pPlayer);
                }
                if (GameConstants.isThiefShadower(pPlayer.getJob())) {
                    ShadowerHandler.handleBodyCount(pPlayer);
                }
            }
        }

        @Override
        public int hashCode() {
            return chrid;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            SingleAttackerEntry other = (SingleAttackerEntry) obj;
            return chrid == other.chrid;
        }
    }

    private class PartyAttackerEntry implements AttackerEntry {

        private long totDamage = 0;
        private final Map<Integer, OnePartyAttacker> attackers = new HashMap<>();
        private final int partyid;

        public PartyAttackerEntry(int partyid) {
            this.partyid = partyid;
        }

        @Override
        public List<AttackingMapleCharacter> getAttackers() {
            List<AttackingMapleCharacter> ret = new ArrayList<>(attackers.size());
            for (Entry<Integer, OnePartyAttacker> entry : attackers.entrySet()) {
                ret.add(new AttackingMapleCharacter(entry.getKey(), entry.getValue().lastAttackTime));
            }
            return ret;
        }

        private Map<MapleCharacter, OnePartyAttacker> resolveAttackers() {
            Map<MapleCharacter, OnePartyAttacker> ret = new HashMap<>(attackers.size());
            for (Entry<Integer, OnePartyAttacker> aentry : attackers.entrySet()) {
                MapleCharacter chr = map.getCharacterById(aentry.getKey());
                if (chr != null) {
                    ret.put(chr, aentry.getValue());
                }
            }
            return ret;
        }

        @Override
        public boolean contains(MapleCharacter chr) {
            return attackers.containsKey(chr.getId());
        }

        @Override
        public long getDamage() {
            return totDamage;
        }

        @Override
        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
            OnePartyAttacker oldPartyAttacker = attackers.get(from.getId());
            if (oldPartyAttacker != null) {
                oldPartyAttacker.damage += damage;
                oldPartyAttacker.lastKnownParty = from.getParty();
                if (updateAttackTime) {
                    oldPartyAttacker.lastAttackTime = System.currentTimeMillis();
                }
            } else {
                // TODO actually this causes wrong behaviour when the party changes between attacks
                // only the last setup will get exp - but otherwise we'd have to store the full party
                // constellation for every attack/everytime it changes, might be wanted/needed in the
                // future but not now
                OnePartyAttacker onePartyAttacker = new OnePartyAttacker(from.getParty(), damage);
                attackers.put(from.getId(), onePartyAttacker);
                if (!updateAttackTime) {
                    onePartyAttacker.lastAttackTime = 0;
                }
            }
            totDamage += damage;
        }

        @Override
        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill, int killerCharId) {
            MapleCharacter highestAttacker = null;
            long iDamage, highestDamage = 0;
            int iexp;
            int fieldPartyMapBonusRate = 0; // bonus EXP rate acquired through a party play field
            double addedPartyLevel, levelMod, innerBaseExp;

            Map<MapleCharacter, ExpMap> expMap = new HashMap<>();
            byte classBonusExp;
            byte premiumBonusExp;

            Map<MapleCharacter, OnePartyAttacker> resolvedAttackers = resolveAttackers();

            // Loop through for every player in the monster's list of attacker
            for (Entry<MapleCharacter, OnePartyAttacker> attacker : resolvedAttackers.entrySet()) {
                MapleParty party = attacker.getValue().getLastKnownParty();

                addedPartyLevel = 0;
                classBonusExp = 0;
                premiumBonusExp = 0;
                List<MapleCharacter> expApplicable = new ArrayList<>();

                int partyZoneLeechableSize = 0;

                // Loop through every party member in this attacker's party, including itself
                for (MaplePartyCharacter partychar : party.getMembers()) {
                    int monsterLevel = stats.getLevel();

                    boolean isEligibleToLeech
                            = attacker.getKey().getLevel() - partychar.getLevel() <= ServerConstants.LEECH_LEVEL_RANGE
                            || monsterLevel - partychar.getLevel() <= ServerConstants.EXP_WITHIN_MONSTER; // between 40 of the monster

                    if (isEligibleToLeech) {
                        MapleCharacter pchr = map.getCharacterById(partychar.getId());

                        if (pchr != null && pchr.isAlive()) {
                            expApplicable.add(pchr);
                            addedPartyLevel += pchr.getLevel();

                            // Party zone map
                            partyZoneLeechableSize++; // player is within leeching range

                            // Class bonus exp, used by nexon when Evan was released.
                            // party up an Evan and get additional EXP.
                            classBonusExp += ServerConstants.classExpBonus(pchr.getJob());

                            // If one person equips welcome back ring, everyone in the party gets 80% too
                            if (pchr.getStat().equippedWelcomeBackRing && premiumBonusExp == 0) {
                                premiumBonusExp = 80;
                            }
                        }
                    }
                }
                iDamage = attacker.getValue().damage;
                if (iDamage > highestDamage) {
                    highestAttacker = attacker.getKey();
                    highestDamage = iDamage;
                }
                innerBaseExp = baseExp * ((double) iDamage / totDamage);
                if (expApplicable.size() <= 1) {
                    classBonusExp = 0; //no class bonus if not in a party.
                } else {
                    if (map.getSharedMapResources().partyBonusR > 0) {
                        // For every member, gives 50% more, except for self
                        fieldPartyMapBonusRate = map.getSharedMapResources().partyBonusR * Math.max(0, (partyZoneLeechableSize - 1));
                    }
                }

                for (MapleCharacter expReceiver : expApplicable) {
                    iexp = expMap.get(expReceiver) == null ? 0 : expMap.get(expReceiver).exp;
                    levelMod = expReceiver.getLevel() / addedPartyLevel * ServerConstants.ATTACKER_EXP_RATIO;
                    iexp += (int) Math.round(((attacker.getKey().getId() == expReceiver.getId() ? ServerConstants.LEECHER_EXP_RATIO : 0.0) + levelMod) * innerBaseExp);

                    expMap.put(expReceiver, new ExpMap(iexp, (byte) expApplicable.size(), classBonusExp, premiumBonusExp, fieldPartyMapBonusRate));
                }
            }

            //Bloodless Channel Party EXP Bonus
            if (ServerConstants.BLOODLESS_EVENT && ServerConstants.BLOODLESS_PARTY_BONUS) {
                if (resolvedAttackers.size() >= 3) { //3 party members+
                    bloodlessPartyBonus = 30; //percentage bonus exp
                } else if (resolvedAttackers.size() >= 2) {
                    bloodlessPartyBonus = 10; //percentage bonus exp
                }
            }

            // Party bonus
            int partybonusMultiplier = calculatePartyBonusEXP(resolvedAttackers.size(), expMap.size()); // bonus EXP in percentage

            // Burning field bonus
            final int burningFieldBonusEXP = getMap().getBurningFieldBonusEXP();

            // Give Exp
            for (Entry<MapleCharacter, ExpMap> expReceiver : expMap.entrySet()) {
                ExpMap expmap = expReceiver.getValue();

                giveExpToCharacter(
                        expReceiver.getKey(),
                        expReceiver.getKey().getId() == killerCharId,
                        expmap.getExp(),
                        partybonusMultiplier,
                        mostDamage ? expReceiver.getKey() == highestAttacker : false,
                        expMap.size(),
                        expmap.getPartySize(),
                        expmap.getFieldPartyMapBonusRate(),
                        expmap.getClassBonusExp(),
                        expmap.getPremiumBonusExp(),
                        burningFieldBonusEXP,
                        lastSkill
                );
            }

            // GC
            expMap.clear();
            resolvedAttackers.clear();
        }

        /**
         * Calculates the party bonus exp to be obtained. http://strategywiki.org/wiki/MapleStory/Formulas#Experience
         *
         * @param numAttackersAttacked
         * @param leechableMembers
         * @return
         */
        private int calculatePartyBonusEXP(int numAttackersAttacked, int leechableMembers) {
            int partybonusMultiplier = 0; // bonus EXP in percentage

            switch (leechableMembers) {
                case 2:
                    partybonusMultiplier = 25;
                    break;
                case 3:
                    partybonusMultiplier = 55;
                    break;
                case 4:
                    partybonusMultiplier = 90;
                    break;
                case 5:
                    partybonusMultiplier = 130;
                    break;
                case 6:
                    partybonusMultiplier = 175;
                    break;
            }
            if (numAttackersAttacked > 1) {
                partybonusMultiplier += (Math.min(3, numAttackersAttacked) * 10);
            }

            return partybonusMultiplier;
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = prime * result + partyid;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            PartyAttackerEntry other = (PartyAttackerEntry) obj;
            return partyid == other.partyid;
        }
    }

    private static class ExpMap {

        private int exp;
        private final byte ptysize;
        private final byte Class_Bonus_EXP;
        private final byte Premium_Bonus_EXP;
        private final int fieldPartyMapBonusRate;

        public ExpMap(int exp, byte ptysize, byte Class_Bonus_EXP, byte Premium_Bonus_EXP, int fieldPartyMapBonusRate) {
            super();
            this.exp = exp;
            this.ptysize = ptysize;
            this.Class_Bonus_EXP = Class_Bonus_EXP;
            this.Premium_Bonus_EXP = Premium_Bonus_EXP;
            this.fieldPartyMapBonusRate = fieldPartyMapBonusRate;
        }

        public int getExp() {
            return exp;
        }

        public byte getPartySize() {
            return ptysize;
        }

        public byte getClassBonusExp() {
            return Class_Bonus_EXP;
        }

        public byte getPremiumBonusExp() {
            return Premium_Bonus_EXP;
        }

        public int getFieldPartyMapBonusRate() {
            return fieldPartyMapBonusRate;
        }
    }

    private static class OnePartyAttacker {

        private MapleParty lastKnownParty;
        private long damage;
        private long lastAttackTime;

        public OnePartyAttacker(MapleParty lastKnownParty, long damage) {
            super();
            this.lastKnownParty = lastKnownParty;
            this.damage = damage;
            this.lastAttackTime = System.currentTimeMillis();
        }

        public MapleParty getLastKnownParty() {
            return lastKnownParty;
        }

        public long getDamage() {
            return damage;
        }

        public long getLastAttackTime() {
            return lastAttackTime;
        }
    }

    // </editor-fold> 
}
