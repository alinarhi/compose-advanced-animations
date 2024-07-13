package ru.edu.hse.advanced_animations.shaders

import org.intellij.lang.annotations.Language

@Language(value = "AGSL")
const val ZIPPY_ZAPS_SHADER = """
uniform shader composable;
uniform float2 size;
uniform float time;
uniform float speed;


float tanhyp(float x) {
    return (1 - exp(-2 * x)) / (1 + exp(-2 * x));
}

float2 tanhyp2(float2 v) {
    return float2(tanhyp(v.x), tanhyp(v.y));
}

half4 main(in float2 fragCoord) {
    float2 u = fragCoord;
    float2 v = size.xy;
    float2 w;
    u = 0.4 * (u + u - v) / max(v.x, v.y);
    float2 k = u;
    float4 o = float4(1, 2, 3, 0);
    float a = 0.5;
    float t = time * speed;

    for (float i = 0.5; i < 10.0; ++i) {
        o += (1.0 + cos(vec4(0, 1, 3, 0) + t)) /
             length((1.0 + i * dot(v, v)) * sin(w * 3.0 - 7.0 * u.yx + t));

        v = cos(++t - 7.0 * u * pow(a, i)) - 4.0 * u;
        u *= mat2(cos(i + t * 0.02 - vec4(0, 11, 33, 0)));
        u += 0.005 * tanhyp2(40.0 * dot(u, u) * cos(1e2 * u.yx + t)) +
             0.2 * a * u +
             0.003 * cos(t + 4.0 * exp(-0.01 * dot(o, o)));
        w = u / (1.0 - 2.0 * dot(u, u));
    }

    o = pow(o = 1.0 - sqrt(exp(-o * o * o/ 2e2)), 0.3 * o / o) -
        dot(k -= u, k) / 250.0;

    return half4(half3(o.rgb), 1.0);
}
"""