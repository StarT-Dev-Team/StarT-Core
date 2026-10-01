#moj_import <start_core:st_noise.glsl>
#moj_import <start_core:st_color.glsl>

const int DS_MAX_RINGS = 8;
const int DS_MAX_PROMINENCES = 5;
const float DS_PI = 3.14159265;
const float DS_GRANULE_PERIOD = 20.0;
const float DS_FLARE_PERIOD = 6.0;
const float DS_PROMINENCE_PERIOD = 40.0;
const float DS_SPOT_PERIOD = 300.0;
const float DS_OMEGA = 2.0 * 3.14159265 * 12.0 / 3600.0;

struct DysonStar {
    float radius;
    vec3 color;
    float time;
    float activity;
    float giant;
    float heat;
    float brightness;
    float protostar;
    float detail;
    float seed;
};

struct DysonShell {
    float radius;
    float intensity;
    float nebula;
};

vec3 dsTheme(DysonStar s) {
    vec3 theme = s.color * mix(vec3(1.0), vec3(1.0, 0.55, 0.3), 0.6 * s.giant);
    return theme / max(max(theme.r, theme.g), max(theme.b, 1e-4));
}

vec3 dsLoop(float time, float period, float radius) {
    float angle = 2.0 * DS_PI * time / period;
    return vec3(cos(angle), sin(angle), 0.0) * radius;
}

vec3 dsRotateY(vec3 p, float angle) {
    float c = cos(angle);
    float sn = sin(angle);
    return vec3(c * p.x + sn * p.z, p.y, -sn * p.x + c * p.z);
}

void dsLayers(float time, out float phaseA, out float phaseB, out float weight) {
    float cycle = time / DS_GRANULE_PERIOD;
    phaseA = fract(cycle);
    phaseB = fract(cycle + 0.5);
    weight = 1.0 - abs(2.0 * phaseA - 1.0);
}

vec3 dsSurfaceFrame(vec3 n, DysonStar s, float layerPhase) {
    float sin2 = n.y * n.y;
    float angle = DS_OMEGA * s.time - DS_OMEGA * 0.2 * sin2 * layerPhase * DS_GRANULE_PERIOD;
    return dsRotateY(n, angle);
}

float dsGranuleLayer(vec3 n, float scale, float phase, float cycleId, DysonStar s) {
    vec3 shift = stHash3(vec3(cycleId, s.seed, 3.7)) * 40.0;
    vec3 drift = vec3(0.0, 0.0, phase * (0.35 + 0.4 * s.activity));
    vec3 p = n * scale + shift + drift;
    p += (vec3(stNoise(p * 0.7), stNoise(p * 0.7 + 11.0),
            stNoise(p * 0.7 + 23.0)) - 0.5) * 0.9;
    vec2 v = stVoronoi(p);
    float lanes = smoothstep(0.0, 0.25 + 0.2 * s.giant, v.y - v.x);
    float center = 1.0 - smoothstep(0.1, 0.95, v.x);
    return mix(0.35 - 0.25 * s.giant, 1.0, lanes) * mix(0.72 - 0.2 * s.giant, 1.0, center);
}

float dsGranulation(vec3 n, DysonStar s) {
    float phaseA, phaseB, weight;
    dsLayers(s.time, phaseA, phaseB, weight);
    float cycle = s.time / DS_GRANULE_PERIOD;
    float scale = mix(40.0, 4.5, s.giant);
    vec3 na = dsSurfaceFrame(n, s, phaseA);
    vec3 nb = dsSurfaceFrame(n, s, phaseB);
    float a = dsGranuleLayer(na, scale, phaseA, floor(cycle), s);
    float b = dsGranuleLayer(nb, scale, phaseB, floor(cycle + 0.5) + 101.0, s);
    float g = mix(b, a, weight);
    float footprint = length(fwidth(n));
    g = mix(g, 0.62, clamp(footprint * scale * 1.2 - 0.2, 0.0, 1.0));
    if (s.detail > 1.5 && footprint * scale * 2.7 < 1.0) {
        float fine = mix(dsGranuleLayer(na, scale * 2.7, phaseA, floor(cycle) + 7.0, s),
                dsGranuleLayer(nb, scale * 2.7, phaseB, floor(cycle + 0.5) + 57.0, s), weight);
        g = mix(g, g * (0.75 + 0.5 * fine), 1.0 - s.giant);
    }
    float supergranules = stFbm(dsSurfaceFrame(n, s, 0.0) * 3.0 + vec3(s.seed));
    return clamp(g * (0.85 + 0.3 * supergranules), 0.0, 1.0);
}

