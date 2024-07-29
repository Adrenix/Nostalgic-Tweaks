package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin
{
    /* Unique */

    @Unique private int nt$restoreHealthAmount = 0;

    @Shadow private int tickTimer;
    @Shadow private float exhaustionLevel;

    /* Injections */

    /**
     * Lets the food level refill the player's heart's.
     */
    @Inject(
        method = "eat(IF)V",
        at = @At("HEAD")
    )
    private void nt_food_health$onEat(int foodLevelModifier, float saturationLevelModifier, CallbackInfo callback)
    {
        if (GameplayTweak.DISABLE_HUNGER.get())
            this.nt$restoreHealthAmount = foodLevelModifier;
    }

    /**
     * Heals the player based on previously eaten food and sets the exhaustion level and tick timer to zero to prevent
     * hunger related side effects on the player. If the difficulty is peaceful, then the player will be automatically
     * healed hunger is disabled.
     */
    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void nt_food_health$onTick(Player player, CallbackInfo callback)
    {
        if (this.nt$restoreHealthAmount > 0)
        {
            player.heal(this.nt$restoreHealthAmount);
            this.nt$restoreHealthAmount = 0;
        }

        if (!GameplayTweak.DISABLE_HUNGER.get())
            return;

        this.exhaustionLevel = 0.0F;
        this.tickTimer = 0;
    }
}
