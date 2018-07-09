package tools.packet;

import java.awt.Point;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import client.CharacterTemporaryStat;
import constants.skills.Assassin;
import constants.skills.Phantom;
import constants.skills.Shade;
import client.jobs.KinesisPsychicLock;
import handling.world.AttackMonster;
import service.SendPacketOpcode;
import net.OutPacket;

import server.Randomizer;
import server.life.Mob;
import server.maps.objects.User;
import tools.packet.CField.EffectPacket.UserEffectCodes;

/**
 *
 * @author Itzik
 * @author Mazen Massoud
 */
public class JobPacket {

    public static OutPacket explodeMeso(Point pos, int cid, int skill, int size, int obj) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(12);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(size);
        for (int i = 0; i < size; i++) {
            oPacket.EncodeInt(obj);
        }
        oPacket.EncodeInt(4210014);//4210014 4211006
        int idk = 2;
        for (int i = 0; i < 4; i++) {
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(idk);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(Randomizer.rand(0x28, 0x2B));// 2A/2B/2C/28
            oPacket.EncodeInt(3);//how many from 1 i dunno it shows 3
            oPacket.EncodeInt(Randomizer.rand(0x31, 0x74));// random
            oPacket.EncodeInt(700);
            oPacket.EncodeInt(pos.x);
            oPacket.EncodeInt(pos.y);
            idk++;
        }
        oPacket.EncodeByte(0);
        return oPacket;
    }

    public static class NightLordPacket {

        public static OutPacket AssassinsMark(User pPlayer, Mob pMob) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());

            oPacket.EncodeByte(1); // bByMob
            oPacket.EncodeInt(pMob.getObjectId());

            oPacket.EncodeInt(pPlayer.getId());
            oPacket.EncodeInt(11); // nAtomType

            oPacket.EncodeByte(1); // bToMob
            oPacket.EncodeInt(pMob.getObjectId());

            oPacket.EncodeInt(Assassin.ASSASSINS_MARK_2); // nSkillID

            for (int i = 0; i < 3; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(i + 2);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0x23);
                oPacket.EncodeInt(5);
                oPacket.EncodeInt(Randomizer.rand(80, 100));
                oPacket.EncodeInt(Randomizer.rand(200, 300));
                oPacket.EncodeLong(0); //v196
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }

            oPacket.EncodeByte(0);
            oPacket.Fill(0, 99); // For no d/c memes.

            return oPacket;
        }
    }

    public static class WindArcherPacket {

        public static OutPacket TrifleWind(int cid, int skillid, int ga, int oid, int gu) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(7);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(gu);
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(skillid);
            for (int i = 1; i < ga; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(2 + i);
                oPacket.EncodeInt(1);
                oPacket.EncodeInt(Randomizer.rand(0x2A, 0x2F));
                oPacket.EncodeInt(7 + i);
                oPacket.EncodeInt(Randomizer.rand(5, 0xAB));
                oPacket.EncodeInt(Randomizer.rand(0, 0x37));
                oPacket.EncodeLong(0);
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(0);

            oPacket.Fill(0, 69); //for no dc goodluck charm! >:D xD LOL

            return oPacket;
        }
    }

    public static class HayatoPacket {

        public static OutPacket SwordEnergy(int nAmount) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ModHayatoCombo.getValue());

            oPacket.EncodeInt(nAmount);

            return oPacket;
        }

        /*public static OutPacket QuickDraw(int nStance) {
            
            
            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.HayatoStance);
            
            oPacket.EncodeInt(nStance); // nStance
            
            oPacket.Fill(0, 28); // For no d/c memes.
            
            return oPacket;
        }*/
    }

    public static class ShadowerPacket {

        public static OutPacket toggleFlipTheCoin(boolean bEnabled) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserFlipTheCoinEnabled.getValue());
            oPacket.EncodeByte(bEnabled ? 1 : 0);

            return oPacket;
        }

        public static OutPacket setKillingPoint(int nAmount) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.KillingPoint);

            oPacket.EncodeByte(nAmount); // Doesn't work.

            return oPacket;
        }
    }

    public static class ShadePacket {

        public static OutPacket FoxSpirit(User pPlayer, AttackMonster oMonster) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());

            oPacket.EncodeByte(0); // bByMob
            oPacket.EncodeInt(pPlayer.getId()); // nCharId
            oPacket.EncodeInt(13); // nAtomType // 17 = Orbital Flame
            oPacket.EncodeByte(1); // bToMob
            oPacket.EncodeInt(1); // Unkown
            oPacket.EncodeInt(oMonster.getObjectId()); // nObjectId

            int nAtomId = Shade.FOX_SPIRITS;
            int nAtomCount = 2;
            if (pPlayer.hasSkill(Shade.FOX_SPIRIT_MASTERY)) {
                nAtomId = Shade.FOX_SPIRIT_MASTERY;
                nAtomCount = 3;
            }
            oPacket.EncodeInt(nAtomId); // nAtomId

            for (int i = 0; i < nAtomCount; i++) { // nAttackCount
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(i + 2); //dwKey
                oPacket.EncodeInt(0); // nInc
                oPacket.EncodeInt(0x23); // nFirstImpact
                oPacket.EncodeInt(5 + i); // nSecondImpact
                oPacket.EncodeInt(Randomizer.rand(80, 100)); // nAngle
                oPacket.EncodeInt(Randomizer.rand(200, 300)); // nStartDelay
                oPacket.EncodeLong(0); // nCharPositionX
                oPacket.EncodeInt(Randomizer.nextInt()); // nCharPositionY
                oPacket.EncodeInt(0); // dwCreateTime
            }

            oPacket.EncodeByte(0); // Unkown
            oPacket.Fill(0, 69); // For no d/c memes.

            return oPacket;
        }

        /*public static OutPacket FoxSpirit2(int cid, int skillid, int ga, int oid, int gu) {
            
            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(13);
            oPacket.Encode(1);
            oPacket.EncodeInt(gu);
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(skillid);
            for (int i = 1; i < ga; i++) {
                oPacket.Encode(1);
                oPacket.EncodeInt(2 + i);
                oPacket.EncodeInt(1);
                oPacket.EncodeInt(Randomizer.rand(0x2A, 0x2F));
                oPacket.EncodeInt(7 + i);
                oPacket.EncodeInt(Randomizer.rand(5, 0xAB));
                oPacket.EncodeInt(Randomizer.rand(0, 0x37));
                oPacket.EncodeLong(0);
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }
            oPacket.Encode(0);

            oPacket.Fill(0, 69);

            return oPacket;
        }*/
    }

    public static class BeastTamerPacket {

        public static OutPacket AnimalMode(int nSkillID) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.AnimalChange);

            oPacket.EncodeShort(nSkillID - 110001500); // nMode
            oPacket.EncodeInt(nSkillID); // nSkillID
            oPacket.EncodeInt(-419268850); // Unkown
            oPacket.EncodeLong(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);

            oPacket.Fill(0, 69); // For no d/c memes.

            return oPacket;
        }
    }

    public static class NightWalkerPacket {

        public static OutPacket ShadowBats(int nCharId, int nObjectId) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(nCharId);
            oPacket.EncodeInt(16); // nAtomType
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(nObjectId);
            oPacket.EncodeInt(14000028); // nAtomId

            for (int i = 0; i < 3; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(i + 2);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0x23);
                oPacket.EncodeInt(5);
                oPacket.EncodeInt(Randomizer.rand(80, 100));
                oPacket.EncodeInt(Randomizer.rand(200, 300));
                oPacket.EncodeLong(0); //v196
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }

            oPacket.EncodeByte(0);
            oPacket.Fill(0, 69); // For no d/c memes.

            return oPacket;
        }
    }

    public static class AngelicBusterPacket {

        public static OutPacket Starshooter(int cid, int skillid, int ga, int oid, int gu) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(18);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(gu);
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(skillid);
            for (int i = 1; i < ga; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(2 + i);
                oPacket.EncodeInt(1);
                oPacket.EncodeInt(Randomizer.rand(0x2A, 0x2F));
                oPacket.EncodeInt(7 + i);
                oPacket.EncodeInt(Randomizer.rand(5, 0xAB));
                oPacket.EncodeInt(Randomizer.rand(0, 0x37));
                oPacket.EncodeLong(0);
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(0);

            oPacket.Fill(0, 69); //for no dc goodluck charm! >:D xD LOL

            return oPacket;
        }
    }

    public static class KaiserPacket {

        public static OutPacket sendKaiserSkillShortcut(int[] skills) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.KaiserSkillShortcut.getValue());
            for (int i = 0; i < 3; i++) {
                if (skills[i] != 0) {
                    oPacket.EncodeBool(true);
                    oPacket.EncodeByte(i);
                    oPacket.EncodeInt(skills[i]);
                    int x = 0;
                    oPacket.EncodeByte(x);
                    if (x != 0) {
                        oPacket.EncodeByte(0);
                        oPacket.EncodeInt(0);
                    }
                }
            }
            return oPacket;
        }
    }

    public static class PhantomPacket {

        public static OutPacket ThrowCarte(User pPlayer, int nObjectId) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(pPlayer.getId());
            oPacket.EncodeInt(1); // nAtomType
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(nObjectId);
            oPacket.EncodeInt(pPlayer.hasSkill(Phantom.CARTE_NOIR) ? Phantom.CARTE_NOIR : Phantom.CARTE_BLANCHE); // nAtomId

            for (int i = 0; i < 3; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(i + 2);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0x23);
                oPacket.EncodeInt(5);
                oPacket.EncodeInt(Randomizer.rand(80, 100));
                oPacket.EncodeInt(Randomizer.rand(200, 300));
                oPacket.EncodeLong(0); //v196
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }

            oPacket.EncodeByte(0);
            oPacket.Fill(0, 69); // For no d/c memes.

            return oPacket;
        }

        public static OutPacket addStolenSkill(int jobNum, int index, int skill, int level) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ChangeStealMemoryResult.getValue());
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(jobNum);
            oPacket.EncodeInt(index);
            oPacket.EncodeInt(skill);
            oPacket.EncodeInt(level);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);

            return oPacket;
        }

        public static OutPacket removeStolenSkill(int jobNum, int index) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ChangeStealMemoryResult.getValue());
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(3);
            oPacket.EncodeInt(jobNum);
            oPacket.EncodeInt(index);
            oPacket.EncodeByte(0);

            return oPacket;
        }

        public static OutPacket replaceStolenSkill(int base, int skill) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ChangeStealMemoryResult.getValue());
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(skill > 0 ? 1 : 0);
            oPacket.EncodeInt(base);
            oPacket.EncodeInt(skill);

            return oPacket;
        }

        public static OutPacket gainCardStack(int oid, int runningId, int color, int skillid, int damage, int times) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(damage);
            oPacket.EncodeInt(skillid);
            for (int i = 0; i < times; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(damage == 0 ? runningId + i : runningId);
                oPacket.EncodeInt(color);
                oPacket.EncodeInt(Randomizer.rand(15, 29));
                oPacket.EncodeInt(Randomizer.rand(7, 11));
                oPacket.EncodeInt(Randomizer.rand(0, 9));
            }
            oPacket.EncodeByte(0);

            oPacket.Fill(0, 69); //for no DC it requires this do not remove

            return oPacket;
        }

        public static OutPacket updateCardStack(final int total) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.IncJudgementStack.getValue());
            oPacket.EncodeByte(total);

            return oPacket;
        }

        public static OutPacket getCarteAnimation(int cid, int oid, int job, int total, int numDisplay) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(1);

            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(job == 2412 ? 24120002 : 24100003);
            oPacket.EncodeByte(1);
            for (int i = 1; i <= numDisplay; i++) {
                oPacket.EncodeInt(total - (numDisplay - i));
                oPacket.EncodeInt(job == 2412 ? 2 : 0);

                oPacket.EncodeInt(15 + Randomizer.nextInt(15));
                oPacket.EncodeInt(7 + Randomizer.nextInt(5));
                oPacket.EncodeInt(Randomizer.nextInt(4));

                oPacket.EncodeByte(i == numDisplay ? 0 : 1);
            }

            return oPacket;
        }
    }

    public static class AngelicPacket {

        public static OutPacket showRechargeEffect() {
            return CField.EffectPacket.OnUserEffect(0, UserEffectCodes.ResetOnStateForOnOffSkill, 0, "", 0, 0, false, 0, 0, 0, (byte) 0, (byte) 0, 0, 0, 0, false, null);
        }

        public static OutPacket DressUpTime(byte type) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
            oPacket.EncodeByte(type);
            oPacket.EncodeShort(7707);
            oPacket.EncodeByte(2);
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
            return oPacket;
        }

        public static OutPacket updateDress(int transform, User chr) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetDressUpState.getValue());
            oPacket.EncodeInt(chr.getId());
            oPacket.EncodeInt(transform);
            return oPacket;
        }

        public static OutPacket lockSkill(int skillid) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SetOffStateForOnOffSkill.getValue());
            oPacket.EncodeInt(skillid);
            return oPacket;
        }

        public static OutPacket unlockSkill() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ResetOnStateForOnOffSkill.getValue());
            oPacket.EncodeInt(0);
            return oPacket;
        }

        public static OutPacket absorbingSoulSeeker(int characterid, int size, Point essence1, Point essence2, int skillid, boolean creation) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(!creation ? 0 : 1);
            oPacket.EncodeInt(characterid);
            if (!creation) {
                // false
                oPacket.EncodeInt(3);
                oPacket.EncodeByte(1);
                oPacket.EncodeByte(size);
                oPacket.Fill(0, 3);
                oPacket.EncodeShort(essence1.x);
                oPacket.EncodeShort(essence1.y);
                oPacket.EncodeShort(essence2.y);
                oPacket.EncodeShort(essence2.x);
            } else {
                // true
                oPacket.EncodeShort(essence1.x);
                oPacket.EncodeShort(essence1.y);
                oPacket.EncodeInt(4);
                oPacket.EncodeByte(1);
                oPacket.EncodeShort(essence1.y);
                oPacket.EncodeShort(essence1.x);
            }
            oPacket.EncodeInt(skillid);
            if (!creation) {
                for (int i = 0; i < 2; i++) {
                    oPacket.EncodeByte(1);
                    oPacket.EncodeInt(Randomizer.rand(19, 20));
                    oPacket.EncodeInt(1);
                    oPacket.EncodeInt(Randomizer.rand(18, 19));
                    oPacket.EncodeInt(Randomizer.rand(20, 23));
                    oPacket.EncodeInt(Randomizer.rand(36, 55));
                    oPacket.EncodeInt(540);
                    oPacket.EncodeShort(0);//new 142
                    oPacket.Fill(0, 6);//new 143
                }
            } else {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(Randomizer.rand(6, 21));
                oPacket.EncodeInt(1);
                oPacket.EncodeInt(Randomizer.rand(42, 45));
                oPacket.EncodeInt(Randomizer.rand(4, 7));
                oPacket.EncodeInt(Randomizer.rand(267, 100));
                oPacket.EncodeInt(0);//540
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(0);
            return oPacket;
        }

        public static OutPacket SoulSeekerRegen(User chr, int sn) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(chr.getId());
            oPacket.EncodeInt(sn);
            oPacket.EncodeInt(4);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(sn);
            oPacket.EncodeInt(65111007); // hide skills
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(Randomizer.rand(0x06, 0x10));
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(Randomizer.rand(0x28, 0x2B));
            oPacket.EncodeInt(Randomizer.rand(0x03, 0x04));
            oPacket.EncodeInt(Randomizer.rand(0xFA, 0x49));
            oPacket.EncodeInt(0);
            oPacket.EncodeLong(0);
            oPacket.EncodeByte(0);
            return oPacket;
        }

        public static OutPacket SoulSeeker(User chr, int skillid, int sn, int sc1, int sc2) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(chr.getId());
            oPacket.EncodeInt(3);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(sn);
            if (sn >= 1) {
                oPacket.EncodeInt(sc1);//SHOW_ITEM_GAIN_INCHAT
                if (sn == 2) {
                    oPacket.EncodeInt(sc2);
                }
            }
            oPacket.EncodeInt(65111007); // hide skills
            for (int i = 0; i < 2; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(i + 2);
                oPacket.EncodeInt(1);
                oPacket.EncodeInt(Randomizer.rand(0x0F, 0x10));
                oPacket.EncodeInt(Randomizer.rand(0x1B, 0x22));
                oPacket.EncodeInt(Randomizer.rand(0x1F, 0x24));
                oPacket.EncodeInt(540);
                oPacket.EncodeInt(0);//wasshort new143
                oPacket.EncodeInt(0);//new143
            }
            oPacket.EncodeByte(0);
            return oPacket;
        }
    }

    public static class LuminousPacket {

        public static OutPacket setLarknessResult(int nSkillID, int nLightGauge, int nDarkGauge, int tDuration) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.Larkness);

            oPacket.EncodeShort(1);
            oPacket.EncodeInt(nSkillID); // 20040217
            oPacket.EncodeInt(tDuration);
            oPacket.Fill(0, 5);
            oPacket.EncodeInt(nSkillID); // 20040217
            oPacket.EncodeInt(483195070);
            oPacket.Fill(0, 8);
            oPacket.EncodeInt(Math.max(nLightGauge, -1)); //light gauge
            oPacket.EncodeInt(Math.max(nDarkGauge, -1)); //dark gauge
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0); // Was 2
            oPacket.EncodeInt(283183599);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);// New v143
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);

            oPacket.Fill(0, 69); // Anti-DC Memes

            return oPacket;
        }

        public static OutPacket updateLuminousGauge(int darktotal, int lighttotal, int darktype, int lighttype) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ChangeLarknessStack.getValue());
            oPacket.EncodeInt(darktotal);
            oPacket.EncodeInt(lighttotal);
            oPacket.EncodeInt(darktype);
            oPacket.EncodeInt(lighttype);
            oPacket.EncodeInt(0);//1210382225 //281874974

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }

        public static OutPacket giveLuminousState(int skill, int light, int dark, int duration) {
            /*final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
            stat.put(CharacterTemporaryStat.Larkness, 1);
            int newLightGauge = Math.max(light, -1);
            int newDarkGauge = Math.max(dark, -1);
            return BuffPacket.giveBuff(null, skill, duration, stat, null, 0, 0, newLightGauge, newDarkGauge);*/
            return setLarknessResult(skill, light, dark, duration);
        }

        public static OutPacket giveLifeTidal(boolean isHpBetter, int value) {
            final EnumMap<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.LifeTidal, isHpBetter ? 2 : 1);
            return BuffPacket.giveBuff(null, 27110007, 2100000000, statups, null, value, 0, 0, 0);
        }
    }

    public static class KinesisPacket {

        public static OutPacket updatePsychicPoint(int nAmount) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.KinesisPsychicPoint);

            oPacket.EncodeInt(nAmount);
            oPacket.Fill(0, 69);

            return oPacket;
        }

        public static OutPacket Orbs(int cid, int skillid, int ga, int oid, int gu) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(22);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(gu);
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(skillid);
            for (int i = 1; i < ga; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(2 + i);
                oPacket.EncodeInt(1);
                oPacket.EncodeInt(Randomizer.rand(0x2A, 0x2F));
                oPacket.EncodeInt(7 + i);
                oPacket.EncodeInt(Randomizer.rand(5, 0xAB));
                oPacket.EncodeInt(Randomizer.rand(0, 0x37));
                oPacket.EncodeLong(0);
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(0);

            oPacket.Fill(0, 69); //for no dc goodluck charm! >:D xD LOL

            return oPacket;
        }
    }

    public static class BlasterPacket {

        public static OutPacket onRWMultiChargeCancelRequest(byte nUnkown, int nSkillID) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SkillUseResult.getValue());
            oPacket.EncodeByte(nUnkown);
            oPacket.EncodeInt(nSkillID);

            return oPacket;
        }
    }

    public static class XenonPacket {

        public static OutPacket giveXenonSupply(short amount) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.SurplusSupply);

            oPacket.EncodeShort(amount);
            oPacket.EncodeInt(30020232); //skill id
            oPacket.EncodeInt(-1); //duration
            oPacket.Fill(0, 18);

            return oPacket;
        }

        public static OutPacket giveAmaranthGenerator() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.SurplusSupply, 0);
            statups.put(CharacterTemporaryStat.AmaranthGenerator, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(20); //gauge fill
            oPacket.EncodeInt(30020232); //skill id
            oPacket.EncodeInt(-1); //duration

            oPacket.EncodeShort(1);
            oPacket.EncodeInt(36121054); //skill id
            oPacket.EncodeInt(10000); //duration

            oPacket.Fill(0, 5);
            oPacket.EncodeInt(1000);
            oPacket.EncodeInt(1);
            oPacket.Fill(0, 1);

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }

        public static OutPacket PinPointRocket(int cid, List<Integer> moblist) {
            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(6);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(moblist.size());
            for (int i = 0; i < moblist.size(); i++) {
                oPacket.EncodeInt(moblist.get(i));
            }
            oPacket.EncodeInt(36001005);
            for (int i = 1; i <= moblist.size(); i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(i + 7);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(Randomizer.rand(10, 20));
                oPacket.EncodeInt(Randomizer.rand(20, 40));
                oPacket.EncodeInt(Randomizer.rand(40, 200));
                oPacket.EncodeInt(Randomizer.rand(500, 2000));
                oPacket.EncodeLong(0); //v196
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(0);

            oPacket.Fill(0, 69); //for no dc
            return oPacket;
        }

        public static OutPacket MegidoFlameRe(int cid, int oid) {
            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(3);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(2121055);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(2);
            oPacket.EncodeInt(2);
            oPacket.EncodeInt(Randomizer.rand(10, 17));
            oPacket.EncodeInt(Randomizer.rand(10, 16));
            oPacket.EncodeInt(Randomizer.rand(40, 52));
            oPacket.EncodeInt(20);
            oPacket.EncodeLong(0);
            oPacket.EncodeLong(0);
            oPacket.EncodeByte(0);
            oPacket.Fill(0, 69); //for no dc
            return oPacket;
        }

        public static OutPacket ShieldChacingRe(int cid, int unkwoun, int oid, int unkwoun2, int unkwoun3) {
            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(unkwoun);
            oPacket.EncodeInt(4);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(31221014);

            oPacket.EncodeByte(1);
            oPacket.EncodeInt(unkwoun2 + 1);
            oPacket.EncodeInt(3);
            oPacket.EncodeInt(unkwoun3);
            oPacket.EncodeInt(3);
            oPacket.EncodeInt(Randomizer.rand(36, 205));
            oPacket.EncodeInt(0);
            oPacket.EncodeLong(0);
            oPacket.EncodeInt(Randomizer.nextInt());
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);
            oPacket.Fill(0, 69); //for no dc
            return oPacket;
        }

        public static OutPacket ShieldChacing(int cid, List<Integer> moblist, int skillid) {
            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(3);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(moblist.size());
            for (int i = 0; i < moblist.size(); i++) {
                oPacket.EncodeInt(((Integer) moblist.get(i)).intValue());
            }
            oPacket.EncodeInt(skillid);
            for (int i = 1; i <= moblist.size(); i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(1 + i);
                oPacket.EncodeInt(3);
                oPacket.EncodeInt(Randomizer.rand(1, 20));
                oPacket.EncodeInt(Randomizer.rand(20, 50));
                oPacket.EncodeInt(Randomizer.rand(50, 200));
                oPacket.EncodeInt(skillid == 2121055 ? 720 : 660);
                oPacket.EncodeLong(0);
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(0);
            oPacket.Fill(0, 69); //for no dc
            return oPacket;
        }

        public static OutPacket EazisSystem(int cid, int oid) {
            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(5);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(36110004);
            for (int i = 0; i < 3; i++) {
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(i + 2);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0x23);
                oPacket.EncodeInt(5);
                oPacket.EncodeInt(Randomizer.rand(80, 100));
                oPacket.EncodeInt(Randomizer.rand(200, 300));
                oPacket.EncodeLong(0); //v196
                oPacket.EncodeInt(Randomizer.nextInt());
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(0);
            oPacket.Fill(0, 69); //for no dc
            return oPacket;
        }
    }

    public static class AvengerPacket {

        public static OutPacket giveAvengerHpBuff(int hp) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());

            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.LifeTidal); //for now
            oPacket.EncodeShort(3);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(2100000000);
            oPacket.Fill(0, 5);
            oPacket.EncodeInt(hp);
            oPacket.Fill(0, 9);

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }

        public static OutPacket giveExceed(short amount) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.OverloadCount);

            oPacket.EncodeShort(amount);
            oPacket.EncodeInt(30010230); //skill id
            oPacket.EncodeInt(0); //duration (-1)
            oPacket.Fill(0, 14);

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }

        public static OutPacket giveExceedAttack(int skill, short amount) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.ExceedOverload);

            oPacket.EncodeShort(amount);
            oPacket.EncodeInt(skill); //skill id
            oPacket.EncodeInt(15000); //duration
            oPacket.Fill(0, 18);

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }

        public static OutPacket cancelExceed() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatReset.getValue());

            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.OverloadCount, 0);
            statups.put(CharacterTemporaryStat.ExceedOverload, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            return oPacket;
        }
    }

    public static class DawnWarriorPacket {

        public static OutPacket giveMoonfallStance(int level) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            //statups.put(CharacterTemporaryStat.CRITICAL_PERCENT_UP, 0);
            //statups.put(CharacterTemporaryStat.MOON_Stance2, 0);
            //statups.put(CharacterTemporaryStat.WARRIOR_Stance, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(level);
            oPacket.EncodeInt(11101022);
            oPacket.EncodeInt(Integer.MAX_VALUE);
            oPacket.EncodeShort(1);
            oPacket.EncodeInt(11101022);
            oPacket.EncodeInt(Integer.MAX_VALUE);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(5);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(11101022);
            oPacket.EncodeInt(level);
            oPacket.EncodeInt(Integer.MAX_VALUE);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }

        public static OutPacket giveSunriseStance(int level) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.Booster, 0);
            statups.put(CharacterTemporaryStat.IndieDamR, 0);
            statups.put(CharacterTemporaryStat.Stance, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(level);
            oPacket.EncodeInt(11111022);
            oPacket.EncodeInt(Integer.MAX_VALUE);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(5);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(11111022);
            oPacket.EncodeInt(-1);
            oPacket.EncodeInt(Integer.MAX_VALUE);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(11111022);
            oPacket.EncodeInt(0x19);
            oPacket.EncodeInt(Integer.MAX_VALUE);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }

        public static OutPacket giveEquinox_Moon(int level, int duration) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            //statups.put(CharacterTemporaryStat.CRITICAL_PERCENT_UP, 0);
            //statups.put(CharacterTemporaryStat.MOON_Stance2, 0);
            //statups.put(CharacterTemporaryStat.EQUINOX_Stance, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(level);
            oPacket.EncodeInt(11121005);
            oPacket.EncodeInt(duration);
            oPacket.EncodeShort(1);
            oPacket.EncodeInt(11121005);
            oPacket.EncodeInt(duration);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(5);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(11121005);
            oPacket.EncodeInt(level);
            oPacket.EncodeInt(duration);
            oPacket.EncodeInt(duration);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }

        public static OutPacket giveEquinox_Sun(int level, int duration) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.Booster, 0);
            statups.put(CharacterTemporaryStat.IndieDamR, 0);
            //statups.put(CharacterTemporaryStat.EQUINOX_Stance, 0);
            PacketHelper.writeBuffMask(oPacket, statups);

            oPacket.EncodeShort(level);
            oPacket.EncodeInt(11121005);
            oPacket.EncodeInt(duration);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(5);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(11121005);
            oPacket.EncodeInt(-1);
            oPacket.EncodeInt(duration);
            oPacket.EncodeInt(duration);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(11121005);
            oPacket.EncodeInt(0x19);
            oPacket.EncodeInt(duration);
            oPacket.EncodeInt(duration);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);

            oPacket.Fill(0, 69); //for no dc

            return oPacket;
        }
    }

    public static class Kinesis {

        public static OutPacket OnCreatePsychicArea(int dwCharacterId, int nAction, int nActionSpeed, int nParentPsychicAreaKey, int nSkillID, short nSLV, int nPsychicAreaKey, int nDurationTime, byte isLeft, short nSekeletonFilePathIdx, short nSkeletonAniIdx, short nSkeletonLoop, Point posStart) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserCreatePsychicArea.getValue());

            oPacket.EncodeInt(dwCharacterId);
            oPacket.EncodeByte(1); // bData
            oPacket.EncodeInt(nAction);
            oPacket.EncodeInt(nActionSpeed);
            oPacket.EncodeInt(nParentPsychicAreaKey);
            oPacket.EncodeInt(nSkillID);
            oPacket.EncodeShort(nSLV);
            oPacket.EncodeInt(nPsychicAreaKey);
            oPacket.EncodeInt(nDurationTime);
            oPacket.EncodeByte(isLeft);
            oPacket.EncodeShort(nSekeletonFilePathIdx);
            oPacket.EncodeShort(nSkeletonAniIdx);
            oPacket.EncodeShort(nSkeletonLoop);
            oPacket.EncodeInt(posStart.x);
            oPacket.EncodeInt(posStart.y);

            return oPacket;
        }

        public static OutPacket OnDoActivePsychicArea(int nKey, int unk2) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.DoActivePsychicArea.getValue());

            oPacket.EncodeInt(nKey);
            oPacket.EncodeInt(unk2);

            return oPacket;
        }

        public static OutPacket OnReleasePsychicArea(int dwCharacterId, int nPsychicAreaKey) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserReleasePsychicArea.getValue());

            oPacket.EncodeInt(dwCharacterId);
            oPacket.EncodeInt(nPsychicAreaKey);

            return oPacket;
        }

        public static OutPacket OnCreatePsychicLock(int dwCharacterId, int nSkillID, short nSLV, int nAction, int nActionSpeed, List<KinesisPsychicLock> PsychicLock) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserCreatePsychicLock.getValue());

            oPacket.EncodeInt(dwCharacterId);
            oPacket.EncodeByte(1); // bData
            oPacket.EncodeInt(nSkillID);
            oPacket.EncodeShort(nSLV);
            oPacket.EncodeInt(nAction);
            oPacket.EncodeInt(nActionSpeed);

            for (KinesisPsychicLock pLock : PsychicLock) {
                oPacket.EncodeByte(1); // bData2
                oPacket.EncodeByte(1); // bPsychicLockSuccess
                oPacket.EncodeInt(pLock.getLocalPsychicLockKey());
                oPacket.EncodeInt(pLock.getLocalPsychicLockKey() * -1);
                oPacket.EncodeInt(pLock.getMobID());
                oPacket.EncodeShort(pLock.getStuffID());
                oPacket.EncodeInt((int) pLock.getMobMaxHP());
                oPacket.EncodeInt((int) pLock.getMobCurHP());
                oPacket.EncodeByte(pLock.getRelPosFirst());
                oPacket.EncodeInt(pLock.getStart().x);
                oPacket.EncodeInt(pLock.getStart().y);
                oPacket.EncodeInt(pLock.getRelPosSecond().x);
                oPacket.EncodeInt(pLock.getRelPosSecond().y);
            }

            oPacket.Fill(0, 10);
            return oPacket;
        }

        public static OutPacket OnReleasePsychicLock(int dwCharacterId, int nParentPsychicAreaKey) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserReleasePsychicLock.getValue());

            oPacket.EncodeInt(dwCharacterId);
            oPacket.EncodeInt(nParentPsychicAreaKey);

            return oPacket;
        }
    }
}
