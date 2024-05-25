#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec3 oldcolor1;
uniform vec3 oldcolor2;
uniform vec3 oldcolor3;
uniform vec3 oldcolor4;
uniform vec3 oldcolor5;
uniform vec3 oldcolor6;
uniform vec3 oldcolor7;
uniform vec3 oldcolor8;
uniform vec3 newcolor1;
uniform vec3 newcolor2;
uniform vec3 newcolor3;
uniform vec3 newcolor4;
uniform vec3 newcolor5;
uniform vec3 newcolor6;
uniform vec3 newcolor7;
uniform vec3 newcolor8;

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
  vec4 color = texture(u_texture, v_texCoords);
  vec3 colorRGB = color.rgb;
  vec3 colorHSV = rgb2hsv(colorRGB);
  float dst = 0.1;

  if (distance(colorHSV.xy, oldcolor1.xy) < dst) {
    vec3 newHSV = newcolor1;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor1.z;
  } else if (distance(colorHSV.xy, oldcolor2.xy) < dst) {
    vec3 newHSV = newcolor2;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor2.z;
  } else if (distance(colorHSV.xy, oldcolor3.xy) < dst) {
    vec3 newHSV = newcolor3;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor3.z;
  } else if (distance(colorHSV.xy, oldcolor4.xy) < dst) {
    vec3 newHSV = newcolor4;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor4.z;
  } else if (distance(colorHSV.xy, oldcolor5.xy) < dst) {
    vec3 newHSV = newcolor5;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor5.z;
  } else if (distance(colorHSV.xy, oldcolor6.xy) < dst) {
    vec3 newHSV = newcolor6;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor6.z;
  } else if (distance(colorHSV.xy, oldcolor7.xy) < dst) {
    vec3 newHSV = newcolor7;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor7.z;
  } else if (distance(colorHSV.xy, oldcolor8.xy) < dst) {
    vec3 newHSV = newcolor8;
    colorHSV.xy = newHSV.xy;
    colorHSV.z = newHSV.z * colorHSV.z / oldcolor8.z;
  }
  color.rgb = hsv2rgb(colorHSV);

  gl_FragColor = v_color * color;
}