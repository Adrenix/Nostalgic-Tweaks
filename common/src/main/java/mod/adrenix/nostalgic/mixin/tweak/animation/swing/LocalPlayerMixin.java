package mod.adrenix.nostalgic.mixin.tweak.animation.swing;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.mixin.duck.SwingBlocker;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin
{
    /**
     * Blocks the client side rendering of the swinging animation, but still sends the swing packet to the server.
     */
    @WrapWithCondition(
        method = "swing",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/AbstractClientPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private boolean nt_animation_swing$shouldSwing(AbstractClientPlayer player, InteractionHand hand)
    {
        return !((SwingBlocker) this).nt$isSwingBlocked();
    }

    /**
     * Blocks the sending of the server bound swing packet if the old swing dropping tweak is enabled and the client is
     * connected to a server with the mod installed.
     */
    @WrapWithCondition(
        method = "swing",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
        )
    )
    private boolean nt_animation_swing$shouldSendSwingPacket(ClientPacketListener packetListener, Packet<?> packet)
    {
        boolean isSwingBlocked = ((SwingBlocker) this).nt$isSwingBlocked();
        ((SwingBlocker) this).nt$setSwingBlocked(false);

        return !AnimationTweak.OLD_SWING_DROPPING.get() || !NostalgicTweaks.isNetworkVerified() || !isSwingBlocked;
    }
}
