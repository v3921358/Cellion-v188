var status;
var msg = "You're either lacking the item needed for upgrade, or you don't have enough Maple Leaves or mesos.";
var nLeaf = 4001211;  // Maple Coin
var leaf = "1 #i4001211#";
var leaf1 = "1 #i4001211#";
var leaf2 = "1 #i4001211#";
var leaf3 = "1 #i4001211#";
var leaf4 = "1 #i4001211#";
var leaf5 = "1 #i4001211#";
var leaf6 = "1 #i4001211#";
var leaf7 = "1 #i4001211#";
var leaf8 = "1 #i4001211#";
var leaf9 = "1 #i4001211#";
var leaf10 = "1 #i4001211#";
var leaf11 = "1 #i4001211#";
var leaf12 = "1 #i4001211#";
var leaf13 = "1 #i4001211#";
var leaf14 = "1 #i4001211#";
var leaf15 = "1 #i4001211#";
var leaf16 = "1 #i4001211#";
var leaf17 = "2 #i4001211#";
var leaf18 = "2 #i4001211#";
var leaf19 = "3 #i4001211#";
var amt = 1;  // amount for the leaves (1)
var amt1 = 1;  // amount for the leaves (2)
var amt2 = 1;  // amount for the leaves (3)
var amt3 = 1;  // amount for the leaves (3)
var amt4 = 1;  // amount for the leaves (3)
var amt5 = 1;  // amount for the leaves (3)
var amt6 = 1;  // amount for the leaves (3)
var amt7 = 1;  // amount for the leaves (3)
var amt8 = 1;  // amount for the leaves (3)
var amt9 = 1;  // amount for the leaves (3)
var amt10 = 1;  // amount for the leaves (3)
var amt11 = 1;  // amount for the leaves (3)
var amt12 = 1;  // amount for the leaves (3)
var amt13 = 1;  // amount for the leaves (3)
var amt14 = 1;  // amount for the leaves (3)
var amt15 = 1;  // amount for the leaves (3)
var amt16 = 1;  // amount for the leaves (3)
var amt17 = 2;  // amount for the leaves (3)
var amt18 = 2;  // amount for the leaves (3)
var amt19 = 3;  // amount for the leaves (3)
var nPrice = 100000;  // meso cost (1)           (n)________ 
var nPrice1 = 150000;  // meso cost (2)          The (n) before the variable name is my way of
var nPrice2 = 200000;  // meso cost (3)          remembering that's an integer value
var nPrice3 = 250000;  // meso cost (3) 
var nPrice4 = 300000;  // meso cost (3) 
var nPrice5 = 350000;  // meso cost (3) 
var nPrice6 = 400000;  // meso cost (3) 
var nPrice7 = 450000;  // meso cost (3) 
var nPrice8 = 500000;  // meso cost (3) 
var nPrice9 = 550000;  // meso cost (3) 
var nPrice10 = 600000;  // meso cost (3) 
var nPrice11 = 650000;  // meso cost (3) 
var nPrice12 = 700000;  // meso cost (3) 
var nPrice13 = 750000;  // meso cost (3) 
var nPrice14 = 800000;  // meso cost (3) 
var nPrice15 = 900000;  // meso cost (3) 
var nPrice16 = 1000000;  // meso cost (3) 
var nPrice17 = 1500000;  // meso cost (3) 
var nPrice18 = 2000000;  // meso cost (3) 
var nPrice19 = 3000000;  // meso cost (3) 
var price = "100,000 mesos";
var price1 = "150,000 mesos";
var price2 = "200,000 mesos";
var price3 = "250,000 mesos";
var price4 = "300,000 mesos";
var price5 = "350,000 mesos";
var price6 = "400,000 mesos";
var price7 = "450,000 mesos";
var price8 = "500,000 mesos";
var price9 = "550,000 mesos";
var price10 = "600,000 mesos";
var price11 = "650,000 mesos";
var price12 = "700,000 mesos";
var price13 = "750,000 mesos";
var price14 = "800,000 mesos";
var price15 = "900,000 mesos";
var price16 = "1,000,000 mesos";
var price17 = "1,500,000 mesos";
var price18 = "2,000,000 mesos";
var price19 = "2,000,000 mesos";
var items = [
/* Evolving Ring */       [1112499, 1112500, 1112501, 1112502, 1112503, 1112504, 1112505, 1112506, 1112507, 1112508, 1112509, 1112510, 1112511, 1112512, 1112513, 1112514, 1112515, 1112516, 1112517], /* Maple Bow, Maple Soul Searcher, Maple Kandiva */
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
        cm.sendSimple("#eHello #h #, You notice the new Daily Attendance event did you. Do you want to upgrade your Ring?" +
                        "\r\n#r#L0#Evolve Ring Upgrade#l");
    }
    else if (status == 1){
        if (selection == 0){  // Evolve Ring
            cm.sendSimple("#e#L0#Buy a #i"+items[0][0]+"# for "+leaf+" and "+price+"#l  \r\n#L1#Trade your #i"+items[0][0]+"#, "+leaf1+", and "+price1+" for a #i"+items[0][1]+"##l  \r\n#L2#Trade your #i"+items[0][1]+"#, "+leaf2+", and "+price2+" for a #i"+items[0][2]+"##l \r\n#L3#Trade your #i"+items[0][2]+"#, "+leaf3+", and "+price3+" for a #i"+items[0][3]+"##l \r\n#L4#Trade your #i"+items[0][3]+"#, "+leaf4+", and "+price4+" for a #i"+items[0][4]+"##l \r\n#L5#Trade your #i"+items[0][4]+"#, "+leaf5+", and "+price5+" for a #i"+items[0][5]+"##l \r\n#L6#Trade your #i"+items[0][5]+"#, "+leaf6+", and "+price6+" for a #i"+items[0][6]+"##l \r\n#L7#Trade your #i"+items[0][6]+"#, "+leaf7+", and "+price7+" for a #i"+items[0][7]+"##l \r\n#L8#Trade your #i"+items[0][7]+"#, "+leaf8+", and "+price8+" for a #i"+items[0][8]+"##l \r\n#L9#Trade your #i"+items[0][8]+"#, "+leaf9+", and "+price9+" for a #i"+items[0][9]+"##l \r\n#L10#Trade your #i"+items[0][9]+"#, "+leaf10+", and "+price10+" for a #i"+items[0][10]+"##l \r\n#L11#Trade your #i"+items[0][10]+"#, "+leaf11+", and "+price11+" for a #i"+items[0][11]+"##l \r\n#L12#Trade your #i"+items[0][11]+"#, "+leaf12+", and "+price12+" for a #i"+items[0][12]+"##l \r\n#L13#Trade your #i"+items[0][12]+"#, "+leaf13+", and "+price13+" for a #i"+items[0][13]+"##l \r\n#L14#Trade your #i"+items[0][13]+"#, "+leaf14+", and "+price14+" for a #i"+items[0][14]+"##l \r\n#L15#Trade your #i"+items[0][14]+"#, "+leaf15+", and "+price15+" for a #i"+items[0][15]+"##l \r\n#L16#Trade your #i"+items[0][15]+"#, "+leaf16+", and "+price16+" for a #i"+items[0][16]+"##l \r\n#L17#Trade your #i"+items[0][16]+"#, "+leaf17+", and "+price17+" for a #i"+items[0][17]+"##l \r\n#L18#Trade your #i"+items[0][17]+"#, "+leaf18+", and "+price18+" for a #i"+items[0][18]+"##l \r\n#L19#Trade your #i"+items[0][18]+"#, "+leaf19+", and "+price19+" for a #i"+items[0][19]+"##l");
        }
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
        }else{
            cm.sendOk("#e#rDejaVu says:#k Go f*** yourself xD");
            cm.dispose();
        }
    }else{
        cm.sendOk("#e#rDejaVu says:#k Go f*** yourself xD");
        cm.dispose();
    }
}  