vec2 dsSpots(vec3 n, DysonStar s) {
    vec3 p = dsSurfaceFrame(n, s, 0.0);
    float latitude = abs(asin(clamp(n.y, -1.0, 1.0)));
    float band = smoothstep(0.08, 0.17, latitude) * (1.0 - smoothstep(0.52, 0.66, latitude));
    float cycle = s.time / DS_SPOT_PERIOD;
    float weight = 1.0 - abs(2.0 * fract(cycle) - 1.0);
    float field = mix(stFbm(p * 3.2 + stHash3(vec3(floor(cycle + 0.5), s.seed, 1.0)) * 50.0),
            stFbm(p * 3.2 + stHash3(vec3(floor(cycle), s.seed, 2.0)) * 50.0), weight);
    float threshold = mix(0.78, 0.58, s.activity);
    float spot = smoothstep(threshold, threshold + 0.05, field) * band;
    float umbra = smoothstep(threshold + 0.07, threshold + 0.1, field) * band;
    float filaments = stNoise(p * vec3(70.0, 70.0, 70.0) + field * 18.0);
    float penumbra = spot * (1.0 - umbra) * (0.75 + 0.5 * filaments);
    float faculae = smoothstep(threshold - 0.09, threshold - 0.01, field) * (1.0 - spot) * band;
    return vec2(max(umbra, penumbra * 0.6), faculae);
}

float dsFlares(vec3 n, DysonStar s) {
    float total = 0.0;
    for (int k = 0; k < 3; k++) {
        float slotTime = s.time / DS_FLARE_PERIOD + float(k) / 3.0;
        float slot = floor(slotTime);
        float age = fract(slotTime) * DS_FLARE_PERIOD;
        vec3 h = stHash3(vec3(slot, float(k) * 13.1, s.seed));
        if (h.x > s.activity * 0.8) continue;
        float latitude = (h.y * 2.0 - 1.0) * 0.55;
        float longitude = h.z * 2.0 * DS_PI;
        vec3 site = vec3(cos(latitude) * cos(longitude), sin(latitude), cos(latitude) * sin(longitude));
        float envelope = smoothstep(0.0, 0.4, age) * exp(-age / 1.4);
        float distance = acos(clamp(dot(dsSurfaceFrame(n, s, 0.0), site), -1.0, 1.0));
        total += envelope * exp(-pow(distance / 0.045, 2.0));
    }
    return total;
}

vec3 dsStarSurface(vec3 n, vec3 viewDir, DysonStar s) {
    float mu = clamp(dot(n, -viewDir), 0.0, 1.0);
    float a = mix(0.47, 0.8, s.giant);
    float b = mix(0.23, 0.12, s.giant);
    float limb = max(1.0 - a * (1.0 - mu) - b * (1.0 - mu) * (1.0 - mu), 0.0);

    float g = dsGranulation(n, s);
    vec2 spots = dsSpots(n, s);
    float flare = dsFlares(n, s);
    float limbFactor = pow(1.0 - mu, 2.0);

    float temperature = 0.74 + 0.16 * (g - 0.5) - 0.38 * (1.0 - mu) + s.heat;
    temperature -= 0.45 * spots.x;
    temperature += 0.14 * spots.y * limbFactor + 0.5 * flare;
    float intensity = limb * (0.7 + 0.6 * g) * (1.0 - 0.8 * spots.x) * (1.0 + 0.6 * spots.y * limbFactor) *
            (1.0 + 2.0 * flare);

    if (s.protostar > 0.0) {
        float dust = stFbm(dsSurfaceFrame(n, s, 0.0) * 4.0 + dsLoop(s.time, 600.0, 6.0) + vec3(s.seed));
        float swirl = stFbm(n * 9.0 + vec3(dust * 3.0, 0.0, 0.0) + dsLoop(s.time, 300.0, 8.0));
        float protoTemperature = 0.12 + 0.28 * swirl + s.heat;
        float protoIntensity = (0.45 + 0.8 * swirl) * mix(0.45, 1.0, mu) * smoothstep(0.2, 0.7, dust + 0.25);
        temperature = mix(temperature, protoTemperature, s.protostar);
        intensity = mix(intensity, protoIntensity, s.protostar);
    }

    vec3 color = stTemperatureRamp(clamp(temperature, 0.0, 1.0), dsTheme(s));
    vec3 bloom = mix(dsTheme(s), vec3(1.0), 0.7) * 0.22 * (1.0 - s.protostar * 0.7);
    return (color * intensity * 1.2 + bloom) * s.brightness;
}

