package client;

import java.io.Serializable;

/**
 *
 * @author Itzik
 */
public class Marriage implements Serializable {

    private int id;
    private int ring;
    private int husbandId;
    private int wifeId;
    private String husbandName;
    private String wifeName;

    public Marriage(int id) {
        this.id = id;
    }

    public Marriage(int id, int ring) {
        this.id = id;
        this.ring = ring;
    }

    public Marriage(int id, int ring, int husbandId, int wifeId, String husbandName, String wifeName) {
        this.id = id;
        this.ring = ring;
        this.husbandId = husbandId;
        this.wifeId = wifeId;
        this.husbandName = husbandName;
        this.wifeName = wifeName;
    }

    public int getId() {
        return id;
    }

    public int getRing() {
        return ring;
    }

    public void setRing(int ring) {
        this.ring = ring;
    }

    public int getHusbandId() {
        return husbandId;
    }

    public void setHusbandId(int husbandId) {
        this.husbandId = husbandId;
    }

    public int getWifeId() {
        return wifeId;
    }

    public void setWifeId(int wifeId) {
        this.wifeId = wifeId;
    }

    public String getHusbandName() {
        return husbandName;
    }

    public void setHusbandName(String husbandName) {
        this.husbandName = husbandName;
    }

    public String getWifeName() {
        return wifeName;
    }

    public void setWifeName(String wifeName) {
        this.wifeName = wifeName;
    }
}
