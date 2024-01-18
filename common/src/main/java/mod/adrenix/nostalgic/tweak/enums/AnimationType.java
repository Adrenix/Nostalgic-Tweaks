package mod.adrenix.nostalgic.tweak.enums;

import com.google.common.base.Suppliers;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.common.animate.Animate;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Some tweaks may benefit from having different animation options. The client handles animations, so this should only
 * be implemented by client-side tweaks.
 */
public enum AnimationType implements EnumTweak
{
    LINEAR(Lang.Enum.ANIMATION_LINEAR, Animate::linear),
    EASE_IN_SINE(Lang.Enum.ANIMATION_EASE_IN_SINE, Animate::easeInSine),
    EASE_IN_CUBIC(Lang.Enum.ANIMATION_EASE_IN_CUBIC, Animate::easeInCubic),
    EASE_IN_CIRCULAR(Lang.Enum.ANIMATION_EASE_IN_CIRCULAR, Animate::easeInCircular),
    EASE_IN_EXPO(Lang.Enum.ANIMATION_EASE_IN_EXPO, Animate::easeInExpo),
    EASE_OUT_SINE(Lang.Enum.ANIMATION_EASE_OUT_SINE, Animate::easeOutSine),
    EASE_OUT_CUBIC(Lang.Enum.ANIMATION_EASE_OUT_CUBIC, Animate::easeOutCubic),
    EASE_OUT_CIRCULAR(Lang.Enum.ANIMATION_EASE_OUT_CIRCULAR, Animate::easeOutCircular),
    EASE_OUT_EXPO(Lang.Enum.ANIMATION_EASE_OUT_EXPO, Animate::easeOutExpo),
    EASE_IN_OUT_SINE(Lang.Enum.ANIMATION_EASE_IN_OUT_SINE, Animate::easeInOutSine),
    EASE_IN_OUT_CUBIC(Lang.Enum.ANIMATION_EASE_IN_OUT_CUBIC, Animate::easeInOutCubic),
    EASE_IN_OUT_CIRCULAR(Lang.Enum.ANIMATION_EASE_IN_OUT_CIRCULAR, Animate::easeInOutCircular),
    EASE_IN_OUT_EXPO(Lang.Enum.ANIMATION_EASE_IN_OUT_EXPO, Animate::easeInOutExpo);

    private final Translation title;
    private final Supplier<Function<Double, Double>> animation;

    AnimationType(Translation title, Supplier<Animation> animator)
    {
        this.title = title;
        this.animation = Suppliers.memoize(() -> animator.get().getAnimation());
    }

    /**
     * Get a new instance of this type's animation supplier.
     *
     * @return A {@link Animation} from the type's {@link Supplier}.
     */
    public Function<Double, Double> getAnimation()
    {
        return this.animation.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
