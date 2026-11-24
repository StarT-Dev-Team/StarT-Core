vec3 stTemperatureRamp(float t, vec3 theme) {
    vec3 mid = theme / max(max(theme.r, theme.g), theme.b);
    vec3 cool = mid * mid;
    vec3 hot = mix(mid, vec3(1.0), 0.85);
    return t < 0.5 ? mix(cool, mid, t * 2.0) : mix(mid, hot, t * 2.0 - 1.0);
}

vec3 stShoulder(vec3 c) {
    vec3 over = max(c - 0.8, 0.0);
    return min(c, vec3(0.8)) + 0.2 * (1.0 - exp(-over / 0.2));
}

float stScreenFade(vec2 uv) {
    vec2 outside = max(-uv, uv - 1.0);
    return 1.0 - smoothstep(0.0, 0.15, max(max(outside.x, outside.y), 0.0));
}
