package mod.adrenix.nostalgic.neoforge.event;

import mod.adrenix.nostalgic.mixin.util.candy.TorchMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.neoforged.bus.api.SubscribeEvent;
import org.embeddedt.embeddium.api.BlockRendererRegistry;
import org.embeddedt.embeddium.api.render.chunk.RenderSectionDistanceFilter;
import org.embeddedt.embeddium.api.render.chunk.RenderSectionDistanceFilterEvent;

public abstract class EmbeddiumHandler
{
    /**
     * Implements old square border support for Embeddium. This modifies the distance filter to mimic the old chessboard
     * chunk range.
     *
     * @param event The {@link RenderSectionDistanceFilterEvent} instance.
     */
    @SubscribeEvent
    public static void onRenderSectionDistanceEvent(RenderSectionDistanceFilterEvent event)
    {
        final RenderSectionDistanceFilter filter = event.getFilter();

        event.setFilter((dx, dy, dz, maxDistance) -> {
            if (CandyTweak.OLD_SQUARE_BORDER.get())
                return Math.max(Math.abs(dx), Math.abs(dz)) <= maxDistance + 1 && Math.abs(dy) < maxDistance;
            else
                return filter.isWithinDistance(dx, dy, dz, maxDistance);
        });
    }

    /**
     * Initialize the Embeddium handler.
     */
    public static void init()
    {
        BlockRendererRegistry.instance().registerRenderPopulator((resultList, context) -> {
            if (TorchMixinHelper.isNotLikeTorch(context.state()))
                return;

            resultList.add((ctx, random, consumer) -> {
                if (TorchMixinHelper.isLikeTorch(ctx.state()))
                {
                    TorchMixinHelper.writeVertices(ctx.stack(), ctx.world(), consumer, ctx.model(), ctx.state(), ctx.pos(), random);

                    return BlockRendererRegistry.RenderResult.OVERRIDE;
                }

                return BlockRendererRegistry.RenderResult.PASS;
            });
        });
    }
}
