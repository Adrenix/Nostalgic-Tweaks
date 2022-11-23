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

import mod.adrenix.nostalgic.common.config.auto.ConfigData;
import mod.adrenix.nostalgic.common.config.auto.annotation.Config;

import java.nio.file.Path;

/**
 * Changes made:
 * - Added get config path method
 * - Added inferred documentation
 * - Cleaned up class to match codebase styling
 *
 * @param <T> The class type that defines config structure.
 */

public interface ConfigSerializer<T extends ConfigData>
{
    /**
     * Serialize config data so that it can be stored to disk.
     * @param config A config class instance.
     * @throws SerializationException When there is a problem serializing config data.
     */
    void serialize(T config) throws SerializationException;

    /**
     * Deserialize config data that is read from disk.
     * @return A config class instance with data stored on disk.
     * @throws SerializationException When there is a problem deserializing config data.
     */
    T deserialize() throws SerializationException;

    /**
     * Instructions for creating a default config instance.
     * @return A config instance with default config data.
     */
    T createDefault();

    /**
     * Defines where the config file will be kept on disk.
     * @return A path that should be somewhere within a mod loader's config folder.
     */
    Path getConfigPath();

    /**
     * A factory that holds the construction class that will be used to serialize/deserialize a config.
     * @param <T> The class type that defines config structure.
     */
    @FunctionalInterface
    interface Factory<T extends ConfigData>
    {
        /**
         * Define the config builder that will be used to serialize/deserialize the config.
         * @param definition The config class definition.
         * @param configClass A config class.
         * @return A class instance using the config serializer interface.
         */
        ConfigSerializer<T> create(Config definition, Class<T> configClass);
    }

    /**
     * Helper class that specifies exceptions for when there is an issue serializing or deserializing a config.
     * This will only be thrown during serializing/deserialization and is separate from IO exceptions.
     */
    class SerializationException extends Exception
    {
        /**
         * Create a new serialization exception.
         * @param cause A cause for the serialization error.
         */
        public SerializationException(Throwable cause) { super(cause); }
    }
}

