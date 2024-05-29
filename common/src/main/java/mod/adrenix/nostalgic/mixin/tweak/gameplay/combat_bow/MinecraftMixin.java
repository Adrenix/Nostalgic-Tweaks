package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_bow;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /**
     * Prevents the player from swinging their interaction hand on the client when using the instantaneous bow tweak.
     */
    @WrapWithCondition(
        method = "startUseItem",
        at = @At(
            ordinal = 2,
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private boolean nt_combat_bow$shouldSwingOnBowUse(LocalPlayer player, InteractionHand hand)
    {
        return !GameplayTweak.INSTANT_BOW.get() || !player.getItemInHand(hand).getItem().equals(Items.BOW);
    }
}
