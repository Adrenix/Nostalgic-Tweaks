package mod.adrenix.nostalgic.tweak;

import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;

/**
 * There are numerous states a tweak can be in. Some tweaks will start in different states than others.
 * <p><br>
 * Each tweak will be in its own unique state. This is done so the server can properly report tweak states to clients
 * and indicate to the user if there is a possible mod conflict.
 * </ul>
 * <ul>
 *     <li>{@code LOADED} - The tweak ran expected code.</li><p>
 *     <li>{@code WAIT} - The tweak will run code in a world.</li><p>
 *     <li>{@code WARN} - Inform the user that there is something important to know about a tweak.</li><p>
 *     <li>{@code FAIL} - The tweak never ran expected code or the tweak has not been loaded by code that runs it.</li>
 * </ul>
 */
public enum TweakStatus implements StatusContext
{
    LOADED(ChatFormatting.GREEN, Lang.Text.LOADED, Lang.Status.LOADED, Icons.TRAFFIC_LIGHT_GREEN, null),
    WAIT(ChatFormatting.YELLOW, Lang.Text.WAIT, Lang.Status.WAIT, Icons.TRAFFIC_LIGHT_YELLOW, null),
    FAIL(ChatFormatting.RED, Lang.Text.FAIL, Lang.Status.FAIL, Icons.TRAFFIC_LIGHT_RED, Icons.TRAFFIC_LIGHT_OFF),
    WARN(ChatFormatting.GOLD, Lang.Text.WARN, Lang.Status.WARN, Icons.TRAFFIC_LIGHT_ORANGE, Icons.TRAFFIC_LIGHT_BLACK);

    /* Static */

    /**
     * Get the name of a status type in colored format. This can only be used by loggers that support ANSI color codes.
     *
     * @param status A status type to retrieve {@code toString} data from.
     * @return A colored {@code toString} value.
     */
    public static String toStringWithColor(TweakStatus status)
    {
        return switch (status)
        {
            case LOADED -> LogColor.apply(LogColor.GREEN, status.toString());
            case WAIT -> LogColor.apply(LogColor.YELLOW, status.toString());
            case WARN -> LogColor.apply(LogColor.GOLD, status.toString());
            case FAIL -> LogColor.apply(LogColor.RED, status.toString());
        };
    }

    /* Fields */

    private final ChatFormatting color;
    private final Translation title;
    private final Translation info;
    private final TextureIcon icon;
    @Nullable private final TextureIcon off;

    /* Constructor */

    TweakStatus(ChatFormatting color, Translation title, Translation info, TextureIcon icon, @Nullable TextureIcon off)
    {
        this.color = color;
        this.title = title;
        this.info = info;
        this.icon = icon;
        this.off = off;
    }

    /* Methods */

    @Override
    public ChatFormatting getColor()
    {
        return this.color;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }

    @Override
    public Translation getInfo()
    {
        return this.info;
    }

    @Override
    public TextureIcon getIcon()
    {
        return this.icon;
    }

    @Override
    public TextureIcon getIcon(boolean isFlashing)
    {
        if (isFlashing && this.off != null)
            return this.off;

        return this.icon;
    }
}
