package server.movement;

import java.awt.Point;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class AbstractLifeMovement implements LifeMovement {

    private byte command;
    private Point position;
    private Point wobble;
    private short foothold;
    private short footholdFallStart;
    private Point offset;
    private byte moveAction;
    private short tElapse;
    private byte bForcedStop;
    private byte numCommands;
    private byte bStat;
    private int randCnt;
    private int actualRandCount;

    public AbstractLifeMovement() {
        position = new Point(0, 0);
        wobble = new Point(0, 0);
    }

    /* (non-Javadoc)
	 * @see server.movement.LifeMovementFragment#serialize(tools.data.OutPacket)
     */
    @Override
    public void serialize(OutPacket oPacket) {
        // TODO Auto-generated method stub

    }

    /**
     * Returns the wobble.
     *
     * @return the wobble
     */
    public Point getWobble() {
        return wobble;
    }

    /**
     * Sets the wobble.
     *
     * @param wobble the wobble to set
     */
    public void setWobble(Point wobble) {
        this.wobble = wobble;
    }

    @Override
    public short getFoothold() {
        return foothold;
    }

    /**
     * Sets the foothold.
     *
     * @param foothold the foothold to set
     */
    public void setFoothold(short foothold) {
        this.foothold = foothold;
    }

    /**
     * Returns the footholdFallStart.
     *
     * @return the footholdFallStart
     */
    public short getFootholdFallStart() {
        return footholdFallStart;
    }

    /**
     * Sets the footholdFallStart.
     *
     * @param footholdFallStart the footholdFallStart to set
     */
    public void setFootholdFallStart(short footholdFallStart) {
        this.footholdFallStart = footholdFallStart;
    }

    /**
     * Returns the offset.
     *
     * @return the offset
     */
    public Point getOffset() {
        return offset;
    }

    /**
     * Sets the offset.
     *
     * @param offset the offset to set
     */
    public void setOffset(Point offset) {
        this.offset = offset;
    }

    /**
     * Sets the moveAction.
     *
     * @param moveAction the moveAction to set
     */
    public void setStance(byte moveAction) {
        this.moveAction = moveAction;
    }

    /**
     * Sets the tElapse.
     *
     * @param tElapse the tElapse to set
     */
    public void setDuration(short tElapse) {
        this.tElapse = tElapse;
    }

    /**
     * Returns the bForcedStop.
     *
     * @return the bForcedStop
     */
    public byte getbForcedStop() {
        return bForcedStop;
    }

    /**
     * Sets the randCnt.
     *
     * @param randCnt the randCnt to set
     */
    public void setForcedStop(byte bForcedStop) {
        this.bForcedStop = bForcedStop;
    }

    /**
     * Sets the command.
     *
     * @param command the command to set
     */
    public void setCommand(byte command) {
        this.command = command;
    }

    /**
     * Sets the position.
     *
     * @param position the position to set
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public int getStance() {
        return moveAction;
    }

    @Override
    public int getDuration() {
        return tElapse;
    }

    @Override
    public int getCommand() {
        return command;
    }

    /**
     * @return the numCommands
     */
    public byte getNumCommands() {
        return numCommands;
    }

    /**
     * @param numCommands the numCommands to set
     */
    public void setNumCommands(byte numCommands) {
        this.numCommands = numCommands;
    }

    /**
     * @return the bStat
     */
    public byte getbStat() {
        return bStat;
    }

    /**
     * @param bStat the bStat to set
     */
    public void setbStat(byte bStat) {
        this.bStat = bStat;
    }

    /**
     * @return the randCnt
     */
    public int getRandCnt() {
        return randCnt;
    }

    /**
     * @param randCnt the randCnt to set
     */
    public void setRandCnt(int randCnt) {
        this.randCnt = randCnt;
    }

    /**
     * @return the actualRandCount
     */
    public int getActualRandCount() {
        return actualRandCount;
    }

    /**
     * @param actualRandCount the actualRandCount to set
     */
    public void setActualRandCount(int actualRandCount) {
        this.actualRandCount = actualRandCount;
    }

}
