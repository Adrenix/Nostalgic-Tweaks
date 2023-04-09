package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.GenericOverlay;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.ComponentBackport;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ServerSideModeOverlay extends GenericOverlay
{
    /* Static Fields */

    public static final int OVERLAY_WIDTH = 270;
    public static final int OVERLAY_HEIGHT = 220;

    /* Overlay Fields */

    private TextWidget headerText;
    private TextWidget messageText;

    /* Constructor & Initialize */

    public ServerSideModeOverlay()
    {
        super(ComponentBackport.translatable(LangUtil.Gui.SSO_OVERLAY_TITLE), OVERLAY_WIDTH, OVERLAY_HEIGHT);

        this.setBackground(0xEF000000);
        this.init();
    }

    /* Methods */

    @Override
    public void init()
    {
        super.init();
        this.generateWidgets();
    }

    @Override
    public void generateWidgets()
    {
        int startX = this.getOverlayStartX() + 2;
        int startY = this.getOverlayStartY() + 4;

        String translation = ComponentBackport.translatable(LangUtil.Gui.SSO_OVERLAY_HEADER).getString();
        Component header = ComponentBackport.literal(ChatFormatting.UNDERLINE + translation);

        this.headerText = new TextWidget(header, TextAlign.CENTER, startX + 2, startY, this.getDrawWidth());

        Component message = ComponentBackport.translatable(LangUtil.Gui.SSO_OVERLAY_MESSAGE);
        int bottomY = this.headerText.getBottomY();

        this.messageText = new TextWidget(message, TextAlign.LEFT, startX, bottomY, this.getDrawWidth());
    }

    @Override
    public void onMainRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.headerText.render(poseStack, mouseX, mouseY, partialTick);
        this.messageText.render(poseStack, mouseX, mouseY, partialTick);
    }
}
