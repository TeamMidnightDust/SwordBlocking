package eu.midnightdust.swordblocking.mixins;

import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import eu.midnightdust.swordblocking.ducks.ArmedItemStackData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class MixinHumanoidModel {
    @Shadow
    protected abstract void poseLeftArm(HumanoidRenderState renderState, HumanoidModel.ArmPose pose);

    @Shadow
    protected abstract void poseRightArm(HumanoidRenderState renderState, HumanoidModel.ArmPose pose);

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;F)V", shift = At.Shift.BEFORE))
    private void swordBlocking$setBlockingAngles(HumanoidRenderState renderState, CallbackInfo ci) {
        final ArmedItemStackData armedItemStackData = (ArmedItemStackData) renderState;
        final ItemStack offHandStack = armedItemStackData.swordblocking$getItemHeldByArm(HumanoidArm.LEFT);
        final ItemStack mainHandStack = armedItemStackData.swordblocking$getItemHeldByArm(HumanoidArm.RIGHT);
        if (renderState.isUsingItem && SwordBlockingClient.canEntityBlock(mainHandStack, offHandStack)) {
            if (offHandStack.getItem() instanceof ShieldItem) {
                this.poseRightArm(renderState, HumanoidModel.ArmPose.BLOCK);
            } else {
                this.poseLeftArm(renderState, HumanoidModel.ArmPose.BLOCK);
            }
        }
    }

    @Redirect(method = "poseBlockingArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float swordBlocking$lockArmPosition(float value, float min, float max) {
        if (SwordBlockingConfig.enabled && SwordBlockingConfig.lockBlockingArmPosition) {
            return 0F;
        } else {
            return value;
        }
    }
}
