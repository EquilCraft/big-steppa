package com.equilcraft.bigsteppa.mixins.late.thaumicenergistics.stand;

import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumicenergistics.common.container.ContainerArcaneAssembler;
import thaumicenergistics.common.container.slot.SlotArmor;

@Mixin(value = ContainerArcaneAssembler.class, remap = false)
public class ContainerArcaneAssemblerMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 4, ordinal = 2))
	private int bigsteppa$offDiscountSlots(int constant) {
		return 0;
	}

	@Inject(method = "mergeWithArmorSlots", at = @At("HEAD"), cancellable = true)
	private void bigsteppa$mergeWithArmorSlots(ItemStack slotStack, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	@Redirect(
		method = "transferStackInSlot", at = @At(value = "FIELD",
		target = "Lthaumicenergistics/common/container/slot/SlotArmor;slotNumber:I",
		opcode = Opcodes.GETFIELD)
	)
	private int bigsteppa$transferStackInSlot(SlotArmor instance) {
		return -1;
	}
}