vec3 dsStarInterior(DysonStar s) {
    return stTemperatureRamp(clamp(0.9 + s.heat, 0.0, 1.0), dsTheme(s)) * 3.0 * s.brightness;
}

vec4 dsProminence(int k, DysonStar s, out vec3 tangent, out float erupting) {
    float slotTime = s.time / DS_PROMINENCE_PERIOD + float(k) * 0.37;
    float slot = floor(slotTime);
    vec3 h = stHash3(vec3(slot, float(k) * 7.3, s.seed + 5.0));
    float latitude = (h.x * 2.0 - 1.0) * 0.6;
    float longitude = h.y * 2.0 * DS_PI - DS_OMEGA * s.time;
    vec3 mid = vec3(cos(latitude) * cos(longitude), sin(latitude), cos(latitude) * sin(longitude));
    vec3 up = abs(mid.y) < 0.9 ? vec3(0.0, 1.0, 0.0) : vec3(1.0, 0.0, 0.0);
    vec3 east = normalize(cross(up, mid));
    vec3 north = cross(mid, east);
    float turn = h.z * DS_PI;
    tangent = cos(turn) * east + sin(turn) * north;
    erupting = step(h.z, 0.25 * s.activity);
    return vec4(mid, fract(slotTime));
}

float dsProminenceDensity(vec3 p, int k, DysonStar s) {
    vec3 tangent;
    float erupting;
    vec4 arch = dsProminence(k, s, tangent, erupting);
    float life = arch.w;
    float rise = smoothstep(0.0, 0.2, life);
    float fall = 1.0 - smoothstep(0.75, 1.0, life);
    float lift = erupting * smoothstep(0.6, 1.0, life);
    float span = s.radius * 0.22;
    float height = s.radius * (0.14 + 0.24 * rise + 1.4 * lift);

    float u = dot(p, tangent);
    float v = dot(p, arch.xyz) - s.radius * 0.98;
    float w = dot(p, cross(arch.xyz, tangent));
    if (v < -0.02 * s.radius) return 0.0;
    float e = sqrt((u / span) * (u / span) + (v / height) * (v / height)) - 1.0;
    float d = length(vec2(e * min(span, height), w));
    float thickness = s.radius * (0.03 + 0.03 * lift);
    float strands = 0.55 + 0.45 * stNoise(vec3(u, v, w) * (18.0 / s.radius) + dsLoop(s.time, 60.0, 4.0) +
            vec3(0.0, 0.0, float(k)));
    float drain = mix(1.0 - smoothstep(height * (fall - 0.2), height * fall + 1e-3, v), 1.0, erupting);
    return exp(-(d / thickness) * (d / thickness)) * strands * rise * max(fall, lift) * drain * (1.0 - 0.7 * lift);
}

float dsShellGlow(float b, float radius, float thickness) {
    if (radius <= 0.0) return 0.0;
    float x = b / radius;
    float inside = x < 1.0 ? 0.35 / sqrt(max(1.0 - x * x, 0.015)) : 0.0;
    float rim = (b - radius) / (radius * thickness);
    return inside * (1.0 - smoothstep(1.0, 1.0 + thickness, x)) + exp(-rim * rim);
}

