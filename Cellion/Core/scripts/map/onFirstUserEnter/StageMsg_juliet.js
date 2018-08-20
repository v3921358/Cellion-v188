function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId) {
        case 926110000:
            api.startMapMessageEffect("There's a rumor running that claims strange sounds can sometimes be heard from this laboratory. I'm sure there's something here!! Please search the perimeter thoroughly.", 2112005, false);
            break;
        case 926110001:
            api.startMapEffect("Please defeat all the monsters!", 5120022, false);
            break;
        case 926110100:
            api.startMapEffect("Please defeat the monster and fill the broken beaker with the liquid you have acquired!", 5120022, false);
            break;
        case 926110200:
            api.startMapEffect("Please find the materials for the experiments in the Dark Lab after acquiring the keycard from the monster.", 5120022, false);
            break;
        case 926110300:
            api.startMapEffect("Please go through the 4 secret passages!", 5120022, false);
            break;
        case 926110301:
        case 926110302:
        case 926110303:
        case 926110304:
            api.startMapEffect("Find the correct platform with the correct answer and go up to the top of the passage way!", 5120022, false);
            break;
    }
}