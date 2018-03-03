package constants.skills;

/**
 * @author NovaK
 *
 */
public enum DanceMoves {

    Prance(80001437),
    Moonwalk(80001438),
    Growl(80001439),
    Shovel(80001486),
    MesoRed(80001512),
    MesoBlue(80001513),
    MesoGreen(80001514),
    MesoYellow(80001515),
    MesoPink(80001516),
    MassDance(80001573),
    Headspin(80001574),
    HandsUp(800014575),
    PhotoPose(80001576),
    ShiningStarMale(80001577),
    ShiningStarFemale(80001578),
    ToTheLeft(80001603),
    Flutter(80001604),
    ToTheRight(80001605),
    Munch(80001606),
    Flutter2(80001607),
    Crunch(80001608),
    Offering(80011143);

    private final int skillid;

    private DanceMoves(int skillid) {
        this.skillid = skillid;
    }

    public int getSkillid() {
        return skillid;
    }
}