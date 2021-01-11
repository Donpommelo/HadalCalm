#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
  float dx = 15.0 * (1.0 / 1024.0);
  float dy = 10.0 * (1.0 / 1024.0);
  vec2 coord = vec2(dx * floor(v_texCoords.x / dx) + dx / 2, dy * floor(v_texCoords.y / dy) + dy / 2);
  gl_FragColor = texture2D(u_texture, coord);
}
