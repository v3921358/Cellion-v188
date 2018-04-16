function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId) {
        case 930000000:
            api.startMapEffect("Step in the portal to be transformed.", 5120023, false);
            break;
        case 930000100:
            api.startMapEffect("Defeat the poisoned monsters!", 5120023, false);
            break;
        case 930000200:
            api.startMapEffect("Eliminate the spore that blocks the way by purifying the poison!", 5120023, false);
            break;
        case 930000300:
            api.startMapEffect("Uh oh! The forest is too confusing! Find me, quick!", 5120023, false);
            break;
        case 930000400:
            api.startMapEffect("Purify the monsters by getting Purification Marbles from me!", 5120023, false);
            break;
        case 930000500:
            api.startMapEffect("Find the Purple Magic Stone!", 5120023, false);
            break;
        case 930000600:
            api.startMapEffect("Place the Magic Stone on the altar!", 5120023, false);
            break;
    }
}