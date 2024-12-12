#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform float u_time;

const float SEED = 42.0;

float swayRandomized(float seed, float value) {
    float f = floor(value);
    float start = sin((cos(f * seed) + sin(f * 1024.)) * 345. + seed);
    float end   = sin((cos((f+1.) * seed) + sin((f+1.) * 1024.)) * 345. + seed);
    return mix(start, end, smoothstep(0., 1., value - f));
}

float cosmic(float seed, vec3 con) {
    float sum = swayRandomized(seed, con.z + con.x);
    sum = sum + swayRandomized(seed, con.x + con.y + sum);
    sum = sum + swayRandomized(seed, con.y + con.z + sum);
    return sum * 0.3333333333 + 0.5;
}

void main() {
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = gl_FragCoord.xy / u_resolution;
    // aTime, s, and c could be uniforms in some engines.
    float aTime = u_time * 0.1;
    vec3 s = vec3(swayRandomized(-16405.31527, aTime - 1.11),
    swayRandomized(-77664.8142, aTime + 1.41),
    swayRandomized(-50993.5190, aTime + 2.61)) * 5.;
    vec3 c = vec3(swayRandomized(-10527.92407, aTime - 1.11),
    swayRandomized(-61557.6687, aTime + 1.41),
    swayRandomized(-43527.8990, aTime + 2.61)) * 5.;
    vec3 con = vec3(0.0004375, 0.0005625, 0.0008125) * aTime + c * uv.x + s * uv.y;
    con.x = cosmic(SEED, con) + 0.25;
    con.y = cosmic(SEED, con) + 0.50;
    con.z = cosmic(SEED, con) + 0.75;

    gl_FragColor = vec4(sin(con * 3.14159265) * 0.5 + 0.5,1.0);
}