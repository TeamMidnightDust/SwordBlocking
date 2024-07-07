package eu.midnightdust.swordblocking.neoforge;

import eu.midnightdust.swordblocking.SwordBlockingClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

import static eu.midnightdust.swordblocking.SwordBlockingClient.MOD_ID;

@SuppressWarnings("all")
@Mod(value = MOD_ID, dist = Dist.CLIENT)
public class SwordBlockingClientNeoForge {
    public SwordBlockingClientNeoForge() {
        SwordBlockingClient.init();
    }
}