package mod.adrenix.nostalgic.helper.candy.light;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import org.jetbrains.annotations.NotNull;

/**
 * This is a simple wrapper class around a chunk's {@link DataLayer} to help with old lighting. The purpose of this
 * approach is to support mods like Sodium and Embeddium without the need of a mixin into their world renderers.
 */
public class NostalgicDataLayer extends DataLayer
{
    /* Fields */

    private final DataLayer parent;
    private final LightLayer layer;
    private final long sectionPos;

    /* Constructor */

    /**
     * Create a new mod data layer wrapper instance.
     *
     * @param parent     The parent {@link DataLayer} instance to get light values from.
     * @param layer      The {@link LightLayer} this data layer is associated with.
     * @param sectionPos The packed coordinates of this layer's {@link SectionPos}.
     */
    public NostalgicDataLayer(DataLayer parent, LightLayer layer, long sectionPos)
    {
        this.parent = parent;
        this.layer = layer;
        this.sectionPos = sectionPos;
    }

    /* Methods */

    /**
     * @return Maintains the extra data provided by this layer extension.
     */
    @Override
    public @NotNull DataLayer copy()
    {
        return new NostalgicDataLayer(this.parent.copy(), this.layer, this.sectionPos);
    }

    /**
     * @return A modified light value based on the mod's current tweak lighting context.
     */
    @Override
    public int get(int x, int y, int z)
    {
        int lightValue = this.parent.get(x, y, z);

        if (GameUtil.isOnIntegratedSeverThread())
            return lightValue;

        return getLightValue(this.layer, SectionPos.of(this.sectionPos).origin().offset(x, y, z), lightValue);
    }

    /**
     * Get a light value based on the mod's current tweak lighting context.
     *
     * @param layer      The {@link LightLayer} this light value is for.
     * @param blockPos   The {@link BlockPos} that light value will be applied to.
     * @param lightValue The original vanilla light value.
     * @return A new light value to use at the given block position.
     */
    public static int getLightValue(LightLayer layer, BlockPos blockPos, int lightValue)
    {
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null)
            return lightValue;

        if (CandyTweak.OLD_CLASSIC_ENGINE.get())
        {
            if (layer == LightLayer.BLOCK)
                return 13;

            return LightingHelper.getClassicLight(lightValue, level, blockPos);
        }

        if (CandyTweak.ROUND_ROBIN_RELIGHT.get())
        {
            if (layer == LightLayer.BLOCK)
                return lightValue;

            return LightingHelper.getCombinedLight(lightValue, level.getBrightness(LightLayer.BLOCK, blockPos));
        }

        return lightValue;
    }

    /**
     * Implement other defaults, for mod compatibility reasons.
     * */

    @Override
    public void set(int x, int y, int z, int value) {
        parent.set(x, y, z, value);
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public void fill(int defaultValue) {
        parent.fill(defaultValue);
    }

    @Override
    public byte[] getData() {
        return parent.getData();
    }

    @Override
    public boolean isDefinitelyHomogenous() {
        return parent.isDefinitelyHomogenous();
    }

    @Override
    public boolean isDefinitelyFilledWith(int value) {
        return parent.isDefinitelyFilledWith(value);
    }
}
