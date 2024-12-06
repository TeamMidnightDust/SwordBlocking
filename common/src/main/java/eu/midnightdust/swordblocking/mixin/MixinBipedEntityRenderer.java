package eu.midnightdust.swordblocking.mixin;

import eu.midnightdust.swordblocking.SwordBlockingClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityRenderer.class)
public abstract class MixinBipedEntityRenderer<T extends MobEntity, S extends BipedEntityRenderState, M extends BipedEntityModel<S>> {
    @Inject(method = "updateBipedRenderState", at = @At("TAIL"))
    private static void swordBlocking$storeEntity(LivingEntity entity, BipedEntityRenderState state, float tickDelta, ItemModelManager itemModelResolver, CallbackInfo ci) {
        SwordBlockingClient.RENDER_STATE_TO_ENTITY_MAP.put(state, entity);
    }
}
