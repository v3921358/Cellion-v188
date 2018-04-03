package handling.world;

import java.util.EnumMap;
import java.util.concurrent.ScheduledFuture;

import client.CharacterTemporaryStat;
import java.util.Map;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.MapleStatEffect.CancelEffectAction;
import server.Timer.BuffTimer;
import server.maps.objects.User;
import tools.packet.BuffPacket;

public enum MapleFamilyBuff {

    Teleport("Family Reunion", "[Target] Me\n[Effect] Teleport directly to the Family member of your choice.", 0, 0, 0, 300, 190000),
    Summon("Summon Family", "[Target] 1 Family member\n[Effect] Summon a Family member of choice to the map you're in.", 1, 0, 0, 500, 190001),
    Drop_12_15("My Drop Rate 1.2x (15min)", "[Target] Me\n[Time] 15 min.\n[Effect] Monster drop rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 2, 15, 120, 700, 190002),
    EXP_12_15("My EXP Rate 1.2x (15min)", "[Target] Me\n[Time] 15 min.\n[Effect] Monster EXP rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 3, 15, 120, 800, 190003),
    Drop_12_30("My Drop Rate 1.2x (30min)", "[Target] Me\n[Time] 30 min.\n[Effect] Monster drop rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 2, 30, 120, 1000, 190004),
    //EXP_12_30("My EXP Rate 1.2x (30min)", "[Target] Me\n[Time] 30 min.\n[Effect] Monster EXP rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 3, 30, 120, 1200, 190005),
    Drop_15_15("My Drop Rate 1.5x (15min)", "[Target] Me\n[Time] 15 min.\n[Effect] Monster drop rate will be increased #c1.5x#.\n*  If the event is in progress, this will be nullified.", 2, 15, 150, 1500, 190009),
    //Drop_15_30("My Drop Rate 1.5x (30min)", "[Target] Me\n[Time] 30 min.\n[Effect] Monster drop rate will be increased #c1.5x#.\n*  If the event is in progress, this will be nullified.", 2, 30, 150, 2000, 190010),
    Bonding("Family Bonding (30min)", "[Target] At least 6 Family members online that are below me in the Pedigree\n[Time] 30 min.\n[Effect] Monster drop rate and EXP earned will be increased #c1.5x#. \n* If the EXP event is in progress, this will be nullified.", 4, 30, 150, 3000, 190006);
    //Drop_Party_12("My Party Drop Rate 1.2x (30min)", "[Target] Party\n[Time] 30 min.\n[Effect] Monster drop rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 2, 30, 120, 4000, 190007),
    //EXP_Party("My Party EXP Rate 1.2x (30min)", "[Target] Party\n[Time] 30 min.\n[Effect] Monster EXP rate will be increased #c1.2x#.\n*  If the event is in progress, this will be nullified.", 3, 30, 120, 5000, 190008),
    //Drop_Party_15("My Party Drop Rate 1.5x (30min)", "[Target] Party\n[Time] 30 min.\n[Effect] Monster drop rate will be increased #c1.5x#.\n*  If the event is in progress, this will be nullified.", 2, 30, 150, 7000, 190011);
    // 0=tele, 1=summ, 2=drop, 3=exp, 4=both

    public String name, desc;
    public int rep, type, questID, duration, effect;
    public EnumMap<CharacterTemporaryStat, Integer> effects;

    private MapleFamilyBuff(String name, String desc, int type, int duration, int effect, int rep, int questID) {
        this.name = name;
        this.desc = desc;
        this.rep = rep;
        this.type = type;
        this.questID = questID;
        this.duration = duration;
        this.effect = effect;
        setEffects();
    }

    public int getEffectId() {
        switch (type) {
            case 2: //drop
                return 2022694;
            case 3: //exp
                return 2450018;
        }
        return 2022332; //custom
    }

    public final void setEffects() {
        //custom
        this.effects = new EnumMap<>(CharacterTemporaryStat.class);
        switch (type) {
            case 2: //drop
                effects.put(CharacterTemporaryStat.DropRate, effect);
                effects.put(CharacterTemporaryStat.MesoUp, effect);
                break;
            case 3: //exp
                effects.put(CharacterTemporaryStat.ExpBuffRate, effect);
                break;
            case 4: //both
                effects.put(CharacterTemporaryStat.ExpBuffRate, effect);
                effects.put(CharacterTemporaryStat.DropRate, effect);
                effects.put(CharacterTemporaryStat.MesoUp, effect);
                break;
        }
    }

    public void applyTo(User chr) {
        for (Map.Entry<CharacterTemporaryStat, Integer> stat : effects.entrySet()) {
            chr.setBuffedValue(stat.getKey(), stat.getValue());
        }
        chr.getClient().write(BuffPacket.giveBuff(chr, -getEffectId(), duration * 60000, effects, null));
        final MapleStatEffect eff = MapleItemInformationProvider.getInstance().getItemEffect(getEffectId());
        chr.cancelEffect(eff, true, -1, effects);
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(chr, eff, starttime, effects);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, duration * 60000);
        chr.registerEffect(eff, starttime, schedule, effects, false, duration, chr.getId());
    }
}
