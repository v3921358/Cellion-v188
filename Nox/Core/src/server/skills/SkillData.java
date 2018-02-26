/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.skills;

import java.io.IOException;
import java.util.HashMap;
import provider.wz.nox.NoxBinaryReader;

/**
 *
 * @author kaz_v
 */
public class SkillData {

    public int skillid;
    public boolean hasLevelData;
    private final HashMap<Integer, SkillLevelData> SkillLevelData;

    public SkillData(int skillid, boolean hasLevelData) {
        this.skillid = skillid;
        this.hasLevelData = hasLevelData;
        SkillLevelData = new HashMap<>();
    }

    public void addLevelData(NoxBinaryReader data, int level) throws IOException {
        if (!hasLevelData) {
            level = 0;
        }
        SkillLevelData.put(level, new SkillLevelData(data, hasLevelData));
    }
}
