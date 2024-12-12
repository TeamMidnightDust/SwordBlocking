package eu.midnightdust.swordblocking.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress);

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    public void swordBlocking$hideShield(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (SwordBlockingClient.shouldHideShield(entity, stack)) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getActiveHand()Lnet/minecraft/util/Hand;", ordinal = 1))
    private Hand swordBlocking$changeActiveHand(AbstractClientPlayerEntity player) {
        Hand activeHand = player.getActiveHand();
        if (SwordBlockingClient.isEntityBlocking(player)) {
            return swordBlocking$getBlockingHand(activeHand);
        } else {
            return activeHand;
        }
    }

    @Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/item/consume/UseAction;"))
    private UseAction swordBlocking$changeItemAction(ItemStack stack, @Local(argsOnly = true) AbstractClientPlayerEntity player, @Local(argsOnly = true) Hand hand) {
        UseAction defaultUseAction = stack.getUseAction();
        if (SwordBlockingClient.isEntityBlocking(player)) {
            return swordBlocking$getBlockingHand(player.getActiveHand()) == hand ? UseAction.BLOCK : defaultUseAction;
        } else {
            return defaultUseAction;
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V", ordinal = 3, shift = At.Shift.AFTER))
    private void swordBlocking$applySwingOffset(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci, @Local Arm arm) {
        if (SwordBlockingConfig.enabled && SwordBlockingConfig.blockHitAnimation) {
            applySwingOffset(matrices, arm, swingProgress);
        }
    }

    @Inject(method = "resetEquipProgress", at = @At("HEAD"), cancellable = true)
    private void swordBlocking$disableEquipAnimation(Hand hand, CallbackInfo ci) {
        if (SwordBlockingConfig.disableUseEquipAnimation && this.client.player != null && this.client.player.isUsingItem()) {
            ci.cancel();
        }
    }

    @Unique
    private Hand swordBlocking$getBlockingHand(Hand activeHand) {
        return activeHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }
}
