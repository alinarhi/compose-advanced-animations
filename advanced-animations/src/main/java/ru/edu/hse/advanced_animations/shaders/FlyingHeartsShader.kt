package ru.edu.hse.advanced_animations.shaders

import org.intellij.lang.annotations.Language

@Language("AGSL")
const val FLYING_HEARTS_SHADER = """
uniform shader composable;
uniform float iTime;
uniform float2 iResolution;

vec3 palette2(in float t) {
    vec3 a = vec3(0.554, -0.057, 0.267);
    vec3 b = vec3(0.335, 0.024, 0.555);
    vec3 c = vec3(0.365, 0.315, 0.975);
    vec3 d = vec3(6.039, 3.522, 0.209);
    return a + b*cos(6.283185*(c*t+d));
}

float dot2(in vec2 p) { return dot(p, p); }

float sdHeart(in vec2 p)
{
    p.x = abs(p.x);

    if (p.y + p.x > 1.0)
        return sqrt(dot2(p - vec2(0.25, 0.75))) - sqrt(2.0) / 4.0;

    return sqrt(min(dot2(p - vec2(0.00, 1.00)),
                    dot2(p - 0.5 * max(p.x + p.y, 0.0)))) * sign(p.x - p.y);
}

vec4 main(in vec2 fragCoord) {
    vec2 uv = fragCoord;
    uv.y = iResolution.y - fragCoord.y;
    uv /= iResolution.xy;
    uv -= 0.5;
    uv.x *= iResolution.x/iResolution.y;
    uv *= 3.;

    vec3 finalColor = vec3(0.0, 0.0, 0.0) + uv.y * vec3(0.180,0.000,0.400); 
    
    // Grid settings
    float gridX = 5.;
    float gridY = 5.;  // Number of hearts in X and Y direction
    for (float i = 0.; i < 5.; i++) {
        for (float j = 0.; j < 5.; j++) {
            // Compute heart position in the grid
            vec2 heartPos = vec2(i - gridX / 2., j - gridY / 2.) * 0.8;

            // Introduce movement (hearts slightly drift using sine waves)
            heartPos.x += 0.2 * sin(iTime + i * j);
            heartPos.y += 0.2 * cos(iTime + i - j);

            // Local UV for each heart
            vec2 localUV = uv - heartPos;

            // Pulsating effect
            float pulse = 0.3 + 0.2 * sin(iTime * 3.0 * (i+j)/(gridX+gridY));
            float dist = sdHeart(localUV / pulse);

            // Fade in & out effect (each heart has a different timing)
            float alpha = smoothstep(0.05, 0.0, dist) * (0.5 + 0.5 * sin(iTime * 2.0 + i + j));

            // Assign different colors
            vec3 heartColor = vec3(palette2(i+j));

            // **Blend the heart with transparency**
            finalColor += heartColor * alpha;  // Additive blending
        }
    }

    // Normalize colors to prevent over-brightness
    //finalColor = clamp(finalColor, 0.0, 1.0);

    return vec4(finalColor, 1.0);
}
"""