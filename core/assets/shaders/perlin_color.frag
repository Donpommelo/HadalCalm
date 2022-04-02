#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float scale = 5.0;
uniform float smoothness = 0.1;

uniform vec2 u_resolution;
uniform float u_time;
uniform float u_random;

float random(vec2 co) {
  float a = u_random;
  float b = 78.233;
  float c = 43758.5453;
  float dt = dot(co.xy, vec2(a, b));
  float sn = mod(dt, 3.14);
  return fract(sin(sn) * c);
}

float noise (in vec2 st) {
  vec2 i = floor(st);
  vec2 f = fract(st);

  // Four corners in 2D of a tile
  float a = random(i);
  float b = random(i + vec2(1.0, 0.0));
  float c = random(i + vec2(0.0, 1.0));
  float d = random(i + vec2(1.0, 1.0));

  // Cubic Hermine Curve.  Same as SmoothStep()
  vec2 u = f * f * (3.0 - 2.0 * f);

  // Mix 4 corners percentages
  return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

void main() {
  vec4 from = texture2D(u_texture, v_texCoords);
  vec3 color = vec3(1.0, 1.0, 1.0);
  vec4 transparent = vec4(0.0, 0.0, 0.0, 0.0);

  float n = noise(v_texCoords * scale);

  float p = mix(-smoothness, 1.0 + smoothness, u_time);
  float p2 = mix(-smoothness, 1.0 + smoothness, min(u_time + 0.25, 1.0));
  float lower = p - smoothness;
  float higher = p + smoothness;
  float lower2 = p2 - smoothness;
  float higher2 = p2 + smoothness;

  float q = smoothstep(lower, higher, n);
  float q2 = smoothstep(lower2, higher2, n);
  gl_FragColor = mix(vec4(mix(from.rgb, color, 1.0 - q2), from.a), transparent, 1.0 - q);
}
