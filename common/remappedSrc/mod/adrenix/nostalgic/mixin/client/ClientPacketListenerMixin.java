package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPacketListenerMixin
{
    /* Shadows */

    @Shadow @Final private RandomSource random;

    /* Injections */

    /**
     * Replaces the experience orb pickup sound with the early beta experience orb pickup sound.
     * This is just the item pickup sound with a deeper pitch.
     *
     * Controlled by the old experience orb pickup tweak.
     *
     * Pickup sound can also be muted by the disabled experience orb pickup sound tweak.
     */
    @Redirect
    (
        method = "handleTakeItemEntity",
        at = @At
        (
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
        )
    )
    private void NT$onPlayLocalExperienceSound(ClientWorld instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean distanceDelay)
    {
        if (ModConfig.Sound.oldXp() && !ModConfig.Sound.disableXpPickup())
            instance.playSound(x, y, z, sound, category, volume, this.random.nextFloat() - this.random.nextFloat() * 0.1F + 0.01F, distanceDelay);
        else if (ModConfig.Sound.disableXpPickup())
            instance.playSound(x, y, z, sound, category, 0.0F, pitch, distanceDelay);
        else
            instance.playSound(x, y, z, sound, category, volume, pitch, distanceDelay);
    }
}
