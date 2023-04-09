# Nostalgic Tweaks - 1.19.4 (v2) Development Branch
### Forge & Fabric
This is the 1.19.4 (v2) development branch for N.T.

### Current In-Development Version
v2.0.0

### Current Released Version
v2.0.0-Beta-8.3

### Related Development Branches
- [Home](https://github.com/Adrenix/Nostalgic-Tweaks)
- [1.19.3](https://github.com/Adrenix/Nostalgic-Tweaks/tree/1.19.3)
- [1.19.2 Multiplayer](https://github.com/Adrenix/Nostalgic-Tweaks/tree/1.19.2-multiplayer)

## Nostalgic Tweaks API For Developers (Fabric)
> **ATTENTION**
>
> This part of the guide is written for Fabric. For Forge, see the section below.

If followed, the directions below will make it so that your mod's dependencies won't include Nostalgic Tweaks at all,
and your mod will load fine with or without Nostalgic Tweaks installed.

To compile against the Nostalgic Tweaks API, include the following in you `build.gradle`:
```
repositories {
    maven { url "https://api.modrinth.com/maven" }
}
```

Next, add this to your `dependencies` block:
```
modCompileOnly("maven.modrinth:nostalgic-tweaks:<version>") {
    transitive = false
}
```

Where `<version>` is replaced by an appropriate version number found on [Modrinth](https://modrinth.com/mod/nostalgic-tweaks/versions).
Ensure you are using a **version number** and not a *version display name*. A version number is formatted as
`mc-version+mod-loader+mod-version`. An example version number would be `1.19.4+fabric+2.0.0`.

Once you are compiling against the Nostalgic Tweaks API, you can use the `"nostalgic_tweaks"` [entrypoint](https://fabricmc.net/wiki/documentation:entrypoint)
to register your event handlers. Below is an example on how to create such a handler.

In your `fabric.mod.json`:
```
"entrypoints": {
  "nostalgic_tweaks": [
    "your.package.name.NostalgicTweaksEventHandler"
  ]
}
```

Here is an example of what the `NostalgicTweaksEventHandler` class may look like:

```java
public class NostalgicTweaksEventHandler implements NostalgicFabricApi
{
    @Override
    public void registerEvents()
    {
        NostalgicHudEvent.RenderHeart.EVENT.register(event ->
        {
            PoseStack poseStack = event.getPoseStack();
            int x = event.getX();
            int y = event.getY();
            
            if (!event.isHungerDisabled())
                doSomethingToHeartBasedOnHunger();
            
            // Make changes to renderer based on Nostalgic Tweaks HUD setup
            renderCustomHeart(poseStack, x, y);
        });
    }
}
```

See the `common` root folder `mod.adrenix.nostalgic.api.event` package for interfaces that are used by mod loader event
implementations. Some mod loader implementations may have extra methods included with their events. Those additions can
be seen in the mod loader's respective package. In the `fabric` root folder, it would be under the same path structure
`mod.adrenix.nostalgic.api`. There is also a `test` package within the `api` package if you want to see a more in-depth
example of how to set up each event.

## Nostalgic Tweaks API For Developers (Forge)
> **ATTENTION**
>
> This part of the guide is written for Forge. For Fabric, see the section above.

If followed, the directions below will make it so that your mod's dependencies won't include Nostalgic Tweaks at all,
and your mod will load fine with or without Nostalgic Tweaks installed.

To compile against the Nostalgic Tweaks API, include the following in your `build.gradle`:

```
repositories {
    maven { url "https://api.modrinth.com/maven" }
}
```

Next, add this to your `dependencies` block:

```
compileOnly "maven.modrinth:nostalgic-tweaks:<version>"
```

Where `<version>` is replaced by an appropriate version number found on [Modrinth](https://modrinth.com/mod/nostalgic-tweaks/versions).
Ensure you are using a **version number** and not a *version display name*. A version number is formatted as
`mc-version+mod-loader+mod-version`. An example version number would be `1.19.4+forge+2.0.0`.

Once you are compiling against the Nostalgic Tweaks API, you can create an event handler and only register it when a
user has `Nostalgic Tweaks` installed. Below is an example on how to create such an implementation.

In a class where an `@Mod` annotation is present register your event handler class to Forge's event bus:
```java
@SubscribeEvent
public static void init(FMLClientSetupEvent event)
{
    if (ModList.get().isLoaded("nostalgic_tweaks"))
        MinecraftForge.EVENT_BUS.register(new NostalgicTweaksEventHandler());
}
```

Here is an example of what the `NostalgicTweaksEventHandler` class may look like:

```java
public class NostalgicTweaksEventHandler()
{
    @SubscribeEvent
    public void onRenderHeartEvent(NostalgicHudEvent.RenderHeart event)
    {
        PoseStack poseStack = event.getPoseStack();
        int x = event.getX();
        int y = event.getY();
        
        if (!event.isHungerDisabled())
            doSomethingToHeartBasedOnHungerBar();
        
        // Make changes to renderer based on Nostalgic Tweaks HUD setup
        renderCustomHeart(poseStack, x, y);
    }
}
```

See the `common` root folder `mod.adrenix.nostalgic.api.event` package for interfaces that are used by mod loader event
implementations. Some mod loader implementations may have extra methods included with their events. Those additions can
be seen in the mod loader's respective package. In the `forge` root folder, it would be under the same path structure
`mod.adrenix.nostalgic.api`. There is also a `test` package within the `api` package if you want to see a more in-depth
example of how to set up each event.