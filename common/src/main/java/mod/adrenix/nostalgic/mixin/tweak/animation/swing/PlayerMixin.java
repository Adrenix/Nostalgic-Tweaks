package mod.adrenix.nostalgic.mixin.tweak.animation.swing;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    /**
     * Prevents the swinging animation when dropping an item from the hand or within an inventory screen.
     */
    @WrapWithCondition(
        method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private boolean nt_animation_swing$shouldSwingOnDropItem(Player player, InteractionHand hand)
    {
        return !AnimationTweak.OLD_SWING_DROPPING.get();
    }
}
