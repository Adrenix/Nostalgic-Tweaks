package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.gameplay.combat.SwordBlockingHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin
{
    /**
     * Prevents the player from swinging their interaction hand on the server when sword blocking.
     */
    @WrapWithCondition(
        method = "handleUseItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;swing(Lnet/minecraft/world/InteractionHand;Z)V"
        )
    )
    private boolean nt_combat_player$shouldSwingOnSwordBlockUse(ServerPlayer player, InteractionHand hand, boolean shouldSwing)
    {
        return !SwordBlockingHelper.isBlocking(player);
    }
}
