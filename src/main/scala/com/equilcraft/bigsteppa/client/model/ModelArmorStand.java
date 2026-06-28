package com.equilcraft.bigsteppa.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelArmorStand extends ModelBase {

    private final ModelRenderer base;
    private final ModelRenderer upright;
    private final ModelRenderer shoulders;
    private final ModelRenderer neck;
    private final ModelRenderer leftBrace;
    private final ModelRenderer rightBrace;

    public ModelArmorStand() {
        textureWidth = 64;
        textureHeight = 32;

        base = part(-7.0F, -2.0F, -5.0F, 14, 2, 10, 0.0F, 24.0F, 0.0F);
        upright = part(-1.0F, -20.0F, -1.0F, 2, 20, 2, 0.0F, 22.0F, 0.0F);
        shoulders = part(-7.0F, -1.0F, -1.5F, 14, 2, 3, 0.0F, 4.0F, 0.0F);
        neck = part(-2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, 2.0F, 0.0F);

        leftBrace = part(-1.0F, 0.0F, -1.0F, 2, 8, 2, -5.5F, 4.0F, 0.0F);
        leftBrace.rotateAngleZ = -0.62F;
        rightBrace = part(-1.0F, 0.0F, -1.0F, 2, 8, 2, 5.5F, 4.0F, 0.0F);
        rightBrace.rotateAngleZ = 0.62F;
    }

    private ModelRenderer part(
        float boxX,
        float boxY,
        float boxZ,
        int width,
        int height,
        int depth,
        float pointX,
        float pointY,
        float pointZ
    ) {
        ModelRenderer renderer = new ModelRenderer(this, 0, 0);
        renderer.addBox(boxX, boxY, boxZ, width, height, depth);
        renderer.setRotationPoint(pointX, pointY, pointZ);
        return renderer;
    }

    public void render(float scale) {
        base.render(scale);
        upright.render(scale);
        shoulders.render(scale);
        neck.render(scale);
        leftBrace.render(scale);
        rightBrace.render(scale);
    }
}
