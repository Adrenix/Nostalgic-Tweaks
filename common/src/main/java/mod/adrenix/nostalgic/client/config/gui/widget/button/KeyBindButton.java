package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.TweakTag;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class KeyBindButton extends Button
{
    private final KeyMapping mapping;
    private boolean isModifying = false;

    public KeyBindButton(KeyMapping mapping)
    {
        super(ConfigRowList.getControlStartX(), 0, ConfigRowList.CONTROL_BUTTON_WIDTH, ConfigRowList.BUTTON_HEIGHT, TextComponent.EMPTY, KeyBindButton::edit);
        this.mapping = mapping;
    }

    private static void edit(Button button)
    {
        ((KeyBindButton) button).isModifying = !((KeyBindButton) button).isModifying;
    }

    public static boolean isMappingConflicted(KeyMapping mapping)
    {
        if(!mapping.isUnbound())
        {
            KeyMapping[] allMappings = Minecraft.getInstance().options.keyMappings;

            for (KeyMapping keyMapping : allMappings)
            {
                if (keyMapping != mapping && mapping.same(keyMapping))
                    return true;
            }
        }

        return false;
    }

    public KeyMapping getMapping() { return this.mapping; }
    public boolean isResettable() { return !this.mapping.isDefault(); }
    public boolean isModifying() { return this.isModifying; }

    public void setKey(int keyCode, int scanCode)
    {
        if (keyCode == 256)
            Minecraft.getInstance().options.setKey(this.mapping, InputConstants.UNKNOWN);
        else
            Minecraft.getInstance().options.setKey(this.mapping, InputConstants.getKey(keyCode, scanCode));

        KeyMapping.resetMapping();
        this.isModifying = false;
    }


    public void reset()
    {
        Minecraft.getInstance().options.setKey(this.mapping, this.mapping.getDefaultKey());
        KeyMapping.resetMapping();
        this.isModifying = false;
    }

    private void renderTags(PoseStack poseStack, int mouseX, int mouseY)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null) return;

        Component title = new TranslatableComponent(this.mapping.getName());
        Component syncTag = new TranslatableComponent(NostalgicLang.Gui.TAG_SYNC);
        Component syncTooltip = new TranslatableComponent(NostalgicLang.Gui.TAG_SYNC_TOOLTIP);
        Component autoTag = new TranslatableComponent(NostalgicLang.Gui.TAG_AUTO);
        Component autoTooltip = new TranslatableComponent(NostalgicLang.Gui.TAG_AUTO_TOOLTIP);

        int startX = ConfigRowList.TEXT_START + minecraft.font.width(title) + 4;
        int startY = this.y + 4;
        int lastX = startX;

        TweakTag.renderTooltip(screen, poseStack, syncTag, syncTooltip, lastX, startY, mouseX, mouseY);
        lastX = TweakTag.renderTag(screen, poseStack, syncTag, lastX, startY, TweakTag.U_KEY_OFFSET);

        TweakTag.renderTooltip(screen, poseStack, autoTag, autoTooltip, lastX, startY, mouseX, mouseY);
        TweakTag.renderTag(screen, poseStack, autoTag, lastX, startY, TweakTag.U_SYNC_OFFSET);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.setMessage(this.mapping.getTranslatedKeyMessage());
        if (this.isModifying)
            this.setMessage((new TextComponent("> ")).append(this.mapping.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
        else if (this.mapping.isUnbound())
            this.setMessage(new TranslatableComponent(NostalgicLang.Key.UNBOUND).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
        else if (isMappingConflicted(this.mapping))
            this.setMessage(this.mapping.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.RED));

        super.renderButton(poseStack, mouseX, mouseY, partialTick);
        this.renderTags(poseStack, mouseX, mouseY);
    }
}
