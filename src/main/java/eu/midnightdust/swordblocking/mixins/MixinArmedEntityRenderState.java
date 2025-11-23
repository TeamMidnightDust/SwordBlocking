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
    private ItemStack swordblocking$leftStack = ItemStack.EMPTY;

    @Unique
    private ItemStack swordblocking$rightStack = ItemStack.EMPTY;

    @Inject(method = "extractArmedEntityRenderState", at = @At("TAIL"))
    private static void swordBlocking$storeRequiredData(LivingEntity livingEntity, ArmedEntityRenderState armedEntityRenderState, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        ArmedItemStackData armedItemStackData = (ArmedItemStackData) armedEntityRenderState;
        armedItemStackData.swordblocking$setItemHeldByArm(HumanoidArm.LEFT, livingEntity.getItemHeldByArm(HumanoidArm.LEFT));
        armedItemStackData.swordblocking$setItemHeldByArm(HumanoidArm.RIGHT, livingEntity.getItemHeldByArm(HumanoidArm.RIGHT));
    }

    @Override
    public ItemStack swordblocking$getItemHeldByArm(HumanoidArm arm) {
        if (arm == HumanoidArm.LEFT) {
            return swordblocking$leftStack;
        } else if (arm == HumanoidArm.RIGHT) {
            return swordblocking$rightStack;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void swordblocking$setItemHeldByArm(HumanoidArm arm, ItemStack itemStack) {
        if (arm == HumanoidArm.LEFT) {
            swordblocking$leftStack = itemStack;
        } else if (arm == HumanoidArm.RIGHT) {
            swordblocking$rightStack = itemStack;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
