package server.messages;

/**
 * @author Steven
 *
 */
public enum ExpGainTypes {

    MobBonus(0x1),
    PartyBonusPercentage(0x4),
    PartyBonus(0x10),
    WeddingBonus(0x20),
    ItemBonus(0x40),
    PremiumIpBonus(0x80),
    //
    RainbowWeekBonus(0x100),
    BoomUpBonus(0x200),
    ExpBuffBonus(0x400),
    PsdBonus(0x800),
    //
    IndieBonus(0x1000),
    RelaxBonus(0x2000),
    InstallItemBonus(0x4000),
    AzwanWinnerBonus(0x8000),
    //
    IncExpR(0x10000),
    ValuePackBonus(0x20000),
    IncPQExpR(0x40000),
    BaseAddExp(0x80000),
    //
    BloodAllianceBonus(0x100000), // Kin Rin bonus wtf
    FreezeEventBonus(0x200000),
    RestFieldBonus(0x400000),
    HpRateBonus(0x800000),
    //
    FieldBonus(0x1000000),
    MobKillBonus(0x2000000), // Accumulated hunt bonus EXP
    MonsterCardSetCompletionBonus(0x4000000), // custom naming, GMS special
    LiveEventBonus(0x8000000),
    //
    PartyRingBonus(0x10000000),;
    /*
	 if ( v3 & 0x800000 )
    v2[27] = CInPacket::Decode4(a2);
  if ( &dword_1000000 & v3 )
    v2[28] = CInPacket::Decode4(a2);
  if ( dword_2000000 & v3 )
    v2[29] = CInPacket::Decode4(a2);
  if ( v3 & 0x8000000 )
    v2[30] = CInPacket::Decode4(a2);
  if ( v3 & 0x10000000 )
    v2[31] = CInPacket::Decode4(a2);
  result = 0;
  if ( v3 & 0x4000000 )
  {
    result = CInPacket::Decode1(a2);
    v2[32] = result;
  }
  return result;
} 

	  some of the enum are wrong
	  
     */
    private final long flag;

    private ExpGainTypes(long flag) {
        this.flag = flag;
    }

    public long getValue() {
        return flag;
    }
}
