/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.skills;

import java.io.IOException;
import java.util.HashMap;
import provider.wz.cache.WzDataStorage;
import provider.wz.nox.NoxBinaryReader;

/**
 *
 * @author song_lin
 */
public class SkillFactory {

    private final HashMap<Integer, SkillData> skills = new HashMap<>();

    public final int SkillEntry = 0;
    public final int SkillRoot = 1;
    public final int AttackType = 2;
    public final int BFSkill = 3;
    public final int Dragon = 4;
    public final int EliteMobSkill = 5;
    public final int FamiliarSkill = 6;
    public final int FieldSkill = 7;
    public final int HekatonFieldSkill = 8;
    public final int ItemSkill = 9;
    public final int ItemOptionSkill = 10;
    public final int MCSkill = 11;
    public final int MCGuardian = 12;
    public final int MobSkill = 13;
    public final int MonsterBattleSKill = 14;
    public final int CraftingRecipe = 15;

    public void loadData() {
        try {
            final NoxBinaryReader data = WzDataStorage.getBinarySkillData();
            int directories = data.readInt();
            for (int i = 0; i < directories; i++) {
                readDirectoryData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readDirectoryData(NoxBinaryReader data) throws IOException {
        switch (data.readInt()) {
            case SkillRoot:
            case AttackType:
            case BFSkill:
            case Dragon:
            case EliteMobSkill:
            case FamiliarSkill:
            case FieldSkill:
            case HekatonFieldSkill:
            case ItemSkill:
            case ItemOptionSkill:
            case MCSkill:
            case MCGuardian:
            case MobSkill:
            case MonsterBattleSKill:
            case CraftingRecipe:
                break;
            case SkillEntry:
                parseSkillEntries(data);
                break;
        }
    }

    private void parseSkillEntries(NoxBinaryReader data) throws IOException {
        int jobid = data.readInt();
        int skillEntries = data.readInt();
        for (int i = 0; i < skillEntries; i++) {
            int skillid = data.readInt();
            boolean hasAction = data.readBoolean();
            if (hasAction) {

            }
        }
    }
}
