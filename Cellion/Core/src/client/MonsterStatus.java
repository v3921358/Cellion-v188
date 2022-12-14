/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

import java.io.Serializable;

import handling.TemporaryStat;

public enum MonsterStatus implements Serializable, TemporaryStat {

    WATK(0x1, 1),
    PDD(0x2, 1),
    MATK(0x4, 1),
    MDD(0x8, 1),
    ACC(0x10, 1),
    EVA(0x20, 1),
    SPEED(0x40, 1),
    STUN(0x80, 1),
    FREEZE(0x100, 1),
    POISON(0x200, 1),
    SEAL(0x400, 1),
    SHOWDOWN(0x800, 1),
    WEAPON_ATTACK_UP(0x1000, 1),
    WEAPON_DEFENSE_UP(0x2000, 1),
    MAGIC_ATTACK_UP(0x4000, 1),
    MAGIC_DEFENSE_UP(0x8000, 1),
    DOOM(0x10000, 1),
    SHADOW_WEB(0x1000000, 11), // VERSION 170. CTS_ShadowWeb
    WEAPON_IMMUNITY(0x40000, 1),
    MAGIC_IMMUNITY(0x80000, 1),
    DAMAGE_IMMUNITY(0x200000, 1),
    NINJA_AMBUSH(0x400000, 1),
    VENOMOUS_WEAPON(0x1000000, 1), //BURN
    DARKNESS(0x2000000, 1),
    HYPNOTIZE(0x10000000, 1),
    WEAPON_DAMAGE_REFLECT(0x20000000, 1),
    MAGIC_DAMAGE_REFLECT(0x40000000, 1),
    NEUTRALISE(0x2, 2), // first int on v.87 or else it won't work.
    IMPRINT(0x4, 2),
    MONSTER_BOMB(0x8, 2),
    MAGIC_CRASH(0x10, 2),
    TRIANGULATION(0x8000, 2),
    STING_EXPLOSION(0x10000, 2),
    //speshul comes after
    EMPTY(0x8000000, 1, true),
    SUMMON(0x80000000, 1, true), //all summon bag mobs have.
    EMPTY_1(0x20, 2, false), //chaos
    EMPTY_2(0x40, 2, true),
    EMPTY_3(0x80, 2, true),
    EMPTY_4(0x100, 2, true), //jump
    EMPTY_5(0x200, 2, true),
    EMPTY_6(0x400, 2, true),
    EMPTY_7(0x2000, 2, true),;
    static final long serialVersionUID = 0L;
    private final int i;
    private final int first;
    private final boolean end;

    private MonsterStatus(int i, int first) {
        this.i = i;
        this.first = first;
        this.end = false;
    }

    private MonsterStatus(int i, int first, boolean end) {
        this.i = i;
        this.first = first;
        this.end = end;
    }

    @Override
    public int getPosition() {
        return first;
    }

    public boolean isEmpty() {
        return end;
    }

    @Override
    public int getValue() {
        return i;
    }

    public static final Disease getLinkedDisease(final MonsterStatus skill) {
        switch (skill) {
            case STUN:
            case SHADOW_WEB:
                return Disease.STUN;
            case POISON:
            case VENOMOUS_WEAPON:
                return Disease.POISON;
            case SEAL:
            case MAGIC_CRASH:
                return Disease.SEAL;
            case FREEZE:
                return Disease.FREEZE;
            case DARKNESS:
                return Disease.DARKNESS;
            case SPEED:
                return Disease.SLOW;
        }
        return null;
    }
}
