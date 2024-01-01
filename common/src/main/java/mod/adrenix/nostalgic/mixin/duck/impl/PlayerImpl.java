package mod.adrenix.nostalgic.mixin.duck.impl;

import mod.adrenix.nostalgic.mixin.duck.SlotTracker;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerImpl extends LivingEntity implements SlotTracker
{
    private PlayerImpl(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    @Shadow private ItemStack lastItemInMainHand;

    @Unique public int NT$lastSlot = -1;
    @Unique public boolean NT$reequip = false;

    @Override
    public void NT$setLastSlot(int slot)
    {
        this.NT$lastSlot = slot;
    }

    @Override
    public void NT$setReequip(boolean state)
    {
        this.NT$reequip = state;
    }

    @Override
    public int NT$getLastSlot()
    {
        return this.NT$lastSlot;
    }

    @Override
    public boolean NT$getReequip()
    {
        return this.NT$reequip;
    }

    @Override
    public ItemStack NT$getLastItem()
    {
        return this.lastItemInMainHand;
    }
}
