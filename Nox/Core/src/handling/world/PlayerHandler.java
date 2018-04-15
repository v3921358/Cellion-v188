package handling.world;

import java.awt.Point;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import client.MapleClient;
import client.MapleQuestStatus.MapleQuestState;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.Mob;
import server.maps.FieldLimitType;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.quest.MapleQuest;
import net.InPacket;
import server.MapleStatEffect;
import server.maps.objects.MapleRuneStone;
import tools.packet.CField;
import tools.packet.CSPacket;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;
import tools.packet.JobPacket.KaiserPacket;

public class PlayerHandler {

    public static int isFinisher(int skillid) {
        switch (skillid) {
            case 1111003:
                return 1;
            case 1111005:
                return 2;
            case 11111002:
                return 1;
            case 11111003:
                return 2;
        }
        return 0;
    }

    public static final void TouchRune(final InPacket iPacket, final User chr) {
        chr.updateTick(iPacket.DecodeInt());
        int type = iPacket.DecodeInt();
        List<MapleRuneStone> runes = chr.getMap().getAllRune();
        MapleRuneStone rune = chr.getMap().getAllRune().get(0);
        if (rune != null) {
            if (chr.getKeyValue("LastTouchedRune") != null && chr.getRuneTimeStamp() > System.currentTimeMillis()) {
                chr.getClient().SendPacket(CField.RunePacket.RuneAction(2, (int) (chr.getRuneTimeStamp() - System.currentTimeMillis())));
                chr.getClient().SendPacket(CWvsContext.enableActions());
                return;
            }
            chr.setTouchedRune(type);
            chr.getClient().SendPacket(CField.RunePacket.RuneAction(5, 0));
        }
        chr.getClient().SendPacket(CWvsContext.enableActions());
    }

    public static final void UseRune(final InPacket iPacket, final User chr) {
        final byte result = iPacket.DecodeByte();
        final MapleRuneStone rune = chr.getMap().getAllRune().get(0);
        MapleStatEffect effect;
        if (result == 1) {
            switch (chr.getTouchedRune()) {
                case 0: //疾速之輪
                    chr.gainItem(5211046, 1, "Rune Of Swiftness");
                    chr.yellowMessage("[Rune] 1 Hour 2x Exp Rune Activated: EXP Coupon Granted!");
                    break;
                case 1: //再生之輪
                    chr.gainItem(2000005, 75);
                    chr.yellowMessage("[Rune] Healing Rune Activated: 75 Power Exliris Granted!");
                    break;
                case 2: //崩壞之輪
                    chr.gainItem(4031864, 1, "Rune Of Decay");
                    chr.yellowMessage("[Rune] 1 Hour 2x NX Rune Activated: Allowance Granted!");
                    break;
                case 3: //破滅之輪
                    List<Integer> items;
                    Integer[] itemArray = {2043003, 2043102, 2043302, 2043402, 2043602,
                        2044002, 2044302, 2044402, 2044502, 2044602, 2044702,
                        2044802, 2044902, 2045302, 2046000, 2049150, 2049149, 2046001, 2046002,
                        2046003, 2046100, 2046101, 2046102, 2046103, 2043201, 2044001, 2041038,
                        2041039, 2041036, 2041037, 2041040, 2041041, 2041026, 2041027, 2044600,
                        2043301, 2040308, 2040309, 2040304, 2040305, 2040810, 2040811, 2040812,
                        2040813, 2040814, 2040815, 2040008, 2040009, 2040010, 2040011, 2040012,
                        2040013, 2040510, 2040511, 2040508, 2040509, 2040518, 2040519};
                    items = Arrays.asList(itemArray);

                    chr.gainItem(items.get(Randomizer.nextInt(items.size())), 1, "Rune Of Destruction");
                    chr.yellowMessage("[Rune] Scroll Rune Activated: Random Scroll Obtained!");
                    break;
            }
            chr.getMap().broadcastMessage(CField.RunePacket.removeRune(rune, chr));
            chr.getMap().broadcastMessage(CField.RunePacket.showRuneEffect(chr.getTouchedRune()));
            chr.getMap().removeMapObject(rune);
            if (chr.getReborns() == 0) {
                chr.setRuneTimeStamp(System.currentTimeMillis() + 14400000);
            }
        } else {
            chr.setRuneTimeStamp(System.currentTimeMillis() + 10000);
        }
    }

