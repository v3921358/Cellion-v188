package server.messages;

import enums.MessageOpcodesType;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class WeddingPortalError implements MessageInterface {

    private int type;

    public WeddingPortalError(int type) {
        this.type = type;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
 /*
	 case 0:
      v13 = 0;
      v12 = 0;
      v11 = 0;
      v10 = 0;
      v9 = 0;
      v8 = 0;
      v7 = v3;
      v4 = &v7;
      v6 = 9120;
      goto LABEL_6;
    case 1:
      v13 = 0;
      v12 = 0;
      v11 = 0;
      v10 = 0;
      v9 = 0;
      v8 = 0;
      v7 = v3;
      v3 = &v7;
      v6 = 9121;
      v5 = &v7;
      goto LABEL_7;
    case 2:
      v13 = 0;
      v12 = 0;
      v11 = 0;
      v10 = 0;
      v9 = 0;
      v8 = 0;
      v7 = v3;
      v2 = &v7;
      v6 = 8960;
      v5 = &v7;
      goto LABEL_7;
    case 3:
      v13 = 0;
      v12 = 0;
      v11 = 0;
      v10 = 0;
      v9 = 0;
      v8 = 0;
      v7 = v3;
      v4 = &v7;
      v6 = 8959;
LABEL_6:
      v5 = v4;
LABEL_7:
      StringPool::GetInstance(v3, v2);
      StringPool::GetString(v5, v6);
      result = sub_1771CC0(v7, v8, v9, v10, v11, v12, v13);
      break;
    default:
      return result;
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.WeddingPortal.getType());
        oPacket.EncodeByte(type);
    }

}
