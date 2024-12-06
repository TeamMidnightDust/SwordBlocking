package eu.midnightdust.swordblocking.mixin;

import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer {
    @Environment(EnvType.CLIENT)
    @Inject(method = "getArmPose(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;", at = @At(value = "RETURN"), cancellable = true)
    private static void swordBlocking$getArmPose(PlayerEntity player, ItemStack stack, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (SwordBlockingConfig.enabled) {
            ItemStack handStack = player.getStackInHand(hand);
            ItemStack offStack = player.getStackInHand(hand.equals(Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND);
            if (!SwordBlockingConfig.alwaysHideShield && (handStack.getItem() instanceof ShieldItem) && !SwordBlockingClient.canShieldSwordBlock(player))
                return;
            if (offStack.getItem() instanceof ShieldItem && SwordBlockingClient.isEntityBlocking(player)) {
                cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
            } else if (handStack.getItem() instanceof ShieldItem && SwordBlockingConfig.hideShield && (cir.getReturnValue() == BipedEntityModel.ArmPose.ITEM || cir.getReturnValue() == BipedEntityModel.ArmPose.BLOCK)) {
                cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
            }
        }
    }
}
