package mod.adrenix.nostalgic.mixin.duck.impl;

import mod.adrenix.nostalgic.mixin.duck.SwingBlocker;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerImpl implements SwingBlocker
{
    @Unique private boolean nt$swingBlocked = false;

    @Override
    public void nt$setSwingBlocked(boolean state)
    {
        this.nt$swingBlocked = state;
    }

    @Override
    public boolean nt$isSwingBlocked()
    {
        return this.nt$swingBlocked;
    }
}
