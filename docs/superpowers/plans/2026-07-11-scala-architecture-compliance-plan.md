# Scala Architecture Compliance Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reorganize the Scala portion of Big Steppa to comply with .agents/AGENTS.md, preserving behavior and leaving all Java and mixin integration files unchanged.

**Architecture:** Keep shared Forge and structure-controller infrastructure in common packages. Move structure-specific Scala code into alchemicalsynthesis, beaconfarmer, and armorstand packages, extract both structureLib definitions into dedicated structure objects, and route all GUIs through one registry-backed handler with client-only GUI implementations registered from ClientProxy.

**Tech Stack:** Minecraft 1.7.10, Forge 10.13.4, GTNH structureLib, Scala, fastutil, Gradle Kotlin DSL.

## Global Constraints

- Existing .java files, including Java files under src/main/scala, are excluded.
- src/main/java/com/equilcraft/bigsteppa/mixins/**, mixin JSON resources, and other mixin integration files must not change.
- All structure definitions use GTNH structureLib; no custom multiblock logic.
- Structure definitions live in common/structure/<structure_name>/.
- Structure blocks and tiles live in matching structure packages.
- All client-side code remains in client/.
- One central IGuiHandler delegates through per-structure GUI providers.
- All Scala constants and final val declarations start with lowercase camel case.
- Comments are restricted to api/ and the explicitly exempted API-like base classes.
- Do not add tests; AGENTS.md prohibits tests.

---

### Task 1: Reorganize structure-specific Scala packages

**Files:**

- Move common/block/BlockArmorStand.scala to common/blocks/armorstand/BlockArmorStand.scala.
- Move common/block/BlockBeaconFarmer.scala to common/blocks/beaconfarmer/BlockBeaconFarmer.scala.
- Move common/block/build/BlockMaster.scala and BlockStructure.scala to common/blocks/beaconfarmer/.
- Move common/block/update/BlockDamageUpdate.scala, BlockLootingUpdate.scala, and Update.scala to common/blocks/beaconfarmer/.
- Move common/block/multiblock/BlockAlchemicalSynthesisAspectInput.scala, BlockAlchemicalSynthesisCore.scala, and BlockAlchemicalSynthesisRune.scala to common/blocks/alchemicalsynthesis/.
- Move TileArmorStand.scala to common/tile/armorstand/, TileBeaconFarmer.scala to common/tile/beaconfarmer/, and both Alchemical Synthesis tiles to common/tile/alchemicalsynthesis/.
- Move ContainerArmorStand.scala and SlotArmorStand.scala to common/inventory/armorstand/.
- Move GuiArmorStand.scala, ModelArmorStand.scala, and all three Armor Stand renderers into matching client subpackages.
- Move common/alchemy/AlchemicalSynthesisRegistry.scala to common/alchemy/alchemicalsynthesis/.
- Modify package declarations and imports in all moved files, SteppaBlocks.scala, and ClientProxy.scala.

**Interfaces:**

- Consumes: existing class names and Forge registration APIs.
- Produces: unchanged public class behavior under structure-scoped packages with no underscore package names.

- [ ] Step 1: Use git mv for only the listed Scala files. Do not move or edit any .java file, src/main/java/**, or mixin resource.
- [ ] Step 2: Set package declarations to:
  ```scala
  com.equilcraft.bigsteppa.common.blocks.armorstand
  com.equilcraft.bigsteppa.common.blocks.beaconfarmer
  com.equilcraft.bigsteppa.common.blocks.alchemicalsynthesis
  com.equilcraft.bigsteppa.common.tile.armorstand
  com.equilcraft.bigsteppa.common.tile.beaconfarmer
  com.equilcraft.bigsteppa.common.tile.alchemicalsynthesis
  com.equilcraft.bigsteppa.common.inventory.armorstand
  com.equilcraft.bigsteppa.client.gui.armorstand
  com.equilcraft.bigsteppa.client.model.armorstand
  com.equilcraft.bigsteppa.client.render.armorstand
  com.equilcraft.bigsteppa.common.alchemy.alchemicalsynthesis
  ```
- [ ] Step 3: Update imports while keeping existing block names and tile entity IDs unchanged. Run ./gradlew compileScala compileJava and expect success.
- [ ] Step 4: Commit:
  ```bash
  git add src/main/scala/com/equilcraft/bigsteppa
  git commit -m "refactor: organize scala code by structure"
  ```

### Task 2: Extract structureLib definitions

**Files:**

- Create common/structure/alchemicalsynthesis/AlchemicalSynthesisStructure.scala.
- Create common/structure/beaconfarmer/BeaconFarmerStructure.scala.
- Modify the two moved multiblock tile files and imports in the moved block files.

**Interfaces:**

- Consumes: existing shapes, block adders, SteppaBlocks, and Botania pylon blocks.
- Produces: AlchemicalSynthesisStructure.structureDefinition and BeaconFarmerStructure.structureDefinition.

- [ ] Step 1: Move the exact Alchemical Synthesis createShape implementation, 80-rune require, rune IBlockAdder, and StructureDefinition builder from TileAlchemicalSynthesisCore into AlchemicalSynthesisStructure. Keep the builder elements for R and I and the same registered blocks.
- [ ] Step 2: Move the exact Beacon Farmer shape and upgrade IBlockAdder into BeaconFarmerStructure. Preserve these rules: BlockDamageUpdate increments damage; BlockLootingUpdate increments loot; BlockStructure is accepted without incrementing; all other blocks are rejected.
- [ ] Step 3: Remove StructureDefinition, StructureUtility, IBlockAdder, shape arrays, and builders from both tile files. The only structure reference in each controller is:
  ```scala
  override protected def structureDef: IStructureDefinition[TileAlchemicalSynthesisCore] =
    AlchemicalSynthesisStructure.structureDefinition

  override protected def structureDef: IStructureDefinition[TileBeaconFarmer] =
    BeaconFarmerStructure.structureDefinition
  ```
- [ ] Step 4: Add this callback to TileBeaconFarmer while keeping counters private:
  ```scala
  private[bigsteppa] def recordUpgrade(block: Block): Boolean = block match {
    case _: BlockDamageUpdate => damageUpdate += 1; true
    case _: BlockLootingUpdate => lootUpdate += 1; true
    case _: BlockStructure => true
    case _ => false
  }
  ```
- [ ] Step 5: Run ./gradlew compileScala compileJava. Expect no structure builder implementation under common/tile/**/Tile*.scala. Commit:
  ```bash
  git add src/main/scala/com/equilcraft/bigsteppa
  git commit -m "refactor: extract structurelib definitions"
  ```

