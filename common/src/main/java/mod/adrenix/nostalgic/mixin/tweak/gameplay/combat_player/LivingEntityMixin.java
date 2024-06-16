package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.mixin.util.gameplay.combat.SwordBlockMixinHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Modifies the amount of damage a player entity takes when they are blocking with a sword.
     */
    @ModifyVariable(
        argsOnly = true,
        method = "hurt",
        at = @At("HEAD")
    )
    private float nt_combat_player$onPlayerHurt(float hurtAmount)
    {
        Optional<Player> optionalPlayer = ClassUtil.cast(this, Player.class);

        if (optionalPlayer.isEmpty() || !optionalPlayer.get().isUsingItem())
            return hurtAmount;

        Player player = optionalPlayer.get();

        if (SwordBlockMixinHelper.isBlocking(player))
        {
            if (hurtAmount >= 3.0F)
            {
                player.getUseItem()
                    .hurtAndBreak(1, player, consumerPlayer -> consumerPlayer.broadcastBreakEvent(player.getUsedItemHand()));

                if (player.getUseItem().isEmpty())
                    player.stopUsingItem();
            }

            return hurtAmount * (1.0F - GameplayTweak.SWORD_BLOCK_DAMAGE_REDUCTION.get() / 100.0F);
        }

        return hurtAmount;
    }

    /**
     * Changes the use duration to prevent instant cancellation of a sword block.
     */
    @ModifyExpressionValue(
        method = "startUsingItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I"
        )
    )
    private int nt_combat_player$modifyGetUseDuration(int useDuration, @Local ItemStack itemStack)
    {
        return ClassUtil.cast(this, Player.class)
            .filter(SwordBlockMixinHelper::canBlock)
            .map(player -> 72000)
            .orElse(useDuration);
    }
}
