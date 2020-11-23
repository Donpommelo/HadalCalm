#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float completion;

float r(in vec2 p) {
  return fract(cos(p.x * 42.98 + p.y * 43.23) * 1127.53);
}

float n(in vec2 p) {
  vec2 fn = floor(p);
  vec2 sn = smoothstep(vec2(0), vec2(1), fract(p));

  float h1 = mix(r(fn), r(fn + vec2(1,0)), sn.x);
  float h2 = mix(r(fn + vec2(0,1)), r(fn + vec2(1)), sn.x);
  return mix(h1 ,h2, sn.y);
}

float noise(in vec2 p) {
  return n(p / 32.0) * 0.58 +
  n(p/16.0) * 0.2  +
  n(p/8.0)  * 0.1  +
  n(p/4.0)  * 0.05 +
  n(p/2.0)  * 0.02 +
  n(p)     * 0.0125;
}

void main() {
  vec4 color = texture2D(u_texture, v_texCoords);
  gl_FragColor = mix(color, gl_FragColor, smoothstep(completion + 0.1, completion - 0.1, noise(v_texCoords * 0.4)));
}