### Task 3: Replace the direct Armor Stand GUI handler

**Files:**

- Create common/gui/GuiProvider.scala, common/gui/GuiRegistry.scala, common/gui/GuiHandler.scala.
- Create common/gui/armorstand/ArmorStandGuiProvider.scala.
- Create client/gui/armorstand/ClientArmorStandGuiProvider.scala.
- Delete common/gui/ArmorStandGuiHandler.scala.
- Modify CommonProxy.scala, ClientProxy.scala, and BlockArmorStand.scala.

**Interfaces:**

- Consumes: Armor Stand tile, container, client GUI, and existing GUI ID 1.
- Produces: one Forge-registered GuiHandler and per-structure provider lookup.

- [ ] Step 1: Define GuiProvider with server and client callbacks. The client callback defaults to null so common code has no client imports:
  ```scala
  trait GuiProvider {
    def getServerGuiElement(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef
    def getClientGuiElement(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = null
  }
  ```
- [ ] Step 2: Implement GuiRegistry with Int2ObjectOpenHashMap[GuiProvider]. register rejects duplicate IDs, registerClient replaces an existing provider only during client initialization, and get returns null for unknown IDs.
- [ ] Step 3: Implement GuiHandler as a common-only IGuiHandler that looks up a provider, delegates both callbacks, and returns null for an unknown ID.
- [ ] Step 4: Implement `class ArmorStandGuiProvider extends GuiProvider` in common/gui/armorstand with companion `object ArmorStandGuiProvider { final val armorStandGuiId = 1; val instance = new ArmorStandGuiProvider }`. Its server callback validates TileArmorStand and creates ContainerArmorStand. Implement `class ClientArmorStandGuiProvider extends ArmorStandGuiProvider` in client/gui/armorstand, with companion `object ClientArmorStandGuiProvider { val instance = new ClientArmorStandGuiProvider }`, overriding only the client callback to create GuiArmorStand.
- [ ] Step 5: In CommonProxy.preInit register `ArmorStandGuiProvider.instance` then one GuiHandler with NetworkRegistry. In ClientProxy.preInit, after super.preInit(event), call GuiRegistry.registerClient(ArmorStandGuiProvider.armorStandGuiId, ClientArmorStandGuiProvider.instance). Make BlockArmorStand use ArmorStandGuiProvider.armorStandGuiId. No common file may import com.equilcraft.bigsteppa.client.
- [ ] Step 6: Run ./gradlew compileScala compileJava and commit:
  ```bash
  git add src/main/scala/com/equilcraft/bigsteppa
  git commit -m "refactor: centralize gui registration"
  ```

