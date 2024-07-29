package mod.adrenix.nostalgic.mixin.tweak.swing;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.animation.PlayerArmHelper;
import mod.adrenix.nostalgic.helper.swing.SwingHelper;
import mod.adrenix.nostalgic.helper.swing.SwingType;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.tweak.config.SwingTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    /* Fake Constructor */

    private LivingEntityMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Changes the speed of the player's swinging animation.
     */
    @ModifyReturnValue(
        method = "getCurrentSwingDuration",
        at = @At("RETURN")
    )
    private int nt_swing$modifyCurrentSwingDuration(int currentSwingDuration)
    {
        AbstractClientPlayer player = Minecraft.getInstance().player;

        if (SwingTweak.OVERRIDE_SPEEDS.get() || player == null)
            return currentSwingDuration;
        else if (this.getType() == EntityType.PLAYER)
        {
            Player entity = (Player) this.getType().tryCast(this);

            if (entity == null || !entity.isLocalPlayer())
                return currentSwingDuration;
        }

        int speed = SwingHelper.getSwingSpeed(player);

        if (AnimationTweak.OLD_CLASSIC_ATTACK_SWING.get() && PlayerArmHelper.SWING_TYPE.get() == SwingType.ATTACK)
            return SwingHelper.isSpeedGlobal() ? speed : 7;

        if (AnimationTweak.OLD_CLASSIC_USE_SWING.get() && PlayerArmHelper.SWING_TYPE.get() == SwingType.USE)
            return SwingHelper.isSpeedGlobal() ? speed : 3;

        if (SwingHelper.isSpeedGlobal())
            return speed;
        else if (SwingHelper.isHasteOverride() && player.hasEffect(MobEffects.DIG_SPEED))
            return SwingHelper.getHasteSpeed();
        else if (SwingHelper.isFatigueOverride() && player.hasEffect(MobEffects.DIG_SLOWDOWN))
            return SwingHelper.getFatigueSpeed();
        else if (MobEffectUtil.hasDigSpeed(player))
            return speed - (1 + MobEffectUtil.getDigSpeedAmplification(player));
        else
        {
            boolean isSlowdown = player.hasEffect(MobEffects.DIG_SLOWDOWN);
            MobEffectInstance slowdown = player.getEffect(MobEffects.DIG_SLOWDOWN);

            return isSlowdown ? speed + (1 + (slowdown != null ? slowdown.getAmplifier() : 0)) * 2 : speed;
        }
    }
}
