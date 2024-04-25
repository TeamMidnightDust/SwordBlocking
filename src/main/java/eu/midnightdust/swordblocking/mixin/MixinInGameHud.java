package eu.midnightdust.swordblocking.mixin;

import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"), method = "renderHotbar")
    public ItemStack swordblocking$hideOffHandSlot(PlayerEntity player) {
        ItemStack realStack = player.getOffHandStack();
        return (SwordBlockingConfig.enabled && SwordBlockingConfig.hideOffhandSlot && realStack.getItem() instanceof ShieldItem) ? ItemStack.EMPTY : realStack;
    }
}
