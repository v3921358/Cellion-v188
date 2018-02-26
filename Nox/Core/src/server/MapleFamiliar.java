package server;

public class MapleFamiliar {

    private int itemid, familiar, mob, passive;
    private byte grade;

    public MapleFamiliar() {
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public int getFamiliar() {
        return familiar;
    }

    public void setFamiliar(int familiar) {
        this.familiar = familiar;
    }

    public int getMob() {
        return mob;
    }

    public void setMob(int mob) {
        this.mob = mob;
    }

    public int getPassive() {
        return passive;
    }

    public void setPassive(int passive) {
        this.passive = passive;
    }

    public byte getGrade() {
        return grade;
    }

    public void setGrade(byte grade) {
        this.grade = grade;
    }
}
