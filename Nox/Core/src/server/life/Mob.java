package server.life;

import server.life.mob.BuffedMob;
import server.life.mob.MobTemporaryStat;
import server.life.mob.ForcedMobStat;
import client.*;
import client.MapleTrait.MapleTraitType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.ServerConstants;
import constants.GameConstants;
import constants.ServerConstants;
import client.jobs.Explorer;
import client.jobs.Explorer.ShadowerHandler;
import client.jobs.Sengoku;
import client.jobs.Sengoku.HayatoHandler;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;

import scripting.EventInstanceManager;
import server.MapleItemInformationProvider;
import server.StatEffect;
import server.Randomizer;
import server.Timer.EtcTimer;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.messages.ExpGainTypes;
import service.ChannelServer;
import tools.ConcurrentEnumMap;
import tools.packet.CField;
import tools.packet.MobPacket;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.OutPacket;
import tools.Utility;

public class Mob extends AbstractLoadedMapleLife {

    private MonsterStats stats;
    private ForcedMonsterStats forcedStats = null;

    private ForcedMobStat forcedMobStat;
    private MobTemporaryStat temporaryStat;

    private long hp, mp, nextKill = 0, lastDropTime = 0;
    private byte carnivalTeam = -1;
    private MapleMap map;
    private WeakReference<Mob> sponge = new WeakReference<>(null);
    private int linkoid = 0, lastNode = -1, highestDamageChar = 0, linkCID = 0; // Just a reference for monster EXP distribution after dead
    private int controller = -1;
    private boolean fake = false, dropsDisabled = false, controllerHasAggro = false, statChanged;
    private final Collection<AttackerEntry> attackers = new LinkedList<>();
    private EventInstanceManager eventInstance;
    private MonsterListener listener = null;
    private OutPacket reflectpack = null, nodepack = null;
    private final ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect> stati = new ConcurrentEnumMap<>(MonsterStatus.class);
    private final LinkedList<MonsterStatusEffect> poisons = new LinkedList<>();
    private final ReentrantReadWriteLock poisonsLock = new ReentrantReadWriteLock();
    private Map<Integer, Long> usedSkills;
    private int stolen = -1;
    private boolean shouldDropItem = false;
    private int triangulation = 0;
    private ChangeableStats ostats = null;
    private int bloodlessPartyBonus = 0;

    public Mob(int id, MonsterStats stats) {
        super(id);
        initWithStats(stats);

        forcedMobStat = new ForcedMobStat();
        temporaryStat = new MobTemporaryStat(this);
    }

    public Mob(Mob monster) {
        super(monster);
        initWithStats(monster.stats);

        forcedMobStat = new ForcedMobStat();
        temporaryStat = new MobTemporaryStat(this);
    }
    
    public ForcedMobStat getForcedMobStat() {
        return forcedMobStat;
    }

