#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform float u_time;

#define PI 3.14159265359

void main() {
    vec2 coord = 7.5f * gl_FragCoord.xy / u_resolution;
    vec2 r =  2.0 * vec2(gl_FragCoord.xy - 0.5 * u_resolution.xy) / u_resolution.y;

    float t = u_time / 2.0;
    r = r * 8.0;

    float v1 = sin(r.x + t);
    float v2 = sin(r.y + t);
    float v3 = sin(r.x * sin(t) + r.y * cos(t) + t);
    float cx = r.x + 0.5 * sin(t / 2.0);
    float cy = r.y + 0.5 * cos(t / 3.0);
    float v4 = sin(sqrt(cx * cx + cy * cy) + 5.0 * t);
    float v = v1 + v2 + v3 + v4;

    v *= 1.0;
    vec3 ret = vec3(cos(v) * 0.4, sin(v + t * PI) * 0.4, (cos(v + 1.0 * PI) + 0.5) * 1.5);

    gl_FragColor = vec4(ret, 1.0);
}
