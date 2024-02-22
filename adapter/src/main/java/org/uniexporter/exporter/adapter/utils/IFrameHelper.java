package org.uniexporter.exporter.adapter.utils;

/**
 *
 * @param <T> image
 * @param <S> itemstsck
 * @param <B> baked model
 * @param <R> renderer
 */
public interface IFrameHelper<T, S, B, R> {
    void begin();
    void end();
    T dumpFrom();
    String base64(T image);
    void render(S stack, int x, int y, B baked, R renderer);
}
