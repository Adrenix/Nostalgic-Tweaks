package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor
{
    @Accessor("leashInfoTag") @Nullable CompoundTag NT$getCompoundTag();
}
