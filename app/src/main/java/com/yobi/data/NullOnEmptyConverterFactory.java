package com.yobi.data;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

// 레트로핏 사용 시 Response가 없을 경우 사용
public class NullOnEmptyConverterFactory extends Converter.Factory{

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody responseBody) throws IOException {
                if (responseBody.contentLength() == 0) {
                    return null;
                }
                return delegate.convert(responseBody);
            }
        };
    }
}
