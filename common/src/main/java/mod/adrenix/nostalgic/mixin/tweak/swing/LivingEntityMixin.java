package mod.adrenix.nostalgic.mixin.tweak.swing;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.util.animation.PlayerArmMixinHelper;
import mod.adrenix.nostalgic.mixin.util.swing.SwingMixinHelper;
import mod.adrenix.nostalgic.mixin.util.swing.SwingType;
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

        if (AnimationTweak.OLD_CLASSIC_HIT_SWING.get() && PlayerArmMixinHelper.SWING_TYPE.get() == SwingType.HIT)
            return 7;

        if (AnimationTweak.OLD_CLASSIC_PLACE_SWING.get() && PlayerArmMixinHelper.SWING_TYPE.get() == SwingType.PLACE)
            return 3;

        int speed = SwingMixinHelper.getSwingSpeed(player);

        if (SwingMixinHelper.isSpeedGlobal())
            return speed;
        else if (SwingMixinHelper.isHasteOverride() && player.hasEffect(MobEffects.DIG_SPEED))
            return SwingMixinHelper.getHasteSpeed();
        else if (SwingMixinHelper.isFatigueOverride() && player.hasEffect(MobEffects.DIG_SLOWDOWN))
            return SwingMixinHelper.getFatigueSpeed();
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
