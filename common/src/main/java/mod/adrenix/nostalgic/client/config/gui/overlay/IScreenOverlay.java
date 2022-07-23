package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IScreenOverlay
{
    void generateWidgets();
    void onResize();
    void onClose();
    boolean isOpen();
    boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY);
    boolean onClick(double mouseX, double mouseY, int button);
    boolean onKeyPressed(int keyCode, int scanCode, int modifiers);
    boolean onMouseScrolled(double mouseX, double mouseY, double delta);
    void onMouseReleased(double mouseX, double mouseY, int button);
    void onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick);
}
