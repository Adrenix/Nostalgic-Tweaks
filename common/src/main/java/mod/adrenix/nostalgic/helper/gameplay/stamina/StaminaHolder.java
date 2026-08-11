package mod.adrenix.nostalgic.helper.gameplay.stamina;

/**
 * Implemented by the {@code Player} class so stamina data is preserved for NBT saving.
 */
public interface StaminaHolder
{
    /**
     * @return The {@link StaminaData} associated with this holder.
     */
    StaminaData nt$getStaminaData();

    /**
     * Change the data object stored in memory. Only do this if reading data from disk.
     *
     * @param data The {@link StaminaData} to associate with this holder.
     */
    void nt$setStaminaData(StaminaData data);
}
