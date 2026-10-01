vec3 stViewRay(vec2 uv, mat4 invProjMat, mat4 modelViewMat) {
    vec4 far = invProjMat * vec4(uv * 2.0 - 1.0, 1.0, 1.0);
    return normalize(transpose(mat3(modelViewMat)) * (far.xyz / far.w));
}

float stSceneDistance(vec2 uv, float depth, mat4 invProjMat) {
    vec4 scene = invProjMat * vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    return length(scene.xyz / scene.w);
}
