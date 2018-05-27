/*
 * Cellion Development
 */
package handling.game;

import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import server.maps.objects.User;
import server.quest.Quest;
import net.InPacket;
import net.ProcessPacket;

/**
 * Player Key Map Handling
 * @author Mazen
 */
public final class KeyMap implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        if (pPlayer == null) return;

        if (iPacket.GetRemainder() > 8L) {
            iPacket.Skip(4);
            int numChanges = iPacket.DecodeInt();

            for (int i = 0; i < numChanges; i++) {
                int nKey = iPacket.DecodeInt();
                byte nType = iPacket.DecodeByte();
                int nAction = iPacket.DecodeInt();

                if ((nType == 1) && (nAction >= 1000)) {
                    Skill skil = SkillFactory.getSkill(nAction);
                    if ((skil != null) && (((!skil.isFourthJob()) && (!skil.isBeginnerSkill()) && (skil.isInvisible()) && (pPlayer.getSkillLevel(skil) < -1)) || (GameConstants.isLinkedAttackSkill(nAction)) || (nAction % 10000 < 1000))) {
                        continue;
                    }
                }
                pPlayer.changeKeybinding(nKey, nType, nAction);
            }
        } else {
            int nType = iPacket.DecodeInt();
            int nData = iPacket.DecodeInt();
            switch (nType) {
                case 1:
                    if (nData <= 0) {
                        pPlayer.getQuestRemove(Quest.getInstance(GameConstants.HP_ITEM));
                    } else {
                        pPlayer.getQuestNAdd(Quest.getInstance(GameConstants.HP_ITEM)).setCustomData(String.valueOf(nData));
                    }
                    break;
                case 2:
                    if (nData <= 0) {
                        pPlayer.getQuestRemove(Quest.getInstance(GameConstants.MP_ITEM));
                    } else {
                        pPlayer.getQuestNAdd(Quest.getInstance(GameConstants.MP_ITEM)).setCustomData(String.valueOf(nData));
                    }
                    break;
            }
        }
        
        pPlayer.saveKeyboardData(); // Save Keymap.
    }
}

