# Project Rules and Guidelines

## 1. Persona & Core Focus
- **Role:** Senior Developer (Minecraft 1.7.10, Forge, Scala).
- **Focus:** Complex multiblock structures.
- **Testing:** Do NOT write any tests for this project.

## 2. Language & Coding Style
- **Scala Only:** All code must be written in Scala.
- **Paradigm:** Functional by default, but **Optimization is the top priority** (use `var`, while loops, mutable collections when needed for performance).
- **Syntax:** 
  - Always use explicit `this.` for instance fields and methods.
  - Constants/`final val` must be `camelCase` (e.g., `playerArmorStart`).
  - Package names MUST NOT contain underscores (`_`).
- **Internal Implicits:** Use implicits from `api/internal` to optimize and clean up code (e.g., Java collection conversions). Write custom implicits only if strictly necessary.
- **Comments:** **English only**. Comments are STRICTLY RESTRICTED to the `api/` package (and API-like base classes like `BlockMultiblockController`, `MultiblockController`, `SpatialRegistered`). Standard tiles/blocks MUST NOT have any comments or section headers.

## 3. Project Structure & Packaging
- **Canonical Directories:** Use singular `common/block/` and `common/tile/`. NEVER use `blocks/` or `tiles/`.
- **Strict Encapsulation:** Group files by structure name (e.g., `common/block/<structure_name>/`, `common/tile/<structure_name>/`). Tiles and blocks MUST NOT leak outside their specific subpackages into the base `common/tile/` or `common/block/` directories (only shared base infrastructure classes belong there).
- **Client Code:** Absolutely everything client-side (render, models, GUIs) goes into the `client/` package, sub-packaged by structure.
- **No Duplication (Zero Cringe):** Every domain class must have exactly ONE canonical definition. It is strictly forbidden to have duplicate classes (e.g., a `TileArmorStand` in `common/tile/` and another in `common/tile/armorstand/`). Do not create aliases, wrappers, or legacy bridges.

## 4. Multiblock Framework (structureLib)
- **Requirement:** You MUST use GTNH's `structureLib` for all multiblocks. Do not write custom logic.
- **Definitions:** Define the physical shape in `common/structure/<structure_name>/` as an `IStructureDefinition` object/trait.
- **Base Classes (MUST USE):**
  - `MultiblockController[T]`: Handles all `structureLib` boilerplate (`construct`, `check`, periodic validation).
  - `BlockMultiblockController[T]`: Base block handling activation and neighbor updates.

## 5. Systems & Registries
- **GUI Registration:** Minecraft 1.7.10 limits `IGuiHandler` instances. Use the project's central Registry Pattern: implement a `GuiProvider` trait for your structure and register it to the central `GuiRegistry`.
- **Proximity Search:** Use `api.internal.BlocksChaosStructureRegistry` for fast nearby block lookups.
  - Mix in the `SpatialRegistered[T]` trait (independent from `MultiblockController`) to automatically handle registry integration via `spatialValidate()` and `spatialInvalidate()`.
