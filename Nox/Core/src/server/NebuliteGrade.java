package server;

/**
 *
 * @author
 */
public enum NebuliteGrade {

    GradeS(4),
    GradeA(3),
    GradeB(2),
    GradeC(1),
    GradeD(0),
    None(-1);

    private final int value;

    private NebuliteGrade(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static NebuliteGrade getGradeByValue(int value) {
        for (NebuliteGrade grade : values()) {
            if (grade.getValue() == value) {
                return grade;
            }
        }
        return None;
    }

    /**
     * Returns the next upgradeable nebulite state. If this item is no longer upgradeable, the current state will be returned.
     *
     * @param current
     * @return
     */
    public static NebuliteGrade getNextGrade(NebuliteGrade current) {
        int nextGradeValue = current.getValue() + 1;

        NebuliteGrade nextGrade = getGradeByValue(nextGradeValue);
        if (nextGrade == None) {
            return current;
        }
        return nextGrade;
    }

    /**
     * Gets the higher nebulite grade of the two
     *
     * @param grade_first
     * @param grade_second
     * @return
     */
    public static NebuliteGrade getHigherRank(NebuliteGrade grade_first, NebuliteGrade grade_second) {
        if (grade_first.getValue() > grade_second.getValue()) {
            return grade_first;
        }
        return grade_second;
    }

    /**
     * Gets the lower nebulite grade of the two
     *
     * @param grade_first
     * @param grade_second
     * @return
     */
    public static NebuliteGrade getLowerRank(NebuliteGrade grade_first, NebuliteGrade grade_second) {
        if (grade_first.getValue() < grade_second.getValue()) {
            return grade_first;
        }
        return grade_second;
    }
}
