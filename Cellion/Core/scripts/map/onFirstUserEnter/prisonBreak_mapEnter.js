function enter(api) {
    var currentMapId = api.getMapId();

    switch (currentMapId) {
        case 921160100:
            api.startMapEffect("Shhh! You must escape the tower by quietly avoiding the obstacles.", 5120053, false);
            break;
        case 921160200:
            api.startMapEffect("You must defeat all guards. Otherwise they will call other guards, and that is bad.", 5120053, false);
            break;
        case 921160300:
            api.startMapEffect("They've created a maze to keep people from entering or escaping. You must find the door that leads to the Aerial Prison!", 5120053, false);
            break;
        case 921160400:
            api.startMapEffect("Defeat all guards that are defending the door!", 5120053, false);
            break;
        case 921160500:
            api.startMapEffect("This is the last obstacle. Please press on to the Aerial Prison.", 5120053, false);
            break;
        case 921160600:
            api.startMapEffect("Open the prison door by defeating the guard and recovering the prison key.", 5120053, false);
            break;
        case 921160700:
            api.startMapEffect("Help us free by defeating the prison guard!", 5120053, false);
            break;
    }
}