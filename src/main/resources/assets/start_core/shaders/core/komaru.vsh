#version 330

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

in vec3 Position;
in vec3 KomaruPosition;

out vec4 inColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    inColor = vec4(1, 1, 1, 1);
}
