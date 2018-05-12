package handling.world;

import java.util.List;

import client.ClientSocket;
import client.MapleDisease;
import java.util.Map;
import server.MapleCarnivalFactory;
import server.MapleCarnivalFactory.MCSkill;
import server.Randomizer;
import server.life.LifeFactory;
import server.life.Mob;
import server.life.MobSkill;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import tools.packet.MonsterCarnivalPacket;

public class MonsterCarnivalHandler {

    public static final void MonsterCarnival(final InPacket iPacket, final ClientSocket c) {
        if (c.getPlayer().getCarnivalParty() == null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final int tab = iPacket.DecodeByte();
        final int num = iPacket.DecodeInt();

        switch (tab) {
            case 0:
                List<Integer> mobs = c.getPlayer().getMap().getSharedMapResources().mcarnival.mobs;
                List<Integer> reqCPs = c.getPlayer().getMap().getSharedMapResources().mcarnival.MobCP;

                if (num >= mobs.size() || c.getPlayer().getAvailableCP() < reqCPs.get(num)) {
                    c.getPlayer().dropMessage(5, "You do not have the CP.");
                    c.SendPacket(WvsContext.enableActions());
                    return;
                }
                final Mob mons = LifeFactory.getMonster(mobs.get(num));
                if (mons != null && c.getPlayer().getMap().makeCarnivalSpawn(c.getPlayer().getCarnivalParty().getTeam(), mons, num)) {
                    c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), mobs.get(num));
                    c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                    for (User chr : c.getPlayer().getMap().getCharacters()) {
                        chr.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                    }
                    c.getPlayer().getMap().broadcastPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, num));
                    c.SendPacket(WvsContext.enableActions());
                } else {
                    c.getPlayer().dropMessage(5, "You may no longer summon the monster.");
                    c.SendPacket(WvsContext.enableActions());
                }
                break;
            case 1: {
                //debuff
                final Map<Integer, Integer> skillid = c.getPlayer().getMap().getSharedMapResources().mcarnival.Skills;

                if (num >= skillid.size() || !skillid.containsKey(num)) {
                    c.getPlayer().dropMessage(5, "An error occurred.");
                    c.SendPacket(WvsContext.enableActions());
                    return;
                }

                final MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(skillid.get(num)); //ugh wtf
                if (skil == null || c.getPlayer().getAvailableCP() < skil.cpLoss) {
                    c.getPlayer().dropMessage(5, "You do not have the CP.");
                    c.SendPacket(WvsContext.enableActions());
                    return;
                }
                final MapleDisease dis = skil.getDisease();
                boolean found = false;
                for (User chr : c.getPlayer().getMap().getCharacters()) {
                    if (chr.getParty() == null || (c.getPlayer().getParty() != null && chr.getParty().getId() != c.getPlayer().getParty().getId())) {
                        if (skil.targetsAll || Randomizer.nextBoolean()) {
                            found = true;
                            if (dis == null) {
                                chr.dispel();
                            } else if (skil.getSkill() == null) {
                                MobSkill skill = new MobSkill(dis.getDisease(), 1);
                                skill.setX(1);
                                skill.setDuration(30000);
                                chr.giveDebuff(dis, skill);
                            } else {
                                chr.giveDebuff(dis, skil.getSkill());
                            }
                            if (!skil.targetsAll) {
                                break;
                            }
                        }
                    }
                }
                if (found) {
                    c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), skil.cpLoss);
                    c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                    for (User chr : c.getPlayer().getMap().getCharacters()) {
                        chr.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                        //chr.dropMessage(5, "[" + (c.getPlayer().getCarnivalParty().getTeam() == 0 ? "Red" : "Blue") + "] " + c.getPlayer().getName() + " has used a skill. [" + dis.name() + "].");
                    }
                    c.getPlayer().getMap().broadcastPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, num));
                    c.SendPacket(WvsContext.enableActions());
                } else {
                    c.getPlayer().dropMessage(5, "An error occurred.");
                    c.SendPacket(WvsContext.enableActions());
                }
                break;
            }
            case 2: {
                //skill
                final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
                if (skil == null || c.getPlayer().getAvailableCP() < skil.cpLoss) {
                    c.getPlayer().dropMessage(5, "You do not have the CP.");
                    c.SendPacket(WvsContext.enableActions());
                    return;
                }
                if (c.getPlayer().getMap().makeCarnivalReactor(c.getPlayer().getCarnivalParty().getTeam(), num)) {
                    c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), skil.cpLoss);
                    c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                    for (User chr : c.getPlayer().getMap().getCharacters()) {
                        chr.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                    }
                    c.getPlayer().getMap().broadcastPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, num));
                    c.SendPacket(WvsContext.enableActions());
                } else {
                    c.getPlayer().dropMessage(5, "You may no longer summon the being.");
                    c.SendPacket(WvsContext.enableActions());
                }
                break;
            }
            default:
                break;
        }

    }
}
