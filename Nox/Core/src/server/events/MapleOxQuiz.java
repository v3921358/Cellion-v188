package server.events;

import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import client.Stat;
import server.Timer.EventTimer;
import server.events.MapleOxQuizFactory.MapleOxQuizEntry;
import server.maps.MapleMap;
import server.maps.objects.User;
import tools.Pair;
import tools.packet.CField;
import tools.packet.WvsContext;

public class MapleOxQuiz extends MapleEvent {

    private ScheduledFuture<?> oxSchedule, oxSchedule2;
    private int timesAsked = 0;
    private boolean finished = false;

    public MapleOxQuiz(final int channel, final MapleEventType type) {
        super(channel, type);
    }

    @Override
    public void finished(User chr) { //do nothing.
    }

    private void resetSchedule() {
        if (oxSchedule != null) {
            oxSchedule.cancel(false);
            oxSchedule = null;
        }
        if (oxSchedule2 != null) {
            oxSchedule2.cancel(false);
            oxSchedule2 = null;
        }
    }

    @Override
    public void onMapLoad(User chr) {
        super.onMapLoad(chr);
        if (chr.getMapId() == type.mapids[0] && !chr.isGM()) {
            chr.canTalk(false);
        }
    }

    @Override
    public void reset() {
        super.reset();
        getMap(0).getPortal("join00").setPortalState(false);
        resetSchedule();
        timesAsked = 0;
    }

    @Override
    public void unreset() {
        super.unreset();
        getMap(0).getPortal("join00").setPortalState(true);
        resetSchedule();
    }
    //apparently npc says 10 questions

    @Override
    public void startEvent() {
        sendQuestion();
        finished = false;
    }

    public void sendQuestion() {
        sendQuestion(getMap(0));
    }

    public void sendQuestion(final MapleMap toSend) {
        final Entry<Pair<Integer, Integer>, MapleOxQuizEntry> question = MapleOxQuizFactory.getInstance().grabRandomQuestion();
        if (oxSchedule2 != null) {
            oxSchedule2.cancel(false);
        }
        oxSchedule2 = EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                int number = 0;
                for (User mc : toSend.getCharacters()) {
                    if (mc.isGM() || !mc.isAlive()) {
                        number++;
                    }
                }
                if (toSend.getCharactersSize() - number <= 1 || timesAsked == 10) {
                    toSend.broadcastPacket(WvsContext.broadcastMsg(6, "The event has ended"));
                    unreset();
                    for (User chr : toSend.getCharacters()) {
                        if (chr != null && !chr.isGM() && chr.isAlive()) {
                            chr.canTalk(true);
                            givePrize(chr);
                            warpBack(chr);
                        }
                    }
                    //prizes here
                    finished = true;
                    return;
                }

                toSend.broadcastPacket(CField.showOXQuiz(question.getKey().left, question.getKey().right, true));
                toSend.broadcastPacket(CField.getClock(10)); //quickly change to 12
            }
        }, 10000);
        if (oxSchedule != null) {
            oxSchedule.cancel(false);
        }
        oxSchedule = EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (finished) {
                    return;
                }
                toSend.broadcastPacket(CField.showOXQuiz(question.getKey().left, question.getKey().right, false));
                timesAsked++;
                for (User chr : toSend.getCharacters()) {
                    if (chr != null && !chr.isGM() && chr.isAlive()) { // make sure they aren't null... maybe something can happen in 12 seconds.
                        if (!isCorrectAnswer(chr, question.getValue().getAnswer())) {
                            chr.getStat().setHp((short) 0, chr);
                            chr.updateSingleStat(Stat.HP, 0);
                        } else {
                            chr.gainExp(3000, true, true, false);
                        }
                    }
                }
                sendQuestion();
            }
        }, 20000); // Time to answer = 30 seconds ( Ox Quiz packet shows a 30 second timer.
    }

    private boolean isCorrectAnswer(User chr, int answer) {
        double x = chr.getTruePosition().getX();
        double y = chr.getTruePosition().getY();
        if ((x > -234 && y > -26 && answer == 0) || (x < -234 && y > -26 && answer == 1)) {
            chr.dropMessage(6, "[Ox Quiz] Correct!"); //i think this is its own packet
            return true;
        }
        chr.dropMessage(6, "[Ox Quiz] Incorrect!");
        return false;
    }
}
