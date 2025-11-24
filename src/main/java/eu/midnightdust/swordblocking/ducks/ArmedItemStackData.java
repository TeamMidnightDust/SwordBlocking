package eu.midnightdust.swordblocking.ducks;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public interface ArmedItemStackData {
    ItemStack swordblocking$getItemHeldByArm(HumanoidArm humanoidArm);

    ItemStack swordblocking$getOffHandItem();

    void swordblocking$setOffHandItem(ItemStack stack);

    ItemStack swordblocking$getMainHandItem();

    void swordblocking$setMainHandItem(ItemStack stack);
}
