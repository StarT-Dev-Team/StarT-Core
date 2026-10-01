#moj_import <start_core:st_noise.glsl>
#moj_import <start_core:st_color.glsl>

const int BH_MAX_STEPS = 256;
const int BH_MAX_CROSSINGS = 3;
const float BH_FLOW_PERIOD = 10.0;

struct BHParams {
    float rs;
    float diskInner;
    float diskOuter;
    vec3 diskNormal;
    vec3 diskTangent;
    vec3 theme;
    float spin;
    float time;
    float diskIntensity;
    float diskSpeed;
    float diskHeat;
    float ringIntensity;
    float glowIntensity;
    float flare;
    float flareSize;
    float shockRadius;
    float shockIntensity;
    float proxyRadius;
    int steps;
};

struct BHResult {
    vec3 emission;
    float transmittance;
    vec3 direction;
    float closest;
};

vec3 bhAcceleration(vec3 p, float strength) {
    float r2 = dot(p, p);
    return -strength * p / (r2 * r2 * sqrt(r2));
}

float bhDiskLayer(float angle, float lr, float seed) {
    float streaks = stFbm(vec3(cos(angle) * 1.6, sin(angle) * 1.6, lr * 28.0 + seed));
    float clumps = stFbm(vec3(cos(angle) * 5.0, sin(angle) * 5.0, lr * 7.0 + seed + 11.0));
    return streaks * mix(0.35, 1.25, smoothstep(0.3, 0.75, clumps));
}

float bhFilaments(float r, float phi, BHParams bh) {
    float omega = 0.7 * bh.diskSpeed * (0.6 + 0.8 * bh.spin) * pow(bh.diskInner / r, 1.5);
    float cycle = bh.time / BH_FLOW_PERIOD;
    float phaseA = fract(cycle);
    float phaseB = fract(cycle + 0.5);
    float weight = 1.0 - abs(2.0 * phaseA - 1.0);
    float lr = log(r);
    float a = bhDiskLayer(phi - omega * phaseA * BH_FLOW_PERIOD, lr, 0.0);
    float b = bhDiskLayer(phi - omega * phaseB * BH_FLOW_PERIOD + 2.1, lr, 23.0);
    return mix(b, a, weight);
}

vec4 bhDisk(vec3 p, vec3 rayDir, BHParams bh) {
    vec3 emission = vec3(0.0);
    float r = length(p);
    float alpha = 0.0;

    if (bh.shockIntensity > 0.0) {
        float shock = exp(-pow((r - bh.shockRadius) / 0.9, 2.0)) * bh.shockIntensity;
        emission += mix(bh.theme, vec3(1.0), 0.7) * shock * 3.0;
        alpha = max(alpha, shock * 0.5);
    }
    if (bh.diskIntensity <= 0.0 || r < bh.diskInner * 0.9 || r > bh.diskOuter) return vec4(emission, alpha);

    vec3 bx = bh.diskTangent;
    vec3 by = cross(bh.diskNormal, bx);
    float phi = atan(dot(p, by), dot(p, bx));
    float x = (r - bh.diskInner) / (bh.diskOuter - bh.diskInner);
    float edges = smoothstep(-0.03, 0.05, x) * (1.0 - smoothstep(0.6, 1.0, x));
    float temperature = pow(bh.diskInner / r, 0.75);

    float filaments = bhFilaments(r, phi, bh);
    float density = edges * (0.45 + 0.55 * smoothstep(0.35, 0.7, filaments));

    vec3 velocity = normalize(cross(bh.diskNormal, p));
    float beta = min(0.7, sqrt(bh.rs / (2.0 * max(r - bh.rs, 0.01)))) * (0.7 + 0.3 * bh.spin);
    float cosTheta = dot(velocity, -rayDir);
    float gamma = inversesqrt(1.0 - beta * beta);
    float g = sqrt(max(1.0 - bh.rs / r, 0.0)) / (gamma * (1.0 - beta * cosTheta));
    float beaming = clamp(pow(g, 3.0), 0.15, 3.5);

    vec3 color = stTemperatureRamp(temperature, bh.theme);
    color = mix(color, vec3(0.8, 0.88, 1.0), clamp((g - 1.0) * 0.3, 0.0, 0.3));
    color = mix(color, color * vec3(1.0, 0.6, 0.4), clamp((1.0 - g) * 0.8, 0.0, 0.6));
    color = mix(color, vec3(0.85, 0.92, 1.0), bh.diskHeat * 0.5);

    emission += color * beaming * density * temperature * bh.diskIntensity * 2.4;
    alpha = max(alpha, clamp(density * 1.1, 0.0, 1.0));
    return vec4(emission, alpha);
}

