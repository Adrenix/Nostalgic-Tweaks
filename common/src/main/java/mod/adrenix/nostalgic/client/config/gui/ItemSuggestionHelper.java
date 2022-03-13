package mod.adrenix.nostalgic.client.config.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mod.adrenix.nostalgic.client.config.gui.screen.CustomizeScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec2;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ItemSuggestionHelper
{
    private final Minecraft minecraft;
    private final CustomizeScreen screen;
    private final EditBox input;
    private final Font font;
    private final int suggestionLineLimit;
    private final int fillColor;
    private final List<FormattedCharSequence> listOfItems = Lists.newArrayList();
    private ParseResults<SharedSuggestionProvider> currentParse;
    private CompletableFuture<Suggestions> pendingSuggestions;
    private ItemSuggestions suggestions;
    private boolean allowSuggestions;
    private boolean keepSuggestions;

    public ItemSuggestionHelper(CustomizeScreen screen, EditBox input, Font font, int suggestionLineLimit, int fillColor)
    {
        this.minecraft = screen.getMinecraft();
        this.screen = screen;
        this.input = input;
        this.font = font;
        this.suggestionLineLimit = suggestionLineLimit;
        this.fillColor = fillColor;
    }

    public Item getItem()
    {
        ItemParser parser = new ItemParser(new StringReader(this.input.getValue()), false);
        try
        {
            parser.readItem();
        }
        // Don't need to handle this since there will obviously be instances of invalid input.
        catch (CommandSyntaxException ignored) {}
        return parser.getItem();
    }

    public boolean isSuggesting() { return this.suggestions != null; }

    public void setAllowSuggestions(boolean flag)
    {
        this.allowSuggestions = flag;
        if (!flag)
            this.suggestions = null;
    }

    public boolean keyPressed(int key)
    {
        if (this.suggestions != null && this.suggestions.keyPressed(key))
            return true;
        else if (this.screen.getFocused() == this.input && key == 258)
        {
            this.showSuggestions();
            return true;
        }
        else
            return false;
    }

    public boolean mouseScrolled(double v)
    {
        return this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp(v, -1.0D, 1.0D));
    }

    public boolean mouseClicked(double x, double y)
    {
        return this.suggestions != null && this.suggestions.mouseClicked((int) x, (int) y);
    }

    public void showSuggestions()
    {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone())
        {
            Suggestions suggestions = this.pendingSuggestions.join();
            if (!suggestions.isEmpty())
            {
                int x = this.input.x;
                int y = this.input.y + 20;
                int width = this.input.getWidth() + 1;
                this.suggestions = new ItemSuggestionHelper.ItemSuggestions(x, y, width, this.sortSuggestions(suggestions));
            }
        }
    }

    private List<Suggestion> sortSuggestions(Suggestions suggestions)
    {
        String check = this.input.getValue().substring(0, this.input.getCursorPosition());
        String path = check.toLowerCase(Locale.ROOT);
        List<Suggestion> sorted = Lists.newArrayList();
        List<Suggestion> remainder = Lists.newArrayList();

        for (Suggestion suggestion : suggestions.getList())
        {
            if (!suggestion.getText().startsWith(check) && !suggestion.getText().startsWith("minecraft:" + path))
                remainder.add(suggestion);
            else
                sorted.add(suggestion);
        }

        sorted.addAll(remainder);
        return sorted;
    }

    public void resetInputBox()
    {
        this.input.setValue("");
        this.resetItemSuggestions();
    }

    private void resetItemSuggestions()
    {
        this.input.setSuggestion(null);
        this.suggestions = null;
    }

    public void updateItemSuggestions()
    {
        if (this.input.getValue().length() == 0)
        {
            this.resetItemSuggestions();
            return;
        }

        String in = this.input.getValue();
        StringReader reader = new StringReader(in);
        SuggestionsBuilder builder = new SuggestionsBuilder(in, 0);
        ItemParser parser = new ItemParser(reader, false);

        if (this.currentParse != null && !this.currentParse.getReader().getString().equals(in))
            this.currentParse = null;

        if (!this.keepSuggestions)
            this.resetItemSuggestions();

        this.listOfItems.clear();

        try
        {
            parser.parse();
        }
        // Unnecessary to handle this exception.
        catch (CommandSyntaxException ignored) {}

        this.pendingSuggestions = parser.fillSuggestions(builder, Registry.ITEM);
        this.pendingSuggestions.thenRun(() -> {
            if (this.pendingSuggestions.isDone())
                this.updateItemInfo();
        });
    }

    private void updateItemInfo()
    {
        if (this.listOfItems.isEmpty())
            this.fillItemSuggestions();

        this.suggestions = null;

        if (!this.listOfItems.isEmpty())
            if (this.allowSuggestions)
                this.showSuggestions();
    }

    private void fillItemSuggestions()
    {
        List<FormattedCharSequence> list = Lists.newArrayList();
        List<Suggestion> suggestions;
        Style style = Style.EMPTY.withColor(ChatFormatting.GRAY);
        int i = 0;
        int count = 0;

        try
        {
            suggestions = this.pendingSuggestions.get().getList();
        }
        catch (ExecutionException | InterruptedException ignored)
        {
            // Unnecessary to handle these errors so just exit the method if an error is thrown.
            return;
        }

        for (Suggestion suggestion : suggestions)
        {
            if (suggestion.getText().equals("{"))
                continue;

            list.add(FormattedCharSequence.forward(suggestion.getText(), style));
            i = Math.max(i, this.font.width(suggestion.getText()));

            count++;
            if (count >= this.suggestionLineLimit)
                break;
        }

        if (!list.isEmpty())
            this.listOfItems.addAll(list);
    }

    public void render(PoseStack stack, int x, int y)
    {
        if (this.suggestions != null)
            this.suggestions.render(stack, x, y);

        if (this.screen.getAddItemButton() != null)
            this.screen.getAddItemButton().active = this.getItem() != null;
    }

    public class ItemSuggestions
    {
        private final Rect2i rectangle;
        private final String originalContents;
        private final List<Suggestion> suggestionList;
        private int offset;
        private int current;
        private Vec2 lastMouse;
        private boolean tabCycles;

        public ItemSuggestions(int x, int y, int w, List<Suggestion> list)
        {
            this.lastMouse = Vec2.ZERO;
            this.rectangle = new Rect2i(x - 1, y, w + 1, Math.min(list.size(), ItemSuggestionHelper.this.suggestionLineLimit) * 12);
            this.originalContents = ItemSuggestionHelper.this.input.getValue();
            this.suggestionList = list;
            this.select(0);
        }

        public void render(PoseStack stack, int x, int y)
        {
            int i = Math.min(this.suggestionList.size(), ItemSuggestionHelper.this.suggestionLineLimit);

            boolean isScrolledAbove = this.offset > 0;
            boolean isScrolledBelow = this.suggestionList.size() > this.offset + 1;
            boolean isScrolled = isScrolledAbove || isScrolledBelow;
            boolean isMouseChanged = this.lastMouse.x != (float) x || this.lastMouse.y != (float) y;

            if (isMouseChanged)
                this.lastMouse = new Vec2((float) x, (float) y);

            if (isScrolled)
            {
                GuiComponent.fill(stack, this.rectangle.getX(), this.rectangle.getY() - 1, this.rectangle.getX() + this.rectangle.getWidth(), this.rectangle.getY(), ItemSuggestionHelper.this.fillColor);
                GuiComponent.fill(stack, this.rectangle.getX(), this.rectangle.getY() + this.rectangle.getHeight(), this.rectangle.getX() + this.rectangle.getWidth(), this.rectangle.getY() + this.rectangle.getHeight() + 1, ItemSuggestionHelper.this.fillColor);

                if (isScrolledAbove)
                {
                    for (int k = 0; k < this.rectangle.getWidth(); k++)
                        if (k % 2 == 0)
                            GuiComponent.fill(stack, this.rectangle.getX() + k, this.rectangle.getY() - 1, this.rectangle.getX() + k + 1, this.rectangle.getY(), -1);
                }

                if (isScrolledBelow)
                {
                    for (int dx = 0; dx < this.rectangle.getWidth(); dx++)
                        if (dx % 2 == 0)
                            GuiComponent.fill(stack, this.rectangle.getX() + dx, this.rectangle.getY() + this.rectangle.getHeight(), this.rectangle.getX() + dx + 1, this.rectangle.getY() + this.rectangle.getHeight() + 1, -1);
                }

                boolean isHovering = false;

                for (int line = 0; line < i; line++)
                {
                    Suggestion suggestion = this.suggestionList.get(line + this.offset);
                    GuiComponent.fill(stack, this.rectangle.getX(), this.rectangle.getY() + 12 * line, this.rectangle.getX() + this.rectangle.getWidth(), this.rectangle.getY() + 12 * line + 12, ItemSuggestionHelper.this.fillColor);

                    if (x > this.rectangle.getX() && x < this.rectangle.getX() + this.rectangle.getWidth() && y > this.rectangle.getY() + 12 * line && y < this.rectangle.getY() + 12 * line + 12)
                    {
                        if (isMouseChanged)
                            this.select(line + this.offset);

                        isHovering = true;
                    }

                    ItemSuggestionHelper.this.font.drawShadow(stack, suggestion.getText(), (float) (this.rectangle.getX() + 1), (float) (this.rectangle.getY() + 2 + 12 * line), line + this.offset == this.current ? -256 : -5592406);
                }

                if (isHovering)
                {
                    Message message = this.suggestionList.get(this.current).getTooltip();
                    if (message != null)
                        ItemSuggestionHelper.this.screen.renderTooltip(stack, ComponentUtils.fromMessage(message), x, y);
                }
            }
        }

        public boolean mouseClicked(int x, int y)
        {
            if (!this.rectangle.contains(x, y))
                return false;
            else
            {
                int i = (y - this.rectangle.getY()) / 12 + this.offset;
                if (i >= 0 && i < this.suggestionList.size())
                {
                    this.select(i);
                    this.useSuggestion();
                }

                return true;
            }
        }

        public boolean mouseScrolled(double dy)
        {
            int x = (int) (ItemSuggestionHelper.this.minecraft.mouseHandler.xpos() * (double) ItemSuggestionHelper.this.minecraft.getWindow().getGuiScaledWidth() / (double) ItemSuggestionHelper.this.minecraft.getWindow().getScreenWidth());
            int y = (int) (ItemSuggestionHelper.this.minecraft.mouseHandler.ypos() * (double) ItemSuggestionHelper.this.minecraft.getWindow().getGuiScaledHeight() / (double) ItemSuggestionHelper.this.minecraft.getWindow().getScreenHeight());

            if (this.rectangle.contains(x, y))
            {
                this.offset = Mth.clamp((int) ((double) this.offset - dy), 0, Math.max(this.suggestionList.size() - ItemSuggestionHelper.this.suggestionLineLimit, 0));
                return true;
            }
            else
                return false;
        }

        public boolean keyPressed(int key)
        {
            if (key == 265)
            {
                this.cycle(-1);
                this.tabCycles = false;
                return true;
            }
            else if (key == 264)
            {
                this.cycle(1);
                this.tabCycles = false;
                return true;
            }
            else if (key == 258)
            {
                if (this.tabCycles)
                    this.cycle(Screen.hasShiftDown() ? -1 : 1);

                this.useSuggestion();
                return true;
            }
            else if (key == 256)
            {
                this.hide();
                return true;
            }
            else
                return false;
        }

        public void cycle(int increment)
        {
            this.select(this.current + increment);

            int offset = this.offset;
            int limitOffset = this.offset + ItemSuggestionHelper.this.suggestionLineLimit - 1;

            if (this.current < offset)
                this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestionList.size() - ItemSuggestionHelper.this.suggestionLineLimit, 0));
            else if (this.current > limitOffset)
                this.offset = Mth.clamp(this.current + 1 - ItemSuggestionHelper.this.suggestionLineLimit, 0, Math.max(this.suggestionList.size() - ItemSuggestionHelper.this.suggestionLineLimit, 0));
        }

        private static String calculateSuggestionSuffix(String in, String suggest)
        {
            return suggest.startsWith(in) ? suggest.substring(in.length()) : null;
        }

        public void select(int i)
        {
            this.current = i;

            if (this.current < 0)
                this.current += this.suggestionList.size();

            if (this.current >= this.suggestionList.size())
                this.current -= this.suggestionList.size();

            Suggestion suggestion = this.suggestionList.get(this.current);
            ItemSuggestionHelper.this.input.setSuggestion(calculateSuggestionSuffix(ItemSuggestionHelper.this.input.getValue(), suggestion.apply(this.originalContents)));
        }

        public void useSuggestion()
        {
            Suggestion suggestion = this.suggestionList.get(this.current);
            ItemSuggestionHelper.this.keepSuggestions = true;
            ItemSuggestionHelper.this.input.setValue(suggestion.apply(this.originalContents));

            int startX = suggestion.getRange().getStart() + suggestion.getText().length();
            ItemSuggestionHelper.this.input.setCursorPosition(startX);
            ItemSuggestionHelper.this.input.setHighlightPos(startX);

            this.select(this.current);

            ItemSuggestionHelper.this.keepSuggestions = false;
            this.tabCycles = true;
        }

        public void hide() { ItemSuggestionHelper.this.suggestions = null; }
    }
}