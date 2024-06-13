package eu.midnightdust.swordblocking;

import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;

public class SwordBlockingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SwordBlockingConfig.init("swordblocking", SwordBlockingConfig.class);
    }

    public static boolean isWeaponBlocking(LivingEntity entity) {
        return entity.isUsingItem() && canWeaponBlock(entity);
    }

    public static boolean canWeaponBlock(LivingEntity entity) {
        if (!SwordBlockingConfig.enabled)
            return false;
        Item mainItem = entity.getMainHandStack().getItem();
        Item offItem = entity.getOffHandStack().getItem();
        return ((mainItem instanceof SwordItem || mainItem instanceof AxeItem || mainItem instanceof MaceItem) && offItem instanceof ShieldItem)
                || ((offItem instanceof SwordItem || offItem instanceof AxeItem || offItem instanceof MaceItem) && mainItem instanceof ShieldItem);
    }
}
