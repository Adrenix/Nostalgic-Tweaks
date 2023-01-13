package mod.adrenix.nostalgic.mixin.duck;

/**
 * Will be injected into the item class.
 *
 * The methods implemented will keep track of the original max stack size. The original max stack size can be modified
 * by the old food tweaks or the custom item stack size map. Any item stack with an original item size of 1 cannot be
 * changed for safety reasons.
 */

public interface MaxSizeChanger
{
    float NT$getOriginalSize();
}
