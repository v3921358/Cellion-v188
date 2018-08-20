package enums;

/**
 *
 * @author Novak
 */
public class ServerMode {

    public enum MapleServerMode {

        //Login Server
        LOGIN(0),
        //Game Server
        GAME(1),
        //Cash Shop Server
        CS(2),
        //Farm Server
        FARM(3),
        //Auth (child of login) Server
        AUTH(4),
        //MapleTalk Server (1 per world)
        MAPLETALK(5),
        //Auction House Server
        AUCTION(6);

        private final int mode;

        private MapleServerMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }
}
