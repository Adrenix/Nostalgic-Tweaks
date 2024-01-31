package mod.adrenix.nostalgic.mixin.duck.impl;

import mod.adrenix.nostalgic.mixin.duck.CameraPitching;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityImpl extends Entity implements CameraPitching
{
    private LivingEntityImpl(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    @Unique private float nt$cameraPitch = 0.0F;
    @Unique private float nt$prevCameraPitch = 0.0F;

    @Override
    public void nt$setCameraPitch(float cameraPitch)
    {
        this.nt$cameraPitch = cameraPitch;
    }

    @Override
    public void nt$setPrevCameraPitch(float prevCameraPitch)
    {
        this.nt$prevCameraPitch = prevCameraPitch;
    }

    @Override
    public float nt$getCameraPitch()
    {
        return this.nt$cameraPitch;
    }

    @Override
    public float nt$getPrevCameraPitch()
    {
        return this.nt$prevCameraPitch;
    }
}
