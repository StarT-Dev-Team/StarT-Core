#moj_import <start_core:st_noise.glsl>
#moj_import <start_core:st_color.glsl>

struct HellFlame {
    vec4 shape;
    float lift;
    vec4 turbulence;
    float flameTime;
    float core;
    float intensity;
    vec3 theme;
    vec3 fringe;
    float smoke;
    vec4 tint;
    float seed;
    vec2 bounds;
};

struct HFResult {
    vec3 color;
    float transmittance;
    float coverDistance;
};

const float HF_EDGE = 0.12;
const float HF_EXPAND = 0.25;
const float HF_EXPAND_REACH = 0.35;
const float HF_SWAY = 0.05;
const float HF_SWAY_MAX = 0.55;
const float HF_EMISSION = 1.6;
const float HF_CLEAN_OPACITY = 0.8;
const float HF_SOOTY_OPACITY = 1.8;
const float HF_SMOKE_OPACITY = 0.7;

float hfRadius(float h, vec4 shape) {
    if (h < shape.z) {
        float t = max(h / shape.z, 0.0);
        return mix(shape.x, shape.y, t * t * (3.0 - 2.0 * t));
    }
    float t = (h - shape.z) / (1.0 - shape.z);
    return shape.y * pow(max(1.0 - pow(max(t, 0.0), 1.6), 0.0), 0.7);
}

float hfFbm(vec3 p, const int octaves) {
    float value = 0.0;
    float amplitude = 0.5;
    float total = 0.0;
    for (int i = 0; i < octaves; i++) {
        value += amplitude * stNoise(p);
        total += amplitude;
        p = p * 2.03 + vec3(1.7, 9.2, 4.1);
        amplitude *= 0.5;
    }
    return value / total;
}

vec2 hfCylinder(vec3 ro, vec3 rd, float radius, float bottom, float top) {
    vec2 range = vec2(-1e9, 1e9);
    float a = dot(rd.xz, rd.xz);
    float b = dot(ro.xz, rd.xz);
    float c = dot(ro.xz, ro.xz) - radius * radius;
    if (a > 1e-9) {
        float disc = b * b - a * c;
        if (disc < 0.0) return vec2(1.0, -1.0);
        float s = sqrt(disc);
        range = vec2(-b - s, -b + s) / a;
    } else if (c > 0.0) {
        return vec2(1.0, -1.0);
    }
    if (abs(rd.y) > 1e-9) {
        vec2 slab = (vec2(bottom, top) - ro.y) / rd.y;
        range = vec2(max(range.x, min(slab.x, slab.y)), min(range.y, max(slab.x, slab.y)));
    } else if (ro.y < bottom || ro.y > top) {
        return vec2(1.0, -1.0);
    }
    return range;
}

vec2 hfSway(float y, float h, float rise, HellFlame f) {
    float bend = min(f.turbulence.x * clamp(h, 0.0, 1.2), HF_SWAY_MAX);
    bend *= bend * f.shape.w * HF_SWAY;
    float s = f.seed * 6.2831;
    float travel = y - 0.6 * rise;
    return bend * vec2(sin(0.45 * travel + s) + 0.5 * sin(1.1 * travel + 2.3 * s),
                       sin(0.39 * travel + 1.7 * s) + 0.5 * sin(0.93 * travel + 3.1 * s));
}

float hfReach(HellFlame f) {
    float bend = min(f.turbulence.x * 1.2, HF_SWAY_MAX);
    float sway = 2.12 * bend * bend * f.shape.w * HF_SWAY;
    float smoke = f.smoke > 0.0 ? 1.35 * f.shape.y + 0.15 : 0.0;
    return min(max(f.shape.y + min(HF_EXPAND * f.shape.y, HF_EXPAND_REACH) + 0.1, smoke) + sway, f.bounds.x);
}

