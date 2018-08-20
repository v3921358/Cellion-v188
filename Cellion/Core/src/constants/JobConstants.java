package constants;

import static constants.GameConstants.isBeastTamer;
import static constants.GameConstants.isDualBlade;
import static constants.GameConstants.isEvan;
import static constants.GameConstants.isZero;

/**
 * JobConstants
 * @author Mazen Massoud
 */
public class JobConstants {

    /*Class Creation Configuration*/
 /*These values will be overwritten by the configuration.ini file.*/
    public static boolean bResistance = true,
                          bAdventurer = true,
                          bCygnus = true,
                          bAran = true,
                          bEvan = true,
                          bMercedes = true,
                          bDemon = true,
                          bPhantom = true,
                          bDualBlade = true,
                          bMihile = true,
                          bLuminous = true,
                          bKaiser = true,
                          bAngelicBuster = true,
                          bCannoneer = true,
                          bXenon = true,
                          bZero = true,
                          bShade = true,
                          bJett = true,
                          bHayato = true,
                          bKanna = true,
                          bBeastTamer = true,
                          bKinesis = true;

    // General Job Advancement Levels
    public static final int nFirstJobAdvancementLv = 10,
                            nSecondJobAdvancementLv = 30,
                            nThirdJobAdvancementLv = 60,
                            nFourthJobAdvancementLv = 100,
                            nFifthJobAdvancementLv = 200;

    // Dual Blade Special Advancement Levels
    public static final int nFirstJobPlusAdvancementLv_DualBlade = 20,
                            nSecondJobPlusAdvancementLv_DualBlade = 45;

    public enum LoginJob {

        Resistance(0, bResistance ? JobFlag.Available : JobFlag.Unavailable),
        Adventurer(1, bAdventurer ? JobFlag.Available : JobFlag.Unavailable),
        Cygnus(2, bCygnus ? JobFlag.Available : JobFlag.Unavailable),
        Aran(3, bAran ? JobFlag.Available : JobFlag.Unavailable),
        Evan(4, bEvan ? JobFlag.Available : JobFlag.Unavailable),
        Mercedes(5, bMercedes ? JobFlag.Available : JobFlag.Unavailable),
        Demon(6, bDemon ? JobFlag.Available : JobFlag.Unavailable),
        Phantom(7, bPhantom ? JobFlag.Available : JobFlag.Unavailable),
        DualBlade(8, bDualBlade ? JobFlag.Available : JobFlag.Unavailable),
        Mihile(9, bMihile ? JobFlag.Available : JobFlag.Unavailable),
        Luminous(10, bLuminous ? JobFlag.Available : JobFlag.Unavailable),
        Kaiser(11, bKaiser ? JobFlag.Available : JobFlag.Unavailable),
        AngelicBuster(12, bAngelicBuster ? JobFlag.Available : JobFlag.Unavailable),
        Cannoneer(13, bResistance ? JobFlag.Available : JobFlag.Unavailable),
        Xenon(14, bCannoneer ? JobFlag.Available : JobFlag.Unavailable),
        Zero(15, bZero ? JobFlag.Available : JobFlag.Unavailable),
        Shade(16, bShade ? JobFlag.Available : JobFlag.Unavailable),
        Jett(17, bJett ? JobFlag.Available : JobFlag.Unavailable),
        Hayato(18, bHayato ? JobFlag.Available : JobFlag.Unavailable),
        Kanna(19, bKanna ? JobFlag.Available : JobFlag.Unavailable),
        BeastTamer(20, bBeastTamer ? JobFlag.Available : JobFlag.Unavailable),
        PinkBean(21, JobFlag.Unavailable), // Monster Job (Event)
        Kinesis(22, bKinesis ? JobFlag.Available : JobFlag.Unavailable);

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

            Unavailable(0),
            Available(1);
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
     * Job Boolean Checks
     * @purpose Determines if a Job ID has reached a certain job class yet.
     */
    
    /**
     * hasSecondJob
     * @param nJobID
     * @return 
     */
    public static boolean hasSecondJob(int nJobID) {
        int nLastDigit = nJobID % 10;
        int nLastTwoDigits = nJobID % 100;
        
        if (isBeastTamer(nJobID) || isZero(nJobID)) {
            return true;
        }
        if (isEvan(nJobID)) { // Evan
            switch (nJobID) {
                //case 2210:
                case 2212:
                case 2214:
                case 2216:
                case 2217:
                case 2218:
                case 2219:
                    return true;
            }
        }
        if (isDualBlade(nJobID)) {
            switch (nJobID) {
                //case 430:
                //case 431:
                case 432:
                case 433:
                case 434:
                case 435:
                case 436:
                    return true;
            }
        }
        
        switch (nLastDigit) {
            case 1: // Third Job
            case 2: // Fourth Job
                return true;
        }
        switch (nLastTwoDigits) { // Second Job
            case 10:
            case 20:
            case 30:
            case 40:
            case 50:
                return true;
        }
        return false;
    }
    
    /**
     * hasThirdJob
     * @param nJobID
     * @return 
     */
    public static boolean hasThirdJob(int nJobID) {
        int nLastDigit = nJobID % 10;
        
        if (isBeastTamer(nJobID) || isZero(nJobID)) {
            return true;
        }
        if (isEvan(nJobID)) { // Evan
            switch (nJobID) {
                //case 2210:
                //case 2212:
                case 2214:
                case 2216:
                case 2217:
                case 2218:
                case 2219:
                    return true;
            }
        }
        if (isDualBlade(nJobID)) {
            switch (nJobID) {
                //case 430:
                //case 431:
                //case 432:
                case 433:
                case 434:
                case 435:
                case 436:
                    return true;
            }
        }
        
        switch (nLastDigit) {
            case 1: // Third Job
            case 2: // Fourth Job
                return true;
        }
        return false;
    }
    
    /**
     * hasFourthJob
     * @param nJobID
     * @return 
     */
    public static boolean hasFourthJob(int nJobID) {
        int nLastDigit = nJobID % 10;
        int nLastTwoDigits = nJobID % 100;
        
        if (isBeastTamer(nJobID) || isZero(nJobID)) {
            return true;
        }
        if (isEvan(nJobID)) { // Evan
            switch (nJobID) {
                //case 2210:
                //case 2212:
                //case 2214:
                //case 2216:
                case 2217:
                case 2218:
                case 2219:
                    return true;
            }
        }
        if (isDualBlade(nJobID)) {
            switch (nJobID) {
                //case 430:
                //case 431:
                //case 432:
                //case 433:
                //case 434:
                case 435:
                case 436:
                    return true;
            }
        }
        
        switch (nLastDigit) {
            case 2: // Fourth Job
                return true;
        }
        return false;
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