vec3 dsHalo(vec3 ro, vec3 rd, float tMax, float extent, DysonStar s, DysonShell shell) {
    vec3 theme = dsTheme(s);
    float along = -dot(ro, rd);
    vec3 closest = ro + rd * along;
    float b = length(closest);
    float x = b / max(s.radius, 1e-3);
    vec3 color = vec3(0.0);
    bool hit = tMax < 1e8;
    float coronaRadius = 3.0 * max(s.radius, 1.0);
    float chord = sqrt(max(coronaRadius * coronaRadius - b * b, 1e-4));
    float share = hit ? clamp((tMax - (along - chord)) / (2.0 * chord), 0.0, 1.0) * step(b, coronaRadius) : 1.0;
    float shellShare = hit ? 0.5 + atan((tMax - along) / max(b, max(s.radius, 1.0))) / DS_PI : 1.0;

    if (s.radius > 0.0 && s.brightness > 0.0) {
        vec3 facing = ro / max(length(ro), 1e-4);
        vec3 dir = normalize(closest - facing * dot(closest, facing) + 1e-5);
        vec3 turned = dsRotateY(dir, DS_OMEGA * s.time);

        float front = hit ? clamp((along - tMax) / max(s.radius, 1.0), 0.0, 1.0) : 0.0;
        float edge = mix(smoothstep(1.0, 1.035, x), 1.0, front);
        float corona = max(pow(max(x, 1.0), -2.5) - 0.02, 0.0) * edge;
        float equator = 0.45 + 0.55 * pow(max(1.0 - dir.y * dir.y, 0.0), 0.75);
        float streamers = stFbm(turned * 2.4 + vec3(0.0, 0.0, s.seed));
        streamers = pow(clamp(streamers * 1.4 - 0.2, 0.0, 1.0), 2.0) * mix(smoothstep(1.02, 1.35, x), 1.0, front) *
                exp(-(x - 1.0) * 0.55);
        float coronaLight = (corona * (0.55 + 0.35 * equator) + 1.4 * streamers * equator) *
                (1.0 - s.protostar * 0.8);
        color += mix(theme, vec3(1.0), 0.55) * coronaLight * 0.45;

        float spicules = 0.6 + 0.4 * stNoise(turned * 60.0 + dsLoop(s.time, 20.0, 3.0));
        float chromosphere = exp(-max(x - 1.0, 0.0) / 0.012) * step(1.0, x) * spicules;
        color += mix(vec3(1.0, 0.35, 0.45), theme, 0.35) * chromosphere * 0.7 * (1.0 - s.protostar);

        float glare = (0.3 * exp(-(max(x, 1.0) - 1.0) * 0.9) + 0.06 * pow(max(x, 1.0), -1.3)) *
                (0.35 + 0.65 * edge);
        color *= share;
        if (!hit) color += mix(theme, vec3(1.0), 0.7) * glare;

        int count = int(2.0 + s.detail * 1.5 * (0.4 + 0.6 * s.activity));
        float outer = s.radius * 1.5;
        float disc = along * along - (dot(ro, ro) - outer * outer);
        if (disc > 0.0 && s.protostar < 0.5 && s.detail >= 0.0) {
            float t0 = max(along - sqrt(disc), 0.0);
            float t1 = min(along + sqrt(disc), tMax);
            float inner = along * along - (dot(ro, ro) - s.radius * s.radius);
            if (inner > 0.0 && along - sqrt(inner) > 0.0) t1 = min(t1, along - sqrt(inner));
            if (t1 > t0) {
                const int STEPS = 24;
                float dt = (t1 - t0) / float(STEPS);
                float density = 0.0;
                for (int i = 0; i < STEPS; i++) {
                    vec3 p = ro + rd * (t0 + dt * (float(i) + 0.5));
                    for (int k = 0; k < DS_MAX_PROMINENCES; k++) {
                        if (k >= count) break;
                        density += dsProminenceDensity(p, k, s);
                    }
                }
                color += mix(vec3(1.0, 0.3, 0.25), theme, 0.25) * density * dt / s.radius * 14.0;
            }
        }
        color *= s.brightness;
    }

    if (shell.intensity > 0.0) {
        float glow = dsShellGlow(b, shell.radius, mix(0.08, 0.35, shell.nebula));
        float gas = stFbm(closest / max(shell.radius, 1.0) * 2.8 + vec3(s.seed));
        gas = mix(gas, stFbm(closest / max(shell.radius, 1.0) * 7.0 + vec3(gas * 2.0)), 0.4);
        vec3 nebula = mix(vec3(0.3, 0.9, 0.8), vec3(0.9, 0.3, 0.6), gas) * (0.4 + 0.8 * gas);
        color += mix(mix(theme, vec3(1.0), 0.75), nebula, shell.nebula) * glow * shell.intensity * shellShare;
    }
    return color * (1.0 - smoothstep(0.7, 1.0, b / extent));
}

