package server.messages;

import enums.MessageOpcodesType;
import enums.ExpType;
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
    private final EnumMap<ExpType, Integer> expIncreaseStats;

    public ExpIncreaseMessage(boolean bIsLastHit, int nIncExp, boolean bOnQuest, byte questBonusEXPRate, int restFieldEXPRate, EnumMap<ExpType, Integer> expIncreaseStats) {
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
        oPacket.EncodeByte(MessageOpcodesType.ExpIncrease.getType());
        oPacket.EncodeByte(bIsLastHit ? 1 : 0);//bIsLastHit [White message]
        oPacket.EncodeInt(nIncExp);//nIncExp
        oPacket.EncodeByte(bOnQuest ? 1 : 0);//bOnQuest

        int flag = 0;
        for (Entry<ExpType, Integer> v : expIncreaseStats.entrySet()) {
            ExpType gainType = v.getKey();

            flag |= gainType.getValue(); // add to total flag value
        }
        oPacket.EncodeLong(flag);//dbCharFlag

        ///
        /// Write EXP given to each individual message
        /// >> We could also use a loop here, however it would be much easier to keep the same order as it is on client.exe
        /// >> if anything gets changed in future
        if (expIncreaseStats.containsKey(ExpType.MobBonus)) {//if ((flag & ExpGainTypes.MobBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.MobBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.PartyBonusPercentage)) {//if ((flag & ExpGainTypes.PartyBonusPercentage.getValue()) != 0) {
            oPacket.EncodeByte(expIncreaseStats.get(ExpType.PartyBonusPercentage));
        }

        if (bOnQuest) {
            oPacket.EncodeByte(questBonusEXPRate > 0 ? 1 : 0);//bOnQuest, repeated.. who knows why, nexon/// =.=
        }
        if (questBonusEXPRate > 0) {
            oPacket.EncodeByte(questBonusEXPRate); // quest bonus rate
        }

        if (expIncreaseStats.containsKey(ExpType.WeddingBonus)) {//if ((flag & ExpGainTypes.WeddingBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.WeddingBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.PartyBonus)) {//if ((flag & ExpGainTypes.PartyBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.PartyBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.ItemBonus)) {//if ((flag & ExpGainTypes.ItemBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.ItemBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.PremiumIpBonus)) {//if ((flag & ExpGainTypes.PremiumIpBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.PremiumIpBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.RainbowWeekBonus)) {//if ((flag & ExpGainTypes.RainbowWeekBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.RainbowWeekBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.BoomUpBonus)) {//if ((flag & ExpGainTypes.BoomUpBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.BoomUpBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.ExpBuffBonus)) {//if ((flag & ExpGainTypes.ExpBuffBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.ExpBuffBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.PsdBonus)) {//if ((flag & ExpGainTypes.PsdBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.PsdBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.IndieBonus)) {//if ((flag & ExpGainTypes.IndieBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.IndieBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.RelaxBonus)) {//if ((flag & ExpGainTypes.RelaxBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.RelaxBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.InstallItemBonus)) {//if ((flag & ExpGainTypes.InstallItemBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.InstallItemBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.AzwanWinnerBonus)) {//if ((flag & ExpGainTypes.AzwanWinnerBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.AzwanWinnerBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.IncExpR)) {//if ((flag & ExpGainTypes.IncExpR.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.IncExpR));
        }
        if (expIncreaseStats.containsKey(ExpType.ValuePackBonus)) {//if ((flag & ExpGainTypes.ValuePackBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.ValuePackBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.IncPQExpR)) {//if ((flag & ExpGainTypes.IncPQExpR.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.IncPQExpR));
        }
        if (expIncreaseStats.containsKey(ExpType.BaseAddExp)) {//if ((flag & ExpGainTypes.BaseAddExp.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.BaseAddExp));
        }
        if (expIncreaseStats.containsKey(ExpType.BloodAllianceBonus)) {//if ((flag & ExpGainTypes.BloodAllianceBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.BloodAllianceBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.FreezeEventBonus)) {//if ((flag & ExpGainTypes.FreezeEventBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.FreezeEventBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.RestFieldBonus)) {//if ((flag & ExpGainTypes.RestFieldBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.RestFieldBonus));// bonus exp
            oPacket.EncodeInt(restFieldEXPRate);//rate
        }
        if (expIncreaseStats.containsKey(ExpType.HpRateBonus)) {//if ((flag & ExpGainTypes.HpRateBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.HpRateBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.FieldBonus)) {//if ((flag & ExpGainTypes.FieldBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.FieldBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.MobKillBonus)) {//if ((flag & ExpGainTypes.MobKillBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.MobKillBonus));
        }
        if (expIncreaseStats.containsKey(ExpType.LiveEventBonus)) {//if ((flag & ExpGainTypes.LiveEventBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.LiveEventBonus));
        }
        // Extra on global
        if (expIncreaseStats.containsKey(ExpType.PartyRingBonus)) {//if ((flag & ExpGainTypes.PartyRingBonus.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.PartyRingBonus));
        }
        // this should be last
        if (expIncreaseStats.containsKey(ExpType.MonsterCardSetCompletionBonus)) {//if ((flag & ExpGainTypes.UNK1.getValue()) != 0) {
            oPacket.EncodeInt(expIncreaseStats.get(ExpType.MonsterCardSetCompletionBonus));
        }
    }

}
