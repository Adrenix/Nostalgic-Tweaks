package mod.adrenix.nostalgic.tweak;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;

public abstract class TweakCondition
{
    /**
     * @return A truthful value if items are being rendered in 2D.
     */
    public static boolean areItemsFlat()
    {
        return CandyTweak.OLD_2D_ITEMS.fromDisk();
    }
}
