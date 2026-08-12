package mod.adrenix.nostalgic.helper.gameplay.stamina;

import com.mojang.serialization.Codec;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.util.Mth;

public class StaminaData
{
    public static final int DEFAULT_MAXIMUM = 20;
    public static final Codec<StaminaData> CODEC = StaminaCodec.create();

    protected int stamina;
    protected int maximum;
    protected int remaining;
    protected int cooldown;
    protected boolean exhausted;

    public StaminaData(int stamina, int maximum, int remaining, int cooldown, boolean exhausted)
    {
        this.stamina = stamina;
        this.maximum = maximum;
        this.remaining = remaining;
        this.cooldown = cooldown;
        this.exhausted = exhausted;
    }

    /**
     * @return A default {@link StaminaData} instance.
     */
    public static StaminaData create()
    {
        int duration = GameplayTweak.STAMINA_DURATION.get() * 20;
        int cooldown = GameplayTweak.STAMINA_COOLDOWN.get() * 20;

        return new StaminaData(DEFAULT_MAXIMUM, DEFAULT_MAXIMUM, duration, cooldown, false);
    }

    /**
     * @return Whether the current stamina level is less than the max amount possible.
     */
    public boolean isTiring()
    {
        return this.stamina < this.maximum;
    }

    /**
     * @return The current stamina value.
     */
    public int getStamina()
    {
        return this.stamina;
    }

    /**
     * Stores the given value as the stamina. Given stamina will be maxed so that it is greater than or equal to 0.
     *
     * @param stamina The stamina value to store.
     */
    public void setStaminaRaw(int stamina)
    {
        this.stamina = Math.max(0, stamina);
    }

    /**
     * Updates the stamina value based on the given target duration in ticks. This is the ratio of the amount of ticks
     * defined by {@link #setRemaining(int)} to the given {@code ticks}.
     *
     * @param ticks The target duration in ticks used as the denominator.
     */
    public void setStaminaUsingTicks(int ticks)
    {
        this.stamina = Mth.clamp((int) Math.ceil(((double) this.remaining / ticks) * this.maximum), 0, this.maximum);
    }

    /**
     * @return The maximum stamina level possible.
     */
    public int getMaximum()
    {
        return this.maximum;
    }

    /**
     * @param maximum The maximum stamina value possible. Will be maxed to zero if given value is negative.
     */
    public void setMaximum(int maximum)
    {
        this.maximum = Math.max(0, maximum);
    }

    /**
     * @return The remaining time, in ticks, before exhaustion.
     */
    public int getRemaining()
    {
        return this.remaining;
    }

    /**
     * @param remainingInTicks The amount, in ticks, that represents how long before exhaustion.
     */
    public void setRemaining(int remainingInTicks)
    {
        this.remaining = Math.max(0, remainingInTicks);
    }

    /**
     * @return The amount of ticks since cooldown began.
     */
    public int getCooldown()
    {
        return this.cooldown;
    }

    /**
     * @param cooldown The amount of ticks to use before stamina can begin recharging.
     */
    public void setCooldown(int cooldown)
    {
        this.cooldown = Mth.clamp(cooldown, 0, GameplayTweak.STAMINA_COOLDOWN.get() * 20);
    }

    /**
     * @return Whether stamina was fully depleted and needs recharged.
     */
    public boolean isExhausted()
    {
        return this.exhausted;
    }

    /**
     * @param exhausted Whether the player is now exhausted.
     */
    public void setExhausted(boolean exhausted)
    {
        this.exhausted = exhausted;
    }
}