    public static void KaiserSkillShortcut(final InPacket iPacket, final User chr) {
        if (GameConstants.isKaiser(chr.getJob())) {
            int count = iPacket.DecodeByte() + 1;
            int tmp1 = 0;
            int[] skills = {0, 0, 0};
            for (int i = 0; i < count; i++) {
                tmp1 = iPacket.DecodeByte();
                if (tmp1 > 2) {
                    return;
                }
                skills[tmp1] = iPacket.DecodeInt();
                if (chr.getSkillLevel(skills[tmp1]) == 0) {
                    return;
                }
                chr.updateOneInfo(52554, "cmd" + tmp1, String.valueOf(skills[tmp1]));
            }
            chr.getClient().SendPacket(KaiserPacket.sendKaiserSkillShortcut(skills));
        }
    }

    public static void OrbitalFlame(final InPacket iPacket, final MapleClient c) {
        User chr = c.getPlayer();

        int tempskill = iPacket.DecodeInt();
        byte unk = iPacket.DecodeByte();
        int direction = iPacket.DecodeShort();
        int skillid = 0;
        int elementid = 0;
        int effect = 0;
        switch (tempskill) {
            case 12001020:
                skillid = 12000026;
                elementid = 12000022;
                effect = 1;
                break;
            case 12100020:
                skillid = 12100028;
                elementid = 12100026;
                effect = 2;
                break;
            case 12110020:
                skillid = 12110028;
                elementid = 12110024;
                effect = 3;
                break;
            case 12120006:
                skillid = 12120010;
                elementid = 12120007;
                effect = 4;
                break;
        }
        MapleStatEffect flame = SkillFactory.getSkill(tempskill).getEffect(chr.getSkillLevel(tempskill));
        /* if (flame != null && chr.getSkillLevel(elementid) > 0) { // This should be fruther looked into.
            if (!chr.getSummons().keySet().contains(elementid)) {
                MapleStatEffect element = SkillFactory.getSkill(elementid).getEffect(chr.getSkillLevel(elementid));
                MapleSummon summon = new MapleSummon(chr, element, chr.getPosition(), SummonMovementType.FOLLOW);
                chr.getSummons().put(elementid, summon);
                chr.getMap().spawnSummon(summon);
                element.applyTo(chr);
            }
        }*/
        chr.getMap().broadcastMessage(CField.OrbitalFlame(chr.getId(), skillid, effect, direction, flame.getRange()));
    }

    public static void absorbingDF(InPacket iPacket, final MapleClient c) {
        int size = iPacket.DecodeInt();
        int room = 0;
        byte unk = 0;
        int sn;
        int skillid = 0;
        if (GameConstants.isKaiser(c.getPlayer().getJob()) || GameConstants.isShade(c.getPlayer().getJob()) || GameConstants.isNightWalkerCygnus(c.getPlayer().getJob()) || GameConstants.isAngelicBuster(c.getPlayer().getJob())) {
            skillid = iPacket.DecodeInt();
        }
        for (int i = 0; i < size; i++) {
            room = GameConstants.isDemonAvenger(c.getPlayer().getJob()) || c.getPlayer().getJob() == 212 ? 0 : iPacket.DecodeInt();
            unk = iPacket.DecodeByte();
            sn = iPacket.DecodeInt();
            if (GameConstants.isDemonSlayer(c.getPlayer().getJob())) {
                //c.getPlayer().addMP(c.getPlayer().getStat().getForce(room));
            }
            if (iPacket.GetRemainder() > 0 && !GameConstants.isNightWalkerCygnus(c.getPlayer().getJob()) && !GameConstants.isAngelicBuster(c.getPlayer().getJob())) {
                unk = iPacket.DecodeByte();
                sn = iPacket.DecodeInt();
            }
            if (GameConstants.isAngelicBuster(c.getPlayer().getJob())) {
                boolean rand = Randomizer.isSuccess(80);
                if (sn > 0) {
                    if (rand) {
                        c.SendPacket(JobPacket.AngelicPacket.SoulSeekerRegen(c.getPlayer(), sn));
                    }
                }
            }
            if ((GameConstants.isDemonAvenger(c.getPlayer().getJob())) && iPacket.GetRemainder() >= 8) {
                //c.getPlayer().getMap().broadcastMessage(MainPacketCreator.ShieldChacingRe(c.getPlayer().getId(), iPacket.DecodeInt(), iPacket.DecodeInt(), unk, c.getPlayer().getKeyValue2("schacing")));
            }
            if (c.getPlayer().getJob() == 212) {
                //c.getPlayer().getMap().broadcastMessage(MainPacketCreator.MegidoFlameRe(c.getPlayer().getId(), unk, iPacket.DecodeInt()));
            }
        }
    }

