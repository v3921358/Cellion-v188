package server;

/**
 *
 * @author Lloyd Korn
 */
public class MapleTamingMobStats {

    private final int tamingMobId;
    private int speed = 0;
    private int jump = 0;
    private boolean continentMove = false;
    private boolean userSpeed = false;
    private boolean userJump = false;
    private int fatigue = 0;
    private float swim = 0;
    private float fs = 0;

    public MapleTamingMobStats(int tamingMobId) {
        this.tamingMobId = tamingMobId;
    }

    /**
     * Get the value of tamingMobId
     *
     * @return the value of tamingMobId
     */
    public int getTamingMobId() {
        return tamingMobId;
    }

    /**
     * Get the value of speed
     *
     * @return the value of speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Set the value of speed
     *
     * @param speed new value of speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Get the value of jump
     *
     * @return the value of jump
     */
    public int getJump() {
        return jump;
    }

    /**
     * Set the value of jump
     *
     * @param jump new value of jump
     */
    public void setJump(int jump) {
        this.jump = jump;
    }

    /**
     * Get the value of continentMove
     *
     * @return the value of continentMove
     */
    public boolean isContinentMove() {
        return continentMove;
    }

    /**
     * Set the value of continentMove
     *
     * @param continentMove new value of continentMove
     */
    public void setContinentMove(boolean continentMove) {
        this.continentMove = continentMove;
    }

    /**
     * Get the value of userSpeed
     *
     * @return the value of userSpeed
     */
    public boolean isUserSpeed() {
        return userSpeed;
    }

    /**
     * Set the value of userSpeed
     *
     * @param userSpeed new value of userSpeed
     */
    public void setUserSpeed(boolean userSpeed) {
        this.userSpeed = userSpeed;
    }

    /**
     * Get the value of userJump
     *
     * @return the value of userJump
     */
    public boolean isUserJump() {
        return userJump;
    }

    /**
     * Set the value of userJump
     *
     * @param userJump new value of userJump
     */
    public void setUserJump(boolean userJump) {
        this.userJump = userJump;
    }

    /**
     * Get the value of fatigue
     *
     * @return the value of fatigue
     */
    public int getFatigue() {
        return fatigue;
    }

    /**
     * Set the value of fatigue
     *
     * @param fatigue new value of fatigue
     */
    public void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }

    /**
     * Get the value of swim
     *
     * @return the value of swim
     */
    public float getSwim() {
        return swim;
    }

    /**
     * Set the value of swim
     *
     * @param swim new value of swim
     */
    public void setSwim(float swim) {
        this.swim = swim;
    }

    /**
     * Get the value of fs
     *
     * @return the value of fs
     */
    public float getFs() {
        return fs;
    }

    /**
     * Set the value of fs
     *
     * @param fs new value of fs
     */
    public void setFs(float fs) {
        this.fs = fs;
    }

}
