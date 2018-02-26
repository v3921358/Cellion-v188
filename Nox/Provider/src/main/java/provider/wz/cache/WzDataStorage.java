package provider.wz.cache;

import provider.wz.nox.NoxBinaryReader;
import java.io.File;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

/**
 * A unified place for all WZ and binary wz (custom format) file. This is to
 * avoid duplicate declaration..
 *
 * BinaryWZ will only be loaded on startup, and the stream should always me
 * immediately closed upon use
 *
 * @author
 */
public class WzDataStorage {

    private static NoxBinaryReader BinMapData, BinStringData, BinNPCData, BinTamingMobData, BinMobsData, BinItemData, BinSkillData;
    private static MapleDataProvider EtcWZ, SkillWZ, ItemWZ, StringWZ, CharacterWZ,
            NpcWZ,
            MobWZ, Mob2WZ,
            QuestWZ,
            ReactorWZ;

    /**
     * Dummy method to invoke on server startup
     */
    public static void load() {
        if (NpcWZ != null) {
            throw new RuntimeException("WzDataStorage already loaded!! "); // someone's really drunk if this happens
        }
        NpcWZ = MapleDataProviderFactory.getDataProvider(new File("wz/Npc.wz"));
        QuestWZ = MapleDataProviderFactory.getDataProvider(new File("wz/Quest.wz"));
        ReactorWZ = MapleDataProviderFactory.getDataProvider(new File("wz/Reactor.wz"));
        Mob2WZ = MapleDataProviderFactory.getDataProvider(new File("wz/Mob2.wz"));
        MobWZ = MapleDataProviderFactory.getDataProvider(new File("wz/Mob.wz"));
        EtcWZ = MapleDataProviderFactory.getDataProvider(new File("wz/Etc.wz"));
        SkillWZ = MapleDataProviderFactory.getDataProvider(new File("wz/Skill.wz"));
        ItemWZ = MapleDataProviderFactory.getDataProvider(new File("wz/Item.wz"));
        StringWZ = MapleDataProviderFactory.getDataProvider(new File("wz/String.wz"));
        CharacterWZ = MapleDataProviderFactory.getDataProvider(new File("wz/Character.wz"));
    }

    // private static NoxBinaryReader  BinReactorData;
    public static NoxBinaryReader getBinaryItemData() {
        if (BinItemData == null) {
            BinItemData = new NoxBinaryReader("wz_bin/Items.nox");
        }
        return BinItemData;
    }
    
    public static NoxBinaryReader getBinaryMapData() {
        if (BinMapData == null) {
            BinMapData = new NoxBinaryReader("wz_bin/Maps.nox");
        }
        return BinMapData;
    }

    public static NoxBinaryReader getBinaryStringData() {
        if (BinStringData == null) {
            BinStringData = new NoxBinaryReader("wz_bin/Strings.nox");
        }
        return BinStringData;
    }
    
    public static NoxBinaryReader getBinaryMobsData() {
        if (BinMobsData == null) {
            BinMobsData = new NoxBinaryReader("wz_bin/Mobs.nox");
        }
        return BinMobsData;
    }
    
    public static NoxBinaryReader getBinaryNPCData() {
        if (BinNPCData == null) {
            BinNPCData = new NoxBinaryReader("wz_bin/NPCs.nox");
        }
        return BinNPCData;
    }

    public static NoxBinaryReader getBinaryTamingMobData() {
        if (BinTamingMobData == null) {
            BinTamingMobData = new NoxBinaryReader("wz_bin/TamingMobs.nox");
        }
        return BinTamingMobData;
    }
    
    public static NoxBinaryReader getBinarySkillData() {
        if (BinSkillData == null) {
            BinSkillData = new NoxBinaryReader("wz_bin/Skill.nox");
        }
        return BinSkillData;
    }
    
    ///////////////////
    public static MapleDataProvider getNPCWz() {
        return NpcWZ;
    }

    public static MapleDataProvider getQuestWZ() {
        return QuestWZ;
    }

    public static MapleDataProvider getReactorWZ() {
        return ReactorWZ;
    }

    public static MapleDataProvider getMob2WZ() {
        return Mob2WZ;
    }

    public static MapleDataProvider getMobWZ() {
        return MobWZ;
    }
    
    public static MapleDataProvider getCharacterWZ() {
        return CharacterWZ;
    }

    public static MapleDataProvider getStringWZ() {
        return StringWZ;
    }

    public static MapleDataProvider getItemWZ() {
        return ItemWZ;
    }

    public static MapleDataProvider getSkillWZ() {
        return SkillWZ;
    }

    public static MapleDataProvider getEtcWZ() {
        return EtcWZ;
    }
}
