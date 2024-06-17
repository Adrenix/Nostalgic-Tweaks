package mod.adrenix.nostalgic.tweak;

import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.DecodeLang;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Create a new tweak issue that will appear below a tweak's description body within the config user interface. Some
 * issues can have default descriptions if a tweak doesn't override it. An example translation would be:
 *
 * <p><br>
 * {@code "gui.nostalgic_tweaks.config.issue.sodium": "This tweak does not work when Sodium is installed."}
 * <p><br>
 * Any enumeration that doesn't define a default description requires all tweaks that use the issue to define an "issue"
 * message within the lang file. An example translation would be:
 *
 * <p><br>
 * {@code "gui.nostalgic_tweaks.config.eyeCandy.oldLightRendering.issue.sodium": "This tweak may break shaders when
 * enabled."}
 */
public enum TweakIssue
{
    SODIUM(Lang.literal("Sodium"), Lang.Issue.SODIUM, Icons.SODIUM, Color.AZURE_WHITE, ModTracker.SODIUM::isInstalled),
    OPTIFINE(Lang.literal("Optifine"), Lang.Issue.OPTIFINE, Icons.OPTIFINE, Color.MUSTARD, ModTracker.OPTIFINE::isInstalled),
    POLYTONE(Lang.literal("Polytone"), Lang.Issue.POLYTONE, Icons.PAINTBRUSH, Color.LIGHT_BLUE, ModTracker.POLYTONE::isInstalled);

    /* Fields */

    private final Translation title;
    private final Translation description;
    private final TextureIcon icon;
    private final Color color;
    private final BooleanSupplier active;

    /* Constructor */

    TweakIssue(Translation title, @Nullable Translation description, TextureIcon icon, Color color, BooleanSupplier active)
    {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.active = active;
    }

    /* Methods */

    /**
     * @return The header title for this issue.
     */
    public Translation getTitle()
    {
        return this.title;
    }

    /**
     * @return The icon that is to be used next to the header.
     */
    public TextureIcon getIcon()
    {
        return this.icon;
    }

    /**
     * @return The color that is to be applied to the header.
     */
    public Color getColor()
    {
        return this.color;
    }

    /**
     * @return Whether the issue is active and should be displayed in the config user interface.
     */
    public BooleanSupplier isActive()
    {
        return this.active;
    }

    /**
     * If a tweak does not have a custom description for this issue, then the default issue description will be used if
     * the enumeration has one. Otherwise, the tweak must define a custom issue translation.
     *
     * @param tweak A {@link Tweak} instance.
     * @return A {@link Component} issue description translation.
     */
    public Component getDescription(Tweak<?> tweak)
    {
        String langKey = tweak.getLangKey() + ".issue." + this.toString().toLowerCase(Locale.ROOT);
        Component translation = DecodeLang.findAndReplace(Component.translatable(langKey));

        if (translation.getString().equals(langKey) && this.description != null)
            return this.description.get();

        return translation;
    }
}