BHResult bhTrace(vec3 ro, vec3 rd, BHParams bh) {
    BHResult result;
    result.emission = vec3(0.0);
    result.transmittance = 1.0;
    result.direction = rd;
    result.closest = 1e9;

    float radius = bh.proxyRadius;
    float b = dot(ro, rd);
    float c = dot(ro, ro) - radius * radius;
    float disc = b * b - c;
    if (disc <= 0.0 || -b + sqrt(disc) < 0.0) return result;

    vec3 p = ro + rd * max(-b - sqrt(disc), 0.0);
    vec3 v = rd;
    vec3 angular = cross(p, v);
    float h2 = dot(angular, angular);
    float impact = sqrt(h2);
    int crossings = 0;
    bool escaped = false;

    for (int i = 0; i < BH_MAX_STEPS; i++) {
        if (i >= bh.steps) break;
        float r = length(p);
        result.closest = min(result.closest, r);
        if (r < bh.rs) {
            result.transmittance = 0.0;
            break;
        }
        if (r > radius && dot(p, v) > 0.0) {
            escaped = true;
            break;
        }

        float dt = clamp(0.14 * (r - 0.8 * bh.rs), 0.02, radius * 0.25);
        float strength = 1.5 * bh.rs * h2 * (1.0 - smoothstep(radius * 0.5, radius, r));
        vec3 k1v = bhAcceleration(p, strength);
        vec3 k2v = bhAcceleration(p + 0.5 * dt * v, strength);
        vec3 k3v = bhAcceleration(p + 0.5 * dt * (v + 0.5 * dt * k1v), strength);
        vec3 k4v = bhAcceleration(p + dt * (v + 0.5 * dt * k2v), strength);
        vec3 next = p + dt * (v + dt / 6.0 * (k1v + k2v + k3v));
        vec3 nextV = v + dt / 6.0 * (k1v + 2.0 * k2v + 2.0 * k3v + k4v);

        float d0 = dot(p, bh.diskNormal);
        float d1 = dot(next, bh.diskNormal);
        if (crossings < BH_MAX_CROSSINGS && d0 * d1 < 0.0 && result.transmittance > 0.01) {
            vec3 hit = mix(p, next, d0 / (d0 - d1));
            vec4 disk = bhDisk(hit, normalize(v), bh);
            result.emission += result.transmittance * disk.rgb;
            result.transmittance *= 1.0 - disk.a;
            crossings++;
        }
        p = next;
        v = nextV;
    }

    float critical = 2.598 * bh.rs;
    if (!escaped && impact < critical * 1.02) result.transmittance = 0.0;
    result.direction = normalize(v);

    if (bh.rs > 0.0) {
        float ring = exp(-pow((impact - critical * 1.012) / (0.02 * critical), 2.0));
        result.emission += mix(bh.theme, vec3(1.0, 0.95, 0.88), 0.7) * ring * bh.ringIntensity * 1.2;
        float halo = exp(-max(impact - critical, 0.0) / (2.2 * bh.rs));
        result.emission += bh.theme * halo * bh.glowIntensity * 0.35 * result.transmittance;
    }
    if (bh.flare > 0.0) {
        float approach = length(cross(ro, rd));
        float ahead = step(0.0, -dot(ro, rd));
        result.emission += mix(bh.theme, vec3(1.0), 0.6) * bh.flare * ahead *
                exp(-pow(approach / max(bh.flareSize * 0.25, 0.05), 2.0));
    }
    return result;
}
