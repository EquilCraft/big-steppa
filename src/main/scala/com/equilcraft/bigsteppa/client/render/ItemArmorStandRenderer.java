package com.equilcraft.bigsteppa.client.render;

import com.equilcraft.bigsteppa.client.model.ModelArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemArmorStandRenderer implements IItemRenderer {

    private final ModelArmorStand model = new ModelArmorStand();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(
        ItemRenderType type,
        ItemStack item,
        ItemRendererHelper helper
    ) {
        return type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        float scale;
        switch (type) {
            case ENTITY:
                scale = 0.45F;
                GL11.glTranslatef(0.0F, 0.68F, 0.0F);
                break;
            case INVENTORY:
                scale = 0.72F;
                GL11.glTranslatef(0.0F, 1.08F, 0.0F);
                GL11.glRotatef(25.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                scale = 0.65F;
                GL11.glTranslatef(0.7F, 1.25F, 0.65F);
                GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EQUIPPED:
            default:
                scale = 0.65F;
                GL11.glTranslatef(0.55F, 1.05F, 0.55F);
                GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                break;
        }

        GL11.glScalef(scale, -scale, -scale);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.48F, 0.29F, 0.13F, 1.0F);
        model.render(0.0625F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}
