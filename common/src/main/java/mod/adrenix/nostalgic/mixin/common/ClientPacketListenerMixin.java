package mod.adrenix.nostalgic.mixin.common;

import mod.adrenix.nostalgic.common.config.MixinConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    /* Shadows */

    @Shadow @Final private RandomSource random;

    /**
     * Client:
     *
     * Replaces the experience orb pickup sound with the early beta experience orb pickup sound.
     * This is just the item pickup sound with a deeper pitch.
     *
     * Controlled by the old experience orb pickup tweak.
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
    private void NT$onPlayLocalExperienceSound(ClientLevel instance, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay)
    {
        if (MixinConfig.Sound.oldXP())
            instance.playLocalSound(x, y, z, SoundEvents.ITEM_PICKUP, category, volume, this.random.nextFloat() - this.random.nextFloat() * 0.1F + 0.01F, distanceDelay);
        else
            instance.playLocalSound(x, y, z, sound, category, volume, pitch, distanceDelay);
    }
}
