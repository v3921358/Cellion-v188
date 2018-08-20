package client;

import java.util.concurrent.ScheduledFuture;

import server.StatEffect;

public class CharacterTemporaryStatValueHolder {

    public StatEffect effect;
    public long startTime;
    public int value, localDuration, cid;
    public ScheduledFuture<?> schedule;

    public CharacterTemporaryStatValueHolder(StatEffect effect, long startTime, ScheduledFuture<?> schedule, int value, int localDuration, int cid) {
        super();
        this.effect = effect;
        this.startTime = startTime;
        this.schedule = schedule;
        this.value = value;
        this.localDuration = localDuration;
        this.cid = cid;
    }
}
