package server.life;

/**
 *
 * @author Lloyd Korn
 */
public class MapleNPCStats {

    private int npcid;

    private String script;
    private String quest;
    private short trunk = 0;
    private boolean floating = false;
    private boolean mapletv = false;
    private boolean hideName = false;
    private boolean hide = false;
    private boolean shop = false;
    private int linkId = 0;
    private boolean componentNPC = false;  // Determines if NPC wears character items [based on itemid]
    private boolean talkMouseOnly = false;
    private boolean forceMove = false;

    private boolean skeleton = false;
    private boolean jsonLoad = false;

    public MapleNPCStats() {
    }

    /**
     * Get the value of npcid
     *
     * @return the value of npcid
     */
    public int getNpcid() {
        return npcid;
    }

    /**
     * Set the value of npcid
     *
     * @param npcid new value of npcid
     */
    public void setNpcid(int npcid) {
        this.npcid = npcid;
    }

    /**
     * Get the value of script
     *
     * @return the value of script
     */
    public String getScript() {
        return script;
    }

    /**
     * Set the value of script
     *
     * @param script new value of script
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Get the value of quest
     *
     * @return the value of quest
     */
    public String getQuest() {
        return quest;
    }

    /**
     * Set the value of quest
     *
     * @param quest new value of quest
     */
    public void setQuest(String quest) {
        this.quest = quest;
    }

    /**
     * Get the value of trunk
     *
     * @return the value of trunk
     */
    public short getTrunk() {
        return trunk;
    }

    /**
     * Set the value of trunk
     *
     * @param trunk new value of trunk
     */
    public void setTrunk(short trunk) {
        this.trunk = trunk;
    }

    /**
     * Get the value of floating
     *
     * @return the value of floating
     */
    public boolean isFloating() {
        return floating;
    }

    /**
     * Set the value of floating
     *
     * @param floating new value of floating
     */
    public void setFloating(boolean floating) {
        this.floating = floating;
    }

    /**
     * Get the value of mapletv
     *
     * @return the value of mapletv
     */
    public boolean isMapletv() {
        return mapletv;
    }

    /**
     * Set the value of mapletv
     *
     * @param mapletv new value of mapletv
     */
    public void setMapletv(boolean mapletv) {
        this.mapletv = mapletv;
    }

    /**
     * Get the value of hideName
     *
     * @return the value of hideName
     */
    public boolean isHideName() {
        return hideName;
    }

    /**
     * Set the value of hideName
     *
     * @param hideName new value of hideName
     */
    public void setHideName(boolean hideName) {
        this.hideName = hideName;
    }

    /**
     * Get the value of hide
     *
     * @return the value of hide
     */
    public boolean isHide() {
        return hide;
    }

    /**
     * Set the value of hide
     *
     * @param hide new value of hide
     */
    public void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     * Get the value of skeleton
     *
     * @return the value of skeleton
     */
    public boolean isSkeleton() {
        return skeleton;
    }

    /**
     * Set the value of skeleton
     *
     * @param skeleton new value of skeleton
     */
    public void setSkeleton(boolean skeleton) {
        this.skeleton = skeleton;
    }

    /**
     * Get the value of jsonLoad
     *
     * @return the value of jsonLoad
     */
    public boolean isJsonLoad() {
        return jsonLoad;
    }

    /**
     * Set the value of jsonLoad
     *
     * @param jsonLoad new value of jsonLoad
     */
    public void setJsonLoad(boolean jsonLoad) {
        this.jsonLoad = jsonLoad;
    }

    /**
     * Get the value of forceMove
     *
     * @return the value of forceMove
     */
    public boolean isForceMove() {
        return forceMove;
    }

    /**
     * Set the value of forceMove
     *
     * @param forceMove new value of forceMove
     */
    public void setForceMove(boolean forceMove) {
        this.forceMove = forceMove;
    }

    /**
     * Get the value of componentNPC
     *
     * @return the value of componentNPC
     */
    public boolean isComponentNPC() {
        return componentNPC;
    }

    /**
     * Set the value of componentNPC
     *
     * @param componentNPC new value of componentNPC
     */
    public void setComponentNPC(boolean componentNPC) {
        this.componentNPC = componentNPC;
    }

    /**
     * Get the value of talkMouseOnly
     *
     * @return the value of talkMouseOnly
     */
    public boolean isTalkMouseOnly() {
        return talkMouseOnly;
    }

    /**
     * Set the value of talkMouseOnly
     *
     * @param talkMouseOnly new value of talkMouseOnly
     */
    public void setTalkMouseOnly(boolean talkMouseOnly) {
        this.talkMouseOnly = talkMouseOnly;
    }

    /**
     * Get the value of shop
     *
     * @return the value of shop
     */
    public boolean isShop() {
        return shop;
    }

    /**
     * Set the value of shop
     *
     * @param shop new value of shop
     */
    public void setShop(boolean shop) {
        this.shop = shop;
    }

    /**
     * Get the value of linkId
     *
     * @return the value of linkId
     */
    public int getLinkId() {
        return linkId;
    }

    /**
     * Set the value of linkId
     *
     * @param linkId new value of linkId
     */
    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

}
