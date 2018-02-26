package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class OnLimitNonCombatExpIncreaseMessage implements MessageInterface {

    private long flag = 0;

    public OnLimitNonCombatExpIncreaseMessage(long flag) {
        this.flag = flag;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.LimitNonCombatExpIncrease.getType());
        oPacket.EncodeLong(flag);
        /*
		  if ( !(v21 & 0x100000) )
  {
    if ( v1 & 0x200000 )
    {
      StringPool::GetInstance(v21, 0);
      v6 = StringPool::GetString(&v21, 8629);
      v3 = 3;
      LOBYTE(v22) = 3;
      StringPool::GetInstance(&a1, v7);
      v8 = StringPool::GetString(&a1, 8634);
      LOBYTE(v22) = 4;
    }
    else
    {
      if ( v1 & 0x400000 )
      {
        StringPool::GetInstance(&v21, 0);
        v9 = StringPool::GetString(&v21, 8630);
        v3 = 5;
        LOBYTE(v22) = 5;
        StringPool::GetInstance(v10, &a1);
        v11 = StringPool::GetString(&a1, 8634);
        v19 = *v9;
        v18 = *v11;
        LOBYTE(v22) = 6;
        v17 = &v20;
        goto LABEL_14;
      }
      if ( v1 & 0x800000 )
      {
        StringPool::GetInstance(v21, 0);
        v6 = StringPool::GetString(&v21, 8631);
        v3 = 7;
        LOBYTE(v22) = 7;
        StringPool::GetInstance(&a1, v12);
        v8 = StringPool::GetString(&a1, 8634);
        LOBYTE(v22) = 8;
      }
      else
      {
        if ( &dword_1000000 & v21 )
        {
          StringPool::GetInstance(&v21, 0);
          v13 = StringPool::GetString(&v21, 8632);
          v3 = 9;
          LOBYTE(v22) = 9;
          StringPool::GetInstance(v14, &a1);
          v15 = StringPool::GetString(&a1, 8634);
          v19 = *v13;
          v18 = *v15;
          LOBYTE(v22) = 10;
          v17 = &v20;
          goto LABEL_14;
        }
        if ( !(dword_2000000 & v21) )
          goto LABEL_18;
        StringPool::GetInstance(dword_2000000 & v21, 0);
        v6 = StringPool::GetString(&v21, 13061);
        v3 = 11;
        LOBYTE(v22) = 11;
        StringPool::GetInstance(&a1, v16);
        v8 = StringPool::GetString(&a1, 8634);
        LOBYTE(v22) = 12;
      }
    }
    v19 = *v6;
    v18 = *v8;
    v17 = &v20;
    goto LABEL_14;
  }
         */
    }

}
