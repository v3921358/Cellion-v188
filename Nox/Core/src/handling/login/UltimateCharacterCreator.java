package handling.login;

import client.MapleCharacterCreationUtil;
import java.util.HashMap;
import java.util.Map;

import client.MapleClient;
import client.QuestStatus.QuestState;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import server.quest.Quest;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

public final class UltimateCharacterCreator implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (!c.getPlayer().isGM()
                && (!c.isLoggedIn() || c.getPlayer() == null || c.getPlayer().getLevel() < 120 || c.getPlayer().getMapId() != 130000000
                || c.getPlayer().getQuestStatus(20734) != QuestState.NotStarted || c.getPlayer().getQuestStatus(20616) != QuestState.Completed || !GameConstants.isCygnusKnight(c.getPlayer().getJob()) || !MapleCharacterCreationUtil.canMakeCharacter(c.getWorld(), c.getAccID()))) {
            c.SendPacket(CField.createUltimate(2));
            //Character slots are full. Please purchase another slot from the Cash Shop.
            return;
        }
        //System.out.println(iPacket.toString());
        final String name = iPacket.DecodeString();
        final int job = iPacket.DecodeInt(); //job ID

        final int face = iPacket.DecodeInt();
        final int hair = iPacket.DecodeInt();

        //No idea what are these used for:
        final int hat = iPacket.DecodeInt();
        final int top = iPacket.DecodeInt();
        final int glove = iPacket.DecodeInt();
        final int shoes = iPacket.DecodeInt();
        final int weapon = iPacket.DecodeInt();

        final byte gender = c.getPlayer().getGender();

        //JobType errorCheck = JobType.Adventurer;
        //if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, errorCheck.type, face)) {
        //    c.write(CWvsContext.enableActions());
        //    return;
        //}
        LoginInformationProvider.JobType jobType = LoginInformationProvider.JobType.UltimateAdventurer;

        User newchar = User.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld((byte) c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); //troll
        newchar.setLevel((short) 50);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        int charPosition = c.loadCharacters(c.getWorld()).size();
        newchar.setCharListPosition(charPosition);
        newchar.setRemainingAp((short) 254); //49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); //2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; //Beginner 10 levels
        newchar.getStat().maxmp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; //Job Advancement
                newchar.getStat().maxhp += 2000; //Levelup 40 times
                newchar.getStat().maxmp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; //Levelup 40 times
                newchar.getStat().maxmp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; //Levelup 40 times
                newchar.getStat().maxmp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; //Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; //Levelup 20 times
                newchar.getStat().maxmp += 400;
                break;
            default:
                return;
        }
        //TODO: Make this GMS - Like
        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(Quest.getInstance(i), QuestState.Completed, null);
        }
        newchar.setQuestAdd(Quest.getInstance(29947), QuestState.Completed, null);
        newchar.setQuestAdd(Quest.getInstance(GameConstants.ULT_EXPLORER), QuestState.NotStarted, c.getPlayer().getName());

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1));
        newchar.changeSkillLevelSkip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        //TODO: Make this GMS - Like
        int[] items = new int[]{1142257, hat, top, shoes, glove, weapon, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1}; //brilliant = fine+1
        for (byte i = 0; i < items.length; i++) {
            Item item = li.getEquipById(items[i]);
            item.setPosition((byte) (i + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }

        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 200, (byte) 0));
        if (MapleCharacterCreationUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm())) {
            User.saveNewCharToDB(newchar, jobType, (short) 0);
            Quest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.SendPacket(CField.createUltimate(0));
        } else if (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) {
            c.SendPacket(CField.createUltimate(3)); //"You cannot use this name."
        } else {
            c.SendPacket(CField.createUltimate(1));
        }
    }

}