/*
void __thiscall CFuncKeyMappedMan::SaveFuncKeyMap(CFuncKeyMappedMan *this)
{
  CFuncKeyMappedMan *v1; // edi@1
  signed int v2; // esi@1
  int *v3; // ebp@1
  int v4; // ebx@1
  unsigned int v5; // edi@5
  unsigned int v6; // eax@5
  int v7; // eax@6
  int v8; // edi@13
  int v9; // ebx@21
  unsigned int v10; // eax@24
  unsigned int v11; // edx@25
  int i; // edi@32
  unsigned int v13; // eax@33
  int v14; // esi@33
  unsigned int v15; // edx@34
  CFuncKeyMappedMan *v16; // ecx@41
  int *v17; // esi@44
  int v18; // edi@44
  CFuncKeyMappedMan *v19; // esi@44
  void *v20; // [sp-1Eh] [bp-50h]@0
  ZAllocHelper _ALLOC; // [sp+1h] [bp-31h]@12
  ZArray<long> anChangedIdx; // [sp+2h] [bp-30h]@1
  CFuncKeyMappedMan *v23; // [sp+6h] [bp-2Ch]@1
  void *p; // [sp+Ah] [bp-28h]@1
  int *v25; // [sp+Eh] [bp-24h]@21
  COutPacket oPacket; // [sp+12h] [bp-20h]@1
  int v27; // [sp+2Ah] [bp-8h]@46
  int v28; // [sp+2Eh] [bp-4h]@1

  v1 = this;
  v23 = this;
  COutPacket::COutPacket(&oPacket, 0x18E);
  v2 = 0;
  v28 = 0;
  COutPacket::Encode4(&oPacket, 0);
  v3 = 0;
  anChangedIdx.a = 0;
  LOBYTE(v28) = 1;
  p = v1->m_aFuncKeyMapped;
  v4 = (int)v1->m_aFuncKeyMapped;
  do
  {
    if ( *(_BYTE *)v4 != *(_BYTE *)(v4 + 445) || *(_DWORD *)(v4 + 1) != *(_DWORD *)(v4 + 446) )
    {
      if ( v3 )
      {
        v7 = *(v3 - 2);
        v5 = *(v3 - 1);
        if ( v7 < 0 )
          v7 = ~v7;
        if ( (unsigned int)(v7 - 4) >> 2 > v5 )
          goto LABEL_13;
        if ( v5 )
          v6 = 2 * v5;
        else
          v6 = 1;
      }
      else
      {
        v5 = 0;
        v6 = 1;
      }
      ZArray<long>::_Reserve(&anChangedIdx, v6, &_ALLOC);
      v3 = anChangedIdx.a;
LABEL_13:
      ++*(v3 - 1);
      v8 = (int)&v3[v5];
      memmove((char *)(v8 + 4), (char *)v8, 0);
      *(_DWORD *)v8 = v2;
    }
    ++v2;
    v4 += 5;
  }
  while ( v2 < 89 );
  if ( TSingleton<CWvsContext>::ms_pInstance.baseclass_0.m_Data )
  {
    if ( TSingleton<CSequencedKeyMan>::ms_pInstance )
      CSequencedKeyMan::Restore(TSingleton<CSequencedKeyMan>::ms_pInstance);
    if ( TSingleton<CSkillCommandMan>::ms_pInstance )
      CSkillCommandMan::Restore(TSingleton<CSkillCommandMan>::ms_pInstance);
  }
  if ( v3 && (v9 = *(v3 - 1), v25 = v3 - 1, v9) )
  {
    v10 = (unsigned int)oPacket.m_aSendBuff.a;
    if ( oPacket.m_aSendBuff.a )
      v11 = *((_DWORD *)oPacket.m_aSendBuff.a - 1);
    else
      v11 = 0;
    if ( oPacket.m_uOffset + 4 > v11 )
    {
      if ( oPacket.m_aSendBuff.a )
        v10 = *((_DWORD *)oPacket.m_aSendBuff.a - 1);
      do
        v10 *= 2;
      while ( oPacket.m_uOffset + 4 > v10 );
      ZArray<unsigned char>::_Realloc(&oPacket.m_aSendBuff, v10, 0, &_ALLOC);
      v10 = (unsigned int)oPacket.m_aSendBuff.a;
    }
    *(_DWORD *)(v10 + oPacket.m_uOffset) = v9;
    oPacket.m_uOffset += 4;
    for ( i = 0; i < v9; ++i )
    {
      v13 = (unsigned int)oPacket.m_aSendBuff.a;
      v14 = v3[i];
      if ( oPacket.m_aSendBuff.a )
        v15 = *((_DWORD *)oPacket.m_aSendBuff.a - 1);
      else
        v15 = 0;
      if ( oPacket.m_uOffset + 4 > v15 )
      {
        if ( oPacket.m_aSendBuff.a )
          v13 = *((_DWORD *)oPacket.m_aSendBuff.a - 1);
        do
          v13 *= 2;
        while ( oPacket.m_uOffset + 4 > v13 );
        ZArray<unsigned char>::_Realloc(&oPacket.m_aSendBuff, v13, 0, &_ALLOC);
        v13 = (unsigned int)oPacket.m_aSendBuff.a;
      }
      v16 = v23;
      *(_DWORD *)(v13 + oPacket.m_uOffset) = v14;
      oPacket.m_uOffset += 4;
      FUNCKEY_MAPPED::Encode((FUNCKEY_MAPPED *)((char *)v16->m_aFuncKeyMapped + 4 * v3[i] + v3[i]), &oPacket);
    }
    if ( TSingleton<CClientSocket>::ms_pInstance._m_pStr )
      CClientSocket::SendPacket((CClientSocket *)TSingleton<CClientSocket>::ms_pInstance._m_pStr, &oPacket);
    v17 = anChangedIdx.a;
    CFuncKeyMappedMan::AdaptVirtualKey((CFuncKeyMappedMan *)anChangedIdx.a);
    v18 = (int)v17 + 454;
    v19 = v23;
    qmemcpy((void *)v18, v23, 0x1BCu);
    *(_BYTE *)(v18 + 444) = v19->m_aFuncKeyMapped[87].nType;
    if ( TSingleton<CUIJaguarActionBar>::ms_pInstance )
      CUIJaguarActionBar::ResetInfo(TSingleton<CUIJaguarActionBar>::ms_pInstance);
    LOBYTE(v27) = 0;
    v20 = p;
LABEL_47:
    ZAllocEx<ZAllocAnonSelector>::Free(&ZAllocEx<ZAllocAnonSelector>::_s_alloc, v20);
  }
  else
  {
    LOBYTE(v28) = 0;
    if ( v3 )
      goto LABEL_47;
  }
  v27 = -1;
  if ( oPacket.m_bLoopback )
    ZAllocEx<ZAllocAnonSelector>::Free(&ZAllocEx<ZAllocAnonSelector>::_s_alloc, (void *)(oPacket.m_bLoopback - 4));
 */
