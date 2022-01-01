#ifdef GL_ES
precision mediump float;
#endif

const float offset = 1.0 / 64.0;
varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    if (color.a > 0.5) {
        gl_FragColor = color;
    }
    else {
        float a = texture2D(u_texture, vec2(v_texCoords.x + offset, v_texCoords.y)).a +
        texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - offset)).a +
        texture2D(u_texture, vec2(v_texCoords.x - offset, v_texCoords.y)).a +
        texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + offset)).a;
        if (color.a == 0.0 && a > 0.5) {
            gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        }
        else {
            gl_FragColor = color;
        }
    }
}
