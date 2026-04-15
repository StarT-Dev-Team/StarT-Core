#version 330

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in vec4 inColor;

out vec4 outColor;

void main() {
    outColor = vec4(inColor.rgb, 1.0);
}
