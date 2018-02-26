package handling.login;

/**
 *
 * @author Steven Even
 */
public enum WorldServerBackgroundHandler {

    Background1("a2", true),
    Background2("a3", true),
    Background3("a5", true),
    Background4("a4", true),
    Background5("a0", true),
    Background6("a1", true);

    private final String image;
    private final boolean flag;

    private WorldServerBackgroundHandler(String image, boolean flag) {
        this.image = image;
        this.flag = flag;
    }

    public String getImage() {
        return image;
    }

    public boolean getFlag() {
        return flag;
    }
}
