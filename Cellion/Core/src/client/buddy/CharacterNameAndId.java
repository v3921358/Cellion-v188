package client.buddy;

public class CharacterNameAndId {

    private final int id;
    private final String name;

    public CharacterNameAndId(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
