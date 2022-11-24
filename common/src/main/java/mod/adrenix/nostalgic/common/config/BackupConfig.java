package mod.adrenix.nostalgic.common.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.common.config.auto.serializer.ConfigSerializer;
import mod.adrenix.nostalgic.util.common.PathUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This utility class provides helper methods for creating backups of configs.
 *
 * Anytime the config file is about to be reset, a backup will be created. A maximum of five files will automatically be
 * backed up. The user can increase this limit and the user can manually create a backup at any time.
 */

public abstract class BackupConfig
{
    /* Fields */

    /**
     * This field contains a unique backup filename formatting that will inform the backup utility cleaner which files
     * were created by the mod. If a user uses this type of filename formatting anywhere else in the backup directory,
     * then those files will also be marked for deletion.
     *
     * The %s will be replaced with a date formatted as month-day-year-time (ex: jan-01-2022-1134pm).
     */
    public static final String FILE_NAME = NostalgicTweaks.MOD_ID + "_backup_" + "%s.json";

    /**
     * This field defines the regex for finding the month within a backup file.
     * Example of a regex month result (jan-01-2022-).
     */
    private static final String REGEX_MONTH = "[a-z]{3}-\\d{2}-\\d{4}-";

    /**
     * This field defines the regex for finding the time within a backup file.
     * Example of a regex time result (1134am) or (1134am_1) where _# indicates a backup made at the same minute.
     */
    private static final String REGEX_TIME = "\\d{4}(?>am|pm)(?>_\\d+)?";

    /**
     * This field contains a unique regex string that will help the backup utility cleaner find files that were created
     * by the mod.
     */
    public static final String FILE_REGEX = NostalgicTweaks.MOD_ID + "_backup_" + REGEX_MONTH + REGEX_TIME + "\\.json";

    /* Methods */

    /**
     * Saves the current config file as a backup file.
     * @param serializer A config serializer to get path data from.
     * @param <T> The class type of the config.
     * @throws IOException When there is an issue reading/writing files.
     */
    public static <T extends ConfigData> void save(ConfigSerializer<T> serializer) throws IOException
    {
        int limit = 5;
        List<Path> oldest = PathUtil.getOldestFiles(PathUtil.getBackupPath(), PathUtil::isBackupFile);

        if (oldest.size() >= limit)
        {
            // Remove the oldest backup files until file limit is satisfied
            int remove = oldest.size() + 1 - limit;

            for (int i = 0; i < remove; i++)
                PathUtil.delete(oldest.get(i));
        }

        int underscore = 1;
        String filename = String.format(FILE_NAME, getTimestamp());
        List<String> backups = PathUtil.getFilenames(PathUtil.getBackupPath(), PathUtil::isBackupFile);

        for (String name : backups)
        {
            // Two backups were made within the same minute - append an underscore value
            if (name.equals(filename))
            {
                // Keep incrementing copy underscore value until it no longer matches a file
                boolean naming = true;

                while (naming)
                {
                    boolean isValueFound = false;

                    for (String duplicate : backups)
                    {
                        if (duplicate.equals(filename.replace(".json", String.format("_%s.json", underscore))))
                        {
                            isValueFound = true;
                            break;
                        }
                    }

                    if (!isValueFound)
                        naming = false;
                    else
                        underscore++;
                }

                filename = filename.replace(".json", String.format("_%s.json", underscore));
                break;
            }
        }

        Files.createDirectories(PathUtil.getBackupPath());
        NostalgicTweaks.LOGGER.debug("Successfully created backup path");

        String info = String.format("Created new config backup at %s>%s", PathUtil.getBackupPath().toString(), filename);

        Files.copy(serializer.getConfigPath(), PathUtil.getBackupPath().resolve(filename));
        NostalgicTweaks.LOGGER.info(info);
    }

    /**
     * Gets the timestamp that will be used in a backup file.
     * @return A timestamp using month-day-year-time (ex: jan-01-2022-1134pm).
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static String getTimestamp()
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy-hhmma")).toLowerCase();
    }
}
