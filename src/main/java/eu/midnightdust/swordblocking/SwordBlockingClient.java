package eu.midnightdust.swordblocking;

import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.MaceItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;

import java.util.Objects;

public class SwordBlockingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SwordBlockingConfig.init("swordblocking", SwordBlockingConfig.class);
    }

    public static boolean isWeaponBlocking(LivingEntity entity) {
        return entity.isUsingItem() && (canWeaponBlock(entity) || isBlockingOnViaVersion(entity));
    }

    public static boolean canWeaponBlock(LivingEntity entity) {
        if (SwordBlockingConfig.enabled && (entity.getOffHandStack().getItem() instanceof ShieldItem || entity.getMainHandStack().getItem() instanceof ShieldItem)) {
            Item weaponItem = entity.getOffHandStack().getItem() instanceof ShieldItem ? entity.getMainHandStack().getItem() : entity.getOffHandStack().getItem();
            return weaponItem instanceof SwordItem || weaponItem instanceof AxeItem || weaponItem instanceof MaceItem;
        }
        return false;
    }
    public static boolean isBlockingOnViaVersion(LivingEntity entity) {
        Item item = entity.getMainHandStack().getItem() instanceof SwordItem ? entity.getMainHandStack().getItem() : entity.getOffHandStack().getItem();
        return item instanceof SwordItem && item.getComponents() != null && item.getComponents().contains(DataComponentTypes.FOOD) && Objects.requireNonNull(item.getComponents().get(DataComponentTypes.FOOD)).eatSeconds() == 3600;
    }
}
