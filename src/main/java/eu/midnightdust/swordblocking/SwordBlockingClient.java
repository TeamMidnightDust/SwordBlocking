package eu.midnightdust.swordblocking;

import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class SwordBlockingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SwordBlockingConfig.init("swordblocking", SwordBlockingConfig.class);

        for (Item item : Registries.ITEM) {
            if (!(item instanceof SwordItem || item instanceof AxeItem))
                continue;
            ModelPredicateProviderRegistry.register(item, new Identifier("blocking"),
                    (stack, world, entity, seed) -> entity != null && isWeaponBlocking(entity) ? 1.0F : 0.0F);
        }

        FabricLoader.getInstance().getModContainer("swordblocking").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("swordblocking", "blocking_predicates"), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
        });
    }

    public static boolean isWeaponBlocking(LivingEntity entity) {
        return (entity.isUsingItem() && canWeaponBlock(entity));
    }

    public static boolean canWeaponBlock(LivingEntity entity) {
        return (SwordBlockingConfig.enabled && (entity.getMainHandStack().getItem() instanceof SwordItem || entity.getMainHandStack().getItem() instanceof AxeItem) &&
                entity.getOffHandStack().getItem() instanceof ShieldItem) ||
                ((entity.getOffHandStack().getItem() instanceof SwordItem || entity.getOffHandStack().getItem() instanceof AxeItem) &&
                        entity.getMainHandStack().getItem() instanceof ShieldItem);
    }
}
