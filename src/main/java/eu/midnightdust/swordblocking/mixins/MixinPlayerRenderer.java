package eu.midnightdust.swordblocking.mixins;

import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? fabric {
/*import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
*///? }

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer {
    //? fabric
    /*@Environment(EnvType.CLIENT)*/
    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At(value = "RETURN"), cancellable = true)
    private static void swordBlocking$getArmPose(Player player, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (SwordBlockingConfig.enabled) {
            final ItemStack handStack = player.getItemInHand(hand);
            final ItemStack offStack = player.getItemInHand(hand.equals(InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (!SwordBlockingConfig.alwaysHideShield && (handStack.getItem() instanceof ShieldItem) && !SwordBlockingClient.canShieldSwordBlock(player.getMainHandItem(), player.getOffhandItem())) {
                return;
            }

            if (offStack.getItem() instanceof ShieldItem && SwordBlockingClient.canEntityBlock(player.getMainHandItem(), player.getOffhandItem())) {
                cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
            } else if (handStack.getItem() instanceof ShieldItem && SwordBlockingConfig.hideShield && (cir.getReturnValue() == HumanoidModel.ArmPose.ITEM || cir.getReturnValue() == HumanoidModel.ArmPose.BLOCK)) {
                cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
            }
        }
    }
}
