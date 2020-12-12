package eu.midnightdust.swordblocking;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SwordBlockingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Registry.ITEM.forEach((item) -> {
            if(item instanceof SwordItem) {
                FabricModelPredicateProviderRegistry.register(item, new Identifier("blocking"), (stack, world, entity) ->
                        entity != null && entity.getOffHandStack().getItem().equals(Items.SHIELD) && entity.isUsingItem() ? 1.0F : 0.0F);
            }
        });
    }
}
