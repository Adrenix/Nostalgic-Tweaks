package mod.adrenix.nostalgic.client.config;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public abstract class ClientKeyMapping
{
    public static final KeyMapping CONFIG_KEY = new KeyMapping
    (
        LangUtil.Key.OPEN_CONFIG,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        LangUtil.Key.CATEGORY_NAME
    );

    public static final KeyMapping FOG_KEY = new KeyMapping
    (
        LangUtil.Key.TOGGLE_FOG,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        LangUtil.Key.CATEGORY_NAME
    );
}
