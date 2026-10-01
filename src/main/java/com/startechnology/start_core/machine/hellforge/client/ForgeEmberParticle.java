package com.startechnology.start_core.machine.hellforge.client;

import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.particle.StarTParticles;
import org.joml.Vector3fc;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeEmberParticle extends TextureSheetParticle {

    private float hotRed = 1, hotGreen = 1, hotBlue = 1;
    private float coolRed = 1, coolGreen = 1, coolBlue = 1;

    protected ForgeEmberParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        gravity = -0.1f;
        friction = 0.96f;
        hasPhysics = false;
        lifetime = 30 + random.nextInt(31);
        quadSize = 0.06f + 0.05f * random.nextFloat();
    }

    public void setColors(Vector3fc hot, Vector3fc cool) {
        hotRed = hot.x();
        hotGreen = hot.y();
        hotBlue = hot.z();
        coolRed = cool.x();
        coolGreen = cool.y();
        coolBlue = cool.z();
        setColor(hotRed, hotGreen, hotBlue);
    }

    @Override
    public void tick() {
        super.tick();
        if (removed) return;
        xd += (random.nextFloat() - 0.5f) * 0.012f;
        zd += (random.nextFloat() - 0.5f) * 0.012f;
        float life = (float) age / lifetime;
        setColor(Mth.lerp(life, hotRed, coolRed), Mth.lerp(life, hotGreen, coolGreen),
                Mth.lerp(life, hotBlue, coolBlue));
        alpha = life < 0.6f ? 1 : 1 - (life - 0.6f) / 0.4f;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return quadSize * Math.max(1 - (age + partialTick) / lifetime, 0);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @SubscribeEvent
    public static void onRegisterProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(StarTParticles.FORGE_EMBER.get(), Provider::new);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xd, double yd, double zd) {
            var ember = new ForgeEmberParticle(level, x, y, z, xd, yd, zd);
            ember.pickSprite(sprites);
            return ember;
        }
    }
}
