float stHash(vec3 p) {
    p = fract(p * 0.3183099 + 0.1);
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float stNoise(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(stHash(i + vec3(0, 0, 0)), stHash(i + vec3(1, 0, 0)), f.x),
                   mix(stHash(i + vec3(0, 1, 0)), stHash(i + vec3(1, 1, 0)), f.x), f.y),
               mix(mix(stHash(i + vec3(0, 0, 1)), stHash(i + vec3(1, 0, 1)), f.x),
                   mix(stHash(i + vec3(0, 1, 1)), stHash(i + vec3(1, 1, 1)), f.x), f.y), f.z);
}

float stFbm(vec3 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        value += amplitude * stNoise(p);
        p = p * 2.03 + vec3(1.7, 9.2, 4.1);
        amplitude *= 0.5;
    }
    return value / 0.9375;
}

vec3 stHash3(vec3 p) {
    return vec3(stHash(p), stHash(p + vec3(17.3, 5.1, 9.7)), stHash(p + vec3(3.9, 23.2, 13.1)));
}

vec2 stVoronoi(vec3 p) {
    vec3 cell = floor(p);
    vec3 local = fract(p);
    float f1 = 8.0;
    float f2 = 8.0;
    for (int z = -1; z <= 1; z++) {
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                vec3 offset = vec3(float(x), float(y), float(z));
                vec3 r = offset + stHash3(cell + offset) - local;
                float d = dot(r, r);
                if (d < f1) {
                    f2 = f1;
                    f1 = d;
                } else if (d < f2) {
                    f2 = d;
                }
            }
        }
    }
    return sqrt(vec2(f1, f2));
}
