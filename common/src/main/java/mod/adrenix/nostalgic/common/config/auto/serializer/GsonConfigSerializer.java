/*
 * This file is from the Cloth Config mod created by shedaniel and has been
 * modified to work with the Nostalgic Tweaks mod created by Adrenix.
 *
 * Copyright (C) 2020 - 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package mod.adrenix.nostalgic.common.config.auto.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.common.config.auto.annotation.Config;
import mod.adrenix.nostalgic.util.common.PathUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Changes made:
 * - Removed unused fields/methods
 * - Cleaned up class to match codebase styling
 * - Changed config path where config saves to
 * - Removed auto config utility and added unsafe constructor method here
 * - Added inferred documentation
 *
 * @param <T> The class type that defines config structure.
 */

@SuppressWarnings("FieldMayBeFinal")
public class GsonConfigSerializer<T extends ConfigData> implements ConfigSerializer<T>
{
    /* Fields */

    private Config definition;
    private Class<T> configClass;
    private Gson gson;

    /* Constructors */

    /**
     * Create a Gson config serializer with a custom Gson builder instance.
     * @param definition A config annotation definition.
     * @param configClass A config class instance.
     * @param gson A Gson builder instance.
     */
    public GsonConfigSerializer(Config definition, Class<T> configClass, Gson gson)
    {
        this.definition = definition;
        this.configClass = configClass;
        this.gson = gson;
    }

    /**
     * Create a Gson config serializer.
     * @param definition A config annotation definition.
     * @param configClass A config class instance.
     */
    public GsonConfigSerializer(Config definition, Class<T> configClass)
    {
        this(definition, configClass, new GsonBuilder().setPrettyPrinting().create());
    }

    /* Methods */

    /**
     * The config file will be kept in the default config folder used by mod loaders.
     * @return A path within a mod loader config folder with the definition name appended as a .json.
     */
    @Override
    public Path getConfigPath() { return PathUtil.getConfigPath().resolve(definition.name() + ".json"); }

    /**
     * Writes the current config data to disk.
     * @param config A config instance.
     * @throws SerializationException When there is an IO exception.
     */
    @Override
    public void serialize(T config) throws SerializationException
    {
        Path configPath = getConfigPath();

        try
        {
            Files.createDirectories(configPath.getParent());

            BufferedWriter writer = Files.newBufferedWriter(configPath);

            gson.toJson(config, writer);
            writer.close();
        }
        catch (IOException exception)
        {
            throw new SerializationException(exception);
        }
    }

    /**
     * Reads a config file from disk.
     * @return A new config instance from data that is stored on disk.
     * @throws SerializationException When there is an IO exception or JSON parse exception.
     */
    @Override
    public T deserialize() throws SerializationException
    {
        Path configPath = getConfigPath();

        if (Files.exists(configPath))
        {
            try
            {
                BufferedReader reader = Files.newBufferedReader(configPath);
                T ret = gson.fromJson(reader, configClass);

                reader.close();

                return ret;
            }
            catch (IOException | JsonParseException exception)
            {
                throw new SerializationException(exception);
            }
        }
        else
            return createDefault();
    }

    /**
     * Creates a new default config class instance.
     * @return A new class instance with its defined default values.
     */
    @Override
    public T createDefault() { return constructUnsafely(configClass); }

    /**
     * Unsafely creates a new class instance.
     * @param cls A class to construct.
     * @param <V> The class type.
     * @return A new class instance.
     */
    private static <V> V constructUnsafely(Class<V> cls)
    {
        try
        {
            Constructor<V> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        }
        catch (ReflectiveOperationException exception)
        {
            throw new RuntimeException(exception);
        }
    }
}
