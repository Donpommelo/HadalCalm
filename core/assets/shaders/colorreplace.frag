#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec3 oldcolor1;
uniform vec3 oldcolor2;
uniform vec3 newcolor1;
uniform vec3 newcolor2;

vec3 rgb2hsv(vec3 c) {
  vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
  vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
  vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

  float d = q.x - min(q.w, q.y);
  float e = 1.0e-10;
  return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
  vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
  vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
  return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
  vec4 color = texture2D(u_texture, v_texCoords);
  vec3 colorRGB = color.rgb;
  vec3 colorHSV = rgb2hsv(colorRGB);
  float dst = 0.15;

  if (distance(colorHSV.xy, oldcolor1.xy) < dst) {
    vec3 newHSV = newcolor1;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor1.z;
  } else if (distance(colorHSV.xy, oldcolor2.xy) < dst) {
    vec3 newHSV = newcolor2;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor2.z;
  }
  color.rgb = hsv2rgb(colorHSV);

  gl_FragColor = v_color * color;
}