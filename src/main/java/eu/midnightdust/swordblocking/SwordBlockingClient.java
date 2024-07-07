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
        if (!SwordBlockingConfig.enabled)
            return false;
        Item mainItem = entity.getMainHandStack().getItem();
        Item offItem = entity.getOffHandStack().getItem();
        return ((mainItem instanceof SwordItem || mainItem instanceof AxeItem || mainItem instanceof MaceItem) && offItem instanceof ShieldItem)
                || ((offItem instanceof SwordItem || offItem instanceof AxeItem || offItem instanceof MaceItem) && mainItem instanceof ShieldItem);
    }
    public static boolean isBlockingOnViaVersion(LivingEntity entity) {
        Item item = entity.getMainHandStack().getItem() instanceof SwordItem ? entity.getMainHandStack().getItem() : entity.getOffHandStack().getItem();
        return item instanceof SwordItem && item.getComponents() != null && item.getComponents().contains(DataComponentTypes.FOOD) && Objects.requireNonNull(item.getComponents().get(DataComponentTypes.FOOD)).eatSeconds() == 3600;
    }
}
