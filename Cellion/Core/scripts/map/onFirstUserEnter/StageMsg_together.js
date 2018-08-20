function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId) {
        case 103000800:
            api.startMapEffect("Solve the question and gather the amount of passes!", 5120017, false);
            break;
        case 103000801:
            api.startMapEffect("Get on the ropes and unveil the correct combination!", 5120017, false);
            break;
        case 103000802:
            api.startMapEffect("Get on the platforms and unveil the correct combination!", 5120017, false);
            break;
        case 103000803:
            api.startMapEffect("Get on the barrels and unveil the correct combination!", 5120017, false);
            break;
        case 103000804:
            api.startMapEffect("Defeat King Slime and his minions!", 5120017, false);
            break;
    }
}