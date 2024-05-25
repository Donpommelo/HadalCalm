#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform float u_time;

uniform sampler2D u_texture;

vec3 hueShift(vec3 color, float angle) {
	const mat3 mat = mat3(
	0.299, 0.587, 0.114,
	0.299, 0.587, 0.114,
	0.299, 0.587, 0.114
	);
	const mat3 cosMat = mat3(
	0.701, -0.587, -0.114,
	-0.299, 0.413, -0.114,
	-0.3, -0.588, 0.886
	);
	const mat3 sinMat = mat3(
	0.168, 0.330, -0.497,
	0.330, -0.174, -0.497,
	0.330, -0.174, -0.498
	);
	return mat * color + cos(angle) * cosMat * color + sin(angle) * sinMat * color;
}

void main() {
	vec4 color = texture(u_texture, v_texCoords);
	gl_FragColor = vec4(hueShift(color.rgb, u_time), color.a);
}
