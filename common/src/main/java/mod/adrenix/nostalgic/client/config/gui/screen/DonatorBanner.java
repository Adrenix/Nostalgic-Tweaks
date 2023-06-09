package mod.adrenix.nostalgic.client.config.gui.screen;

import com.google.gson.Gson;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import mod.adrenix.nostalgic.util.common.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DonatorBanner
{
    /* JSON Structure */

    private static class JsonSupporters
    {
        /**
         * The data that will be kept in this map can be found at {@link LinkLocation#SUPPORTERS}. The map utilizes the
         * key field as the supporter's display name and the value field as the supporter's display color.
         */
        Map<String, String> supporters;
    }

    /* Static Fields */

    private static JsonSupporters cache = null;
    private static boolean downloaded = false;
    private static boolean opened = ClientConfigCache.getGui().displayDonatorBanner;
    private static int height = 0;

    private static final String NAME_SPACER = "    ";
    private static final TimeWatcher CACHE_TIMER = new TimeWatcher(10000L, 3);
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    private static final MutableComponent THANKS_MESSAGE = Component.translatable(LangUtil.Gui.SETTINGS_SUPPORTERS);
    private static final MutableComponent CONNECTING_MESSAGE = Component.translatable(LangUtil.Gui.SETTINGS_CONNECTING);
    private static final MutableComponent DISCONNECT_MESSAGE = Component.translatable(LangUtil.Gui.SETTINGS_DISCONNECTED);
    private static final ArrayList<MutableComponent> SUPPORTERS = new ArrayList<>();

    /* Static Methods */

    /**
     * Used to shift setting screen elements down or up depending on if the banner is opened.
     * @return The offset from the top of the screen.
     */
    public static int getHeight() { return DonatorBanner.height; }

    /**
     * Used to determine if elements on the settings screen should be shifted down or up.
     * @return Whether the support banner is opened or closed.
     */
    public static boolean isOpen() { return DonatorBanner.opened; }

    /**
     * Toggles the opened state of support banners.
     */
    public static void toggle() { DonatorBanner.opened = !DonatorBanner.opened; }

    /**
     * Since the settings screen can be jumped over during initialization, caching the screen width will not work.
     * @return Gets the current screen width.
     */
    private static int getWidth()
    {
        if (Minecraft.getInstance().screen != null)
            return Minecraft.getInstance().screen.width;

        return 0;
    }

    /* Fields */

    private final Font font = Minecraft.getInstance().font;
    private int messageX = 0;
    private int messageWidth = 0;
    private boolean isRunning = false;

    /* Constructor */

    /**
     * Create a new donator banner overlay instance.
     * This will be used at the main settings screen.
     */
    public DonatorBanner()
    {
        if (DonatorBanner.cache == null && DonatorBanner.opened)
        {
            if (DonatorBanner.CACHE_TIMER.isMaxReached())
                DonatorBanner.CACHE_TIMER.reset();

            this.connect();
        }

        this.build();
    }

    /**
     * Establish a connection with GitHub in a separate thread to download the supporter JSON file from the mod's data
     * branch.
     */
    private void connect()
    {
        if (!DonatorBanner.CACHE_TIMER.isReady())
            return;

        NostalgicTweaks.LOGGER.info("Connecting to %s", LinkLocation.SUPPORTERS);

        Thread thread = new Thread("GitHub Supporter Connector #" + UNIQUE_THREAD_ID.incrementAndGet())
        {
            /**
             * Reads supporter data from the mod's GitHub data branch.
             * Link pointers are kept in {@link LinkLocation}.
             */
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);

                    URL url = new URL(LinkLocation.SUPPORTERS);
                    InputStreamReader reader = new InputStreamReader(url.openStream());
                    DonatorBanner.cache = new Gson().fromJson(reader, JsonSupporters.class);

                    NostalgicTweaks.LOGGER.info("Successfully downloaded supporter data from GitHub");
                }
                catch (Exception exception)
                {
                    NostalgicTweaks.LOGGER.error("Could not get supporter data from GitHub\n%s", exception);
                }
            }
        };

        thread.start();
    }

    /**
     * Creates the supporter message and message width cache for this banner instance.
     */
    private void build()
    {
        if (DonatorBanner.cache == null)
            return;

        DonatorBanner.SUPPORTERS.clear();
        Map<String, String> fromJson = DonatorBanner.cache.supporters;

        for (Map.Entry<String, String> supporter : fromJson.entrySet())
            DonatorBanner.SUPPORTERS.add(ComponentUtil.color(supporter.getKey(), ColorUtil.toHexInt(supporter.getValue())));

        Collections.shuffle(DonatorBanner.SUPPORTERS);

        this.messageWidth = 0;

        for (MutableComponent supporter : DonatorBanner.SUPPORTERS)
            this.messageWidth += this.font.width(supporter) + this.font.width(NAME_SPACER);
    }

    /**
     * Get an x-position centered on the current screen.
     * @param message A component message.
     * @return A centered x-position.
     */
    private float getCenterX(MutableComponent message)
    {
        return (getWidth() / 2.0F) - (this.font.width(message) / 2.0F);
    }

    /**
     * Render the support message block.
     * @param graphics The current GuiGraphics object.
     * @param partialTick The change in game frame time.
     */
    public void render(GuiGraphics graphics, float partialTick)
    {
        int headerY = 3;
        int messageY = headerY + this.font.lineHeight + 4;
        int endY = (this.font.lineHeight * 2) + 10;

        if (!DonatorBanner.opened)
        {
            DonatorBanner.height = 0;
            return;
        }
        else
            DonatorBanner.height = endY + 2;

        RenderUtil.fill(graphics, 0.0F, (float) getWidth(), 0.0F, endY, 0x7F000000);
        graphics.drawString(font, THANKS_MESSAGE, (int) this.getCenterX(THANKS_MESSAGE), headerY, 0xFFFF00, true);

        if (DonatorBanner.cache == null && DonatorBanner.opened)
        {
            MutableComponent message = DonatorBanner.CACHE_TIMER.isMaxReached() ? DISCONNECT_MESSAGE : CONNECTING_MESSAGE;
            ChatFormatting color = DonatorBanner.CACHE_TIMER.isMaxReached() ? ChatFormatting.RED : ChatFormatting.GOLD;
            int centerX = (int) this.getCenterX(message);

            graphics.drawString(font, message.withStyle(color), centerX, messageY, 0xFFFFFF, true);
            this.connect();

            return;
        }

        if (DonatorBanner.cache != null && !DonatorBanner.downloaded)
        {
            DonatorBanner.downloaded = true;
            this.build();
        }

        int startX = getWidth() + 15;
        float offset = MathUtil.isOdd(getWidth()) ? 0.0F : 0.1F;

        if (!this.isRunning)
        {
            this.messageX = startX;
            this.isRunning = true;
        }

        if (this.messageX <= -this.messageWidth)
        {
            this.isRunning = false;
            this.build();
        }

        this.messageX -= (1.2F + (1.0F / 3.0F)) * partialTick;
        int currentX = this.messageX;

        for (MutableComponent supporter : DonatorBanner.SUPPORTERS)
        {
            if (currentX > startX)
                break;

            int width = this.font.width(supporter) + this.font.width(NAME_SPACER);

            if (currentX + width < 0)
            {
                currentX += width;
                continue;
            }

            RenderUtil.blit256(TextureLocation.WIDGETS, graphics, currentX - 12.5F + offset, messageY, 0, 32, 9, 9);
            graphics.drawString(font, supporter, currentX, messageY, 0xFFFFFF, true);

            currentX += width;
        }
    }
}
