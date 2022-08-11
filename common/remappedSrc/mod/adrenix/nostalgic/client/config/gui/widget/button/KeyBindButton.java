package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.TweakTag;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class KeyBindButton extends ControlButton
{
    private final KeyBinding mapping;
    private boolean isModifying = false;

    public KeyBindButton(KeyBinding mapping)
    {
        super(Text.empty(), KeyBindButton::edit);
        this.mapping = mapping;
    }

    private static void edit(ButtonWidget button)
    {
        ((KeyBindButton) button).isModifying = !((KeyBindButton) button).isModifying;
    }

    public static boolean isMappingConflicted(KeyBinding mapping)
    {
        if(!mapping.isUnbound())
        {
            KeyBinding[] allMappings = MinecraftClient.getInstance().options.allKeys;

            for (KeyBinding keyMapping : allMappings)
            {
                if (keyMapping != mapping && mapping.equals(keyMapping))
                    return true;
            }
        }

        return false;
    }

    public KeyBinding getMapping() { return this.mapping; }
    public boolean isResettable() { return !this.mapping.isDefault(); }
    public boolean isModifying() { return this.isModifying; }

    public void setKey(int keyCode, int scanCode)
    {
        if (keyCode == 256)
            MinecraftClient.getInstance().options.setKeyCode(this.mapping, InputUtil.UNKNOWN_KEY);
        else
            MinecraftClient.getInstance().options.setKeyCode(this.mapping, InputUtil.fromKeyCode(keyCode, scanCode));

        KeyBinding.updateKeysByCode();
        this.isModifying = false;
    }


    public void reset()
    {
        MinecraftClient.getInstance().options.setKeyCode(this.mapping, this.mapping.getDefaultKey());
        KeyBinding.updateKeysByCode();
        this.isModifying = false;
    }

    private void renderTags(MatrixStack poseStack, int mouseX, int mouseY)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Screen screen = minecraft.currentScreen;
        if (screen == null) return;

        Text title = Text.translatable(this.mapping.getTranslationKey());
        Text syncTag = Text.translatable(NostalgicLang.Gui.TAG_SYNC);
        Text syncTooltip = Text.translatable(NostalgicLang.Gui.TAG_SYNC_TOOLTIP);
        Text autoTag = Text.translatable(NostalgicLang.Gui.TAG_AUTO);
        Text autoTooltip = Text.translatable(NostalgicLang.Gui.TAG_AUTO_TOOLTIP);

        int startX = ConfigRowList.getStartX() + minecraft.textRenderer.getWidth(title) + 4;
        int startY = this.y + 4;
        int lastX = startX;

        TweakTag.renderTooltip(screen, poseStack, syncTag, syncTooltip, lastX, startY, mouseX, mouseY);
        lastX = TweakTag.renderTag(screen, poseStack, syncTag, lastX, startY, TweakTag.U_KEY_OFFSET);

        TweakTag.renderTooltip(screen, poseStack, autoTag, autoTooltip, lastX, startY, mouseX, mouseY);
        TweakTag.renderTag(screen, poseStack, autoTag, lastX, startY, TweakTag.U_SYNC_OFFSET);
    }

    @Override
    public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.setMessage(this.mapping.getBoundKeyLocalizedText());
        if (this.isModifying)
            this.setMessage((Text.literal("> ")).append(this.mapping.getBoundKeyLocalizedText().shallowCopy().formatted(Formatting.YELLOW)).append(" <").withStyle(Formatting.YELLOW));
        else if (this.mapping.isUnbound())
            this.setMessage(Text.translatable(NostalgicLang.Key.UNBOUND).withStyle(Formatting.RED).withStyle(Formatting.ITALIC));
        else if (isMappingConflicted(this.mapping))
            this.setMessage(this.mapping.getBoundKeyLocalizedText().shallowCopy().formatted(Formatting.RED));

        super.renderButton(poseStack, mouseX, mouseY, partialTick);
        this.renderTags(poseStack, mouseX, mouseY);
    }
}