vec2 hfField(vec3 p, HellFlame f, out float tau, out float h) {
    tau = 0.0;
    float height = max(f.shape.w, 0.05);
    float y = p.y - f.lift;
    h = y / height;
    float rise = f.flameTime * f.turbulence.z;
    vec2 xz = p.xz - hfSway(y, h, rise, f);
    float r = length(xz);
    vec2 density = vec2(0.0);
    float amp = f.turbulence.x;
    float env = h > -0.02 && h < 1.0 ? hfRadius(max(h, 0.0), f.shape) : 0.0;
    float expand = min(HF_EXPAND, HF_EXPAND_REACH / max(env, 0.02));
    if (env > 0.0 && r < env * (1.0 + expand) + 0.02) {
        vec3 q = vec3(xz.x, 0.5 * y - rise, xz.y) * f.turbulence.y + vec3(17.0, 0.0, 31.0) * f.seed;
        float n = (hfFbm(q, 3) - 0.5) * 2.4;
        float erosion = amp * (0.35 * smoothstep(0.0, 0.12, h) + 2.4 * h * h);
        float d = r / max(env, 0.02) - 1.0 - min(erosion * (n - 0.15), expand);
        float baseFade = 0.02 + 0.5 * clamp(f.lift / height, 0.0, 0.4);
        density.x = smoothstep(0.0, HF_EDGE / f.turbulence.w, -d) * smoothstep(-0.02, baseFade, h);
        tau = f.core * (0.3 + 0.7 * smoothstep(0.0, 0.55, -d)) * (1.0 - 0.72 * smoothstep(0.0, 1.0, h));
    }
    if (f.smoke > 0.0) {
        float hs = (h - 0.6) / 0.8;
        float column = f.shape.y * (0.35 + clamp(hs, 0.0, 1.0)) + 0.15;
        if (hs > 0.0 && hs < 1.0 && r < column) {
            vec3 q = vec3(xz.x, 0.6 * y - 0.75 * rise, xz.y) * (0.8 * f.turbulence.y) + vec3(5.3, 11.0, 2.9);
            float billow = hfFbm(q, 2);
            float fall = smoothstep(0.0, 0.25, hs) * (1.0 - smoothstep(0.3, 1.0, hs));
            density.y = f.smoke * fall * smoothstep(column, 0.3 * column, r) * smoothstep(0.45, 0.75, billow);
        }
    }
    return density * (1.0 - smoothstep(f.bounds.x - 0.3, f.bounds.x - 0.05, length(p.xz)))
            * (1.0 - smoothstep(f.bounds.y - 0.8, f.bounds.y - 0.1, p.y));
}

vec3 hfColor(float tau, float h, HellFlame f) {
    vec3 color = stTemperatureRamp(clamp(0.6 * tau, 0.0, 1.0), f.theme);
    color = mix(f.fringe, color, smoothstep(0.1, 0.5, tau));
    return mix(color, f.tint.rgb, f.tint.a * (1.0 - smoothstep(0.0, 0.7, h)));
}

float hfGlow(float tau) {
    float t = clamp(tau, 0.0, 1.3);
    return 0.03 + 3.2 * t * t * t;
}

HFResult hfMarch(vec3 ro, vec3 rd, float tMin, float tMax, int steps, float jitter, HellFlame f) {
    HFResult result;
    result.color = vec3(0.0);
    result.transmittance = 1.0;
    result.coverDistance = -1.0;
    float top = min(f.lift + f.shape.w * (f.smoke > 0.0 ? 1.4 : 1.0), f.bounds.y);
    vec2 tight = hfCylinder(ro, rd, hfReach(f), f.lift - 0.05, top);
    float start = max(tMin, tight.x);
    float end = min(tMax, tight.y);
    if (end <= start) return result;

    float opacity = mix(HF_CLEAN_OPACITY, HF_SOOTY_OPACITY, f.smoke);
    float dt = (end - start) / float(steps);
    float t = start + dt * jitter;
    for (int i = 0; i < steps; i++) {
        if (t > end) break;
        vec3 p = ro + rd * t;
        float tau;
        float h;
        vec2 density = hfField(p, f, tau, h);
        if (density.x + density.y > 0.0) {
            vec3 radiance = vec3(0.0);
            float extinction = 0.0;
            if (density.x > 0.0) {
                extinction += density.x * opacity * mix(0.15, 1.0, smoothstep(0.1, 0.5, tau));
                radiance += density.x * HF_EMISSION * hfColor(tau, h, f) * (f.intensity * hfGlow(tau));
            }
            if (density.y > 0.0) {
                vec3 lit = stTemperatureRamp(0.3, f.theme) * 0.25 * exp(-4.0 * max(h - 0.75, 0.0));
                extinction += density.y * HF_SMOKE_OPACITY;
                radiance += density.y * HF_SMOKE_OPACITY * (vec3(0.03, 0.028, 0.026) + lit * f.intensity);
            }
            float a = exp(-extinction * dt);
            float weight = extinction * dt > 1e-3 ? (1.0 - a) / extinction : dt;
            result.color += result.transmittance * radiance * weight;
            result.transmittance *= a;
            if (result.coverDistance < 0.0 && result.transmittance < 0.5) result.coverDistance = t;
            if (result.transmittance < 0.02) break;
        }
        t += dt;
    }
    return result;
}
