package server.maps;

import enums.SavedLocationType;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import client.ClientSocket;
import client.QuestStatus;
import client.QuestStatus.QuestState;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import constants.GameConstants;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.provider.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.life.LifeFactory;
import server.life.Mob;
import server.maps.SharedMapResources.DirectionInfo;
import server.maps.objects.User;
import server.maps.objects.Reactor;
import server.quest.Quest;
import server.quest.Quest.MedalQuest;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CUserLocal;
import tools.packet.WvsContext;
import tools.packet.MobPacket;

public class MapScriptMethods {

    private static final Point witchTowerPos = new Point(-60, 184);
    private static final String[] mulungEffects = {
        "I have been waiting for you! If you have an ounce of courage in you, you'll be walking in that door right now!",
        "How brave of you to take on Mu Lung Training Tower!",
        "I will make sure you will regret taking on Mu Lung Training Tower!",
        "I do like your intestinal fortitude! But don't confuse your courage with recklessness!",
        "If you want to step on the path to failure, by all means to do so!"};

    private static enum onFirstUserEnter {
        //new Stuff
        visitor_summon,
        visitor_summon2,
        enter_visitor_1,
        enter_visitor_2,
        enter_visitor_3,
        enter_visitor_4,
        enter_visitor_5,
        
        mCastle_enter,
        mapFU_910028310,
        mapFU_910028360,
        mapFU_910028330,
        mapFU_910028350,
        PinkZakumEnt,
        boss_Event_PinkZakum,
        dojang_Eff,
        dojang_Msg,
        PinkBeen_before,
        onRewordMap,
        StageMsg_together,
        StageMsg_crack,
        StageMsg_davy,
        StageMsg_goddess,
        party6weatherMsg,
        StageMsg_juliet,
        StageMsg_romio,
        moonrabbit_mapEnter,
        astaroth_summon,
        boss_Ravana,
        boss_Ravana_mirror,
        killing_BonusSetting,
        killing_MapSetting,
        metro_firstSetting,
        balog_bonusSetting,
        balog_summon,
        easy_balog_summon,
        Sky_TrapFEnter,
        shammos_Fenter,
        PRaid_D_Fenter,
        PRaid_B_Fenter,
        summon_pepeking,
        Xerxes_summon,
        VanLeon_Before,
        cygnus_Summon,
        storymap_scenario,
        shammos_FStart,
        kenta_mapEnter,
        iceman_FEnter,
        iceman_Boss,
        prisonBreak_mapEnter,
        Visitor_Cube_poison,
        Visitor_Cube_Hunting_Enter_First,
        VisitorCubePhase00_Start,
        visitorCube_addmobEnter,
        Visitor_Cube_PickAnswer_Enter_First_1,
        visitorCube_medicroom_Enter,
        visitorCube_iceyunna_Enter,
        Visitor_Cube_AreaCheck_Enter_First,
        visitorCube_boomboom_Enter,
        visitorCube_boomboom2_Enter,
        CubeBossbang_Enter,
        MalayBoss_Int,
        mPark_summonBoss,
        magnus_summon,
        Ranmaru_Before,
        banban_Summon,
        pierre_Summon,
        pierre_Summon1,
        queen_summon0,
        abysscave_ent,
        sao_enterF01,
        sao_enterF02,
        sao_enterF03,
        sao_enterF04,
        sao_enterF05,
        sao_enterF06,
        sao_enterF07,
        sao_enterF08,
        sao_enterF09,
        sao_enterF10,
        sao_enterF11,
        sao_enterF12,
        sao_enterF13,
        sao_enterF14,
        sao_enterF15,
        sao_enterF16,
        sao_enterF17,
        sao_enterF18,
        sao_enterF19,
        sao_enterF20,
        NULL;

        private static onFirstUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    private static enum onUserEnter {

        PinkZak,
        Advanture_tuto00,
        Advanture_tuto01,
        Advanture_tuto02,
        Advanture_tuto04,
        Advanture_tuto10,
        Advanture_tuto11,
        Advanture_tuto33,
        Ranmaru_ExitCheck,
        root_camera,
        root_secret,
        root_ereb00,
        enter_101072002,
        enter_101073300,
        enter_101073201,
        enter_101073110,
        enter_101073010,
        enter_101070000,
        evolvingDirection1,
        evolvingDirection2,
        evolvingDirection3,
        np_tuto_0_0_before,
        np_tuto_0_0,
        enter_931060110,
        enter_931060120,
        dubl2Tuto0,
        dubl2Tuto10,
        dublTuto21,
        dublTuto23,
        enter_masRoom,
        enter_23214,
        map_913070000,
        map_913070001,
        map_913070002,
        map_913070003,
        map_913070004,
        map_913070020,
        map_913070050,
        mihail_direc,
        PTtutor000,
        PTtutor100,
        PTtutor200,
        PTtutor300,
        PTtutor301,
        PTtutor400,
        PTtutor500,
        PTjob1,
        PTjob2M,
        babyPigMap,
        crash_Dragon,
        cygnus_Minimap,
        check_q20833,
        evanleaveD,
        getDragonEgg,
        meetWithDragon,
        go1010100,
        go1010200,
        go1010300,
        go1010400,
        evanPromotion,
        PromiseDragon,
        evanTogether,
        incubation_dragon,
        TD_MC_Openning,
        TD_MC_gasi,
        TD_MC_title,
        startEreb,
        dojang_Msg,
        dojang_1st,
        reundodraco,
        undomorphdarco,
        explorationPoint,
        goAdventure,
        go10000,
        go20000,
        go30000,
        go40000,
        go50000,
        go1000000,
        go1010000,
        go1020000,
        go2000000,
        goArcher,
        goPirate,
        goRogue,
        goMagician,
        goSwordman,
        goLith,
        iceCave,
        mirrorCave,
        aranDirection,
        rienArrow,
        rien,
        check_count,
        Massacre_first,
        Massacre_result,
        aranTutorAlone,
        evanAlone,
        dojang_QcheckSet,
        Sky_StageEnter,
        outCase,
        balog_buff,
        balog_dateSet,
        Sky_BossEnter,
        Sky_GateMapEnter,
        shammos_Enter,
        shammos_Result,
        shammos_Base,
        dollCave00,
        dollCave01,
        dollCave02,
        Sky_Quest,
        enterBlackfrog,
        onSDI,
        blackSDI,
        summonIceWall,
        metro_firstSetting,
        start_itemTake,
        findvioleta,
        pepeking_effect,
        TD_MC_keycheck,
        TD_MC_gasi2,
        in_secretroom,
        sealGarden,
        TD_NC_title,
        TD_neo_BossEnter,
        PRaid_D_Enter,
        PRaid_B_Enter,
        PRaid_Revive,
        PRaid_W_Enter,
        PRaid_WinEnter,
        PRaid_FailEnter,
        Resi_tutor10,
        Resi_tutor20,
        Resi_tutor30,
        Resi_tutor40,
        Resi_tutor50,
        Resi_tutor60,
        Resi_tutor70,
        Resi_tutor80,
        Resi_tutor50_1,
        summonSchiller,
        q31102e,
        q2614M,
        q31103s,
        jail,
        VanLeon_ExpeditionEnter,
        cygnus_ExpeditionEnter,
        knights_Summon,
        TCMobrevive,
        mPark_stageEff,
        moonrabbit_takeawayitem,
        StageMsg_crack,
        shammos_Start,
        iceman_Enter,
        prisonBreak_1stageEnter,
        VisitorleaveDirectionMode,
        visitorPT_Enter,
        VisitorCubePhase00_Enter,
        visitor_ReviveMap,
        cannon_tuto_01,
        cannon_tuto_direction,
        cannon_tuto_direction1,
        cannon_tuto_direction2,
        userInBattleSquare,
        merTutorDrecotion00,
        merTutorDrecotion10,
        merTutorDrecotion20,
        merStandAlone,
        merOutStandAlone,
        merTutorSleep00,
        merTutorSleep01,
        merTutorSleep02,
        np_tuto_0_5,
        np_tuto_0_8,
        EntereurelTW,
        ds_tuto_ill0,
        ds_tuto_0_0,
        ds_tuto_1_0,
        ds_tuto_3_0,
        ds_tuto_3_1,
        ds_tuto_4_0,
        ds_tuto_5_0,
        ds_tuto_2_prep,
        ds_tuto_1_before,
        ds_tuto_2_before,
        enter_edelstein,
        angelic_tuto0,
        standbyAzwan,
        patrty6_1stIn,
        ds_tuto_home_before,
        ds_tuto_ani,
        azwan_stageEff,
        magnus_enter_HP,
        q53251_enter,
        q53244_dun_in,
        rootabyssTakeItem,
        PTjob3M,
        PTjob3M_1,
        PTjob3M2,
        PTjob4M,
        PTjob4M_1,
        PTjob4M2,
        hayatoJobChange,
        sao_enterRewardMap,
        sao_enterRewardMap2,
        sao_enter01,
        sao_enter02,
        sao_enter03,
        sao_enter04,
        sao_enter05,
        sao_enter06,
        sao_enter07,
        sao_enter08,
        sao_enter09,
        sao_enter10,
        sao_enter11,
        sao_enter12,
        sao_enter13,
        sao_enter14,
        sao_enter15,
        sao_enter16,
        sao_enter17,
        sao_enter18,
        sao_enter19,
        sao_enter20,
        lightning_tuto_1_0,
        onUserEnter_866100000,
        q59000_tuto,
        onUserEnter_866191000,
        onUserEnter_866103000,
        onUserEnter_866104000,
        onUserEnter_866105000,
        onUserEnter_866106000,
        onUserEnter_866107000,
        onUserEnter_866109000,
        onUserEnter_866135000,
        onUserEnter_866138000,
        onUserEnter_866000130,
        enter_866033000,
        direction_59054,
        direction_59061,
        direction_59063,
        direction_59070,
        direction_59070b,
        StageUserEnterSengo,
        aswan_stageEff,
        np_tuto_0_9,
        enter_underbase,
        kaiser_tuto_0_0,
        xn_tuto_0_0,
        resetEvolvingSystem,
        DI_Hillah,
        DI_HvsM,
        map940200060,
        map130030000,
        NULL;

        private static onUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    private static enum directionInfo {

        merTutorDrecotion01,
        merTutorDrecotion02,
        merTutorDrecotion03,
        merTutorDrecotion04,
        merTutorDrecotion05,
        merTutorDrecotion12,
        merTutorDrecotion21,
        ds_tuto_0_1,
        ds_tuto_0_2,
        ds_tuto_0_3,
        NULL;

        private static directionInfo fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    /**
     * Executes the hard coded map script,
     *
     * @param c
     * @param scriptName
     * @return true if executed, false if not available
     */
    public static boolean startScript_FirstUser(final ClientSocket c, String scriptName) {
        if (c.getPlayer() == null) {
            return true;
        }
        switch (onFirstUserEnter.fromString(scriptName)) {
            case visitor_summon: {
                if (c.getPlayer().getMap().getMobCount() == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390111), new Point(757, 32));
                    c.getPlayer().getMap().startMapEffect("The Epic Visitor has spawned, Hurry and take him out!", 5120111);
                    break;
                }
            }        
            case visitor_summon2: {
                if (c.getPlayer().getMap().getMobCount()== 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390113), new Point(750, 32));
                    c.getPlayer().getMap().startMapEffect("The Ultimate Visitor has spawned, Hurry and take him out!", 5120111);
                    break;
                }
            }
            case enter_visitor_1:
                c.SendPacket(CField.showEffect("Visitor/Stage1"));
                break;
            case enter_visitor_2:
                c.SendPacket(CField.showEffect("Visitor/Start"));
                break;
            case enter_visitor_3:
                c.SendPacket(CField.showEffect("Visitor/Start"));
                break;
            case enter_visitor_4:
                c.SendPacket(CField.showEffect("Visitor/Stage4"));
                break;
            case enter_visitor_5:
                c.SendPacket(CField.showEffect("Visitor/StageF"));
                break;
                
