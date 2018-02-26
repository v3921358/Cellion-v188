/* Created by **DejaVu**
    This is my own work, not leeched from a release or borrowed from anyone.
    Feel free to re-distribute my work, but remember to give the proper credits.  */

var status;
var msg = "You're either lacking the item needed for upgrade, or you don't have enough Maple Leaves or mesos.";
var nLeaf = 4001126;  // Maple Leaf Item ID
var leaf = "200 #i4001126#";
var leaf1 = "400 #i4001126#";
var leaf2 = "600 #i4001126#";
var amt = 200;  // amount for the leaves (1)
var amt1 = 400;  // amount for the leaves (2)
var amt2 = 600;  // amount for the leaves (3)
var nPrice = 500000;  // meso cost (1)           (n)________ 
var nPrice1 = 1000000;  // meso cost (2)          The (n) before the variable name is my way of
var nPrice2 = 1500000;  // meso cost (3)          remembering that's an integer value
var price = "500,000 mesos";
var price1 = "1,000,000 mesos";
var price2 = "1,500,000 mesos";
var items = [
/* Bows */       [1452016, 1452022, 1452045], /* Maple Bow, Maple Soul Searcher, Maple Kandiva */
/* Claws */      [1472030, 1472032, 1472055], /* Maple Claw, Maple Kandayo, Maple Skanda */
/* CrossBow */   [1462014, 1462019, 1462040], /* Maple Crow, Maple Crossbow, Maple Nishada */
/* Dagger */     [1332025, 1332055, 1332056], /* Maple Wagner, Maple Dark Mate, Maple Asura Dagger */
/* Axe */        [1412011, 1412027, 1312032], /* Maple Dragon Axe, Maple Demon Axe, Maple Steel Axe */
/* Mace */       [1422014, 1422029, 1322054], /* Maple Doom Singer, Maple Belzet, Maple Havoc Hammer */
/* Sword */      [1302020, 1302030, 1302064, 1402039], /* Maple Sword, Maple Soul Singer, Maple Glory Sword, Maple Soul Rohen */
/* Pole Arm */   [1442024, 1442051],          /* Maple Scorpio, Maple Karstan */
/* Spear */      [1432012, 1432040],          /* Maple Impaler, Maple Soul Spear */
/* Wand/Staff */ [1382009, 1382012, 1382039, 1372034],  /* Maple Staff, Maple Lama Staff, Maple Wisdom Staff, Maple Shine Wand */
/* Gun */        [1492020, 1492021, 1492022], /* Maple Gun, Maple Storm Pistol, Maple Cannon Shooter */
/* Knuckle */    [1482020, 1482021, 1482022], /* Maple Knuckle, Maple Storm Finger, Maple Golden Claw */
                                           ]; 

function start(){
    status = -1;
    action(1,0,0);
}

