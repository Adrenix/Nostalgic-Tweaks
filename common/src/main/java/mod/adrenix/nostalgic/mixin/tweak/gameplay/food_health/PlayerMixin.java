package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity
{
    /* Fake Constructor */

    private PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Allows the player to eat if their current health is below their max health.
     */
    @ModifyReturnValue(
        method = "canEat",
        at = @At("RETURN")
    )
    private boolean nt_food_health$canPlayerEat(boolean playerCanEat, boolean canAlwaysEat)
    {
        if (GameplayTweak.DISABLE_HUNGER.get() && !canAlwaysEat)
            return this.getHealth() < this.getMaxHealth();

        return playerCanEat;
    }
}
