#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec3 u_camera;
uniform float u_time;

float random (in vec2 _st) {
    return fract(sin(dot(_st.xy, vec2(12.9898,78.233))) * 43758.5453123);
}

float noise (in vec2 _st) {
    vec2 i = floor (_st);
    vec2 f = fract(_st);

    float a = random(i);
    float b = random (i + vec2(1.0, 0.0));
    float c = random (i + vec2(0.0, 1.0));
    float d = random (i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

#define NUM_OCTAVES 5

float fbm (in vec2 _st) {
    float v = 0.0;
    float a = 0.5;

    vec2 shift = vec2(100.0);
    mat2 rot = mat2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));

    for (int i = 0; i < NUM_OCTAVES; ++i) {
        v += a * noise(_st);
        _st = rot * _st * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec2 coord = 3.0 * gl_FragCoord.xy / u_resolution;
    vec2 cameraCoord = u_camera.xy / u_resolution;
    vec2 st = vec2(coord.x + cameraCoord.x, coord.y + cameraCoord.y);

    vec3 color = vec3(0.0);

    vec2 q = vec2(0.);
    q.x = fbm(st + 0.1 * u_time);
    q.y = fbm(st + vec2(1.0));

    vec2 r = vec2(0.);
    r.x = fbm(st + 0.1 * q + vec2(1.7, 9.2) + 0.15 * u_time);
    r.y = fbm(st + vec2(1.0 * q + vec2(8.3, 2.8) + 0.126 * u_time));

    float f = fbm(st + r);

    color = mix(vec3(0.101961, 0.619608, 0.666667), vec3(0.666667,0.666667,0.498039), clamp((f * f) * 4.0,0.0,1.0));
    color = mix(color, vec3(0,0,0.164706), clamp(length(q),0.0,1.0));
    color = mix(color, vec3(0.666667,1,1), clamp(length(r.x),0.0,1.0));

    gl_FragColor = vec4((f * f * f + .6 * f * f + .5 * f) * color,1.);
}
