# Shattered

Shattered is a platformer style game engine written in Java with almost all logic handled through lua scripts that can be customized by mods and/or
behaviour packs.

## Boot process

1. (**Unimplemented**) Launcher Bootstrap
    - Also known as the launcher-launcher, the Launcher bootstrap has only one job: making sure the newest launcher is downloaded and started.
2. (**Unimplemented**) Launcher
    - The launcher goes through the following stages:
        1. Reading metadata from the Shattered update servers
            - Dependency information
            - Shattered version information
            - If an update is available: a changelog
        2. Deleting any downloaded files whose checksum don't match the one from the Shattered servers causing them to download during the next
           stages.
        3. Download the newest version of Shattered when there's an update available.
        4. Download the newest version of dependencies that have an update available.
        5. Showing a changelog (if the user didn't disable that).
        6. Starting the Shattered boot process.
    - The launcher is the first graphical interface the user gets to see when starting the Shattered boot-chain
3. Bootstrap
    - Every release of Shattered gets automatically obfuscated and optimized before being uploaded to the update servers causing normal boot methods
      to fail. To counter this, a dummy bootstrap class was added to Shattered that bootstraps the real boot process.
4. Preboot
    - Preboot is comparable to booting the kernel of an OS. Preboot code runs on a virtual lower level than the next boot step. Preboot is responsible
      for preparing the runtime environment for Shattered.
        1. Instantiating the PrebootClassLoader
            - The PrebootClassLoader is a wrapper for the default ClassLoader that prevents Shattered from accessing the Preboot classes. It also has
              other functionalities used by other preboot steps.
        2. Loading the bytecode for every class used by Shattered and storing it in memory.
        3. Transforming classes based on predefined conditions using bytecode manipulation tools.
        4. Force-loading all transformed classes and caching them in memory using the PrebootClassLoader.
        5. Populating lists of classes keyed by annotations.
        6. Retrieving the list of classes annotated by the @BootManager annotation.
        7. Bootstrapping the first class in that list into the sandbox created by PrebootClassLoader.
5. Secondary boot
    - The "real" Shattered boot stage. Very complicated but simple to understand once simplified into these steps
      (text between parentheses is a system message event):
        1. Creating the system EventBus.
        2. Automatically registering all classes annotated with the @EventBusSubscriber annotation into their respective EventBus.
        3. Posting the CreateRegistryEvent, causing all registries to be created and registered into a master registry.
        4. Initializing GLFW (init_glfw) and the rendering system (init_gfw).
        5. Loading initial assets for the loading screen.
        6. Booting up the rendering system and starting the loading screen thread (start_gfx).
        7. Loading, parsing and validating the json-based registries for every asset type (init_assets).
        8. Loading all assets defined into these registries into real registries, one asset type at a time.
        9. Loading the configuration database.
        10. Instantiating the main menu screen and registering it into the GuiManager.
        11. Initializing the keyboard/mouse input handler.
        12. Starting the core runtime.
        13. Stopping the loading screen thread.
        14. Playing the boot animation and boot sound.
        15. Showing the main menu.