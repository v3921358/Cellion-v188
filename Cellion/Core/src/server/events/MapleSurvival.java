package server.events;

import java.util.concurrent.ScheduledFuture;

import server.Timer.EventTimer;
import server.maps.objects.User;
import tools.packet.CField;
import tools.packet.WvsContext;

public class MapleSurvival extends MapleEvent {

    protected long time = 360000; //reduce for less time
    protected long timeStarted = 0;
    protected ScheduledFuture<?> olaSchedule;

    public MapleSurvival(final int channel, final MapleEventType type) {
        super(channel, type);
    }

    @Override
    public void finished(final User chr) {
        givePrize(chr);
    }

    @Override
    public void onMapLoad(User chr) {
        super.onMapLoad(chr);
        if (isTimerStarted()) {
            chr.getClient().SendPacket(CField.getClock((int) (getTimeLeft() / 1000)));
        }
    }

    @Override
    public void startEvent() { // TODO: Messages
        unreset();
        super.reset(); //isRunning = true
        broadcast(CField.getClock((int) (time / 1000)));
        this.timeStarted = System.currentTimeMillis();

        olaSchedule = EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < type.mapids.length; i++) {
                    for (User chr : getMap(i).getCharacters()) {
                        warpBack(chr);
                    }
                    unreset();
                }
            }
        }, this.time);

        broadcast(WvsContext.broadcastMsg(0, "The portal has now opened. Press the up arrow key at the portal to enter."));
        broadcast(WvsContext.broadcastMsg(0, "Fall down once, and never get back up again! Get to the top without falling down!"));
    }

    public boolean isTimerStarted() {
        return timeStarted > 0;
    }

    public long getTime() {
        return time;
    }

    public void resetSchedule() {
        this.timeStarted = 0;
        if (olaSchedule != null) {
            olaSchedule.cancel(false);
        }
        olaSchedule = null;
    }

    @Override
    public void reset() {
        super.reset();
        resetSchedule();
        getMap(0).getPortal("join00").setPortalState(false);
    }

    @Override
    public void unreset() {
        super.unreset();
        resetSchedule();
        getMap(0).getPortal("join00").setPortalState(true);
    }

    public long getTimeLeft() {
        return time - (System.currentTimeMillis() - timeStarted);
    }
}
