package com.equilcraft.bigsteppa.mixins.late.thaumicenergistics.stand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "thaumicenergistics.common.tiles.TileArcaneAssembler$AAInv", remap = false)
public class TileArcaneAssembler$AAInvMixin {
	@ModifyArg(
		method = "<init>", at = @At(value = "INVOKE",
		target = "Lthaumicenergistics/common/inventory/TheInternalInventory;<init>(Ljava/lang/String;II)V"),
		index = 1
	)
	private static int injectCustomSlotCount(int originalCount) {
		return 23;
	}
}
