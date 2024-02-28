package mod.adrenix.nostalgic.mixin.util.candy.inventory;

import mod.adrenix.nostalgic.mixin.access.AbstractContainerScreenAccess;
import mod.adrenix.nostalgic.mixin.access.ScreenAccess;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.InventoryShield;
import mod.adrenix.nostalgic.tweak.enums.RecipeBook;
import mod.adrenix.nostalgic.util.common.asset.ModAsset;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

/**
 * This utility class is used only by the client.
 */
public abstract class InventoryMixinHelper
{
    /* Sprites */

    private static final WidgetSprites SMALL_RECIPE_BUTTON = new WidgetSprites(ModAsset.sprite("recipe_book/button_small"), ModAsset.sprite("recipe_book/button_small_highlighted"));
    private static final WidgetSprites LARGE_RECIPE_BUTTON = new WidgetSprites(ModAsset.sprite("recipe_book/button_large"), ModAsset.sprite("recipe_book/button_large_highlighted"));

    /* Methods */

    /**
     * Set the inventory's screen slot positions based on tweak context.
     *
     * @param slots   A {@link NonNullList} of {@link Slot}.
     * @param recipes The inventory {@link RecipeBookComponent} instance.
     * @return The offhand {@link Slot} instance to use, or {@code null}.
     */
    @Nullable
    public static Slot setInventorySlots(NonNullList<Slot> slots, RecipeBookComponent recipes)
    {
        InventoryShield shield = CandyTweak.INVENTORY_SHIELD.get();
        Slot offHand = null;

        for (Slot slot : slots)
        {
            SlotTracker.CRAFT_TOP_LEFT.moveOrReset(slot, 88, 26);
            SlotTracker.CRAFT_TOP_RIGHT.moveOrReset(slot, 106, 26);
            SlotTracker.CRAFT_BOTTOM_LEFT.moveOrReset(slot, 88, 44);
            SlotTracker.CRAFT_BOTTOM_RIGHT.moveOrReset(slot, 106, 44);
            SlotTracker.CRAFT_RESULT.moveOrReset(slot, 144, 36);

            if (SlotTracker.OFF_HAND.isEqualTo(slot))
            {
                offHand = slot;

                if (InventoryShield.MODERN == shield)
                    SlotTracker.OFF_HAND.moveOrReset(slot, 152, 62);

                switch (shield)
                {
                    case INVISIBLE -> SlotTracker.OFF_HAND.move(slot, -9999, -9999);
                    case MIDDLE_RIGHT -> SlotTracker.OFF_HAND.move(slot, 152, 62);
                    case BOTTOM_LEFT ->
                    {
                        if (recipes.isVisible())
                            SlotTracker.OFF_HAND.move(slot, 174, 142);
                        else
                            SlotTracker.OFF_HAND.move(slot, -14, 142);
                    }
                }
            }
        }

        return offHand;
    }

    /**
     * Get a new image button that represents the "large" recipe button.
     *
     * @param inventory The inventory {@link AbstractContainerScreenAccess} instance.
     * @param original  The original {@link ImageButton} instance.
     * @return A new {@link ImageButton} instance.
     */
    private static ImageButton getLargeBook(AbstractContainerScreenAccess inventory, ImageButton original)
    {
        return new ImageButton(inventory.nt$getLeftPos() + 151, inventory.nt$getTopPos() + 7, 18, 18, LARGE_RECIPE_BUTTON, (button) -> {
            original.onPress();
            button.setPosition(inventory.nt$getLeftPos() + 151, inventory.nt$getTopPos() + 7);
        });
    }

    /**
     * Get a new image button that represents the "small" recipe button.
     *
     * @param inventory The inventory {@link AbstractContainerScreenAccess} instance.
     * @param original  The original {@link ImageButton} instance.
     * @return A new {@link ImageButton} instance.
     */
    private static ImageButton getSmallBook(AbstractContainerScreenAccess inventory, ImageButton original)
    {
        return new ImageButton(inventory.nt$getLeftPos() + 160, inventory.nt$getTopPos() + 7, 9, 10, SMALL_RECIPE_BUTTON, (button) -> {
            original.onPress();
            button.setPosition(inventory.nt$getLeftPos() + 160, inventory.nt$getTopPos() + 7);
        });
    }

    /**
     * Set the inventory screen's recipe button to use.
     *
     * @param inventory The inventory {@link AbstractContainerScreenAccess} instance.
     */
    public static void setRecipeButton(AbstractContainerScreenAccess inventory)
    {
        ImageButton recipeButton = null;
        RecipeBook book = CandyTweak.INVENTORY_BOOK.get();
        Screen screen = Minecraft.getInstance().screen;

        if (screen == null)
            throw new NullPointerException("Tried adding recipe book button to a null screen");

        for (GuiEventListener widget : screen.children())
        {
            if (widget instanceof ImageButton button)
            {
                recipeButton = button;
                break;
            }
        }

        if (recipeButton == null)
            return;

        switch (book)
        {
            case DISABLED ->
            {
                recipeButton.active = false;
                recipeButton.visible = false;
            }
            case LARGE ->
            {
                ((ScreenAccess) screen).nt$removeWidget(recipeButton);
                ((ScreenAccess) screen).nt$addRenderableWidget(getLargeBook(inventory, recipeButton));
            }
            case SMALL ->
            {
                ((ScreenAccess) screen).nt$removeWidget(recipeButton);
                ((ScreenAccess) screen).nt$addRenderableWidget(getSmallBook(inventory, recipeButton));
            }
        }
    }

    /**
     * Render the off-hand slot.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param recipes  The inventory {@link RecipeBookComponent} instance.
     * @param offHand  The off-hand {@link Slot}.
     * @param leftPos  The left-position of the screen.
     * @param height   The height of the screen.
     */
    public static void renderOffHandSlot(GuiGraphics graphics, RecipeBookComponent recipes, @Nullable Slot offHand, int leftPos, int height)
    {
        InventoryShield shield = CandyTweak.INVENTORY_SHIELD.get();
        boolean isModernOverride = CandyTweak.OLD_INVENTORY.get() && shield == InventoryShield.MODERN;

        if (InventoryShield.BOTTOM_LEFT == shield && offHand != null)
        {
            if (recipes.isVisible())
            {
                graphics.blit(TextureLocation.INVENTORY, leftPos + 172, height / 2 + 51, 200, 33, 26, 32);
                SlotTracker.OFF_HAND.move(offHand, 174, 142);
            }
            else
            {
                graphics.blit(TextureLocation.INVENTORY, leftPos - 22, height / 2 + 51, 200, 0, 25, 32);
                SlotTracker.OFF_HAND.move(offHand, -14, 142);
            }
        }
        else if (InventoryShield.MIDDLE_RIGHT == shield || isModernOverride)
            graphics.blit(TextureLocation.INVENTORY, leftPos + 151, height / 2 - 22, 178, 0, 18, 18);

        if (!CandyTweak.OLD_INVENTORY.get() && InventoryShield.MODERN != shield)
            graphics.blit(TextureLocation.INVENTORY, leftPos + 76, height / 2 - 22, 178, 18, 18, 18);
    }
}
