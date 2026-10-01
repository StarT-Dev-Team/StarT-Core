#version 150

#moj_import <start_core:dyson_common.glsl>

uniform vec3 CenterOffset;
uniform float Radius;
uniform vec3 Color;
uniform float Time;
uniform float Activity;
uniform float Giant;
uniform float Heat;
uniform float Brightness;
uniform float Protostar;
uniform float Detail;
uniform float Seed;
uniform float Extent;
uniform float ShellRadius;
uniform float ShellIntensity;
uniform float ShellNebula;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float RingsSolid;
uniform float RingBase;
uniform float RingSpacing;
uniform float RingWidth;
uniform float RingThickness;
uniform vec3 RingNormal0;
uniform vec3 RingNormal1;
uniform vec3 RingNormal2;
uniform vec3 RingNormal3;
uniform vec3 RingNormal4;
uniform vec3 RingNormal5;

in vec3 vWorld;
in vec2 vQuad;

out vec4 fragColor;

void main() {
    DysonStar s = DysonStar(Radius, Color, Time, Activity, Giant, Heat, Brightness, Protostar, Detail, Seed);
    DysonShell shell = DysonShell(ShellRadius, ShellIntensity, ShellNebula);
    vec3 ro = -CenterOffset;
    vec3 rd = normalize(vWorld);
    float tRing = 1e9;
    if (RingsSolid > 0.5) {
        vec3 normals[6] = vec3[](RingNormal0, RingNormal1, RingNormal2, RingNormal3, RingNormal4, RingNormal5);
        for (int i = 0; i < 6; i++) {
            tRing = min(tRing, dsRingHit(ro, rd, normals[i], RingBase + RingSpacing * float(i), RingWidth,
                    RingThickness));
        }
    }
    float t = min(tRing - 0.05, length(vWorld));
    vec4 clip = ProjMat * ModelViewMat * vec4(rd * t, 1.0);
    gl_FragDepth = clip.z / clip.w * 0.5 + 0.5;

    vec3 halo = dsHalo(ro, rd, tRing, Extent, s, shell);
    halo *= 1.0 - smoothstep(0.6, 1.0, length(vQuad));
    fragColor = vec4(stShoulder(halo), 1.0);
}
