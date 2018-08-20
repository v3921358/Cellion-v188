function enter(api) {
    var currentMapId = api.getMapId();

    switch ((currentMapId / 100) % 10) {
        case 1:
            api.startMapEffect("Can you hear me? Could you eliminate all the enraged monsters?", 5120052, false);
            break;
        case 2:
            api.startMapEffect("I...can't hold... my breath much longer. Get me some Air Bubbles!", 5120052, false);
            break;
        case 3:
            api.startMapEffect("Here! I'm here! I was trapped by those furious sea creatures. Thank you so much for coming.", 5120052, false);
            break;
        case 4:
            api.startMapEffect("Do you see those humongous fish over there?", 5120052, false);
            break;
    }
}