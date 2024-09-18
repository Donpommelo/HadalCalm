#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform vec2 u_resolution;

uniform sampler2D u_texture;

uniform float u_strength = 1.0;

void main() {
  vec4 color = texture(u_texture, v_texCoords);
  // if it's not transparent
  if (color.a > 0.0) {
    // find the size of one pixel by reading the input size
    vec2 pixel_size = 1.0 / u_resolution;

    // assume our new color is middle gray (R: 0.5, G: 0.5, B: 0.5, A: 1)
    vec4 new_color = vec4(vec3(0.5), 1);

    // move up one pixel diagonally and read the current color, multiply it by the input strength, then add it to our pixel color
    new_color += texture(u_texture, v_texCoords + pixel_size) * u_strength;

    // move down one pixel diagonally and read the current color, multiply it by the input strength, then subtract it to our pixel color
    new_color -= texture(u_texture, v_texCoords - pixel_size) * u_strength;

    // sum the RGB values for our new color
    float combined = new_color.r + new_color.g + new_color.b;

    // divide that sum by 3 to give us an average, and use that for the RGB values of our color
    new_color.rgb = vec3(combined / 3.0);

    // use that new color, with an alpha of 1, for our pixel color, multiplying by this pixel's alpha
    // (to avoid a hard edge) and also multiplying by the alpha for this node
    gl_FragColor = vec4(new_color.rgb, 1) * color.a * v_color.a;
  } else {
    // use the current (transparent) color
    gl_FragColor = v_color * color;
  }
}