    private void initWithStats(MonsterStats stats) {
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
        
        getStats().setHp(0);
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

    public MonsterStats getStats() {
        return stats;
    }

    public void disableDrops() {
        this.dropsDisabled = true;
    }

    public boolean dropsDisabled() {
        return dropsDisabled;
    }

    public void setSponge(Mob mob) {
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

    public Mob getSponge() {
        return sponge.get();
    }

    public void damage(User from, long damage, boolean updateAttackTime) {
        damage(from, damage, updateAttackTime, 0);
    }

    public void damage(User from, long damage, boolean updateAttackTime, int lastSkill) {
        if (from == null || damage <= 0 || !isAlive()) {
            return;
        }

        // Setup to instant kill bosses while testing/debugging.
        if (from.isDeveloper()) {
            
        }

        if (ServerConstants.DEVELOPER_DEBUG_MODE) System.err.println("[Damage Operation] Mob Damage Received (" + damage + "), Mob ID (" + getId() + ")");

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

            if (from.isGM()) {
                from.dropMessage(5, "[Monster Debug] Starting HP : " + hp);
            }

            hp -= rDamage;

            if (from.isGM()) {
                from.dropMessage(5, "[Monster Debug] Damage Applied : " + rDamage + " / Final HP : " + hp);
            }

            if (hp > 0) {
                if (hp < stats.getSelfDHp()) { // HP is below the selfd level
                    map.killMonster(this, from, false, false, stats.getSelfD(), lastSkill);
                } else { // Show HP
                    for (AttackerEntry mattacker : getAttackers()) {
                        for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                            User chr = map.getCharacterById(cattacker.getAttacker());
                            if (chr == null) {
                                continue; // ref is gone. logged off.
                            }
                            if (chr.getMap() == from.getMap()) { // compare map object, in case the attacker is in an instanced map with the same mapid
                                if (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000) {
                                    chr.getClient().SendPacket(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
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
                        map.broadcastPacket(MobPacket.showBossHP(sponge.get().getId(), -1, sponge.get().getMobMaxHp()));
                        map.killMonster(sponge.get(), from, true, false, (byte) 1, lastSkill);
                    } else {
                        map.broadcastPacket(MobPacket.showBossHP(sponge.get()));
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
                            map.broadcastPacket(MobPacket.showBossHP(this), this.getTruePosition());
                            break;
                        case FriendlyMonsterHP:
                            map.broadcastPacket(from, MobPacket.damageFriendlyMob(this, damage, true), false);
                            break;
                        case MulungDojoMonsterHP:
                            map.broadcastPacket(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                            from.mulungEnergyModifier(true);
                            break;
                        case RegularMonsterHP:
                            for (AttackerEntry mattacker : getAttackers()) {
                                if (mattacker != null) {
                                    for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                                        User chr = map.getCharacterById(cattacker.getAttacker());
                                        if (chr == null) {
                                            continue;
                                        }

                                        if (chr.getMap() == from.getMap()) { // compare map object, in case the attacker is in an instanced map with the same mapid
                                            if (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000) {
                                                chr.getClient().SendPacket(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case BossHPWithRegularHPBar:
                            map.broadcastPacket(MobPacket.showBossHP(this), this.getPosition());
                            map.broadcastPacket(MobPacket.showMonsterHP(getObjectId(), (int) Math.ceil((hp * 100.0) / getMobMaxHp())));
                            break;
                    }
                }

                if (hp <= 0) {
                    if (stats.getHPDisplayType() == MonsterHpDisplayType.BossHP) {
                        map.broadcastPacket(MobPacket.showBossHP(getId(), -1, getMobMaxHp()), this.getTruePosition());
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
            map.broadcastPacket(MobPacket.healMonster(getObjectId(), hp));
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

    /**
     * Gives experience to a player after a monster has been killed. This also handles the additional EXP acquired through other variables
     * such as item buff, map, party bonus, etc
     *
     * @param pPlayer
     * @param nLastSkillID
     * @param nBaseEXP
     * @param nPartybonusMultiplier -- The party bonus in percentage to give to this character for being in a party
     * @param bHighestDamage
     * @param nExpSharers
     * @param PartyBonusPercentage
     * @param nClassBonusEXPPercentage
     * @param nPremiumBonusEXPPercentage
     * @param nBurningFieldBonusEXPRate - The burning field bonus EXP in percentage (eg: 20%, 30%) [This parameter is pushed to avoid
     * multiple calls to MapleMap.getBurningFieldBonus() per character, since its all the same]
     * @param bKiller - Determines if this character receiving the EXP is the one who had the last hit.
     */
    private void giveExpToCharacter(User pPlayer, boolean bKiller, long nBaseEXP,
            int nPartybonusMultiplier, boolean bHighestDamage, int nExpSharers, byte nPartySize, int nFieldPartyBonusEXPPercentage,
            byte nClassBonusEXPPercentage, byte nPremiumBonusEXPPercentage, int nBurningFieldBonusEXPRate,
            int nLastSkillID) {

        OnNxGainRequest(pPlayer, bKiller);
        OnMesoDropRequest(pPlayer);
        pPlayer.incrementVMatrixKills(this); // For VMatrix Quest
        
        if (BuffedMob.OnBuffedChannel(Utility.requestChannel(pPlayer.getId()))) {
            if (!BuffedMob.BUFFED_BOSSES) {
                if (!getStats().isBoss()) {
                    nBaseEXP /= BuffedMob.HP_BUFF; // Corrects the original base exp calculation from increasing the HP.
                    nBaseEXP *= BuffedMob.EXP_BUFF;
                }   
            } else {
                nBaseEXP /= BuffedMob.HP_BUFF;
                nBaseEXP *= BuffedMob.EXP_BUFF;
            }
        }

        if (bHighestDamage) {
            if (eventInstance != null) {
                eventInstance.monsterKilled(pPlayer, this);
            } else {
                EventInstanceManager em = pPlayer.getEventInstance();
                if (em != null) {
                    em.monsterKilled(pPlayer, this);
                }
            }
            highestDamageChar = pPlayer.getId();
        }

        if (nBaseEXP > 0) {
            EnumMap<ExpGainTypes, Integer> expIncreaseStats = new EnumMap<>(ExpGainTypes.class);

            // Server EXP Rate
            double expRate_Server = ChannelServer.getInstance(map.getChannel()).getExpRate(pPlayer.getWorld());

            if (expRate_Server > 1) {
                nBaseEXP *= expRate_Server;
            }

            long totalEXPGained = nBaseEXP;
            long bonusEXPGained = 0; //bonus EXP do not get shown to the user via the main EXP gained.

            ///
            ///  Accounting for additional EXP gain here. These orders are according to ExpGainTypes, as acquired from the client.
            ///
            // Exp Coupons
            if (pPlayer.haveItem(GameConstants._200PercentExpBoost)) {
                totalEXPGained *= 2;
            } else if (pPlayer.haveItem(GameConstants._150PercentExpBoost)) {
                totalEXPGained *= 1.5;
            }

            // 2x coupons rate
            if (pPlayer.getStat().expMod > 1.0f) {
                totalEXPGained *= pPlayer.getStat().expMod;
            }
            // Double time rate
            if (ServerConstants.DOUBLE_TIME) {
                totalEXPGained *= 2.0;
            }
            // Showdown skill
            MonsterStatusEffect ms = stati.get(MonsterStatus.SHOWDOWN);
            if (ms != null) {
                totalEXPGained += (int) (nBaseEXP * (ms.getX() / 100.0));

            }

            // Party bonus
            if (nPartybonusMultiplier > 0) {
                int partyBonusAdditionalEXP = (int) (nBaseEXP * (nPartybonusMultiplier / 100f));
                bonusEXPGained += partyBonusAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.PartyBonus, partyBonusAdditionalEXP);
                expIncreaseStats.put(ExpGainTypes.PartyBonusPercentage, nPartybonusMultiplier);
            }

            // Holy symbol
            Integer holySymbol = pPlayer.getBuffedValue(CharacterTemporaryStat.HolySymbol);
            if (holySymbol != null) {
                double partybonusPercentage = (holySymbol.doubleValue() / 100.0f);
                int holySymbolAdditionalEXP = (int) (nBaseEXP * partybonusPercentage);
                bonusEXPGained += holySymbolAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.BaseAddExp, holySymbolAdditionalEXP);
            }

            // Equipment bonus EXP [fairy]
            if (pPlayer.getStat().equippedFairy > 0 && pPlayer.getFairyExp() > 0) {          //  Equipment_Bonus_EXP = (int) ((exp / 100.0) * attacker.getStat().equipmentBonusExp);
                int fairyBonusEXP = (int) ((nBaseEXP / 100.0) * pPlayer.getFairyExp());
                bonusEXPGained += fairyBonusEXP;

                expIncreaseStats.put(ExpGainTypes.ItemBonus, fairyBonusEXP);
            }

            // Exp buff bonus
            if (pPlayer.getStat().expBuff > 100) {
                int bonusAdditionalEXP = (int) (nBaseEXP * (pPlayer.getStat().expBuff / 100.0f));
                bonusEXPGained += bonusAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.ExpBuffBonus, bonusAdditionalEXP);
            }

            // Class bonus EXP
            if (nClassBonusEXPPercentage > 0) {
                int psdBonusExp = (int) ((nBaseEXP / 100.0) * nClassBonusEXPPercentage);
                bonusEXPGained += psdBonusExp;

                expIncreaseStats.put(ExpGainTypes.PsdBonus, psdBonusExp);
            }

            if (pPlayer.getStat().expMod_ElveBlessing >= 1.0) {
                int psdBonusExp = (int) (nBaseEXP * (pPlayer.getStat().expMod_ElveBlessing - 1.0f));
                bonusEXPGained += psdBonusExp;

                expIncreaseStats.put(ExpGainTypes.PsdBonus, psdBonusExp);
            }

            // premium bonus
            if (nPremiumBonusEXPPercentage > 0) {
                int premiumBonusExp = (int) ((nBaseEXP / 100.0) * nPremiumBonusEXPPercentage);
                bonusEXPGained += premiumBonusExp;

                expIncreaseStats.put(ExpGainTypes.PremiumIpBonus, premiumBonusExp);
            }

            // Indie EXP rate [rune]
            if (pPlayer.getStat().indieExpBuff > 100) {
                int indieExpAdditionalEXP = (int) (nBaseEXP * ((pPlayer.getStat().indieExpBuff - 100) / 100.0f));
                bonusEXPGained += indieExpAdditionalEXP;

                expIncreaseStats.put(ExpGainTypes.IndieBonus, indieExpAdditionalEXP);
            }

            // Burning field bonus EXP
            if (nBurningFieldBonusEXPRate > 0) {
                int burningFieldBonusEXP = (int) (nBaseEXP * (nBurningFieldBonusEXPRate / 100f));
                bonusEXPGained += burningFieldBonusEXP;

                expIncreaseStats.put(ExpGainTypes.RestFieldBonus, burningFieldBonusEXP);
            }

            // Map bonus EXP
            if (nFieldPartyBonusEXPPercentage >= 0) {
                int fieldPartyMapBonusEXP = (int) (nBaseEXP * (nFieldPartyBonusEXPPercentage / 100f));
                bonusEXPGained += fieldPartyMapBonusEXP;

                expIncreaseStats.put(ExpGainTypes.FieldBonus, fieldPartyMapBonusEXP);
            }

            //////////////// Cursed, put this last.
            if (pPlayer.hasDisease(MapleDisease.CURSE)) {
                totalEXPGained /= 2;
                bonusEXPGained /= 2;
            }
            pPlayer.gainExp(totalEXPGained, bonusEXPGained, true, bHighestDamage, false, nBurningFieldBonusEXPRate, expIncreaseStats);

            // Others - Trait
            pPlayer.getTrait(MapleTraitType.charisma).addExp(stats.getCharismaEXP(), pPlayer);

        }

        // For quest kills
        pPlayer.mobKilled(stats.getName(), getId(), nLastSkillID);
    }

    public int killBy(User killer, int lastSkill) {
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

        User controll = getController();
        if (controll != null) { // this can/should only happen when a hidden gm attacks the monster
            if (GameConstants.isAzwanMap(killer.getMapId())) {
                controll.getClient().SendPacket(MobPacket.stopControllingMonster(this, true));
            } else {
                controll.getClient().SendPacket(MobPacket.stopControllingMonster(this, false));
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
        Mob oldSponge = getSponge();
        sponge = new WeakReference<>(null);
        if (oldSponge != null && oldSponge.isAlive()) {
            boolean set = true;
            for (MapleMapObject mon : map.getAllMapObjects(MapleMapObjectType.MONSTER)) {
                Mob mons = (Mob) mon;
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
        Mob spongy = null;
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
                    Mob mob = LifeFactory.getMonster(i);

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
                    map.OnSpawnMonster(spongy, -2);
                    for (MapleMapObject mon : map.getAllMapObjects(MapleMapObjectType.MONSTER)) {
                        Mob mons = (Mob) mon;
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
                List<Mob> mobs = new ArrayList<>();

                for (int i : toSpawn) {
                    Mob mob = LifeFactory.getMonster(i);

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
                    map.OnSpawnMonster(spongy, -2);

                    for (Mob i : mobs) {
                        map.OnSpawnMonster(i, -2);
                        i.setSponge(spongy);
                    }
                }
                break;
            }
            case 8820014: {
                for (int i : toSpawn) {
                    Mob mob = LifeFactory.getMonster(i);

                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    mob.setPosition(getTruePosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.OnSpawnMonster(mob, -2);
                }
                break;
            }
            default: {
                for (int i : toSpawn) {
                    Mob mob = LifeFactory.getMonster(i);

                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    mob.setPosition(getTruePosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.spawnRevives(mob, this.getObjectId());

                    if (mob.getId() == 9300216) {
                        map.broadcastPacket(CField.environmentChange("Dojang/clear", 5, 0));//was4
                        map.broadcastPacket(CField.environmentChange("dojang/end/clear", 12, 0));//was3
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

    public User getController() {
        if (controller != -1) {
            User chr = map.getCharacterById(controller);
            return chr;
        }
        return null;
    }

    public void setController(User chr) {
        if (chr != null) {
            this.controller = chr.getId();
        } else {
            this.controller = -1;
        }
    }

    public void switchController(User newController, boolean immediateAggro) {
        User controllers = getController();
        if (controllers == newController) {
            return;
        } else if (controllers != null) {
            controllers.stopControllingMonster(this);
            if (GameConstants.isAzwanMap(newController.getMapId())) {
                controllers.getClient().SendPacket(MobPacket.stopControllingMonster(this, true));
            } else {
                controllers.getClient().SendPacket(MobPacket.stopControllingMonster(this, false));
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

    public void sendStatus(ClientSocket client) {
        if (reflectpack != null) {
            client.SendPacket(reflectpack);
        }
        if (poisons.size() > 0) {
            poisonsLock.readLock().lock();
            try {
                client.SendPacket(MobPacket.applyMonsterStatus(this, poisons));
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
    public void sendSpawnData(ClientSocket client) {
        if (!isAlive()) {
            return;
        }
        if (GameConstants.isAzwanMap(client.getPlayer().getMapId())) {
            client.SendPacket(MobPacket.spawnMonster(this, fake && linkCID <= 0 ? -4 : -1, 0, true));
        } else {
            client.SendPacket(MobPacket.spawnMonster(this, fake && linkCID <= 0 ? -4 : -1, 0, false));
        }
        sendStatus(client);
        if (map != null && !stats.isEscort() && client.getPlayer() != null && client.getPlayer().getTruePosition().distanceSq(getTruePosition()) <= GameConstants.maxViewRangeSq_Half()) {
            map.updateMonsterController(this);
        }
    }

    @Override
    public void sendDestroyData(ClientSocket client) {
        if (stats.isEscort() && getEventInstance() != null && lastNode >= 0) { //shammos
            map.resetShammos(client);
        } else {
            if (GameConstants.isAzwanMap(client.getPlayer().getMapId())) {
                client.SendPacket(MobPacket.killMonster(getObjectId(), 0, true));
            } else {
                client.SendPacket(MobPacket.killMonster(getObjectId(), 0, false));
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
        User chr = getController();
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

    public void applyStatus(User from, MonsterStatusEffect status, boolean poison, long duration, boolean checkboss, StatEffect eff) {
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
        User con = getController();
        if (stat == MonsterStatus.POISON || stat == MonsterStatus.VENOMOUS_WEAPON) {
            poisonsLock.writeLock().lock();
            try {
                poisons.add(status);
                if (con != null) {
                    map.broadcastPacket(con, MobPacket.applyMonsterStatus(this, poisons), getTruePosition());
                    con.getClient().SendPacket(MobPacket.applyMonsterStatus(this, poisons));
                } else {
                    map.broadcastPacket(MobPacket.applyMonsterStatus(this, poisons), getTruePosition());
                }
            } finally {
                poisonsLock.writeLock().unlock();
            }
        } else {
            stati.put(stat, status);
            if (con != null) {
                map.broadcastPacket(con, MobPacket.applyMonsterStatus(this, status), getTruePosition());
                con.getClient().SendPacket(MobPacket.applyMonsterStatus(this, status));
            } else {
                map.broadcastPacket(MobPacket.applyMonsterStatus(this, status), getTruePosition());
            }
        }
    }

    public void applyStatus(MonsterStatusEffect status) { //ONLY USED FOR POKEMONN, ONLY WAY POISON CAN FORCE ITSELF INTO STATI.
        if (stati.containsKey(status.getStati())) {
            cancelStatus(status.getStati());
        }
        stati.put(status.getStati(), status);
        map.broadcastPacket(MobPacket.applyMonsterStatus(this, status), getTruePosition());
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
        User con = getController();
        if (reflection.size() > 0) {
            this.reflectpack = MobPacket.applyMonsterStatus(getObjectId(), effect, reflection, skill);
            if (con != null) {
                map.broadcastPacket(con, reflectpack, getTruePosition());
                con.getClient().SendPacket(this.reflectpack);
            } else {
                map.broadcastPacket(reflectpack, getTruePosition());
            }
        } else {
            for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
                if (con != null) {
                    map.broadcastPacket(con, MobPacket.applyMonsterStatus(getObjectId(), z.getKey(), z.getValue(), skill), getTruePosition());
                    con.getClient().SendPacket(MobPacket.applyMonsterStatus(getObjectId(), z.getKey(), z.getValue(), skill));
                } else {
                    map.broadcastPacket(MobPacket.applyMonsterStatus(getObjectId(), z.getKey(), z.getValue(), skill), getTruePosition());
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

    public void doPoison(MonsterStatusEffect status, WeakReference<User> weakChr) {
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
        User chr = weakChr.get();
        boolean cancel = damage <= 0 || chr == null || chr.getMapId() != map.getId();
        if (damage >= hp) {
            damage = hp - 1;
            cancel = !shadowWeb || cancel;
        }
        if (!cancel) {
            damage(chr, damage, false);
            if (shadowWeb) {
                map.broadcastPacket(MobPacket.damageMonster(getObjectId(), damage), getTruePosition());
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

    public void handleSteal(User chr) {
        double showdown = 100.0;
        MonsterStatusEffect mse = getBuff(MonsterStatus.SHOWDOWN);
        if (mse != null) {
            showdown += mse.getX();
        }

        Skill steal = SkillFactory.getSkill(4201004);
        final int level = chr.getTotalSkillLevel(steal);
        float chServerrate = ChannelServer.getInstance(chr.getClient().getChannel()).getDropRate(chr.getWorld());
        if (level > 0 && !getStats().isBoss() && stolen == -1 && steal.getEffect(level).makeChanceResult()) {
            MonsterInformationProvider mi = MonsterInformationProvider.getInstance();
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
        User con = getController();
        if (con != null) {
            map.broadcastPacket(con, MobPacket.cancelMonsterStatus(getObjectId(), stat), getTruePosition());
            con.getClient().SendPacket(MobPacket.cancelMonsterStatus(getObjectId(), stat));
        } else {
            map.broadcastPacket(MobPacket.cancelMonsterStatus(getObjectId(), stat), getTruePosition());
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
            User con = getController();
            if (con != null) {
                map.broadcastPacket(con, MobPacket.cancelPoison(this.getObjectId(), stat), getTruePosition());
                con.getClient().SendPacket(MobPacket.cancelPoison(this.getObjectId(), stat));
            } else {
                map.broadcastPacket(MobPacket.cancelPoison(this.getObjectId(), stat), getTruePosition());
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

    public OutPacket getNodePacket() {
        return nodepack;
    }

    public void setNodePacket(OutPacket np) {
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

    public void setTemporaryStat(MobTemporaryStat temporaryStat) {
        this.temporaryStat = temporaryStat;
    }

    public MobTemporaryStat getTemporaryStat() {
        return temporaryStat;
    }

    /**
     * Monster Meso Drop System
     * @author Mazen Massoud
     * 
     * @param pDropOwner
     * @purpose Request to drop bag of Mesos, used for Global Meso Drops without database clutter.
     */
    public void OnMesoDropRequest(User pDropOwner) {
        long nMobHP = getMobMaxHp();
        short nMobLV = stats.getLevel();
        if (nMobHP > 1000000000L) nMobHP = 1000000000L;                                             // Caps the the HP at this value for the calculation.
        
        int nMinRange = (int) (nMobHP / 250000) + (nMobLV * 3);                                     // Meso Drop Formula
        int nMaxRange = (int) Math.round(nMinRange * 1.25);                                         // Amount Meso Drop can randomize up to.
        int nResultMeso = (int) (nMinRange + (Math.random() * ((nMaxRange - nMinRange) + 1)));      // Formula to produce a value between the specified range.
        
        int nGainChance = 42;                                                                       // Base Meso Drop Chance %
        
        if (BuffedMob.OnBuffedChannel(Utility.requestChannel(pDropOwner.getId()))) {                // Increase Meso Rates from Buffed Monsters.
            if (!BuffedMob.BUFFED_BOSSES) {
                if (!getStats().isBoss()) {                                     
                    nResultMeso *= BuffedMob.MESO_BUFF;
                }
            } else {
                nResultMeso *= BuffedMob.MESO_BUFF;
            }
        }
        
        if(Utility.resultSuccess(nGainChance)) pDropOwner.getMap().spawnMesoDrop(nResultMeso, this.getTruePosition(), this, pDropOwner, false, (byte) 0);
    }
    
    /**
     * Monster NX Drop System
     * @author Mazen Massoud
     *
     * @param pPlayer
     * @param bKiller
     * @purpose Provide the player will NX upon killing a monster, 
     * with the value based on multiple factors.
     */
    public void OnNxGainRequest(User pPlayer, boolean bKiller) {

        long nMobHP = getMobMaxHp();
        short nMobLV = stats.getLevel();
        if (nMobHP > 800000000L) nMobHP = 800000000L;                                           // Caps the the HP at this value for the calculation.
        if (nMobLV < 60) nMobLV /= 3;
        
        int nMinRange = (int) (nMobHP / 550000) + (nMobLV * 3);                                 // NX Gain Formula
        int nMaxRange = (int) Math.round(nMinRange * 1.25);                                     // Amount NX Gain can go up to.
        int nResultNX = (int) (nMinRange + (Math.random() * ((nMaxRange - nMinRange) + 1)));    // Formula to produce a value between the specified range.

        int nGainChance = 15;                                                                   // Base NX Drop Chance %

        if (BuffedMob.OnBuffedChannel(Utility.requestChannel(pPlayer.getId()))) {               // Increase NX Rates from Buffed Monsters.
            if (!BuffedMob.BUFFED_BOSSES) {
                if (!getStats().isBoss()) {                                     
                    nResultNX *= BuffedMob.NX_BUFF;
                }
            } else {
                nResultNX *= BuffedMob.NX_BUFF;
            }
        }
        
        if (!bKiller) nResultNX *= 0.3;                                                         // Reduce amount gained if Player is not the killer of the mob.
        if (Utility.resultSuccess(nGainChance)) pPlayer.gainNX(nResultNX, true);                // Award the Player with the NX gained if chance succeeds.
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

        public void addDamage(User from, long damage, boolean updateAttackTime);

        public long getDamage();

        public boolean contains(User chr);

        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill, int killerCharId);
    }

    private class SingleAttackerEntry implements AttackerEntry {

        private long damage = 0;
        private final int chrid;
        private long lastAttackTime;

        public SingleAttackerEntry(User from) {
            this.chrid = from.getId();
        }

        @Override
        public void addDamage(User from, long damage, boolean updateAttackTime) {
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
        public boolean contains(User chr) {
            return chrid == chr.getId();
        }

        @Override
        public long getDamage() {
            return damage;
        }

        @Override
        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill, int killerCharId) {
            User pPlayer = map.getCharacterById(chrid);
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

        private Map<User, OnePartyAttacker> resolveAttackers() {
            Map<User, OnePartyAttacker> ret = new HashMap<>(attackers.size());
            for (Entry<Integer, OnePartyAttacker> aentry : attackers.entrySet()) {
                User chr = map.getCharacterById(aentry.getKey());
                if (chr != null) {
                    ret.put(chr, aentry.getValue());
                }
            }
            return ret;
        }

        @Override
        public boolean contains(User chr) {
            return attackers.containsKey(chr.getId());
        }

        @Override
        public long getDamage() {
            return totDamage;
        }

        @Override
        public void addDamage(User from, long damage, boolean updateAttackTime) {
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
            User highestAttacker = null;
            long iDamage, highestDamage = 0;
            int iexp;
            int fieldPartyMapBonusRate = 0; // bonus EXP rate acquired through a party play field
            double addedPartyLevel, levelMod, innerBaseExp;

            Map<User, ExpMap> expMap = new HashMap<>();
            byte classBonusExp;
            byte premiumBonusExp;

            Map<User, OnePartyAttacker> resolvedAttackers = resolveAttackers();

            // Loop through for every player in the monster's list of attacker
            for (Entry<User, OnePartyAttacker> attacker : resolvedAttackers.entrySet()) {
                MapleParty party = attacker.getValue().getLastKnownParty();

                addedPartyLevel = 0;
                classBonusExp = 0;
                premiumBonusExp = 0;
                List<User> expApplicable = new ArrayList<>();

                int partyZoneLeechableSize = 0;

                // Loop through every party member in this attacker's party, including itself
                for (MaplePartyCharacter partychar : party.getMembers()) {
                    int monsterLevel = stats.getLevel();

                    boolean isEligibleToLeech
                            = attacker.getKey().getLevel() - partychar.getLevel() <= ServerConstants.LEECH_LEVEL_RANGE
                            || monsterLevel - partychar.getLevel() <= ServerConstants.EXP_WITHIN_MONSTER; // between 40 of the monster

                    if (isEligibleToLeech) {
                        User pchr = map.getCharacterById(partychar.getId());

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

                for (User expReceiver : expApplicable) {
                    iexp = expMap.get(expReceiver) == null ? 0 : expMap.get(expReceiver).exp;
                    levelMod = expReceiver.getLevel() / addedPartyLevel * ServerConstants.ATTACKER_EXP_RATIO;
                    iexp += (int) Math.round(((attacker.getKey().getId() == expReceiver.getId() ? ServerConstants.LEECHER_EXP_RATIO : 0.0) + levelMod) * innerBaseExp);

                    expMap.put(expReceiver, new ExpMap(iexp, (byte) expApplicable.size(), classBonusExp, premiumBonusExp, fieldPartyMapBonusRate));
                }
            }

            // Party bonus
            int partybonusMultiplier = calculatePartyBonusEXP(resolvedAttackers.size(), expMap.size()); // bonus EXP in percentage

            // Burning field bonus
            final int burningFieldBonusEXP = getMap().getBurningFieldBonusEXP();

            // Give Exp
            for (Entry<User, ExpMap> expReceiver : expMap.entrySet()) {
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
