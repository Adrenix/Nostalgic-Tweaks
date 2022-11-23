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

/**
 * Changes made:
 * - Added inferred documentation.
 * - Removed unused methods and constructors.
 */

public interface ConfigData
{
    /**
     * Validate data loaded from the config file on disk.
     * @throws ValidationException When invalid data is serialized.
     */
    default void validatePostLoad() throws ValidationException { }

    /**
     * Specific exception class for invalid config data.
     */
    class ValidationException extends Exception
    {
        /**
         * Construct a new validation exception.
         * @param cause What caused the exception.
         */
        public ValidationException(Throwable cause) { super(cause); }
    }
}
