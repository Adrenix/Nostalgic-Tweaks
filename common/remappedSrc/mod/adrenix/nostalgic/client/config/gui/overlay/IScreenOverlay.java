package mod.adrenix.nostalgic.client.config.gui.overlay;

import net.minecraft.client.util.math.MatrixStack;

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
    void onRender(MatrixStack poseStack, int mouseX, int mouseY, float partialTick);
}
