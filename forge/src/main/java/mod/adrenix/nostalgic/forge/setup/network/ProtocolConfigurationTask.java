package mod.adrenix.nostalgic.forge.setup.network;

import mod.adrenix.nostalgic.network.ModConnection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;

import java.util.function.Consumer;

public record ProtocolConfigurationTask(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask
{
    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ModConnection.PROTOCOL_ID);

    @Override
    public void run(Consumer<CustomPacketPayload> sender)
    {
        sender.accept(new ProtocolRequest());
    }

    @Override
    public Type type()
    {
        return TYPE;
    }
}
