package server.commands;

import client.MapleClient;
import constants.ServerConstants;
import java.util.concurrent.ScheduledFuture;
import service.ChannelServer;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Timer;
import server.maps.objects.MapleCharacter;

/**
 * Unit test command just to test for racing conditions/threading issue in giving player buff... >>> LOL never to use this on production
 * server ever!
 *
 * @author
 */
public class ThreadTesterCommand {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.INTERNAL_DEVELOPER;
    }

    private static ScheduledFuture schedule = null;
    private static ScheduledFuture schedule2 = null;

    public static class StartTestBuffRacingCondition extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (schedule != null) {
                schedule.cancel(false);
                schedule = null;
            }
            if (schedule2 != null) {
                schedule2.cancel(false);
                schedule2 = null;
            }

            final ChannelServer ch = c.getChannelServer();

            final Runnable r = new Runnable() {

                @Override
                public void run() {
                    ch.getPlayerStorage().getAllCharacters().stream().forEach(character -> giveBuffInternal(character));

                }

                private void giveBuffInternal(MapleCharacter chr) {
                    MapleStatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(2022179); // onyx apple
                    chr.registerEffect(effect, 0, null, chr.getId());

                    chr.getAllBuffs();
                    chr.cancelMagicDoor();
                    chr.getMorphState();
                    chr.cancelMorphs();
                    chr.cancelAllBuffs();
                    chr.cancelBuffs();
                    chr.dispelBuff(123);
                    chr.dispelSummons();
                    chr.dispelSkill(123);
                    chr.dispel();

                    chr.registerEffect(effect, 0, null, chr.getId());

                    chr.getBuffedValuesPlayerStats();
                }
            };
            final Runnable r2 = new Runnable() {

                @Override
                public void run() {
                    ch.getPlayerStorage().getAllCharacters().stream().forEach(character -> giveBuffInternal(character));

                }

                private void giveBuffInternal(MapleCharacter chr) {
                    chr.getAllBuffs();
                    chr.cancelMagicDoor();
                    chr.getMorphState();
                    chr.cancelMorphs();
                    chr.cancelAllBuffs();
                    chr.cancelBuffs();
                    chr.dispelBuff(123);
                    chr.dispelSummons();
                    chr.dispelSkill(123);
                    chr.dispel();

                    MapleStatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(2022179); // onyx apple
                    chr.registerEffect(effect, 0, null, chr.getId());

                    chr.getBuffedValuesPlayerStats();

                    chr.registerEffect(effect, 0, null, chr.getId());
                    chr.registerEffect(effect, 0, null, chr.getId());
                }
            };
            schedule = Timer.MapTimer.getInstance().register(r, 10);
            schedule2 = Timer.MapTimer.getInstance().register(r2, 7);

            return 1;
        }
    }

    public static class StopTestBuffRacingCondition extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (schedule != null) {
                schedule.cancel(false);
                schedule = null;
            }
            if (schedule2 != null) {
                schedule2.cancel(false);
                schedule2 = null;
            }

            return 1;
        }
    }
}
