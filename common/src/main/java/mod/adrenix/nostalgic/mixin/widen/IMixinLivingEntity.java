package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface IMixinLivingEntity
{
    @Accessor("attackStrengthTicker") void NT$setAttackStrengthTicker(int attackStrengthTicker);
}
