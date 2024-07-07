package eu.midnightdust.swordblocking.fabric;

import eu.midnightdust.swordblocking.SwordBlockingClient;
import net.fabricmc.api.ClientModInitializer;

public class SwordBlockingClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SwordBlockingClient.init();
    }
}
