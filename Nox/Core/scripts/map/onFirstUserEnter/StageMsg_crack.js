function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId) {
        case 922010100:
            api.startMapEffect("Defeat all the Ratz!", 5120018, false);
            break;
        case 922010200:
            api.startMapEffect("Collect all the passes!", 5120018, false);
            break;
        case 922010300:
            api.startMapEffect("Destroy the monsters!", 5120018, false);
            break;
        case 922010400:
            api.startMapEffect("Destroy the monsters in each room!", 5120018, false);
            break;
        case 922010500:
            api.startMapEffect("Collect passes from each room!", 5120018, false);
            break;
        case 922010600:
            api.startMapEffect("Get to the top!", 5120018, false);
            break;
        case 922010700:
            api.startMapEffect("Destroy the Rombots!", 5120018, false);
            break;
        case 922010800:
            api.startMapEffect("Get the right combination!", 5120018, false);
            break;
        case 922010900:
            api.startMapEffect("Defeat Alishar!", 5120018, false);
            break;
    }
}