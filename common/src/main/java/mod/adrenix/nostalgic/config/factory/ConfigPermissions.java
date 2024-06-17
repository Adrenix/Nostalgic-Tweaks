package mod.adrenix.nostalgic.config.factory;

/**
 * Some config handlers may want specific permissions defined during read/write operations. Those permissions can be
 * defined using this enumeration.
 *
 * <ul>
 *     <li>{@code READ_ONLY} - No write operations will be allowed to occur.</li>
 *     <li>{@code READ_WRITE} - Write operations will be allowed to occur.</li>
 * </ul>
 */
public enum ConfigPermissions
{
    READ_ONLY,
    READ_WRITE
}
