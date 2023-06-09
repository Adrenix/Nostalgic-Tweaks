package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowGroup;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * A container button handles subscribed configuration rows that are defined by a config row container type or rows that
 * are defined manually.
 *
 * The rows that are put into container buttons are defined in the client config or manually assigned in a different
 * class.
 */

public class ContainerButton extends Button
{
    /* Expansion Cache */

    /**
     * Since container buttons are identified by an enumeration value, any container buttons with the same identifier
     * will be linked together. This will cause their expansions and collapses to be in sync. If this behavior is not
     * desired, then it is important to ensure container button enumeration identifiers are unique.
     */
    private static final Map<Enum<?>, Boolean> EXPANDED = new HashMap<>();

    /* Fields */

    private final Enum<?> id;
    private final ConfigRowGroup.ContainerRow row;
    private final Component title;

    private boolean grandparentTreeNeeded;
    private boolean parentTreeNeeded;
    private boolean lastSubcategory;
    private boolean highlighted;

    /* Constructor */

    /**
     * Create a new container button for a config row list container.
     * @param row The row instance associated with this button.
     * @param id An enumeration identifier that will be used in the expanded map state tracker.
     * @param title A component title for this container button.
     * @param containerType The config row list container type for this button.
     */
    public ContainerButton(ConfigRowGroup.ContainerRow row, Enum<?> id, Component title, ConfigRowGroup.ContainerType containerType)
    {
        super(ConfigRowGroup.ContainerRow.getIndent(containerType), 0, 0, 0, Component.empty(), RunUtil::nothing, DEFAULT_NARRATION);

        this.id = id;
        this.row = row;
        this.title = title;
        this.width = 18;
        this.height = 16;

        EXPANDED.putIfAbsent(id, false);
    }

    /* Utility Methods */

    /**
     * Expand a container.
     * @param id A container identifier.
     */
    public static void expand(Enum<?> id)
    {
        if (EXPANDED.containsKey(id))
            EXPANDED.put(id, true);
    }

    /**
     * Collapse a container.
     * @param id A container identifier.
     */
    public static void collapse(Enum<?> id)
    {
        if (EXPANDED.containsKey(id))
            EXPANDED.put(id, false);
    }

    /**
     * Collapses if expanded and expands if collapsed.
     */
    private void toggle() { EXPANDED.put(this.id, !EXPANDED.get(this.id)); }

    /**
     * Remove any subscribed rows that are visible from a previous expansion.
     */
    public void collapse()
    {
        if (this.isExpanded())
        {
            this.toggle();
            this.row.collapse();
        }
    }

    /**
     * Collapse every container button defined in the expanded map.
     */
    public static void collapseAll() { EXPANDED.forEach((id, state) -> EXPANDED.put(id, false)); }

    /**
     * Initialize a container by having its rows expanded/collapsed as needed.
     *
     * When a container renders, it checks if it should be expanded/collapsed. During item searching, the row list gets
     * reset. A container will be regenerated with its row already collapsed. During the first rendering cycle, the
     * container will expand since it was regenerated already collapsed. The next cycle will render the expanded rows.
     * This will cause a "flash" effect, which is not desirable. This initializer method will perform this check before
     * the list renders all its rows. See the rendering method below:
     *
     * {@link mod.adrenix.nostalgic.client.config.gui.widget.list.AbstractRowList#render(PoseStack, int, int, float)}
     */
    public void init()
    {
        if (this.isExpanded() && !this.row.isExpanded())
            this.row.expand();

        if (!this.isExpanded() && this.row.isExpanded())
            this.row.collapse();
    }

    /**
     * @return The title of this container button as a component.
     */
    public Component getTitle() { return this.title; }

    /**
     * Informs the config list renderer that this container button requires tree lines outward from an embedded
     * container.
     *
     * @param state Whether an embedded grandparent tree is needed.
     */
    public void setGrandparentTreeNeeded(boolean state) { this.grandparentTreeNeeded = state; }

