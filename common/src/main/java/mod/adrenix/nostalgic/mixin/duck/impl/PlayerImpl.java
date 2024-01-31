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

    @Unique public int nt$lastSlot = -1;
    @Unique public boolean nt$reequip = false;

    @Override
    public void nt$setLastSlot(int slot)
    {
        this.nt$lastSlot = slot;
    }

    @Override
    public void nt$setReequip(boolean state)
    {
        this.nt$reequip = state;
    }

    @Override
    public int nt$getLastSlot()
    {
        return this.nt$lastSlot;
    }

    @Override
    public boolean nt$getReequip()
    {
        return this.nt$reequip;
    }

    @Override
    public ItemStack nt$getLastItem()
    {
        return this.lastItemInMainHand;
    }
}