function action(mode, type, selection){
    if (mode != 1){
        cm.dispose();
        return;
    } else {
        status++;
    }
    if (status == 0){
        cm.sendSimple("#eHello #h #, I'm the Maple Weapon Upgrader of #bSpookyStory#k! You can upgrade and purchase Maple Weapons from me. What would you like to buy/upgrade?" +
                        "\r\n#r#L0#Maple Bow#l " +
                        "\r\n#L1#Maple Claw#l " +
                        "\r\n#L2#Maple Crossbow#l " +
                        "\r\n#L3#Maple Dagger#l " +
                        "\r\n#L4#Maple Axe#l " +
                        "\r\n#L5#Maple Mace#l " +
                        "\r\n#L6#Maple Sword#l " +
                        "\r\n#L7#Maple Pole Arm#l " +
                        "\r\n#L8#Maple Spear#l " +
                        "\r\n#L9#Maple Wand/Staff#l" +
                        "\r\n#L10#Maple Gun#l" +
                        "\r\n#L11#Maple Knuckle");
    }
    else if (status == 1){
        if (selection == 0){  // bows
            cm.sendSimple("#e#L0#Buy a #i"+items[0][0]+"# for "+leaf+" and "+price+"#l  \r\n#L1#Trade your #i"+items[0][0]+"#, "+leaf1+", and "+price1+" for a #i"+items[0][1]+"##l  \r\n#L2#Trade your #i"+items[0][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[0][2]+"##l");
        }
        else if (selection == 1){  // claws
            cm.sendSimple("#e#L3#Buy a #i"+items[1][0]+"# for "+leaf+" and "+price+"#l  \r\n#L4#Trade your #i"+items[1][0]+"#, "+leaf1+", and "+price1+" for a #i"+items[1][1]+"##l  \r\n#L5#Trade your #i"+items[1][1]+", "+leaf2+", and "+price2+" for a #i"+items[1][2]+"##l");
        }
        else if (selection == 2){  // crossbows
            cm.sendSimple("#e#L6#Buy a #i"+items[2][0]+"# for "+leaf+" and "+price+"#l  \r\n#L7#Trade your #i"+items[2][0]+"#, "+leaf1+", and "+price1+" for a #i"+items[2][1]+"##l  \r\n#L8#Trade your #i"+items[2][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[2][2]+"##l");
        }
        else if (selection == 3){  // daggers
            cm.sendSimple("#e#L9#Buy a #i"+items[3][0]+"# for "+leaf1+" and "+price1+"#l  \r\n#L10#Trade your #i"+items[3][0]+"#, "+leaf2+", and "+price2+" for a #i"+items[3][1]+"##l  \r\n#L11#Trade your #i"+items[3][0]+"#, "+leaf2+", and "+price2+" for a #i"+items[3][2]+"##l");
        }
        else if (selection == 4){  // axes
            cm.sendSimple("#e#L12#Buy a #i"+items[4][0]+"# for "+leaf1+" and "+price1+"#l  \r\n#L13#Trade your #i"+items[4][0]+"#, "+leaf2+", and "+price2+" for a #i"+items[4][1]+"##l  \r\n#L14#Trade your #i"+items[4][0]+"#, "+leaf2+", and "+price2+" for a #i"+items[4][2]+"##l");
        }
        else if (selection == 5){  // maces
            cm.sendSimple("#e#L15#Buy a #i"+items[5][0]+"# for "+leaf1+" and "+price1+"#l  \r\n#L16#Trade your #i"+items[5][0]+"#, "+leaf2+", and "+price2+" for a #i"+items[5][1]+"##l  \r\n#L17#Trade your #i"+items[5][0]+"#, "+leaf2+", and "+price2+" for a #i"+items[5][2]+"##l");
        }
        else if (selection == 6){  // swords
            cm.sendSimple("#e#L18#Buy a #i"+items[6][0]+"# for "+leaf+" and "+price+"#l  \r\n#L19#Trade your #i"+items[6][0]+"#, "+leaf1+", and "+price1+" for a #i"+items[6][1]+"#  \r\n#L20#Trade your #i"+items[6][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[6][2]+"##l  \r\n#L21#Trade your #i"+items[6][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[6][3]+"##l");
        }
        else if (selection == 7){  // pole arms
            cm.sendSimple("#e#L22#Buy a #i"+items[7][0]+"# for "+leaf1+" and "+price1+"#l  \r\n#L23#Trade your #i"+items[7][0]+"#, "+leaf2+", and "+price2+" for a #i"+items[7][1]+"##l");
        }
        else if (selection == 8){  // spears
            cm.sendSimple("#e#L24#Buy a #i"+items[8][0]+"# for "+leaf1+" and "+price1+"#l  \r\n#L25#Trade your #i"+items[8][0]+"#, "+leaf2+", and "+price2+" for a #i"+items[8][1]+"##l");
        }
        else if (selection == 9){  // wands / staffs
            cm.sendSimple("#e#L26#Buy a #i"+items[9][0]+"# for "+leaf+" and "+price+"#l  \r\n#L27#Trade your #i"+items[9][0]+"#, "+leaf1+", and "+price1+" for a #i"+items[9][1]+"##l  \r\n#L28#Trade your #i"+items[9][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[9][2]+"##l  \r\n#L29#Trade your #i"+items[9][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[9][3]+"##l");
        }
        else if (selection == 10){ // guns
            cm.sendSimple("#e#L30#Buy a #i"+items[10][0]+"# for "+leaf+" and "+price+"#l \r\n#L31#Trade your #i"+items[10][0]+"#, "+leaf1+", and "+price1+" for a #i"+items[10][1]+"##l \r\n#L32#Trade your #i"+items[10][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[10][2]+"##l");
        }
        else if (selection == 11){ // knuckles
            cm.sendSimple("#e#L33#Buy a #i"+items[11][0]+"# for "+leaf+" and "+price+"#l \r\n#L34#Trade your #i"+items[11][0]+"#, "+leaf1+", and "+price1+" for a #i"+items[11][1]+"##l \r\n#L35#Trade your #i"+items[11][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[11][2]+"##l");
        } else {
            cm.sendOk("#e#rDejaVu says:#k Go f*** yourself xD");
            cm.dispose();
        }
    }
    else if (status == 2){
        if (selection == 0){  // Maple Bow
            if(cm.haveItem(nLeaf, amt) && cm.getMeso() >= nPrice){
                cm.sendOk("Here's your #i"+items[0][0]+"#");
                cm.gainItem(items[0][0], 1);
                cm.gainItem(nLeaf, -amt);
                cm.gainMeso(-nPrice);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 1){  // Maple Soul Searcher
            if(cm.haveItem(items[0][0], 1) && cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[0][1]+"#");
                cm.gainItem(items[0][1], 1);
                cm.gainItem(items[0][0], -1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 2){  // Maple Kandiva Bow
            if(cm.haveItem(items[0][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[0][2]+"#");
                cm.gainItem(items[0][2], 1);
                cm.gainItem(items[0][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 3){  // Maple Claw
            if(cm.haveItem(nLeaf, amt) && cm.getMeso() >= nPrice){
                cm.sendOk("Here's your #i"+items[1][0]+"#");
                cm.gainItem(items[1][0], 1);
                cm.gainItem(nLeaf, -amt);
                cm.gainMeso(-nPrice);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 4){  // Maple Kandayo
            if(cm.haveItem(items[1][0], 1) && cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[1][1]+"#");
                cm.gainItem(items[1][1], 1);
                cm.gainItem(items[1][0], -1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 5){  // Maple Skanda
            if(cm.haveItem(items[1][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[1][2]+"#");
                cm.gainItem(items[1][2], 1);
                cm.gainItem(items[1][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 6){  // Maple Crow
            if(cm.haveItem(nLeaf, amt) && cm.getMeso() >= nPrice){
                cm.sendOk("Here's your #i"+items[2][0]+"#");
                cm.gainItem(items[2][0], 1);
                cm.gainItem(nLeaf, -amt);
                cm.gainMeso(-nPrice);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 7){  // Maple Crossbow
            if(cm.haveItem(items[2][0], 1) && cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[2][1]+"#");
                cm.gainItem(items[2][1], 1);
                cm.gainItem(items[2][0], -1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 8){  // Maple Nishada
            if(cm.haveItem(items[2][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[2][2]+"#");
                cm.gainItem(items[2][2], 1);
                cm.gainItem(items[2][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 9){  // Maple Wagner
            if(cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[3][0]+"#");
                cm.gainItem(items[3][0], 1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 10){  // Maple Dark Mate
            if(cm.haveItem(items[3][0], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[3][1]+"#");
                cm.gainItem(items[3][1], 1);
                cm.gainItem(items[3][0], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 11){  // Maple Asura Dagger
            if(cm.haveItem(items[3][0], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[3][2]+"#");
                cm.gainItem(items[3][2], 1);
                cm.gainItem(items[3][0], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 12){  // Maple Dragon Axe
            if(cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[4][0]+"#");
                cm.gainItem(items[4][0], 1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 13){  // Maple Demon Axe
            if(cm.haveItem(items[4][0], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[4][1]+"#");
                cm.gainItem(items[4][1], 1);
                cm.gainItem(items[4][0], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 14){  // Maple Steel Axe
            if(cm.haveItem(items[4][0], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[4][2]+"#");
                cm.gainItem(items[4][2], 1);
                cm.gainItem(items[4][0], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 15){  // Maple Doom Singer
            if(cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[5][0]+"#");
                cm.gainItem(items[5][0], 1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 16){  // Maple Belzet
            if(cm.haveItem(items[5][0], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[5][1]+"#");
                cm.gainItem(items[5][1], 1);
                cm.gainItem(items[5][0], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 17){  // Maple Havoc Hammer
            if(cm.haveItem(items[5][0], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[5][2]+"#");
                cm.gainItem(items[5][2], 1);
                cm.gainItem(items[5][0], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 18){  // Maple Sword
            if(cm.haveItem(nLeaf, amt) && cm.getMeso() >= nPrice){
                cm.sendOk("Here's your #i"+items[6][0]+"#");
                cm.gainItem(items[6][0], 1);
                cm.gainItem(nLeaf, -amt);
                cm.gainMeso(-nPrice);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 19){  // Maple Soul Singer
            if(cm.haveItem(items[6][0], 1) && cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[6][1]+"#");
                cm.gainItem(items[6][1], 1);
                cm.gainItem(items[6][0], -1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 20){  // Maple Glory Sword
            if(cm.haveItem(items[6][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[6][2]+"#");
                cm.gainItem(items[6][2], 1);
                cm.gainItem(items[6][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 21){  // Maple Soul Rohen
            if(cm.haveItem(items[6][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[6][3]+"#");
                cm.gainItem(items[6][3], 1);
                cm.gainItem(items[6][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 22){  // Maple Scorpio
            if(cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[7][0]+"#");
                cm.gainItem(items[7][0], 1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 23){  // Maple Karstan
            if(cm.haveItem(items[7][0], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[7][1]+"#");
                cm.gainItem(items[7][1], 1);
                cm.gainItem(items[7][0], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 24){  // Maple Impaler
            if(cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[8][0]+"#");
                cm.gainItem(items[8][0], 1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 25){  // Maple Soul Spear
            if(cm.haveItem(items[8][0], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[8][1]+"#");
                cm.gainItem(items[8][1], 1);
                cm.gainItem(items[8][0], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 26){  // Maple Staff
            if(cm.haveItem(nLeaf, amt) && cm.getMeso() >= nPrice){
                cm.sendOk("Here's your #i"+items[9][0]+"#");
                cm.gainItem(items[9][0], 1);
                cm.gainItem(nLeaf, -amt);
                cm.gainMeso(-nPrice);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 27){  // Maple Lama Staff
            if(cm.haveItem(items[9][0], 1) && cm.haveItem(nLeaf, amt1) && cm.getMeso() >= nPrice1){
                cm.sendOk("Here's your #i"+items[9][1]+"#");
                cm.gainItem(items[9][1], 1);
                cm.gainItem(items[9][0], -1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-nPrice1);
                cm.dispose();
            }
        } else if (selection == 28){  // Maple Wisdom Staff
            if(cm.haveItem(items[9][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){    
                cm.sendOk("Here's your #i"+items[9][2]+"#");
                cm.gainItem(items[9][2], 1);
                cm.gainItem(items[9][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 29){  // Maple Shine Wand
            if(cm.haveItem(items[9][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= nPrice2){
                cm.sendOk("Here's your #i"+items[9][3]+"#");
                cm.gainItem(items[9][3], 1);
                cm.gainItem(items[9][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-nPrice2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 30){  // Maple Gun
            if(cm.haveItem(nLeaf, amt) && cm.getMeso() >= nPrice){
                cm.sendOk("Here's your #i"+items[10][0]+"#");
                cm.gainItem(items[10][0], 1);
                cm.gainItem(nLeaf, -amt);
                cm.gainMeso(-nPrice);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 31){  // Maple Storm Pistol
            if(cm.haveItem(items[10][0], 1) && cm.haveItem(nLeaf, amt1) && cm.getMeso() >= price1){
                cm.sendOk("Here's your #i"+items[10][1]+"#");
                cm.gainItem(items[10][1], 1);
                cm.gainItem(items[10][0], -1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-price1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 32){  // Maple Cannon Shooter
            if(cm.haveItem(items[10][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= price2){
                cm.sendOk("Here's your #i"+items[10][2]+"#");
                cm.gainItem(items[10][2], 1);
                cm.gainItem(items[10][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-price2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 33){  // Maple Knuckle
            if(cm.haveItem(nLeaf, amt) && cm.getMeso() >= price){
                cm.sendOk("Here's your #i"+items[11][0]+"#");
                cm.gainItem(items[11][0], 1);
                cm.gainItem(nLeaf, -amt);
                cm.gainMeso(-price);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 34){  // Maple Storm Finger
            if(cm.haveItem(items[11][0], 1) && cm.haveItem(nLeaf, amt1) && cm.getMeso() >= price1){
                cm.sendOk("Here's your #i"+items[11][1]+"#");
                cm.gainItem(items[11][1], 1);
                cm.gainItem(items[11][0], -1);
                cm.gainItem(nLeaf, -amt1);
                cm.gainMeso(-price1);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        } else if (selection == 35){  // Maple Golden Claw
            if(cm.haveItem(items[11][1], 1) && cm.haveItem(nLeaf, amt2) && cm.getMeso() >= price2){
                cm.sendOk("Here's your #i"+items[11][2]+"#");
                cm.gainItem(items[11][2], 1);
                cm.gainItem(items[11][1], -1);
                cm.gainItem(nLeaf, -amt2);
                cm.gainMeso(-price2);
                cm.dispose();
            }else{
                cm.sendOk(msg);
                cm.dispose();
            }
        }else{
            cm.sendOk("#e#rDejaVu says:#k Go f*** yourself xD");
            cm.dispose();
        }
    }else{
        cm.sendOk("#e#rDejaVu says:#k Go f*** yourself xD");
        cm.dispose();
    }
}  