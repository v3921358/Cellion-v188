package handling.login;

import client.MapleClient;
import handling.PacketThrottleLimits;
import net.InPacket;
import tools.packet.CLogin;
import net.ProcessPacket;

@PacketThrottleLimits(
        FlagCount = 5,
        ResetTimeMillis = 1000 * 60 * 20, // 20 minutes 
        MinTimeMillisBetweenPackets = 30000, // Client is at least 60 seconds between to sent it once

        FunctionName = "AuthRequestHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.PermanentBan)
public final class PrivateServerPacketHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        /*
        Client sending packet:
        ?Run@CWvsApp@@QAE?AUtagMSG@@PAH@Z
        
        if ( tCurTime - thisa->m_tLastServerIPCheck2 > 60000 * rand )
        {
          thisa->m_tLastServerIPCheck2 = tCurTime;
          if ( TSecType<int>::operator int(&g_bisAuthPacket) )
          {
            v30 = NtCurrentTeb();
            v31 = v30->NtTib.StackLimit;
            for ( j = v30->NtTib.StackBase; j > v31; *j = 0 )
              --j;
            return 0;
          }
          TSecType<int>::operator=(&g_bisAuthPacket, 1);
          dwPrivateAuth = GetCurrentThreadId() ^ 0x86;
          COutPacket::COutPacket(&oPacket, 0x86);
          v116 = 2;
          COutPacket::Encode4(&oPacket, dwPrivateAuth);
          if ( TSingleton<CClientSocket>::IsInstantiated() )
          {
            v29 = TSingleton<CClientSocket>::GetInstance();
            CClientSocket::SendPacket(v29, &oPacket);
          }
          v116 = -1;
          COutPacket::~COutPacket(&oPacket);
          if ( !TSecType<int>::operator int(&g_bisAuthPacket) )
          {
            v33 = NtCurrentTeb();
            v34 = v33->NtTib.StackLimit;
            for ( k = v33->NtTib.StackBase; k > v34; *k = 0 )
              --k;
            return 0;
          }
        
        
            Client processing response:
        void __cdecl CClientSocket::OnPrivateServerAuth(CInPacket *iPacket)
{
  unsigned int dwPrivateServerKey; // STA0_4@1
  struct _TEB *v2; // eax@7
  _DWORD *v3; // ecx@7
  _DWORD *i; // eax@7

  dwPrivateServerKey = CInPacket::Decode4(iPacket);
  if ( dwPrivateServerKey == (GetCurrentThreadId() ^ 0x91) )
  {
    if ( TSecType<int>::operator int(&g_bisAuthPacket) )
    {
      TSecType<int>::operator=(&g_bisAuthPacket, 0);
    }
    else if ( TSecType<int>::operator int(&g_bisAuthPacket2) )
    {
      TSecType<int>::operator=(&g_bisAuthPacket2, 0);
    }
  }
  else
  {
    v2 = NtCurrentTeb();
    v3 = v2->NtTib.StackLimit;
    for ( i = v2->NtTib.StackBase; i > v3; *i = 0 )
      --i;
  }
}
         */

        // dwPrivateAuth = GetCurrentThreadId() ^ 0x86
        int clientCurrentThreadId = iPacket.DecodeInt();

        c.SendPacket(CLogin.sendAuthResponse(clientCurrentThreadId));
    }

}
