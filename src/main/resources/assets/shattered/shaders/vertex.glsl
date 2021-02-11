#version 330 core

uniform mat4 matProj;
uniform mat4 matModel;
uniform mat4 matCustom = mat4(1.0);

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec4 inColor;
layout(location = 2) in vec2 inUV;

out DATA {
    vec4 color;
    vec2 uv;
} outData;

void main() {
    gl_Position = matProj * matModel * matCustom * vec4(inPos, 1.0);
    outData.color = inColor;
    outData.uv = inUV;
}