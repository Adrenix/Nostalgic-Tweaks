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

package mod.adrenix.nostalgic.common.config.auto;

import mod.adrenix.nostalgic.common.config.auto.annotation.Config;
import mod.adrenix.nostalgic.common.config.auto.serializer.ConfigSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Changes made:
 * - Cleanup class to match codebase styling
 * - Removed object non-null checks
 * - Removed constructor since this class is no longer the entry point for a mod
 * - Removed unused fields/methods/classes since the cloth-config GUI is not used by Nostalgic Tweaks
 * - Made the class abstract
 * - Added inferred documentation
 */

public abstract class AutoConfig
{
    /**
     * Keeps track of all configs that are registered.
     * Nostalgic Tweaks uses sided configs but only one will be used during runtime.
     */
    private static final Map<Class<? extends ConfigData>, ConfigHolder<?>> HOLDERS = new HashMap<>();

    /**
     * Register a new config class to the auto config's registry.
     * @param configClass The config structure class.
     * @param factory The serializer factory to use.
     * @param <T> The config structure class type.
     * @return A config manager instance.
     * @throws RuntimeException When the config is already registered or the config does not have a @Config annotation.
     */
    public static <T extends ConfigData> ConfigHolder<T> register(Class<T> configClass, ConfigSerializer.Factory<T> factory)
    {
        if (HOLDERS.containsKey(configClass))
            throw new RuntimeException(String.format("config '%s' already registered", configClass));
        
        Config definition = configClass.getAnnotation(Config.class);
        
        if (definition == null)
            throw new RuntimeException(String.format("no @Config annotation is attached to %s", configClass));
        
        ConfigSerializer<T> serializer = factory.create(definition, configClass);
        ConfigManager<T> manager = new ConfigManager<>(definition, configClass, serializer);

        HOLDERS.put(configClass, manager);
        
        return manager;
    }

    /**
     * Get the config holder that is assigned to a config class.
     * @param configClass The config structure class.
     * @param <T> The config structure class type.
     * @return A config holder that is associated with a config class.
     * @throws RuntimeException When the config class has not been registered.
     */
    @SuppressWarnings("unchecked") // The config class type is guaranteed
    public static <T extends ConfigData> ConfigHolder<T> getConfigHolder(Class<T> configClass)
    {
        if (HOLDERS.containsKey(configClass))
            return (ConfigHolder<T>) HOLDERS.get(configClass);
        else
            throw new RuntimeException(String.format("config '%s' has not been registered", configClass));
    }
}
