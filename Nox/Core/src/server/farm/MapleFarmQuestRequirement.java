package server.farm;

import client.QuestStatus.QuestState;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.maps.objects.User;
import server.quest.Quest;
import enums.QuestRequirementType;

/**
 *
 * @author Itzik
 */
public class MapleFarmQuestRequirement implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private Quest quest;
    private int intStore;
    private int reqCompleted;
    private int installObjectID;
    private int installObjectCount;
    private int farmLevel;
    private boolean repeat;
    private int npcClick;
    private boolean achieve;
    private int harvestCount;
    private int careCount;
    private int playCount;
    private int combineCount;
    private int decoPoint;

    /**
     * Creates a new instance of MapleQuestRequirement
     *
     * @param quest
     * @param type
     * @param rse
     * @throws java.sql.SQLException
     */
    public MapleFarmQuestRequirement(Quest quest, boolean repeatable, ResultSet rse) throws SQLException {
        this.quest = quest;
        if (repeatable) {
            intStore = Integer.parseInt(rse.getString("stringStore"));
        }
        reqCompleted = rse.getInt("reqCompleted");
        installObjectID = rse.getInt("installObjectID");
        installObjectCount = rse.getInt("installObjectCount");
        farmLevel = rse.getInt("farmLevel");
        repeat = rse.getInt("repeat") > 0;
        npcClick = rse.getInt("npcClick");
        achieve = rse.getInt("achieve") > 0;
        harvestCount = rse.getInt("harvestCount");
        careCount = rse.getInt("careCount");
        playCount = rse.getInt("playCount");
        combineCount = rse.getInt("combineCount");
        decoPoint = rse.getInt("decoPoint");
    }

    public boolean check(User c, boolean repeatable) { //getQuest into getFarmQuest
        if (repeatable && (c.getQuest(quest).getStatus() != QuestState.Completed || c.getQuest(quest).getCompletionTime() <= System.currentTimeMillis() - intStore * 60 * 1000L)) {
            return true;
        }
        return false;
    }

    public QuestRequirementType getType() {
        return QuestRequirementType.interval;
    }

    @Override
    public String toString() {
        return "interval";
    }
}
