package client.inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import provider.wz.cache.WzDataStorage;
import server.Randomizer;

public class PetDataFactory {

    private static final MapleDataProvider dataRoot = WzDataStorage.getItemWZ();
    private static final Map<Integer, List<PetCommand>> petCommands = new HashMap<>();
    private static final Map<Integer, Integer> petHunger = new HashMap<>();

    public static final PetCommand getRandomPetCommand(final int petId) {
        if (getPetCommand(petId, 0) == null) {
            return null;
        } //loaded, and checked if it existed
        final List<PetCommand> gg = petCommands.get(petId);
        return gg.get(Randomizer.nextInt(gg.size()));
    }

    public static final PetCommand getPetCommand(final int petId, final int skillId) {
        List<PetCommand> gg = petCommands.get(petId);
        if (gg != null) {
            if (gg.size() > skillId && gg.size() > 0) {
                return gg.get(skillId);
            }
            return null;
        }
        final MapleData skillData = dataRoot.getData("Pet/" + petId + ".img");
        int theSkill = 0;
        gg = new ArrayList<>();
        while (skillData != null) {
            MapleData dd = skillData.getChildByPath("interact/" + theSkill);
            if (dd == null) {
                break;
            }
            PetCommand retr = new PetCommand(petId, skillId, MapleDataTool.getInt("prob", dd, 0), MapleDataTool.getInt("inc", dd, 0));
            gg.add(retr);
            theSkill++;
        }
        petCommands.put(petId, gg);
        if (gg.size() <= skillId && gg.size() > 0) {
            return gg.get(skillId);
        }
        return null;
    }

    public static final int getHunger(final int petId) {
        Integer ret = petHunger.get(petId);
        if (ret != null) {
            return ret;
        }
        final MapleData hungerData = dataRoot.getData("Pet/" + petId + ".img").getChildByPath("info/hungry");
        ret = MapleDataTool.getInt(hungerData, 1);
        petHunger.put(petId, ret);

        return ret;
    }
}
