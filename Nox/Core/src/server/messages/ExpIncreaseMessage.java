package server.messages;

import java.util.EnumMap;
import java.util.Map.Entry;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class ExpIncreaseMessage implements MessageInterface {

    private final boolean bIsLastHit;
    private final int nIncExp, restFieldEXPRate;
    private final boolean bOnQuest;
    private final byte questBonusEXPRate;
    private final EnumMap<ExpGainTypes, Integer> expIncreaseStats;

    public ExpIncreaseMessage(boolean bIsLastHit, int nIncExp, boolean bOnQuest, byte questBonusEXPRate, int restFieldEXPRate, EnumMap<ExpGainTypes, Integer> expIncreaseStats) {
        this.bIsLastHit = bIsLastHit;
        this.nIncExp = nIncExp;
        this.bOnQuest = bOnQuest; // TODO: Find out where this comes from :( a quest? which one
        this.restFieldEXPRate = restFieldEXPRate;
        this.questBonusEXPRate = questBonusEXPRate;
        this.expIncreaseStats = expIncreaseStats;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.ExpIncrease.getType());
        oPacket.Encode(bIsLastHit ? 1 : 0);//bIsLastHit [White message]
        oPacket.EncodeInteger(nIncExp);//nIncExp
        oPacket.Encode(bOnQuest ? 1 : 0);//bOnQuest

        int flag = 0;
        for (Entry<ExpGainTypes, Integer> v : expIncreaseStats.entrySet()) {
            ExpGainTypes gainType = v.getKey();

            flag |= gainType.getValue(); // add to total flag value
        }
        oPacket.EncodeLong(flag);//dbCharFlag

        ///
        /// Write EXP given to each individual message
        /// >> We could also use a loop here, however it would be much easier to keep the same order as it is on client.exe
        /// >> if anything gets changed in future
        if (expIncreaseStats.containsKey(ExpGainTypes.MobBonus)) {//if ((flag & ExpGainTypes.MobBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.MobBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.PartyBonusPercentage)) {//if ((flag & ExpGainTypes.PartyBonusPercentage.getValue()) != 0) {
            oPacket.Encode(expIncreaseStats.get(ExpGainTypes.PartyBonusPercentage));
        }

        if (bOnQuest) {
            oPacket.Encode(questBonusEXPRate > 0 ? 1 : 0);//bOnQuest, repeated.. who knows why, nexon/// =.=
        }
        if (questBonusEXPRate > 0) {
            oPacket.Encode(questBonusEXPRate); // quest bonus rate
        }

        if (expIncreaseStats.containsKey(ExpGainTypes.WeddingBonus)) {//if ((flag & ExpGainTypes.WeddingBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.WeddingBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.PartyBonus)) {//if ((flag & ExpGainTypes.PartyBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.PartyBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.ItemBonus)) {//if ((flag & ExpGainTypes.ItemBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.ItemBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.PremiumIpBonus)) {//if ((flag & ExpGainTypes.PremiumIpBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.PremiumIpBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.RainbowWeekBonus)) {//if ((flag & ExpGainTypes.RainbowWeekBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.RainbowWeekBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.BoomUpBonus)) {//if ((flag & ExpGainTypes.BoomUpBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.BoomUpBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.ExpBuffBonus)) {//if ((flag & ExpGainTypes.ExpBuffBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.ExpBuffBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.PsdBonus)) {//if ((flag & ExpGainTypes.PsdBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.PsdBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.IndieBonus)) {//if ((flag & ExpGainTypes.IndieBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.IndieBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.RelaxBonus)) {//if ((flag & ExpGainTypes.RelaxBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.RelaxBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.InstallItemBonus)) {//if ((flag & ExpGainTypes.InstallItemBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.InstallItemBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.AzwanWinnerBonus)) {//if ((flag & ExpGainTypes.AzwanWinnerBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.AzwanWinnerBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.IncExpR)) {//if ((flag & ExpGainTypes.IncExpR.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.IncExpR));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.ValuePackBonus)) {//if ((flag & ExpGainTypes.ValuePackBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.ValuePackBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.IncPQExpR)) {//if ((flag & ExpGainTypes.IncPQExpR.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.IncPQExpR));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.BaseAddExp)) {//if ((flag & ExpGainTypes.BaseAddExp.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.BaseAddExp));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.BloodAllianceBonus)) {//if ((flag & ExpGainTypes.BloodAllianceBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.BloodAllianceBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.FreezeEventBonus)) {//if ((flag & ExpGainTypes.FreezeEventBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.FreezeEventBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.RestFieldBonus)) {//if ((flag & ExpGainTypes.RestFieldBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.RestFieldBonus));// bonus exp
            oPacket.EncodeInteger(restFieldEXPRate);//rate
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.HpRateBonus)) {//if ((flag & ExpGainTypes.HpRateBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.HpRateBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.FieldBonus)) {//if ((flag & ExpGainTypes.FieldBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.FieldBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.MobKillBonus)) {//if ((flag & ExpGainTypes.MobKillBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.MobKillBonus));
        }
        if (expIncreaseStats.containsKey(ExpGainTypes.LiveEventBonus)) {//if ((flag & ExpGainTypes.LiveEventBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.LiveEventBonus));
        }
        // Extra on global
        if (expIncreaseStats.containsKey(ExpGainTypes.PartyRingBonus)) {//if ((flag & ExpGainTypes.PartyRingBonus.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.PartyRingBonus));
        }
        // this should be last
        if (expIncreaseStats.containsKey(ExpGainTypes.MonsterCardSetCompletionBonus)) {//if ((flag & ExpGainTypes.UNK1.getValue()) != 0) {
            oPacket.EncodeInteger(expIncreaseStats.get(ExpGainTypes.MonsterCardSetCompletionBonus));
        }
    }

}