### Task 4: Normalize Scala names, comments, and imports

**Files:**

- Modify Scala files under src/main/scala/com/equilcraft/bigsteppa/, excluding api/ and the three exempted base classes.
- Preserve unchanged all .java files and mixin integration files.

**Interfaces:**

- Consumes: current identifiers and call sites.
- Produces: lower-camel-case constants with identical values and behavior.

- [ ] Step 1: Apply these mappings everywhere:
  ```text
  SlotHead -> slotHead
  SlotChest -> slotChest
  SlotLegs -> slotLegs
  SlotFeet -> slotFeet
  SlotCount -> slotCount
  GuiId -> armorStandGuiId
  StandStart -> standStart
  StandEnd -> standEnd
  PlayerMainStart -> playerMainStart
  PlayerMainEnd -> playerMainEnd
  HotbarStart -> hotbarStart
  HotbarEnd -> hotbarEnd
  PlayerArmorStart -> playerArmorStart
  PlayerArmorEnd -> playerArmorEnd
  ```
- [ ] Step 2: Scan every Scala final val and rename any remaining declaration beginning with uppercase, updating references.
- [ ] Step 3: Remove comments and section headers from ordinary blocks, tiles, inventories, registries, proxies, and client classes. Preserve comments only in api/ and the three exempted base classes.
- [ ] Step 4: Run ./gradlew compileScala compileJava and commit:
  ```bash
  git add src/main/scala/com/equilcraft/bigsteppa
  git commit -m "refactor: normalize scala naming and comments"
  ```

### Task 5: Verify the complete migration

**Files:**

- Verify all changed Scala files.
- Verify unchanged src/main/java/**, Java files under src/main/scala/**, src/main/resources/mixins*, and mixin JSON files.

- [ ] Step 1: Verify protected files against the pre-migration commit:
  ```bash
  git diff --name-only 42eed3d..HEAD -- '*.java' 'src/main/resources/mixins*' 'src/main/resources/*.json'
  ```
  Expected: no output.
- [ ] Step 2: Verify structure placement and package names:
  ```bash
  rg -n "StructureDefinition|addShape|addElement" src/main/scala/com/equilcraft/bigsteppa/common/tile || true
  find src/main/scala/com/equilcraft/bigsteppa -type d -name '*_*' -print
  ```
  Expected: no structure builder implementation in tile packages and no underscore directory.
- [ ] Step 3: Verify client isolation and constants:
  ```bash
  rg -n "com\\.equilcraft\\.bigsteppa\\.client" src/main/scala/com/equilcraft/bigsteppa/common || true
  rg -n "final val [A-Z]|final\\s+val\\s+[A-Z]" src/main/scala/com/equilcraft/bigsteppa --glob '*.scala' --glob '!**/api/**' || true
  ```
  Expected: no common-to-client imports and no uppercase Scala final val.
- [ ] Step 4: Run ./gradlew build. Expected: BUILD SUCCESSFUL and test NO-SOURCE.
- [ ] Step 5: Review git diff --check, git status --short, and git diff --stat. Expected: no whitespace errors, only intended Scala/package changes, and no protected Java or mixin files modified.
