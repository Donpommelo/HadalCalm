#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform float u_time;

void main() {
	vec2 coord = 7.5f * gl_FragCoord.xy / u_resolution;
	float aTime = u_time * 0.25;

	for (int n = 1; n < 8; n++) {
		float i = float(n);
		coord += vec2(0.7 / i * sin(i * coord.y + aTime + 0.3 * i) + 0.8, 0.4 / i * sin(coord.x + aTime + 0.3 * i) + 1.6);
	}

	gl_FragColor = vec4(vec3(vec3(0.25 * sin(coord.x) + 0.2, 0.25 * sin(coord.y) + 0.2, sin(coord.x + coord.y) + .8)), 1.0);
}
