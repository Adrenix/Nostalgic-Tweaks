package mod.adrenix.nostalgic.util.common;

/**
 * Changes the default mixin priority in unique cases.
 */

public abstract class MixinPriority
{
    /**
     * This will force our mixins to be applied first.
     * Any mod using a lower value will take precedence.
     */
    public static final int APPLY_FIRST = 999;

    /**
     * This will force our mixins to be applied last.
     * Any mod using a higher value will load after us.
     */
    public static final int APPLY_LAST = 1001;
}
