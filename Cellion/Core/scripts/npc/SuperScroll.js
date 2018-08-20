/* Super Scroll
 By Charmander
 */
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.tools.data.output);
importPackage(Packages.org.apache.mina.common);

var SCROLL_ID = new Array(2043002, 2043003);
var toScroll;
var theScroll;
var scroll;
var status = -1;
var scrolls = 0;
var equipslot, scrollslot;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        status--;
    }
    if (status == 0) {
        cm.sendYesNo("Do you want to use a 100% scroll? #rThis do not include Equip Enhancement, Potential and Other Scrolls. Only use it on the base scrolls for 10%, 60% and 100%.");
    } else if (status == 1) {
        cm.sendSimple("Please select the item you want to scroll. #rNote: Your Scroll must correspond to your Weapon or Armour Description.(#rAny misuse of Super Scroll won't be refunded, Heart scrolling doesn't work#l) \r\n\r\n" + cm.EquipList(cm.getClient()));
    } else if (status == 2) {
        toScroll = cm.getEquipId(selection);
        equipslot = selection;
        var invlist = cm.getPlayer().getInventory(MapleInventoryType.USE).list().toArray();
        for (var i = 0; i < invlist.length; i++) {
            scroll = invlist[i];
            if (scroll.getItemId() >= 2040000 && scroll.getItemId() <= 2050000) {//TODO
                scrolls += "#L" + scroll.getPosition() + "##i" + scroll.getItemId() + "# - #b#t" + scroll.getItemId() + "##l#k\r\n";
            }
        }
        cm.sendSimple("#rPlease Note!#k Any scroll will work with #r100% rate!#k\r\nPlease select the scroll you want to use on:\r\n#i" + toScroll + ":##t" + toScroll + "#. \r\n\r\n " + scrolls);
    } else if (status == 3) {
        theScroll = cm.getUseId(selection);
        scrollslot = selection;
        cm.sendYesNo("Are you sure you want to use #i" + theScroll + ":##t" + theScroll + "# on #i" + toScroll + ":##t" + toScroll + "#?");
    } else if (status == 4) {
        cm.superScroll(scrollslot, equipslot);
        cm.gainItem(2430949, -1);
        cm.dispose();
    }
}