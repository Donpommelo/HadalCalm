#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform float u_time;

uniform sampler2D u_texture;

uniform float u_strength = 3.0;
uniform float u_speed = 3.0;
uniform float u_frequency = 10.0;

void main() {

  // bring both speed and strength into the kinds of ranges we need for this effect
  float speed = u_time * u_speed * 0.05;
  float strength = u_strength / 100.0;

  // take a copy of the current texture coordinate so we can modify it
  vec2 coord = v_texCoords;

  // offset the coordinate by a small amount in each direction, based on wave frequency and wave strength
  coord.x += sin((coord.x + speed) * u_frequency) * strength;
  coord.y += cos((coord.y + speed) * u_frequency) * strength;

  // use the color at the offset location for our new pixel color
  gl_FragColor = texture(u_texture, coord) * v_color.a;
}
