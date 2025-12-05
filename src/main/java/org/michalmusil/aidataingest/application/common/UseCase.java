package org.michalmusil.aidataingest.application.common;

public interface UseCase<T, R> {
    R execute(T input);
}
