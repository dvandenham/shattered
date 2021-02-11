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
            - The PrebootClassLoader is a wrapper for the default ClassLoader that prevents Shattered from accessing the Preboot classes.
              It also has other functionalities used by other preboot steps.
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

## Registries
| Name                      | Key type         | Value type | Freezable | Unique Keys |
|---------------------------|------------------|------------|-----------|-------------|
| ResourceSingletonRegistry | ResourceLocation | Object     | Yes       | Yes         |

## Assets
| Type           | Registry location                    | Asset root directory            | Extension |
|----------------|--------------------------------------|---------------------------------|-----------|
| Language Files | /assets/\<namespace\>/language.json  | /assets/\<namespace\>/language/ | .lang     |
| Textures       | /textures/\<namespace\>/texture.json | /assets/\<namespace\>/textures/ | .json     |
| Fonts          | /assets/\<namespace\>/font.json      | /assets/\<namespace\>/fonts/    | .ttf      |
| Audio          | /assets/\<namespace\>/audio.json     | /assets/\<namespace\>/audio/    | .json     |
| Lua Scripts    | /assets/\<namespace\>/scripts.json   | /assets/\<namespace\>/scripts/  | .lua      |
| Binary Files   | /assets/\<namespace\>/binary.json    | /assets/\<namespace\>/          |           |

### Language files
Language files are regular text-files with unique key-value pairs:  
```shattered.screen.main_menu.button.exit=Exit Shattered```  
If a key is defined multiple times, the last occurence will be used.  
If the localizer tries fails to localize a key, it will first try to localize it using the fallback "en_us" language file.  
If localizing using the fallback language fails, the key itself will be registered as the localization for the key.

**Registry mapping**  
A registry mapping ```<namespace>:en_us``` will map to a language file located at ```/assets/<namespace>/language/en_us.lang```.

### Textures
Textues seem daunting at first but are quite easy once the metadata structure is understood.  
Every texture requires at least the following properties in the metadata file:  
```json
{
  "type": "default/stitched/mapped/animated",
  "variants": {
    "default": "<namespace>:<resource>"
  }
}
```
If no metadata file could be found, Shattered will try to load a texture at the same path but with the .png extension instead.  
If the variants section is missing from the metadata file, the "default" variant will be registered with the same resource name as the metadata file. See the "Variant mapping" section below for more information.

**Registry mapping**
A registry mapping ```<namespace>:logo``` will map to a texture metadata file located at ```/assets/<namespace>/textures/logo.json```.  
If the metadata file does not exist, the mapping will map to an image located at ```/assets/<namespace>/textures/logo.png```.

**Variant mapping**  
A variant mapping ```"default": "<namespace>:<resource>"```will map to an image file located at ```/assets/<namespace>/textures/<resource>.png```.  
A variant can have metadata file for that variant. The name should be the same name as the image file with the ```.json``` extension added to it. This means that ```logo.png``` can have a metadata file named ```logo.png.json```.  
These variant metadata files cannot have any variants themselves, they will be ignored if provided.  
If the variant metadata file has a ```type``` definition, that type will be used instead of the type definition from the "parent" metadata file. This makes it possible to use multiple texture types for different variants.

There are 4 types of textures:  
  - Default
  - Stitched
  - Mapped
  - Animated

**Default**  
The whole image file will be used as 1 single texture.  
Default textures use the following metadata format:  
```json
{
  "type": "default",
  "variants": {
    ...
  }
}
```

**Stitched**
Stitched textures are used to store multiple sprites into a single image file. These stitched textures are indexed, starting at index 0, from left-to-right, top-to-bottom.  
Stitched textures use the following metadata format:
```json
{
  "type": "stitched",
  "variants": {
    ...
  },
  "sprite_count": 3, //The amount of sprites in the image file.
  "usable_width": 500 //Optional, if stitched textures do not fit perfectly, they should be padded to a power of two, and the real boundary width should be provided here
}
```
In addition, one of the following property groups must be present:
```json
{
  "sprite_size": 230 //The width and height of a sprite
}
```
```json
{
  "sprite_width": 230, //The width of a sprite
  "sprite_height": 256 //The height of a sprite
}
```

**Mapped**
Mapped textures are used to store multiple images into a single image. These mapped textures are mapped using unique name-to-rectangle mappings inside the metadata file.  
Mapped textures use the following metadata format:  
```json
{
  "type": "mapped",
  "variants": {
    ...
  },
  mapping: {
    "mytex1": {"x": 10, "y": 20, "width": 100, "height": 50}, //X, Y, Width and Height are the amount of pixels in the image
    "mytex2": {"x": 10, "y": 20, "w": 100, "h": 50}, //Short form
    "mytex3": "10x10x100x50" //Even shorter form
  }
}
```

**Animations**
Animations are a series of still images inside a large image that get rendering based on the time.  
While this sounds confusing, it's really easy to get working if the following requirements are met:  
- The image width is used as the size for one animation frame
- The image height should be **exactly** the width * the amount of frames
  - Example: An animation with a width of 64px and 15 frames should be 64 * 15 = 960 pixels in height.  
Note: the width and height can be swapped, meaning that an animation image can be either horizontal or vertical.  
Animations use the following metadata format:
```json
{
  "type": "animated",
  "variants": {
    ...
  },
  "fps": 10, //10 Frames per Second, can be fractions: fps: 0.5 = 2 seconds per frame
  "frame_mapping": [ //Optional, manually specify the frames that get played, frames can occur multiple times
    0, 1, 2, 3, 4, 5, 5, 4, 3, 2, 1, 0 //This animation will play backwards once frame 6 (index 5) has been reached.
  ]
}
```
