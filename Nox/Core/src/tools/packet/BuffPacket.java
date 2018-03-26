package tools.packet;

import java.awt.Point;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import client.CharacterTemporaryStat;
import client.CharacterTemporaryStat.TSIndex;
import client.CharacterTemporaryStatValueHolder;
import client.MapleDisease;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import service.SendPacketOpcode;
import provider.data.HexTool;
import net.OutPacket;
import net.Packet;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Randomizer;
import server.life.MobSkill;
import server.maps.objects.MapleCharacter;
import tools.LogHelper;
import tools.Pair;

/**
 *
 * @author Novak ~De meester der buffs~
 *
 */
public class BuffPacket {

    public static Packet giveDice(int buffid, int skillid, int duration, Map<CharacterTemporaryStat, Integer> statups) {
        if (ServerConstants.DEVELOPER_PACKET_DEBUG_MODE) {
            System.out.println("[Debug] Call Location (" + new java.lang.Throwable().getStackTrace()[0] + ")");
        }
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());

        PacketHelper.writeBuffMask(oPacket, statups);
        oPacket.EncodeShort(buffid);
        oPacket.EncodeInteger(skillid);
        oPacket.EncodeInteger(duration);
        oPacket.Fill(0, 5);

        switch (buffid) { // Die Roll
            case 2: // +30% Weapon Defense
                statups.put(CharacterTemporaryStat.PDD, 30);
                break;
            case 3: // +20% Max HP & MP
                statups.put(CharacterTemporaryStat.MaxHP, 20);
                statups.put(CharacterTemporaryStat.MaxMP, 20);
                break;
            case 4: // +15% Critical Chance Rate
                statups.put(CharacterTemporaryStat.IndieCr, 15);
                break;
            case 5: // +20% Damage
                statups.put(CharacterTemporaryStat.PAD, 20);
                statups.put(CharacterTemporaryStat.MAD, 20);
                break;
            case 6: // +30% EXP Gain
                statups.put(CharacterTemporaryStat.IndieEXP, 30);
                break;
        }

