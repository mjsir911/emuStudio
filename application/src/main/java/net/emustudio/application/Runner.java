/*
 * This file is part of emuStudio.
 *
 * Copyright (C) 2006-2020  Peter Jakubčo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.emustudio.application;

import net.emustudio.application.api.ApplicationApiImpl;
import net.emustudio.application.configuration.ApplicationConfig;
import net.emustudio.application.configuration.ComputerConfig;
import net.emustudio.application.configuration.ConfigFiles;
import net.emustudio.application.emulation.Automation;
import net.emustudio.application.emulation.AutomationException;
import net.emustudio.application.gui.GuiDialogsImpl;
import net.emustudio.application.gui.NoGuiDialogsImpl;
import net.emustudio.application.gui.debugtable.DebugTableImpl;
import net.emustudio.application.gui.debugtable.DebugTableModelImpl;
import net.emustudio.application.gui.dialogs.LoadingDialog;
import net.emustudio.application.gui.dialogs.OpenComputerDialog;
import net.emustudio.application.gui.dialogs.StudioFrame;
import net.emustudio.application.virtualcomputer.ContextPoolImpl;
import net.emustudio.application.virtualcomputer.InvalidPluginException;
import net.emustudio.application.virtualcomputer.VirtualComputer;
import net.emustudio.emulib.plugins.memory.Memory;
import net.emustudio.emulib.plugins.memory.MemoryContext;
import net.emustudio.emulib.runtime.ApplicationApi;
import net.emustudio.emulib.runtime.ContextNotFoundException;
import net.emustudio.emulib.runtime.ContextPool;
import net.emustudio.emulib.runtime.InvalidContextException;
import net.emustudio.emulib.runtime.interaction.Dialogs;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class Runner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);
    private static final long emustudioId = UUID.randomUUID().toString().hashCode();

    public static void main(String[] args) {
        try {
            CommandLine commandLine = CommandLine.parse(args);

            setupLookAndFeel();
            Optional<LoadingDialog> splash = showSplashScreen(commandLine.isNoGUI(), commandLine.getConfigName());

            Path configFile = Path.of("emuStudio.toml");
            if (Files.notExists(configFile)) {
                Files.createFile(configFile);
            }
            ApplicationConfig applicationConfig = ApplicationConfig.fromFile(
                configFile, commandLine.isNoGUI(), commandLine.isAuto()
            );

            Dialogs dialogs = commandLine.isNoGUI() ? new NoGuiDialogsImpl() : new GuiDialogsImpl();
            ContextPoolImpl contextPool = new ContextPoolImpl(emustudioId);
            DebugTableImpl debugTable = new DebugTableImpl();
            ApplicationApi applicationApi = new ApplicationApiImpl(debugTable, contextPool, dialogs);

            ConfigFiles configFiles = new ConfigFiles();

            ComputerConfig computerConfig = null;
            if (commandLine.getConfigName() == null && !commandLine.isNoGUI()) {
                OpenComputerDialog dialog = new OpenComputerDialog(configFiles, applicationConfig, dialogs);
                dialog.setVisible(true);
                if (dialog.getOK()) {
                    computerConfig = dialog.getSelectedComputerConfig();
                }
            } else {
                computerConfig = configFiles.loadConfiguration(commandLine.getConfigName()).orElseThrow();
            }

            if (computerConfig == null) {
                System.err.println("Virtual computer must be selected!");
                System.exit(1);
            }

            VirtualComputer computer = new VirtualComputer(computerConfig, applicationApi, applicationConfig);

            splash.ifPresent(Window::dispose);

            if (!commandLine.isAuto()) {
                showMainWindow(computer, applicationConfig, dialogs, debugTable, contextPool, commandLine.getInputFileName());
            } else {
                System.exit(runAutomation(computer, commandLine.getInputFileName(), applicationConfig, dialogs));
            }
        } catch (CmdLineException | IOException | NoSuchElementException | InvalidPluginException e) {
            LOGGER.error("Could not run emuStudio", e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static int runAutomation(VirtualComputer computer, String inputFileName, ApplicationConfig applicationConfig,
                                     Dialogs dialogs) {
        try {
            new Automation(computer, inputFileName, applicationConfig, dialogs).run();
            return 0;
        } catch (AutomationException e) {
            LOGGER.error("Unexpected error during automation.", e);
            dialogs.showError("Unexpected error during automation. Please see log file for details.");
            return 1;
        } finally {
            computer.close();
        }
    }

    private static void showMainWindow(VirtualComputer computer, ApplicationConfig applicationConfig, Dialogs dialogs,
                                       DebugTableImpl debugTable, ContextPool contextPool, String inputFileName) {
        computer.getCPU().ifPresent(cpu -> {
            int memorySize = computer.getMemory().map(Memory::getSize).orElse(0);
            debugTable.setModel(new DebugTableModelImpl(cpu, memorySize));
        });

        MemoryContext<?> memoryContext = null;
        try {
            memoryContext = contextPool.getMemoryContext(emustudioId, MemoryContext.class);
        } catch (ContextNotFoundException | InvalidContextException e) {
            LOGGER.warn("Could not find memory context", e);
        }

        if (inputFileName != null) {
            new StudioFrame(computer, applicationConfig, dialogs, debugTable, memoryContext, inputFileName).setVisible(true);
        } else {
            new StudioFrame(computer, applicationConfig, dialogs, debugTable, memoryContext).setVisible(true);
        }
    }

    private static Optional<LoadingDialog> showSplashScreen(boolean noGUI, String computerName) {
        LoadingDialog splash = null;
        if (!noGUI) {
            splash = new LoadingDialog();
            splash.setVisible(true);
        } else {
            LOGGER.info("Loading virtual computer: {}", computerName);
        }
        return Optional.ofNullable(splash);
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (javax.swing.UnsupportedLookAndFeelException | ClassNotFoundException
            | InstantiationException | IllegalAccessException e) {
            LOGGER.warn("Unable to set system look and feel", e);
        }
        UIManager.put("TabbedPane.selected", UIManager.get("Panel.background"));
        UIManager.put("TabbedPane.background", UIManager.get("Panel.background"));
        UIManager.put("TabbedPane.contentAreaColor", UIManager.get("Panel.background"));
        UIManager.put("TextPane.font", Constants.MONOSPACED_PLAIN_12);
        UIManager.put("TextArea.font", Constants.MONOSPACED_PLAIN_12);
        UIManager.put("List.font", Constants.MONOSPACED_PLAIN_12);
        UIManager.put("Button.background", UIManager.get("Panel.background"));
    }
}
