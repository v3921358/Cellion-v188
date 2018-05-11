package constants;

/**
 *
 * @author Itzik
 * @author Mazen Massoud
 */
public class JobConstants {

    /*Class Creation Configuration*/
 /*These values will be overwritten by the configuration.ini file.*/
    public static boolean enableResistance = true;
    public static boolean enableAdventurer = true;
    public static boolean enableCygnus = true;
    public static boolean enableAran = true;
    public static boolean enableEvan = true;
    public static boolean enableMercedes = true;
    public static boolean enableDemon = true;
    public static boolean enablePhantom = true;
    public static boolean enableDualBlade = true;
    public static boolean enableMihile = true;
    public static boolean enableLuminous = true;
    public static boolean enableKaiser = true;
    public static boolean enableAngelicBuster = true;
    public static boolean enableCannoneer = true;
    public static boolean enableXenon = true;
    public static boolean enableZero = true;
    public static boolean enableShade = true;
    public static boolean enableJett = true;
    public static boolean enableHayato = true;
    public static boolean enableKanna = true;
    public static boolean enableBeastTamer = true;
    public static boolean enableKinesis = true;

    // General Job Advancement Levels
    public static final int _1stJobAdvancementLv = 10,
            _2ndJobAdvancementLv = 30,
            _3rdJobAdvancementLv = 60,
            _4thJobAdvancementLv = 100,
            _5thJobAdvancementLv = 200;

    // Dual Blade Special Advancement Levels
    public static final int DualBlade_1stJobPlusAdvancementLv = 20,
            DualBlade_2ndJobPlusAdvancementLv = 45;

    public enum LoginJob {

        Resistance(0, enableResistance ? JobFlag.ENABLED : JobFlag.DISABLED),
        Adventurer(1, enableAdventurer ? JobFlag.ENABLED : JobFlag.DISABLED),
        Cygnus(2, enableCygnus ? JobFlag.ENABLED : JobFlag.DISABLED),
        Aran(3, enableAran ? JobFlag.ENABLED : JobFlag.DISABLED),
        Evan(4, enableEvan ? JobFlag.ENABLED : JobFlag.DISABLED),
        Mercedes(5, enableMercedes ? JobFlag.ENABLED : JobFlag.DISABLED),
        Demon(6, enableDemon ? JobFlag.ENABLED : JobFlag.DISABLED),
        Phantom(7, enablePhantom ? JobFlag.ENABLED : JobFlag.DISABLED),
        DualBlade(8, enableDualBlade ? JobFlag.ENABLED : JobFlag.DISABLED),
        Mihile(9, enableMihile ? JobFlag.ENABLED : JobFlag.DISABLED),
        Luminous(10, enableLuminous ? JobFlag.ENABLED : JobFlag.DISABLED),
        Kaiser(11, enableKaiser ? JobFlag.ENABLED : JobFlag.DISABLED),
        AngelicBuster(12, enableAngelicBuster ? JobFlag.ENABLED : JobFlag.DISABLED),
        Cannoneer(13, enableResistance ? JobFlag.ENABLED : JobFlag.DISABLED),
        Xenon(14, enableCannoneer ? JobFlag.ENABLED : JobFlag.DISABLED),
        Zero(15, enableZero ? JobFlag.ENABLED : JobFlag.DISABLED),
        Shade(16, enableShade ? JobFlag.ENABLED : JobFlag.DISABLED),
        Jett(17, enableJett ? JobFlag.ENABLED : JobFlag.DISABLED),
        Hayato(18, enableHayato ? JobFlag.ENABLED : JobFlag.DISABLED),
        Kanna(19, enableKanna ? JobFlag.ENABLED : JobFlag.DISABLED),
        BeastTamer(20, enableBeastTamer ? JobFlag.ENABLED : JobFlag.DISABLED),
        PinkBean(21, JobFlag.DISABLED), // Monster Job (Event)
        Kinesis(22, enableKinesis ? JobFlag.ENABLED : JobFlag.DISABLED);

        private final int jobType, flag;

        private LoginJob(int jobType, JobFlag flag) {
            this.jobType = jobType;
            this.flag = flag.getFlag();
        }

        public int getJobType() {
            return jobType;
        }

        public int getFlag() {
            return flag;
        }

        public enum JobFlag {

            DISABLED(0),
            ENABLED(1);
            private final int flag;

            private JobFlag(int flag) {
                this.flag = flag;
            }

            public int getFlag() {
                return flag;
            }
        }
    }

    /**
     *
     * @param jobid (Eg: 312)
     * @param classId (Eg: 1, 2, 3, 4)
     * @return
     */
    public static boolean isJobClass(int jobid, int classId) {
        return jobid / 100 % 10 == classId; // divide by 100 and get last digit
    }
}
