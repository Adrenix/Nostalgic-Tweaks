package mod.adrenix.nostalgic.mixin.tweak.candy.player_particles;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /**
     * Prevents the spawning of block particles when the player falls.
     */
    @WrapWithCondition(
        method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V"
        )
    )
    private boolean nt_player_particles$shouldAddParticle(LevelRenderer levelRenderer, ParticleOptions options, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean isDisabled = CandyTweak.DISABLE_FALLING_PARTICLES.get() && options.getType() == ParticleTypes.BLOCK;

        if (player == null || player.canSpawnSprintParticle() || !isDisabled)
            return true;

        boolean isAtX = MathUtil.tolerance(player.getX(), x, 0.01F);
        boolean isAtY = MathUtil.tolerance(player.getY(), y, 0.01F);
        boolean isAtZ = MathUtil.tolerance(player.getZ(), z, 0.01F);

        return !isAtX || !isAtY || !isAtZ;
    }
}
