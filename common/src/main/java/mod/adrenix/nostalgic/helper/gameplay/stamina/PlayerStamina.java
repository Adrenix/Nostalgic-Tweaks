package mod.adrenix.nostalgic.helper.gameplay.stamina;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.api.NostalgicAttributes;
import mod.adrenix.nostalgic.network.packet.stamina.ClientboundStaminaSync;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.tweak.enums.StaminaRegain;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public class PlayerStamina
{
    /**
     * @param player The {@link Player} to link this data to.
     * @return A new default {@link PlayerStamina} instance.
     * @throws IllegalStateException If the player is not an instance of {@link StaminaHolder}.
     */
    public static PlayerStamina create(Player player)
    {
        if (player instanceof StaminaHolder holder)
            return new PlayerStamina(player, holder.nt$getStaminaData());
        else
            throw new IllegalStateException("Player instance not a holder of stamina data");
    }

    protected int durationInTicks;
    protected int cooldownInTicks;
    protected int rechargeInTicks;
    protected int halfRateInTicks;
    protected int previousMaximum;
    protected boolean tickAgain = false;

    public final Player player;
    public final StaminaData data;

    protected PlayerStamina(Player player, StaminaData data)
    {
        this.player = player;
        this.data = data;

        this.durationInTicks = this.getDurationInTicks();
        this.cooldownInTicks = GameplayTweak.STAMINA_COOLDOWN.get() * 20;
        this.rechargeInTicks = GameplayTweak.STAMINA_RECHARGE.get() * 20;
        this.halfRateInTicks = 0;
        this.previousMaximum = data.getMaximum();
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public StaminaData getData()
    {
        return this.data;
    }

    /**
     * @return The duration, in ticks, of how long the player can sprint.
     */
    public int getDurationInTicks()
    {
        return (int) ((GameplayTweak.STAMINA_DURATION.get() * 20) * (this.data.getMaximum() / (double) StaminaData.DEFAULT_MAXIMUM));
    }

    /**
     * Only the render thread and dedicated server thread should move tick counters. Otherwise, the integrated server
     * thread will double the tick counters.
     *
     * @return Whether the caller may modify stamina tick counters.
     */
    public boolean isLogical()
    {
        return NostalgicTweaks.isServer() || !GameUtil.isOnIntegratedSeverThread();
    }

    /**
     * Reset the duration, recharge, and cooldown timers based on current stamina tweak context.
     */
    public void reset()
    {
        int duration = this.getDurationInTicks();
        int cooldown = GameplayTweak.STAMINA_COOLDOWN.get() * 20;
        int recharge = GameplayTweak.STAMINA_RECHARGE.get() * 20;

        this.durationInTicks = duration;
        this.cooldownInTicks = cooldown;
        this.rechargeInTicks = recharge;
        this.halfRateInTicks = 0;

        this.data.setCooldown(0);
        this.data.setRemaining(duration);
        this.data.setStaminaUsingTicks(duration);
        this.data.setExhausted(false);
    }

    /**
     * Synchronize the current server stamina data with the player's client.
     */
    public void sync()
    {
        if (this.player instanceof ServerPlayer serverPlayer)
            PacketUtil.sendToPlayer(serverPlayer, ClientboundStaminaSync.create(serverPlayer));
    }

    /**
     * Update stamina data for this tick. May run twice per tick if player has positive effect. No server-to-client
     * synchronization occurs here.
     */
    public void tick()
    {
        AttributeInstance attribute = this.player.getAttribute(NostalgicAttributes.MAX_STAMINA);

        if (attribute != null)
            this.data.setMaximum((int) attribute.getValue());

        if (this.previousMaximum != this.data.getMaximum())
        {
            if (this.isLogical())
            {
                this.previousMaximum = this.data.getMaximum();
                this.durationInTicks = this.getDurationInTicks();
            }

            if (NostalgicTweaks.isServer() || GameUtil.isOnIntegratedSeverThread())
            {
                this.data.remaining = Math.min(this.data.remaining, Math.min(this.durationInTicks, this.getDurationInTicks()));
                this.sync();
            }
        }

        if (GameplayTweak.STAMINA_INFINITE_PEACEFUL.get() && Difficulty.PEACEFUL == this.player.level().getDifficulty())
        {
            this.data.setStaminaRaw(this.data.getMaximum());
            return;
        }

        if (this.data.isExhausted())
        {
            if (this.isLogical() && this.isRegainable() && this.isTrackerNotHalved())
                this.data.remaining++;

            if (this.data.remaining >= this.rechargeInTicks)
            {
                this.data.remaining = this.durationInTicks;
                this.data.cooldown = 0;
                this.data.setExhausted(false);
            }

            this.data.setStaminaUsingTicks(this.data.isExhausted() ? this.rechargeInTicks : this.durationInTicks);
        }
        else if (this.isAtFullSprint())
        {
            if (this.isLogical())
                this.data.remaining--;

            this.data.cooldown = this.cooldownInTicks;

            if (this.data.remaining <= 0)
            {
                this.data.remaining = 0;

                if (NostalgicTweaks.isServer() || GameUtil.isOnIntegratedSeverThread())
                {
                    this.data.setExhausted(true);
                    this.sync();
                }
            }

            this.data.setStaminaUsingTicks(this.durationInTicks);
        }
        else
        {
            if (this.isLogical() && this.data.remaining < this.durationInTicks)
            {
                if (this.data.cooldown <= 0 && this.isRegainable() && this.isTrackerNotHalved())
                    this.data.remaining++;
                else
                    this.data.cooldown--;
            }

            this.data.setStaminaUsingTicks(this.durationInTicks);
        }

        boolean shouldTickAgain = this.hasPositiveEffect();

        if (this.hasNegativeEffect() && this.isAtFullSprint())
            shouldTickAgain = true;

        if (shouldTickAgain && !this.tickAgain)
        {
            this.tickAgain = true;
            this.tick();
        }
        else
            this.tickAgain = false;
    }

    /**
     * @return Whether the player is moving.
     */
    public boolean isMoving()
    {
        double dx = this.player.getKnownMovement().x();
        double dz = this.player.getKnownMovement().z();

        return dx * dx + dz * dz > 2.5E-5F;
    }

    /**
     * @return Whether the player is at full sprinting speed.
     */
    public boolean isAtFullSprint()
    {
        return !this.player.isUsingItem() && this.player.isSprinting();
    }

    /**
     * Check if the player has lost, is losing, or is about to lose, their stamina.
     *
     * @return Whether the player is exhausted, has lost stamina, or is currently sprinting.
     */
    public boolean isTiringOrExhausted()
    {
        if (!GameplayTweak.STAMINA_SPRINT.get())
            return false;

        return this.data.isTiring() || this.data.isExhausted() || this.isAtFullSprint();
    }

    /**
     * @return Get whether the player is cooling down before their stamina begins to increase.
     */
    public boolean isCoolingDown()
    {
        if (!GameplayTweak.STAMINA_SPRINT.get())
            return false;

        return this.data.cooldown > 0 && this.data.cooldown < this.cooldownInTicks;
    }

    /**
     * @return Whether the player can regain their stamina.
     */
    public boolean isRegainable()
    {
        if (GameplayTweak.STAMINA_REGAIN_WHEN_MOVING.get() != StaminaRegain.NONE)
            return true;

        return !this.isMoving();
    }

    /**
     * @return Whether the player cannot regain their stamina.
     */
    public boolean isNotRegainable()
    {
        return !this.isRegainable() && !this.isAtFullSprint() && this.data.isTiring();
    }

    /**
     * @return Check if the tick rate has not been doubled.
     */
    public boolean isTrackerNotHalved()
    {
        boolean isHalved = false;
        boolean isHungry = false;

        if (GameplayTweak.STAMINA_REGAIN_WHEN_MOVING.get() == StaminaRegain.HALF && this.isMoving())
            isHalved = !this.isAtFullSprint() && this.data.isTiring();

        if (GameplayTweak.STAMINA_HUNGER_EFFECT.get())
            isHungry = this.player.hasEffect(MobEffects.HUNGER);

        if (isHalved || isHungry)
        {
            if (this.halfRateInTicks >= 1)
                this.halfRateInTicks = 0;
            else
            {
                this.halfRateInTicks++;
                return false;
            }
        }
        else
            this.halfRateInTicks = 0;

        return true;
    }

    /**
     * @return Whether the player has a positive effect that affects their stamina.
     */
    public boolean hasPositiveEffect()
    {
        if (GameplayTweak.STAMINA_SATURATION_EFFECT.get() && this.isTiringOrExhausted())
            return this.player.hasEffect(MobEffects.SATURATION) && !this.isAtFullSprint();

        return false;
    }

    /**
     * @return Whether the player has a negative effect that affects their stamina.
     */
    public boolean hasNegativeEffect()
    {
        if (GameplayTweak.STAMINA_HUNGER_EFFECT.get() && this.isTiringOrExhausted())
            return this.player.hasEffect(MobEffects.HUNGER);

        return false;
    }
}
