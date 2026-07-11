# Project Rules and Guidelines

## Agent Persona / Role
- You are a **Senior Developer** specializing in developing modifications for **Minecraft 1.7.10** using **Forge** and the **Scala** programming language. Your work is entirely focused on creating and optimizing complex multiblock structures.

## Project Overview
- This is a Minecraft 1.7.10 mod focused on **multiblock structures**.

## Multiblock Framework
- **structureLib:** You MUST use the `structureLib` from GTNH (which is already included in the project dependencies) for defining, creating, and handling all multiblock structures. Do not write custom multiblock logic from scratch.

## Language & Coding Style
- **Scala Only:** All code must be written in **Scala**.
- **Functional Style:** Strictly follow a functional programming style by default (immutability, pure functions, higher-order functions).
- **Optimization First:** Optimization is the absolute **top priority**. You are explicitly allowed and encouraged to violate functional programming principles (e.g., using `var`, mutable collections, while loops) if it is necessary for optimization or performance improvements.
- **Internal API Implicits:** Do not forget to use the Scala `implicit` conversions and helpers provided in `api/internal` when necessary. These are designed to optimize operations (like Java collection conversions) and keep the code clean. If strictly necessary for optimization or cleaner code, you should write your own custom implicits.
- **Naming Conventions:** All constants and `final val` declarations must start with a lowercase letter (camelCase). For example, use `playerArmorStart` instead of `PlayerArmorStart`.
- **Comments:** Comments MUST be written in **English only**, and they are STRICTLY RESTRICTED to the `api/` package. The only exceptions are API-like base classes outside of `api/` (such as `BlockMultiblockController`, `MultiblockController`, and `SpatialRegistered`), which already have detailed comments that must not be modified or removed. Standard tiles, blocks, items, etc., MUST NOT contain any comments whatsoever. Do not add section headers like `// === Section ===`.

## Project Structure & Architecture
The project structure is strictly modularized by multiblock structures. For each multiblock structure, you must create a dedicated subpackage named after the structure inside the respective component packages:
- **Structure Definitions:** The physical shape, layout, and block requirements of a multiblock structure (using `structureLib`) MUST be defined in the `common/structure/<structure_name>/` package. Do not hardcode the structure shape pattern directly inside the TileEntity class. Instead, create a dedicated object or trait (e.g., `object <StructureName>Structure`) in the `structure` package that provides the `IStructureDefinition`, and let the controller TileEntity reference it.
- **Blocks:** All blocks belonging to a specific structure must be placed in `common/blocks/<structure_name>/`.
- **Tile Entities:** All tile entities for a structure must be placed in a dedicated package, e.g., `common/tile/<structure_name>/` (or the respective base tile package).
- **Client-Side Code (Render, GUI, Models, etc.):** Absolutely everything related to the client side (e.g., rendering, GUIs, models) MUST be placed strictly within the `client/` package. Do not mix client-side code into `common/`. Apply the structure-based packaging here as well (e.g., `client/render/<structure_name>/`, `client/gui/<structure_name>/`).
- **General Principle:** This structure-based packaging principle must be applied to all other relevant parts of the project (e.g., items, GUIs, containers). Everything related to a specific structure's component type must be isolated in its own `<structure_name>` package.

## GUI Registration
- **Single GUI Handler Pool:** Minecraft 1.7.10 does not support registering multiple `IGuiHandler` instances per mod smoothly. All GUIs and their respective containers MUST be handled elegantly through a single, central `IGuiHandler` that acts as a router. Do not create and register a new `IGuiHandler` for every new machine or multiblock.
- **Separation of Concerns (Registry Pattern):** To keep the central handler clean and decoupled, use a **Registry Pattern**. Introduce a `GuiProvider` trait (with methods to get server and client elements) and a central `GuiRegistry` that maps integer IDs to these providers. Each multiblock structure's subpackage should define its own `GuiProvider` and register it with the central registry independently. The central `IGuiHandler` then simply looks up the ID in the registry and delegates the calls, keeping everything modular.

## Multiblock Base Classes
All multiblock controllers MUST use the project's base traits and classes to avoid code duplication:
- **`MultiblockController[T]` trait** (`common/tile/MultiblockController.scala`): Encapsulates all `structureLib` boilerplate (`construct`, `check`, `buildOrHints`, periodic structure validation). Concrete controllers only override configuration values (`structureDef`, `mainPiece`, offsets) and lifecycle hooks (`onPreStructureCheck`, `onStructureFormed`, `onStructureBroken`). Call `tickStructureCheck()` from `updateEntity()` for automatic periodic checks.
- **`SpatialRegistered[T]` trait** (`common/tile/SpatialRegistered.scala`): Optional trait for tiles that need proximity search via `BlocksChaosStructureRegistry`. Provides `spatialValidate()` and `spatialInvalidate()` helper methods — call them from `validate()` and `invalidate()` in your tile. Fully independent from `MultiblockController` — can be mixed into any `TileEntity` (multiblock or standalone).
- **`BlockMultiblockController[T]` class** (`common/block/BlockMultiblockController.scala`): Abstract base block for controller blocks. Handles `onBlockActivated` (check structure + chat message) and `onNeighborBlockChange` (schedule recheck). Concrete blocks only override `createNewTileEntity`, `formedTranslationKey`, and `incompleteTranslationKey`.

## Proximity Search & Spatial Queries
- **Fast Block Search:** For tasks that require looking for specific blocks nearby and reacting to them, you MUST use the internal API registrar class (`api.internal.BlocksChaosStructureRegistry` from `api/internal`) for fast proximity searches. Use the `SpatialRegistered[T]` trait (see above) to integrate with the registry automatically.
- **Reference Example:** See the implementation of `ArmorStand` (`TileArmorStand` and related classes) for a detailed example of how to use the `SpatialRegistered` trait.

## Testing
- **No Tests:** Do not write any tests for this project.
