package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Global;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Mazen
 *
 */
@Effect
public class GlobalEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Global.BUFFALO_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932065);
                break;
            case Global.HOG:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1902000); // wrong :/
                break;
            case Global.SILVER_MANE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1902001);
                break;
            case Global.RED_DRACO:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1902002);
                break;
            case Global.MIMIANA:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1902005);
                break;
            case Global.MIMIO:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1902006);
                break;
            case Global.SHINJO:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1992001);
                break;
            case Global.WEREWOLF:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932015);
                break;
            case Global.WEREWOLF_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932016);
                break;
            case Global.WEREWOLF_2:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932017);
                break;
            case Global.WEREWOLF_3:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932018);
                break;
                
            case Global.JET_BOAT_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932280);
                break;
            case Global.SUBMARINE_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932281);
                break;
            case Global.GYROCOPTER_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932282);
                break;
            case Global.HIGH_QUALITY_SINGLE_PASSENGER_CLASSIC_CAR_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932350);
                break;
            case Global.YETI:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932003);
                break;
            case Global.WITCHS_BROOMSTICK_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932005);
                break;
            case Global.CROCO:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932007);
                break;
            case Global.PINK_SCOOTER:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932009);
                break; 
            case Global.BLACK_SCOOTER:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1902039);
                break; 
            case Global.CLOUD_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932011);
                break; 
            case Global.MIST_BALROG:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932012);
                break; 
            case Global.RACE_KART:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932013);
                break; 
            case Global.ZD_TIGER:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932014);
                break; 
            case Global.UNICORN:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932018);
                break; 
            case Global.LOW_RIDER_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932019);
                break; 
            case Global.RED_TRUCK:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932020);
                break; 
            case Global.GARGOYLE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932021);
                break; 
            case Global.NIGHTMARE_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932025);
                break; 
            case Global.OSTRICH:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932026);
                break; 
            case Global.PINK_BEAR_HOTAIR_BALLOON:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932027);
                break; 
            case Global.TRANSFORMED_ROBOT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932028);
                break; 
            case Global.CHICKEN:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932029);
                break; 
            case Global.MOTORCYCLE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932034);
                break; 
            case Global.POWER_SUIT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932035);
                break;
            case Global.SPACESHIP_2:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932037);
                break; 
            case Global.OWL:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932038);
                break; 
            case Global.BLUE_SCOOTER:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932043);
                break; 
            case Global.GIANT_RABBIT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932045);
                break; 
            case Global.SMALL_RABBIT_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932046);
                break; 
            case Global.RABBIT_RICKSHAW_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932061);
                break; 
            case Global.WOODEN_AIRPLANE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932049);
                break; 
            case Global.RED_AIRPLANE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932050);
                break; 
            case Global.FROG:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932063);
                break; 
            case Global.TURTLE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932064);
                break; 
            case Global.SPIRIT_VIKING_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932071);
                break; 
            case Global.NAPOLEAN_MOUNT_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932081);
                break; 
            case Global.CRIMSON_NIGHTMARE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932092);
                break; 
            case Global.PANDA_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932097);
                break; 
            case Global.PENGUIN_PACK_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932098);
                break; 
            case Global.HELLHOUND_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932109);
                break; 
            case Global.DRAGONOIR_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932140);
                break; 
            case Global.BLACK_WYVERN_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932153);
                break; 
            case Global.PINK_BEAN_BALLOON_MOUNT_30_DAYS:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932161);
                break; 
            case Global.KUPOS_RIDE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932165);
                break; 
            case Global.PEGASUS_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932177);
                break; 
            case Global.PELICAN_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932198);
                break; 
            case Global.HEKATONS_FIST_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932204);
                break; 
            case Global.DOLPHIN_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932212);
                break; 
            case Global.SKATEBOARD_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932220);
                break; 
            case Global.PUMPKIN_CARRIAGE_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932221);
                break; 
            case Global.VELLUM_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932222);
                break; 
            case Global.GENIE_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932223);
                break; 
            case Global.BBQ_GIGANTIC_ROOSTER_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932228);
                break; 
            case Global.HELICOPTER_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932233);
                break; 
            case Global.NEINHEART_SNOWFIELD_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932237);
                break; 
            case Global.CYGNUS_SNOWFIELD_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932238);
                break; 
            case Global.ORCHID_SNOWFIELD_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932239);
                break; 
            case Global.HILLA_SNOWFIELD_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932241);
                break; 
            case Global.BLUE_FLAME_NIGHTMARE_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932244);
                break; 
            case Global.FLYING_FEET_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932251);
                break; 
            case Global.EAGLE_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932256);
                break; 
            case Global.CYGNUSS_AIRSHIP:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932279);
                break; 
            case Global.HOTAIR_BALLOON_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932286);
                break; 
            case Global.CAKE_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932292);
                break; 
            case Global.ORCHIDS_SUPPORT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932294);
                break; 
            case Global.ATHENA_PIERCES_SUPPORT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932295);
                break; 
            case Global.FLOWER_PETAL_PROP_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932332);
                break; 
            case Global.SURFBOARD_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932334);
                break; 
            case Global.CARD_MASTER_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932338);
                break; 
            case Global.BABY_URSUS_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932339);
                break; 
            case Global.WRIGGLING_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932341);
                break; 
            case Global.MIDNIGHT_TRAIN_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932342);
                break; 
            case Global.MERLION_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932345);
                break; 
            case Global.BUNNY_MOON_GAZING_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932347);
                break; 
            case Global.DEMON_MASK_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932351);
                break; 
            case Global.WIND_BREAKER_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932353);
                break; 
            case Global.PTEROSAUR_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932360);
                break; 
            case Global.STEAM_CYLINDER_WING_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932374);
                break; 
            case Global.PINK_BEAN_CLASSIC_CAR_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932376);
                break; 
            case Global.SUPERHERO_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932377);
                break; 
            case Global.INFERNAL_MUTT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932109);
                break; 
            case Global.OS3A_MACHINE:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932040);
                break; 
            case Global.DOUBLE_PINK_UNICORN_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932018);
                break; 
            case Global.UNICORN_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932084);
                break; 
            case Global.BUNNY_MOUNT:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1902060);
                break; 
        }   
    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 1 || nClass == 7200 || nClass == 40000 || nClass == 40001 || nClass == 40002 || nClass == 40003 || nClass == 580 || nClass == 40004 || nClass == 40005 || nClass == 581 || nClass == 582 || nClass == 590 || nClass == 591 || nClass == 592 || nClass == 2200 || nClass == 9500 || nClass == 9000 || nClass == 8000 || nClass == 8001 || nClass == 7000 || nClass == 14200 || nClass == 9100 || nClass == 7100 || nClass == 11211 || nClass == 9200 || nClass == 9201 || nClass == 9202 || nClass == 9203 || nClass == 9204 || nClass == 509;
    }

}
