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

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.BackupConfig;
import mod.adrenix.nostalgic.common.config.auto.annotation.Config;
import mod.adrenix.nostalgic.common.config.auto.event.ConfigSerializeEvent;
import mod.adrenix.nostalgic.common.config.auto.serializer.ConfigSerializer;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Changes made to class:
 * - Removed unused fields/methods
 * - Cleaned up class to match codebase styling
 * - Added inferred documentation
 * - Added reset backup protection
 *
 * @param <T> The class type that defines config structure.
 */

@ApiStatus.Internal
public class ConfigManager<T extends ConfigData> implements ConfigHolder<T>
{
    /* Fields */

    private final Config definition;
    private final Class<T> configClass;
    private final ConfigSerializer<T> serializer;
    
    private final List<ConfigSerializeEvent.Save<T>> saveEvent = new ArrayList<>();
    private final List<ConfigSerializeEvent.Load<T>> loadEvent = new ArrayList<>();
    
    private T config;

    /* Constructor */

    /**
     * Create a new config manager instance.
     * @param definition A config POJO that defines the config structure.
     * @param configClass The class type that uses the definition annotation.
     * @param serializer The config file serializer.
     */
    public ConfigManager(Config definition, Class<T> configClass, ConfigSerializer<T> serializer)
    {
        this.definition = definition;
        this.configClass = configClass;
        this.serializer = serializer;
        
        if (this.load())
            this.save();
    }

    /* Methods */

    /**
     * Get the config annotation metadata associated with the config class.
     * @return Config annotation metadata.
     */
    public Config getDefinition() { return this.definition; }

    /**
     * @return Get the current config class instance held by this manager.
     */
    @Override
    public T getConfig() { return this.config; }

    /**
     * Change the current config instance to a different config instance.
     * @param config A config class instance.
     */
    @Override
    public void setConfig(T config) { this.config = config; }

    /**
     * Register a new load listener for when a config load event is fired.
     * @param load Instructions to perform when a new config file is loaded.
     */
    @Override
    public void registerLoadListener(ConfigSerializeEvent.Load<T> load) { this.loadEvent.add(load); }

    /**
     * Register a new save listener for when a config save event is fired.
     * @param save Instructions to perform when a new config file is saved.
     */
    @Override
    public void registerSaveListener(ConfigSerializeEvent.Save<T> save) { this.saveEvent.add(save); }

    /**
     * Sends a save event to the config handler and tries to serialize the current config.
     * Serialization exceptions are caught and an error printed to the console.
     */
    @Override
    public void save()
    {
        for (ConfigSerializeEvent.Save<T> save : this.saveEvent)
        {
            InteractionResult result = save.onSave(this, this.config);

            if (result == InteractionResult.FAIL)
                return;
            else if (result != InteractionResult.PASS)
                break;
        }

        try
        {
            this.serializer.serialize(this.config);
        }
        catch (ConfigSerializer.SerializationException exception)
        {
            NostalgicTweaks.LOGGER.error("Failed to save config '{}'", this.configClass, exception);
        }
    }

    /**
     * Deserializes the saved config file and sends a load event to the config handler.
     *
     * Any deserialization errors are caught, when caught, the config file is reset and an error is printed to the
     * logging console.
     *
     * @return Returns true if the deserialized config passed validation and successfully loaded. Otherwise, resets to
     * config file to the default config and returns false.
     */
    @Override
    public boolean load()
    {
        try
        {
            T deserialized = this.serializer.deserialize();
            
            for (ConfigSerializeEvent.Load<T> load : this.loadEvent)
            {
                InteractionResult result = load.onLoad(this, deserialized);

                if (result == InteractionResult.FAIL)
                {
                    this.config = serializer.createDefault();
                    this.config.validatePostLoad();

                    return false;
                }
                else if (result != InteractionResult.PASS)
                    break;
            }
            
            this.config = deserialized;
            this.config.validatePostLoad();

            return true;
        }
        catch (ConfigSerializer.SerializationException | ConfigData.ValidationException exception)
        {
            NostalgicTweaks.LOGGER.error("Failed to load config due to invalid data", configClass, exception);

            this.resetToDefault();
            this.save();

            NostalgicTweaks.LOGGER.warn("Config file has been reset - see backup file for old data");

            return false;
        }
    }

    /**
     * Resets the current config file to its default values.
     * A backup of the current data that is about to reset will be created.
     */
    @Override
    public void resetToDefault()
    {
        try
        {
            BackupConfig.save(this.serializer);
        }
        catch (IOException exception)
        {
            throw new RuntimeException("could not create a backup config file", exception);
        }

        this.config = serializer.createDefault();

        try
        {
            this.config.validatePostLoad();
        }
        catch (ConfigData.ValidationException exception)
        {
            throw new RuntimeException("could not create a default config file", exception);
        }
    }
}
