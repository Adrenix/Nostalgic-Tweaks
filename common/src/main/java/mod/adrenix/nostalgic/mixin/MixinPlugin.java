package mod.adrenix.nostalgic.mixin;

import mod.adrenix.nostalgic.util.common.ClassUtil;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * This plugin allows Forge to reach the "missing dependencies" startup screen.
 *
 * Some configuration entries need to be known when mixins are applied.
 * If AutoConfig or Architectury is missing, then mixins will be loaded with missing classes which will prevent Forge
 * from reaching this screen.
 *
 * Do <b>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors during the
 * mixin application process.
 *
 * For example, any mod that applies transformations to the <code>ResourceLocation</code> class will throw a mixin
 * application error because the <code>ResourceLocation</code> class was class loaded by this plugin before the mixin
 * processor could apply patches.
 */

public class MixinPlugin implements IMixinConfigPlugin
{
    private boolean isAutoConfigPresent = false;
    private boolean isArchitecturyPresent = false;

    @Override
    public void onLoad(String mixinPackage)
    {
        isAutoConfigPresent = ClassUtil.isAutoConfigPresent();
        isArchitecturyPresent = ClassUtil.isArchitecturyPresent();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        return isAutoConfigPresent && isArchitecturyPresent;
    }

    @Override public List<String> getMixins() { return null; }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
