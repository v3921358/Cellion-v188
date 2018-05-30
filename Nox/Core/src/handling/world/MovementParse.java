package handling.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;

import server.maps.AnimatedMapleMapObject;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.MovementTypeA;
import server.movement.MovementTypeB;
import server.movement.MovementTypeC;
import server.movement.MovementTypeD;
import server.movement.MovementTypeE;
import server.movement.MovementTypeF;
import server.movement.MovementTypeG;
import server.movement.MovementTypeH;
import tools.LogHelper;

public class MovementParse {

    public static List<LifeMovementFragment> parseMovement(InPacket iPacket) {
        
        List<LifeMovementFragment> res = new ArrayList<>();
        byte numberOfCommands = iPacket.DecodeByte();
        for (byte index = 0; index < numberOfCommands; index++) {
            byte command = iPacket.DecodeByte();
            switch (command) {
                case Normal:
                case HangOnBack:
                case FallDown:
                case DragDown:
                case Wings:
                case MobAttackRush:
                case MobAttackRushStop:
                case MobAttackLeap:
                case Unknown70:
                case Unknown71: {
                    short xpos = iPacket.DecodeShort();
                    short ypos = iPacket.DecodeShort();
                    short vx = iPacket.DecodeShort(); 
                    short vy = iPacket.DecodeShort(); 
                    short foothold = iPacket.DecodeShort();
                    short footholdFallStart = 0;
                    if (command == FallDown || command == DragDown) {
                        footholdFallStart = iPacket.DecodeShort(); 
                    }
                    short xoffset = iPacket.DecodeShort();
                    short yoffset = iPacket.DecodeShort();
                    byte bmoveAction = iPacket.DecodeByte(); 
                    short tElapse = iPacket.DecodeShort();
                    byte bForcedStop = iPacket.DecodeByte(); 

                    MovementTypeA movement = new MovementTypeA();
                    movement.setCommand(command);
                    movement.setNumCommands(numberOfCommands);
                    movement.setPosition(new Point(xpos, ypos));
                    movement.setWobble(new Point(vx, vy));
                    movement.setFoothold(foothold);
                    movement.setFootholdFallStart(footholdFallStart);
                    movement.setOffset(new Point(xoffset, yoffset));
                    movement.setStance(bmoveAction);
                    movement.setDuration(tElapse);
                    movement.setForcedStop(bForcedStop);
                    res.add(movement);
                    break;
                }
                case ImpactIgnoreMovepath:
                case MobTeleport:
                case Unknown90: {
                    // Not sure if the information here is correct but structure is right
                    short xpos = iPacket.DecodeShort();
                    short ypos = iPacket.DecodeShort();
                    short vx = iPacket.DecodeShort(); 
                    short vy = iPacket.DecodeShort(); 
                    short foothold = iPacket.DecodeShort();
                    byte moveAction = iPacket.DecodeByte(); 
                    short tElapse = iPacket.DecodeShort(); 
                    byte bForceStop = iPacket.DecodeByte(); 

                    MovementTypeB movement = new MovementTypeB();
                    movement.setCommand(command);
                    movement.setPosition(new Point(xpos, ypos));
                    movement.setWobble(new Point(vx, vy));
                    movement.setFoothold(foothold);
                    movement.setStance(moveAction);
                    movement.setDuration(tElapse);
                    movement.setForcedStop(bForceStop);
                    res.add(movement);
                    break;
                }
                case Jump:
                case Impact:
                case StartWings:
                case MobToss:
                case MobTossSlowDown:
                case DashSlide:
                case MobLadder:
                case MobRightAngle:
                case MobStopNodeStart:
                case MobBeforeNode: {
                    // Right structure, not sure if information is right
                    short vx = iPacket.DecodeShort(); 
                    short vy = iPacket.DecodeShort(); 

                    short footholdFallStart = 0;
                    if (command == MobToss || command == MobTossSlowDown) {
                        footholdFallStart = iPacket.DecodeShort();
                    }

                    byte moveAction = iPacket.DecodeByte(); 
                    short tElapse = iPacket.DecodeShort(); 
                    byte bForceStop = iPacket.DecodeByte(); 

                    MovementTypeC movement = new MovementTypeC();
                    movement.setCommand(command);
                    movement.setWobble(new Point(vx, vy));
                    movement.setFootholdFallStart(footholdFallStart);
                    movement.setStance(moveAction);
                    movement.setDuration(tElapse);
                    movement.setForcedStop(bForceStop);
                    res.add(movement);
                    break;
                }
                case FlashJump:
                case DoubleJump:
                case DoubleJumpDown:
                case TripleJump:
                case FlashJumpChangeEff:
                case RocketBooster:
                case BackstepShot:
                case CannonJump:
                case QuickSilverJump:
                case MobPowerKnockback:
                case VerticalJump:
                case CustomImpact:
                case CustomImpact2:
                case CombatStep:
                case Hit:
                case TimeBombAttack:
                case SnowballTouch:
                case BuffZoneEffect:
                case LeafTornado:
                case StylishRope:
                case StrikerUppercut:
                case Crawl:
                case DbBladeAscension:
                case AngleImpact:
                case StarplanetRidingBooster:
                case UserToss:
                case SlashJump:
                case BattlePvPMugongSomerSault:
                case BattlePvPHelenaStepShot:
                case SunOfGlory:
                case Hookshot:
                case FinalToss:
                case NightlordShadowWeb:
                case RwExplosionCannon:
                case Unknown86:
                case Unknown87:
                case Unknown88: {
                    byte moveAction = iPacket.DecodeByte(); 
                    short tElapse = iPacket.DecodeShort(); 
                    byte bForcedStop = iPacket.DecodeByte(); 

                    MovementTypeD movement = new MovementTypeD();
                    movement.setCommand(command);
                    movement.setStance(moveAction);
                    movement.setDuration(tElapse);
                    movement.setForcedStop(bForcedStop);
                    res.add(movement);
                    break;
                }
                case Immediate:
                case Teleport:
                case RandomTeleport:
                case DemonTraceTeleport:
                case ReturnTeleport:
                case Assaulter:
                case Assassination:
                case Rush:
                case SitDown:
                case BlinkLight:
                case TeleportZero1:
                case TeleportByMobSkillArea:
                case ZeroTag:
                case RetreatShot:
                case Unknown61:
                case PinkbeanPogoStick:
                case PinkbeanPogoStickEnd:
                case PinkbeanRollingAir:
                case TeleportKinesis1:
                case TeleportAran1:
                case Unknown91: {
                    short xpos = iPacket.DecodeShort();
                    short ypos = iPacket.DecodeShort();
                    short foothold = iPacket.DecodeShort();
                    byte moveAction = iPacket.DecodeByte(); 
                    short tElapse = iPacket.DecodeShort(); 
                    byte bForcedStop = iPacket.DecodeByte(); 

                    MovementTypeE movement = new MovementTypeE();
                    movement.setCommand(command);
                    movement.setPosition(new Point(xpos, ypos));
                    movement.setFoothold(foothold);
                    movement.setStance(moveAction);
                    movement.setDuration(tElapse);
                    movement.setForcedStop(bForcedStop);
                    res.add(movement);
                    break;
                }
                case StartFallDown:
                case StartDragDown: {
                    // Correct structure. Not sure if right information.
                    short vx = iPacket.DecodeShort();
                    short vy = iPacket.DecodeShort();
                    short footholdFallStart = iPacket.DecodeShort();
                    byte moveAction = iPacket.DecodeByte(); 
                    short tElapse = iPacket.DecodeShort();
                    byte bForcedStop = iPacket.DecodeByte(); 

                    MovementTypeF movement = new MovementTypeF();
                    movement.setCommand(command);
                    movement.setWobble(new Point(vx, vy));
                    movement.setFootholdFallStart(footholdFallStart);
                    movement.setStance(moveAction);
                    movement.setDuration(tElapse);
                    movement.setForcedStop(bForcedStop);
                    res.add(movement);
                    break;
                }
                case FlyingBlock: {
                    short xpos = iPacket.DecodeShort();
                    short ypos = iPacket.DecodeShort();
                    short vx = iPacket.DecodeShort(); 
                    short vy = iPacket.DecodeShort(); 
                    byte moveAction = iPacket.DecodeByte(); 
                    short tElapse = iPacket.DecodeShort(); 
                    byte bForcedStop = iPacket.DecodeByte(); 

                    MovementTypeG movement = new MovementTypeG();
                    movement.setCommand(command);
                    movement.setPosition(new Point(xpos, ypos));
                    movement.setWobble(new Point(vx, vy));
                    movement.setStance(moveAction);
                    movement.setDuration(tElapse);
                    movement.setForcedStop(bForcedStop);
                    res.add(movement);
                    break;
                }
                case RopeConnect: {
                    short xOffset = iPacket.DecodeShort();
                    byte moveAction = iPacket.DecodeByte();
                    short tElapse = iPacket.DecodeShort();
                    byte bForcedStop = iPacket.DecodeByte();

                    MovementTypeG movement = new MovementTypeG();
                    movement.setCommand(command);
                    movement.setOffset(new Point(xOffset, 0));
                    movement.setStance(moveAction);
                    movement.setDuration(tElapse);
                    movement.setForcedStop(bForcedStop);
                    res.add(movement);
                    break;
                }
                case StatChange: {
                    byte bStat = iPacket.DecodeByte();
                    MovementTypeH movement = new MovementTypeH();
                    movement.setCommand(command);
                    movement.setbStat(bStat);
                    res.add(movement);
                    break;
                }
                default:
                    LogHelper.UNCODED.get().info("Unhandled Move Path Attribute: " + command + "");
                    break;
            }
        }
        return res;
    }

