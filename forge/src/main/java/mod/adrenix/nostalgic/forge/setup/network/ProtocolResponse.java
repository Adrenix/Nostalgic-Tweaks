package mod.adrenix.nostalgic.forge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ProtocolResponse(String version) implements CustomPacketPayload
{
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(NostalgicTweaks.MOD_ID, "protocol_response");

    public ProtocolResponse(final FriendlyByteBuf buffer)
    {
        this(buffer.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(NostalgicTweaks.PROTOCOL);
    }

    @Override
    public ResourceLocation id()
    {
        return IDENTIFIER;
    }
}
