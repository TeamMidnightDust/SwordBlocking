package eu.midnightdust.swordblocking.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class MixinGui {
    @ModifyExpressionValue(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getOffhandItem()Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack swordBlocking$hideOffHandSlot(ItemStack original) {
        if (SwordBlockingConfig.enabled && SwordBlockingConfig.hideOffhandSlot && original.getItem() instanceof ShieldItem) {
            return ItemStack.EMPTY;
        } else {
            return original;
        }
    }
}
