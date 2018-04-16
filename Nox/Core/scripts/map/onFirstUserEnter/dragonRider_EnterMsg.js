function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId) {
        case 240080100:
            api.startMapEffect("Defeat all Soaring Hawks and Soaring Eagles!", 5120026, false);
            break;
        case 240080200:
            api.startMapEffect("Defeat all Wyverns and Griffeys!", 5120026, false);
            break;
        case 240080300:
            api.startMapEffect("Defeat Dragonoir and enter the Crimson Sky Nest!", 5120026, false);
            break;
        case 240080400:
            api.startMapEffect("All party members must overcome the obstacles and enter the Crimson Sky Nest within 3 minutes!", 5120026, false);
            break;
        case 240080500:
            api.startMapEffect("Defeat the Dragon Rider that are wreaking havoc on Minar!", 5120026, false);
            break;
    }
}