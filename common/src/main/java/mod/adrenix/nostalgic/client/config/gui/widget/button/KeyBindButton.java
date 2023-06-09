package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.client.config.gui.widget.TweakTag;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * This control button changes the key associated with a key mapping. Any utilities regarding key mappings should be
 * used and referenced by the {@link KeyUtil} class.
 */

public class KeyBindButton extends ControlButton
{
    /* Fields */

    private final KeyMapping mapping;
    private boolean isModifying = false;

    /* Constructor */

    /**
     * Create a new key bind controller button instance.
     * @param mapping A key mapping that will be associated with this controller.
     */
    public KeyBindButton(KeyMapping mapping)
    {
        super(Component.empty(), KeyBindButton::edit);
        this.mapping = mapping;
    }

    /* Static Methods */

    /**
     * Flips the state of the {@link KeyBindButton#isModifying} field flag.
     * @param button A button instance that will be cast as a key bind button.
     */
    private static void edit(Button button)
    {
        ((KeyBindButton) button).isModifying = !((KeyBindButton) button).isModifying;
    }

    /* Methods */

    /**
     * Get the key mapping associated with this controller.
     * @return A key mapping instance.
     */
    public KeyMapping getMapping() { return this.mapping; }

    /**
     * Checks if this key mapping is not the default value.
     * @return Whether this key mapping can be set to a default state.
     */
    public boolean isResettable() { return !this.mapping.isDefault(); }

    /**
     * Checks if this key mapping is currently being modified.
     * @return Returns the {@link KeyBindButton#isModifying} flag.
     */
    public boolean isModifying() { return this.isModifying; }

    /**
     * Change the key associated with this controller's key mapping.
     * @param keyCode A key code.
     * @param scanCode A key scancode.
     */
    public void setKey(int keyCode, int scanCode)
    {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE)
            Minecraft.getInstance().options.setKey(this.mapping, InputConstants.UNKNOWN);
        else
            Minecraft.getInstance().options.setKey(this.mapping, InputConstants.getKey(keyCode, scanCode));

        KeyMapping.resetMapping();

        this.isModifying = false;
    }

    /**
     * Reset the key mapping that is associated with this controller.
     */
    public void reset()
    {
        Minecraft.getInstance().options.setKey(this.mapping, this.mapping.getDefaultKey());
        KeyMapping.resetMapping();

        this.isModifying = false;
    }

    /* Rendering */

    /**
     * Render the tags associated with key bind buttons.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    private void renderTags(GuiGraphics graphics, int mouseX, int mouseY)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        if (screen == null)
            return;

        Component title = Component.translatable(this.mapping.getName());
        Component syncTag = Component.translatable(LangUtil.Gui.TAG_SYNC);
        Component syncTooltip = Component.translatable(LangUtil.Gui.TAG_SYNC_TOOLTIP);
        Component autoTag = Component.translatable(LangUtil.Gui.TAG_AUTO);
        Component autoTooltip = Component.translatable(LangUtil.Gui.TAG_AUTO_TOOLTIP);

        int startX = ConfigRowList.getStartX() + minecraft.font.width(title) + 4;
        int startY = this.getY() + 4;
        int lastX = startX;

        TweakTag.renderTooltip(screen, graphics, syncTag, syncTooltip, lastX, startY, mouseX, mouseY);

        lastX = TweakTag.renderTag(graphics, syncTag, lastX, startY, TweakTag.U_KEY_OFFSET);

        TweakTag.renderTooltip(screen, graphics, autoTag, autoTooltip, lastX, startY, mouseX, mouseY);
        TweakTag.renderTag(graphics, autoTag, lastX, startY, TweakTag.U_SYNC_OFFSET);
    }

    /**
     * Handler method for rendering this controller button.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.setMessage(this.mapping.getTranslatedKeyMessage());

        if (this.isModifying)
        {
            this.setMessage
            (
                Component.literal("> ")
                    .append(this.mapping.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW))
                    .append(" <")
                    .withStyle(ChatFormatting.YELLOW)
            );
        }
        else if (this.mapping.isUnbound())
            this.setMessage(Component.translatable(LangUtil.Key.UNBOUND).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
        else if (KeyUtil.isMappingConflict(this.mapping))
            this.setMessage(this.mapping.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.RED));

        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        this.renderTags(graphics, mouseX, mouseY);
    }
}
