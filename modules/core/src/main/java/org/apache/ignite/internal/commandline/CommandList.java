/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.commandline;

import org.apache.ignite.internal.commandline.cache.CacheCommands;
import org.apache.ignite.internal.commandline.diagnostic.DiagnosticCommand;

/**
 * High-level commands.
 */
public enum CommandList {
    /** */
    ACTIVATE("--activate", new ActivateCommand()),

    /** */
    DEACTIVATE("--deactivate", new DeactivateCommand()),

    /** */
    STATE("--state", new StateCommand()),

    /** */
    BASELINE("--baseline", new BaselineCommand()),

    /** */
    TX("--tx", new TxCommands()),

    /** */
    CACHE("--cache", new CacheCommands()),

    /** */
    WAL("--wal", new WalCommands()),

    /** */
    DIAGNOSTIC("--diagnostic", new DiagnosticCommand());

    /** Private values copy so there's no need in cloning it every time. */
    private static final CommandList[] VALUES = CommandList.values();

    /** */
    private final String text;

    /** Command implementation. */
    private final Command command;

    /**
     * @param text Text.
     * @param command Command implementation.
     */
    CommandList(String text, Command command) {
        this.text = text;
        this.command = command;
    }

    /**
     * @param text Command text.
     * @return Command for the text.
     */
    public static CommandList of(String text) {
        for (CommandList cmd : VALUES) {
            if (cmd.text().equalsIgnoreCase(text))
                return cmd;
        }

        return null;
    }

    /**
     * @return Command text.
     */
    public String text() {
        return text;
    }

    /**
     * @return Command implementation.
     */
    public Command command() {
        return command;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return text;
    }
}
