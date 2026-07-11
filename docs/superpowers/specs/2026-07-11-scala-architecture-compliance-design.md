# Scala Architecture Compliance Design

## Goal

Bring the Scala portion of Big Steppa into compliance with `.agents/AGENTS.md` while preserving the existing Minecraft 1.7.10 behavior and leaving every Java source file and mixin integration untouched.

## Scope

### Included

- Scala code under `src/main/scala/com/equilcraft/bigsteppa/`.
- Package layout for Alchemical Synthesis, Beacon Farmer, and Armor Stand.
- `structureLib` definitions and their controller references.
- A central GUI handler backed by a provider registry.
- Lower-camel-case names for all Scala constants and `final val` declarations.
- Removal of comments from ordinary Scala blocks, tiles, inventories, and client code.
- Import and registration updates required by the reorganization.

### Excluded

- Every existing `.java` file, including Java files located under `src/main/scala`.
- `src/main/java/com/equilcraft/bigsteppa/mixins/**`.
- Mixin JSON resources and other mixin integration files.
- New test files, because the project rules explicitly prohibit tests.
- Functional behavior changes unrelated to the requested architecture migration.

## Package Architecture

Shared infrastructure remains in its existing shared packages:

- `common/tile/MultiblockController.scala` remains the common structure controller trait.
- `common/tile/SpatialRegistered.scala` remains the common spatial registration trait.
- `common/block/BlockMultiblockController.scala` remains the common controller block base.
- `api/` remains the API and internal optimization boundary.

Structure-specific Scala code is grouped without underscores in package names:

- `common/structure/alchemicalsynthesis/`
- `common/blocks/alchemicalsynthesis/`
- `common/tile/alchemicalsynthesis/`
- `common/structure/beaconfarmer/`
- `common/blocks/beaconfarmer/`
- `common/tile/beaconfarmer/`
- `common/blocks/armorstand/`
- `common/tile/armorstand/`
- `common/inventory/armorstand/`
- `common/gui/armorstand/`
- `client/gui/armorstand/`
- `client/model/armorstand/`
- `client/render/armorstand/`

The existing general-purpose initialization, configuration, entity, and API packages remain shared when they do not belong to one structure.

## StructureLib Design

Each multiblock gets a dedicated structure definition object outside its tile entity:

- `AlchemicalSynthesisStructure` owns the shape, rune block adder, and `IStructureDefinition[TileAlchemicalSynthesisCore]`.
- `BeaconFarmerStructure` owns the shape, upgrade block adder, and `IStructureDefinition[TileBeaconFarmer]`.

The concrete controllers keep only structure configuration and lifecycle behavior. They reference the dedicated structure object through `structureDef`, `mainPiece`, and the configured offsets inherited from `MultiblockController`.

Structure callbacks may update controller state through package-scoped APIs where the structure definition needs to record runes or upgrades. No custom multiblock validation or construction logic is introduced; all structure operations continue to use GTNH `structureLib`.

## GUI Design

The project uses one registered Forge GUI handler:

1. `GuiProvider` defines the server-side and client-side element factories.
2. `GuiRegistry` stores providers by integer GUI ID and rejects duplicate IDs.
3. A single central `GuiHandler` looks up the provider and delegates `getServerGuiElement` and `getClientGuiElement`.
4. `CommonProxy` registers only the central handler with `NetworkRegistry`.
5. Armor Stand contributes its provider from the `armorstand` package. Client-only GUI classes remain under `client/gui/armorstand/` and are not imported by common-only code.

The existing Armor Stand GUI ID and behavior remain unchanged, while the old direct `ArmorStandGuiHandler` registration is removed.

## Scala Style Rules

- All Scala constants and `final val` declarations use lower camel case.
- Existing public behavior and Forge signatures are preserved.
- Immutable and functional code is preferred, with mutable state retained only for Minecraft lifecycle state, inventory state, structure counters, or measured performance needs.
- Existing `api/internal` implicits are reused for Java collection and fastutil interoperability.
- Comments remain only in `api/` and in the explicitly exempted API-like base classes. Ordinary blocks, tiles, inventories, registries, and client classes contain no comments.
- No package name introduced by this migration contains `_`.

## Migration Order

1. Move structure-specific Scala files and update package declarations and imports.
2. Extract Alchemical Synthesis and Beacon Farmer structure definitions into their dedicated structure packages.
3. Update tile entities and registrations to reference the extracted definitions.
4. Move Armor Stand inventory, GUI, model, and renderer code into structure-specific packages.
5. Add the common GUI provider/registry/router and migrate Armor Stand registration.
6. Rename constants and remove disallowed comments.
7. Run structural checks and the full Gradle build.

## Compatibility and Verification

Verification must include:

- `./gradlew build` succeeds.
- `git diff --name-only` contains no Java file, mixin source, or mixin resource.
- No `StructureDefinition`, `addShape`, or `addElement` implementation remains inside a `Tile*` source file.
- No new package path contains `_`.
- Common code has no imports of client-only GUI, model, or renderer classes.
- Block, tile entity, GUI, renderer, and model registration still points to the migrated classes.

The migration is complete only when these checks pass and the resulting diff contains no unrelated behavior changes.