            case mCastle_enter:
                c.SendPacket(CField.MapEff("event/mCastle"));
                break;
            case mapFU_910028310:
                final MapleMap map = c.getPlayer().getMap();
                map.resetFully();
                c.getPlayer().getMap().startMapEffect("Be sure to clean up the Party Room!", 5120079);
                break;
            case mapFU_910028360:
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().startMapEffect("Get rid of the Whipped Cream Wight.", 5120079);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9500579), new Point(733, 146));
                break;
            case mapFU_910028330:
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().startMapEffect("Hunt down Witch Cats and collect 10 Party Outfix Boxes.", 5120079);
                break;
            case mapFU_910028350:
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().startMapEffect("Vanquish those ghosts and find the letter.", 5120079);
                break;
            case PinkZakumEnt:
                //  c.getPlayer().getMap().startEventTimer(6000000);
                //    c.getPlayer().getMap().resetFully();
                final EventManager eeem = c.getChannelServer().getEventSM().getEventManager("PinkZakumEntrance");
                final EventInstanceManager eim = eeem.getInstance(("PinkZakumEntrance"));
                //   eim.unregisterPlayer(c.getPlayer());
                eim.registerPlayer(c.getPlayer());
                eim.startEventTimer(45000);
                c.getPlayer().getMap().startMapEffect("Work together and defeat Pink Zakum!.", 5120039);
                break;
            case boss_Event_PinkZakum:
                c.getPlayer().getMap().startMapEffect("DO NOT BE ALARMED! The Pink Zakum clone was just to help adventurers like you relieve stress!", 5120039);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.getPlayer().getMap().spawnPinkZakum(-10, 329);
                        c.getPlayer().getMap().broadcastPacket(CField.musicChange("Bgm10.img/Eregos"));
                    }
                }, 10000);
                break;
            case dojang_Eff: {
                if (c.getPlayer().getMapId() == 925020100 || c.getPlayer().getMapId() == 925030100 || c.getPlayer().getMapId() == 925040100) {
                    c.getPlayer().getMap().startMapEffect("Don't forget that you have to clear it within the time limit! Take down the monster and head to the next floor!", 5120024);
                }
                int temp = (c.getPlayer().getMapId() - 925000000) / 100;
                int stage = (int) (temp - ((temp / 100) * 100));
//                String lol = c.getPlayer().getInfoQuest((int)7214);
//                System.err.println("ol " + lol);
//                int ad = Integer.parseInt(lol);

                sendDojoClock(c, 120);//getTiming(stage) * 60);
                sendDojoStart(c, stage - getDojoStageDec(stage));
                break;
            }
            case PinkBeen_before: {
                handlePinkBeanStart(c);
                break;
            }
            case onRewordMap: {
                reloadWitchTower(c);
                break;
            }
            //5120019 = orbis(start_itemTake - onUser)
            case moonrabbit_mapEnter: {
                c.getPlayer().getMap().startMapEffect("Gather the Primrose Seeds around the moon and protect the Moon Bunny!", 5120016);
                break;
            }

            case Ranmaru_Before: {
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9421581), new Point(109, 123));
                    break;
                }
            }
            case pierre_Summon: {
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0 
                        && (c.getPlayer().getMap().getId() >= 105200210 && c.getPlayer().getMap().getId() <= 105200219)) { // Spawn Normal Pierre
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8900100), new Point(497, 551));
                    c.getPlayer().getMap().startMapEffect("From the bottom of my heart, welcome to the tea party!", 5120098);
                    c.SendPacket(CField.showEffect("rootabyss/firework"));
                    c.SendPacket(CField.playSound("rootabyss/firework"));
                    c.SendPacket(CUserLocal.updateDeathCount(6));
                    break;
                } else if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8900000), new Point(497, 551));
                    c.getPlayer().getMap().startMapEffect("From the bottom of my heart, welcome to the tea party!", 5120098);
                    c.SendPacket(CField.showEffect("rootabyss/firework"));
                    c.SendPacket(CField.playSound("rootabyss/firework"));
                    c.SendPacket(CUserLocal.updateDeathCount(6));
                    break;
                }
            }
            case pierre_Summon1: {
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0 
                        && (c.getPlayer().getMap().getId() >= 105200210 && c.getPlayer().getMap().getId() <= 105200219)) { // Spawn Normal Pierre
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8900100), new Point(497, 551));
                    c.getPlayer().getMap().startMapEffect("From the bottom of my heart, welcome to the tea party!", 5120098);
                    c.SendPacket(CField.showEffect("rootabyss/firework"));
                    c.SendPacket(CField.playSound("rootabyss/firework"));
                    c.SendPacket(CUserLocal.updateDeathCount(6));
                } else if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8900000), new Point(497, 551));
                    c.getPlayer().getMap().startMapEffect("From the bottom of my heart, welcome to the tea party!", 5120098);
                    c.SendPacket(CField.showEffect("rootabyss/firework"));
                    c.SendPacket(CField.playSound("rootabyss/firework"));
                    c.SendPacket(CUserLocal.updateDeathCount(6));
                    break;
                }
            }
            case queen_summon0: {
                //c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) {
                    c.getPlayer().getMap().startMapEffect("Attempt to wake the Crimson Queen", 5120107);
                    c.SendPacket(CUserLocal.updateDeathCount(6));
                    //       c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8920000), new Point(4, 135));
                    break;
                }
            }
            case abysscave_ent: {
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8930000), new Point(256, 443));
                    c.SendPacket(CUserLocal.updateDeathCount(6));
                    break;
                }
            }
            case banban_Summon: {
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0 
                        && (c.getPlayer().getMap().getId() >= 105200110 && c.getPlayer().getMap().getId() <= 105200119)) { // Spawn Normal Von Bon
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8910100), new Point(256, 443));
                    c.SendPacket(CUserLocal.updateDeathCount(6));
                    break;
                } else if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) { // Chaos
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8910000), new Point(256, 443));
                    c.SendPacket(CUserLocal.updateDeathCount(6));
                    break;
                }
            }
            case sao_enterF01: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390706), new Point(-615, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390706), new Point(-364, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390706), new Point(78, 336));
                //   c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(2600208), new Point(736, 336));
                Mob theMob = LifeFactory.getMonster(2600208);
                /*   OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 15.0)); //10k to 4m 
                                    theMob.setOverrideStats(oms);*/
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(736, 336));
                break;
            }
            case sao_enterF02: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(-454, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(-119, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(563, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(995, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(527, 28));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(842, 28));
                Mob theMob = LifeFactory.getMonster(2600209);
                /*    OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 15.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(1225, 51));
                break;
            }
            case sao_enterF03: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(-593, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(-205, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(207, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(614, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(1017, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(842, 28));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(1218, -84));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(768, -84));
                Mob theMob = LifeFactory.getMonster(2600015);
                /* OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 15.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(-430, -410));
                break;
            }
            case sao_enterF04: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390702), new Point(-631, -204));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390702), new Point(-14, -204));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390702), new Point(505, -204));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390702), new Point(748, -204));
                Mob theMob = LifeFactory.getMonster(2600030);
                /*  OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 15.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(-12, 336));
                break;
            }
            case sao_enterF05: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);
                Mob theMob = LifeFactory.getMonster(9300003);
                /*  OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 1500.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(-12, 336));
                break;
            }
            case sao_enterF06: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390706), new Point(-615, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390706), new Point(-364, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390706), new Point(78, 336));
                //   c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(2600208), new Point(736, 336));
                Mob theMob = LifeFactory.getMonster(6220001);
                /*  OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 25.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(736, 336));
                break;
            }
            case sao_enterF07: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(-454, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(-119, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(563, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(995, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(527, 28));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390700), new Point(842, 28));
                Mob theMob = LifeFactory.getMonster(8210013);
                /* OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 3.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(1225, 51));
                break;
            }
            case sao_enterF08: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(-593, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(-205, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(207, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(614, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(1017, 336));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(842, 28));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(1218, -84));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390701), new Point(768, -84));
                Mob theMob = LifeFactory.getMonster(8220006);
                /*   OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 5.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(-430, -410));
                break;
            }
            case sao_enterF09: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390702), new Point(-631, -204));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390702), new Point(-14, -204));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390702), new Point(505, -204));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390702), new Point(748, -204));
                Mob theMob = LifeFactory.getMonster(8620009);
                /*  OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 5.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(-12, 336));
                break;
            }
            case sao_enterF10: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);
                Mob theMob = LifeFactory.getMonster(8620011);
                /* OverrideMonsterStats oms = new OverrideMonsterStats();
                                    oms.setOMp(theMob.getMobMaxMp());
                                    oms.setOExp(theMob.getMobExp() * 0);
                                    oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * 20.0)); //10k to 4m
                                    theMob.setOverrideStats(oms); */
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(theMob, new Point(-12, 336));
                break;
            }
            /*case abysscave_ent: {
                //c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) {
                    c.getPlayer().getMap().startMapEffect("Vellum is nowhere to be seen. Take a look around the altar", 5120107);
                    //     c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8930000), new Point(256, 443));
                    break;
                }
            }
            case banban_Summon: {
                //c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) {
                    c.getPlayer().getMap().startMapEffect("Summon Von Bon in the Dimensional Schism", 5120107);
                    //        c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8910000), new Point(256, 443));
                    break;
                }
            }*/
            case StageMsg_goddess: {
                switch (c.getPlayer().getMapId()) {
                    case 920010000:
                        c.getPlayer().getMap().startMapEffect("Please save me by collecting Cloud Pieces!", 5120019);
                        break;
                    case 920010100:
                        c.getPlayer().getMap().startMapEffect("Bring all the pieces here to save Minerva!", 5120019);
                        break;
                    case 920010200:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters and gather Statue Pieces!", 5120019);
                        break;
                    case 920010300:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters in each room and gather Statue Pieces!", 5120019);
                        break;
                    case 920010400:
                        c.getPlayer().getMap().startMapEffect("Play the correct LP of the day!", 5120019);
                        break;
                    case 920010500:
                        c.getPlayer().getMap().startMapEffect("Find the correct combination!", 5120019);
                        break;
                    case 920010600:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters and gather Statue Pieces!", 5120019);
                        break;
                    case 920010700:
                        c.getPlayer().getMap().startMapEffect("Get the right combination once you get to the top!", 5120019);
                        break;
                    case 920010800:
                        c.getPlayer().getMap().startMapEffect("Summon and defeat Papa Pixie!", 5120019);
                        break;
                }
                break;
            }
            case StageMsg_crack: {
                switch (c.getPlayer().getMapId()) {
                    case 922010100:
                        c.getPlayer().getMap().startMapEffect("Defeat all the Ratz!", 5120018);
                        break;
                    case 922010200:
                        c.getPlayer().getMap().startSimpleMapEffect("Collect all the passes!", 5120018);
                        break;
                    case 922010300:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters!", 5120018);
                        break;
                    case 922010400:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters in each room!", 5120018);
                        break;
                    case 922010500:
                        c.getPlayer().getMap().startMapEffect("Collect passes from each room!", 5120018);
                        break;
                    case 922010600:
                        c.getPlayer().getMap().startMapEffect("Get to the top!", 5120018);
                        break;
                    case 922010700:
                        c.getPlayer().getMap().startMapEffect("Destroy the Rombots!", 5120018);
                        break;
                    case 922010800:
                        c.getPlayer().getMap().startSimpleMapEffect("Get the right combination!", 5120018);
                        break;
                    case 922010900:
                        c.getPlayer().getMap().startMapEffect("Defeat Alishar!", 5120018);
                        break;
                }
                break;
            }
            case StageMsg_together: {
                switch (c.getPlayer().getMapId()) {
                    case 103000800:
                        c.getPlayer().getMap().startMapEffect("Solve the question and gather the amount of passes!", 5120017);
                        break;
                    case 103000801:
                        c.getPlayer().getMap().startMapEffect("Get on the ropes and unveil the correct combination!", 5120017);
                        break;
                    case 103000802:
                        c.getPlayer().getMap().startMapEffect("Get on the platforms and unveil the correct combination!", 5120017);
                        break;
                    case 103000803:
                        c.getPlayer().getMap().startMapEffect("Get on the barrels and unveil the correct combination!", 5120017);
                        break;
                    case 103000804:
                        c.getPlayer().getMap().startMapEffect("Defeat King Slime and his minions!", 5120017);
                        break;
                }
                break;
            }
            case StageMsg_romio: {
                switch (c.getPlayer().getMapId()) {
                    case 926100000:
                        c.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120021);
                        break;
                    case 926100001:
                        c.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120021);
                        break;
                    case 926100100:
                        c.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120021);
                        break;
                    case 926100200:
                        c.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120021);
                        break;
                    case 926100203:
                        c.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120021);
                        break;
                    case 926100300:
                        c.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120021);
                        break;
                    case 926100401:
                        c.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120021);

                        break;
                }
                break;
            }
            case StageMsg_juliet: {
                switch (c.getPlayer().getMapId()) {
                    case 926110000:
                        c.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120022);
                        break;
                    case 926110001:
                        c.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120022);
                        break;
                    case 926110100:
                        c.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120022);
                        break;
                    case 926110200:
                        c.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120022);
                        break;
                    case 926110203:
                        c.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120022);
                        break;
                    case 926110300:
                        c.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120022);
                        break;
                    case 926110401:
                        c.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120022);
                        break;
                }
                break;
            }
            case party6weatherMsg: {
                switch (c.getPlayer().getMapId()) {
                    case 930000000:
                        c.getPlayer().getMap().startMapEffect("Step in the portal to be transformed.", 5120023);
                        break;
                    case 930000100:
                        c.getPlayer().getMap().startMapEffect("Defeat the poisoned monsters!", 5120023);
                        break;
                    case 930000200:
                        c.getPlayer().getMap().startMapEffect("Eliminate the spore that blocks the way by purifying the poison!", 5120023);
                        break;
                    case 930000300:
                        c.getPlayer().getMap().startMapEffect("Uh oh! The forest is too confusing! Find me, quick!", 5120023);
                        break;
                    case 930000400:
                        c.getPlayer().getMap().startMapEffect("Purify the monsters by getting Purification Marbles from me!", 5120023);
                        break;
                    case 930000500:
                        c.getPlayer().getMap().startMapEffect("Find the Purple Magic Stone!", 5120023);
                        break;
                    case 930000600:
                        c.getPlayer().getMap().startMapEffect("Place the Magic Stone on the altar!", 5120023);
                        break;
                }
                break;
            }
            case prisonBreak_mapEnter: {
                break;
            }
            case StageMsg_davy: {
                switch (c.getPlayer().getMapId()) {
                    case 925100000:
                        c.getPlayer().getMap().startMapEffect("Defeat the monsters outside of the ship to advance!", 5120020);
                        break;
                    case 925100100:
                        c.getPlayer().getMap().startMapEffect("We must prove ourselves! Get me Pirate Medals!", 5120020);
                        break;
                    case 925100200:
                        c.getPlayer().getMap().startMapEffect("Defeat the guards here to pass!", 5120020);
                        break;
                    case 925100300:
                        c.getPlayer().getMap().startMapEffect("Eliminate the guards here to pass!", 5120020);
                        break;
                    case 925100400:
                        c.getPlayer().getMap().startMapEffect("Lock the doors! Seal the root of the Ship's power!", 5120020);
                        break;
                    case 925100500:
                        c.getPlayer().getMap().startMapEffect("Destroy the Lord Pirate!", 5120020);
                        break;
                }
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("Pirate");
                if (c.getPlayer().getMapId() == 925100500 && em != null && em.getProperty("stage5") != null) {
                    int mobId = Randomizer.nextBoolean() ? 9300107 : 9300119; //lord pirate
                    final int st = Integer.parseInt(em.getProperty("stage5"));
                    switch (st) {
                        case 1:
                            mobId = Randomizer.nextBoolean() ? 9300119 : 9300105; //angry
                            break;
                        case 2:
                            mobId = Randomizer.nextBoolean() ? 9300106 : 9300105; //enraged
                            break;
                    }
                    final Mob shammos = LifeFactory.getMonster(mobId);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                    }
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(411, 236));
                }
                break;
            }
            case astaroth_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9400633), new Point(600, -26)); //rough estimate
                break;
            }
            case boss_Ravana_mirror:
            case boss_Ravana: { //event handles this so nothing for now until i find out something to do with it
                c.getPlayer().getMap().broadcastPacket(WvsContext.broadcastMsg(5, "Ravana has appeared!"));
                break;
            }
            case killing_BonusSetting: { //spawns monsters according to mapid
                //910320010-910320029 = Train 999 bubblings.
                //926010010-926010029 = 30 Yetis
                //926010030-926010049 = 35 Yetis
                //926010050-926010069 = 40 Yetis
                //926010070-926010089 - 50 Yetis (specialized? immortality)
                //TODO also find positions to spawn these at
                c.getPlayer().getMap().resetFully();
                c.SendPacket(CField.showEffect("killing/bonus/bonus"));
                c.SendPacket(CField.showEffect("killing/bonus/stage"));
                Point pos1 = null, pos2 = null, pos3 = null;
                int spawnPer = 0;
                int mobId = 0;
                //9700019, 9700029
                //9700021 = one thats invincible
                if (c.getPlayer().getMapId() >= 910320010 && c.getPlayer().getMapId() <= 910320029) {
                    pos1 = new Point(121, 218);
                    pos2 = new Point(396, 43);
                    pos3 = new Point(-63, 43);
                    mobId = 9700020;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010010 && c.getPlayer().getMapId() <= 926010029) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010030 && c.getPlayer().getMapId() <= 926010049) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 15;
                } else if (c.getPlayer().getMapId() >= 926010050 && c.getPlayer().getMapId() <= 926010069) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 20;
                } else if (c.getPlayer().getMapId() >= 926010070 && c.getPlayer().getMapId() <= 926010089) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700029;
                    spawnPer = 20;
                } else {
                    break;
                }
                for (int i = 0; i < spawnPer; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(mobId), new Point(pos1));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(mobId), new Point(pos2));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(mobId), new Point(pos3));
                }
                c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
                break;
            }

            case mPark_summonBoss: {
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getEventInstance().getProperty("boss") != null && c.getPlayer().getEventInstance().getProperty("boss").equals("0")) {
                    for (int i = 9800119; i < 9800125; i++) {
                        final Mob boss = LifeFactory.getMonster(i);
                        c.getPlayer().getEventInstance().registerMonster(boss);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(boss, new Point(c.getPlayer().getMap().getPortal(2).getPosition()));
                    }
                }
                break;
            }
            case shammos_Fenter: {
                if (c.getPlayer().getMapId() >= 921120100 && c.getPlayer().getMapId() < 921120300) {
                    final Mob shammos = LifeFactory.getMonster(9300275);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (User pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return true;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    c.SendPacket(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));

                    /*} else if (c.getPlayer().getMapId() == (GameConstants.GMS ? 921120300 : 921120500) && c.getPlayer().getMap().getAllMonstersThreadsafe().size() == 0) {
                     final MapleMonster shammos = LifeFactory.getMonster(9300281);
                     if (c.getPlayer().getEventInstance() != null) {
                     int averageLevel = 0, size = 0;
                     for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                     averageLevel += pl.getLevel();
                     size++;
                     }
                     if (size <= 0) {
                     return;
                     }
                     averageLevel /= size;
                     shammos.changeLevel(Math.max(120, Math.min(200, averageLevel)));
                     }
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(350, 170));*/
                }
                break;
            }
            //5120038 =  dr bing. 5120039 = visitor lady. 5120041 = unknown dr bing.
            case iceman_FEnter: {
                if (c.getPlayer().getMapId() >= 932000100 && c.getPlayer().getMapId() < 932000300) {
                    final Mob shammos = LifeFactory.getMonster(9300438);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (User pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return true;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    c.SendPacket(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));

                }
                break;
            }
            case PRaid_D_Fenter: {
                switch (c.getPlayer().getMapId() % 10) {
                    case 0:
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    case 1:
                        c.getPlayer().getMap().startMapEffect("Break the boxes and eliminate the monsters!", 5120033);
                        break;
                    case 2:
                        c.getPlayer().getMap().startMapEffect("Eliminate the Officer!", 5120033);
                        break;
                    case 3:
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    case 4:
                        c.getPlayer().getMap().startMapEffect("Find the way to the other side!", 5120033);
                        break;
                }
                break;
            }
            case PRaid_B_Fenter: {
                c.getPlayer().getMap().startMapEffect("Defeat the Ghost Ship Captain!", 5120033);
                break;
            }
            case summon_pepeking: {
                c.getPlayer().getMap().resetFully();
                final int rand = Randomizer.nextInt(10);
                int mob_ToSpawn = 100100;
                if (rand >= 4) { //60%
                    mob_ToSpawn = 3300007;
                } else if (rand >= 1) {
                    mob_ToSpawn = 3300006;
                } else {
                    mob_ToSpawn = 3300005;
                }
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(mob_ToSpawn), c.getPlayer().getPosition());
                break;
            }
            case Xerxes_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(6160003), c.getPlayer().getPosition());
                break;
            }
            case shammos_FStart:
                c.getPlayer().getMap().startMapEffect("Defeat the monsters!", 5120035);
                break;
            case kenta_mapEnter:
                switch ((c.getPlayer().getMapId() / 100) % 10) {
                    case 1:
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120052);
                        break;
                    case 2:
                        c.getPlayer().getMap().startMapEffect("Get me 20 Air Bubbles for me to survive!", 5120052);
                        break;
                    case 3:
                        c.getPlayer().getMap().startMapEffect("Help! Make sure I live for three minutes!", 5120052);
                        break;
                    case 4:
                        c.getPlayer().getMap().startMapEffect("Eliminate the two Pianus!", 5120052);
                        break;
                } //TODOO find out which one it really is, lol
                break;
            case cygnus_Summon: {
                c.getPlayer().getMap().startMapEffect("It's been a long time since I've had guests. It's been even longer since any have left alive.", 5120043);
                break;
            }
            case iceman_Boss: {
                c.getPlayer().getMap().startMapEffect("You will perish!", 5120050);
                break;
            }
            case Visitor_Cube_poison: {
                c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120039);
                break;
            }
            case Visitor_Cube_Hunting_Enter_First: {
                c.getPlayer().getMap().startMapEffect("Eliminate all the Visitors!", 5120039);
                break;
            }
            case VisitorCubePhase00_Start: {
                c.getPlayer().getMap().startMapEffect("Eliminate all the flying monsters!", 5120039);
                break;
            }
            case visitorCube_addmobEnter: {
                c.getPlayer().getMap().startMapEffect("Eliminate all the monsters by moving around the map!", 5120039);
                break;
            }
            case Visitor_Cube_PickAnswer_Enter_First_1: {
                c.getPlayer().getMap().startMapEffect("One of the aliens must have a clue to the way out.", 5120039);
                break;
            }
            case visitorCube_medicroom_Enter: {
                c.getPlayer().getMap().startMapEffect("Eliminate all of the Unjust Visitors!", 5120039);
                break;
            }
            case visitorCube_iceyunna_Enter: {
                c.getPlayer().getMap().startMapEffect("Eliminate all of the Speedy Visitors!", 5120039);
                break;
            }
            case Visitor_Cube_AreaCheck_Enter_First: {
                c.getPlayer().getMap().startMapEffect("The switch at the top of the room requires a heavy weight.", 5120039);
                break;
            }
            case visitorCube_boomboom_Enter: {
                c.getPlayer().getMap().startMapEffect("The enemy is powerful! Watch out!", 5120039);
                break;
            }
            case visitorCube_boomboom2_Enter: {
                c.getPlayer().getMap().startMapEffect("This Visitor is strong! Be careful!", 5120039);
                break;
            }
            case CubeBossbang_Enter: {
                c.getPlayer().getMap().startMapEffect("This is it! Give it your best shot!", 5120039);
                break;
            }
            case MalayBoss_Int:
            case storymap_scenario:
            case VanLeon_Before:
            case dojang_Msg:
            case balog_summon:
            case easy_balog_summon: { //we dont want to reset
                break;
            }
            case metro_firstSetting:
            case killing_MapSetting:
            case Sky_TrapFEnter:
            case balog_bonusSetting: { //not needed
                c.getPlayer().getMap().resetFully();
                break;
            }
            case magnus_summon: {
                c.getPlayer().getMap().spawnObtacleAtom();
                c.getPlayer().getMap().resetFully();
                //  c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(8880000), c.getPlayer().getPosition());
                break;
            }

            default: {
                LogHelper.UNCODED.get().info("Unhandled script : " + scriptName + ", type : onFirstUserEnter - MAPID " + c.getPlayer().getMapId());
                return false;
            }
        }
        return true;
    }

    public static boolean startScript_User(final ClientSocket c, String scriptName) {
        if (c.getPlayer() == null) {
            return true;
        }
        String data = "";
        switch (onUserEnter.fromString(scriptName)) {
            case root_secret: {
                c.getPlayer().changeMap(105200000, 0);
            }
            case Advanture_tuto00: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    Thread.sleep(2100);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 30));
                    Thread.sleep(30);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/BalloonMsg0/0", 2100, 0, -120, 0, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2100));
                    Thread.sleep(2800);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 420));
                    Thread.sleep(420);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 420));
                    Thread.sleep(420);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 420));
                    Thread.sleep(420);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction12.img/effect/tuto/BalloonMsg0/1", 2100, 0, -120, 0, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 1800));
                    Thread.sleep(2800);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/BalloonMsg0/1", 2100, 0, -120, 0, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2100));
                    Thread.sleep(2800);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/key/0", 3000000, -300, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/key/0", 3000000, 0, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/key/0", 3000000, 300, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/key/1", 3000000, 540, 70));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 1200));
                    Thread.sleep(1800);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/BalloonMsg0/2", 2100, 0, -120, 0, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2100));
                    Thread.sleep(2800);
                    c.SendPacket(WvsContext.getTopMsg("Press the left and right keys to move."));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 3000));
                    Thread.sleep(3000);
                    c.SendPacket(WvsContext.getTopMsg("Go to where the portal is and press the up key to move to the next map."));

