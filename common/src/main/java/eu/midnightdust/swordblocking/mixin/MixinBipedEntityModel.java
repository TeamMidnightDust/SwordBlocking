package eu.midnightdust.swordblocking.mixin;

import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class MixinBipedEntityModel<T extends BipedEntityRenderState> {
    @Shadow
    protected abstract void positionRightArm(T entityRenderState, BipedEntityModel.ArmPose armPose);

    @Shadow
    protected abstract void positionLeftArm(T entityRenderState, BipedEntityModel.ArmPose armPose);

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;F)V", shift = At.Shift.BEFORE))
    private void swordBlocking$setBlockingAngles(T bipedEntityRenderState, CallbackInfo ci) {
        LivingEntity livingEntity = SwordBlockingClient.RENDER_STATE_TO_ENTITY_MAP.get(bipedEntityRenderState);
        if (!SwordBlockingConfig.enabled || livingEntity == null || !SwordBlockingClient.isEntityBlocking(livingEntity))
            return;
        if (livingEntity.getOffHandStack().getItem() instanceof ShieldItem)
            this.positionRightArm(bipedEntityRenderState, BipedEntityModel.ArmPose.BLOCK);
        else
            this.positionLeftArm(bipedEntityRenderState, BipedEntityModel.ArmPose.BLOCK);
    }

    @Redirect(method = "positionBlockingArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
    private float swordBlocking$lockArmPosition(float value, float min, float max) {
        if (SwordBlockingConfig.enabled && SwordBlockingConfig.lockBlockingArmPosition) {
            return 0F;
        } else {
            return value;
        }
    }
}
