package eu.midnightdust.swordblocking;

import eu.midnightdust.swordblocking.config.SwordBlockingConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

//? fabric {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
*///?}

//? fabric
@Entrypoint
//? neoforge
/*@Mod(value = SwordBlockingClient.MOD_ID, dist = Dist.CLIENT)*/
public final class SwordBlockingClient
        //? fabric
        implements ClientModInitializer
{
    public static final String MOD_ID = "@MODID@";

    // TODO/NOTE: I know this can be condensed more but i'm tired so will recheck later
    public static boolean canEntityBlock(ItemStack mainHandStack, ItemStack offHandStack) {
        return SwordBlockingConfig.enabled && canShieldSwordBlock(mainHandStack, offHandStack);
    }

    // TODO/NOTE: I know this can be condensed more but i'm tired so will recheck later
    public static boolean canShieldSwordBlock(ItemStack mainHandStack, ItemStack offHandStack) {
        if (SwordBlockingConfig.enabled && (offHandStack.getItem() instanceof ShieldItem || mainHandStack.getItem() instanceof ShieldItem)) {
            final Item weaponItem = offHandStack.getItem() instanceof ShieldItem ? mainHandStack.getItem() : offHandStack.getItem();
            return weaponItem.components().has(DataComponents.DAMAGE);
        } else {
            return false;
        }
    }

    // TODO/NOTE: I know this can be condensed more but i'm tired so will recheck later
    public static boolean shouldHideShield(ItemStack mainHandStack, ItemStack offHandStack, ItemStack stack) {
        if (SwordBlockingConfig.enabled && stack.getItem() instanceof ShieldItem) {
            return (SwordBlockingConfig.alwaysHideShield && SwordBlockingConfig.hideShield) ||
                    (SwordBlockingConfig.hideShield && SwordBlockingClient.canShieldSwordBlock(mainHandStack, offHandStack));
        } else {
            return false;
        }
  }

    private void initialize() {
        SwordBlockingConfig.init(MOD_ID, SwordBlockingConfig.class);
    }

    //? neoforge {
    /*public SwordBlockingClient() {
        initialize();
    }
    *///?} else {
    @Override
    public void onInitializeClient() {
        initialize();
    }
    //?}
}