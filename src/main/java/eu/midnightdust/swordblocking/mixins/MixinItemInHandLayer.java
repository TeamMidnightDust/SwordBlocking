package eu.midnightdust.swordblocking.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import eu.midnightdust.swordblocking.ducks.ArmedItemStackData;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandLayer.class)
public abstract class MixinItemInHandLayer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel> {
    @Redirect(
            //? >=1.21.10 {
            method = "submitArmWithItem",
            //? } else {
            /*method = "renderArmWithItem",
            *///? }
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;isEmpty()Z")
    )
    private boolean swordBlocking$hideShield(ItemStackRenderState instance, @Local(argsOnly = true) S renderState, @Local(argsOnly = true) HumanoidArm arm) {
        if (SwordBlockingConfig.enabled) {
            final ArmedItemStackData armedItemStackData = (ArmedItemStackData) renderState;
            return SwordBlockingClient.shouldHideShield(armedItemStackData.swordblocking$getMainHandItem(), armedItemStackData.swordblocking$getOffHandItem(), armedItemStackData.swordblocking$getItemHeldByArm(arm));
        }

        return instance.isEmpty();
    }
}
