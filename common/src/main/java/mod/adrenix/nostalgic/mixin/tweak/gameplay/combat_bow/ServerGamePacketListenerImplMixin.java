package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_bow;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin
{
    /**
     * Prevents the player from swinging their interaction hand on the server when using the instantaneous bow tweak.
     */
    @WrapWithCondition(
        method = "handleUseItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;swing(Lnet/minecraft/world/InteractionHand;Z)V"
        )
    )
    private boolean nt_combat_bow$shouldSwingOnBowUse(ServerPlayer player, InteractionHand hand, boolean shouldSwing)
    {
        return !GameplayTweak.INSTANT_BOW.get() || !player.getItemInHand(hand).getItem().equals(Items.BOW);
    }
}
