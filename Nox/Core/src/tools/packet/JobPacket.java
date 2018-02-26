package tools.packet;

import java.awt.Point;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import client.CharacterTemporaryStat;
import constants.skills.Shade;
import handling.world.AttackMonster;
import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;
import server.MapleStatInfo;
import server.Randomizer;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MapleForceAtom;
import server.maps.objects.MapleForceAtomTypes;
import tools.packet.CField.EffectPacket.UserEffectCodes;

/**
 *
 * @author Itzik
 */
public class JobPacket {

    public static Packet createForceAtom(MapleForceAtom atom) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.Encode(atom.isByMob());//bByMob
        if (!atom.isByMob()) {
            oPacket.EncodeInteger(atom.getCharId());
        }
        oPacket.EncodeInteger(atom.getType().getType());//nForceAtomType

        if (atom.getType() != MapleForceAtomTypes.ZEROFORCE_LOCAL && atom.getType() != MapleForceAtomTypes.EVENTPOINT_LOCAL) {
            oPacket.Encode(atom.isToMob());//bToMob
            switch (atom.getType()) {
                case FLYINGSWORD_BOTH:
                case SOULSEEKER_BOTH:
                case AEGISACTIVE_BOTH:
                case TRIFLINGWHIM_BOTH:
                case MARKOFASSASSIN_BOTH:
                case MESOEXPLOSION_BOTH:
                case POSSESSION_BOTH:
                case NONTARGET_BOTH:
                case SSFSHOOTING_BOTH:
                case HORMING:
                case MAGIC_WRECKAGE:
                case ADV_MAGIC_WRECKAGE:
                case AUTO_SOULSEEKER_BOTH:
                    oPacket.EncodeInteger(atom.getObjects().size());
                    for (int object : atom.getObjects()) {
                        oPacket.EncodeInteger(object); //dwTarget oid
                    }
                    break;
                default:
                    oPacket.EncodeInteger(atom.getObjects().get(0));//dwFirstMobID
                    break;
            }
        }
        oPacket.EncodeInteger(atom.getSkillId()); //skillId
        for (int i = 0; i < atom.getAttackCount(); i++) {
            oPacket.Encode(i < atom.getAttackCount()); //part of the doWhile loop
            oPacket.EncodeInteger(i + 2); //dwKey
            oPacket.EncodeInteger(0); //nInc
            oPacket.EncodeInteger(atom.getFirstImpact()); //nFirstImpact
            oPacket.EncodeInteger(atom.getSecondImpact()); //nSecondImpact
            oPacket.EncodeInteger(atom.getAngle()); //nAngle
            oPacket.EncodeInteger(atom.getSpawnDelay()); //nStartDelay
            oPacket.EncodeInteger(atom.getPosition().x); //char pos x
            oPacket.EncodeInteger(atom.getPosition().y); //char pos y
            oPacket.EncodeInteger(0); //dwCreateTime
            oPacket.EncodeInteger(0); //nMaxHitCount
        }
        if (atom.getType() == MapleForceAtomTypes.QUIVERCATRIDGE_BOTH) {
            oPacket.EncodeInteger(0);//rcStart.left
            oPacket.EncodeInteger(0);//rcStart.top
            oPacket.EncodeInteger(0);//rcStart.right
            oPacket.EncodeInteger(0);//rcStart.bottom
            oPacket.EncodeInteger(0);//nBulletItemID
        }
        if (atom.getType() == MapleForceAtomTypes.ZEROFORCE_LOCAL || atom.getType() == MapleForceAtomTypes.SHADOW_BAT_BOTH) {
            oPacket.EncodeInteger(0);//rcStart.left
            oPacket.EncodeInteger(0);//rcStart.top
            oPacket.EncodeInteger(0);//rcStart.right
            oPacket.EncodeInteger(0);//rcStart.bottom
        }
        if (atom.getType() == MapleForceAtomTypes.SHADOW_BAT_BOUND_BOTH) {
            oPacket.EncodeInteger(0);//rcStart.right
            oPacket.EncodeInteger(0);//rcStart.top
        }
        if (atom.getType() == MapleForceAtomTypes.SSFSHOOTING_BOTH) {
            oPacket.EncodeInteger(0);//nArriveDir
            oPacket.EncodeInteger(0);//nArriveRange
        }
        if (atom.getType() == MapleForceAtomTypes.TYPINGGAME_BOTH) {
            oPacket.EncodeInteger(0);//nForceTargetX
            oPacket.EncodeInteger(0);//ptForcedTarget.y
        }
        oPacket.Encode(0);
        return oPacket.ToPacket();
    }

    public static Packet explodeMeso(Point pos, int cid, int skill, int size, int obj) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.Encode(0);
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(12);
        oPacket.Encode(1);
        oPacket.EncodeInteger(size);
        for (int i = 0; i < size; i++) {
            oPacket.EncodeInteger(obj);
        }
        oPacket.EncodeInteger(4210014);//4210014 4211006
        int idk = 2;
        for (int i = 0; i < 4; i++) {
            oPacket.Encode(1);
            oPacket.EncodeInteger(idk);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(Randomizer.rand(0x28, 0x2B));// 2A/2B/2C/28
            oPacket.EncodeInteger(3);//how many from 1 i dunno it shows 3
            oPacket.EncodeInteger(Randomizer.rand(0x31, 0x74));// random
            oPacket.EncodeInteger(700);
            oPacket.EncodeInteger(pos.x);
            oPacket.EncodeInteger(pos.y);
            idk++;
        }
        oPacket.Encode(0);
        return oPacket.ToPacket();
    }

    public static class WindArcherPacket {

        public static Packet TrifleWind(int cid, int skillid, int ga, int oid, int gu) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(7);
            oPacket.Encode(1);
            oPacket.EncodeInteger(gu);
            oPacket.EncodeInteger(oid);
            oPacket.EncodeInteger(skillid);
            for (int i = 1; i < ga; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(2 + i);
                oPacket.EncodeInteger(1);
                oPacket.EncodeInteger(Randomizer.rand(0x2A, 0x2F));
                oPacket.EncodeInteger(7 + i);
                oPacket.EncodeInteger(Randomizer.rand(5, 0xAB));
                oPacket.EncodeInteger(Randomizer.rand(0, 0x37));
                oPacket.EncodeLong(0);
                oPacket.EncodeInteger(Randomizer.nextInt());
                oPacket.EncodeInteger(0);
            }
            oPacket.Encode(0);

            oPacket.Fill(0, 69); //for no dc goodluck charm! >:D xD LOL

            return oPacket.ToPacket();
        }
    }

    public static class ShadePacket {
        
        public static Packet FoxSpirit(MapleCharacter pPlayer, AttackMonster oMonster) {
            OutPacket oPacket = new OutPacket(80);
            
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            
            oPacket.Encode(0); // bByMob
            oPacket.EncodeInteger(pPlayer.getId()); // nCharId
            oPacket.EncodeInteger(13); // nAtomType // 17 = Orbital Flame
            oPacket.Encode(1); // bToMob
            oPacket.EncodeInteger(1); // Unkown
            oPacket.EncodeInteger(oMonster.getObjectId()); // nObjectId
            
            int nAtomId = Shade.FOX_SPIRITS_ATOM;
            int nAtomCount = 2;
            if (pPlayer.hasSkill(Shade.FOX_SPIRIT_MASTERY)) {
                nAtomId = Shade.FOX_SPIRITS_ATOM_2;
                nAtomCount = 3;
            }
            oPacket.EncodeInteger(nAtomId); // nAtomId
            
            for (int i = 0; i < nAtomCount; i++) { // nAttackCount
                oPacket.Encode(1);
                oPacket.EncodeInteger(i + 2); //dwKey
                oPacket.EncodeInteger(0); // nInc
                oPacket.EncodeInteger(0x23); // nFirstImpact
                oPacket.EncodeInteger(5 + i); // nSecondImpact
                oPacket.EncodeInteger(Randomizer.rand(80, 100)); // nAngle
                oPacket.EncodeInteger(Randomizer.rand(200, 300)); // nStartDelay
                oPacket.EncodeLong(0); // nCharPositionX
                oPacket.EncodeInteger(Randomizer.nextInt()); // nCharPositionY
                oPacket.EncodeInteger(0); // dwCreateTime
            }
            
            oPacket.Encode(0); // Unkown
            oPacket.Fill(0, 69); // For no d/c memes.
            
            return oPacket.ToPacket();
        }
        
        /*public static Packet FoxSpirit2(int cid, int skillid, int ga, int oid, int gu) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(13);
            oPacket.Encode(1);
            oPacket.EncodeInteger(gu);
            oPacket.EncodeInteger(oid);
            oPacket.EncodeInteger(skillid);
            for (int i = 1; i < ga; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(2 + i);
                oPacket.EncodeInteger(1);
                oPacket.EncodeInteger(Randomizer.rand(0x2A, 0x2F));
                oPacket.EncodeInteger(7 + i);
                oPacket.EncodeInteger(Randomizer.rand(5, 0xAB));
                oPacket.EncodeInteger(Randomizer.rand(0, 0x37));
                oPacket.EncodeLong(0);
                oPacket.EncodeInteger(Randomizer.nextInt());
                oPacket.EncodeInteger(0);
            }
            oPacket.Encode(0);

            oPacket.Fill(0, 69);

            return oPacket.ToPacket();
        }*/
    }
    
    public static class NightWalkerPacket {
        
        public static Packet ShadowBats(int nCharId, int nObjectId) {
            OutPacket oPacket = new OutPacket(80);
            
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(nCharId);
            oPacket.EncodeInteger(16); // nAtomType
            oPacket.Encode(1);
            oPacket.EncodeInteger(nObjectId);
            oPacket.EncodeInteger(14000028); // nAtomId
            
            for (int i = 0; i < 3; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(i + 2);
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(0x23);
                oPacket.EncodeInteger(5);
                oPacket.EncodeInteger(Randomizer.rand(80, 100));
                oPacket.EncodeInteger(Randomizer.rand(200, 300));
                oPacket.EncodeLong(0); //v196
                oPacket.EncodeInteger(Randomizer.nextInt());
                oPacket.EncodeInteger(0);
            }
            
            oPacket.Encode(0);
            oPacket.Fill(0, 69); // For no d/c memes.
            
            return oPacket.ToPacket();
        }
    }
    
    public static class AngelicBusterPacket {

        public static Packet Starshooter(int cid, int skillid, int ga, int oid, int gu) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(18);
            oPacket.Encode(1);
            oPacket.EncodeInteger(gu);
            oPacket.EncodeInteger(oid);
            oPacket.EncodeInteger(skillid);
            for (int i = 1; i < ga; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(2 + i);
                oPacket.EncodeInteger(1);
                oPacket.EncodeInteger(Randomizer.rand(0x2A, 0x2F));
                oPacket.EncodeInteger(7 + i);
                oPacket.EncodeInteger(Randomizer.rand(5, 0xAB));
                oPacket.EncodeInteger(Randomizer.rand(0, 0x37));
                oPacket.EncodeLong(0);
                oPacket.EncodeInteger(Randomizer.nextInt());
                oPacket.EncodeInteger(0);
            }
            oPacket.Encode(0);

            oPacket.Fill(0, 69); //for no dc goodluck charm! >:D xD LOL

            return oPacket.ToPacket();
        }
    }
    
    public static class KaiserPacket {

        public static Packet sendKaiserSkillShortcut(int[] skills) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.KaiserSkillShortcut.getValue());
            for (int i = 0; i < 3; i++) {
                if (skills[i] != 0) {
                    oPacket.Encode(true);
                    oPacket.Encode(i);
                    oPacket.EncodeInteger(skills[i]);
                    int x = 0;
                    oPacket.Encode(x);
                    if (x != 0) {
                        oPacket.Encode(0);
                        oPacket.EncodeInteger(0);
                    }
                }
            }
            return oPacket.ToPacket();
        }

    }

    public static class PhantomPacket {

        public static Packet ThrowCarte(int nCharId, int nObjectId) {
            OutPacket oPacket = new OutPacket(80);
            
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(nCharId);
            oPacket.EncodeInteger(1); // nAtomType
            oPacket.Encode(1);
            oPacket.EncodeInteger(nObjectId);
            oPacket.EncodeInteger(24120002); // nAtomId
            
            for (int i = 0; i < 3; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(i + 2);
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(0x23);
                oPacket.EncodeInteger(5);
                oPacket.EncodeInteger(Randomizer.rand(80, 100));
                oPacket.EncodeInteger(Randomizer.rand(200, 300));
                oPacket.EncodeLong(0); //v196
                oPacket.EncodeInteger(Randomizer.nextInt());
                oPacket.EncodeInteger(0);
            }
            
            oPacket.Encode(0);
            oPacket.Fill(0, 69); // For no d/c memes.
            
            return oPacket.ToPacket();
        }
        
        public static Packet addStolenSkill(int jobNum, int index, int skill, int level) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ChangeStealMemoryResult.getValue());
            oPacket.Encode(1);
            oPacket.Encode(0);
            oPacket.EncodeInteger(jobNum);
            oPacket.EncodeInteger(index);
            oPacket.EncodeInteger(skill);
            oPacket.EncodeInteger(level);
            oPacket.EncodeInteger(0);
            oPacket.Encode(0);

            return oPacket.ToPacket();
        }

        public static Packet removeStolenSkill(int jobNum, int index) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ChangeStealMemoryResult.getValue());
            oPacket.Encode(1);
            oPacket.Encode(3);
            oPacket.EncodeInteger(jobNum);
            oPacket.EncodeInteger(index);
            oPacket.Encode(0);

            return oPacket.ToPacket();
        }

        public static Packet replaceStolenSkill(int base, int skill) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ChangeStealMemoryResult.getValue());
            oPacket.Encode(1);
            oPacket.Encode(skill > 0 ? 1 : 0);
            oPacket.EncodeInteger(base);
            oPacket.EncodeInteger(skill);

            return oPacket.ToPacket();
        }

        public static Packet gainCardStack(int oid, int runningId, int color, int skillid, int damage, int times) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(oid);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(damage);
            oPacket.EncodeInteger(skillid);
            for (int i = 0; i < times; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(damage == 0 ? runningId + i : runningId);
                oPacket.EncodeInteger(color);
                oPacket.EncodeInteger(Randomizer.rand(15, 29));
                oPacket.EncodeInteger(Randomizer.rand(7, 11));
                oPacket.EncodeInteger(Randomizer.rand(0, 9));
            }
            oPacket.Encode(0);

            oPacket.Fill(0, 69); //for no DC it requires this do not remove

            return oPacket.ToPacket();
        }

        public static Packet updateCardStack(final int total) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.IncJudgementStack.getValue());
            oPacket.Encode(total);

            return oPacket.ToPacket();
        }

        public static Packet getCarteAnimation(int cid, int oid, int job, int total, int numDisplay) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(1);

            oPacket.EncodeInteger(oid);
            oPacket.EncodeInteger(job == 2412 ? 24120002 : 24100003);
            oPacket.Encode(1);
            for (int i = 1; i <= numDisplay; i++) {
                oPacket.EncodeInteger(total - (numDisplay - i));
                oPacket.EncodeInteger(job == 2412 ? 2 : 0);

                oPacket.EncodeInteger(15 + Randomizer.nextInt(15));
                oPacket.EncodeInteger(7 + Randomizer.nextInt(5));
                oPacket.EncodeInteger(Randomizer.nextInt(4));

                oPacket.Encode(i == numDisplay ? 0 : 1);
            }

            return oPacket.ToPacket();
        }
    }

    public static class AngelicPacket {

        public static Packet showRechargeEffect() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.Encode(UserEffectCodes.ResetOnStateForOnOffSkill.getEffectId());

            return oPacket.ToPacket();
        }

        public static Packet DressUpTime(byte type) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
            oPacket.Encode(type);
            oPacket.EncodeShort(7707);
            oPacket.Encode(2);
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
            return oPacket.ToPacket();
        }

        public static Packet updateDress(int transform, MapleCharacter chr) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.UserSetDressUpState.getValue());
            oPacket.EncodeInteger(chr.getId());
            oPacket.EncodeInteger(transform);
            return oPacket.ToPacket();
        }

        public static Packet lockSkill(int skillid) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.SetOffStateForOnOffSkill.getValue());
            oPacket.EncodeInteger(skillid);
            return oPacket.ToPacket();
        }

        public static Packet unlockSkill() {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ResetOnStateForOnOffSkill.getValue());
            oPacket.EncodeInteger(0);
            return oPacket.ToPacket();
        }

        public static Packet absorbingSoulSeeker(int characterid, int size, Point essence1, Point essence2, int skillid, boolean creation) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(!creation ? 0 : 1);
            oPacket.EncodeInteger(characterid);
            if (!creation) {
                // false
                oPacket.EncodeInteger(3);
                oPacket.Encode(1);
                oPacket.Encode(size);
                oPacket.Fill(0, 3);
                oPacket.EncodeShort(essence1.x);
                oPacket.EncodeShort(essence1.y);
                oPacket.EncodeShort(essence2.y);
                oPacket.EncodeShort(essence2.x);
            } else {
                // true
                oPacket.EncodeShort(essence1.x);
                oPacket.EncodeShort(essence1.y);
                oPacket.EncodeInteger(4);
                oPacket.Encode(1);
                oPacket.EncodeShort(essence1.y);
                oPacket.EncodeShort(essence1.x);
            }
            oPacket.EncodeInteger(skillid);
            if (!creation) {
                for (int i = 0; i < 2; i++) {
                    oPacket.Encode(1);
                    oPacket.EncodeInteger(Randomizer.rand(19, 20));
                    oPacket.EncodeInteger(1);
                    oPacket.EncodeInteger(Randomizer.rand(18, 19));
                    oPacket.EncodeInteger(Randomizer.rand(20, 23));
                    oPacket.EncodeInteger(Randomizer.rand(36, 55));
                    oPacket.EncodeInteger(540);
                    oPacket.EncodeShort(0);//new 142
                    oPacket.Fill(0, 6);//new 143
                }
            } else {
                oPacket.Encode(1);
                oPacket.EncodeInteger(Randomizer.rand(6, 21));
                oPacket.EncodeInteger(1);
                oPacket.EncodeInteger(Randomizer.rand(42, 45));
                oPacket.EncodeInteger(Randomizer.rand(4, 7));
                oPacket.EncodeInteger(Randomizer.rand(267, 100));
                oPacket.EncodeInteger(0);//540
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(0);
            }
            oPacket.Encode(0);
            return oPacket.ToPacket();
        }

        public static Packet SoulSeekerRegen(MapleCharacter chr, int sn) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(1);
            oPacket.EncodeInteger(chr.getId());
            oPacket.EncodeInteger(sn);
            oPacket.EncodeInteger(4);
            oPacket.Encode(1);
            oPacket.EncodeInteger(sn);
            oPacket.EncodeInteger(65111007); // hide skills
            oPacket.Encode(1);
            oPacket.EncodeInteger(Randomizer.rand(0x06, 0x10));
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(Randomizer.rand(0x28, 0x2B));
            oPacket.EncodeInteger(Randomizer.rand(0x03, 0x04));
            oPacket.EncodeInteger(Randomizer.rand(0xFA, 0x49));
            oPacket.EncodeInteger(0);
            oPacket.EncodeLong(0);
            oPacket.Encode(0);
            return oPacket.ToPacket();
        }

        public static Packet SoulSeeker(MapleCharacter chr, int skillid, int sn, int sc1, int sc2) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(chr.getId());
            oPacket.EncodeInteger(3);
            oPacket.Encode(1);
            oPacket.EncodeInteger(sn);
            if (sn >= 1) {
                oPacket.EncodeInteger(sc1);//SHOW_ITEM_GAIN_INCHAT
                if (sn == 2) {
                    oPacket.EncodeInteger(sc2);
                }
            }
            oPacket.EncodeInteger(65111007); // hide skills
            for (int i = 0; i < 2; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(i + 2);
                oPacket.EncodeInteger(1);
                oPacket.EncodeInteger(Randomizer.rand(0x0F, 0x10));
                oPacket.EncodeInteger(Randomizer.rand(0x1B, 0x22));
                oPacket.EncodeInteger(Randomizer.rand(0x1F, 0x24));
                oPacket.EncodeInteger(540);
                oPacket.EncodeInteger(0);//wasshort new143
                oPacket.EncodeInteger(0);//new143
            }
            oPacket.Encode(0);
            return oPacket.ToPacket();
        }
    }

    public static class LuminousPacket {

        public static Packet updateLuminousGauge(int darktotal, int lighttotal, int darktype, int lighttype) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ChangeLarknessStack.getValue());
            oPacket.EncodeInteger(darktotal);
            oPacket.EncodeInteger(lighttotal);
            oPacket.EncodeInteger(darktype);
            oPacket.EncodeInteger(lighttype);
            oPacket.EncodeInteger(281874974);//1210382225

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }

        public static Packet giveLuminousState(int skill, int light, int dark, int duration) {
            final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
            stat.put(CharacterTemporaryStat.Larkness, 1);
            int newLightGauge = Math.max(light, -1);
            int newDarkGauge = Math.max(dark, -1);
            return BuffPacket.giveBuff(null, skill, duration, stat, null, 0, 0, newLightGauge, newDarkGauge);
        }

        public static Packet giveLifeTidal(boolean isHpBetter, int value) {
            final EnumMap<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.LifeTidal, isHpBetter ? 2 : 1);
            return BuffPacket.giveBuff(null, 27110007, 2100000000, statups, null, value, 0, 0, 0);
        }
    }

    public static class KinesisPacket {

        public static Packet Orbs(int cid, int skillid, int ga, int oid, int gu) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(22);
            oPacket.Encode(1);
            oPacket.EncodeInteger(gu);
            oPacket.EncodeInteger(oid);
            oPacket.EncodeInteger(skillid);
            for (int i = 1; i < ga; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(2 + i);
                oPacket.EncodeInteger(1);
                oPacket.EncodeInteger(Randomizer.rand(0x2A, 0x2F));
                oPacket.EncodeInteger(7 + i);
                oPacket.EncodeInteger(Randomizer.rand(5, 0xAB));
                oPacket.EncodeInteger(Randomizer.rand(0, 0x37));
                oPacket.EncodeLong(0);
                oPacket.EncodeInteger(Randomizer.nextInt());
                oPacket.EncodeInteger(0);
            }
            oPacket.Encode(0);

            oPacket.Fill(0, 69); //for no dc goodluck charm! >:D xD LOL

            return oPacket.ToPacket();
        }
        
        public static Packet givePsychicPoint(int skillid, int point) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.KinesisPsychicPoint);

            oPacket.EncodeShort(point);
            oPacket.EncodeInteger(skillid);
            oPacket.Fill(0, 22);

            return oPacket.ToPacket();
        }

        /*public static Packet givePsychicPoint(short amount) { 
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.KinesisPsychicPoint);

            oPacket.EncodeShort(amount);
            oPacket.EncodeInteger(0); //skill id
            oPacket.EncodeInteger(-1); //duration
            oPacket.Fill(0, 18);

            return oPacket.ToPacket();
        }*/
    }

    public static class XenonPacket {

        public static Packet giveXenonSupply(short amount) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.SurplusSupply);

            oPacket.EncodeShort(amount);
            oPacket.EncodeInteger(30020232); //skill id
            oPacket.EncodeInteger(-1); //duration
            oPacket.Fill(0, 18);

            return oPacket.ToPacket();
        }

        public static Packet giveAmaranthGenerator() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.SurplusSupply, 0);
            statups.put(CharacterTemporaryStat.AmaranthGenerator, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(20); //gauge fill
            oPacket.EncodeInteger(30020232); //skill id
            oPacket.EncodeInteger(-1); //duration

            oPacket.EncodeShort(1);
            oPacket.EncodeInteger(36121054); //skill id
            oPacket.EncodeInteger(10000); //duration

            oPacket.Fill(0, 5);
            oPacket.EncodeInteger(1000);
            oPacket.EncodeInteger(1);
            oPacket.Fill(0, 1);

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }

        public static Packet PinPointRocket(int cid, List<Integer> moblist) {
            OutPacket packet = new OutPacket(80);
            packet.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            packet.Encode(0);
            packet.EncodeInteger(cid);
            packet.EncodeInteger(6);
            packet.Encode(1);
            packet.EncodeInteger(moblist.size());
            for (int i = 0; i < moblist.size(); i++) {
                packet.EncodeInteger(moblist.get(i));
            }
            packet.EncodeInteger(36001005);
            for (int i = 1; i <= moblist.size(); i++) {
                packet.Encode(1);
                packet.EncodeInteger(i + 7);
                packet.EncodeInteger(0);
                packet.EncodeInteger(Randomizer.rand(10, 20));
                packet.EncodeInteger(Randomizer.rand(20, 40));
                packet.EncodeInteger(Randomizer.rand(40, 200));
                packet.EncodeInteger(Randomizer.rand(500, 2000));
                packet.EncodeLong(0); //v196
                packet.EncodeInteger(Randomizer.nextInt());
                packet.EncodeInteger(0);
            }
            packet.Encode(0);

            packet.Fill(0, 69); //for no dc
            return packet.ToPacket();
        }

        public static Packet MegidoFlameRe(int cid, int oid) {
            OutPacket packet = new OutPacket(80);
            packet.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            packet.Encode(0);
            packet.EncodeInteger(cid);
            packet.EncodeInteger(3);
            packet.Encode(1);
            packet.EncodeInteger(1);
            packet.EncodeInteger(oid);
            packet.EncodeInteger(2121055);
            packet.Encode(1);
            packet.EncodeInteger(2);
            packet.EncodeInteger(2);
            packet.EncodeInteger(Randomizer.rand(10, 17));
            packet.EncodeInteger(Randomizer.rand(10, 16));
            packet.EncodeInteger(Randomizer.rand(40, 52));
            packet.EncodeInteger(20);
            packet.EncodeLong(0);
            packet.EncodeLong(0);
            packet.Encode(0);
            packet.Fill(0, 69); //for no dc
            return packet.ToPacket();
        }

        public static Packet ShieldChacingRe(int cid, int unkwoun, int oid, int unkwoun2, int unkwoun3) {
            OutPacket packet = new OutPacket(80);
            packet.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            packet.Encode(1);
            packet.EncodeInteger(cid);
            packet.EncodeInteger(unkwoun);
            packet.EncodeInteger(4);
            packet.Encode(1);
            packet.EncodeInteger(oid);
            packet.EncodeInteger(31221014);

            packet.Encode(1);
            packet.EncodeInteger(unkwoun2 + 1);
            packet.EncodeInteger(3);
            packet.EncodeInteger(unkwoun3);
            packet.EncodeInteger(3);
            packet.EncodeInteger(Randomizer.rand(36, 205));
            packet.EncodeInteger(0);
            packet.EncodeLong(0);
            packet.EncodeInteger(Randomizer.nextInt());
            packet.EncodeInteger(0);
            packet.Encode(0);
            packet.Fill(0, 69); //for no dc
            return packet.ToPacket();
        }

        public static Packet ShieldChacing(int cid, List<Integer> moblist, int skillid) {
            OutPacket packet = new OutPacket(80);
            packet.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            packet.Encode(0);
            packet.EncodeInteger(cid);
            packet.EncodeInteger(3);
            packet.Encode(1);
            packet.EncodeInteger(moblist.size());
            for (int i = 0; i < moblist.size(); i++) {
                packet.EncodeInteger(((Integer) moblist.get(i)).intValue());
            }
            packet.EncodeInteger(skillid);
            for (int i = 1; i <= moblist.size(); i++) {
                packet.Encode(1);
                packet.EncodeInteger(1 + i);
                packet.EncodeInteger(3);
                packet.EncodeInteger(Randomizer.rand(1, 20));
                packet.EncodeInteger(Randomizer.rand(20, 50));
                packet.EncodeInteger(Randomizer.rand(50, 200));
                packet.EncodeInteger(skillid == 2121055 ? 720 : 660);
                packet.EncodeLong(0);
                packet.EncodeInteger(Randomizer.nextInt());
                packet.EncodeInteger(0);
            }
            packet.Encode(0);
            packet.Fill(0, 69); //for no dc
            return packet.ToPacket();
        }

        public static Packet EazisSystem(int cid, int oid) {
            OutPacket packet = new OutPacket(80);
            packet.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
            packet.Encode(0);
            packet.EncodeInteger(cid);
            packet.EncodeInteger(5);
            packet.Encode(1);
            packet.EncodeInteger(oid);
            packet.EncodeInteger(36110004);
            for (int i = 0; i < 3; i++) {
                packet.Encode(1);
                packet.EncodeInteger(i + 2);
                packet.EncodeInteger(0);
                packet.EncodeInteger(0x23);
                packet.EncodeInteger(5);
                packet.EncodeInteger(Randomizer.rand(80, 100));
                packet.EncodeInteger(Randomizer.rand(200, 300));
                packet.EncodeLong(0); //v196
                packet.EncodeInteger(Randomizer.nextInt());
                packet.EncodeInteger(0);
            }
            packet.Encode(0);
            packet.Fill(0, 69); //for no dc
            return packet.ToPacket();
        }
    }

    public static class AvengerPacket {

        public static Packet giveAvengerHpBuff(int hp) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());

            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.LifeTidal); //for now
            oPacket.EncodeShort(3);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(2100000000);
            oPacket.Fill(0, 5);
            oPacket.EncodeInteger(hp);
            oPacket.Fill(0, 9);

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }

        public static Packet giveExceed(short amount) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.OverloadCount);

            oPacket.EncodeShort(amount);
            oPacket.EncodeInteger(30010230); //skill id
            oPacket.EncodeInteger(0); //duration (-1)
            oPacket.Fill(0, 14);

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }

        public static Packet giveExceedAttack(int skill, short amount) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.ExceedOverload);

            oPacket.EncodeShort(amount);
            oPacket.EncodeInteger(skill); //skill id
            oPacket.EncodeInteger(15000); //duration
            oPacket.Fill(0, 18);

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }

        public static Packet cancelExceed() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatReset.getValue());

            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.OverloadCount, 0);
            statups.put(CharacterTemporaryStat.ExceedOverload, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            return oPacket.ToPacket();
        }
    }

    public static class DawnWarriorPacket {

        public static Packet giveMoonfallStance(int level) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            //statups.put(CharacterTemporaryStat.CRITICAL_PERCENT_UP, 0);
            //statups.put(CharacterTemporaryStat.MOON_Stance2, 0);
            //statups.put(CharacterTemporaryStat.WARRIOR_Stance, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(level);
            oPacket.EncodeInteger(11101022);
            oPacket.EncodeInteger(Integer.MAX_VALUE);
            oPacket.EncodeShort(1);
            oPacket.EncodeInteger(11101022);
            oPacket.EncodeInteger(Integer.MAX_VALUE);
            oPacket.EncodeInteger(0);
            oPacket.Encode(5);
            oPacket.Encode(1);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(11101022);
            oPacket.EncodeInteger(level);
            oPacket.EncodeInteger(Integer.MAX_VALUE);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }

        public static Packet giveSunriseStance(int level) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.Booster, 0);
            statups.put(CharacterTemporaryStat.IndieDamR, 0);
            statups.put(CharacterTemporaryStat.Stance, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(level);
            oPacket.EncodeInteger(11111022);
            oPacket.EncodeInteger(Integer.MAX_VALUE);
            oPacket.EncodeInteger(0);
            oPacket.Encode(5);
            oPacket.Encode(1);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(11111022);
            oPacket.EncodeInteger(-1);
            oPacket.EncodeInteger(Integer.MAX_VALUE);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(11111022);
            oPacket.EncodeInteger(0x19);
            oPacket.EncodeInteger(Integer.MAX_VALUE);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }

        public static Packet giveEquinox_Moon(int level, int duration) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            //statups.put(CharacterTemporaryStat.CRITICAL_PERCENT_UP, 0);
            //statups.put(CharacterTemporaryStat.MOON_Stance2, 0);
            //statups.put(CharacterTemporaryStat.EQUINOX_Stance, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(level);
            oPacket.EncodeInteger(11121005);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeShort(1);
            oPacket.EncodeInteger(11121005);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeInteger(0);
            oPacket.Encode(5);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(11121005);
            oPacket.EncodeInteger(level);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }

        public static Packet giveEquinox_Sun(int level, int duration) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.Booster, 0);
            statups.put(CharacterTemporaryStat.IndieDamR, 0);
            //statups.put(CharacterTemporaryStat.EQUINOX_Stance, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(level);
            oPacket.EncodeInteger(11121005);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeInteger(0);
            oPacket.Encode(5);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(11121005);
            oPacket.EncodeInteger(-1);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(11121005);
            oPacket.EncodeInteger(0x19);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeInteger(duration);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);

            oPacket.Fill(0, 69); //for no dc

            return oPacket.ToPacket();
        }
    }
}
