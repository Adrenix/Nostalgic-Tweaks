package mod.adrenix.nostalgic.util.common;

import dev.architectury.platform.Platform;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.BackupConfig;
import net.minecraft.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PathUtil
{
    /* Path Functional Interfaces */

    /**
     * Functional interface for checking if a path is not a directory.
     * @param file A path.
     * @return Whether the path is a file.
     */
    public static boolean isNotDirectory(Path file) { return !Files.isDirectory(file); }

    /**
     * Functional interface for checking if a file is a backup file.
     * @param file A file.
     * @return Whether the file is a mod config backup file.
     */
    public static boolean isBackupFile(Path file)
    {
        return !Files.isDirectory(file) && file.getFileName().toString().matches(BackupConfig.FILE_REGEX);
    }

    /* Path Utilities */

    /**
     * On Windows, it is a backslash, on other operating systems, it is a forward slash.
     * @return A directory separator symbol.
     */
    public static String getDirectorySlash() { return Util.getPlatform() == Util.OS.WINDOWS ? "\\" : "/"; }

    /**
     * Lazily get a list of files within a directory.
     * This does not check embedded directories or walk the filesystem.
     *
     * @param path The path to get files from.
     * @return A set of paths within the directory.
     * @throws IOException When there is an issue reading files.
     */
    public static List<String> getFilenames(Path path, Predicate<? super Path> filter) throws IOException
    {
        try (Stream<Path> stream = Files.list(path))
        {
            return stream
                .filter(filter)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList())
            ;
        }
    }

    /**
     * Overload method for {@link PathUtil#getFilenames(Path, Predicate)}. The default filter prevents directories from
     * being included in the resulting list.
     *
     * @param path The path to get files from.
     * @return A set of paths (which will only be files) within the directory.
     * @throws IOException When there is an issue reading files.
     */
    @SuppressWarnings("unused") // May or may not be used by a future config system.
    public static List<String> getFilenames(Path path) throws IOException
    {
        return PathUtil.getFilenames(path, PathUtil::isNotDirectory);
    }

    /**
     * Retrieve the creation time of a file. If the file system does not store a date of creating a file, then this
     * method will return the last modified date. If the last modified date is not stored as well, then the epoch
     * of (01.01.1970) will be returned.
     *
     * @param path The path to get creation time from.
     * @return A creation timestamp measured in milliseconds since the epoch (01.01.1970).
     */
    public static long getCreationTime(Path path)
    {
        try
        {
            return Files.readAttributes(path, BasicFileAttributes.class).creationTime().toMillis();
        }
        catch (IOException exception)
        {
            return 0;
        }
    }

    /**
     * Lazily get a list of files (oldest to newest) within a directory.
     * This does not check embedded directories or walk the filesystem.
     * @param path The path to get files from.
     * @return A set of paths from oldest modified to newest modified.
     * @throws IOException When there is an issue reading files.
     */
    public static List<Path> getOldestFiles(Path path, Predicate<? super Path> filter) throws IOException
    {
        try (Stream<Path> stream = Files.list(path))
        {
            return stream
                .filter(filter)
                .sorted(Comparator.comparingLong(PathUtil::getCreationTime))
                .collect(Collectors.toCollection(LinkedList::new))
            ;
        }
    }

    /**
     * Overload method for {@link PathUtil#getOldestFiles(Path, Predicate)}. The default filter prevents directories
     * from being included in the resulting list.
     *
     * @param path The path to get the oldest files from.
     * @return A set of paths (which will only be files) within the directory from oldest modified to newest modified.
     * @throws IOException When there is an issue reading files.
     */
    @SuppressWarnings("unused") // May or may not be used by a future config system.
    public static List<Path> getOldestFiles(Path path) throws IOException
    {
        return PathUtil.getOldestFiles(path, PathUtil::isNotDirectory);
    }

    /**
     * Delete the given path from the file system. This task is cannot be undone.
     * @param file The file path to delete.
     */
    public static void delete(Path file)
    {
        try
        {
            Files.delete(file);
            NostalgicTweaks.LOGGER.info("Deleted: %s", file.getFileName().toString());
        }
        catch (NoSuchFileException exception)
        {
            NostalgicTweaks.LOGGER.error("File: %s - does not exist so it cannot be deleted", file);
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error(exception.getMessage());
        }
    }

    /* Path Getters */

    /**
     * Get (and create the path if necessary) a path instance.
     * @param path A path to get.
     * @return A path instance from the given string.
     */
    private static Path getPath(String path)
    {
        try
        {
            return Files.createDirectories(Platform.getConfigFolder().resolve(path));
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error(exception.getMessage());
        }

        return null;
    }

    /**
     * @return The mod's main config directory.
     */
    public static Path getConfigPath() { return getPath(NostalgicTweaks.MOD_ID); }

    /**
     * @return The mod's backup config directory.
     */
    public static Path getBackupPath() { return getPath(NostalgicTweaks.MOD_ID + "/backup"); }

    /**
     * @return The mod's preset config directory.
     */
    @SuppressWarnings("unused") // This will eventually be used for the mod's config preset system
    public static Path getPresetPath() { return getPath(NostalgicTweaks.MOD_ID + "/presets"); }
}
