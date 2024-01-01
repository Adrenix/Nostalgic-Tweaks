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

    @Unique private float NT$cameraPitch = 0.0F;
    @Unique private float NT$prevCameraPitch = 0.0F;

    @Override
    public void NT$setCameraPitch(float cameraPitch)
    {
        this.NT$cameraPitch = cameraPitch;
    }

    @Override
    public void NT$setPrevCameraPitch(float prevCameraPitch)
    {
        this.NT$prevCameraPitch = prevCameraPitch;
    }

    @Override
    public float NT$getCameraPitch()
    {
        return this.NT$cameraPitch;
    }

    @Override
    public float NT$getPrevCameraPitch()
    {
        return this.NT$prevCameraPitch;
    }
}
