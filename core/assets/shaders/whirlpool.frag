#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec3 u_objective;
uniform float u_time;

void main() {
	vec2 coord = gl_FragCoord.xy / u_resolution;
	vec2 objectiveCoord = u_objective.xy / u_resolution;
	vec2 st = vec2(atan(coord.x - objectiveCoord.x, coord.y - objectiveCoord.y), length(coord));
	float aTime = u_time * 0.25;

	float color = 0.0;

	color += sin(aTime * 5.0 + st.y * 5.0 + cos(aTime * 5.0 + st.x * 10.0 + sin(aTime * 5.0 + st.y * 15.0 + cos(aTime * 5.0 + st.x * 20.0 + sin(aTime * 5.0 + st.y * 25.0 + cos(aTime * 5.0 + st.x * 10.0))))));

	gl_FragColor = vec4(vec3(0.0, 0.0, color * coord.x * coord.y + 0.5), 1.0);
}
