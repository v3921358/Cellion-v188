package constants;

/**
 *
 * @author Itzik
 */
public class WorldConstants {

    public static final int gmserver = -1; // -1 = no gm server

    /**
     *
     * @Warning: World will be duplicated if it's the same as the gm server
     */
    public static enum WorldOption {
        
        Reboot2(46, ServerConstants.EXP_RATE, ServerConstants.MESO_RATE, ServerConstants.DROP_RATE, 1, (byte) 2, false, false, 19),
        
        Arcane(50, 1, 1, 1, 1, (byte) 2, false, false, 19),
        Aurora(44, 1, 1, 1, 1, (byte) 2, false, false, 19),
        Reboot(44, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Red(43, 10, 2, 2, 1, (byte) 0, false, false, 19),
        Aether(42, 10, 2, 2, 1, (byte) 0, false, false, 19),
        Ultimate(41, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Phanteon(40, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Tempest(39, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Raven(38, 1, 1, 1, 1, (byte) 0, false, false, 19), //Translation: Raven
        Justice(37, 1, 1, 1, 1, (byte) 0, false, false, 19), //Translation: Justice
        Elf(36, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Legends(35, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Titan(34, 1, 1, 1, 1, (byte) 0, false, false, 19), //Translation: Titan
        Chaos2(33, 1, 1, 1, 1, (byte) 0, false, false, 19), //Translation: Chaos
        Luna(30, 1, 1, 1, 1, (byte) 5, false, false, 20),
        Androa(23, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Cosmo(22, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Aster(21, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Kastia(20, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Judis(19, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Croa(18, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Azwan(17, 1, 1, 1, 1, (byte) 0, false, false, 19),
        Regenades(16, 1, 1, 1, 1, (byte) 2, false, false, 20, "New World!"),
        Nova(15, 1, 1, 1, 1, (byte) 3, false, false, 20, "World Alliance:\rBellocan, Nova"),
        Chaos(14, 1, 1, 1, 1, (byte) 0, false, false, 20, "World Alliance:\rMardia, Kradia,\rYellonde,Chaos"),
        Arcania(13, 1, 1, 1, 1, (byte) 1, false, false, 20, "World Alliance:\rDemethos, Galicia, El Nido,\rZenith,Arcania"),
        Zenith(12, 1, 1, 1, 1, (byte) 1, false, false, 20, "World Alliance:\rDemethos, Galicia, El Nido,\rZenith,Arcania"),
        ElNido(11, 1, 1, 1, 1, (byte) 1, false, false, 20, "World Alliance:\rDemethos, Galicia, El Nido,\rZenith,Arcania"),
        Galicia(10, 1, 1, 1, 1, (byte) 1, false, false, 20, "World Alliance:\rDemethos, Galicia, El Nido,\rZenith,Arcania"),
        Demethos(9, 1, 1, 1, 1, (byte) 1, false, false, 20, "World Alliance:\rDemethos, Galicia, El Nido,\rZenith,Arcania"),
        Yellonde(8, 1, 1, 1, 1, (byte) 0, false, false, 20, "World Alliance:\rMardia, Kradia,\rYellonde,Chaos"),
        Kradia(7, 1, 1, 1, 1, (byte) 0, false, false, 20, "World Alliance:\rMardia, Kradia,\rYellonde,Chaos"),
        Mardia(6, 1, 1, 1, 1, (byte) 0, false, false, 20, "World Alliance:\rMardia, Kradia,\rYellonde,Chaos"),
        Bellocan(5, 1, 1, 1, 1, (byte) 3, false, false, 20, "World Alliance:\rBellocan, Nova"),
        Khaini(4, 1, 1, 1, 1, (byte) 0, false, false, 20),
        Windia(3, 1, 1, 1, 1, (byte) 0, false, false, 20),
        Broa(2, 100, 1000, 3, 1, (byte) 1, false, false, 20, "Fun 100x rates! RBS!"),
        Bera(1, 1, 1, 1, 1, (byte) 1, false, false, 20, "Low, GMS like rates!"),
        Scania(0, ServerConstants.EXP_RATE, ServerConstants.MESO_RATE, ServerConstants.DROP_RATE, 1, (byte) 2, true, true, 20, "Default world!");
        private final float exp, meso, drop, miraclecube;
        private final int world, channels;
        private final byte flag;
        private final boolean show, available;
        private final String worldtip;
        public static final byte recommended = (byte) Red.getWorld(); //-1 = no recommended
        public static final String recommendedmsg = "        Join " + getById(recommended).name() + ",       the newest world! (If you have friends who play, consider joining their world instead. Characters can`t move between worlds.)";

        WorldOption(int world, byte flag, boolean show, int channels) {
            this.world = world;
            this.exp = ServerConstants.EXP_RATE;
            this.meso = ServerConstants.MESO_RATE;
            this.drop = ServerConstants.DROP_RATE;
            this.miraclecube = ServerConstants.MIRACLE_CUBE_RATE;
            this.flag = flag;
            this.show = show;
            this.available = show;
            this.channels = channels;
            this.worldtip = "";
        }

        WorldOption(int world, float exp, float meso, float drop, float miraclecube, byte flag, boolean show, boolean available, int channels) {
            this.world = world;
            this.exp = exp;
            this.meso = meso;
            this.drop = drop;
            this.miraclecube = miraclecube;
            this.flag = flag;
            this.show = show;
            this.available = available;
            this.channels = channels;
            this.worldtip = "";
        }

        WorldOption(int world, float exp, float meso, float drop, float miraclecube, byte flag, boolean show, boolean available, int channels, String worldtip) {
            this.world = world;
            this.exp = exp;
            this.meso = meso;
            this.drop = drop;
            this.miraclecube = miraclecube;
            this.flag = flag;
            this.show = show;
            this.available = available;
            this.channels = channels;
            this.worldtip = worldtip;
        }

        public int getWorld() {
            return world;
        }

        public float getExp() {
            return exp;
        }

        public float getMeso() {
            return meso;
        }

        public float getDrop() {
            return drop;
        }

        public float getMiracleCube() {
            return miraclecube;
        }

        public byte getFlag() {
            return flag;
        }

        public boolean show() {
            return show;
        }

        public boolean isAvailable() {
            return available;
        }

        public int getChannelCount() {
            return channels;
        }

        public String getWorldTip() {
            return worldtip;
        }

        public static WorldOption getById(int g) {
            for (WorldOption e : WorldOption.values()) {
                if (e.world == g) {
                    return e;
                }
            }
            return null;
        }

        public static WorldOption getByName(String g) {
            for (WorldOption e : WorldOption.values()) {
                if (e.toString().equals(g)) {
                    return e;
                }
            }
            return null;
        }

        public static boolean isExists(int id) {
            return getById(id) != null;
        }
    }

    public static String getNameById(int serverid) {
        if (!WorldOption.isExists(serverid)) {
            System.out.println("World doesn't exists exception. ID: " + serverid);
            return "";
        }
        return WorldOption.getById(serverid).name();
    }

    public static enum TespiaWorldOption {
        Reboot("t44", 1, 1, 1, 1, (byte) 0, true, true, 19),
        Red("t43", 1, 1, 1, 1, (byte) 0, true, true, 19),
        Aether("t42", 1, 1, 1, 1, (byte) 0, true, true, 19),
        Ultimate("t41", 1, 1, 1, 1, (byte) 0, true, false, 19),
        Phanteon("t40", 1, 1, 1, 1, (byte) 0, true, false, 19),
        Tempest("t39", 1, 1, 1, 1, (byte) 0, true, false, 19),
        Raven("t38", 1, 1, 1, 1, (byte) 0, false, false, 19), //Translaition: Raven
        Justice("t37", 1, 1, 1, 1, (byte) 0, false, false, 19), //Translaition: Justice
        Elf("t36", 1, 1, 1, 1, (byte) 0, false, false, 19), //Translaition: Elf
        Legend("t35", 1, 1, 1, 1, (byte) 0, false, false, 19), //Translaition: Legend
        T34("t34", 1, 1, 1, 1, (byte) 0, false, false, 19),
        T33("t33", 1, 1, 1, 1, (byte) 0, false, false, 19),
        Androa("t32", 1, 1, 1, 1, (byte) 0, false, false, 19),
        Cosmo("t31", 1, 1, 1, 1, (byte) 0, false, false, 19),
        Nova("t30", 1, 1, 1, 1, (byte) 0, false, false, 19),
        Broa("t3", 1, 1, 1, 1, (byte) 0, false, false, 19), //Translation: Broa
        Bera("t2", 1, 1, 1, 1, (byte) 0, false, false, 19), //Translation: Bera
        Scania("t1", 1, 1, 1, 1, (byte) 0, false, false, 19), //Translation: Scania
        Tespia("t0", 1, 1, 1, 1, (byte) 0, true, false, 19);
        private final float exp, meso, drop, miraclecube;
        private final int channels;
        private final byte flag;
        private final String world;
        private final boolean show, available;
        public static final String recommended = "t0";
        public static final String recommendedmsg = "        Join " + getById(recommended).name() + ",       the newest world! (If youhave friends who play, consider joining their worldinstead. Characters can`t move between worlds.)";

        TespiaWorldOption(String world, float exp, float meso, float drop, float miraclecube, byte flag, boolean show, boolean available, int channels) {
            this.world = world;
            this.exp = exp;
            this.meso = meso;
            this.drop = drop;
            this.miraclecube = miraclecube;
            this.flag = flag;
            this.show = show;
            this.available = available;
            this.channels = channels;
        }

        public String getWorld() {
            return world;
        }

        public float getExp() {
            return exp;
        }

        public float getMeso() {
            return meso;
        }

        public float getDrop() {
            return drop;
        }

        public float getMiracleCube() {
            return miraclecube;
        }

        public byte getFlag() {
            return flag;
        }

        public boolean show() {
            return show;
        }

        public boolean isAvailable() {
            return available;
        }

        public int getChannelCount() {
            return channels;
        }

        public static TespiaWorldOption getById(String g) {
            for (TespiaWorldOption e : TespiaWorldOption.values()) {
                if (e.world.equals(g)) {
                    return e;
                }
            }
            return null;
        }

        public static TespiaWorldOption getByName(String g) {
            for (TespiaWorldOption e : TespiaWorldOption.values()) {
                if (e.name().equals(g)) {
                    return e;
                }
            }
            return null;
        }

        public static boolean isExists(String id) {
            return getById(id) != null;
        }
    }

    public static String getTespiaNameById(String serverid) {
        if (!TespiaWorldOption.isExists(serverid)) {
            System.out.println("Tespia World doesn't exists exception. ID: " + serverid);
            return "";
        }
        return TespiaWorldOption.getById(serverid).name();
    }
}
