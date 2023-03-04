package mod.adrenix.nostalgic.fabric.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Do <b>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors during the
 * mixin application process.
 */

public class MixinFlywheelPlugin implements IMixinConfigPlugin
{
    /* Fields */

    private final boolean isFlywheelPresent = FabricLoader.getInstance().getModContainer("flywheel").isPresent();
    private final boolean isSodiumPresent = FabricLoader.getInstance().getModContainer("sodium").isPresent();

    /* Methods */

    /**
     * The result of this method determines whether mixins are applied to the target class.
     * @param targetClassName Fully qualified class name of the target class.
     * @param mixinClassName Fully qualified class name of the mixin.
     * @return Whether the mixin instructions from the mixin class is applied to the target class.
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (mixinClassName.equals("SodiumWorldRendererMixin"))
            return this.isFlywheelPresent && this.isSodiumPresent;

        return this.isFlywheelPresent;
    }

    /* Required Plugin Overrides */

    @Override public void onLoad(String mixinPackage) { }
    @Override public List<String> getMixins() { return null; }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
