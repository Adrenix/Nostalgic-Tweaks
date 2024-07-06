package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.config.ClientConfig;
import mod.adrenix.nostalgic.config.ServerConfig;
import mod.adrenix.nostalgic.config.cache.ConfigReflect;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.config.factory.ConfigHandler;
import mod.adrenix.nostalgic.config.factory.ConfigMeta;
import mod.adrenix.nostalgic.network.packet.backup.ServerboundCreateBackup;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakMeta;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ManageThreadMessage
{
    JAVA_ERROR(MessageType.ERROR, Lang.Error.JAVA_TITLE, Lang.Error.JAVA_MESSAGE),
    IMPORT_ERROR(MessageType.RED_WARNING, Lang.Error.IMPORT_TITLE, Lang.Error.IMPORT_MESSAGE),
    DOWNLOAD_ERROR(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.DOWNLOAD_WRITER),
    IMPORT_CLIENT_SUCCESS(MessageType.SUCCESS, Lang.Info.IMPORT_CLIENT_TITLE, Lang.Info.IMPORT_CLIENT_MESSAGE),
    IMPORT_SERVER_SUCCESS(MessageType.SUCCESS, Lang.Info.IMPORT_SERVER_TITLE, Lang.Info.IMPORT_SERVER_MESSAGE),
    EXPORT_CLIENT_SUCCESS(MessageType.SUCCESS, Lang.Info.EXPORT_CLIENT_TITLE, Lang.Info.EXPORT_CLIENT_MESSAGE),
    EXPORT_SERVER_SUCCESS(MessageType.SUCCESS, Lang.Info.EXPORT_SERVER_TITLE, Lang.Info.EXPORT_SERVER_MESSAGE),
    CREATE_PRESET_SUCCESS(MessageType.SUCCESS, Lang.Info.CREATE_PRESET_TITLE, Lang.Info.CREATE_PRESET_MESSAGE);

    /* Fields */

    private final MessageType messageType;
    private final Translation header;
    private final Translation body;
    private final AtomicBoolean atomic;
    private boolean closed;
    private Path path;
    private ConfigHandler<ClientConfig> client;
    private ConfigHandler<ServerConfig> server;

    /* Constructor */

    ManageThreadMessage(MessageType messageType, Translation header, Translation body)
    {
        this.messageType = messageType;
        this.header = header;
        this.body = body;
        this.closed = true;
        this.atomic = new AtomicBoolean();
    }

    /* Methods */

    /**
     * Open the overlay associated with this message. It is safe to invoke this method on a thread that is separate from
     * the game's main thread.
     */
    public void open()
    {
        this.atomic.set(true);
    }

    /**
     * Open the overlay associated with this message. The overlay will include a button that will open the given path
     * when clicked.
     *
     * @param path A {@link Path} instance.
     */
    public void open(Path path)
    {
        this.path = path;
        this.open();
    }

    /**
     * Open the overlay associated with this message and define a config handler.
     *
     * @param handler A {@link ConfigHandler} that handles a {@link ConfigMeta} instance.
     */
    @SuppressWarnings("unchecked") // Loaded config uses the class type parameter T
    public <T extends ConfigMeta> void open(ConfigHandler<T> handler)
    {
        if (handler.getLoaded() instanceof ClientConfig)
            this.client = (ConfigHandler<ClientConfig>) handler;
        else if (handler.getLoaded() instanceof ServerConfig)
            this.server = (ConfigHandler<ServerConfig>) handler;

        this.open();
    }

    /**
     * Releases {@link ConfigMeta} and any {@link Path} from memory when a message overlay closes.
     */
    private void close()
    {
        this.closed = true;
        this.client = null;
        this.server = null;
        this.path = null;
    }

    /**
     * Performs importing or exporting instructions on the main thread when certain manage message overlays open from
     * the response of importing or exporting a file with the machine's operating system.
     */
    private void importOrExport()
    {
        if (this.equals(IMPORT_CLIENT_SUCCESS) && this.client != null)
        {
            ConfigHandler<ClientConfig> clientHandler = ConfigBuilder.getHandler();

            ConfigBuilder.getHandler().backup();
            clientHandler.setLoaded(this.client.getLoaded());
            clientHandler.save();

            AfterConfigSave.reloadAndRun();
        }

        if (this.equals(IMPORT_SERVER_SUCCESS) && this.server != null)
        {
            ServerConfig serverConfig = this.server.getLoaded();

            PacketUtil.sendToServer(new ServerboundCreateBackup(false));

            TweakPool.filter(Tweak::isMultiplayerLike).map(TweakMeta::wildcard).forEach(tweak -> {
                Object fieldValue = ConfigReflect.getFieldValue(tweak, ServerConfig.class, serverConfig);
                String className = tweak.getGenericType().getSimpleName();

                if (fieldValue != null)
                {
                    if (!tweak.applySafely(fieldValue, tweak::setReceived))
                        NostalgicTweaks.LOGGER.warn("[Server Import] %s did not match class type (%s)", tweak, className);
                }
                else
                    NostalgicTweaks.LOGGER.warn("[Server Import] %s is not a known server tweak", tweak);
            });

            TweakPool.filter(Tweak::isMultiplayerLike).forEach(Tweak::sendToServer);
        }

        if (this.equals(EXPORT_SERVER_SUCCESS) && this.path != null)
        {
            ConfigHandler<ServerConfig> serverHandler = ConfigBuilder.temp(ServerConfig.class, this.path);
            ServerConfig serverConfig = serverHandler.getDefault();

            TweakPool.filter(Tweak::isMultiplayerLike).forEach(tweak -> {
                Object value = tweak.fromDisk();

                if (NetUtil.isConnected())
                    value = tweak.fromServer();

                ConfigReflect.setManualField(ServerConfig.class, serverConfig, tweak, value);
            });

            serverHandler.setLoaded(serverConfig);
            serverHandler.export(this.path);

            this.path = this.path.getParent();
        }
    }

    /**
     * Instructions to perform during each game tick. This is <b color=red>not</b> safe to invoke off the game's main
     * thread.
     */
    public void tick()
    {
        if (this.atomic.getAndSet(false) && this.closed)
        {
            MessageOverlay message = MessageOverlay.create(this.messageType, this.header, this.body)
                .setResizePercentage(0.65D);

            if (this.equals(JAVA_ERROR))
                message.addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()));

            this.importOrExport();

            if (this.path != null)
                message.addButton(ButtonTemplate.openFolder(this.path));

            message.getBuilder().onClose(this::close);
            message.build().open();

            this.closed = false;
        }
    }
}
