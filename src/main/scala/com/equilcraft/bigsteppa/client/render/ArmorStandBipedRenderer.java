package com.equilcraft.bigsteppa.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

final class ArmorStandBipedRenderer extends RenderBiped {

    private static final ResourceLocation STEVE_TEXTURE =
        new ResourceLocation("minecraft", "textures/entity/steve.png");

    ArmorStandBipedRenderer() {
        super(new ModelBiped(0.0F), 0.0F);
        hideModel(modelBipedMain);
    }

    private static void hideModel(ModelBiped model) {
        model.bipedHead.showModel = false;
        model.bipedHeadwear.showModel = false;
        model.bipedBody.showModel = false;
        model.bipedRightArm.showModel = false;
        model.bipedLeftArm.showModel = false;
        model.bipedRightLeg.showModel = false;
        model.bipedLeftLeg.showModel = false;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return STEVE_TEXTURE;
    }
}
