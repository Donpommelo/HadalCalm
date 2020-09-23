#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
  float dx = 15.0 * (1.0 / 256.0);
  float dy = 10.0 * (1.0 / 256.0);
  vec2 coord = vec2(dx * floor(v_texCoords.x / dx), dy * floor(v_texCoords.y / dy));
  gl_FragColor = texture2D(u_texture, coord);
}
