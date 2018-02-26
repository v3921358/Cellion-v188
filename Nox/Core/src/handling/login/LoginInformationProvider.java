package handling.login;

import constants.ServerConstants;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.wz.cache.WzDataStorage;
import tools.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginInformationProvider {

    // Enables all jobs to start on the same map.
    // Obtains this data from our Server Constants.
    final static boolean universalStart = ServerConstants.UNIVERSAL_START;
    final static int setMap = ServerConstants.UNIVERSAL_START_MAP;

    public enum JobType {

        UltimateAdventurer(-1, 0, 100000000, true, true, false, false, true, false, false, false),
        Resistance(0, 3000, (universalStart ? setMap : 931000000), false, true, false, false, false, false, false, false), // Introduction Coded
        Adventurer(1, 0, (universalStart ? setMap : 10000), false, true, false, false, false, false, false, false), // Introduction Coded 
        Cygnus(2, 1000, (universalStart ? setMap : 130030000), false, true, false, false, false, true, false, false), // Introduction Coded
        Aran(3, 2000, (universalStart ? setMap : 914000000), false, true, false, false, true, false, false, false), // Introduction Coded
        Evan(4, 2001, (universalStart ? setMap : 900010000), false, true, false, false, true, false, false, false),
        Mercedes(5, 2002, (universalStart ? setMap : 910150000), false, false, false, false, false, false, false, false),// Introduction Coded
        Demon(6, 3001, (universalStart ? setMap : 927020070), false, false, true, false, false, false, false, false), // Introduction Coded
        Phantom(7, 2003, (universalStart ? setMap : 915000000), false, true, false, false, false, true, false, false),
        DualBlade(8, 0, (universalStart ? setMap : 103050900), false, true, false, false, false, false, false, false), // Introduction Coded
        Mihile(9, 5000, (universalStart ? setMap : 913070000), true, true, false, false, true, false, false, false),
        Luminous(10, 2004, (universalStart ? setMap : 931030000), false, true, false, false, false, true, false, false),
        Kaiser(11, 6000, (universalStart ? setMap : 940001000), false, true, false, false, false, false, false, false),
        AngelicBuster(12, 6001, (universalStart ? setMap : 940011000), false, true, false, false, false, false, false, false), // Introduction Coded
        Cannoneer(13, 0, (universalStart ? setMap : 3000000), true, true, false, false, true, false, false, false), // Introduction Coded
        Xenon(14, 3002, (universalStart ? setMap : 931050920), true, true, true, false, false, false, false, false),
        Zero(15, 10112, (universalStart ? setMap : 321000000), false, true, false, false, false, true, false, false),
        Shade(16, 2005, (universalStart ? setMap : 927030050), false, true, false, false, true, true, false, false),
        Jett(17, 0, (universalStart ? setMap : 552000050), false, false, false, false, false, true, false, false),
        Hayato(18, 4001, (universalStart ? setMap : 807000000), true, true, false, true, false, false, false, false),
        Kanna(19, 4002, (universalStart ? setMap : 807040000), true, true, false, true, false, false, false, false),
        BeastTamer(20, 11212, (universalStart ? setMap : 866000000), false, true, true, false, false, false, true, true),
        PinkBean(21, 13100, (universalStart ? setMap : 866000000), false, false, false, false, false, false, false, false),
        Kinesis(22, 14000, (universalStart ? setMap : 331001110), false, true, false, false, false, false, false, false);

        private final int type, jobid, map;
        private final boolean hairColor, skinColor, faceMark, hat, bottom, cape, ears, tail;

        private JobType(int type, int jobid, int map, boolean hairColor, boolean skinColor, boolean faceMark, boolean hat, boolean bottom, boolean cape, boolean ears, boolean tail) {
            this.type = type;
            this.jobid = jobid;
            this.map = map;
            this.hairColor = hairColor;
            this.skinColor = skinColor;
            this.faceMark = faceMark;
            this.hat = hat;
            this.bottom = bottom;
            this.cape = cape;
            this.ears = ears;
            this.tail = tail;
        }

        public int getType() {
            return type;
        }

        public int getJobId() {
            return jobid;
        }

        public int getStartingMapId() {
            return map;
        }

        public boolean hasHairColor() {
            return hairColor;
        }

        public boolean hasSkinColor() {
            return skinColor;
        }

        public boolean hasFaceMark() {
            return faceMark;
        }

        public boolean hasHat() {
            return hat;
        }

        public boolean hasBottom() {
            return bottom;
        }

        public boolean hasCape() {
            return cape;
        }

        public boolean hasEars() {
            return ears;
        }

        public boolean hasTail() {
            return tail;
        }

        public static JobType getByType(int g) {
            if (g == JobType.Cannoneer.type) {
                return JobType.Adventurer;
            }
            for (JobType e : JobType.values()) {
                if (e.type == g) {
                    return e;
                }
            }
            return null;
        }

        public static JobType getById(int g) {
            if (g == JobType.Adventurer.jobid) {
                return JobType.Adventurer;
            }
            if (g == 508) {
                return JobType.Jett;
            }
            for (JobType e : JobType.values()) {
                if (e.jobid == g) {
                    return e;
                }
            }
            return null;
        }
    }
    private final static LoginInformationProvider instance = new LoginInformationProvider();
    protected final List<String> ForbiddenName = new ArrayList<>();
    //gender, val, job
    protected final Map<Triple<Integer, Integer, Integer>, List<Integer>> makeCharInfo = new HashMap<>();
    //0 = eyes 1 = hair 2 = haircolor 3 = skin 4 = top 5 = bottom 6 = shoes 7 = weapon

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    protected LoginInformationProvider() {
        final String WZpath = System.getProperty("wzpath");
        final MapleDataProvider prov = WzDataStorage.getEtcWZ();

        // Forbidden Name
        MapleData nameData = prov.getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data));
        }

        // Curse Filter
        nameData = prov.getData("Curse.img");
        for (final MapleData data : nameData.getChildren().get(0).getChildren()) { //Nexon added a "Blacklist" and a "Whitelist" tag in the wz
            ForbiddenName.add(MapleDataTool.getString(data).split(",")[0]);
        }

        // Make character information
        final MapleData infoData = prov.getData("MakeCharInfo.img");

        for (MapleData data : infoData) {
            int type = -1;

            switch (data.getName()) {
                case "Name":
                case "Info":
                    break;
                case "3001_Dummy":
                case "10112_Dummy": // ???
                    break;
                case "UltimateAdventurer":
                    break;
                case "ResistanceCharMale":
                case "ResistanceCharFemale":
                    type = JobType.Resistance.type;
                    break;
                case "PremiumCharMale":
                case "PremiumCharFemale":
                    break;
                case "OrientCharMale":
                case "OrientCharFemale":
                    break;
                case "JumpingCharacter": // Some Lvl 50 character.. events? IDK
                    break;
                case "EvanCharMale":
                case "EvanCharFemale":
                    break;
                case "CharMale":
                case "CharFemale":
                    break;
                case "30000": // no idea what is this
                    break;
                case "11000": // beast tamer create info
                    break;
                case "000_1":
                    type = JobType.DualBlade.type;
                    break;
                default: {
                    try {
                        type = JobType.getById(Integer.parseInt(data.getName())).type;
                    } catch (Exception nfe) {
                        System.out.println("New character in Etc/MakeCharInfo, data name: " + data.getName());
                        continue;
                        //  throw new RuntimeException("New character in Etc/MakeCharInfo, data name: " + dat.getName());
                    }
                    break;
                }
            }
            if (type == -1) {
                continue; // not handled yet
            }

            for (MapleData d : data) {
                int val;
                if (d.getName().contains("female")) {
                    val = 1;
                } else if (d.getName().contains("male")) {
                    val = 0;
                } else {
                    continue;
                }
                for (MapleData da : d) {
                    int index;
                    Triple<Integer, Integer, Integer> key;
                    index = Integer.parseInt(da.getName());
                    key = new Triple<>(val, index, type);
                    List<Integer> our = makeCharInfo.get(key);
                    if (our == null) {
                        our = new ArrayList<>();
                        makeCharInfo.put(key, our);
                    }
                    for (MapleData dd : da) {
                        if (dd.getName().equalsIgnoreCase("color")) {
                            for (MapleData dda : dd) {
                                for (MapleData ddd : dda) {
                                    our.add(MapleDataTool.getInt(ddd, -1));
                                }
                            }
                        } else {
                            try {
                                our.add(MapleDataTool.getInt(dd, -1));
                            } catch (Exception ex) { //probably like color
                                for (MapleData dda : dd) {
                                    for (MapleData ddd : dda) {
                                        our.add(MapleDataTool.getInt(ddd, -1));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final MapleData uA = infoData.getChildByPath("UltimateAdventurer");
        for (MapleData dat : uA) {
            final Triple<Integer, Integer, Integer> key = new Triple<>(-1, Integer.parseInt(dat.getName()), JobType.UltimateAdventurer.type);
            List<Integer> our = makeCharInfo.get(key);
            if (our == null) {
                our = new ArrayList<>();
                makeCharInfo.put(key, our);
            }
            for (MapleData d : dat) {
                our.add(MapleDataTool.getInt(d, -1));
            }
        }
    }

    public final boolean isForbiddenName(final String in) {
        for (final String name : ForbiddenName) {
            if (in.toLowerCase().contains(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public final boolean isEligibleItem(final int gender, final int val, final int job, final int item) {
        if (item < 0) {
            return false;
        }
        final Triple<Integer, Integer, Integer> key = new Triple<>(gender, val, job);
        final List<Integer> our = makeCharInfo.get(key);
        if (our == null) {
            return false;
        }
        return our.contains(item);
    }
}
