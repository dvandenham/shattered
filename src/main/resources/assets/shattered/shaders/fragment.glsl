#version 330 core

uniform sampler2D samler;
uniform bool doTexture;

in DATA {
	vec4 color;
	vec2 uv;
} inData;

out vec4 outColor;

void main() {
	if (doTexture) {
		outColor = inData.color * texture(samler, inData.uv);
	} else {
		outColor = inData.color;
	}
}