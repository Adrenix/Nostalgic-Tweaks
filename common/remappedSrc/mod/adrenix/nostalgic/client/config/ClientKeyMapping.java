package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public abstract class ClientKeyMapping
{
    public static final KeyBinding CONFIG_KEY = new KeyBinding
    (
        NostalgicLang.Key.OPEN_CONFIG,
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        NostalgicLang.Key.CATEGORY_NAME
    );

    public static final KeyBinding FOG_KEY = new KeyBinding
    (
        NostalgicLang.Key.TOGGLE_FOG,
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        NostalgicLang.Key.CATEGORY_NAME
    );
}
