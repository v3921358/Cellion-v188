function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId % 10) {
        case 0:
            api.startMapEffect("Eliminate all the monsters!", 5120033, false);
            break;
        case 1:
            api.startMapEffect("Break the boxes and eliminate the monsters!", 5120033, false);
            break;
        case 2:
            api.startMapEffect("Eliminate the Officer!", 5120033, false);
            break;
        case 3:
            api.startMapEffect("Eliminate all the monsters!", 5120033, false);
            break;
        case 4:
            api.startMapEffect("Find the way to the other side!", 5120033, false);
            break;
    }
}