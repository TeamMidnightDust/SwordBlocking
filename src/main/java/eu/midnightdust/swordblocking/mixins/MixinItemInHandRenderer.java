package eu.midnightdust.swordblocking.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress);

    @WrapMethod(method = "renderItem")
    public void swordBlocking$hideShield(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Operation<Void> original) {
        if (!SwordBlockingClient.shouldHideShield(entity.getMainHandItem(), entity.getOffhandItem(), stack)) {
            original.call(entity, stack, displayContext, poseStack, bufferSource, packedLight);
        }
    }

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getUsedItemHand()Lnet/minecraft/world/InteractionHand;", ordinal = 1))
    private InteractionHand swordBlocking$changeActiveHand(AbstractClientPlayer instance) {
        final InteractionHand activeHand = instance.getUsedItemHand();
        if (SwordBlockingClient.canEntityBlock(instance.getMainHandItem(), instance.getOffhandItem())) {
            return swordBlocking$getBlockingHand(activeHand);
        } else {
            return activeHand;
        }
    }

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/ItemUseAnimation;"))
    private ItemUseAnimation swordBlocking$changeItemAction(ItemStack stack, @Local(argsOnly = true) AbstractClientPlayer player, @Local(argsOnly = true) InteractionHand interactionHand) {
        final ItemUseAnimation defaultUseAction = stack.getUseAnimation();
        if (SwordBlockingClient.canEntityBlock(player.getMainHandItem(), player.getOffhandItem())) {
            return swordBlocking$getBlockingHand(player.getUsedItemHand()) == interactionHand ? ItemUseAnimation.BLOCK : defaultUseAction;
        } else {
            return defaultUseAction;
        }
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 3, shift = At.Shift.AFTER))
    private void swordBlocking$applySwingOffset(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci, @Local HumanoidArm arm) {
        if (SwordBlockingConfig.enabled && SwordBlockingConfig.blockHitAnimation) {
            this.applyItemArmAttackTransform(poseStack, arm, swingProgress);
        }
    }

    @WrapMethod(method = "itemUsed")
    private void swordBlocking$disableEquipAnimation(InteractionHand hand, Operation<Void> original) {
        if (!SwordBlockingConfig.disableUseEquipAnimation || this.minecraft.player == null || !this.minecraft.player.isUsingItem()) {
            original.call(hand);
        }
    }

    @Unique
    private InteractionHand swordBlocking$getBlockingHand(InteractionHand interactionHand) {
        return interactionHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
}
