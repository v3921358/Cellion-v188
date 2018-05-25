/*
 * Cellion
 * @author aa
 */

status = -1;

var targetName;
var targetQuantity;
var quantity;
var itemId;
var invType;
var fullInv;
//selection = slotId in this case

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (!cm.getPlayer().isGM()) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        cm.sendGetText("Enter a player's name to access their inventory");
        targetName = cm.getText();
    } else if (status == 1) {
        cm.sendGetText("Enter the inventory type # you'd like to access #r#n1=EQUIP#r#n2=USE#r#n3=ETC#r#n4=SETUP#r#n5=CASH");
        targetSlot = cm.getText();  
    } else if (status == 2) {
        fullInv = cm.accessInventory(targetName, invType);
        cm.sendSimple("Select an item to remove for " + targetName + "#r#n" + fullinv);
    } else if (status == 3) {
        if (selection > 0) {
            itemId = cm.getItemFromSlot(invType, selection);
            targetQuantity = cm.getPlayer().getitemQuantity(itemId);
            if (targetQuantity > 0) {
                cm.sendGetText("How much of this item would you like to remove? (" + targetQuantity + ")");
                quantity = cm.getText();
            }
        }
    } else if (status == 4) {
        cm.deleteItemBySlot(targetName, invType, selection, quantity);
        cm.sendOk("Deleted #t" + itemId + "# x" + quantity + " from " + targetName + " at slotId: " + selection);
        cm.dispose();
    }
}