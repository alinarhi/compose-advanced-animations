package ru.edu.hse.advanced_animations.shaders

import org.intellij.lang.annotations.Language

@Language(value = "AGSL")
const val FRACTAL_SHADER_SRC = """

uniform float2 size;
uniform float time;
uniform float spread;
uniform float iterations_;
uniform float iChannel[12];
uniform float zoom;
uniform float offset;
uniform shader composable;

float hash(float2 _v) {
    return fract(sin(dot(_v, float2(89.44, 19.36))) * 753.5453123);
}

float noise(float2 x) {
    float2 p = floor(x);
    float2 f = fract(x);
    f = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
    float n = p.x + p.y * 157.0;
    return mix(
        mix(hash(float2(n + 0.0)), hash(float2(n + 1.0)), f.x),
        mix(hash(float2(n + 157.0)), hash(float2(n + 158.0)), f.x),
        f.y
    );
}

float fbm(float2 position, float3 amp) {
    float v = 0.0;
    v += noise(position * amp.x) * 0.250;
    v += noise(position * amp.y) * 1.525;
    v += noise(position * amp.z) * 0.125;
    return v;
}

float3 draw(
    float2 uv,
    float3 offset,
    float3 colorV,
    float3 colorSets[4],
    float time,
    int iterations,
    float amplitude,
    float2 thicknessRange
) {
    float3 finalColor = float3(0.0);
    const int maxIterations = 10;

    for(int i = 0; i < maxIterations; i++) {
        if (i >= iterations) break;
        float findex = float(i);
        float amp = amplitude + findex;

        float period = 2.0 + (findex + 1.57079632679);
        float thickness = mix(thicknessRange.x, thicknessRange.y, noise(uv * 2.0));

        float t = abs(1.0 / (sin(uv.y + fbm(uv + time * period, offset)) * amp) * thickness);
        finalColor += t * colorSets[i];
    }

    for(int i = 0; i < maxIterations; i++) {
        if (i >= iterations) break;
        float findex = float(i);
        float amp = amplitude / 2.0 + (findex * 3.14159265359);

        float period = 3.14159265359 + (findex + 1.57079632679);
        float thickness = mix(0.15, 0.25, noise(uv * (3.14159265359 * 3.0)));

        float t = abs(1.0 / (sin(uv.y + fbm(uv + time * period, offset)) * amp) * thickness);
        finalColor += t * colorSets[i] * colorV;
    }
    return finalColor;
}

half4 main(
    float2 fragcoord
) {
    float2 uv = (fragcoord / min(size.x, size.y)) * zoom + offset;
    uv *= 1.0 + 0.25;
    uv.y = 1.0 - uv.y;

    float3 lineColorG = float3(0.3, 0.45, 1.3);
    float3 lineColorB = float3(0.2, 1.5, 0.1);

    float3 finalColor = float3(composable.eval(fragcoord).xyz);
    float3 colorSet[4];

    colorSet[0] = float3(iChannel[0], iChannel[1], iChannel[2]);
    colorSet[1] = float3(iChannel[3], iChannel[4], iChannel[5]);
    colorSet[2] = float3(iChannel[6], iChannel[7], iChannel[8]);
    colorSet[3] = float3(iChannel[9], iChannel[10], iChannel[11]);

    float t = sin(time) * 0.25 + 0.5;
    float amplitude = 50.0;
    float2 thicknessRange = float2(0.4, 0.2);
    int iterations = int(iterations_);

    float3 r = draw(
        uv,
        float3(5.0 * spread / 2.0, 2.5 * spread, 1.0),
        lineColorG,
        colorSet,
        time * 0.03,
        iterations,
        amplitude,
        thicknessRange
    );

    float3 b = draw(
        uv,
        float3(0.25 * spread * 2.0, 1.5 * spread, 0.5),
        lineColorB,
        colorSet,
        time * 0.03,
        iterations,
        amplitude,
        thicknessRange
    );

    finalColor = r + b * sin(t * 0.2);
    return half4(finalColor.r, finalColor.g, finalColor.b, 1.0);
}

"""