package mod.adrenix.nostalgic.tweak;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.cache.CacheMode;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;

public enum TweakContext implements StatusContext
{
    MOD_CONFLICT(ChatFormatting.DARK_RED, Lang.Text.MOD_CONFLICT, Lang.Status.CONFLICT, Icons.TRAFFIC_LIGHT_ERROR, Icons.TRAFFIC_LIGHT_BLACK),
    DYNAMIC(ChatFormatting.LIGHT_PURPLE, Lang.Tag.DYNAMIC, Lang.EMPTY, Icons.TRAFFIC_LIGHT_PURPLE, null);

    /* Static */

    /**
     * Get a status context that is relevant to the given tweak and cache mode.
     *
     * @param tweak     A {@link Tweak} instance to get data from.
     * @param cacheMode A {@link CacheMode} to retrieve data from.
     * @return A {@link StatusContext} instance.
     * @throws RuntimeException If the server is accessing this method.
     */
    public static StatusContext from(Tweak<?> tweak, @Nullable CacheMode cacheMode)
    {
        if (NostalgicTweaks.isServer())
            throw new RuntimeException("Server cannot access client-only tweak context class");

        if (tweak.isModConflict())
            return MOD_CONFLICT;

        if (NetUtil.isConnected())
        {
            if (tweak.isDynamic() && tweak.isLoaded())
                return DYNAMIC;
        }

        if (tweak.isDynamic() && tweak.isLoaded())
            return DYNAMIC;

        return cacheMode == null ? tweak.getStatus() : tweak.getStatus(cacheMode);
    }

    /**
     * Get a status context that is relevant to the given tweak based on the current cache mode the tweak is in.
     *
     * @param tweak A {@link Tweak} instance to get data from.
     * @return A {@link StatusContext} instance.
     * @throws RuntimeException If the server is accessing this method.
     */
    public static StatusContext from(Tweak<?> tweak)
    {
        return from(tweak, null);
    }

    /* Fields */

    private final ChatFormatting color;
    private final Translation title;
    private final Translation info;
    private final TextureIcon icon;
    @Nullable private final TextureIcon off;

    /* Constructor */

    TweakContext(ChatFormatting color, Translation title, Translation info, TextureIcon icon, @Nullable TextureIcon off)
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
        if (this.equals(DYNAMIC))
        {
            if (NetUtil.isConnected())
                return NetUtil.isPlayerOp() ? Lang.Status.DYNAMIC_OPERATOR : Lang.Status.DYNAMIC_VERIFIED;

            if (NetUtil.isMultiplayer())
                return Lang.Status.DYNAMIC_UNVERIFIED;

            return Lang.Status.DYNAMIC;
        }

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
