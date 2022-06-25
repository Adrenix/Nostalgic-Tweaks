package mod.adrenix.nostalgic.common.config.reflect;

/**
 * There are numerous states a tweak can be in. Some tweaks will start in different states than others.
 *
 * Each tweak cache will have tweaks in their own unique state. This is done so the server can properly report
 * tweak states to clients.
 *
 * <ol>
 * <b>LOADED</b> - The tweak ran expected code. <br>
 * <b>WAIT</b> - The tweak will run code in a world. <br>
 * <b>WARN</b> - Inform the user that there is something important to know about a tweak. <br>
 * <b>FAIL</b> - The tweak never ran expected code or the tweak has not been loaded by code that runs it. <br>
 * </ol>
 */

public enum StatusType
{
    LOADED,
    WAIT,
    WARN,
    FAIL
}