struct DysonRings {
    int count;
    float width;
    float thickness;
    float glow;
    float heat;
    float ambient;
};

float dsRingHit(vec3 ro, vec3 rd, vec3 n, float radius, float width, float thickness) {
    float best = 1e9;
    vec3 qp = ro - n * dot(ro, n);
    vec3 dp = rd - n * dot(rd, n);
    float a = dot(dp, dp);
    float half_ = width * 0.5;
    for (int wall = 0; wall < 2; wall++) {
        float r = wall == 0 ? radius : radius + thickness;
        float b = dot(qp, dp);
        float disc = b * b - a * (dot(qp, qp) - r * r);
        if (disc < 0.0 || a < 1e-8) continue;
        for (int side = 0; side < 2; side++) {
            float t = (-b + (side == 0 ? -1.0 : 1.0) * sqrt(disc)) / a;
            if (t > 0.01 && t < best && abs(dot(ro + rd * t, n)) <= half_) best = t;
        }
    }
    float dn = dot(rd, n);
    if (abs(dn) > 1e-6) {
        for (int side = 0; side < 2; side++) {
            float t = ((side == 0 ? half_ : -half_) - dot(ro, n)) / dn;
            vec3 x = ro + rd * t;
            float radial = length(x - n * dot(x, n));
            if (t > 0.01 && t < best && radial >= radius && radial <= radius + thickness) best = t;
        }
    }
    return best;
}

float dsRingShadow(vec3 p, int self, vec3 normals[DS_MAX_RINGS], float radii[DS_MAX_RINGS], DysonRings rings,
                   DysonStar s) {
    float lit = 1.0;
    float toStar = length(p);
    for (int i = 0; i < DS_MAX_RINGS; i++) {
        if (i >= rings.count) break;
        if (i >= self) break;
        vec3 n = normals[i];
        float axial = dot(p, n);
        float radial = length(p - axial * n);
        if (radial <= radii[i]) continue;
        float s01 = 1.0 - radii[i] / radial;
        float height = axial * (1.0 - s01);
        float projected = s.radius * s01;
        float half_ = rings.width * 0.5;
        float coverage = (half_ / max(projected, 1e-3)) * 1.2732395 *
                sqrt(max(1.0 - (height / max(projected + half_, 1e-3)) * (height / max(projected + half_, 1e-3)),
                        0.0));
        lit *= 1.0 - clamp(coverage, 0.0, 0.95);
    }
    return lit;
}

float dsGgx(float nh, float alpha) {
    float a2 = alpha * alpha;
    float d = nh * nh * (a2 - 1.0) + 1.0;
    return a2 / (DS_PI * d * d);
}

struct DysonPanel {
    vec3 albedo;
    float roughness;
    float seam;
    float greeble;
    float column;
};

DysonPanel dsRingPanel(vec2 panelUv, float radius, float ring) {
    float panels = floor(radius * 2.0);
    vec2 grid = vec2(panelUv.x * panels, panelUv.y * 3.0);
    vec2 cell = floor(grid);
    vec2 local = fract(grid);
    float plate = stHash(vec3(cell, ring + 0.5));
    float seam = smoothstep(0.0, 0.035, local.x) * (1.0 - smoothstep(0.965, 1.0, local.x)) *
            smoothstep(0.0, 0.06, local.y) * (1.0 - smoothstep(0.94, 1.0, local.y));
    float greeble = stNoise(vec3(grid * vec2(4.0, 6.0), ring));
    float hoop = smoothstep(0.035, 0.06, panelUv.y) * (1.0 - smoothstep(0.94, 0.965, panelUv.y));
    vec3 albedo = vec3(0.62, 0.6, 0.57) * mix(0.75, 1.1, plate) * mix(0.55, 1.0, seam) * mix(0.35, 1.0, hoop);
    return DysonPanel(albedo, mix(0.28, 0.6, plate) + 0.15 * greeble, seam, greeble, cell.x);
}

float dsRingStrip(vec2 panelUv, DysonPanel panel) {
    return smoothstep(0.42, 0.45, panelUv.y) * (1.0 - smoothstep(0.55, 0.58, panelUv.y)) * panel.seam;
}

