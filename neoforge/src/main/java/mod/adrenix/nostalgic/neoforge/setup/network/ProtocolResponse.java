package mod.adrenix.nostalgic.neoforge.setup.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ProtocolResponse(String version) implements CustomPacketPayload
{
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(NostalgicTweaks.MOD_ID, "protocol_response");
    public static final CustomPacketPayload.Type<ProtocolResponse> TYPE = new CustomPacketPayload.Type<>(IDENTIFIER);
    public static final StreamCodec<FriendlyByteBuf, ProtocolResponse> CODEC = CustomPacketPayload.codec(ProtocolResponse::encode, ProtocolResponse::new);

    public ProtocolResponse(final FriendlyByteBuf buffer)
    {
        this(buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(NostalgicTweaks.PROTOCOL);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
