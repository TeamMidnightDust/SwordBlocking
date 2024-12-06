package eu.midnightdust.swordblocking.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.midnightdust.swordblocking.SwordBlockingClient;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class MixinHeldItemFeatureRenderer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ModelWithArms> {
    @Redirect(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderState;isEmpty()Z"))
    private boolean swordBlocking$hideShield(ItemRenderState instance, @Local(argsOnly = true) Arm arm, @Local(argsOnly = true) S entityState) {
        LivingEntity livingEntity = SwordBlockingClient.RENDER_STATE_TO_ENTITY_MAP.get(entityState);
        if (SwordBlockingConfig.enabled && livingEntity != null) {
            ItemStack itemStack = livingEntity.getStackInHand(arm == Arm.LEFT ? Hand.OFF_HAND : Hand.MAIN_HAND);
            return SwordBlockingClient.shouldHideShield(livingEntity, itemStack);
        }

        return instance.isEmpty();
    }
}