//                    MapleQuest.getInstance(32200).forceStart(c.getPlayer(), 0, null);
//                    MapleQuest.getInstance(32200).forceComplete(c.getPlayer(), 0);
//                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                }
                c.SendPacket(UIPacket.IntroEnableUI(false));
                break;
            }

            case Advanture_tuto01: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(WvsContext.enableActions());
                    c.getPlayer().getMap().resetFully();
                    final Reactor chain = new Reactor(MapleReactorFactory.getReactor(1008010), 1008010);
                    //String name = MapleDataTool.getString("info/link",MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Reactor.wz")).getData(StringUtil.getLeftPaddedStr(Integer.toString(1008010) + ".img", '0', 11)));
                    //System.out.println(name);
                    //chain.setName(name);
                    c.getPlayer().getMap().spawnReactorOnGroundBelow(chain, new Point(365, 216));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 3000));
                    Thread.sleep(3000);
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/BalloonMsg0/3", 2100, 0, -120, 0, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 1800));
                    Thread.sleep(1800);
                    c.SendPacket(WvsContext.getTopMsg("Press the Ctrl key to use normal attacks."));//Use @check before pressing the Ctrl key to use normal attacks on the chain."));
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                } catch (InterruptedException e) {
                }
                c.SendPacket(UIPacket.IntroEnableUI(false));
                break;
            }

            case Advanture_tuto02: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.environmentChange("demonSlayer/whiteOut", 12, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 1950));
                    Thread.sleep(1950);
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(4000010);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                } catch (InterruptedException e) {
                }
                break;
            }

            case Advanture_tuto10: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2100));
                    Thread.sleep(2100);
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    NPCScriptManager.getInstance().dispose(c);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 900));
                    Thread.sleep(900);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2100));
                    Thread.sleep(2100);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction3.img/effect/tuto/key/2", 3000000, -520, -740));
                    c.SendPacket(WvsContext.getTopMsg("Press the down key and Alt at the same time to go down."));
                } catch (InterruptedException e) {
                }
                c.SendPacket(UIPacket.IntroEnableUI(false));
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.SendPacket(WvsContext.enableActions());
                NPCScriptManager.getInstance().start(c, 10310, "ExplorerTut00");
                break;
            }

            case Advanture_tuto11:
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroLock(true));
                    c.SendPacket(CField.environmentChange("maplemap/enter/10000", 12, 0));
                    //NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    //NPCScriptManager.getInstance().start(c, 10310, "ExplorerTut06"); //old
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(NPCPacket.setNPCSpecialAction(814124, "summon"));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 1800));
                    Thread.sleep(1800);
                    c.SendPacket(NPCPacket.NPCSpecialAction(814124, 1, 1000));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent_new((byte) 0, 200));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 4542));
                    Thread.sleep(4542);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent_new((byte) 1, 300));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2834));
                    Thread.sleep(2834);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 900));
                    Thread.sleep(900);
                    c.SendPacket(NPCPacket.getSelfTalkText("Who was that girl? Why did she run away when she saw me?"));
                    Thread.sleep(3000);
                    c.SendPacket(NPCPacket.getSelfTalkText("Maybe I'll follow her.."));
                    c.SendPacket(UIPacket.IntroLock(false));
                } catch (InterruptedException e) {
                }
                break;

            case Advanture_tuto33: {
                c.getPlayer().getMap().resetFully();
                c.SendPacket(WvsContext.getTopMsg("Eliminate Mano."));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300815), new Point(0, 0));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                break;
            }

            case Advanture_tuto04: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroEnableUI(true));
                MapleMap mapto = c.getChannelServer().getMapFactory().getMap(4000005);
                c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroEnableUI(false));
                // c.write(UIPacket.playMovie("adventurer.avi", true));
                break;
            }

            case root_camera: {
                if (c.getPlayer().getQuestStatus(30000) == QuestState.Started) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 1064026, "AbyssTut01");
                    break;
                }
            }

            case root_ereb00: {
//                c.write(CField.UIPacket.IntroEnableUI(true));
//                c.write(CField.UIPacket.getDirectionStatus(true));
//                if (!c.getPlayer().getMap().containsNPC(1064026)) {
//                    c.getPlayer().getMap().spawnNpc(1064026, new Point(-113, 88));
//                }
//                NPCScriptManager.getInstance().dispose(c);
//                c.removeClickedNPC();
//                NPCScriptManager.getInstance().start(c, 1064026, "AbyssTut00");
                break;
            }

            case enter_101072002: {
                c.getPlayer().getMap().resetFully();
                c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500004, null);
                break;
            }
            case enter_101073300: {
                c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getQuestStatus(32128) == QuestState.Started) {
                    Quest.getInstance(32128).forceComplete(c.getPlayer(), 0);
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500016, null);
                break;
            }

            case enter_101073201: {
                c.getPlayer().getMap().resetFully();
                c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                if (!c.getPlayer().getMap().containsNPC(1500026)) {
                    c.getPlayer().getMap().spawnNpc(1500026, new Point(-369, 245));
                }
                if (!c.getPlayer().getMap().containsNPC(1500031)) {
                    c.getPlayer().getMap().spawnNpc(1500031, new Point(55, 245));
                }
                if (!c.getPlayer().getMap().containsNPC(1500032)) {
                    c.getPlayer().getMap().spawnNpc(1500032, new Point(200, 245));
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500026, null);
                break;
            }

            case enter_101073110: {
                c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getQuestStatus(32126) == QuestState.Started) {
                    Quest.getInstance(32126).forceComplete(c.getPlayer(), 0);
                }
                c.SendPacket(CField.getClock(10 * 60));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500019, null);
                break;
            }

            case enter_101073010: {
                c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getQuestStatus(32123) == QuestState.Started) {
                    Quest.getInstance(32123).forceComplete(c.getPlayer(), 0);
                }
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-53, 185));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-53, 185));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-53, 185));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-53, 185));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(3501006), new Point(-53, 185));
                c.SendPacket(CField.getClock(10 * 60));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500017, null);
                break;
            }

            case enter_101070000: {
                try {
                    c.SendPacket(WvsContext.getTopMsg("The forest of fairies seems to materialize from nowhere as you exit the passage."));
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                c.SendPacket(CField.MapEff("temaD/enter/fairyAcademy"));
                break;
            }

            case evolvingDirection1: {
                try {
                    Quest.getInstance(1801).forceStart(c.getPlayer(), 9075005, null);
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                    c.SendPacket(CField.MapEff("evolving/mapname"));
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9075005, "TutEvolving1");
                break;
            }

            case evolvingDirection2: {
                try {
                    Quest.getInstance(1801).forceComplete(c.getPlayer(), 0);
                    c.getPlayer().getMap().resetFully();
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                    c.SendPacket(CField.MapEff("evolving/swoo1"));
                    if (!c.getPlayer().getMap().containsNPC(9075004)) {
                        c.getPlayer().getMap().spawnNpc(9075004, new Point(70, 136));
                    }
                    Thread.sleep(14000);
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9075004, "TutEvolving2");
                break;
            }

            case enter_931060110: {
                c.getPlayer().saveLocation(SavedLocationType.fromString("TUTORIAL"));
                try {
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 4, 9072200));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 2));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1200));
                    Thread.sleep(1200);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 30));
                    Thread.sleep(30);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9072200, "enter_931060110");
            }
            case enter_931060120: {
                c.getPlayer().saveLocation(SavedLocationType.fromString("TUTORIAL"));
                try {
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 4, 9072200));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 2));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1200));
                    Thread.sleep(1200);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 30));
                    Thread.sleep(30);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9072200, "enter_931060120");
            }
            case rootabyssTakeItem: {
                break;
            }
            case cannon_tuto_direction: {
                if (c.getPlayer().getJob() == 0) {
                    c.getPlayer().gainItem(1532000, 1);//Cannon lvl 10
                    c.getPlayer().changeJob((short) 501);//cannoner
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(120000000);//pirate town
                    c.getPlayer().changeMap(mapto, mapto.getPortal(3));

                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                }
                showIntro(c, "Effect/Direction4.img/cannonshooter/Scene00");
                showIntro(c, "Effect/Direction4.img/cannonshooter/out00");
                break;
            }
            case cannon_tuto_direction1: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroLock(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction4.img/effect/cannonshooter/balloon/0", 5000, 0, 0, 1, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction4.img/effect/cannonshooter/balloon/1", 5000, 0, 0, 1, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction4.img/effect/cannonshooter/balloon/2", 5000, 0, 0, 1, 0));
                c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction4.img/cannonshooter/face04"));
                c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction4.img/cannonshooter/out01"));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 5000));
                break;
            }
            case cannon_tuto_direction2: {
                showIntro(c, "Effect/Direction4.img/cannonshooter/Scene01");
                showIntro(c, "Effect/Direction4.img/cannonshooter/out02");
                break;
            }
            case shammos_Enter: { //nothing to go on inside the map
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 921120300) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2022006, null);
                }
                break;
            }
            case iceman_Enter: { //nothing to go on inside the map
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 932000300) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2159020, null);
                }
                break;
            }
            case start_itemTake: { //nothing to go on inside the map
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("OrbisPQ");
                if (em != null && em.getProperty("pre").equals("0")) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2013001, null);
                }
                break;
            }
            case PRaid_W_Enter: {
                c.SendPacket(WvsContext.sendPyramidEnergy("PRaid_expPenalty", "0"));
                c.SendPacket(WvsContext.sendPyramidEnergy("PRaid_ElapssedTimeAtField", "0"));
                c.SendPacket(WvsContext.sendPyramidEnergy("PRaid_Point", "-1"));
                c.SendPacket(WvsContext.sendPyramidEnergy("PRaid_Bonus", "-1"));
                c.SendPacket(WvsContext.sendPyramidEnergy("PRaid_Total", "-1"));
                c.SendPacket(WvsContext.sendPyramidEnergy("PRaid_Team", ""));
                c.SendPacket(WvsContext.sendPyramidEnergy("PRaid_IsRevive", "0"));
                c.getPlayer().writePoint("PRaid_Point", "-1");
                c.getPlayer().writeStatus("Red_Stage", "1");
                c.getPlayer().writeStatus("Blue_Stage", "1");
                c.getPlayer().writeStatus("redTeamDamage", "0");
                c.getPlayer().writeStatus("blueTeamDamage", "0");
                break;
            }
            case jail: {
                if (!c.getPlayer().isIntern()) {
                    c.getPlayer().getQuestNAdd(Quest.getInstance(GameConstants.JAIL_TIME)).setCustomData(String.valueOf(System.currentTimeMillis()));
                    final QuestStatus stat = c.getPlayer().getQuestNAdd(Quest.getInstance(GameConstants.JAIL_QUEST));
                    if (stat.getCustomData() != null) {
                        final int seconds = Integer.parseInt(stat.getCustomData());
                        if (seconds > 0) {
                            c.getPlayer().startMapTimeLimitTask(seconds, c.getChannelServer().getMapFactory().getMap(950000100));
                        }
                    }
                }
                break;
            }
            case TD_neo_BossEnter:
            case findvioleta: {
                c.getPlayer().getMap().resetFully();
                break;
            }

            case StageMsg_crack:
                if (c.getPlayer().getMapId() == 922010400) { //2nd stage
                    MapleMapFactory mf = c.getChannelServer().getMapFactory();
                    int q = 0;
                    for (int i = 0; i < 5; i++) {
                        q += mf.getMap(922010401 + i).getAllMapObjectSize(MapleMapObjectType.MONSTER);
                    }
                    if (q > 0) {
                        c.getPlayer().dropMessage(-1, "There are still " + q + " monsters remaining.");
                    }
                } else if (c.getPlayer().getMapId() >= 922010401 && c.getPlayer().getMapId() <= 922010405) {
                    if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) > 0) {
                        c.getPlayer().dropMessage(-1, "There are still some monsters remaining in this map.");
                    } else {
                        c.getPlayer().dropMessage(-1, "There are no monsters remaining in this map.");
                    }
                }
                break;
            case q31102e:
                if (c.getPlayer().getQuestStatus(31102) == QuestState.Started) {
                    Quest.getInstance(31102).forceComplete(c.getPlayer(), 2140000);
                }
                break;
            case q31103s:
                if (c.getPlayer().getQuestStatus(31103) == QuestState.NotStarted) {
                    Quest.getInstance(31103).forceComplete(c.getPlayer(), 2142003);
                }
                break;
            case cygnus_Minimap:
                c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/OnUserEff.img/guideEffect/cygnusTutorial/0"));
                break;
            case check_q20833:
                if (c.getPlayer().getQuestStatus(20833) == QuestState.Started) {
                    Quest.getInstance(20833).forceComplete(c.getPlayer(), 0);
                    c.SendPacket(WvsContext.getTopMsg("Who's that on the right of the map?"));
                }
                break;
            case q2614M:
                if (c.getPlayer().getQuestStatus(2614) == QuestState.Started) {
                    Quest.getInstance(2614).forceComplete(c.getPlayer(), 0);
                }
                break;
            case Resi_tutor20:
                c.SendPacket(CField.MapEff("resistance/tutorialGuide"));
                break;
            case Resi_tutor30:
                c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/OnUserEff.img/guideEffect/resistanceTutorial/userTalk"));
                break;
            case Resi_tutor40:
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159012, null);
                break;
            case Resi_tutor50:
                c.SendPacket(UIPacket.getDirectionStatus(false));
                c.SendPacket(UIPacket.IntroLock(false));
                c.SendPacket(WvsContext.enableActions());
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159006, null);
                break;
            case Resi_tutor70:
                showIntro(c, "Effect/Direction4.img/Resistance/TalkJ");
                break;
            case prisonBreak_1stageEnter:
            case shammos_Start:
            case moonrabbit_takeawayitem:
            case TCMobrevive:
            case cygnus_ExpeditionEnter:
            case knights_Summon:
            case VanLeon_ExpeditionEnter:
            case Resi_tutor10:
            case Resi_tutor60:
            case Resi_tutor50_1:
            case sealGarden:
            case in_secretroom:
            case TD_MC_gasi2:
            case TD_MC_keycheck:
            case pepeking_effect:
            case userInBattleSquare:
            case summonSchiller:
            case VisitorleaveDirectionMode:
            case visitorPT_Enter:
            case VisitorCubePhase00_Enter:
            case visitor_ReviveMap:
            case PRaid_D_Enter:
            case PRaid_B_Enter:
            case PRaid_WinEnter: //handled by event
            case PRaid_FailEnter: //also
            case PRaid_Revive: //likely to subtract points or remove a life, but idc rly
            case metro_firstSetting:
            case blackSDI:
            case summonIceWall:
            case onSDI:
            case enterBlackfrog:
            case Sky_Quest: //forest that disappeared 240030102
            case dollCave00:
            case dollCave01:
            case dollCave02:
            case shammos_Base:
            case shammos_Result:
            case Sky_BossEnter:
            case Sky_GateMapEnter:
            case balog_dateSet:
            case balog_buff:
            case outCase:
            case Sky_StageEnter:
            case dojang_QcheckSet:
            case evanTogether:
            case merStandAlone:
            case EntereurelTW:
            case aranTutorAlone:
            case evanAlone: { //evan intro
                c.SendPacket(WvsContext.enableActions());
                break;
            }
            case merOutStandAlone: {
                if (c.getPlayer().getQuestStatus(24001) == QuestState.Started) {
                    Quest.getInstance(24001).forceComplete(c.getPlayer(), 0);
                    c.getPlayer().dropMessage(5, "Quest complete.");
                }
                break;
            }

            case np_tuto_0_5: {
                //NPCConversationManager cmnp_tuto_0_5= new NPCConversationManager(c, 0, 0, (byte)0, null);
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(CField.showEffect("phantom/back"));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 4));
                c.SendPacket(CField.showEffect("newPirate/Shuttle/0"));
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                }
                c.SendPacket(CField.showEffect("phantom/back"));
                c.SendPacket(CField.showEffect("newPirate/TimeTravel/0"));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                c.SendPacket(CField.showEffect("newPirate/text1"));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }

                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        NPCScriptManager.getInstance().start(c, 9270084, "np_tuto_0_5");
                    }
                }, 2000);

                break;
            }

            case np_tuto_0_8: {
                c.SendPacket(UIPacket.IntroEnableUI(true));
                if (!c.getPlayer().getMap().containsNPC(9270084)) {
                    c.getPlayer().getMap().spawnNpc(9270084, new Point(146, -120));
                }
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));

                        c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/DirectionNewPirate.img/effect/tuto/pirateAttack", 2000, 0, -100, 1, 0));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/DirectionNewPirate.img/newPirate/balloonMsg2/17", 2000, 0, -100, 1, 0));
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                        }
                        MapleMap mapto = c.getChannelServer().getMapFactory().getMap(552000050);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    }
                }, 5000);

                break;
            }

            case merTutorSleep00: {
                showIntro(c, "Effect/Direction5.img/mersedesTutorial/Scene0");
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                sa.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20020109), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20021110), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20020111), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(20020112), new SkillEntry((byte) 1, (byte) 1, -1));
                c.getPlayer().changeSkillsLevel(sa);
                break;
            }
            case merTutorSleep01: {
                while (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().OnLevelUp();
                }
                c.getPlayer().changeJob((short) 2300);
                showIntro(c, "Effect/Direction5.img/mersedesTutorial/Scene1");
                break;
            }
            case merTutorSleep02: {
                c.SendPacket(UIPacket.IntroEnableUI(false));
                break;
            }
            case merTutorDrecotion00: {
                c.SendPacket(UIPacket.playMovie("Mercedes.avi", true));
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                sa.put(SkillFactory.getSkill(20021181), new SkillEntry((byte) 1, (byte) 1, -1));
                //sa.put(SkillFactory.getSkill(20021166), new SkillEntry((byte) 1, (byte) 1, -1));
                c.getPlayer().changeSkillsLevel(sa);
                break;
            }
            case merTutorDrecotion10: {
                while (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().OnLevelUp();
                }
//                c.getPlayer().changeJob((short) 2300);
                final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(101050000);
                c.getPlayer().changeMap(mapto, mapto.getPortal(0));
//                c.write(UIPacket.getDirectionStatus(true));
//                c.write(UIPacket.IntroEnableUI(true));
//                c.write(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/6", 2000, 0, -100, 1, 0));
//                c.write(UIPacket.getDirectionInfo(1, 2000));
//                c.getPlayer().setDirection(0);
                break;
            }
            case merTutorDrecotion20: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/mercedesInIce/merBalloon/9", 2000, 0, -100, 1, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2000));
                c.getPlayer().setDirection(0);
                break;
            }
            case ds_tuto_ani: {
                c.SendPacket(UIPacket.playMovie("DemonSlayer1.avi", true));
                break;
            }
            case Resi_tutor80:
            case startEreb:
            case mirrorCave:
            case babyPigMap:
            case evanleaveD: {
                c.SendPacket(UIPacket.getDirectionStatus(false));
                c.SendPacket(UIPacket.IntroLock(false));
                c.SendPacket(WvsContext.enableActions());
                break;
            }
            case dojang_Msg: {
                c.getPlayer().getMap().startMapEffect(mulungEffects[Randomizer.nextInt(mulungEffects.length)], 5120024);
                break;
            }
            case dojang_1st: {
                c.getPlayer().writeMulungEnergy();
                break;
            }
            case undomorphdarco:
            case reundodraco: {
                c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(2210016), false, -1);
                break;
            }
            case goAdventure: {
                showIntro(c, "Effect/Direction3.img/goAdventure/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case crash_Dragon:
                showIntro(c, "Effect/Direction4.img/crash/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case getDragonEgg:
                showIntro(c, "Effect/Direction4.img/getDragonEgg/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case meetWithDragon:
                showIntro(c, "Effect/Direction4.img/meetWithDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case PromiseDragon:
                showIntro(c, "Effect/Direction4.img/PromiseDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case evanPromotion:
                switch (c.getPlayer().getMapId()) {
                    case 900090000:
                        data = "Effect/Direction4.img/promotion/Scene0" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090001:
                        data = "Effect/Direction4.img/promotion/Scene1";
                        break;
                    case 900090002:
                        data = "Effect/Direction4.img/promotion/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090003:
                        data = "Effect/Direction4.img/promotion/Scene3";
                        break;
                    case 900090004:
                        c.SendPacket(UIPacket.getDirectionStatus(false));
                        c.SendPacket(UIPacket.IntroLock(false));
                        c.SendPacket(WvsContext.enableActions());
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(900010000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        return true;
                }
                showIntro(c, data);
                break;
            case mPark_stageEff:
                c.getPlayer().dropMessage(-1, "All monsters must be eliminated before proceeding to the next stage.");
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        c.SendPacket(CField.MapEff("monsterPark/stageEff/stage"));
                        c.SendPacket(CField.MapEff("monsterPark/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100) + 1)));
                        break;
                    case 4:
                        if (c.getPlayer().getMapId() / 1000000 == 952) {
                            c.SendPacket(CField.MapEff("monsterPark/stageEff/final"));
                        } else {
                            c.SendPacket(CField.MapEff("monsterPark/stageEff/stage"));
                            c.SendPacket(CField.MapEff("monsterPark/stageEff/number/5"));
                        }
                        break;
                    case 5:
                        c.SendPacket(CField.MapEff("monsterPark/stageEff/final"));
                        break;
                }

                break;
            case aswan_stageEff:
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 1:
                    case 2:
                        c.SendPacket(CField.MapEff("aswan/stageEff/stage"));
                        c.SendPacket(CField.MapEff("aswan/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100))));
                        break;
                    case 3:
                        c.SendPacket(CField.MapEff("aswan/stageEff/final"));
                        break;
                }
                break;
            case TD_MC_title: {
                c.SendPacket(UIPacket.getDirectionStatus(false));
                c.SendPacket(UIPacket.IntroLock(false));
                c.SendPacket(WvsContext.enableActions());
                c.SendPacket(CField.MapEff("temaD/enter/mushCatle"));
                break;
            }
            case TD_NC_title: {
                switch ((c.getPlayer().getMapId() / 100) % 10) {
                    case 0:
                        c.SendPacket(CField.MapEff("temaD/enter/teraForest"));
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        c.SendPacket(CField.MapEff("temaD/enter/neoCity" + ((c.getPlayer().getMapId() / 100) % 10)));
                        break;
                }
                break;
            }
            case resetEvolvingSystem:
                if (c.getPlayer().getQuestStatus(1802) == QuestState.Started) {
                    Quest.getInstance(1802).forceComplete(c.getPlayer(), 0);
                }
                break;
            case explorationPoint: {
                if (c.getPlayer().getMapId() == 104000000) {
                    c.SendPacket(CField.environmentChange("maplemap/enter/104000000", 12, 0));
                    c.SendPacket(UIPacket.getDirectionStatus(false));
                    c.SendPacket(UIPacket.IntroLock(false));
                    c.SendPacket(WvsContext.enableActions());
                }
                MedalQuest m = null;
                for (MedalQuest mq : MedalQuest.values()) {
                    for (int i : mq.maps) {
                        if (c.getPlayer().getMapId() == i) {
                            m = mq;
                            break;
                        }
                    }
                }
                if (m != null && c.getPlayer().getLevel() >= m.level && c.getPlayer().getQuestStatus(m.questid) != QuestState.Completed) {
                    if (c.getPlayer().getQuestStatus(m.lquestid) != QuestState.Started) {
                        Quest.getInstance(m.lquestid).forceStart(c.getPlayer(), 0, "0");
                    }
                    if (c.getPlayer().getQuestStatus(m.questid) != QuestState.Started) {
                        Quest.getInstance(m.questid).forceStart(c.getPlayer(), 0, null);
                        final StringBuilder sb = new StringBuilder("enter=");
                        for (int i = 0; i < m.maps.length; i++) {
                            sb.append("0");
                        }
                        c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                        Quest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, "0");
                    }
                    String quest = c.getPlayer().getInfoQuest(m.questid - 2005);
                    if (quest.length() != m.maps.length + 6) { //enter= is 6
                        final StringBuilder sb = new StringBuilder("enter=");
                        for (int i = 0; i < m.maps.length; i++) {
                            sb.append("0");
                        }
                        quest = sb.toString();
                        c.getPlayer().updateInfoQuest(m.questid - 2005, quest);
                    }
                    final QuestStatus stat = c.getPlayer().getQuestNAdd(Quest.getInstance(m.questid - 1995));
                    if (stat.getCustomData() == null) { //just a check.
                        stat.setCustomData("0");
                    }
                    int number = Integer.parseInt(stat.getCustomData());
                    final StringBuilder sb = new StringBuilder("enter=");
                    boolean changedd = false;
                    for (int i = 0; i < m.maps.length; i++) {
                        boolean changed = false;
                        if (c.getPlayer().getMapId() == m.maps[i]) {
                            if (quest.substring(i + 6, i + 7).equals("0")) {
                                sb.append("1");
                                changed = true;
                                changedd = true;
                            }
                        }
                        if (!changed) {
                            sb.append(quest.substring(i + 6, i + 7));
                        }
                    }
                    if (changedd) {
                        number++;
                        c.getPlayer().updateInfoQuest(m.questid - 2005, sb.toString());
                        Quest.getInstance(m.questid - 1995).forceStart(c.getPlayer(), 0, String.valueOf(number));
                        c.getPlayer().dropMessage(-1, "Visited " + number + "/" + m.maps.length + " regions.");
                        c.getPlayer().dropMessage(-1, "Title " + String.valueOf(m) + " Explorer currently in progress");
                        c.SendPacket(WvsContext.showQuestMsg("Title " + String.valueOf(m) + " Explorer currently in progress " + number + "/" + m.maps.length + " completed"));
                    }
                }
                break;
            }

            case enter_masRoom: {
                if (c.getPlayer().getQuestStatus(23213) == QuestState.Started
                        && c.getPlayer().getQuestStatus(23214) != QuestState.Started
                        && c.getPlayer().getQuestStatus(23214) != QuestState.Completed) {;

                    Quest.getInstance(23213).forceComplete(c.getPlayer(), 0);
                    Quest.getInstance(23214).forceStart(c.getPlayer(), 0, "1");
                    final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(931050120); //exit Map
                    c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                }
                break;
            }

            case StageUserEnterSengo:
//                if (c.getPlayer().getMapId() == 925020100 || c.getPlayer().getMapId() == 925030100 || c.getPlayer().getMapId() == 925040100) {
//                    c.getPlayer().getMap().startMapEffect("Don't forget that you have to clear it within the time limit! Take down the monster and head to the next floor!", 5120024);
//                }

                int stage = (c.getPlayer().getMapId() - 744000020);
                boolean over = false;
                if (stage > 9) {
                    over = true;
                    String str = Integer.toString(stage);
                    char[] charArray = str.toCharArray();
                    int stage0 = Character.getNumericValue(charArray[0]);
                    int stage1 = Character.getNumericValue(charArray[1]);
                    sendRedLeafStart(c, stage0, stage1, over);
                } else {
                    sendRedLeafStart(c, stage, 0, over);
                }

                sendRedLeafClock(c, 300);//5 min
//                sendRedLeafStart(c, stage, over);
                break;

            case enter_23214: {
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9001038), new Point(816, -14));
                break;
            }

            case dubl2Tuto0: {
                try {
                    c.SendPacket(WvsContext.getTopMsg("The Secret Garden Depths"));
                    c.SendPacket(WvsContext.getTopMsg("On a rainy day..."));
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.DublStart(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 3000));
                    Thread.sleep(3000);
//                    c.write(UIPacket.DublStart(false));
//                    c.write(UIPacket.DublStart(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                    Thread.sleep(7000);
                    c.SendPacket(UIPacket.IntroEnableUI(false));
                    c.SendPacket(UIPacket.DublStart(false));
                } catch (InterruptedException ex) {
                }

                break;
            }
            case dubl2Tuto10: {
                c.SendPacket(WvsContext.getTopMsg("The Secret Garden Depths"));
                c.SendPacket(WvsContext.getTopMsg("On a rainy day..."));
                break;
            }

            case dublTuto21: {
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300522), new Point(-578, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300521), new Point(-358, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300522), new Point(-138, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300522), new Point(-82, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300522), new Point(-302, 152));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300522), new Point(-522, 152));
                break;
            }

            case dublTuto23: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9300523), new Point(-283, 152));
                break;
            }

            case np_tuto_0_0_before: {
                try {
                    c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                    c.SendPacket(CField.environmentChange("newPirate/text0", 12, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 9500));
                    Thread.sleep(9500);
                } catch (InterruptedException ex) {
                }
                c.SendPacket(CField.UIPacket.getDirectionStatus(false));
                c.SendPacket(CField.UIPacket.IntroEnableUI(false));
                c.getPlayer().changeMap(552000010, 0);
                break;
            }
            case np_tuto_0_0: {
                try {
                    c.getPlayer().getMap().resetFully();
                    //c.write(NPCPacket.getCutSceneSkip()); // error 38, but this packet isn't important.
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                }
                c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 2000));
                try {
                    Thread.sleep(2000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/DirectionNewPirate.img/newPirate/balloonMsg2/0", 0, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9270083, "np_tuto_0_1");
                break;
            }
            case map_913070000: {
                try {
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                    c.SendPacket(WvsContext.getTopMsg("Mr.Limbert's General Store"));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 500));
                    Thread.sleep(500);
                    c.SendPacket(WvsContext.getTopMsg("Month 3, Day 4"));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/0", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.directionFacialExpression(6, 10000));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/1", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/2", 3000, 0, -100, 1, 0));
                    c.SendPacket(CField.directionFacialExpression(4, 8000));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 3000));
                    Thread.sleep(3000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/3", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.directionFacialExpression(6, 2000));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9075005, "tuto001");
                break;
            }
            case map_913070001: {
                c.SendPacket(WvsContext.getTopMsg("Mr.Limbert's General Store"));
                c.SendPacket(WvsContext.getTopMsg("Month 3, Day 4"));
                break;
            }
            case map_913070002: {
                c.SendPacket(WvsContext.getTopMsg("Mr.Limbert's General Store"));
                c.SendPacket(WvsContext.getTopMsg("Month 3, Day 8"));
                break;
            }
            case map_913070020: {
                c.SendPacket(WvsContext.getTopMsg("Mr.Limbert's General Store"));
                //bigby spawn is not gms like yet
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9001051), new Point(185, 65));
                c.SendPacket(CField.getClock(5 * 60));
                MapTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (c.getPlayer().getMapId() == 913070020) {
                            c.getPlayer().changeMap(913070003, 0);
                        }
                    }
                }, 5 * 60 * 1000);
                break;
            }
            case map_913070004: {
                try {
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                    c.SendPacket(WvsContext.getTopMsg("Mr.Limbert's General Store"));
                    c.SendPacket(WvsContext.getTopMsg("Month 3, Day 11"));
                    Thread.sleep(500);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 2));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/5", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 2));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 500));
                    Thread.sleep(500);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/6", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/4", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/7", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/8", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(WvsContext.getTopMsg("Someone suspicious is in the back yard..."));
                } catch (InterruptedException ex) {
                }
                c.SendPacket(CField.UIPacket.IntroEnableUI(false));
                c.SendPacket(CField.UIPacket.getDirectionStatus(false));
                c.SendPacket(WvsContext.enableActions());
                break;
            }
            case map_913070050: {
                try {
                    Quest.getInstance(20034).forceStart(c.getPlayer(), 1106000, null);
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(CField.UIPacket.getDirectionStatus(true));
                    c.SendPacket(WvsContext.getTopMsg("General Store Yard"));
                    c.SendPacket(CField.getClock(10 * 60));
                    MapTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            if (c.getPlayer().getMapId() >= 913070050 && c.getPlayer().getMapId() < 913070070) {
                                c.getPlayer().changeMap(913070004, 0);
                            }
                        }
                    }, 10 * 60 * 1000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction7.img/effect/tuto/step0/4", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.SendPacket(CField.directionFacialExpression(6, 10000));
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1106000, "tuto004");
                break;
            }
            case mihail_direc: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroLock(true));
                    showIntro(c, "Effect/Direction7.img/mikhail/1st_Job");
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                    c.getPlayer().changeJob((short) 5100);
                    Thread.sleep(4000);
                    c.SendPacket(UIPacket.getDirectionStatus(false));
                    c.SendPacket(UIPacket.IntroLock(false));
                    c.getPlayer().changeMap(130000000, 0);
                    c.getPlayer().forceChangeChannel(c.getChannel());
                } catch (InterruptedException ex) {
                }
                break;
            }
            case PTtutor000: {
                try {
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.playMovie("phantom_memory.avi", true));
                    Thread.sleep(85000);
                    c.SendPacket(CField.MapEff("phantom/mapname1"));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 2));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/OnUserEff.img/questEffect/phantom/tutorial", 2000, 0, -100, 1, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 2));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 1));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 500));
                    Thread.sleep(500);
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 3, 0));
                    c.SendPacket(CField.UIPacket.UserInGameDirectionEvent((byte) 1, 1000));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1402100, "PTtutor000_0");
                break;
            }
            case PTtutor100: {
                break;
            }
            case PTtutor200: {
                break;
            }
            case PTtutor300: {
                c.SendPacket(CField.MapEff("phantom/mapname2"));
                String[] wzinfo = {"UI/tutorial.img/phantom/0/0", "UI/tutorial.img/phantom/1/0", "UI/tutorial.img/phantom/2/0", "UI/tutorial.img/phantom/3/0", "UI/tutorial.img/phantom/4/0", "UI/tutorial.img/phantom/5/0"};
                c.SendPacket(NPCPacket.getAdviceTalk(wzinfo));
                //c.getPlayer().getMap().broadcastMessage(HexTool.getByteArrayFromHexString("D8 99 71 00 01 12 EA 8D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 E0 13 00 00 00 00 00 88 01 00 00 00 00 00 00 00 4C 73 00 00 00 00 00 00 00 00 4C 73 00 00 00 00 00 00 00 00 4C 73 00 00 00 00 00 00 00 00 4C 73 00 01 00 00 00 FE F7 4C FB 02 30 00 30 00 FF FF 80 66 AB 13 00 00 00 00 D2 F6 FF FF 2A F9 FF FF 96 00 00 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF"));
                //spawn guards packet
                break;
            }
            case PTtutor301: {
                //respawn guards
                break;
            }
            case PTtutor400: {
                break;
            }
            case PTtutor500: {
                c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                try {
                    c.SendPacket(CField.MapEff("phantom/mapname3"));
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1402100, "PTtutor500_0");
                break;
            }
            case PTjob1: {
                if (c.getPlayer().getLevel() < 10) {
                    while (c.getPlayer().getLevel() < (short) 10) {
                        c.getPlayer().OnLevelUp();
                    }
//                    c.getPlayer().changeJob((short) 2400);
                    c.getPlayer().setExp(0);
                    c.getPlayer().resetStats(4, 4, 4, 25);
                    c.SendPacket(UIPacket.playMovie("phantom.avi", true));
                }
                break;
            }
            case PTjob2M: {
                c.getPlayer().setQuestAdd(Quest.getInstance(25102), QuestState.Started, "1");
                if (c.getPlayer().getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) < 1) {
                    c.getPlayer().getMap().spawnMonsterOnGroudBelow(LifeFactory.getMonster(9001045), new Point(171, 182));
                }
                break;
            }
            /*case go10000:
                c.write(CField.environmentChange("maplemap/enter/10000", 12, 0));
                break;*/
            case go20000:
                c.SendPacket(CField.environmentChange("maplemap/enter/20000", 12, 0));
                if (c.getPlayer().getQuestStatus(32200) == QuestState.NotStarted) {
                    Quest.getInstance(32200).forceStart(c.getPlayer(), 0, null);
                    Quest.getInstance(32200).forceComplete(c.getPlayer(), 0);
                    Quest.getInstance(32201).forceStart(c.getPlayer(), 0, null);
                    Quest.getInstance(32201).forceComplete(c.getPlayer(), 0);
                    Quest.getInstance(32202).forceStart(c.getPlayer(), 0, null);
                    Quest.getInstance(32202).forceComplete(c.getPlayer(), 0);
                }
                break;
            case go30000:
                c.SendPacket(CField.environmentChange("maplemap/enter/30000", 12, 0));
                break;
            case go40000:
                c.SendPacket(CField.environmentChange("maplemap/enter/40000", 12, 0));
                break;
            case go50000:
                c.SendPacket(CField.environmentChange("maplemap/enter/50000", 12, 0));
                break;
            case go1000000:
                c.SendPacket(CField.environmentChange("maplemap/enter/1000000", 12, 0));
                break;
            case go1010000:
                c.SendPacket(CField.environmentChange("maplemap/enter/1010000", 12, 0));
                break;
            case go1010100:
                c.SendPacket(CField.environmentChange("maplemap/enter/1010100", 12, 0));
                break;
            case go1010200:
                c.SendPacket(CField.environmentChange("maplemap/enter/1010200", 12, 0));
                break;
            case go1010300:
                c.SendPacket(CField.environmentChange("maplemap/enter/1010300", 12, 0));
                break;
            case go1010400:
                c.SendPacket(CField.environmentChange("maplemap/enter/1010400", 12, 0));
                break;
            case go1020000:
                c.SendPacket(CField.environmentChange("maplemap/enter/1020000", 12, 0));
                c.SendPacket(UIPacket.getDirectionStatus(false));
                c.SendPacket(UIPacket.IntroLock(false));
                c.SendPacket(WvsContext.enableActions());
                break;
            case go2000000:
                c.SendPacket(CField.environmentChange("maplemap/enter/2000000", 12, 0));
                break;
            case enter_edelstein:
            case patrty6_1stIn:
            case standbyAzwan:
            case angelic_tuto0://for now TODO real tut
                if (c.getPlayer().getJob() == 6001) {
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                    c.getPlayer().changeJob((short) 6500);
                    c.getPlayer().gainItem(1142495, 1);//Nova Contractor
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(400000000);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    c.SendPacket(UIPacket.playMovie("angelic.avi", true));
                }
                break;
            case np_tuto_0_9://jett
                if (c.getPlayer().getJob() == 0) {
                    c.getPlayer().changeJob((short) 508);

                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                }
                break;
            case kaiser_tuto_0_0://for now TODO real tut
                if (c.getPlayer().getJob() == 6000) {
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                    c.getPlayer().setExp(0);
                    c.getPlayer().gainItem(1142495, 1);//Nova Contractor
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(400000000);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    c.SendPacket(UIPacket.playMovie("kaiser.avi", true));
                }
                break;

            case lightning_tuto_1_0://for now TODO real tut Luminous
                if (c.getPlayer().getJob() == 2004) {
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(101020100);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(9));
                    c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(27001201), (byte) 0, (byte) 10, -1);//dark
                    c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(27001100), (byte) 0, (byte) 10, -1);//light
                    c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(27000207), (byte) 0, (byte) 5, -1);//Dark Affinity
                    c.getPlayer().gainItem(1142479, 1);//In both Darkness and Light - emblem
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                    c.getPlayer().setExp(0);
                    c.SendPacket(UIPacket.playMovie("Luminous.avi", true));
                }
                break;

            case xn_tuto_0_0://for now TODO real tut
                if (c.getPlayer().getJob() == 3002) {
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                    c.getPlayer().setExp(0);
                    c.getPlayer().gainItem(1142575, 1);//memory seeker - emblem 
                    c.getPlayer().gainItem(3010585, 1);//Xenon chair
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(310010000);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    c.SendPacket(UIPacket.playMovie("xenon.avi", true));
                }
                break;//

            case onUserEnter_866191000://Beast skip tut
                if (c.getPlayer().getJob() == 11212 && c.getPlayer().getLevel() < 10) {
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                    c.getPlayer().gainItem(1142673, 1);//Sprout Guardian - emblem 
                    c.getPlayer().gainItem(2000001, 50);//Orange potion
                    c.getPlayer().gainItem(2000006, 50);//Mana Elexir
                    c.getPlayer().gainItem(1352810, 1);//Tiny Whisper
                    c.getPlayer().resetStats(4, 4, 25, 4);
                    c.getPlayer().setExp(0);
                    c.SendPacket(UIPacket.playMovie("BeastTamer.avi", true));
                }
                break;
            case map940200060: { //shade starter
                if (c.getPlayer().getJob() == 2005) {
                    c.SendPacket(UIPacket.playMovie("Opening.avi", true));
                    c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(25001000), (byte) 0, (byte) 25, -1);
                    c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(25001002), (byte) 0, (byte) 25, -1);
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().OnLevelUp();
                    }
                    c.getPlayer().setExp(0);
                }
                break;
            }
            case map130030000: { //Cygnus Movie starter. Because I love movie o.o
                if (c.getPlayer().getJob() == 1000) {
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(130030000);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    c.SendPacket(UIPacket.playMovie("cygnusReturns.avi", true));
                }
                break;
            }
            case PTjob3M2: {
                if (c.getPlayer().getQuestStatus(25111) == QuestState.Started) {
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        }
                    }, 2500);
                    Quest.getInstance(25111).forceComplete(c.getPlayer(), 0);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                    double timeOut = 0;
                    while (true) {
                        if (timeOut > 10000) {
                            break;
                        }
                        if (c.getPlayer().getJob() == 2410) {
                            c.SendPacket(UIPacket.IntroEnableUI(false));
                            c.removeClickedNPC();
                            NPCScriptManager.getInstance().dispose(c);
                            c.SendPacket(WvsContext.enableActions());
                            Quest.getInstance(29969).forceComplete(c.getPlayer(), 0);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                            }
                            c.getPlayer().changeJob((short) 2411);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                            }
                            c.SendPacket(CField.showEffect("phantom/suu"));
                            c.removeClickedNPC();
                            NPCScriptManager.getInstance().dispose(c);
                            c.SendPacket(WvsContext.enableActions());
                            break;
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                        timeOut += 100;
                    }
                } else {
                    c.getPlayer().dropMessage(5, "Or move out and proof your strength!");
                }
                break;
            }

            case PTjob4M: {
                if (c.getPlayer().getQuestStatus(25120) == QuestState.Started) {// && c.getPlayer().getQuestStatus(25101)!=1 && c.getPlayer().getQuestStatus(25101)!=2)
                    Quest.getInstance(25120).forceComplete(c.getPlayer(), 0);
                } else {
                    c.getPlayer().dropMessage(5, "Or move out and proof your strength!");
                }
                break;
            }

            case PTjob4M_1: {
                if (c.getPlayer().getJob() == 2411) {
                    c.getPlayer().getMap().resetFully();
                    c.getPlayer().forceCompleteQuest(25122);
                    if (!c.getPlayer().getMap().containsNPC(2159307)) {
                        c.getPlayer().getMap().spawnNpc(1403002, new Point(302, 182));
                    }
                    //c.getPlayer().forceCompleteQuest(29970);
                    //NPCScriptManager.getInstance().start(c, 1403002);
                } else {
                    c.getPlayer().dropMessage(5, "Or move out and proof your strength!");
                }
                break;
            }

            case PTjob4M2: {
                //c.write(UIPacket.getDirectionInfo(4, 2159310));

                if (c.getPlayer().getQuestStatus(25122) == QuestState.Completed && c.getPlayer().getJob() == 2411) {
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 30));
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                            c.SendPacket(CField.showEffect("demonSlayer/text6"));
                        }
                    }, 2500);
                    ScheduledFuture<?> schedule;
                    schedule = EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                            c.SendPacket(CField.showEffect("demonSlayer/text5"));
                        }
                    }, 4500);

                    EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            c.SendPacket(UIPacket.IntroEnableUI(false));
                            c.removeClickedNPC();
                            NPCScriptManager.getInstance().dispose(c);
                            c.SendPacket(WvsContext.enableActions());
                        }
                    }, 6500);

                    EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            c.getPlayer().dropMessage(-1, "Come inside me, Phantom!");
                        }
                    }, 8500);

                    EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            double timeOut = 0;
                            while (true) {
                                if (timeOut > 20000) {
                                    break;
                                }
                                if (c.getPlayer().getJob() == 2411 && c.getPlayer().getPosition().y == -30) {
                                    c.getPlayer().changeJob((short) 2412);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                    }
                                    c.SendPacket(CField.showEffect("phantom/darkphantom"));
                                    break;
                                }
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ex) {
                                    break;
                                }
                                timeOut += 100;
                            }
                        }
                    }, 9000);
                } else {
                    c.getPlayer().dropMessage(5, "Or move out, and proof your strength!");
                }
                break;
            }

            case q53244_dun_in: {
                c.SendPacket(UIPacket.IntroEnableUI(false));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                }
                c.getPlayer().getMap().resetFully();
                c.getPlayer().dropMessage(-1, "Father, There they are. All located in the planets!");
                if (!c.getPlayer().getMap().containsNPC(9270084)) {
                    c.getPlayer().getMap().spawnNpc(9270084, new Point(-103, 55));
                }
                if (!c.getPlayer().getMap().containsNPC(9270090)) {
                    c.getPlayer().getMap().spawnNpc(9270090, new Point(65, 55));
                }
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/DirectionNewPirate.img/newPirate/balloonMsg2/11", 2000, 0, 1, -100, 1));
                for (int i = 0; i < 10; i++) {
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 5));
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                    }
                }
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));

                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        c.getPlayer().dropMessage(-1, "Heh heh heh, nguoi da cham soc no tot that day!");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        //c.write(UIPacket.getDirectionInfo(4, 1403002));
                        NPCScriptManager.getInstance().start(c, 9270090, "q53244_dun_in");
                    }
                }, 1000);
                break;
            }

            case q53251_enter: {
                c.SendPacket(UIPacket.IntroEnableUI(true));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                }
                c.getPlayer().getMap().resetFully();
                if (!c.getPlayer().getMap().containsNPC(9270092)) {
                    c.getPlayer().getMap().spawnNpc(9270092, new Point(352, 55));
                }
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        NPCScriptManager.getInstance().start(c, 9270092, "q53251_enter");
                    }
                }, 1000);
                //final MapleMap mapmap = c.getChannelServer().getMapFactory().getMap(552000074);
                break;
            }

            case ds_tuto_ill0: {
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 6300));
                showIntro(c, "Effect/Direction6.img/DemonTutorial/SceneLogo");
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.getDirectionStatus(false));
                        c.SendPacket(UIPacket.IntroLock(false));
                        c.SendPacket(WvsContext.enableActions());
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(927000000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    }
                }, 6300); //wtf
                break;
            }
            case ds_tuto_home_before: {
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 30));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 90));

                c.SendPacket(CField.showEffect("demonSlayer/text11"));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 4000));
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        showIntro(c, "Effect/Direction6.img/DemonTutorial/Scene2");
                    }
                }, 1000);
                break;
            }
            case ds_tuto_1_0: {
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 30));
                c.SendPacket(UIPacket.getDirectionStatus(true));

                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(4, 2159310));
                        NPCScriptManager.getInstance().start(c, 2159310, null);
                    }
                }, 1000);
                break;
            }
            case ds_tuto_4_0: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(4, 2159344));
                NPCScriptManager.getInstance().start(c, 2159344, null);
                break;
            }
            case cannon_tuto_01: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110), (byte) 1, (byte) 1);
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(4, 1096000));
                NPCScriptManager.getInstance().dispose(c);
                NPCScriptManager.getInstance().start(c, 1096000, null);
                break;
            }
            case ds_tuto_5_0: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(4, 2159314));
                NPCScriptManager.getInstance().dispose(c);
                NPCScriptManager.getInstance().start(c, 2159314, null);
                break;
            }
            case ds_tuto_3_0: {
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 30));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(CField.showEffect("demonSlayer/text12"));

                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(4, 2159311));
                        NPCScriptManager.getInstance().dispose(c);
                        NPCScriptManager.getInstance().start(c, 2159311, null);
                    }
                }, 1000);
                break;
            }
            case ds_tuto_3_1: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                if (!c.getPlayer().getMap().containsNPC(2159340)) {
                    c.getPlayer().getMap().spawnNpc(2159340, new Point(175, 0));
                    c.getPlayer().getMap().spawnNpc(2159341, new Point(300, 0));
                    c.getPlayer().getMap().spawnNpc(2159342, new Point(600, 0));
                }
                c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/tuto/balloonMsg2/0", 2000, 0, -100, 1, 0));
                c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/tuto/balloonMsg1/3", 2000, 0, -100, 1, 0));
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(4, 2159340));
                        NPCScriptManager.getInstance().dispose(c);
                        NPCScriptManager.getInstance().start(c, 2159340, null);
                    }
                }, 1000);
                break;
            }
            case ds_tuto_2_before: {
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 30));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        c.SendPacket(CField.showEffect("demonSlayer/text13"));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 500));
                    }
                }, 1000);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(CField.showEffect("demonSlayer/text14"));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 4000));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(927000020);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        c.SendPacket(UIPacket.IntroEnableUI(false));
                        Quest.getInstance(23204).forceStart(c.getPlayer(), 0, null);
                        Quest.getInstance(23205).forceComplete(c.getPlayer(), 0);
                        final Map<Skill, SkillEntry> sa = new HashMap<>();
                        sa.put(SkillFactory.getSkill(30011170), new SkillEntry((byte) 1, (byte) 1, -1));
                        sa.put(SkillFactory.getSkill(30011169), new SkillEntry((byte) 1, (byte) 1, -1));
                        sa.put(SkillFactory.getSkill(30011168), new SkillEntry((byte) 1, (byte) 1, -1));
                        sa.put(SkillFactory.getSkill(30011167), new SkillEntry((byte) 1, (byte) 1, -1));
                        sa.put(SkillFactory.getSkill(30010166), new SkillEntry((byte) 1, (byte) 1, -1));
                        c.getPlayer().changeSkillsLevel(sa);
                        c.getPlayer().changeKeybinding(44, (byte) 1, 30010166);
                        c.getPlayer().changeKeybinding(45, (byte) 1, 30011167);
                        c.getPlayer().changeKeybinding(46, (byte) 1, 30011168);
                        c.getPlayer().changeKeybinding(47, (byte) 1, 30011169);
                        c.getPlayer().changeKeybinding(48, (byte) 1, 30011170);
                        c.SendPacket(CField.getKeymap(c.getPlayer().getKeyLayout(), c.getPlayer().getJob()));
                    }
                }, 5500);
                break;
            }
            case ds_tuto_1_before: {
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 30));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                        c.SendPacket(CField.showEffect("demonSlayer/text8"));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 500));
                    }
                }, 1000);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(CField.showEffect("demonSlayer/text9"));
                        c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 3000));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(927000010);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    }
                }, 4500);
                break;
            }
            case enter_underbase:
                if (c.getPlayer().getJob() == 3001 && c.getPlayer().getLevel() == 1) {
                    c.SendPacket(NPCPacket.getDemonSelection());
                }
                break;
            case ds_tuto_0_0: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.IntroEnableUI(true));
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                sa.put(SkillFactory.getSkill(30011109), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(30010110), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(30010111), new SkillEntry((byte) 1, (byte) 1, -1));
                sa.put(SkillFactory.getSkill(30010185), new SkillEntry((byte) 1, (byte) 1, -1));
                c.getPlayer().changeSkillsLevel(sa);
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(CField.showEffect("demonSlayer/back"));
                c.SendPacket(CField.showEffect("demonSlayer/text0"));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 500));
                c.getPlayer().setDirection(0);
                c.SendPacket(UIPacket.IntroEnableUI(false));
                c.SendPacket(NPCPacket.getDemonSelection());
                c.SendPacket(UIPacket.IntroEnableUI(false));
                if (!c.getPlayer().getMap().containsNPC(2159307)) {
                    c.getPlayer().getMap().spawnNpc(2159307, new Point(1305, 50));
                }
                break;
            }
            case ds_tuto_2_prep: {
                if (!c.getPlayer().getMap().containsNPC(2159309)) {
                    c.getPlayer().getMap().spawnNpc(2159309, new Point(550, 50));
                    c.getPlayer().changeKeybinding(44, (byte) 1, 30010166);
                    c.getPlayer().changeKeybinding(45, (byte) 1, 30011167);
                    c.getPlayer().changeKeybinding(46, (byte) 1, 30011168);
                    c.getPlayer().changeKeybinding(47, (byte) 1, 30011169);
                    c.getPlayer().changeKeybinding(48, (byte) 1, 30011170);
                    c.SendPacket(CField.getKeymap(c.getPlayer().getKeyLayout(), c.getPlayer().getJob()));
                    break;
                }
            }
            case goArcher: {
                showIntro(c, "Effect/Direction3.img/archer/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goPirate: {
                showIntro(c, "Effect/Direction3.img/pirate/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goRogue: {
                showIntro(c, "Effect/Direction3.img/rogue/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goMagician: {
                showIntro(c, "Effect/Direction3.img/magician/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goSwordman: {
                showIntro(c, "Effect/Direction3.img/swordman/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goLith: {
                showIntro(c, "Effect/Direction3.img/goLith/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case TD_MC_Openning: {
                showIntro(c, "Effect/Direction2.img/open/back0");

                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/back1"));
                    }
                }, 1000);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/light"));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/pepeKing"));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/line"));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/violeta0"));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/violeta1"));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/frame"));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/chat"));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction2.img/open/out"));
                    }
                }, 1500);
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(106020000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                    }
                }, 4500);
                break;
            }
            case TD_MC_gasi: {
                showIntro(c, "Effect/Direction2.img/gasi");
                break;
            }
            case aranDirection: {
                switch (c.getPlayer().getMapId()) {
                    case 914090010:
                        data = "Effect/Direction1.img/aranTutorial/Scene0";
                        break;
                    case 914090011:
                        data = "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090012:
                        data = "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090013:
                        data = "Effect/Direction1.img/aranTutorial/Scene3";
                        break;
                    case 914090100:
                        data = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914090200:
                        data = "Effect/Direction1.img/aranTutorial/Maha";
                        break;
                }
                showIntro(c, data);
                break;
            }
            case iceCave: {
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                sa.put(SkillFactory.getSkill(20000014), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20000015), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20000016), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20000017), new SkillEntry((byte) -1, (byte) 0, -1));
                sa.put(SkillFactory.getSkill(20000018), new SkillEntry((byte) -1, (byte) 0, -1));
                c.getPlayer().changeSkillsLevel(sa);
                c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/Direction1.img/aranTutorial/ClickLirin"));
                c.SendPacket(UIPacket.getDirectionStatus(false));
                c.SendPacket(UIPacket.IntroLock(false));
                c.SendPacket(WvsContext.enableActions());
                break;
            }
            case rienArrow: {
                if (c.getPlayer().getInfoQuest(21019).equals("miss=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;helper=clear");
                    c.SendPacket(EffectPacket.showReservedEffect_CutScene("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3"));
                }
                break;
            }
            case rien: {
                if (c.getPlayer().getQuestStatus(21101) == QuestState.Completed && c.getPlayer().getInfoQuest(21019).equals("miss=o;arr=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21019, "miss=o;arr=o;ck=1;helper=clear");
                }
                c.SendPacket(UIPacket.getDirectionStatus(false));
                c.SendPacket(UIPacket.IntroLock(false));
                break;
            }
            case check_count: {
                if (c.getPlayer().getMapId() == 950101010 && (!c.getPlayer().haveItem(4001433, 20) || c.getPlayer().getLevel() < 50)) { //ravana Map
                    final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(950101100); //exit Map
                    c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                }
                break;
            }
            case Massacre_first: { //sends a whole bunch of shit.
                if (c.getPlayer().getPyramidSubway() == null) {
                    c.getPlayer().setPyramidSubway(new Event_PyramidSubway(c.getPlayer()));
                }
                break;
            }
            case magnus_enter_HP: {
                if (c.getPlayer().getMapId() >= 401060100 && c.getPlayer().getMapId() < 401060100) {
                    final Mob shammos = LifeFactory.getMonster(8880000);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (User pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return true;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    c.SendPacket(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));

                }
                break;
            }

            case DI_Hillah:
                c.getPlayer().getMap().resetFully();
                final Mob boss1 = LifeFactory.getMonster(9300627);
                c.getPlayer().getMap().spawnMonsterOnGroudBelow(boss1, new Point(2630, 29));
                break;
            case DI_HvsM:
                c.getPlayer().getMap().resetFully();
                final Mob boss = LifeFactory.getMonster(9300627);
                c.getPlayer().getMap().spawnMonsterOnGroudBelow(boss, new Point(2630, 29));
                break;
//            case DI_.Magnus:
//                if (c.getPlayer().getMap().getMobsSize() < 1) {
//                    final MapleMonster boss = LifeFactory.getMonster(9300627);
//                    boss.changeLevel(c.getPlayer().getLevel());
////                    boss.setHp(stage);
//                    c.getPlayer().getEventInstance().registerMonster(boss);
//                    c.getPlayer().getMap().spawnMonsterOnGroudBelow(boss, new Point(2630, 0));
//                }
//                break;//            case DI_.Magnus:
//                if (c.getPlayer().getMap().getMobsSize() < 1) {
//                    final MapleMonster boss = LifeFactory.getMonster(9300627);
//                    boss.changeLevel(c.getPlayer().getLevel());
////                    boss.setHp(stage);
//                    c.getPlayer().getEventInstance().registerMonster(boss);
//                    c.getPlayer().getMap().spawnMonsterOnGroudBelow(boss, new Point(2630, 0));
//                }
//                break;//            case DI_.Magnus:
//                if (c.getPlayer().getMap().getMobsSize() < 1) {
//                    final MapleMonster boss = MapleLifeFactory.getMonster(9300627);
//                    boss.changeLevel(c.getPlayer().getLevel());
////                    boss.setHp(stage);
//                    c.getPlayer().getEventInstance().registerMonster(boss);
//                    c.getPlayer().getMap().spawnMonsterOnGroudBelow(boss, new Point(2630, 0));
//                }
//                break;//            case DI_.Magnus:
//                if (c.getPlayer().getMap().getMobsSize() < 1) {
//                    final MapleMonster boss = MapleLifeFactory.getMonster(9300627);
//                    boss.changeLevel(c.getPlayer().getLevel());
////                    boss.setHp(stage);
//                    c.getPlayer().getEventInstance().registerMonster(boss);
//                    c.getPlayer().getMap().spawnMonsterOnGroudBelow(boss, new Point(2630, 0));
//                }
//                break;

            case azwan_stageEff: {
                //  c.write(CWvsContext.getTopMsg("Remove all the monsters in the field need to be able to move to the next stage."));
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 1:
                    case 2:
                    case 3:
                        c.SendPacket(CField.showEffect("aswan/stageEff/stage"));
                        c.SendPacket(CField.showEffect("aswan/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100))));
                        break;
                }
                synchronized (MapScriptMethods.class) {
                    for (MapleMapObject mon : c.getPlayer().getMap().getAllMapObjects(MapleMapObjectType.MONSTER)) {
                        Mob mob = (Mob) mon;
                        if (mob.getEventInstance() == null) {
                            c.getPlayer().getEventInstance().registerMonster(mob);
                        }
                    }
                }
                break;
            }
            case Massacre_result: { //clear, give exp, etc.
                //if (c.getPlayer().getPyramidSubway() == null) {
                c.SendPacket(CField.showEffect("killing/fail"));
                //} else {
                //	c.write(CField.showEffect("killing/clear"));
                //}
                //left blank because pyramidsubway handles this.
                break;
            }

            case hayatoJobChange: {
                if (c.getPlayer().getJob() == 4001 && c.getPlayer().getLevel() == 1) {//hayato
                    c.SendPacket(UIPacket.playMovie("JPHayato.avi", true));
                    while (c.getPlayer().getLevel() < 10) {
                        c.SendPacket(CField.showEffect("JPHayato/text0"));
                        c.SendPacket(CField.showEffect("JPHayato/text1"));
                        c.SendPacket(CField.showEffect("JPHayato/text2"));
                        while (c.getPlayer().getLevel() < 10) {
                            c.getPlayer().OnLevelUp();
                        }
                        c.getPlayer().setExp(0);
                        if (c.getPlayer().getQuestStatus(28862) == QuestState.Started) {
                            Quest.getInstance(28862).forceComplete(c.getPlayer(), 0);
                        }
                    }
                } else if (c.getPlayer().getJob() == 4002 && c.getPlayer().getLevel() == 1) {
                    c.SendPacket(UIPacket.playMovie("JPKanna.avi", true));
                    while (c.getPlayer().getLevel() < 10) {
                        c.SendPacket(CField.showEffect("JPKanna/text0"));
                        c.SendPacket(CField.showEffect("JPKanna/text1"));
                        c.SendPacket(CField.showEffect("JPKanna/text2"));
                        while (c.getPlayer().getLevel() < 10) {
                            c.getPlayer().OnLevelUp();
                        }
                        c.getPlayer().setExp(0);
                        if (c.getPlayer().getQuestStatus(28862) == QuestState.Started) {
                            Quest.getInstance(28862).forceComplete(c.getPlayer(), 0);
                        }
                    }
                }
            }
            case direction_59070b: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 9390336, "BeastTamerQuestLine4");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                c.SendPacket(UIPacket.IntroEnableUI(false));
                break;
            }

            case direction_59070: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9390336, "BeastTamerQuestLine3");
                break;
            }

            case direction_59063: {
                try {
                    if (c.getPlayer().getQuestStatus(59063) == QuestState.Started) {
                        Quest.getInstance(59063).forceComplete(c.getPlayer(), 0);
                    }
                    c.SendPacket(WvsContext.getTopMsg("On voyage to Nautilus."));
                    c.SendPacket(CField.getClock(1 * 30));
                    Thread.sleep(30000);
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(866000240);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                } catch (InterruptedException e) {
                }
                break;
            }

            case direction_59061: {
                if (c.getPlayer().getQuestStatus(59061) == QuestState.Started) {
                    Quest.getInstance(59061).forceComplete(c.getPlayer(), 0);
                    //MapleQuest.getInstance(59063).forceStart(c.getPlayer(), 0, null);
                    //MapleMap mapto = c.getChannelServer().getMapFactory().getMap(866000230);
                    //c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                }
                //c.write(UIPacket.getDirectionStatus(true));
                //c.write(UIPacket.getDirectionInfo(3, 0));
                break;
            }

            case enter_866033000: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390915), new Point(-153, 49));
                break;
            }

            case direction_59054: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 100));
                    Thread.sleep(100);
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent((byte) 0, 500, 575, 865));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2825));
                    Thread.sleep(2825);
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 9390313, "BeastTamerQuestLine1");
                } catch (InterruptedException e) {
                }
                break;
            }

            case onUserEnter_866138000: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.playMovie("BeastTamer.avi", true));
                    Thread.sleep(75000);
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(866191000);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                } catch (InterruptedException e) {
                }
                break;
            }

            case onUserEnter_866135000: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(UIPacket.IntroEnableUI(true));
                if (c.getPlayer().haveItem(2500004, 0)) {
                    c.getPlayer().gainItem(2500004, 1);//Animal SP Reset Scroll (Beast Tamer only)
                    final Map<Skill, SkillEntry> sa = new HashMap<>();
                    sa.put(SkillFactory.getSkill(110000012), new SkillEntry((byte) 14, (byte) 14, -1));
                    sa.put(SkillFactory.getSkill(110001506), new SkillEntry((byte) 1, (byte) 1, -1));
                    sa.put(SkillFactory.getSkill(110001514), new SkillEntry((byte) 1, (byte) 1, -1));
                    sa.put(SkillFactory.getSkill(110001510), new SkillEntry((byte) 1, (byte) 1, -1));
                    sa.put(SkillFactory.getSkill(110001500), new SkillEntry((byte) 1, (byte) 1, -1));
                    sa.put(SkillFactory.getSkill(110001502), new SkillEntry((byte) 1, (byte) 1, -1));
                    sa.put(SkillFactory.getSkill(112100000), new SkillEntry((byte) 1, (byte) 14, -1));
                    sa.put(SkillFactory.getSkill(110001501), new SkillEntry((byte) 1, (byte) 1, -1));
                    sa.put(SkillFactory.getSkill(110001501), new SkillEntry((byte) 1, (byte) 1, -1));
                    sa.put(SkillFactory.getSkill(112000000), new SkillEntry((byte) 1, (byte) 14, -1));
                    c.getPlayer().changeSkillsLevel(sa);
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9390300, "BeastTamerTutIntro");
                break;
            }

            case onUserEnter_866109000: {
                Quest.getInstance(59008).forceComplete(c.getPlayer(), 0);
                Quest.getInstance(59009).forceStart(c.getPlayer(), 0, null);
                Quest.getInstance(59009).forceComplete(c.getPlayer(), 0);
                Quest.getInstance(59011).forceStart(c.getPlayer(), 0, null);
                Quest.getInstance(59011).forceComplete(c.getPlayer(), 0);
                Quest.getInstance(59013).forceStart(c.getPlayer(), 0, null);
                Quest.getInstance(59013).forceComplete(c.getPlayer(), 0);
                Quest.getInstance(59015).forceStart(c.getPlayer(), 0, null);
                Quest.getInstance(59015).forceComplete(c.getPlayer(), 0);
                Quest.getInstance(59016).forceStart(c.getPlayer(), 0, null);
                Quest.getInstance(59016).forceComplete(c.getPlayer(), 0);
                MapleMap mapto = c.getChannelServer().getMapFactory().getMap(866135000);
                c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                break;
            }

            case onUserEnter_866107000: {
                try {
                    c.getPlayer().getMap().resetFully();
                    if (c.getPlayer().getQuestStatus(59005) == QuestState.Started) {
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390931), new Point(661, 246));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390931), new Point(761, 246));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390931), new Point(861, 246));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390931), new Point(961, 246));
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390931), new Point(1061, 246));
                    }
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 9390300, "BeastTamerTut06");
                    Thread.sleep(1000);
                    Quest.getInstance(59005).forceComplete(c.getPlayer(), 0);
                } catch (InterruptedException ex) {
                }
                break;
            }

            case onUserEnter_866106000: {
                try {
                    c.getPlayer().getMap().resetFully();
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    if (!c.getPlayer().getMap().containsNPC(9390381)) {
                        c.getPlayer().getMap().spawnNpc(9390381, new Point(89, 32));
                    }
                    c.SendPacket(NPCPacket.setNPCSpecialAction(9390381, "summon"));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 1000));
                    Thread.sleep(1000);
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 9390381, "BeastTamerTut05");
                    Thread.sleep(6000);
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(866107000);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                } catch (InterruptedException e) {
                }
                break;
            }

            case onUserEnter_866105000: {
                c.SendPacket(UIPacket.IntroEnableUI(false));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390936), new Point(515, 77));
                break;
            }

            case onUserEnter_866104000: {
                try {
                    c.SendPacket(UIPacket.getDirectionStatus(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                    c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 300));
                    Thread.sleep(300);
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 9390300, "BeastTamerTut04");
                    Thread.sleep(6000);
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(866105000);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                } catch (InterruptedException e) {
                }
                break;
            }

            case onUserEnter_866103000: {
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390937), new Point(515, 77));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(9390935), new Point(278, 70));
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9390300, "BeastTamerTut03");
                break;
            }

            case onUserEnter_866100000: {
                c.SendPacket(UIPacket.getDirectionStatus(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 0));
                c.SendPacket(CField.UIPacket.IntroEnableUI(true));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9390305, "BeastTamerTut01");
                break;
            }

            case q59000_tuto: {
                try {
                    c.SendPacket(UIPacket.IntroEnableUI(false));
                    c.SendPacket(CField.sendHint("Press #e#b[left],[right]#k#n to move around.", 150, 5));
                    Thread.sleep(5000);
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 1103005, "TotInfoBT");
                    Quest.getInstance(28862).forceStart(c.getPlayer(), 0, null);
                    Quest.getInstance(28862).forceComplete(c.getPlayer(), 0);
                    c.SendPacket(WvsContext.getTopMsg("Earned Forever Single title!"));
                } catch (InterruptedException e) {
                }
                break;
            }

            case sao_enterRewardMap: {
                c.SendPacket(CField.showEffect("Sao/Start1"));
                //         c.getPlayer().getMap().startMapEffect("Attempt to wake the Crimson Queen", 5120107);
                break;
            }

            case sao_enterRewardMap2: {
                c.SendPacket(CField.showEffect("Sao/Start2"));
                break;
            }

            case sao_enter01: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 1.", 5120107);
                break;
            }

            case sao_enter02: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 2.", 5120107);

                break;
            }

            case sao_enter03: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 3.", 5120107);
                break;
            }

            case sao_enter04: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 4.", 5120107);
                break;
            }

            case sao_enter05: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 5.", 5120107);
                break;
            }

            case sao_enter06: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 6.", 5120107);
                break;
            }
            case sao_enter07: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 7.", 5120107);
                break;
            }
            case sao_enter08: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 8.", 5120107);
                break;
            }
            case sao_enter09: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 9.", 5120107);
                break;
            }
            case sao_enter10: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 10.", 5120107);
                break;
            }
            case sao_enter11: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 11.", 5120107);
                break;
            }

            case sao_enter12: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 12.", 5120107);

                break;
            }

            case sao_enter13: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 13.", 5120107);
                break;
            }

            case sao_enter14: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 14.", 5120107);
                break;
            }

            case sao_enter15: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 15.", 5120107);
                break;
            }

            case sao_enter16: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 16.", 5120107);
                break;
            }
            case sao_enter17: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 17.", 5120107);
                break;
            }
            case sao_enter18: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 18.", 5120107);
                break;
            }
            case sao_enter19: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 19.", 5120107);
                break;
            }
            case sao_enter20: {
                //   c.write(CField.showEffect("Sao/Start1"));
                c.getPlayer().getMap().startMapEffect("Floor 20.", 5120107);
                break;
            }

            case evolvingDirection3: {
                try {
                    Quest.getInstance(1801).forceComplete(c.getPlayer(), 0);
                    c.getPlayer().getMap().resetFully();
                    //   c.write(CField.UIPacket.IntroEnableUI(true));
                    //    c.write(CField.UIPacket.getDirectionStatus(true));
                    c.SendPacket(CField.MapEff("evolving/swoo2"));
                    Thread.sleep(4000);
                    showIntro(c, "Effect/Direction5.img/evolvingDereciton/Scene0");
                    Thread.sleep(8000);
                    //   c.write(CField.UIPacket.IntroEnableUI(false));
                    //  c.write(CField.UIPacket.getDirectionStatus(false));
                    c.getPlayer().changeMap(310010000, 0);
                    c.getPlayer().changeMap(310010000, 0);
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                break;
            }
            case PinkZak:
                final EventManager ezm = c.getChannelServer().getEventSM().getEventManager("PinkZakumEntrance");
                final EventInstanceManager eim = ezm.getInstance(("PinkZakumEntrance"));
                eim.registerPlayer(c.getPlayer());
                break;
            default: {
                LogHelper.UNCODED.get().info("Unhandled script : " + scriptName + ", type : onUserEnter - MAPID " + c.getPlayer().getMapId());
                return false;
            }
        }
        return true;
    }

    private static int getDojoStageDec(int ids) {
        if (ids <= 5) {
            return 0;
        } else if (ids >= 7 && ids <= 11) {
            return 1;
        } else if (ids >= 13 && ids <= 17) {
            return 2;
        } else if (ids >= 19 && ids <= 23) {
            return 3;
        } else if (ids >= 25 && ids <= 29) {
            return 4;
        } else if (ids >= 31 && ids <= 35) {
            return 5;
        } else if (ids >= 37 && ids <= 38) {
            return 6;
        }
        return 0;
    }

    private static void showIntro(final ClientSocket c, final String data) {
        c.SendPacket(UIPacket.IntroEnableUI(true));
        c.SendPacket(UIPacket.IntroLock(true));
        c.SendPacket(EffectPacket.showReservedEffect_CutScene(data));
    }

    private static void sendDojoClock(ClientSocket c, int time) {
        c.SendPacket(CField.getClock(time));
    }

    private static void sendDojoStart(ClientSocket c, int stage) {
        for (int i = 0; i < 3; i++) {
            c.getPlayer().updateInfoQuest(1213, "try=3");
        }
        c.SendPacket(CField.environmentChange("Dojang/start", 5, 0));//was4
        c.SendPacket(CField.environmentChange("dojang/start/stage", 12, 0));//was3
        c.SendPacket(CField.environmentChange("dojang/start/number/" + stage, 12, 0));//was3
        c.SendPacket(CField.trembleEffect(0, 1));
    }

    private static void handlePinkBeanStart(ClientSocket c) {
        final MapleMap map = c.getPlayer().getMap();
        map.resetFully();

        if (!map.containsNPC(2141000)) {
            map.spawnNpc(2141000, new Point(-190, -42));
        }
    }

    private static void reloadWitchTower(ClientSocket c) {
        final MapleMap map = c.getPlayer().getMap();
        map.killAllMonsters(false);

        final int level = c.getPlayer().getLevel();
        int mob;
        if (level <= 10) {
            mob = 9300367;
        } else if (level <= 20) {
            mob = 9300368;
        } else if (level <= 30) {
            mob = 9300369;
        } else if (level <= 40) {
            mob = 9300370;
        } else if (level <= 50) {
            mob = 9300371;
        } else if (level <= 60) {
            mob = 9300372;
        } else if (level <= 70) {
            mob = 9300373;
        } else if (level <= 80) {
            mob = 9300374;
        } else if (level <= 90) {
            mob = 9300375;
        } else if (level <= 100) {
            mob = 9300376;
        } else {
            mob = 9300377;
        }
        Mob theMob = LifeFactory.getMonster(mob);
        /*  OverrideMonsterStats oms = new OverrideMonsterStats();
        oms.setOMp(theMob.getMobMaxMp());
        oms.setOExp(theMob.getMobExp());
        oms.setOHp((long) Math.ceil(theMob.getMobMaxHp() * (level / 5.0))); //10k to 4m
        theMob.setOverrideStats(oms); */
        map.spawnMonsterOnGroundBelow(theMob, witchTowerPos);
    }

    public static void startDirectionInfo(User chr, boolean start) {
        final ClientSocket c = chr.getClient();
        DirectionInfo di = chr.getMap().getSharedMapResources().getDirection(start ? 0 : chr.getDirection());
        if (di != null && di.eventQ.size() > 0) {
            if (start) {
                c.SendPacket(UIPacket.IntroEnableUI(true));
                c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 4));
            } else {
                for (String s : di.eventQ) {
                    switch (directionInfo.fromString(s)) {
                        case merTutorDrecotion01: //direction info: 1 is probably the time
                            c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/mercedesInIce/merBalloon/0", 2000, 0, -100, 1, 0));
                            break;
                        case merTutorDrecotion02:
                            c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/mercedesInIce/merBalloon/1", 2000, 0, -100, 1, 0));
                            break;
                        case merTutorDrecotion03:
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                            c.SendPacket(UIPacket.getDirectionStatus(true));
                            c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", 2000, 0, -100, 1, 0));
                            break;
                        case merTutorDrecotion04:
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                            c.SendPacket(UIPacket.getDirectionStatus(true));
                            c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/mercedesInIce/merBalloon/3", 2000, 0, -100, 1, 0));
                            break;
                        case merTutorDrecotion05:
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                            c.SendPacket(UIPacket.getDirectionStatus(true));
                            c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/mercedesInIce/merBalloon/4", 2000, 0, -100, 1, 0));
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                                    c.SendPacket(UIPacket.getDirectionStatus(true));
                                    c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/mercedesInIce/merBalloon/5", 2000, 0, -100, 1, 0));
                                }
                            }, 2000);
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.SendPacket(UIPacket.IntroEnableUI(false));
                                    c.SendPacket(WvsContext.enableActions());
                                }
                            }, 4000);
                            break;
                        case merTutorDrecotion12:
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                            c.SendPacket(UIPacket.getDirectionStatus(true));
                            c.SendPacket(UIPacket.UserInGameDirectionEvent("Effect/Direction5.img/effect/mercedesInIce/merBalloon/8", 2000, 0, -100, 1, 0));
                            c.SendPacket(UIPacket.IntroEnableUI(false));
                            break;
                        case merTutorDrecotion21:
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 1));
                            c.SendPacket(UIPacket.getDirectionStatus(true));
                            MapleMap mapto = c.getChannelServer().getMapFactory().getMap(910150005);
                            c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                            break;
                        case ds_tuto_0_2:
                            c.SendPacket(CField.showEffect("demonSlayer/text1"));
                            break;
                        case ds_tuto_0_1:
                            c.SendPacket(UIPacket.UserInGameDirectionEvent(3, 2));
                            break;
                        case ds_tuto_0_3:
                            c.SendPacket(CField.showEffect("demonSlayer/text2"));
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 4000));
                                    c.SendPacket(CField.showEffect("demonSlayer/text3"));
                                }
                            }, 2000);
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 500));
                                    c.SendPacket(CField.showEffect("demonSlayer/text4"));
                                }
                            }, 6000);
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 4000));
                                    c.SendPacket(CField.showEffect("demonSlayer/text5"));
                                }
                            }, 6500);
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 500));
                                    c.SendPacket(CField.showEffect("demonSlayer/text6"));
                                }
                            }, 10500);
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 4000));
                                    c.SendPacket(CField.showEffect("demonSlayer/text7"));
                                }
                            }, 11000);
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.SendPacket(UIPacket.UserInGameDirectionEvent(4, 2159307));
                                    NPCScriptManager.getInstance().dispose(c);
                                    NPCScriptManager.getInstance().start(c, 2159307, null);
                                }
                            }, 15000);
                            break;
                    }
                }
            }
            c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2000));
            chr.setDirection(chr.getDirection() + 1);
            if (chr.getMap().getSharedMapResources().getDirection(chr.getDirection()) == null) {
                chr.setDirection(-1);
            }
        } else if (start) {
            switch (chr.getMapId()) {
                //hack
                case 931050300:
                    while (chr.getLevel() < 10) {
                        chr.OnLevelUp();
                    }
                    final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(931050000);
                    chr.changeMap(mapto, mapto.getPortal(0));
                    break;
            }
        }
    }

    private static void sendRedLeafClock(ClientSocket c, int time) {
        c.SendPacket(CField.getClock(time));
    }

    private static void sendRedLeafStart(ClientSocket c, int stage, int stage1, boolean over) {
        if (stage == 2 && stage1 == 1) {
            c.SendPacket(CField.environmentChange("MapleHighSchool/stageEff/final", 12, 0));
        } else {
            c.SendPacket(CField.environmentChange("MapleHighSchool/stageEff/stage", 12, 0));
            c.SendPacket(CField.environmentChange("MapleHighSchool/stageEff/number_0/" + stage, 12, 0));
            if (over) {
                c.SendPacket(CField.environmentChange("MapleHighSchool/stageEff/number_00/" + stage1, 12, 0));
            }
        }
        c.SendPacket(CField.trembleEffect(0, 1));
    }
}
