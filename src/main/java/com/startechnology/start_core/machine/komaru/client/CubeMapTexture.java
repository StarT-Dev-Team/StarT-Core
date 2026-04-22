package com.startechnology.start_core.machine.komaru.client;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CubeMapTexture extends AbstractTexture {

    protected final ResourceLocation location;
    protected final boolean alpha;
    @Getter
    private int width;
    @Getter
    private int height;
    private static final String[] CUBE_MAP_SIDES = {"right", "left", "top", "bottom", "front", "back"};

    public CubeMapTexture(ResourceLocation location, boolean hasAlphaChannel) {
        this.location = location;
        this.alpha = hasAlphaChannel;
    }

    public boolean loaded() {
        return this.id > 0;
    }

    @Override
    public void load(@NotNull ResourceManager resourceManager) {
        this.id = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, id);

        for (int i = 0; i < CUBE_MAP_SIDES.length; i++) {
            var sideLocation = new ResourceLocation(location.getNamespace(), location.getPath() + "_" + CUBE_MAP_SIDES[i] + ".png");
            var resource = resourceManager.getResource(sideLocation);
            if (resource.isPresent()) {
                try {
                    var textureData = TextureUtil.readResource(resource.get().open());
                    textureData.rewind();

                    ByteBuffer image;
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        var width = stack.mallocInt(1);
                        var height = stack.mallocInt(1);
                        var channels = stack.mallocInt(1);

                        image = STBImage.stbi_load_from_memory(textureData, width, height, channels, alpha ? 4 : 3);
                        if (image == null) {
                            throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
                        }
                        int texFormat = this.alpha ? GL30.GL_RGBA : GL30.GL_RGB;
                        GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, texFormat, width.get(), height.get(), 0, texFormat, GL30.GL_UNSIGNED_BYTE, image);
                        this.width = width.get(0);
                        this.height = height.get(0);
                        STBImage.stbi_image_free(image);
                    }


                } catch (Exception e) {
                    throw new RuntimeException("Failed to load cubemap side: " + sideLocation, e);
                }
            }
        }

        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL30.GL_CLAMP_TO_EDGE);
    }

    @Override
    public void bind() {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.id);
    }
}
