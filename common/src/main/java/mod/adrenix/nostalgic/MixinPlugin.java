package mod.adrenix.nostalgic;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * This plugin allows Forge to reach the "missing dependencies" startup screen.
 *
 * Some configuration entries need to be known when mixins are applied.
 * If the cloth-config API is missing, then mixins will be loaded with missing
 * classes which will prevent Forge from reaching this screen.
 */

public class MixinPlugin implements IMixinConfigPlugin
{
    private boolean isClothPresent = true;

    @Override
    public void onLoad(String mixinPackage)
    {
        try
        {
            Class.forName("me.shedaniel.autoconfig.AutoConfig");
        }
        catch (ClassNotFoundException e)
        {
            isClothPresent = false;
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        return isClothPresent;
    }

    @Override public List<String> getMixins() { return null; }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
