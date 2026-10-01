#version 150

#moj_import <start_core:black_hole_common.glsl>
#moj_import <start_core:st_view.glsl>

uniform sampler2D SceneColor;
uniform sampler2D SceneDepth;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 InvProjMat;
uniform vec2 ScreenSize;
uniform vec3 CameraOffset;
uniform vec3 FallbackColor;

uniform float Rs;
uniform float DiskInner;
uniform float DiskOuter;
uniform vec3 DiskNormal;
uniform vec3 DiskTangent;
uniform vec3 Theme;
uniform float Spin;
uniform float Time;
uniform float DiskIntensity;
uniform float DiskSpeed;
uniform float DiskHeat;
uniform float RingIntensity;
uniform float GlowIntensity;
uniform float Flare;
uniform float FlareSize;
uniform float ShockRadius;
uniform float ShockIntensity;
uniform float ProxyRadius;
uniform int Steps;
uniform float BendStrength;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec3 rd = stViewRay(uv, InvProjMat, ModelViewMat);
    vec3 ro = CameraOffset;

    float b = dot(ro, rd);
    float disc = b * b - (dot(ro, ro) - ProxyRadius * ProxyRadius);
    if (disc <= 0.0 || -b + sqrt(disc) < 0.0) discard;

    float depth = texture(SceneDepth, uv).r;
    if (depth < 1.0 && stSceneDistance(uv, depth, InvProjMat) < -b - DiskOuter) discard;

    BHParams bh = BHParams(Rs, DiskInner, DiskOuter, DiskNormal, DiskTangent, Theme, Spin, Time, DiskIntensity,
            DiskSpeed, DiskHeat, RingIntensity, GlowIntensity, Flare, FlareSize, ShockRadius, ShockIntensity,
            ProxyRadius, Steps);
    BHResult result = bhTrace(ro, rd, bh);

    vec3 background = FallbackColor;
    vec3 bent = normalize(mix(rd, result.direction, BendStrength));
    vec4 clip = ProjMat * vec4(mat3(ModelViewMat) * bent * 1000.0, 1.0);
    if (clip.w > 0.0) {
        vec2 bentUv = clip.xy / clip.w * 0.5 + 0.5;
        background = mix(FallbackColor, texture(SceneColor, clamp(bentUv, 0.001, 0.999)).rgb,
                stScreenFade(bentUv));
    }
    fragColor = vec4(background * result.transmittance + stShoulder(result.emission), 1.0);
}
