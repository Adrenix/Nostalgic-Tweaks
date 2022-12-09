package mod.adrenix.nostalgic.common.config.list;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.DefaultConfig;

import java.util.Map;

/**
 * This class is responsible for checking the lists within a config. List validation can be performed by both the client
 * and the server. Although list manipulation should be done through the mod's configuration user interface, it is
 * important to validate the lists anyway when they are read from disk.
 */

public abstract class ValidateList
{
    /**
     * Runs validation instructions based on the provided list identifier. No errors will be thrown; instead, list data
     * will be cleaned or reset.
     *
     * @param id A list identifier.
     * @param list A list object.
     * @return Whether validation was successful.
     */
    @SuppressWarnings("unchecked") // List types are associated with their list identifier
    public static boolean scan(ListId id, Object list)
    {
        return switch (id)
        {
            case CUSTOM_SWING -> customSwings((Map<String, Integer>) list);
        };
    }

    /* List Validators */

    /**
     * Validate the contents of the custom swing speed map.
     * @param swingSpeeds A custom swing speed map.
     * @return Whether validation is successful.
     */
    private static boolean customSwings(Map<String, Integer> swingSpeeds)
    {
        boolean isValid = true;

        for (Map.Entry<String, Integer> entry : swingSpeeds.entrySet())
        {
            String key = entry.getKey();
            int value = entry.getValue();

            if (value < DefaultConfig.Swing.MIN_SPEED || value > DefaultConfig.Swing.MAX_SPEED)
            {
                NostalgicTweaks.LOGGER.warn("%s has invalid swing speed: %s", key, value);
                NostalgicTweaks.LOGGER.warn("%s has been updated to speed: %s", key, DefaultConfig.Swing.OLD_SPEED);
                entry.setValue(DefaultConfig.Swing.OLD_SPEED);

                isValid = false;
            }
        }

        return isValid;
    }
}
