package eu.midnightdust.swordblocking.mixins;

import eu.midnightdust.swordblocking.ducks.ArmedItemStackData;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmedEntityRenderState.class)
public abstract class MixinArmedEntityRenderState implements ArmedItemStackData {
    @Unique
    private ItemStack swordblocking$offHandStack = ItemStack.EMPTY;

    @Unique
    private ItemStack swordblocking$mainHandStack = ItemStack.EMPTY;

    @Inject(method = "extractArmedEntityRenderState", at = @At("TAIL"))
    private static void swordBlocking$storeRequiredData(LivingEntity livingEntity, ArmedEntityRenderState armedEntityRenderState, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        ArmedItemStackData armedItemStackData = (ArmedItemStackData) armedEntityRenderState;
        armedItemStackData.swordblocking$setOffHandItem(livingEntity.getOffhandItem());
        armedItemStackData.swordblocking$setMainHandItem(livingEntity.getMainHandItem());
    }

    @Override
    public ItemStack swordblocking$getItemHeldByArm(HumanoidArm arm) {
        if (arm == HumanoidArm.LEFT) {
            return swordblocking$offHandStack;
        } else if (arm == HumanoidArm.RIGHT) {
            return swordblocking$mainHandStack;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public ItemStack swordblocking$getOffHandItem() {
        return this.swordblocking$offHandStack;
    }

    @Override
    public void swordblocking$setOffHandItem(ItemStack stack) {
        this.swordblocking$offHandStack = stack;
    }

    @Override
    public ItemStack swordblocking$getMainHandItem() {
        return this.swordblocking$mainHandStack;
    }

    @Override
    public void swordblocking$setMainHandItem(ItemStack stack) {
        this.swordblocking$mainHandStack = stack;
    }
}
