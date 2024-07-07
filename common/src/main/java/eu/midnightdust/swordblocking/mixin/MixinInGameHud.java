package eu.midnightdust.swordblocking.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"), method = "renderHotbarVanilla")
    public ItemStack swordblocking$hideOffHandSlot(ItemStack original) {
        return (SwordBlockingConfig.enabled && SwordBlockingConfig.hideOffhandSlot && original.getItem() instanceof ShieldItem) ? ItemStack.EMPTY : original;
    }
}