    public static void LinkSkill(final InPacket iPacket, final MapleClient c, final User chr) {
        //iPacket: [76 7F 31 01] [35 00 00 00]
        c.getPlayer().dropMessage(1, "Beginning link skill.");
        int skill = iPacket.DecodeInt();
        int cid = iPacket.DecodeInt();
        boolean found = false;
        for (User chr2 : c.loadCharacters(c.getPlayer().getWorld())) {
            if (chr2.getId() == cid) {
                found = true;
            }
        }
        if (GameConstants.getLinkSkillByJob(chr.getJob()) != skill || !found || chr.getLevel() > 70) {
            c.getPlayer().dropMessage(1, "An error has occured.");
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        User.addLinkSkill(cid, skill);
    }

    public static void AdminCommand(InPacket iPacket, MapleClient c, User chr) {
        if (!c.getPlayer().isGM()) {
            return;
        }
        byte mode = iPacket.DecodeByte();
        String victim;
        User target;
        switch (mode) {
            case 0x00: // Level1~Level8 & Package1~Package2
                int[][] toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(iPacket.DecodeInt());
                for (int[] toSpawnChild : toSpawn) {
                    if (Randomizer.nextInt(101) <= toSpawnChild[1]) {
                        c.getPlayer().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(toSpawnChild[0]), c.getPlayer().getPosition());
                    }
                }
                c.SendPacket(CWvsContext.enableActions());
                break;
            case 0x01: { // /d (inv)
                byte type = iPacket.DecodeByte();
                MapleInventory in = c.getPlayer().getInventory(MapleInventoryType.getByType(type));
                for (byte i = 0; i < in.getSlotLimit(); i++) {
                    if (in.getItem(i) != null) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(type), i, in.getItem(i).getQuantity(), false);
                    }
                    return;
                }
                break;
            }
            case 0x02: // Exp
                c.getPlayer().setExp(iPacket.DecodeInt());
                break;
            case 0x03: // /ban <name>
                victim = iPacket.DecodeString();
                String reason = victim + " permanent banned by " + c.getPlayer().getName();
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);

                if (target != null) {
                    String readableTargetName = User.makeMapleReadable(target.getName());
                    String ip = target.getClient().GetIP().split(":")[0];
                    reason += readableTargetName + " (IP: " + ip + ")";
                    target.getClient().ban(reason, false, false);
                    target.sendPolice();

                    c.SendPacket(CField.getGMEffect(4, (byte) 0));
                } else if (MapleClient.banOfflineCharacter(reason, victim)) {
                    c.SendPacket(CField.getGMEffect(4, (byte) 0));
                } else {
                    c.SendPacket(CField.getGMEffect(6, (byte) 1));
                }
                break;
            case 0x04: // /block <name> <duration (in days)> <HACK/BOT/AD/HARASS/CURSE/SCAM/MISCONDUCT/SELL/ICASH/TEMP/GM/IPROGRAM/MEGAPHONE>
                victim = iPacket.DecodeString();
                int type = iPacket.DecodeByte(); //reason
                int duration = iPacket.DecodeInt();
                String description = iPacket.DecodeString();
                reason = c.getPlayer().getName() + " used /ban to ban";
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
                if (target != null) {
                    String readableTargetName = User.makeMapleReadable(target.getName());
                    String ip = target.getClient().GetIP().split(":")[0];
                    reason += readableTargetName + " (IP: " + ip + ")";
                    if (duration == -1) {
                        c.ban(description + " " + reason, false, false);
                    } else {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, duration);
                        target.tempban(description, cal, type, false);
                        target.sendPolice();
                    }
                    c.SendPacket(CField.getGMEffect(4, (byte) 0));
                } else if (MapleClient.banOfflineCharacter(reason, victim)) {
                    c.SendPacket(CField.getGMEffect(4, (byte) 0));
                } else {
                    c.SendPacket(CField.getGMEffect(6, (byte) 1));
                }
                break;
            case 0x10: // /h, information by vana (and tele mode f1) ... hide ofcourse
                if (iPacket.DecodeByte() > 0) {
                    SkillFactory.getSkill(9101004).getEffect(1).applyTo(c.getPlayer());
                } else {
                    c.getPlayer().dispelBuff(9101004);
                }
                break;
            case 0x11: // Entering a map
                switch (iPacket.DecodeByte()) {
                    case 0:// /u
                        StringBuilder sb = new StringBuilder("USERS ON THIS MAP: ");
                        for (User mc : c.getPlayer().getMap().getCharacters()) {
                            sb.append(mc.getName());
                            sb.append(" ");
                        }
                        c.getPlayer().dropMessage(5, sb.toString());
                        break;
                    case 12:// /uclip and entering a map
                        break;
                }
                break;
            case 0x12: // Send
                victim = iPacket.DecodeString();
                int mapId = iPacket.DecodeInt();
                c.getChannelServer().getPlayerStorage().getCharacterByName(victim).changeMap(c.getChannelServer().getMapFactory().getMap(mapId));
                break;
            case 0x15: // Kill
                int mobToKill = iPacket.DecodeInt();
                int amount = iPacket.DecodeInt();
                List<MapleMapObject> monsterx = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
                for (int x = 0; x < amount; x++) {
                    Mob monster = (Mob) monsterx.get(x);
                    if (monster.getId() == mobToKill) {
                        c.getPlayer().getMap().killMonster(monster, c.getPlayer(), false, false, (byte) 1);
                    }
                }
                break;
            case 0x16: // Questreset
                MapleQuest.getInstance(iPacket.DecodeShort()).forfeit(c.getPlayer());
                break;
            case 0x17: // Summon
                int mobId = iPacket.DecodeInt();
                int quantity = iPacket.DecodeInt();
                for (int i = 0; i < quantity; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(mobId), c.getPlayer().getPosition());
                }
                break;
            case 0x18: // Maple & Mobhp
                int mobHp = iPacket.DecodeInt();
                c.getPlayer().dropMessage(5, "Monsters HP");
                List<MapleMapObject> monsters = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
                for (MapleMapObject mobs : monsters) {
                    Mob monster = (Mob) mobs;
                    if (monster.getId() == mobHp) {
                        c.getPlayer().dropMessage(5, monster.getName() + ": " + monster.getHp());
                    }
                }
                break;
            case 0x1E: // Warn
                victim = iPacket.DecodeString();
                String message = iPacket.DecodeString();
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
                if (target != null) {
                    target.getClient().SendPacket(CWvsContext.broadcastMsg(1, message));
                    c.SendPacket(CField.getGMEffect(0x1E, (byte) 1));
                } else {
                    c.SendPacket(CField.getGMEffect(0x1E, (byte) 0));
                }
                break;
            case 0x24:// /Artifact Ranking
                break;
            case 0x77:
                break;
            default:
                System.out.println("New GM packet encountered (MODE : " + mode + ": " + iPacket.toString());
                break;
        }
    }

    public static void aranCombo(MapleClient c, User chr, int toAdd) {
        if ((chr != null) && (chr.getJob() >= 2000) && (chr.getJob() <= 2112)) {
            short combo = chr.getCombo();
            long curr = System.currentTimeMillis();

            if ((combo > 0) && (curr - chr.getLastComboTime() > 7000L)) {
                combo = 0;
            }
            combo = (short) Math.min(30000, combo + toAdd);
            chr.setLastCombo(curr);
            chr.setCombo(combo);

            c.SendPacket(CField.updateCombo(combo));

            switch (combo) {
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 70:
                case 80:
                case 90:
                case 100:
                    if (chr.getSkillLevel(21000000) < combo / 10) {
                        break;
                    }
                    SkillFactory.getSkill(21000000).getEffect(combo / 10).applyComboBuff(chr, combo);
                    break;
            }
        }
    }

    public static void ChangeHaku(InPacket iPacket, MapleClient c, User chr) {
        int oid = iPacket.DecodeInt();
        if (chr.getHaku() != null) {
            chr.getHaku().sendStats();
            // chr.getMap().broadcastMessage(chr, CField.spawnHaku_change0(chr.getId()), true);
            // chr.getMap().broadcastMessage(chr, CField.spawnHaku_change1(chr.getHaku()), true);
            // chr.getMap().broadcastMessage(chr, CField.transformHaku(chr.getId(), chr.getHaku().getStats()), true);
        }
    }

    public static void leftKnockBack(InPacket iPacket, MapleClient c) {
        if (c.getPlayer().getMapId() / 10000 == 10906) {
            c.SendPacket(CField.leftKnockBack());
            c.SendPacket(CWvsContext.enableActions());
        }
    }

    public static void MessengerRanking(InPacket iPacket, MapleClient c, User chr) {
        if (chr == null) {
            return;
        }
        c.SendPacket(CField.messengerOpen(iPacket.DecodeByte(), null));
    }
}
