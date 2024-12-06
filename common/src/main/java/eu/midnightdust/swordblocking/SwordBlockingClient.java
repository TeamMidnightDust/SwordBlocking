package eu.midnightdust.swordblocking;

import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;

import java.util.HashMap;
import java.util.Map;

public class SwordBlockingClient {
    public static final String MOD_ID = "swordblocking";
    public static Map<BipedEntityRenderState, LivingEntity> RENDER_STATE_TO_ENTITY_MAP = new HashMap<>();

    public static void init() {
        SwordBlockingConfig.init(MOD_ID, SwordBlockingConfig.class);
    }

    public static boolean isEntityBlocking(LivingEntity entity) {
        return SwordBlockingConfig.enabled && entity.isUsingItem() && canShieldSwordBlock(entity);
    }

    public static boolean canShieldSwordBlock(LivingEntity entity) {
        if (SwordBlockingConfig.enabled && (entity.getOffHandStack().getItem() instanceof ShieldItem || entity.getMainHandStack().getItem() instanceof ShieldItem)) {
            Item weaponItem = entity.getOffHandStack().getItem() instanceof ShieldItem ? entity.getMainHandStack().getItem() : entity.getOffHandStack().getItem();
            return weaponItem instanceof SwordItem || weaponItem instanceof AxeItem || weaponItem instanceof MaceItem;
        } else {
            return false;
        }
    }

    public static boolean shouldHideShield(LivingEntity entity, ItemStack stack) {
        return SwordBlockingConfig.enabled && (SwordBlockingConfig.alwaysHideShield && SwordBlockingConfig.hideShield && stack.getItem() instanceof ShieldItem)
                || (SwordBlockingConfig.hideShield && stack.getItem() instanceof ShieldItem && SwordBlockingClient.canShieldSwordBlock(entity));
    }
}
