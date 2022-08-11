package mod.adrenix.nostalgic.client.config.gui.widget.group;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class RadioGroup<E extends Enum<E>> extends ClickableWidget
{
    private final ConfigRowList list;
    private final Class<E> radios;
    private final Enum<E> defaultValue;
    private final Supplier<Enum<E>> currentValue;
    private final Function<Enum<E>, Text> label;
    private final OnPress<E> onPress;
    private ArrayList<ConfigRowList.Row> cache;

    public interface OnPress<E extends Enum<E>> { void press(Enum<E> selected); }

    public RadioGroup(ConfigRowList list, Class<E> radios, Enum<E> defaultValue, Supplier<Enum<E>> currentValue, Function<Enum<E>, Text> label, OnPress<E> onPress)
    {
        super(
            ConfigRowList.TEXT_START,
            0,
            0,
            ConfigRowList.BUTTON_HEIGHT,
            Text.empty()
        );

        this.list = list;
        this.radios = radios;
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
        this.label = label;
        this.onPress = onPress;
    }

    public ArrayList<ConfigRowList.Row> getRows()
    {
        if (this.cache != null) return this.cache;

        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
        this.cache = rows;

        EnumSet<E> options = EnumSet.allOf(this.radios);
        options.forEach((e) -> rows.add(new ConfigRowList.ManualRow(List.of(
            new Radio<>(e, this.defaultValue, this.currentValue, this.label, this.onPress)
        )).add()));

        return rows;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        ArrayList<ConfigRowList.Row> rows = this.getRows();
        ArrayList<Integer> found = new ArrayList<>();

        for (ConfigRowList.Row listChild : this.list.children())
        {
            for (ConfigRowList.Row enumChild : rows)
            {
                if (listChild.equals(enumChild))
                    found.add(1);
            }
        }

        if (found.size() != rows.size())
            this.getRows().forEach((row) -> this.list.children().add(row));
    }

    @Override public void appendNarrations(NarrationMessageBuilder narrationElementOutput) {}

    private static class Radio<E extends Enum<E>> extends ClickableWidget
    {
        private final Enum<E> instance;
        private final Enum<E> defaultValue;
        private final Supplier<Enum<E>> currentValue;
        private final Function<Enum<E>, Text> label;
        private final OnPress<E> onPress;

        public Radio(Enum<E> instance, Enum<E> defaultValue, Supplier<Enum<E>> currentValue, Function<Enum<E>, Text> label, OnPress<E> onPress)
        {
            super(ConfigRowList.TEXT_START, 0, ConfigRowList.BUTTON_HEIGHT, ConfigRowList.BUTTON_HEIGHT, Text.empty());

            this.instance = instance;
            this.defaultValue = defaultValue;
            this.currentValue = currentValue;
            this.label = label;
            this.onPress = onPress;
        }

        public boolean isSelected() { return this.instance.equals(this.currentValue.get()); }
        public boolean isDefault() { return !this.isSelected() && this.instance.equals(this.defaultValue); }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            this.onPress.press(this.instance);
            super.onClick(mouseX, mouseY);
        }

        @Override
        public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
            MinecraftClient minecraft = MinecraftClient.getInstance();
            Screen screen = minecraft.currentScreen;
            if (screen == null) return;

            int uOffset = 0;
            int vOffset = 103;
            int uWidth = 20;
            int vHeight = 20;

            if (this.isMouseOver(mouseX, mouseY))
            {
                uOffset = 20;
                vOffset = this.isSelected() ? 63 : vOffset;
            }
            else if (this.isSelected())
                vOffset = 63;

            screen.drawTexture(poseStack, this.x, this.y, uOffset, vOffset, uWidth, vHeight);

            Text defaultText = Text.literal(this.isDefault() ? String.format(" (%s)", Text.translatable(NostalgicLang.Gui.DEFAULT).getString()) : "");
            Text optionText = Text.literal(this.label.apply(this.instance).getString() + defaultText.getString());

            RadioGroup.drawTextWithShadow(poseStack, MinecraftClient.getInstance().textRenderer, optionText, this.x + 24, this.y + (this.height - 8) / 2, 0xFFFFFF);
        }

        @Override public void appendNarrations(NarrationMessageBuilder narrationElementOutput) {}
    }
}
