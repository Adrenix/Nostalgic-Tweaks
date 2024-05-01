package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface PlayerAccess
{
    @Accessor("hurtDir")
    void nt$setHurtDir(float hurtDir);
}