        /*
         * 2 : 30
         * 3 : 20, 20
         * 4 : 15
         * 5 : 20
         * 6 : 30
         *
         */
        oPacket.EncodeInteger(buffid == 3 ? 30 : 0); //MAX HP
        oPacket.EncodeInteger(buffid == 3 ? 30 : 0); //MAX MP
        oPacket.EncodeInteger(buffid == 4 ? 15 : 0); //CRITICAL RATE
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(buffid == 2 ? 30 : 0); //Physical Defense
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(buffid == 5 ? 20 : 0); //Increase Damage
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(buffid == 6 ? 30 : 0); //Increase EXP Rate
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0); //1.2.214+
        oPacket.EncodeInteger(0);
        oPacket.Encode(1);
        oPacket.EncodeInteger(0);
        return oPacket.ToPacket();
    }

    /*public static Packet giveDice(int diceId, int skillid, int duration, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
        int diceVal = (Math.max(diceId / 100, Math.max(diceId / 10, diceId % 10))); // 1-6
        return giveBuff(null, skillid, duration, statups, effect, diceId, diceVal, 0, 0);
    }*/
    public static Packet giveEnergyCharged(MapleCharacter chr, int bar, int skillid, int bufflength) {
        final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
        stat.put(CharacterTemporaryStat.EnergyCharged, bar);
        return giveBuff(chr, skillid, bufflength, stat, null, 0, 0, 0, 0);
    }

    private static void clearFakeBuffstats(Map<CharacterTemporaryStat, Integer> statups) {
        //Replace fake stats with a real one so that the buff icon can appear.
        if (statups.containsKey(CharacterTemporaryStat.SUMMON)) {
            statups.remove(CharacterTemporaryStat.SUMMON);
            statups.put(CharacterTemporaryStat.MAD, 0);
        }

        if (statups.containsKey(CharacterTemporaryStat.PUPPET)) {
            statups.remove(CharacterTemporaryStat.PUPPET);
            statups.put(CharacterTemporaryStat.MAD, 0);
        }

        if (statups.containsKey(CharacterTemporaryStat.REAPER)) {
            statups.remove(CharacterTemporaryStat.REAPER);
            statups.put(CharacterTemporaryStat.MAD, 0);
        }
    }

    private static void clearFakeBuffstats(List<CharacterTemporaryStat> statups) {
        //Replace fake stats with a real one so that the buff icon can appear.
        if (statups.contains(CharacterTemporaryStat.SUMMON)) {
            statups.remove(CharacterTemporaryStat.SUMMON);
        }

        if (statups.contains(CharacterTemporaryStat.PUPPET)) {
            statups.remove(CharacterTemporaryStat.PUPPET);
        }

        if (statups.contains(CharacterTemporaryStat.REAPER)) {
            statups.remove(CharacterTemporaryStat.REAPER);
        }
    }

    /**
     * Guides the Encoding process of buffs, flagging certain specific status changes.
     *
     * @param chr - The character the buff gets applied to
     * @param buffid - The skillId of the buff
     * @param bufflength - The duration of the buff
     * @param statups - The Buffstats and effects that have to be applied
     * @param effect - The status changes that occur with the buff
     * @return The encoded buff packet.
     */
    public static Packet giveBuff(MapleCharacter chr, int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
        return giveBuff(chr, buffid, bufflength, statups, effect, 0, 0, 0, 0);
    }

    public static Packet giveBuff(MapleCharacter chr, int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect, int diceRange, int diceId, int lightGauge, int darkGauge) {
        OutPacket oPacket = new OutPacket(80);

        BuffPacket.clearFakeBuffstats(statups);
        boolean bEndecode4Byte = false, bMovementAffecting = false;
        for (Map.Entry<CharacterTemporaryStat, Integer> stat : statups.entrySet()) {
            System.out.println(stat.getKey().name());
            if (CharacterTemporaryStat.isEnDecode4Byte(stat.getKey())) {
                bEndecode4Byte = true;
            }
            if (CharacterTemporaryStat.isMovementAffectingStat(stat.getKey())) {
                bMovementAffecting = true;
            }
        }

        oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());

        PacketHelper.writeBuffMask(oPacket, statups);

        encodeForLocal(oPacket, chr, buffid, bufflength, statups, effect, diceRange, diceId, lightGauge, darkGauge, bEndecode4Byte);

        oPacket.EncodeShort(0);// tDelay
        oPacket.Encode(0);//unsure, seems unimportant
        oPacket.Encode(0);//bJustBuffCheck
        oPacket.Encode(0);//bFirstSet

        if (bMovementAffecting) {
            oPacket.Encode(0);
        }

        //todo: if (newFireBomb) write(0);
        return oPacket.ToPacket();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> SortCTS(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Encodes buffstats and status effects to send to the client in order for it to display the buff effect.
     *
     * @param nBuffID - The skillId of the buff
     * @param tDuration - The duration of the buff
     * @param mTemporaryStats - The Buffstats and effects that have to be applied
     * @param pEffect - The status changes that occur with the buff
     * @return The encoded buff packet.
     */
    private static void encodeForLocal(OutPacket oPacket, MapleCharacter pPlayer, int nBuffID, int tDuration,
            Map<CharacterTemporaryStat, Integer> mTemporaryStats, MapleStatEffect pEffect, int diceRange,
            int diceId, int lightGauge, int darkGauge, boolean bEndecode4Byte) {

        mTemporaryStats = SortCTS(mTemporaryStats);
        for (Map.Entry<CharacterTemporaryStat, Integer> stat : mTemporaryStats.entrySet()) {
            if (stat.getKey().getFlag() <= CharacterTemporaryStat.Stigma.getFlag()
                    && stat.getKey().getFlag() >= CharacterTemporaryStat.PAD.getFlag()) {
                if (bEndecode4Byte) {
                    oPacket.EncodeInteger(stat.getValue());
                } else {
                    oPacket.EncodeShort(stat.getValue());
                }
                oPacket.EncodeInteger(nBuffID);
                oPacket.EncodeInteger(tDuration > 2000000000 ? 0 : tDuration);
            }
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.SoulMP)) { //SoulMP
            oPacket.EncodeInteger(pEffect.getX());//xSoulMP
            oPacket.EncodeInteger(0);//rSoulMP
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.FullSoulMP)) { //FullSoulMP
            oPacket.EncodeInteger(pEffect.getX());//xFullSoulMP
        }

        int ammount = 0;
        oPacket.EncodeShort(ammount);
        for (int i = 0; i < ammount; i++) {
            oPacket.EncodeInteger(0);//Key
            oPacket.Encode(0);//Key --> mBuffedForSpecMap
        }

        oPacket.Encode(pPlayer.getBuffedValue(CharacterTemporaryStat.DefenseAtt) == null ? 0
                : pPlayer.getBuffedValue(CharacterTemporaryStat.DefenseAtt));//nDefenseAtt
        oPacket.Encode(pPlayer.getBuffedValue(CharacterTemporaryStat.DefenseState) == null ? 0
                : pPlayer.getBuffedValue(CharacterTemporaryStat.DefenseState));//nDefenseState
        oPacket.Encode(pEffect == null ? 0 : pEffect.getPVPDamage());//nPVPDamage

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Dice)) {
            //You can loop, or just write the dice stats + zeros. It has to add up to 88 bytes.
            //for (int j = 0; j < 22; ++j)) {
            //  oPacket.EncodeInteger(GameConstants.getDiceStat(buffid, 3));//aDiceInfo
            //}
            oPacket.EncodeInteger(GameConstants.getDiceStat(diceId, 3));
            oPacket.EncodeInteger(GameConstants.getDiceStat(diceId, 3));
            oPacket.EncodeInteger(GameConstants.getDiceStat(diceId, 4));
            oPacket.Fill(0, 20);
            oPacket.EncodeInteger(GameConstants.getDiceStat(diceId, 2));
            oPacket.Fill(0, 12);
            oPacket.EncodeInteger(GameConstants.getDiceStat(diceId, 5));
            oPacket.Fill(0, 16);
            oPacket.EncodeInteger(GameConstants.getDiceStat(diceId, 6));
            oPacket.Fill(0, 16);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.KillingPoint)) {//KillingPoint
            oPacket.Encode(1);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.PinkbeanRollingGrade)) {//PinkBeanRollingGrade
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Judgement)) {//JudgeMent (IDK IF ITS DRAW)
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.StackBuff)) {//StackBuff
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Trinity)) {//Trinity
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.ElementalCharge)) { //ElementalCharge
            oPacket.Encode(pEffect.getLevel());//nElementalCharge
            oPacket.EncodeShort(pEffect.getW());//wElementalCharge
            oPacket.Encode(pEffect.getU());//uElementalCharge
            oPacket.Encode(pEffect.getZ());//zElementalCharge
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.LifeTidal)) {//LifeTidal
            int newTidal = diceRange; //no need for another new var, so we're storing it here for this buffstat.                    
            oPacket.EncodeInteger(newTidal);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.AntiMagicShell)) {//AntiMagicShell
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Larkness)) { //Larkness
            for (int k = 0; k < 2; k++) {
                encodeLuminousEquilibriumInfo(oPacket, nBuffID);
            }
            oPacket.EncodeInteger(lightGauge);// dgLarkness (Light Gauge)
            oPacket.EncodeInteger(darkGauge);// lgLarkness (Dark Gauge)

            // GMS Additional Bytes
            oPacket.EncodeInteger(1);// 1 (according to old packet)
            oPacket.EncodeInteger(1);// 1 (according to old packet)
            oPacket.EncodeInteger(283183599);// 283183599 (according to old packet)
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.IgnoreTargetDEF)) {//IgnoreTargetDef?
            oPacket.EncodeInteger(1); // Originally oPacket.EncodeInteger(0);
        }

        //TEMPEST_BLADES are ProjectileAtoms like the above StopForceAtom. 
        //I'm unsure if they share this buffstat among jobs, since only kaiser actualy writes the weapons too.
        //For stuff like xenon it might be the ID of the sprite though..
        if (mTemporaryStats.containsKey(CharacterTemporaryStat.StopForceAtomInfo)) {
            List<Integer> ss = new ArrayList<>();
            int type = 1;
            switch (pEffect.getSourceId()) {
                case 61101002:
                    type = 1;
                    break;
                case 61120007:
                    type = 2;
                    break;
                case 61111008:
                    type = 3;
                    break;
                case 61120008:
                    type = 4;
                    break;
            }
            int count = pEffect.getSourceId() == 61101002 ? 3 : 5;
            for (int j = 0; j < count; j++) {
                ss.add(0);
            }

            oPacket.EncodeInteger(type);
            oPacket.EncodeInteger(count);
            oPacket.EncodeInteger(pEffect.getWeapon());
            oPacket.EncodeInteger(ss.size()); // Size
            for (int i = 0; i < ss.size(); i++) {
                oPacket.EncodeInteger(ss.get(i));
            }
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.SmashStack)) {//SmashStack
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.MobZoneState)) { //MobZoneState
            oPacket.EncodeInteger(0);//if > 0 the client loops through it reading nothing else.
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Slow)) {//Slow
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.IceAura)) {//IceAura
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.KnightsAura)) {//KnightsAura
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.IgnoreMobpdpR)) {//IgnoreMobpdpR
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.BdR)) {//Bdr
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.DropRIncrease)) {
            oPacket.EncodeInteger(pEffect.getX());//xDropIncrease
            oPacket.Encode(0);//bDropIncrease
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.PoseType)) {//PoseType
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Beholder)) { //Beholder
            oPacket.EncodeInteger(pEffect.getS());//sBeholder
            oPacket.EncodeInteger(0);//ssBeholder
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.CrossOverChain)) {//CrossOverChain
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Reincarnation)) {//Reincarnation
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.ExtremeArchery)) { //ExtremeArchery
            oPacket.EncodeInteger(0);//bExtremeArchery
            oPacket.EncodeInteger(pEffect.getX());//xExtremeArchery
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.QuiverCatridge)) {//QuiverCartridge?
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.ImmuneBarrier)) {//ImmuneBarrier
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.ZeroAuraStr)) {//ZeroAuraStr
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.ZeroAuraSpd)) {//ZeroAuraSpd
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.CriticalGrowing)) {//CriticalGrowing
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.ArmorPiercing)) {//ArmorPiercing
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.SharpEyes)) {//SharpEyes
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.AdvancedBless)) {//AdvancedBless
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.DotHealHPPerSecond)) {//DotHealHPPerSecond
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.SpiritGuard)) {//SpiritGuard --IDK if this buffstat is correct (ward != guard)
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.KnockBack)) {//KnockBack
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.ShieldAttack)) {//ShieldAttack
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.SSFShootingAttack)) {//SFFShootingAttack?
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.BMageAura)) { //BMageAura
            oPacket.EncodeInteger(pEffect.getX());//xBMageAura
            oPacket.Encode(0);//bBMageAura
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.BattlePvP_Helena_Mark)) {//BattlePvP_Helena_Mark
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.PinkbeanAttackBuff)) {//PinkBeanAttackBuff
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.RoyalGuardState)) { //RoyalGuardState
            oPacket.EncodeInteger(0);//bRoyalGuardState
            oPacket.EncodeInteger(pEffect.getX());//xRoyalGuardState
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.MichaelSoulLink)) { //MichaelSoulLink
            oPacket.EncodeInteger(pEffect.getX());//xMichaelSoulLink
            oPacket.Encode(0);//bMichaelSoulLink
            oPacket.EncodeInteger(0);//cMichaelSoulLink
            oPacket.EncodeInteger(pEffect.getY());//yMichaelSoulLink
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.AdrenalinBoost)) {//AdrenalinBoost
            oPacket.Encode(1); // Was a zero byte, but the byte needs a value of 1 to allow finishers.
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.RWCylinder)) { //RWCylinder
            oPacket.Encode(pPlayer.getPrimaryStack());//bRWCylinder - Ammo
            oPacket.EncodeShort(pPlayer.getAdditionalStack());//cRWCylinder - Gauge
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.RWMagnumBlow)) { //RWMagnumBlow
            oPacket.EncodeShort(0);//bRWMagnumBlow
            oPacket.Encode((byte) pEffect.getX());//xRWMagnumBlow
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.EnergyCharged)) {
            oPacket.EncodeInteger(nBuffID);
        } else {
            oPacket.EncodeInteger(0);//nViperEnergyCharged
        }
        
        if (mTemporaryStats.containsKey(CharacterTemporaryStat.HayatoStance)) { 
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }
        
        if (mTemporaryStats.containsKey(CharacterTemporaryStat.HayatoStanceBonus)) { 
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.BladeStance)) { //BladeStance -THINK ITS THE HAYATO ONE, IDK DO
            oPacket.Encode(1);
            oPacket.EncodeInteger(2);
        }
        
        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Battoujutsu)) { 
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }
        
        if (mTemporaryStats.containsKey(CharacterTemporaryStat.DarkSight)) { //DarkSight
            oPacket.EncodeInteger(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.BasicStatUp)) { //unofficial..
            oPacket.Encode(0);
        }

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.Stigma)) { //Stigma
            oPacket.EncodeInteger(0);
        }

        encodeTwoStateTemporaryStat(oPacket, pPlayer, nBuffID, tDuration, mTemporaryStats, pEffect);
        encodeIndieTempStat(oPacket, nBuffID, tDuration, mTemporaryStats);

        if (mTemporaryStats.containsKey(CharacterTemporaryStat.UsingScouter)) {
            oPacket.EncodeInteger(0);
        }
    }

    /**
     * Encodes IndieTemp status changes. IndieTempStats are defined by Buffstats in the 16th int. (or simply usually the last int)
     *
     */
    private static void encodeIndieTempStat(OutPacket oPacket, int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups) {
        int tTime = (int) System.currentTimeMillis();
        for (Map.Entry<CharacterTemporaryStat, Integer> effect : statups.entrySet()) {
            if (effect.getKey().isIndie()) {
                oPacket.EncodeInteger(1);
                oPacket.EncodeInteger(buffid);//reason = skillid
                oPacket.EncodeInteger(effect.getValue());//v7
                oPacket.EncodeInteger(tTime);//nKey
                oPacket.EncodeInteger(tTime + 1);//v8
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(0);
            }
        }
        int v10 = 0;
        oPacket.EncodeInteger(0);
        if (v10 > 0) {
            for (int j = 0; j < v10; j++) {
                oPacket.EncodeInteger(0);//nMValueKey
                oPacket.EncodeInteger(0);//nMValue
            }
        }
    }

    /**
     * Encodes TwoState status changes. These states are always applied to any character.
     *
     */
    private static void encodeTwoStateTemporaryStat(OutPacket oPacket, MapleCharacter chr, int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
        if (statups.containsKey(CharacterTemporaryStat.EnergyCharged)) {
            oPacket.EncodeInteger(statups.get(CharacterTemporaryStat.EnergyCharged));//Value
            oPacket.EncodeInteger(buffid);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(1);//tCur
        }
        if (statups.containsKey(CharacterTemporaryStat.DashSpeed)) {
            oPacket.EncodeInteger(statups.get(CharacterTemporaryStat.DashSpeed));//Value
            oPacket.EncodeInteger(buffid);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(0);
            oPacket.EncodeInteger(bufflength);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        }
        if (statups.containsKey(CharacterTemporaryStat.DashJump)) {
            oPacket.EncodeInteger(statups.get(CharacterTemporaryStat.DashJump));//Value
            oPacket.EncodeInteger(buffid);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(0);
            oPacket.EncodeInteger(bufflength);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        }
        if (statups.containsKey(CharacterTemporaryStat.RideVehicle)) {
            System.out.println("RIDEVEHICLE:: " + statups.get(CharacterTemporaryStat.RideVehicle));
            oPacket.EncodeInteger(statups.get(CharacterTemporaryStat.RideVehicle));//Value(MountID)
            oPacket.EncodeInteger(buffid);//Reason(SkillID)
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(1);//tCur
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }
        if (statups.containsKey(CharacterTemporaryStat.PartyBooster)) {
            oPacket.EncodeInteger(statups.get(CharacterTemporaryStat.PartyBooster));//Value
            oPacket.EncodeInteger(buffid);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(0);
            oPacket.EncodeInteger(0);//tCur
            //EncodeTime(tCurrentTime)
            oPacket.Encode(0);
            oPacket.EncodeInteger(bufflength);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        }
        if (statups.containsKey(CharacterTemporaryStat.GuidedBullet)) {
            oPacket.EncodeInteger(statups.get(CharacterTemporaryStat.GuidedBullet));//Value
            oPacket.EncodeInteger(buffid);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(0);
            oPacket.EncodeInteger(bufflength);//tCur
            oPacket.EncodeInteger(0);//dwMobID
            oPacket.EncodeInteger(chr.getId());//dwUserID 
        }
        if (statups.containsKey(CharacterTemporaryStat.Undead)) {
            oPacket.EncodeInteger(statups.get(CharacterTemporaryStat.Undead));//Value
            oPacket.EncodeInteger(buffid);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(0);
            oPacket.EncodeInteger(bufflength);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        }
        if (statups.containsKey(CharacterTemporaryStat.RideVehicleExpire)) {
            oPacket.EncodeInteger(statups.get(CharacterTemporaryStat.RideVehicleExpire));//Value
            oPacket.EncodeInteger(buffid);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(0);
            oPacket.EncodeInteger(bufflength);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        }
    }

    /**
     * Encodes TwoState status changes. These states are always applied to any character.
     *
     */
    private static void encodeRemoteTwoStateTemporaryStat(OutPacket oPacket, MapleCharacter chr) {
        int nullValueTCur = Randomizer.nextInt();
        if (chr.getBuffSource(CharacterTemporaryStat.EnergyCharged) != -1 && chr.getBuffedValue(CharacterTemporaryStat.EnergyCharged) != null) {
            oPacket.EncodeInteger(chr.getBuffedValue(CharacterTemporaryStat.EnergyCharged));//Value
            oPacket.EncodeInteger(chr.getBuffSource(CharacterTemporaryStat.EnergyCharged));//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(chr.getBuffDuration(CharacterTemporaryStat.EnergyCharged));//tCur
        } else {
            oPacket.EncodeInteger(0);//Value
            oPacket.EncodeInteger(0);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
        }

        if (chr.getBuffSource(CharacterTemporaryStat.DashSpeed) != -1 && chr.getBuffedValue(CharacterTemporaryStat.DashSpeed) != null) {
            oPacket.EncodeInteger(chr.getBuffedValue(CharacterTemporaryStat.DashSpeed));//Value
            oPacket.EncodeInteger(chr.getBuffSource(CharacterTemporaryStat.DashSpeed));//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        } else {
            oPacket.EncodeInteger(0);//Value
            oPacket.EncodeInteger(0);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        }

        if (chr.getBuffSource(CharacterTemporaryStat.DashJump) != -1 && chr.getBuffedValue(CharacterTemporaryStat.DashJump) != null) {
            oPacket.EncodeInteger(chr.getBuffedValue(CharacterTemporaryStat.DashJump));//Value
            oPacket.EncodeInteger(chr.getBuffSource(CharacterTemporaryStat.DashJump));//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        } else {
            oPacket.EncodeInteger(0);//Value
            oPacket.EncodeInteger(0);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        }

        if (chr.getBuffSource(CharacterTemporaryStat.RideVehicle) != -1 && chr.getBuffedValue(CharacterTemporaryStat.RideVehicle) != null) {
            oPacket.EncodeInteger(chr.getBuffedValue(CharacterTemporaryStat.RideVehicle));//Value(MountID)
            oPacket.EncodeInteger(chr.getBuffSource(CharacterTemporaryStat.RideVehicle));//Reason(SkillID)
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
        } else {
            oPacket.EncodeInteger(0);//Value
            oPacket.EncodeInteger(0);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
        }

        if (chr.getBuffSource(CharacterTemporaryStat.PartyBooster) != -1 && chr.getBuffedValue(CharacterTemporaryStat.PartyBooster) != null) {
            oPacket.EncodeInteger(chr.getBuffedValue(CharacterTemporaryStat.PartyBooster));//Value
            oPacket.EncodeInteger(chr.getBuffSource(CharacterTemporaryStat.PartyBooster));//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.Encode(0);
            oPacket.EncodeInteger(0);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        } else {
            oPacket.EncodeInteger(0);//Value
            oPacket.EncodeInteger(0);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.Encode(0);
            oPacket.Encode(HexTool.getByteArrayFromHexString("86 39 95 7D"));
            oPacket.EncodeShort(0);//usExpireTerm
        }

        if (chr.getBuffSource(CharacterTemporaryStat.GuidedBullet) != -1 && chr.getBuffedValue(CharacterTemporaryStat.GuidedBullet) != null) {
            oPacket.EncodeInteger(chr.getBuffedValue(CharacterTemporaryStat.GuidedBullet));//Value
            oPacket.EncodeInteger(chr.getBuffSource(CharacterTemporaryStat.GuidedBullet));//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeInteger(0);//dwMobID
            oPacket.EncodeInteger(chr.getId());//dwUserID (Note: This isn't in v90, but since it's in KMS it's likely in your version. it's new)
        } else {
            oPacket.EncodeInteger(0);//Value
            oPacket.EncodeInteger(0);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }

        if (chr.getBuffSource(CharacterTemporaryStat.Undead) != -1 && chr.getBuffedValue(CharacterTemporaryStat.Undead) != null) {
            oPacket.EncodeInteger(chr.getBuffedValue(CharacterTemporaryStat.Undead));//Value
            oPacket.EncodeInteger(chr.getBuffSource(CharacterTemporaryStat.Undead));//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        } else {
            oPacket.EncodeInteger(0);//Value
            oPacket.EncodeInteger(0);//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeShort(0);
        }
        if (chr.getBuffSource(CharacterTemporaryStat.RideVehicleExpire) != -1 && chr.getBuffedValue(CharacterTemporaryStat.RideVehicleExpire) != null) {
            oPacket.EncodeInteger(chr.getBuffedValue(CharacterTemporaryStat.RideVehicleExpire));//Value
            oPacket.EncodeInteger(chr.getBuffSource(CharacterTemporaryStat.RideVehicleExpire));//Reason
            //EncodeTime(tLastUpdated)
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeShort(0);//usExpireTerm
        } else {
            oPacket.EncodeInteger(0);//Value
            oPacket.EncodeInteger(0);//Reason
            //EncodeTime(tLastUpdated)	
            oPacket.Encode(1);
            oPacket.EncodeInteger(nullValueTCur);//tCur
            oPacket.EncodeShort(0);
        }
    }

    /**
     * Encodes buff values for luminous equilibrium changing skills.
     *
     */
    private static void encodeLuminousEquilibriumInfo(OutPacket oPacket, int buffid) {//Nexon's EncodeLarknessInfo
        //both ints are used for randomizing clientsided
        oPacket.EncodeInteger(buffid);//skill?
        oPacket.EncodeInteger(483195070);//duration?
    }

    private static void encodeProjectileAtoms(OutPacket oPacket) {
        encodeProjectileAtoms(oPacket, 0, 0, 0);
    }

    /**
     * Encodes projectiles that sit around the player and can target mobs when executed a second time.
     *
     * @param Idx - Skill level index for kaiser. Either 1 or 2.
     * @param Count - The amount of projectiles to be created.
     * @param WeaponId - The ID of the weapon to be copied as projectile sprite. (if applicable)
     */
    private static void encodeProjectileAtoms(OutPacket oPacket, int Idx, int Count, int WeaponId) {
        oPacket.EncodeInteger(Idx);//nIdx
        oPacket.EncodeInteger(Count);//nCount
        oPacket.EncodeInteger(WeaponId);//nWeaponId

        //TODO: code targeting system
        int lockedOnProjectiles = 0;
        oPacket.EncodeInteger(lockedOnProjectiles);

        for (int i = 0; i < lockedOnProjectiles; i++) {
            short targetX = 0;
            short targetY = 0;
            oPacket.EncodePosition(new Point(targetX, targetY));
            //turns out it's prolly not a pos but just je objectId
        }
    }

    public static Packet giveDebuff(MapleDisease statups, MobSkill skill) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
        PacketHelper.writeSingleMask(oPacket, statups);
        oPacket.EncodeShort(skill.getX());
        oPacket.EncodeShort(skill.getSkillId());
        oPacket.EncodeShort(skill.getSkillLevel());
        oPacket.EncodeShort(skill.getSkillLevel());
        oPacket.EncodeShort((int) (skill.getDuration() / 500));////duration
        oPacket.EncodeShort(0);//effectDelay
        //oPacket.Encode(1);
        oPacket.Encode(0);
        //oPacket.Encode(1);
        oPacket.Fill(0, 30);
        //System.out.println(HexTool.toString(oPacket.getPacket()));
        return oPacket.ToPacket();
    }

    public static Packet cancelBuff(List<CharacterTemporaryStat> statups) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.TemporaryStatReset.getValue());

        BuffPacket.clearFakeBuffstats(statups);
        PacketHelper.writeMask(oPacket, statups);
        for (CharacterTemporaryStat z : statups) {
            if (z.isIndie()) {
                oPacket.EncodeInteger(0);
            }
        }

        oPacket.Encode(3);
        oPacket.Encode(1);
        oPacket.EncodeLong(0L);
        oPacket.EncodeLong(0L);
        oPacket.EncodeLong(0L);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet cancelDebuff(MapleDisease mask) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.TemporaryStatReset.getValue());

        PacketHelper.writeSingleMask(oPacket, mask);
        oPacket.Encode(3);
        oPacket.Encode(1);
        oPacket.EncodeLong(0);
        oPacket.Encode(0);//v112
        return oPacket.ToPacket();
    }

    public static Packet cancelHoming() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.TemporaryStatReset.getValue());

        //PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.HOMING_BEACON);
        oPacket.Encode(0);//v112

        return oPacket.ToPacket();
    }

    public static Packet giveForeignBuff(int cid, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserTemporaryStatSet.getValue());
        oPacket.EncodeInteger(cid);

        PacketHelper.writeBuffMask(oPacket, statups);
        for (Map.Entry<CharacterTemporaryStat, Integer> statup : statups.entrySet()) {
            switch (effect.getSourceId()) {
                case 36111006:
                case 30021237:
                    oPacket.EncodeShort(1394);
                    break;
                case 65101002:
                    oPacket.EncodeShort(1000);
                    break;
                case 61111008:
                case 61120008:
                case 61121053:
                    oPacket.Encode(0x0A);
                    oPacket.EncodeShort(1201);
                    break;
                default:
                    oPacket.EncodeShort(statup.getValue().shortValue());
                    break;
            }
            if (statup.getKey() == CharacterTemporaryStat.Speed || statup.getKey() == CharacterTemporaryStat.ComboCounter) {
                oPacket.Encode(statup.getValue().byteValue());
            } else if (statup.getKey() == CharacterTemporaryStat.BMageAura || statup.getKey() == CharacterTemporaryStat.ShadowPartner || statup.getKey() == CharacterTemporaryStat.ShadowServant || statup.getKey() == CharacterTemporaryStat.Mechanic) {
                oPacket.EncodeShort(statup.getValue().shortValue());
                oPacket.EncodeInteger(effect.getSourceId());
            } else if (statup.getKey() == CharacterTemporaryStat.ItemCritical) {
                oPacket.EncodeInteger(statup.getValue());
                oPacket.EncodeInteger(effect.getCharColor());
            } else if (statup.getKey() == CharacterTemporaryStat.Speed) {

            } else if (effect.getSourceId() == 32120000 || effect.getSourceId() == 32001003 || effect.getSourceId() == 32110000 || effect.getSourceId() == 32111012 || effect.getSourceId() == 32120001 || effect.getSourceId() == 32101003) { //오라
                oPacket.EncodeLong(0);
                oPacket.EncodeLong(0);
                oPacket.Fill(0, 5);
            } else {
                oPacket.EncodeShort(statup.getValue().shortValue());
            }
        }
        oPacket.EncodeShort(1);
        oPacket.Encode(0);
        oPacket.EncodeInteger(2);
        oPacket.Fill(0, 13);
        oPacket.EncodeShort(600);

        oPacket.Fill(0, 20);

        return oPacket.ToPacket();
    }

    /*public static Packet giveForeignBuff(MapleCharacter chr, int cid, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserTemporaryStatSet.getValue());
        oPacket.EncodeInteger(cid);
        PacketHelper.writeBuffMask(oPacket, statups);

        encodeForLocal(oPacket, chr, 0, 0, statups, effect, 0, 0, 0, 0);

        boolean containsMovementAffectingBuffstat = false;
        for (Map.Entry<CharacterTemporaryStat, Integer> stat : statups.entrySet()) {
            if (CharacterTemporaryStat.isMovementAffectingStat(stat.getKey())) {
                containsMovementAffectingBuffstat = true;
            }
        }

        oPacket.EncodeShort(0);
        oPacket.Encode(0);//unsure, seems unimportant
        oPacket.Encode(0);//bJustBuffCheck
        oPacket.Encode(0);//bFirstSet

        if (containsMovementAffectingBuffstat) {
            oPacket.Encode(0);
        }
        return oPacket.ToPacket();
    }*/
    public static Packet giveForeignDebuff(int cid, final MapleDisease statups, MobSkill skill) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserTemporaryStatSet.getValue());
        oPacket.EncodeInteger(cid);

        PacketHelper.writeSingleMask(oPacket, statups);
        if (skill.getSkillId() == 125) {
            oPacket.EncodeShort(0);
            oPacket.Encode(0); //todo test
        }
        oPacket.EncodeShort(skill.getX());
        oPacket.EncodeShort(skill.getSkillId());
        oPacket.EncodeShort(skill.getSkillLevel());
        oPacket.EncodeShort((int) skill.getDuration() / 500); // same as give_buff
        oPacket.EncodeShort(0);//effectDelay
        oPacket.Encode(1);
        oPacket.Encode(1);
        oPacket.Encode(0);//v112
        oPacket.Fill(0, 20);
        return oPacket.ToPacket();
    }

    public static Packet cancelForeignBuff(int cid, List<CharacterTemporaryStat> statups) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserTemporaryStatReset.getValue());
        oPacket.EncodeInteger(cid);
        PacketHelper.writeMask(oPacket, statups);
        oPacket.Encode(3);
        oPacket.Encode(1);
        oPacket.Encode(0);
        oPacket.Fill(0, 20);

        return oPacket.ToPacket();
    }

    public static Packet cancelForeignRiding(int cid, List<CharacterTemporaryStat> statups) {
        OutPacket packet = new OutPacket(80);
        packet.EncodeShort(SendPacketOpcode.UserTemporaryStatReset.getValue());
        packet.EncodeInteger(cid);
        packet.EncodeLong(CharacterTemporaryStat.RideVehicle.getValue());
        packet.EncodeLong(0);
        packet.EncodeLong(0);
        packet.EncodeLong(0);
        packet.EncodeLong(0); // v181
        packet.Encode(1);
        return packet.ToPacket();
    }

    public static Packet cancelForeignDebuff(int cid, MapleDisease mask) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserTemporaryStatReset.getValue());
        oPacket.EncodeInteger(cid);
        PacketHelper.writeSingleMask(oPacket, mask);//48 bytes
        oPacket.Encode(1);
        return oPacket.ToPacket();
    }

    public static Packet giveCard(int cid, int oid, int skillid) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.Encode(0);
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(skillid);
        oPacket.Encode(1);
        oPacket.EncodeInteger(2);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(21);
        oPacket.EncodeInteger(8);
        oPacket.EncodeInteger(8);
        oPacket.Encode(0);
        return oPacket.ToPacket();
    }

    public static void encodeForRemote(OutPacket oPacket, MapleCharacter chr) {
        final List<Pair<Integer, Integer>> uFlagData = new ArrayList<>();
        final int[] uFlagTemp = new int[GameConstants.CFlagSize + 1];

        int stopForceIdx = 0;
        int stopForceCount = 0;
        int stopForceProjectileId = 0;
        int nViperEnergyCharged = (chr.getBuffedValue(CharacterTemporaryStat.EnergyCharged) == null ? 0 : chr.getBuffSource(CharacterTemporaryStat.EnergyCharged));

        //TwoStateTemporaryStat
        uFlagTemp[CharacterTemporaryStat.EnergyCharged.getPosition()] |= CharacterTemporaryStat.EnergyCharged.getValue();
        uFlagTemp[CharacterTemporaryStat.DashSpeed.getPosition()] |= CharacterTemporaryStat.DashSpeed.getValue();
        uFlagTemp[CharacterTemporaryStat.DashJump.getPosition()] |= CharacterTemporaryStat.DashJump.getValue();
        uFlagTemp[CharacterTemporaryStat.RideVehicle.getPosition()] |= CharacterTemporaryStat.RideVehicle.getValue();
        uFlagTemp[CharacterTemporaryStat.PartyBooster.getPosition()] |= CharacterTemporaryStat.PartyBooster.getValue();
        uFlagTemp[CharacterTemporaryStat.GuidedBullet.getPosition()] |= CharacterTemporaryStat.GuidedBullet.getValue();
        uFlagTemp[CharacterTemporaryStat.Undead.getPosition()] |= CharacterTemporaryStat.Undead.getValue();
        uFlagTemp[CharacterTemporaryStat.RideVehicleExpire.getPosition()] |= CharacterTemporaryStat.RideVehicleExpire.getValue();

        if (chr.getBuffedValue(CharacterTemporaryStat.Speed) != null) {
            uFlagTemp[CharacterTemporaryStat.Speed.getPosition()] |= CharacterTemporaryStat.Speed.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Speed), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ComboCounter) != null) {
            uFlagTemp[CharacterTemporaryStat.ComboCounter.getPosition()] |= CharacterTemporaryStat.ComboCounter.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ComboCounter), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) {
            uFlagTemp[CharacterTemporaryStat.WeaponCharge.getPosition()] |= CharacterTemporaryStat.WeaponCharge.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.WeaponCharge), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.WeaponCharge), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ElementalCharge) != null) {
            uFlagTemp[CharacterTemporaryStat.ElementalCharge.getPosition()] |= CharacterTemporaryStat.ElementalCharge.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ElementalCharge), 2));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Stun) != null) {
            uFlagTemp[CharacterTemporaryStat.Stun.getPosition()] |= CharacterTemporaryStat.Stun.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Stun), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Stun), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Shock) != null) {
            uFlagTemp[CharacterTemporaryStat.Shock.getPosition()] |= CharacterTemporaryStat.Shock.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Shock), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Darkness) != null) {
            uFlagTemp[CharacterTemporaryStat.Darkness.getPosition()] |= CharacterTemporaryStat.Darkness.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Darkness), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Darkness), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Seal) != null) {
            uFlagTemp[CharacterTemporaryStat.Seal.getPosition()] |= CharacterTemporaryStat.Seal.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Seal), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Seal), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Weakness) != null) {
            uFlagTemp[CharacterTemporaryStat.Weakness.getPosition()] |= CharacterTemporaryStat.Weakness.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Weakness), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Weakness), 4));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.WeaknessMdamage) != null) {
            uFlagTemp[CharacterTemporaryStat.WeaknessMdamage.getPosition()] |= CharacterTemporaryStat.WeaknessMdamage.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.WeaknessMdamage), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.WeaknessMdamage), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Curse) != null) {
            uFlagTemp[CharacterTemporaryStat.Curse.getPosition()] |= CharacterTemporaryStat.Curse.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Curse), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Curse), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Slow) != null) {
            uFlagTemp[CharacterTemporaryStat.Slow.getPosition()] |= CharacterTemporaryStat.Slow.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Slow), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Slow), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.PvPRaceEffect) != null) {
            uFlagTemp[CharacterTemporaryStat.PvPRaceEffect.getPosition()] |= CharacterTemporaryStat.PvPRaceEffect.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.PvPRaceEffect), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.PvPRaceEffect), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.IceKnight) != null) {
            uFlagTemp[CharacterTemporaryStat.IceKnight.getPosition()] |= CharacterTemporaryStat.IceKnight.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.IceKnight), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.IceKnight), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.TimeBomb) != null) {
            uFlagTemp[CharacterTemporaryStat.TimeBomb.getPosition()] |= CharacterTemporaryStat.TimeBomb.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.TimeBomb), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.TimeBomb), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Team) != null) {
            uFlagTemp[CharacterTemporaryStat.Team.getPosition()] |= CharacterTemporaryStat.Team.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Team), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Disorder) != null) {
            uFlagTemp[CharacterTemporaryStat.Disorder.getPosition()] |= CharacterTemporaryStat.Disorder.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Disorder), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Disorder), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Thread) != null) {
            uFlagTemp[CharacterTemporaryStat.Thread.getPosition()] |= CharacterTemporaryStat.Thread.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Thread), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Thread), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Poison) != null) {
            uFlagTemp[CharacterTemporaryStat.Poison.getPosition()] |= CharacterTemporaryStat.Poison.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Poison), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Poison), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ShadowPartner) != null) {
            uFlagTemp[CharacterTemporaryStat.ShadowPartner.getPosition()] |= CharacterTemporaryStat.ShadowPartner.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ShadowPartner), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ShadowPartner), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DarkSight) != null) {
            uFlagTemp[CharacterTemporaryStat.DarkSight.getPosition()] |= CharacterTemporaryStat.DarkSight.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SoulArrow) != null) {
            uFlagTemp[CharacterTemporaryStat.SoulArrow.getPosition()] |= CharacterTemporaryStat.SoulArrow.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Morph) != null) {
            uFlagTemp[CharacterTemporaryStat.Morph.getPosition()] |= CharacterTemporaryStat.Morph.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Morph), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Morph), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Ghost) != null) {
            uFlagTemp[CharacterTemporaryStat.Ghost.getPosition()] |= CharacterTemporaryStat.Ghost.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Ghost), 2));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Attract) != null) {
            uFlagTemp[CharacterTemporaryStat.Attract.getPosition()] |= CharacterTemporaryStat.Attract.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Attract), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Attract), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Magnet) != null) {
            uFlagTemp[CharacterTemporaryStat.Magnet.getPosition()] |= CharacterTemporaryStat.Magnet.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Magnet), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Magnet), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.MagnetArea) != null) {
            uFlagTemp[CharacterTemporaryStat.MagnetArea.getPosition()] |= CharacterTemporaryStat.MagnetArea.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.MagnetArea), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.MagnetArea), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.NoBulletConsume) != null) {
            uFlagTemp[CharacterTemporaryStat.NoBulletConsume.getPosition()] |= CharacterTemporaryStat.NoBulletConsume.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.NoBulletConsume), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BanMap) != null) {
            uFlagTemp[CharacterTemporaryStat.BanMap.getPosition()] |= CharacterTemporaryStat.BanMap.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BanMap), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.BanMap), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Barrier) != null) {
            uFlagTemp[CharacterTemporaryStat.Barrier.getPosition()] |= CharacterTemporaryStat.Barrier.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Barrier), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Barrier), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DojangShield) != null) {
            uFlagTemp[CharacterTemporaryStat.DojangShield.getPosition()] |= CharacterTemporaryStat.DojangShield.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DojangShield), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.DojangShield), 4));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.ReverseInput) != null) {
            uFlagTemp[CharacterTemporaryStat.ReverseInput.getPosition()] |= CharacterTemporaryStat.ReverseInput.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ReverseInput), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ReverseInput), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.RespectPImmune) != null) {
            uFlagTemp[CharacterTemporaryStat.RespectPImmune.getPosition()] |= CharacterTemporaryStat.RespectPImmune.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.RespectPImmune), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.RespectMImmune) != null) {
            uFlagTemp[CharacterTemporaryStat.RespectMImmune.getPosition()] |= CharacterTemporaryStat.RespectMImmune.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.RespectMImmune), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DefenseAtt) != null) {
            uFlagTemp[CharacterTemporaryStat.DefenseAtt.getPosition()] |= CharacterTemporaryStat.DefenseAtt.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DefenseAtt), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DefenseState) != null) {
            uFlagTemp[CharacterTemporaryStat.DefenseState.getPosition()] |= CharacterTemporaryStat.DefenseState.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DefenseState), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DojangBerserk) != null) {
            uFlagTemp[CharacterTemporaryStat.DojangBerserk.getPosition()] |= CharacterTemporaryStat.DojangBerserk.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DojangBerserk), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.DojangBerserk), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DojangInvincible) != null) {
            uFlagTemp[CharacterTemporaryStat.DojangInvincible.getPosition()] |= CharacterTemporaryStat.DojangInvincible.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.RepeatEffect) != null) {
            uFlagTemp[CharacterTemporaryStat.RepeatEffect.getPosition()] |= CharacterTemporaryStat.RepeatEffect.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.RepeatEffect), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.RepeatEffect), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.StopPortion) != null) {
            uFlagTemp[CharacterTemporaryStat.StopPortion.getPosition()] |= CharacterTemporaryStat.StopPortion.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.StopPortion), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.StopPortion), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.StopMotion) != null) {
            uFlagTemp[CharacterTemporaryStat.StopMotion.getPosition()] |= CharacterTemporaryStat.StopMotion.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.StopMotion), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.StopMotion), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Fear) != null) {
            uFlagTemp[CharacterTemporaryStat.Fear.getPosition()] |= CharacterTemporaryStat.Fear.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Fear), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Fear), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.MagicShield) != null) {
            uFlagTemp[CharacterTemporaryStat.MagicShield.getPosition()] |= CharacterTemporaryStat.MagicShield.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.MagicShield), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Flying) != null) {
            uFlagTemp[CharacterTemporaryStat.Flying.getPosition()] |= CharacterTemporaryStat.Flying.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Frozen) != null) {
            uFlagTemp[CharacterTemporaryStat.Frozen.getPosition()] |= CharacterTemporaryStat.Frozen.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Frozen), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Frozen), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Frozen2) != null) {
            uFlagTemp[CharacterTemporaryStat.Frozen2.getPosition()] |= CharacterTemporaryStat.Frozen2.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Frozen2), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Frozen2), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Web) != null) {
            uFlagTemp[CharacterTemporaryStat.Web.getPosition()] |= CharacterTemporaryStat.Web.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Web), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Web), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DrawBack) != null) {
            uFlagTemp[CharacterTemporaryStat.DrawBack.getPosition()] |= CharacterTemporaryStat.DrawBack.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DrawBack), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.DrawBack), 4));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.FinalCut) != null) {
            uFlagTemp[CharacterTemporaryStat.FinalCut.getPosition()] |= CharacterTemporaryStat.FinalCut.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.FinalCut), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.FinalCut), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Cyclone) != null) {
            uFlagTemp[CharacterTemporaryStat.Cyclone.getPosition()] |= CharacterTemporaryStat.Cyclone.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Cyclone), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.OnCapsule) != null) {
            uFlagTemp[CharacterTemporaryStat.OnCapsule.getPosition()] |= CharacterTemporaryStat.OnCapsule.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.OnCapsule), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Sneak) != null) {
            uFlagTemp[CharacterTemporaryStat.Sneak.getPosition()] |= CharacterTemporaryStat.Sneak.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BeastFormDamageUp) != null) {
            uFlagTemp[CharacterTemporaryStat.BeastFormDamageUp.getPosition()] |= CharacterTemporaryStat.BeastFormDamageUp.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Mechanic) != null) {
            uFlagTemp[CharacterTemporaryStat.Mechanic.getPosition()] |= CharacterTemporaryStat.Mechanic.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Mechanic), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Mechanic), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BlessingArmor) != null) {
            uFlagTemp[CharacterTemporaryStat.BlessingArmor.getPosition()] |= CharacterTemporaryStat.BlessingArmor.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BlessingArmorIncPAD) != null) {
            uFlagTemp[CharacterTemporaryStat.BlessingArmorIncPAD.getPosition()] |= CharacterTemporaryStat.BlessingArmorIncPAD.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Inflation) != null) {
            uFlagTemp[CharacterTemporaryStat.Inflation.getPosition()] |= CharacterTemporaryStat.Inflation.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Inflation), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Inflation), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Explosion) != null) {
            uFlagTemp[CharacterTemporaryStat.Explosion.getPosition()] |= CharacterTemporaryStat.Explosion.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Explosion), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Explosion), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DarkTornado) != null) {
            uFlagTemp[CharacterTemporaryStat.DarkTornado.getPosition()] |= CharacterTemporaryStat.DarkTornado.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DarkTornado), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.DarkTornado), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.AmplifyDamage) != null) {
            uFlagTemp[CharacterTemporaryStat.AmplifyDamage.getPosition()] |= CharacterTemporaryStat.AmplifyDamage.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.AmplifyDamage), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.AmplifyDamage), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.HideAttack) != null) {
            uFlagTemp[CharacterTemporaryStat.HideAttack.getPosition()] |= CharacterTemporaryStat.HideAttack.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.HideAttack), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.HideAttack), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.HolyMagicShell) != null) {
            uFlagTemp[CharacterTemporaryStat.HolyMagicShell.getPosition()] |= CharacterTemporaryStat.HolyMagicShell.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DevilishPower) != null) {
            uFlagTemp[CharacterTemporaryStat.DevilishPower.getPosition()] |= CharacterTemporaryStat.DevilishPower.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DevilishPower), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.DevilishPower), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SpiritLink) != null) {
            uFlagTemp[CharacterTemporaryStat.SpiritLink.getPosition()] |= CharacterTemporaryStat.SpiritLink.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.SpiritLink), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.SpiritLink), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Event) != null) {
            uFlagTemp[CharacterTemporaryStat.Event.getPosition()] |= CharacterTemporaryStat.Event.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Event), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Event), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Event2) != null) {
            uFlagTemp[CharacterTemporaryStat.Event2.getPosition()] |= CharacterTemporaryStat.Event2.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Event2), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Event2), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DeathMark) != null) {
            uFlagTemp[CharacterTemporaryStat.DeathMark.getPosition()] |= CharacterTemporaryStat.DeathMark.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DeathMark), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.DeathMark), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.PainMark) != null) {
            uFlagTemp[CharacterTemporaryStat.PainMark.getPosition()] |= CharacterTemporaryStat.PainMark.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.PainMark), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.PainMark), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Lapidification) != null) {
            uFlagTemp[CharacterTemporaryStat.Lapidification.getPosition()] |= CharacterTemporaryStat.Lapidification.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Lapidification), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Lapidification), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.VampDeath) != null) {
            uFlagTemp[CharacterTemporaryStat.VampDeath.getPosition()] |= CharacterTemporaryStat.VampDeath.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.VampDeath), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.VampDeath), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.VampDeathSummon) != null) {
            uFlagTemp[CharacterTemporaryStat.VampDeathSummon.getPosition()] |= CharacterTemporaryStat.VampDeathSummon.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.VampDeathSummon), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.VampDeathSummon), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.VenomSnake) != null) {
            uFlagTemp[CharacterTemporaryStat.VenomSnake.getPosition()] |= CharacterTemporaryStat.VenomSnake.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.VenomSnake), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.VenomSnake), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.PyramidEffect) != null) {
            uFlagTemp[CharacterTemporaryStat.PyramidEffect.getPosition()] |= CharacterTemporaryStat.PyramidEffect.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.PyramidEffect), 4));
        } else {
            uFlagTemp[CharacterTemporaryStat.PyramidEffect.getPosition()] |= CharacterTemporaryStat.PyramidEffect.getValue();
            uFlagData.add(new Pair<>(-1, 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.KillingPoint) != null) {
            uFlagTemp[CharacterTemporaryStat.KillingPoint.getPosition()] |= CharacterTemporaryStat.KillingPoint.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.KillingPoint), 1));
        } else {
            //Default flag. It's always encoded.
            uFlagTemp[CharacterTemporaryStat.KillingPoint.getPosition()] |= CharacterTemporaryStat.KillingPoint.getValue();
            uFlagData.add(new Pair<>(0, 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.PinkbeanRollingGrade) != null) {
            uFlagTemp[CharacterTemporaryStat.PinkbeanRollingGrade.getPosition()] |= CharacterTemporaryStat.PinkbeanRollingGrade.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.PinkbeanRollingGrade), 1));
        } else {
            //default buffstat always on
            uFlagTemp[CharacterTemporaryStat.PinkbeanRollingGrade.getPosition()] |= CharacterTemporaryStat.PinkbeanRollingGrade.getValue();
            uFlagData.add(new Pair<>(0, 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.IgnoreTargetDEF) != null) {
            uFlagTemp[CharacterTemporaryStat.IgnoreTargetDEF.getPosition()] |= CharacterTemporaryStat.IgnoreTargetDEF.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.IgnoreTargetDEF), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.IgnoreTargetDEF), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Invisible) != null) {
            uFlagTemp[CharacterTemporaryStat.Invisible.getPosition()] |= CharacterTemporaryStat.Invisible.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Invisible), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Invisible), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Judgement) != null) {
            uFlagTemp[CharacterTemporaryStat.Judgement.getPosition()] |= CharacterTemporaryStat.Judgement.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Judgement), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Judgement), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.KeyDownAreaMoving) != null) {
            uFlagTemp[CharacterTemporaryStat.KeyDownAreaMoving.getPosition()] |= CharacterTemporaryStat.KeyDownAreaMoving.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.KeyDownAreaMoving), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.KeyDownAreaMoving), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.StackBuff) != null) {
            uFlagTemp[CharacterTemporaryStat.StackBuff.getPosition()] |= CharacterTemporaryStat.StackBuff.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.StackBuff), 2));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BlessOfDarkness) != null) {
            uFlagTemp[CharacterTemporaryStat.BlessOfDarkness.getPosition()] |= CharacterTemporaryStat.BlessOfDarkness.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BlessOfDarkness), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Larkness) != null) {
            uFlagTemp[CharacterTemporaryStat.Larkness.getPosition()] |= CharacterTemporaryStat.Larkness.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Larkness), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Larkness), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ReshuffleSwitch) != null) {
            uFlagTemp[CharacterTemporaryStat.ReshuffleSwitch.getPosition()] |= CharacterTemporaryStat.ReshuffleSwitch.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ReshuffleSwitch), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ReshuffleSwitch), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SpecialAction) != null) {
            uFlagTemp[CharacterTemporaryStat.SpecialAction.getPosition()] |= CharacterTemporaryStat.SpecialAction.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.SpecialAction), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.SpecialAction), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.StopForceAtomInfo) != null) {
            uFlagTemp[CharacterTemporaryStat.StopForceAtomInfo.getPosition()] |= CharacterTemporaryStat.StopForceAtomInfo.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.StopForceAtomInfo), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.StopForceAtomInfo), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SoulGazeCriDamR) != null) {
            uFlagTemp[CharacterTemporaryStat.SoulGazeCriDamR.getPosition()] |= CharacterTemporaryStat.SoulGazeCriDamR.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.SoulGazeCriDamR), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.SoulGazeCriDamR), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.PowerTransferGauge) != null) {
            uFlagTemp[CharacterTemporaryStat.PowerTransferGauge.getPosition()] |= CharacterTemporaryStat.PowerTransferGauge.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.PowerTransferGauge), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.PowerTransferGauge), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.AffinitySlug) != null) {
            uFlagTemp[CharacterTemporaryStat.AffinitySlug.getPosition()] |= CharacterTemporaryStat.AffinitySlug.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.AffinitySlug), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.AffinitySlug), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SoulExalt) != null) {
            uFlagTemp[CharacterTemporaryStat.SoulExalt.getPosition()] |= CharacterTemporaryStat.SoulExalt.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.SoulExalt), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.SoulExalt), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.HiddenPieceOn) != null) {
            uFlagTemp[CharacterTemporaryStat.HiddenPieceOn.getPosition()] |= CharacterTemporaryStat.HiddenPieceOn.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.HiddenPieceOn), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.HiddenPieceOn), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SmashStack) != null) {
            uFlagTemp[CharacterTemporaryStat.SmashStack.getPosition()] |= CharacterTemporaryStat.SmashStack.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.SmashStack), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.SmashStack), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.MobZoneState) != null) {
            uFlagTemp[CharacterTemporaryStat.MobZoneState.getPosition()] |= CharacterTemporaryStat.MobZoneState.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.MobZoneState), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.MobZoneState), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.GiveMeHeal) != null) {
            uFlagTemp[CharacterTemporaryStat.GiveMeHeal.getPosition()] |= CharacterTemporaryStat.GiveMeHeal.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.GiveMeHeal), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.GiveMeHeal), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.TouchMe) != null) {
            uFlagTemp[CharacterTemporaryStat.TouchMe.getPosition()] |= CharacterTemporaryStat.TouchMe.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.TouchMe), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.TouchMe), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Contagion) != null) {
            uFlagTemp[CharacterTemporaryStat.Contagion.getPosition()] |= CharacterTemporaryStat.Contagion.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Contagion), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Contagion), 4));
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Contagion), 4)); // tOption
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ComboUnlimited) != null) {
            uFlagTemp[CharacterTemporaryStat.ComboUnlimited.getPosition()] |= CharacterTemporaryStat.ComboUnlimited.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ComboUnlimited), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ComboUnlimited), 4));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.IgnorePCounter) != null) {
            uFlagTemp[CharacterTemporaryStat.IgnorePCounter.getPosition()] |= CharacterTemporaryStat.IgnorePCounter.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.IgnorePCounter), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.IgnorePCounter), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.IgnoreAllCounter) != null) {
            uFlagTemp[CharacterTemporaryStat.IgnoreAllCounter.getPosition()] |= CharacterTemporaryStat.IgnoreAllCounter.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.IgnoreAllCounter), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.IgnoreAllCounter), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.IgnorePImmune) != null) {
            uFlagTemp[CharacterTemporaryStat.IgnorePImmune.getPosition()] |= CharacterTemporaryStat.IgnorePImmune.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.IgnorePImmune), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.IgnorePImmune), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.IgnoreAllImmune) != null) {
            uFlagTemp[CharacterTemporaryStat.IgnoreAllImmune.getPosition()] |= CharacterTemporaryStat.IgnoreAllImmune.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.IgnoreAllImmune), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.IgnoreAllImmune), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.FinalJudgement) != null) {
            uFlagTemp[CharacterTemporaryStat.FinalJudgement.getPosition()] |= CharacterTemporaryStat.FinalJudgement.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.FinalJudgement), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.FinalJudgement), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.KnightsAura) != null) {
            uFlagTemp[CharacterTemporaryStat.KnightsAura.getPosition()] |= CharacterTemporaryStat.KnightsAura.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.KnightsAura), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.KnightsAura), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.IceAura) != null) {
            uFlagTemp[CharacterTemporaryStat.IceAura.getPosition()] |= CharacterTemporaryStat.IceAura.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.IceAura), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.IceAura), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.FireAura) != null) {
            uFlagTemp[CharacterTemporaryStat.FireAura.getPosition()] |= CharacterTemporaryStat.FireAura.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.FireAura), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.FireAura), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.VengeanceOfAngel) != null) {
            uFlagTemp[CharacterTemporaryStat.VengeanceOfAngel.getPosition()] |= CharacterTemporaryStat.VengeanceOfAngel.getValue();
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.HeavensDoor) != null) {
            uFlagTemp[CharacterTemporaryStat.HeavensDoor.getPosition()] |= CharacterTemporaryStat.HeavensDoor.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.HeavensDoor), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.HeavensDoor), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DamAbsorbShield) != null) {
            uFlagTemp[CharacterTemporaryStat.DamAbsorbShield.getPosition()] |= CharacterTemporaryStat.DamAbsorbShield.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DamAbsorbShield), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.DamAbsorbShield), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.AntiMagicShell) != null) {
            uFlagTemp[CharacterTemporaryStat.AntiMagicShell.getPosition()] |= CharacterTemporaryStat.AntiMagicShell.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.AntiMagicShell), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.AntiMagicShell), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.NotDamaged) != null) {
            uFlagTemp[CharacterTemporaryStat.NotDamaged.getPosition()] |= CharacterTemporaryStat.NotDamaged.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.NotDamaged), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.NotDamaged), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BleedingToxin) != null) {
            uFlagTemp[CharacterTemporaryStat.BleedingToxin.getPosition()] |= CharacterTemporaryStat.BleedingToxin.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BleedingToxin), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.BleedingToxin), 4));
        }
        /*
        if (chr.getBuffedValue(CharacterTemporaryStat.WindBreakerFinal) != null) {
            uFlagTemp[CharacterTemporaryStat.WindBreakerFinal.getPosition()] |= CharacterTemporaryStat.WindBreakerFinal.getValue();
            uFlagData.add(new Pair<>(pUser.getBuffedValue(CharacterTemporaryStat.WindBreakerFinal), 2));
            uFlagData.add(new Pair<>(pUser.getTrueBuffSource(CharacterTemporaryStat.WindBreakerFinal), 4));
        }
         */
        if (chr.getBuffedValue(CharacterTemporaryStat.IgnoreMobDamR) != null) {
            uFlagTemp[CharacterTemporaryStat.IgnoreMobDamR.getPosition()] |= CharacterTemporaryStat.IgnoreMobDamR.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.IgnoreMobDamR), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.IgnoreMobDamR), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Asura) != null) {
            uFlagTemp[CharacterTemporaryStat.Asura.getPosition()] |= CharacterTemporaryStat.Asura.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Asura), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Asura), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.UnityOfPower) != null) {
            uFlagTemp[CharacterTemporaryStat.UnityOfPower.getPosition()] |= CharacterTemporaryStat.UnityOfPower.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.UnityOfPower), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.UnityOfPower), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Stimulate) != null) {
            uFlagTemp[CharacterTemporaryStat.Stimulate.getPosition()] |= CharacterTemporaryStat.Stimulate.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Stimulate), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Stimulate), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ReturnTeleport) != null) {
            uFlagTemp[CharacterTemporaryStat.ReturnTeleport.getPosition()] |= CharacterTemporaryStat.ReturnTeleport.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ReturnTeleport), 1));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ReturnTeleport), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.CapDebuff) != null) {
            uFlagTemp[CharacterTemporaryStat.CapDebuff.getPosition()] |= CharacterTemporaryStat.CapDebuff.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.CapDebuff), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.CapDebuff), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.OverloadCount) != null) {
            uFlagTemp[CharacterTemporaryStat.OverloadCount.getPosition()] |= CharacterTemporaryStat.OverloadCount.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.OverloadCount), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.OverloadCount), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.FireBomb) != null) {
            uFlagTemp[CharacterTemporaryStat.FireBomb.getPosition()] |= CharacterTemporaryStat.FireBomb.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.FireBomb), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.FireBomb), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SurplusSupply) != null) {
            uFlagTemp[CharacterTemporaryStat.SurplusSupply.getPosition()] |= CharacterTemporaryStat.SurplusSupply.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.SurplusSupply), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.NewFlying) != null) {
            uFlagTemp[CharacterTemporaryStat.NewFlying.getPosition()] |= CharacterTemporaryStat.NewFlying.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.NewFlying), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.NewFlying), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.NaviFlying) != null) {
            uFlagTemp[CharacterTemporaryStat.NaviFlying.getPosition()] |= CharacterTemporaryStat.NaviFlying.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.NaviFlying), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.NaviFlying), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.AmaranthGenerator) != null) {
            uFlagTemp[CharacterTemporaryStat.AmaranthGenerator.getPosition()] |= CharacterTemporaryStat.AmaranthGenerator.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.AmaranthGenerator), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.AmaranthGenerator), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.CygnusElementSkill) != null) {
            uFlagTemp[CharacterTemporaryStat.CygnusElementSkill.getPosition()] |= CharacterTemporaryStat.CygnusElementSkill.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.CygnusElementSkill), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.CygnusElementSkill), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.StrikerHyperElectric) != null) {
            uFlagTemp[CharacterTemporaryStat.StrikerHyperElectric.getPosition()] |= CharacterTemporaryStat.StrikerHyperElectric.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.StrikerHyperElectric), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.StrikerHyperElectric), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.EventPointAbsorb) != null) {
            uFlagTemp[CharacterTemporaryStat.EventPointAbsorb.getPosition()] |= CharacterTemporaryStat.EventPointAbsorb.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.EventPointAbsorb), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.EventPointAbsorb), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.EventAssemble) != null) {
            uFlagTemp[CharacterTemporaryStat.EventAssemble.getPosition()] |= CharacterTemporaryStat.EventAssemble.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.EventAssemble), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.EventAssemble), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Albatross) != null) {
            uFlagTemp[CharacterTemporaryStat.Albatross.getPosition()] |= CharacterTemporaryStat.Albatross.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Albatross), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Albatross), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Translucence) != null) {
            uFlagTemp[CharacterTemporaryStat.Translucence.getPosition()] |= CharacterTemporaryStat.Translucence.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Translucence), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Translucence), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.PoseType) != null) {
            uFlagTemp[CharacterTemporaryStat.PoseType.getPosition()] |= CharacterTemporaryStat.PoseType.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.PoseType), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.PoseType), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.LightOfSpirit) != null) {
            uFlagTemp[CharacterTemporaryStat.LightOfSpirit.getPosition()] |= CharacterTemporaryStat.LightOfSpirit.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.LightOfSpirit), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.LightOfSpirit), 4));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.ElementSoul) != null) {
            uFlagTemp[CharacterTemporaryStat.ElementSoul.getPosition()] |= CharacterTemporaryStat.ElementSoul.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ElementSoul), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ElementSoul), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.GlimmeringTime) != null) {
            uFlagTemp[CharacterTemporaryStat.GlimmeringTime.getPosition()] |= CharacterTemporaryStat.GlimmeringTime.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.GlimmeringTime), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.GlimmeringTime), 4));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.Reincarnation) != null) {
            uFlagTemp[CharacterTemporaryStat.Reincarnation.getPosition()] |= CharacterTemporaryStat.Reincarnation.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Reincarnation), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Reincarnation), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Beholder) != null) {
            uFlagTemp[CharacterTemporaryStat.Beholder.getPosition()] |= CharacterTemporaryStat.Beholder.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Beholder), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Beholder), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.QuiverCatridge) != null) {
            uFlagTemp[CharacterTemporaryStat.QuiverCatridge.getPosition()] |= CharacterTemporaryStat.QuiverCatridge.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.QuiverCatridge), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.QuiverCatridge), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ArmorPiercing) != null) {
            uFlagTemp[CharacterTemporaryStat.ArmorPiercing.getPosition()] |= CharacterTemporaryStat.ArmorPiercing.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ArmorPiercing), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ArmorPiercing), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.UserControlMob) != null) {
            uFlagTemp[CharacterTemporaryStat.UserControlMob.getPosition()] |= CharacterTemporaryStat.UserControlMob.getValue();
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.ZeroAuraStr) != null) {
            uFlagTemp[CharacterTemporaryStat.ZeroAuraStr.getPosition()] |= CharacterTemporaryStat.ZeroAuraStr.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ZeroAuraStr), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ZeroAuraStr), 4));
        } else {
            //Default flag. It's always encoded.
            uFlagTemp[CharacterTemporaryStat.ZeroAuraStr.getPosition()] |= CharacterTemporaryStat.ZeroAuraStr.getValue();
            uFlagData.add(new Pair<>(0, 2));
            uFlagData.add(new Pair<>(0, 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ZeroAuraSpd) != null) {
            uFlagTemp[CharacterTemporaryStat.ZeroAuraSpd.getPosition()] |= CharacterTemporaryStat.ZeroAuraSpd.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ZeroAuraSpd), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ZeroAuraSpd), 4));
        } else {
            //Default flag. It's always encoded.
            uFlagTemp[CharacterTemporaryStat.ZeroAuraSpd.getPosition()] |= CharacterTemporaryStat.ZeroAuraSpd.getValue();
            uFlagData.add(new Pair<>(0, 2));
            uFlagData.add(new Pair<>(0, 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ImmuneBarrier) != null) {
            uFlagTemp[CharacterTemporaryStat.ImmuneBarrier.getPosition()] |= CharacterTemporaryStat.ImmuneBarrier.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ImmuneBarrier), 4)); // nOption
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ImmuneBarrier), 4)); // xOption
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.FullSoulMP) != null) {
            uFlagTemp[CharacterTemporaryStat.FullSoulMP.getPosition()] |= CharacterTemporaryStat.FullSoulMP.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.FullSoulMP), 4)); // rOption
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.FullSoulMP), 4)); // xOption
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.AntiMagicShell) != null) {
            uFlagTemp[CharacterTemporaryStat.AntiMagicShell.getPosition()] |= CharacterTemporaryStat.AntiMagicShell.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.AntiMagicShell), 1));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Dance) != null) {
            uFlagTemp[CharacterTemporaryStat.Dance.getPosition()] |= CharacterTemporaryStat.Dance.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Dance), 4));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Dance), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.SpiritGuard) != null) {
            uFlagTemp[CharacterTemporaryStat.SpiritGuard.getPosition()] |= CharacterTemporaryStat.SpiritGuard.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.SpiritGuard), 4));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.SpiritGuard), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ComboTempest) != null) {
            uFlagTemp[CharacterTemporaryStat.ComboTempest.getPosition()] |= CharacterTemporaryStat.ComboTempest.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ComboTempest), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ComboTempest), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.HalfstatByDebuff) != null) {
            uFlagTemp[CharacterTemporaryStat.HalfstatByDebuff.getPosition()] |= CharacterTemporaryStat.HalfstatByDebuff.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.HalfstatByDebuff), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.HalfstatByDebuff), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ComplusionSlant) != null) {
            uFlagTemp[CharacterTemporaryStat.ComplusionSlant.getPosition()] |= CharacterTemporaryStat.ComplusionSlant.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ComplusionSlant), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ComplusionSlant), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.ShadowServant) != null) {
            uFlagTemp[CharacterTemporaryStat.ShadowServant.getPosition()] |= CharacterTemporaryStat.ShadowServant.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ShadowServant), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.ShadowServant), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.JaguarSummoned) != null) {
            uFlagTemp[CharacterTemporaryStat.JaguarSummoned.getPosition()] |= CharacterTemporaryStat.JaguarSummoned.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.JaguarSummoned), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.JaguarSummoned), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BMageAura) != null) {
            uFlagTemp[CharacterTemporaryStat.BMageAura.getPosition()] |= CharacterTemporaryStat.BMageAura.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BMageAura), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.BMageAura), 4));
        } else {
            //Default flag. It's always encoded.
            uFlagTemp[CharacterTemporaryStat.BMageAura.getPosition()] |= CharacterTemporaryStat.BMageAura.getValue();
            uFlagData.add(new Pair<>(0, 2));
            uFlagData.add(new Pair<>(0, 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.DarkLighting) != null) {
            uFlagTemp[CharacterTemporaryStat.DarkLighting.getPosition()] |= CharacterTemporaryStat.DarkLighting.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.DarkLighting), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.DarkLighting), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.AttackCountX) != null) {
            uFlagTemp[CharacterTemporaryStat.AttackCountX.getPosition()] |= CharacterTemporaryStat.AttackCountX.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.AttackCountX), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.AttackCountX), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.FireBarrier) != null) {
            uFlagTemp[CharacterTemporaryStat.FireBarrier.getPosition()] |= CharacterTemporaryStat.FireBarrier.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.FireBarrier), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.FireBarrier), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.KeyDownMoving) != null) {
            uFlagTemp[CharacterTemporaryStat.KeyDownMoving.getPosition()] |= CharacterTemporaryStat.KeyDownMoving.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.KeyDownMoving), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.KeyDownMoving), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.MichaelSoulLink) != null) {
            uFlagTemp[CharacterTemporaryStat.MichaelSoulLink.getPosition()] |= CharacterTemporaryStat.MichaelSoulLink.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.MichaelSoulLink), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.MichaelSoulLink), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.KinesisPsychicEnergeShield) != null) {
            uFlagTemp[CharacterTemporaryStat.KinesisPsychicEnergeShield.getPosition()] |= CharacterTemporaryStat.KinesisPsychicEnergeShield.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.KinesisPsychicEnergeShield), 4));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.KinesisPsychicEnergeShield), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.BladeStance) != null) {
            uFlagTemp[CharacterTemporaryStat.BladeStance.getPosition()] |= CharacterTemporaryStat.BladeStance.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BladeStance), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.BladeStance), 4));
            //uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BladeStance), 4)); // xOption
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Fever) != null) {
            uFlagTemp[CharacterTemporaryStat.Fever.getPosition()] |= CharacterTemporaryStat.Fever.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Fever), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Fever), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.AdrenalinBoost) != null) {
            uFlagTemp[CharacterTemporaryStat.AdrenalinBoost.getPosition()] |= CharacterTemporaryStat.AdrenalinBoost.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.AdrenalinBoost), 4));
        } else {
            //default buffstat always on
            uFlagTemp[CharacterTemporaryStat.AdrenalinBoost.getPosition()] |= CharacterTemporaryStat.AdrenalinBoost.getValue();
            uFlagData.add(new Pair<>(0, 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.RWBarrier) != null) {
            uFlagTemp[CharacterTemporaryStat.RWBarrier.getPosition()] |= CharacterTemporaryStat.RWBarrier.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.RWBarrier), 4));
        } else {
            //default buffstat always on
            uFlagTemp[CharacterTemporaryStat.RWBarrier.getPosition()] |= CharacterTemporaryStat.RWBarrier.getValue();
            uFlagData.add(new Pair<>(0, 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.RWMagnumBlow) != null) {
            uFlagTemp[CharacterTemporaryStat.RWMagnumBlow.getPosition()] |= CharacterTemporaryStat.RWMagnumBlow.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.RWMagnumBlow), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Stigma) != null) {
            uFlagTemp[CharacterTemporaryStat.Stigma.getPosition()] |= CharacterTemporaryStat.Stigma.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Stigma), 2));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.Stigma), 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.Unknown474) != null) {
            uFlagTemp[CharacterTemporaryStat.Unknown474.getPosition()] |= CharacterTemporaryStat.Unknown474.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Unknown474), 4));
        } else {
            //default buffstat always on
            uFlagTemp[CharacterTemporaryStat.Unknown474.getPosition()] |= CharacterTemporaryStat.Unknown474.getValue();
            uFlagData.add(new Pair<>(0, 4));
        }
        if (chr.getBuffedValue(CharacterTemporaryStat.PoseType) != null) {
            uFlagTemp[CharacterTemporaryStat.PoseType.getPosition()] |= CharacterTemporaryStat.PoseType.getValue();
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.PoseType), 1));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.BattlePvP_LangE_Protection) != null) {
            uFlagTemp[CharacterTemporaryStat.BattlePvP_LangE_Protection.getPosition()] |= CharacterTemporaryStat.BattlePvP_LangE_Protection.getValue();
        } else {
            //Default flag. It's always encoded.
            uFlagTemp[CharacterTemporaryStat.BattlePvP_LangE_Protection.getPosition()] |= CharacterTemporaryStat.BattlePvP_LangE_Protection.getValue();
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.BattlePvP_Helena_Mark) != null) {
            uFlagTemp[CharacterTemporaryStat.BattlePvP_Helena_Mark.getPosition()] |= CharacterTemporaryStat.BattlePvP_Helena_Mark.getValue();
        } else {
            //Default flag. It's always encoded.
            uFlagTemp[CharacterTemporaryStat.BattlePvP_Helena_Mark.getPosition()] |= CharacterTemporaryStat.BattlePvP_Helena_Mark.getValue();
        }

        for (int i = uFlagTemp.length; i >= 2; i--) {
            oPacket.EncodeInteger(uFlagTemp[i - 1]);
        }

        for (Pair<Integer, Integer> nStats : uFlagData) {
            if (nStats.right == 4) {
                oPacket.EncodeInteger(nStats.left);
            } else if (nStats.right == 2) {
                oPacket.EncodeShort(nStats.left);
            } else if (nStats.right == 1) {
                oPacket.Encode(nStats.left);
            }
        }

        uFlagData.clear(); // Clear Array for new bytes

        oPacket.Encode(0); // nDefenseAtt
        oPacket.Encode(0); // nDefenseState
        oPacket.Encode(0); // nPVPDamage

        if (chr.getBuffedValue(CharacterTemporaryStat.ZeroAuraStr) != null) {
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ZeroAuraStr), 1));
        } else {
            //default buffstat always on
            uFlagData.add(new Pair<>(0, 1));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.ZeroAuraSpd) != null) {
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.ZeroAuraSpd), 1));
        } else {
            //default buffstat always on
            uFlagData.add(new Pair<>(0, 1));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.BMageAura) != null) {
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BMageAura), 1));
        } else {
            //default buffstat always on
            uFlagData.add(new Pair<>(0, 1));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.BattlePvP_Helena_Mark) != null) {
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BattlePvP_Helena_Mark), 4));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.BattlePvP_Helena_Mark), 4));
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BattlePvP_Helena_Mark), 4)); // cOption
        } else {
            //default buffstat always on
            uFlagData.add(new Pair<>(0, 4));
            uFlagData.add(new Pair<>(0, 4));
            uFlagData.add(new Pair<>(0, 4));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.BattlePvP_LangE_Protection) != null) {
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.BattlePvP_LangE_Protection), 4));
            uFlagData.add(new Pair<>(chr.getTrueBuffSource(CharacterTemporaryStat.BattlePvP_LangE_Protection), 4));
        } else {
            //default buffstat always on
            uFlagData.add(new Pair<>(0, 4));
            uFlagData.add(new Pair<>(0, 4));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.MichaelSoulLink) != null) {
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.MichaelSoulLink), 4)); // xOption
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.MichaelSoulLink), 1)); // bOption
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.MichaelSoulLink), 4)); // cOption
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.MichaelSoulLink), 4)); // yOption
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.AdrenalinBoost) != null) {
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.AdrenalinBoost), 1));
        } else {
            //default buffstat always on
            uFlagData.add(new Pair<>(0, 1));
        }

        if (chr.getBuffedValue(CharacterTemporaryStat.Stigma) != null) {
            uFlagData.add(new Pair<>(chr.getBuffedValue(CharacterTemporaryStat.Stigma), 4));
        }

        for (Pair<Integer, Integer> nStats : uFlagData) {
            if (nStats.right == 4) {
                oPacket.EncodeInteger(nStats.left);
            } else if (nStats.right == 2) {
                oPacket.EncodeShort(nStats.left);
            } else if (nStats.right == 1) {
                oPacket.Encode(nStats.left);
            }
        }
        encodeProjectileAtoms(oPacket, stopForceIdx, stopForceCount, stopForceProjectileId);
        oPacket.EncodeInteger(nViperEnergyCharged); //nViperEnergyCharged
        encodeRemoteTwoStateTemporaryStat(oPacket, chr);
    }
}