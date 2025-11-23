package eu.midnightdust.swordblocking.ducks;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public interface ArmedItemStackData {
    ItemStack swordblocking$getItemHeldByArm(HumanoidArm humanoidArm);

    void swordblocking$setItemHeldByArm(HumanoidArm arm, ItemStack itemStack);
}
