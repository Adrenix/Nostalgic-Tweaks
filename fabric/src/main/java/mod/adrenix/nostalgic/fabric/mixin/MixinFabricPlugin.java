package mod.adrenix.nostalgic.fabric.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinFabricPlugin implements IMixinConfigPlugin
{
    private boolean isSodiumPresent = true;

    @Override
    public void onLoad(String mixinPackage)
    {
        isSodiumPresent = FabricLoader.getInstance().getModContainer("sodium").isPresent();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (mixinClassName.equals("mod.adrenix.nostalgic.fabric.mixin.client.ItemColorsSodiumMixin"))
            return isSodiumPresent;
        return true;
    }

    @Override public List<String> getMixins() { return null; }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
