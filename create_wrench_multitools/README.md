# Create: Wrench Multitools

A NeoForge 1.21.1 mod that adds enchantable, durability-free multitool wrenches to Create mod.

## Features

- 5 tiers: Copper, Iron, Brass, Diamond, Netherite
- Behaves like a Create Wrench (rotation, dismantle, Create UI interactions) — inherits from `WrenchItem`
- Breaks **any** block at a tier-appropriate speed, always with correct drops
- **No durability** — no bar, no usage limit
- Enchantable, with tier-scaled enchantability
- Smithing-table upgrade chain with a custom upgrade template (netherite tier uses vanilla's netherite upgrade template)
- Data-generated recipes, models, and language files

## Recipe chain

| Tier      | Template                              | Base            | Addition            |
|-----------|---------------------------------------|-----------------|---------------------|
| Copper    | Wrench Upgrade Template               | `create:wrench` | `create:copper_sheet` |
| Iron      | Wrench Upgrade Template               | Copper Wrench   | Iron Ingot          |
| Brass     | Wrench Upgrade Template               | Iron Wrench     | `create:brass_ingot` |
| Diamond   | Wrench Upgrade Template               | Brass Wrench    | Diamond             |
| Netherite | **Vanilla** Netherite Upgrade Template | Diamond Wrench  | Netherite Ingot     |

The Wrench Upgrade Template itself is crafted in a crafting table: 8 diamonds + netherrack in the centre + `create:wrench` in the top-middle slot (netherite-template-style pattern). Tweak the recipe in `ModRecipeProvider` as you like.

---

## First-time setup (Windows 11)

### 1. Install prerequisites

- **JDK 21** (required for 1.21.1). Install [Microsoft OpenJDK 21](https://learn.microsoft.com/en-us/java/openjdk/download) or [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21). Make sure `JAVA_HOME` points at it.
- **IntelliJ IDEA Community** (free) — strongly recommended for mod dev. Download from [jetbrains.com/idea](https://www.jetbrains.com/idea/download/).
- **Git** (optional but recommended). [git-scm.com/download/win](https://git-scm.com/download/win).

Verify in PowerShell:

```powershell
java -version      # should print 21.x
echo $env:JAVA_HOME
```

### 2. Open the project

1. Unzip `create_wrench_multitools.zip` to a location without spaces or non-ASCII characters in the path (e.g. `C:\dev\create_wrench_multitools`).
2. In IntelliJ: **File → Open** → select the project folder → **Open as project**.
3. IntelliJ will detect `build.gradle` and start Gradle sync. **Let it finish** — it will download NeoForge, Create, and their deps. First sync takes a while (hundreds of MB).
4. When the "Trust project?" prompt appears, trust it.

### 3. Build the project

From the terminal (in the project root), or IntelliJ's Gradle panel:

```powershell
# Run data generators (creates recipes, models, lang JSONs under src/generated/resources)
.\gradlew.bat runClientData

# Build the JAR
.\gradlew.bat build
```

The output JAR will be in `build/libs/createwrenchmultitools-0.1.0.jar`.

### 4. Run in the dev client

```powershell
.\gradlew.bat runClient
```

This launches Minecraft 1.21.1 + NeoForge + Create + your mod. You should see the wrenches in the Creative "Tools & Utilities" tab. Recipes also work in a survival test.

### 5. Gradle wrapper note

The `gradlew.bat` / `gradlew` scripts **are not included** in the starter zip because they're downloaded per-machine. Before first build, generate them:

```powershell
gradle wrapper --gradle-version 8.10
```

If you don't have Gradle installed globally, the simplest route is to let IntelliJ do it on first Gradle sync — it will bootstrap Gradle automatically. Otherwise, [install Gradle](https://gradle.org/install/) first.

---

## Adding your textures

Replace the placeholder PNGs in:

```
src/main/resources/assets/createwrenchmultitools/textures/item/
    copper_wrench.png
    iron_wrench.png
    brass_wrench.png
    diamond_wrench.png
    netherite_wrench.png
    wrench_upgrade_template.png
```

All should be **16×16 PNG** with transparent backgrounds. The generated item model uses the `minecraft:item/handheld` parent, so they'll appear at a pickaxe-like angle in-hand.

---

## Project structure

```
create_wrench_multitools/
├── build.gradle
├── gradle.properties              # All versions live here — update Create/NeoForge here
├── settings.gradle
├── src/
│   ├── main/
│   │   ├── java/nz/gks/wrenchmultitools/
│   │   │   ├── CreateWrenchMultitools.java      # @Mod entry point
│   │   │   ├── item/
│   │   │   │   ├── WrenchTier.java              # Tier enum (speed, enchantability)
│   │   │   │   ├── MultitoolWrenchItem.java     # Extends Create's WrenchItem
│   │   │   │   └── ModItems.java                # DeferredRegister
│   │   │   └── datagen/
│   │   │       ├── DataGenerators.java          # Hooks GatherDataEvent
│   │   │       ├── ModRecipeProvider.java       # Smithing + template recipes
│   │   │       ├── ModItemModelProvider.java    # Handheld item models
│   │   │       └── ModLanguageProvider.java     # en_us translations
│   │   ├── resources/
│   │   │   ├── pack.mcmeta
│   │   │   └── assets/createwrenchmultitools/textures/item/    # <-- your PNGs go here
│   │   └── templates/
│   │       └── META-INF/neoforge.mods.toml      # Templated, processed at build time
│   └── generated/                                # Auto-generated by runClientData
└── README.md
```

---

## Troubleshooting

- **`WrenchItem cannot be resolved`** — Create isn't on the classpath. Check the Gradle sync completed without errors. Try `.\gradlew.bat --refresh-dependencies build`.
- **Crash on load about `create` mod version** — bump `create_version` in `gradle.properties` to match what you have installed. The current latest for 1.21.1 is documented in the [Create mod wiki](https://wiki.createmod.net/developers/depend-on-create/neoforge-1.21.1).
- **`expected item create:... not found`** during datagen — your Create dependency didn't load. Verify `build.gradle` repositories and that `:slim` classifier is present.
- **Missing textures in game** — run `runClientData` again and confirm PNGs exist at the path above. Texture filenames must match exactly (lowercase, underscores).
- **`WrenchItem` is `final` in a newer Create version** — swap `MultitoolWrenchItem extends WrenchItem` for a direct `Item` extension and forward rotation logic via Create's public `IWrenchable` API. Not an issue on Create 6.0.x.

## License

MIT — do what you want. Dependencies keep their own licenses.
