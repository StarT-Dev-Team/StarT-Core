#version 150

#moj_import <start_core:neutron_star_common.glsl>
#moj_import <start_core:st_view.glsl>

uniform sampler2D SceneDepth;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 InvProjMat;
uniform vec2 ScreenSize;
uniform vec3 CenterOffset;
uniform float Scale;
uniform mat3 Basis;

uniform mat3 StarMat;
uniform mat3 JetMat;
uniform mat3 LoopMat0;
uniform mat3 LoopMat1;
uniform mat3 LoopMat2;
uniform mat3 LoopMat3;
uniform mat3 LoopMat4;
uniform mat3 LoopMat5;
uniform mat3 LoopMat6;
uniform mat3 LoopMat7;
uniform vec4 LoopGrowA;
uniform vec4 LoopGrowB;
uniform float SurfaceTime;
uniform float StarRadius;
uniform vec4 Surface;
uniform vec3 SurfaceFlow;
uniform vec2 JetWidth;
uniform float JetLength;
uniform vec4 LoopShape;
uniform float LoopMinor;
uniform float BeadPhase;
uniform float BeadRadius;
uniform vec4 GlowFalloff;
uniform vec4 GlowWeight;
uniform int Steps;
uniform float SurfDist;
uniform vec3 HitColor;
uniform vec3 MissColor;
uniform vec2 Gain;
uniform float HaloBound;
uniform float JetBoundRadius;
uniform float Flash;
uniform float ShellRadius;
uniform float ShellIntensity;
uniform float DarkCore;
uniform float Time;

out vec4 fragColor;

float depthAt(vec3 cameraRelative) {
    vec4 clip = ProjMat * ModelViewMat * vec4(cameraRelative, 1.0);
    return clamp(clip.z / clip.w * 0.5 + 0.5, 0.0, 1.0);
}

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec3 rdWorld = stViewRay(uv, InvProjMat, ModelViewMat);
    float sceneDepth = texture(SceneDepth, uv).r;
    float sceneDistance = sceneDepth < 1.0 ? stSceneDistance(uv, sceneDepth, InvProjMat) : 1e9;

    vec3 ro = Basis * -CenterOffset / Scale;
    vec3 rd = Basis * rdWorld;
    vec2 range = nsBounds(ro, rd, HaloBound, JetLength, JetBoundRadius);
    float tMax = min(range.y, sceneDistance / Scale);
    vec2 closest = nsClosest(ro, rd);
    bool centerVisible = closest.x > 0.0 && closest.x < sceneDistance / Scale;
    bool inside = length(ro) < StarRadius;
    bool marched = !inside && range.x < range.y && tMax > max(range.x, 0.0);
    if (!inside && !marched && ShellIntensity <= 0.0 && Flash <= 0.0 && DarkCore <= 0.0) discard;

    NeutronStar ns;
    ns.starMat = StarMat;
    ns.jetMat = JetMat;
    ns.loopMat[0] = LoopMat0;
    ns.loopMat[1] = LoopMat1;
    ns.loopMat[2] = LoopMat2;
    ns.loopMat[3] = LoopMat3;
    ns.loopMat[4] = LoopMat4;
    ns.loopMat[5] = LoopMat5;
    ns.loopMat[6] = LoopMat6;
    ns.loopMat[7] = LoopMat7;
    for (int i = 0; i < 4; i++) {
        ns.loopGrow[i] = LoopGrowA[i];
        ns.loopGrow[i + 4] = LoopGrowB[i];
    }
    ns.surfaceTime = SurfaceTime;
    ns.starRadius = StarRadius;
    ns.surface = Surface;
    ns.surfaceFlow = SurfaceFlow;
    ns.jetWidth = JetWidth;
    ns.jetLength = JetLength;
    ns.loopShape = LoopShape;
    ns.loopMinor = LoopMinor;
    ns.beadPhase = BeadPhase;
    ns.beadRadius = BeadRadius;
    ns.glowFalloff = GlowFalloff;
    ns.glowWeight = GlowWeight;
    ns.steps = Steps;
    ns.surfDist = SurfDist;

    vec4 color = vec4(0.0);
    float depth = sceneDepth;
    if (inside) {
        color = vec4(HitColor * 0.6 + 0.3, 0.0);
    } else if (marched) {
        NSResult result = nsMarch(ro, rd, range.x, tMax, ns);
        if (result.hit) {
            float alpha = nsHitAlpha(result);
            color = vec4(HitColor * result.glow * Gain.x, alpha);
            if (alpha > 0.5) depth = depthAt(rdWorld * result.distance * Scale);
        } else {
            color = vec4(MissColor * result.glow * Gain.y, 0.0);
        }
    }
    if (centerVisible) color.rgb += nsGlare(ro, rd, Flash, HitColor);
    color.rgb += nsShell(ro, rd, ShellRadius, ShellIntensity, HitColor, Time);
    if (centerVisible && closest.y < DarkCore) {
        color = vec4(HitColor * 0.4 * smoothstep(0.6 * DarkCore, DarkCore, closest.y), 1.0);
        depth = depthAt(rdWorld * closest.x * Scale);
    }
    if (color.a <= 0.0 && max(color.r, max(color.g, color.b)) < 0.5 / 255.0) discard;
    gl_FragDepth = depth;
    fragColor = clamp(color, 0.0, 1.0);
}
