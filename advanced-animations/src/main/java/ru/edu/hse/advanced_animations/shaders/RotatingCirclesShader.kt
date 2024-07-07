package ru.edu.hse.advanced_animations.shaders

import org.intellij.lang.annotations.Language

@Language(value = "AGSL")
const val ROTATING_CIRCLES_SHADER = """
uniform shader composable;
uniform float2 size;
uniform float time;
layout(color) uniform half4 color;
uniform float iterations;

mat2 rotate2d(float angle) {
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

half4 main(in float2 fragCoord) {
    float2 position = fragCoord;
    
    int2 F = int2(min(size.x, size.y) * 0.25);
    F.y -= int(min(size.x, size.y) * 0.075);
    float2 r = float2(position.xy);
    float2 u = (float2(F+F) - r) / r.y;
    float3 col = color.rgb;
    
    const float maxIterations = 10.0;
    
    for (float i = 0.0; i < maxIterations; i++) {
        if (i >= iterations) break;
        col += 0.004 / (abs(length(u * u) - i * 0.04) + 0.005)
        * (cos(i + float3(0.0, 1.0, 2.0)) + 1.0)
        * smoothstep(0.35, 0.4, abs(abs(mod(time, 2.0) - i * 0.1) - 1.0));
        
        float angle = (time + i) * 0.03 + 3.14159265359 / 2.0;
        u = rotate2d(angle) * u;
    }
    
    return half4(half3(col.rgb), 1.0);
}
"""