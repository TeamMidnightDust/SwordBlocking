package eu.midnightdust.swordblocking;

import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;

public class SwordBlockingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SwordBlockingConfig.init("swordblocking", SwordBlockingConfig.class);
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
