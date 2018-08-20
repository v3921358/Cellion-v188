/* 
 NPC Name: 		Johanna
 Map(s): 		Time lane Three Doors
 */

var status = -1;


function action(mode, type, selection) {
    var isAsecnsionUpdateOrWZEditedServer = cm.isWZEditingv115Server() || cm.isMapleAscensionUpdate();

    if (isAsecnsionUpdateOrWZEditedServer) {
        cm.openShop(81);
    }
    cm.dispose();
}