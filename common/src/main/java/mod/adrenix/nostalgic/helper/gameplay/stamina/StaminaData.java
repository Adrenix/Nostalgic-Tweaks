package mod.adrenix.nostalgic.helper.gameplay.stamina;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;

public class StaminaData
{
    /* Constants */

    public static final int MAX_STAMINA_LEVEL = 20;

    /* Fields */

    private boolean isExhausted = false;
    private int durationInTicks = 0;
    private int rechargeInTicks = 0;
    private int tickTimer = 0;
    private int staminaLevel = MAX_STAMINA_LEVEL;

    /* Constructor */

    public StaminaData()
    {
        this.syncTickTimers();
    }

    /* Methods */

    /**
     * Redefine the duration and recharge timers based on current tweak context.
     */
    public void syncTickTimers()
    {
        int durationFromTweak = GameplayTweak.STAMINA_DURATION.get() * 20;
        int rechargeFromTweak = GameplayTweak.STAMINA_RECHARGE.get() * 20;

        if (this.durationInTicks != durationFromTweak)
        {
            this.durationInTicks = durationFromTweak;
            this.tickTimer = durationFromTweak;
        }

        if (this.rechargeInTicks != rechargeFromTweak)
            this.rechargeInTicks = rechargeFromTweak;
    }

    /**
     * Update stamina data every tick.
     *
     * @param player The {@link Player} instance this data is associated with.
     */
    public void tick(Player player)
    {
        Difficulty difficulty = player.level().getDifficulty();
        boolean isSprinting = player.isSprinting();
        boolean canTick = player.level().isClientSide() || NostalgicTweaks.isServer();

        if (GameplayTweak.STAMINA_INFINITE_PEACEFUL.get() && difficulty == Difficulty.PEACEFUL)
        {
            this.staminaLevel = 20;
            return;
        }

        this.syncTickTimers();

        if (this.isExhausted)
        {
            if (canTick)
                this.tickTimer++;

            if (this.tickTimer >= this.rechargeInTicks)
            {
                this.isExhausted = false;
                this.tickTimer = this.durationInTicks;
            }

            this.setStaminaLevel(this.isExhausted ? this.rechargeInTicks : this.durationInTicks);
        }
        else if (isSprinting)
        {
            if (canTick)
                this.tickTimer--;

            if (this.tickTimer <= 0)
            {
                this.isExhausted = true;
                this.tickTimer = 0;
            }

            this.setStaminaLevel(this.durationInTicks);
        }
        else
        {
            if (this.tickTimer < this.durationInTicks && canTick)
                this.tickTimer++;

            this.setStaminaLevel(this.durationInTicks);
        }
    }

    /**
     * Set the stamina level based on the current tick count out of the given amount in ticks.
     *
     * @param amountInTicks The "out of" amount in ticks.
     */
    private void setStaminaLevel(int amountInTicks)
    {
        this.staminaLevel = (int) Math.ceil(((double) this.tickTimer / amountInTicks) * 20.0D);
    }

    /**
     * @return Get the amount of stamina the player has.
     */
    public int getStaminaLevel()
    {
        return this.staminaLevel;
    }

    /**
     * @return Whether the player is considered exhausted.
     */
    public boolean isExhausted()
    {
        if (!GameplayTweak.STAMINA_SPRINT.get())
            return false;

        return this.isExhausted;
    }
}
