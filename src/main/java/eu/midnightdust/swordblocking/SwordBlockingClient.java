package eu.midnightdust.swordblocking;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class SwordBlockingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (Item item : Registry.ITEM) {
            if (item instanceof SwordItem) {
                FabricModelPredicateProviderRegistry.register(item, new Identifier("blocking"), new UnclampedModelPredicateProvider() {
                    @Override
                    public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
                        return entity != null && entity.getOffHandStack().getItem().equals(Items.SHIELD) && entity.isUsingItem() ? 1.0F : 0.0F;
                    }
                });
            }
        }
    }
}
