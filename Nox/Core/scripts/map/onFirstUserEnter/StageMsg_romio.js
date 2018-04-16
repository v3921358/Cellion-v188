function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId) {
        case 926100000:
            api.startMapMessageEffect("There's a rumor running that claims strange sounds can sometimes be heard from this laboratory. I'm sure there's something here!! Please search the perimeter thoroughly.", 2112004, false);
            break;
        case 926100001:
            api.startMapEffect("Please defeat all the monsters!", 5120021, false);
            break;
        case 926100100:
            api.startMapEffect("Please defeat the monster and fill the broken beaker with the liquid you have acquired!", 5120021, false);
            break;
        case 926100200:
            api.startMapEffect("Please find the materials for the experiments in the Dark Lab after acquiring the keycard from the monster.", 5120021, false);
            break;
        case 926100300:
            api.startMapEffect("Please go through the 4 secret passages!", 5120021, false);
            break;
        case 926100301:
        case 926100302:
        case 926100303:
        case 926100304:
            api.startMapEffect("Find the correct platform with the correct answer and go up to the top of the passage way!", 5120021, false);
            break;
    }
}