    /**
     * Informs the config list renderer that this container button requires tree lines outward from a subcategory
     * container.
     *
     * @param state Whether a subcategory parent tree is needed.
     */
    public void setParentTreeNeeded(boolean state) { this.parentTreeNeeded = state; }

    /**
     * Informs the config list renderer that this container button is the last subcategory and no further rendering is
     * needed.
     *
     * @param state Whether this container is the last subcategory.
     */
    public void setLastSubcategory(boolean state) { this.lastSubcategory = state; }

    /**
     * Informs the container button renderer whether the text for this widget should be highlighted or not.
     * @param state A flag that controls text highlighting.
     */
    public void setHighlight(boolean state) { this.highlighted = state; }

    /**
     * @return Check if an embedded tree line is needed.
     */
    public boolean isGrandparentTreeNeeded() { return this.grandparentTreeNeeded; }

    /**
     * @return Check if a subcategory tree line is needed.
     */
    public boolean isParentTreeNeeded() { return this.parentTreeNeeded; }

    /**
     * @return Check if this container is the last subcategory.
     */
    public boolean isLastSubcategory() { return this.lastSubcategory; }

    /**
     * @return Check if this container button is showing its subscribed rows.
     */
    public boolean isExpanded() { return EXPANDED.get(this.id); }

    /**
     * In automatically generated container buttons, this is category, subcategory, or embedded value. These are defined
     * in annotations in the client config. Manually created container buttons have their own unique identifiers. These
     * are defined in other classes.
     *
     * @return Get the enumeration value of this container button.
     */
    public Enum<?> getId() { return this.id; }

    /**
     * Collapses or expands a container button without making a button click sound.
     * No expansion or collapse checks are required before invoking.
     */
    public void silentPress()
    {
        if (this.isExpanded())
            this.row.collapse();
        else
            this.row.expand();

        this.toggle();
    }

    /* Widget Overrides */

    /**
     * Handler method for when a container button is pressed.
     */
    @Override
    public void onPress()
    {
        this.silentPress();
        super.onPress();
    }

    /**
     * Handler method for when a key is pressed.
     * @param keyCode The code of the key that was pressed.
     * @param scanCode The scan code.
     * @param modifiers Any key modifiers.
     * @return Whether this method handled the pressed key event.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (KeyUtil.isEnter(keyCode) && this.isFocused() && this.isActive())
        {
            this.silentPress();
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        return false;
    }

    /**
     * Rendering instructions for the container button widget.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        if (screen == null)
            return;

        boolean expanded = this.isExpanded();

        if (expanded && !this.row.isExpanded())
            this.row.expand();

        if (!expanded && this.row.isExpanded())
            this.row.collapse();

        int uOffset = 33;
        int vOffset = 0;
        int uWidth = 12;
        int vHeight = 18;
        int blitX = this.getX();
        int blitY = this.getY();
        int color = this.highlighted ? 0xFFAA00 : 0xFFFFFF;
        boolean isMouseOver = this.isMouseOver(expanded ? mouseX + 4 : mouseX, expanded ? mouseY - 4 : mouseY) || this.isFocused();

        if (isMouseOver)
        {
            uOffset = expanded ? 47 : 33;
            vOffset = 23;
        }
        else if (expanded)
            uOffset = 47;

        if (expanded)
        {
            uWidth = 18;
            vHeight = 12;
            blitX = this.getX() - 4;
            blitY = this.getY() + 4;
        }

        this.width = 20 + minecraft.font.width(this.title);
        this.active = !Overlay.isOpened();

        graphics.blit(TextureLocation.WIDGETS, blitX, blitY, uOffset, vOffset, uWidth, vHeight);
        graphics.drawString(minecraft.font, this.title, this.getX() + 20, this.getY() + 5, isMouseOver ? 0xFFD800 : color);
    }
}
