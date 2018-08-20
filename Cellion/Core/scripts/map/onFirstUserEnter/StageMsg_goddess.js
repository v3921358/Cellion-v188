function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId) {
        case 920010000:
            api.startMapEffect("Please save me by collecting Cloud Pieces!", 5120019, false);
            break;
        case 920010100:
            api.startMapEffect("Bring all the pieces here to save Minerva!", 5120019, false);
            break;
        case 920010200:
            api.startMapEffect("Destroy the monsters and gather Statue Pieces!", 5120019, false);
            break;
        case 920010300:
            api.startMapEffect("Destroy the monsters in each room and gather Statue Pieces!", 5120019, false);
            break;
        case 920010400:
            api.startMapEffect("Play the correct LP of the day!", 5120019, false);
            break;
        case 920010500:
            api.startMapEffect("Find the correct combination!", 5120019, false);
            break;
        case 920010600:
            api.startMapEffect("Destroy the monsters and gather Statue Pieces!", 5120019, false);
            break;
        case 920010700:
            api.startMapEffect("Get the right combination once you get to the top!", 5120019, false);
            break;
        case 920010800:
            api.startMapEffect("Summon and defeat Papa Pixie!", 5120019, false);
            break;
    }
}