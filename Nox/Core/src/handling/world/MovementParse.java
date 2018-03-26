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
        // A list of LifeMovementFragment which will contain each movement command that will
        // will be written

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
                case MobAttackLeap:
                case BattlePvPMugongSomerSault:
                case BattlePvPHelenaStepShot: {
                    short xpos = iPacket.DecodeShort();
                    short ypos = iPacket.DecodeShort();
                    short vx = iPacket.DecodeShort(); // not sure what this is. Maybe a wobble?
                    short vy = iPacket.DecodeShort(); // not sure what this is. Maybe a wobble?
                    short foothold = iPacket.DecodeShort();

                    short footholdFallStart = 0;
                    if (command == FallDown || command == DragDown) {
                        footholdFallStart = iPacket.DecodeShort(); // not entirely sure what this is used for
                    }

                    short xoffset = iPacket.DecodeShort();
                    short yoffset = iPacket.DecodeShort();

                    byte bmoveAction = iPacket.DecodeByte(); // not sure the purpose of this either
                    short tElapse = iPacket.DecodeShort(); // not sure the purpose of this either
                    byte bForcedStop = iPacket.DecodeByte(); // not sure the purpose of this either

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
                case AngleImpact:
                case MobAttackRushStop:
                case Unknown86: {
                    // Not sure if the information here is correct but structure is right
                    short xpos = iPacket.DecodeShort();
                    short ypos = iPacket.DecodeShort();
                    short vx = iPacket.DecodeShort(); // not sure what this is. Maybe a wobble?
                    short vy = iPacket.DecodeShort(); // not sure what this is. Maybe a wobble?
                    short foothold = iPacket.DecodeShort();
                    // This part is definitely right
                    byte moveAction = iPacket.DecodeByte(); // not sure the purpose of this either
                    short tElapse = iPacket.DecodeShort(); // not sure the purpose of this either
                    byte bForceStop = iPacket.DecodeByte(); // not sure the purpose of this either

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
                case MobStopNodeStart:
                case MobBeforeNode:
                case MobTeleport:
                case MobAttackRush: {
                    // Right structure, not sure if information is right
                    short vx = iPacket.DecodeShort(); // not sure what this is. Maybe a wobble?
                    short vy = iPacket.DecodeShort(); // not sure what this is. Maybe a wobble?

                    short footholdFallStart = 0;
                    if (command == MobToss || command == MobTossSlowDown) {
                        footholdFallStart = iPacket.DecodeShort();
                    }

                    // This part is definitely right
                    byte moveAction = iPacket.DecodeByte(); // not sure the purpose of this either
                    short tElapse = iPacket.DecodeShort(); // not sure the purpose of this either
                    byte bForceStop = iPacket.DecodeByte(); // not sure the purpose of this either

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
                case ImpactIgnoreMovepath:
                case StarplanetRidingBooster:
                case UserToss:
                case SlashJump:
                case MobLadder:
                case SunOfGlory:
                case HookshotStart:
                case Hookshot:
                case PinkbeanPogoStick:
                case NightlordShadowWeb:
                case RwExplosionCannon:
                case Unknown83: // Unknown
                case Unknown84: {
                    byte moveAction = iPacket.DecodeByte(); // not sure the purpose of this either
                    short tElapse = iPacket.DecodeShort(); // not sure the purpose of this either
                    byte bForcedStop = iPacket.DecodeByte(); // not sure the purpose of this either

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
                case ZeroTag:
                case RetreatShot:
                case DbBladeAscension:
                case MobRightAngle:
                case PinkbeanRollingAir:
                case FinalToss:
                case TeleportKinesis1:
                case TeleportAran1:
                case Unknown82: {
                    short xpos = iPacket.DecodeShort();
                    short ypos = iPacket.DecodeShort();
                    short foothold = iPacket.DecodeShort();
                    byte moveAction = iPacket.DecodeByte(); // not sure the purpose of this either
                    short tElapse = iPacket.DecodeShort(); // not sure the purpose of this either
                    byte bForcedStop = iPacket.DecodeByte(); // not sure the purpose of this either

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
                    // This part is definitely right
                    byte moveAction = iPacket.DecodeByte(); // not sure the purpose of this either
                    short tElapse = iPacket.DecodeShort(); // not sure the purpose of this either
                    byte bForcedStop = iPacket.DecodeByte(); // not sure the purpose of this either

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
                    short vx = iPacket.DecodeShort(); // not sure what this is. Maybe a wobble?
                    short vy = iPacket.DecodeShort(); // not sure what this is. Maybe a wobble?
                    // This part is definitely right
                    byte moveAction = iPacket.DecodeByte(); // not sure the purpose of this either
                    short tElapse = iPacket.DecodeShort(); // not sure the purpose of this either
                    byte bForcedStop = iPacket.DecodeByte(); // not sure the purpose of this either

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
                case StatChange: { // Stat Change (probably chair)
                    byte bStat = iPacket.DecodeByte();
                    MovementTypeH movement = new MovementTypeH();
                    movement.setCommand(command);
                    movement.setbStat(bStat);
                    res.add(movement);
                    break;
                }
                default:
                    LogHelper.UNCODED.get().info("Movement case: " + command + ", this means that MPA info needs to be added.");
                    break;
            }
        }
        return res;
    }

    public static void updatePosition(List<LifeMovementFragment> movement, AnimatedMapleMapObject target) {
        for (LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                Point position = ((LifeMovement) move).getPosition();
                target.setPosition(position);
                target.setStance(((LifeMovement) move).getStance());
            }
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
            FlashJump = 28,
            DoubleJump = 29,
            DoubleJumpDown = 30,
            TripleJump = 31,
            FlashJumpChangeEff = 32,
            RocketBooster = 33,
            BackstepShot = 34,
            CannonJump = 35,
            QuickSilverJump = 36,
            MobPowerKnockback = 37,
            VerticalJump = 38,
            CustomImpact = 39,
            CustomImpact2 = 40,
            CombatStep = 41,
            Hit = 42,
            TimeBombAttack = 43,
            SnowballTouch = 44,
            BuffZoneEffect = 45,
            LeafTornado = 46,
            StylishRope = 47,
            RopeConnect = 48,
            StrikerUppercut = 49,
            Crawl = 50,
            TeleportByMobSkillArea = 51,
            ZeroTag = 52,
            RetreatShot = 53,
            DbBladeAscension = 54,
            ImpactIgnoreMovepath = 55,
            AngleImpact = 56,
            StarplanetRidingBooster = 57,
            UserToss = 58,
            SlashJump = 59,
            MobLadder = 60,
            MobRightAngle = 61,
            MobStopNodeStart = 62,
            MobBeforeNode = 63,
            MobTeleport = 64,
            MobAttackRush = 65,
            MobAttackRushStop = 66,
            MobAttackLeap = 67,
            BattlePvPMugongSomerSault = 68,
            BattlePvPHelenaStepShot = 69,
            SunOfGlory = 70,
            HookshotStart = 71,
            Hookshot = 72,
            HookshotEnd = 73,
            PinkbeanPogoStick = 74,
            PinkbeanPogoStickEnd = 75,
            PinkbeanRollingAir = 76,
            FinalToss = 77,
            TeleportKinesis1 = 78,
            NightlordShadowWeb = 79,
            TeleportAran1 = 80,
            RwExplosionCannon = 81,
            Unknown82 = 82,
            Unknown83 = 83,
            Unknown84 = 84,
            Unknown85 = 85,
            Unknown86 = 86;
}
