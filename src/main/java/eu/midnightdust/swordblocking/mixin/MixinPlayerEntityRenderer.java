package eu.midnightdust.swordblocking.mixin;

import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer {
    @Inject(at = @At(value = "RETURN"), method = "getArmPose", cancellable = true)
    @Environment(EnvType.CLIENT)
    private static void swordblocking$getArmPose(AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (!SwordBlockingConfig.enabled) return;

        ItemStack handStack = abstractClientPlayerEntity.getStackInHand(hand);
        ItemStack offStack = abstractClientPlayerEntity.getStackInHand(hand.equals(Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND);
        if (!SwordBlockingConfig.alwaysHideShield && (handStack.getItem() instanceof ShieldItem) && !SwordBlockingClient.canWeaponBlock(abstractClientPlayerEntity))
            return;

        if (offStack.getItem() instanceof ShieldItem && abstractClientPlayerEntity.isUsingItem()) {
            cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
        } else if (handStack.getItem() instanceof ShieldItem && SwordBlockingConfig.hideShield) {
            cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
        }
    }
}
