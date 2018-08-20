/*
 * @author Mazen Massoud;
 * @author MrPie :)
 * ���ֿ� sch2307@naver.com
 * ���� junny_adm@naver.com
 * ������ raccoonfox69@gmail.com
 * ������ ku3135@nate.com
 * ����ȫ designer@inerve.kr
 */
package client;

public class KeyBinding {

    private int type, action;

    public KeyBinding(int type, int action) {
        super();
        this.type = type;
        this.action = action;
    }

    public int getType() {
        return type;
    }

    public int getAction() {
        return action;
    }
}
