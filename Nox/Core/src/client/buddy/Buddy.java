package client.buddy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Steven
 *
 */
public class Buddy {

    private BuddyResult result;
    private List<BuddylistEntry> entries = new ArrayList<>();
    private int capacity;
    private int job;
    private int level;
    private int subJob;

    public Buddy() {
    }

    public Buddy(BuddyResult result) {
        this.result = result;
    }

    /**
     * @return the result
     */
    public BuddyResult getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(BuddyResult result) {
        this.result = result;
    }

    /**
     * @return the entries
     */
    public List<BuddylistEntry> getEntries() {
        return entries;
    }

    /**
     * @param entries the entries to set
     */
    public void setEntries(List<BuddylistEntry> entries) {
        this.entries = entries;
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * It returns the first entry
     *
     * @return BuddylistEntry
     */
    public BuddylistEntry getEntry() {
        return entries.get(0);
    }

    /**
     * It sets the first entry
     *
     * @param entry
     */
    public void setEntry(BuddylistEntry entry) {
        entries.add(entry);
    }

    /**
     * @return jobId
     */
    public int getJob() {
        return job;
    }

    /**
     * Sets the jobId
     *
     * @param job
     */
    public void setJob(int job) {
        this.job = job;
    }

    /**
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level
     *
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the subJob
     */
    public int getSubJob() {
        return subJob;
    }

    /**
     * @param subJob the subJob to set
     */
    public void setSubJob(int subJob) {
        this.subJob = subJob;
    }

}