    public static void updatePosition(List<LifeMovementFragment> movement, AnimatedMapleMapObject target) {
        target.pLock.lock();
       
        try {
            for (LifeMovementFragment move : movement) {
                if (move instanceof LifeMovement) {
                    Point position = ((LifeMovement) move).getPosition();
                    target.setPosition(position);
                    target.setStance(((LifeMovement) move).getStance());
                }
            }
        } finally {
            target.pLock.unlock();
        }
    }

    public static final byte // MovePathAttr
        Normal = 0,
        Jump = 1,
        Impact = 2,
        Immediate = 3,
        Teleport = 4,
        RandomTeleport = 5,
        DemonTraceTeleport = 6,
        ReturnTeleport = 7,
        HangOnBack = 8,
        Assaulter = 9,
        Assassination = 10,
        Rush = 11,
        StatChange = 12,
        SitDown = 13,
        StartFallDown = 14,
        FallDown = 15,
        StartDragDown = 16,
        DragDown = 17,
        StartWings = 18,
        Wings = 19,
        AranAdjust = 20,
        MobToss = 21,
        MobTossSlowDown = 22,
        FlyingBlock = 23,
        DashSlide = 24,
        BmageAdjust = 25,
        BlinkLight = 26,
        TeleportZero1 = 27,
        Unknown28 = 28,
        FlashJump = 29,
        DoubleJump = 30,
        DoubleJumpDown = 31,
        TripleJump = 32,
        FlashJumpChangeEff = 33,
        RocketBooster = 34,
        BackstepShot = 35,
        CannonJump = 36,
        QuickSilverJump = 37,
        MobPowerKnockback = 38,
        VerticalJump = 39,
        CustomImpact = 40,
        CustomImpact2 = 41,
        CombatStep = 42,
        Hit = 43,
        TimeBombAttack = 44,
        SnowballTouch = 45,
        BuffZoneEffect = 46,
        LeafTornado = 47,
        StylishRope = 48,
        RopeConnect = 49,
        StrikerUppercut = 50,
        Crawl = 51,
        TeleportByMobSkillArea = 52,
        ZeroTag = 53,
        RetreatShot = 54,
        DbBladeAscension = 55,
        ImpactIgnoreMovepath = 56,
        AngleImpact = 57,
        StarplanetRidingBooster = 58,
        UserToss = 59,
        SlashJump = 60,
        Unknown61 = 61,
        MobLadder = 62,
        MobRightAngle = 63,
        MobStopNodeStart = 64,
        MobBeforeNode = 65,
        MobTeleport = 66,
        MobAttackRush = 67,
        MobAttackRushStop = 68,
        MobAttackLeap = 69,
        Unknown70 = 70, // Unknown Mob Action
        Unknown71 = 71, // Unknown Mob Action
        BattlePvPMugongSomerSault = 72,
        BattlePvPHelenaStepShot = 73,
        SunOfGlory = 74,
        HookshotStart = 75,
        Hookshot = 76,
        HookshotEnd = 77,
        PinkbeanPogoStick = 78,
        PinkbeanPogoStickEnd = 79,
        PinkbeanRollingAir = 80,
        FinalToss = 81,
        TeleportKinesis1 = 82,
        NightlordShadowWeb = 83,
        TeleportAran1 = 84,
        RwExplosionCannon = 85,
        Unknown86 = 86,
        Unknown87 = 87,
        Unknown88 = 88,
        Unknown89 = 89,
        Unknown90 = 90,
        Unknown91 = 91;
}
