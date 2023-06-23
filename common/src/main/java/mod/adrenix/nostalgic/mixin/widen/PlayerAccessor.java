package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface PlayerAccessor
{
    @Accessor("hurtDir") void NT$setHurtDir(float dir);
}