vec3 dsRingShade(vec3 p, vec3 normal, vec3 viewDir, vec2 panelUv, bool inner, int self,
                 vec3 normals[DS_MAX_RINGS], float radii[DS_MAX_RINGS], DysonRings rings, DysonStar s) {
    vec3 theme = dsTheme(s);
    DysonPanel panel = dsRingPanel(panelUv, radii[self], float(self));
    vec3 albedo = panel.albedo;
    float roughness = panel.roughness;

    vec3 toStar = -p;
    float distance = length(toStar);
    vec3 l = toStar / distance;
    vec3 v = -viewDir;
    float sinA = clamp(s.radius / distance, 0.0, 1.0);
    float nl = dot(normal, l);
    float diffuse = clamp((nl + sinA) / (1.0 + sinA), 0.0, 1.0);
    diffuse *= diffuse;

    vec3 r = reflect(viewDir, normal);
    vec3 centerToRay = dot(toStar, r) * r - toStar;
    vec3 closest = toStar + centerToRay * clamp(s.radius / max(length(centerToRay), 1e-4), 0.0, 1.0);
    vec3 ls = normalize(closest);
    vec3 h = normalize(ls + v);
    float alpha = roughness * roughness;
    float alphaSphere = clamp(alpha + 0.5 * sinA, 0.0, 1.0);
    float energy = (alpha / alphaSphere) * (alpha / alphaSphere);
    float nv = max(dot(normal, v), 1e-3);
    float nls = max(dot(normal, ls), 0.0);
    vec3 fresnel = albedo + (1.0 - albedo) * pow(1.0 - max(dot(h, v), 0.0), 5.0);
    float geometry = 0.5 / mix(2.0 * nls * nv, nls + nv, alpha);
    vec3 specular = fresnel * dsGgx(max(dot(normal, h), 0.0), alpha) * energy * geometry * nls;

    float shadow = dsRingShadow(p, self, normals, radii, rings, s);
    vec3 starLight = mix(theme, vec3(1.0), 0.45) * s.brightness * (1.2 + 8.0 * sinA * sinA);
    vec3 color = (albedo * 0.25 * diffuse + specular * 0.6) * starLight * shadow;

    color += albedo * mix(theme, vec3(1.0), 0.5) * 0.06 * s.brightness;
    color += albedo * rings.ambient * (0.3 + 0.2 * normal.y) + specular * rings.ambient * 0.02;

    if (inner) {
        float strip = dsRingStrip(panelUv, panel);
        float flicker = 0.85 + 0.15 * stNoise(vec3(panel.column, float(self), 0.0) + dsLoop(s.time, 10.0, 2.0));
        color += theme * strip * rings.glow * flicker * 1.6;
        float proximity = exp(-max(distance - s.radius, 0.0) / 6.0);
        color += vec3(1.0, 0.38, 0.12) * rings.heat * proximity * (0.6 + 0.4 * panel.greeble) * 1.8;
    }
    return color;
}

vec3 dsWeldGlow(float turn, float sweep, float radius, vec3 theme) {
    if (sweep >= 1.0) return vec3(0.0);
    float gap = (sweep - turn) * radius * 2.0 * DS_PI;
    return mix(theme, vec3(1.0), 0.8) * exp(-gap * gap / 2.0) * 4.0 * step(0.0, gap);
}

vec3 dsGodRays(sampler2D sceneDepth, vec2 uv, vec2 starUv, float starDepth, float coronaRadius, float aspect,
               int samples, vec3 tint, float intensity) {
    vec2 delta = (starUv - uv) / float(samples) * 0.9;
    vec2 coord = uv;
    float decay = 1.0;
    float light = 0.0;
    for (int i = 0; i < 96; i++) {
        if (i >= samples) break;
        coord += delta;
        vec2 inside = step(vec2(0.0), coord) * step(coord, vec2(1.0));
        float depth = texture(sceneDepth, coord).r;
        vec2 offset = (coord - starUv) * vec2(aspect, 1.0);
        float corona = 1.0 - smoothstep(0.5, 1.0, length(offset) / coronaRadius);
        light += step(starDepth, depth) * corona * inside.x * inside.y * decay;
        decay *= 0.965;
    }
    float reach = length((uv - starUv) * vec2(aspect, 1.0)) / (coronaRadius * 4.0);
    float behind = step(starDepth, texture(sceneDepth, uv).r);
    return tint * light / float(samples) * intensity * exp(-reach * reach) * behind;
}
