package constants;

/**
 *
 * @author
 */
public class SkillConstants {

    public static final int COMBO_KILL_BLESSING = 20000297,
            L_COMBO_KILL_BLESSING = 80000370;

    /**
     * Gets the link skill for the character.
     *
     * @param jobskillId The skillid to use if the character beings to this job group
     * @param linkskillId The link skillid to use if the character beings to other job group
     * @param currentjob Character's current job ID
     * @return The skillid
     */
    public static int getLinkSkillByJob(int jobskillId, int linkskillId, int currentjob) {
        if (GameConstants.isJobFamily(jobskillId / 1000, currentjob)) {
            return jobskillId;
        }
        return linkskillId;
    }
}
