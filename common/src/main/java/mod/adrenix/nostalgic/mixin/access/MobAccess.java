package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccess
{
    @Nullable
    @Accessor("leashInfoTag")
    CompoundTag nt$getCompoundTag();
}
