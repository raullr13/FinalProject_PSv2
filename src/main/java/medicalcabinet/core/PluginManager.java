package medicalcabinet.core;

import medicalcabinet.domain.plugincontracts.IExportPlugin;
import medicalcabinet.domain.plugincontracts.IStatisticsPlugin;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class PluginManager {

    public List<IExportPlugin> loadExportPlugins(String pluginsFolderPath) {
        List<IExportPlugin> plugins = new ArrayList<>();
        File pluginsDir = new File(pluginsFolderPath);

        if (pluginsDir.exists() && pluginsDir.isDirectory()) {
            File[] jarFiles = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));

            if (jarFiles != null) {
                try {
                    URL[] urls = new URL[jarFiles.length];
                    for (int i = 0; i < jarFiles.length; i++) {
                        urls[i] = jarFiles[i].toURI().toURL();
                    }

                    URLClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

                    ServiceLoader<IExportPlugin> loader = ServiceLoader.load(IExportPlugin.class, classLoader);

                    for (IExportPlugin plugin : loader) {
                        plugins.add(plugin);
                        System.out.println("Loaded plugin: " + plugin.getFormatName());
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load plugins: " + e.getMessage());
                }
            }
        }
        return plugins;
    }

    public List<IStatisticsPlugin> loadStatisticsPlugins(String pluginsDir) {
        List<IStatisticsPlugin> plugins = new ArrayList<>();
        java.io.File dir = new java.io.File(pluginsDir);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Plugins directory not found: " + pluginsDir);
            return plugins;
        }

        java.io.File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) return plugins;

        try {
            java.net.URL[] urls = new java.net.URL[files.length];
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }

            java.net.URLClassLoader child = new java.net.URLClassLoader(urls, this.getClass().getClassLoader());
            java.util.ServiceLoader<IStatisticsPlugin> loader = java.util.ServiceLoader.load(IStatisticsPlugin.class, child);

            for (IStatisticsPlugin plugin : loader) {
                plugins.add(plugin);
            }
        } catch (Exception e) {
            System.err.println("Failed to load statistics plugins: " + e.getMessage());
        }

        return plugins;
    }


}