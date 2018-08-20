package handling.world;

import java.util.List;
import java.util.Map;

import server.maps.objects.Pet;

public interface MapleCharacterLook {

    public byte getGender();

    public byte getSkinColor();

    public int getFace();

    public int getHair();

    public byte getSecondaryGender();

    public int getZeroBetaFace();

    public int getZeroBetaHair();

    public int getAngelicDressupHair();

    public int getAngelicDressupFace();

    public int getAngelicDressupSuit();

    public int getFaceMarking();

    public int getEars();

    public int getTail();

    public int getElf();

    public short getJob();

    public List<Pet> getPets();

    public Map<Short, Integer> getEquips(boolean fusionAnvil);

    public Map<Short, Integer> getSecondaryEquips(boolean fusionAnvil);

    public Map<Short, Integer> getTotems();
}
