package server.life;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.wz.cache.WzDataStorage;
import tools.Pair;

public class MobSkillFactory {

    private static Map<Pair<Integer, Integer>, MobSkill> mobSkills = new HashMap<>();
    private static MapleDataProvider dataSource = WzDataStorage.getSkillWZ();
    private static final MapleData skillRoot = dataSource.getData("MobSkill.img");

    public static MobSkill getMobSkill(int skillId, int level) {
        MobSkill ret = mobSkills.get(new Pair<>(skillId, level));
        if (ret != null) {
            return ret;
        }

        final MapleData skillData = skillRoot.getChildByPath(skillId + "/level/" + level);
        if (skillData != null) {
            List<Integer> toSummon = new ArrayList<>();
            for (int i = 0; i > -1; i++) {
                if (skillData.getChildByPath(String.valueOf(i)) == null) {
                    break;
                }
                toSummon.add(MapleDataTool.getInt(skillData.getChildByPath(String.valueOf(i)), 0));
            }
            final MapleData ltd = skillData.getChildByPath("lt");
            Point lt = null;
            Point rb = null;
            if (ltd != null) {
                lt = (Point) ltd.getData();
                rb = (Point) skillData.getChildByPath("rb").getData();
            }
            ret = new MobSkill(skillId, level);
            ret.addSummons(toSummon);
            ret.setCoolTime(MapleDataTool.getInt("interval", skillData, 0) * 1000);
            ret.setDuration(MapleDataTool.getInt("time", skillData, 0) * 1000);
            ret.setHp(MapleDataTool.getInt("hp", skillData, 100));
            ret.setMpCon(MapleDataTool.getInt(skillData.getChildByPath("mpCon"), 0));
            ret.setSpawnEffect(MapleDataTool.getInt("summonEffect", skillData, 0));
            ret.setX(MapleDataTool.getInt("x", skillData, 1));
            ret.setY(MapleDataTool.getInt("y", skillData, 1));
            ret.setProp(MapleDataTool.getInt("prop", skillData, 100) / 100);
            ret.setLimit((short) MapleDataTool.getInt("limit", skillData, 0));
            ret.setLtRb(lt, rb);

            MobSkill put = mobSkills.put(new Pair<>(skillId, Integer.valueOf(level)), ret);
        }
        return ret;
    }
}
