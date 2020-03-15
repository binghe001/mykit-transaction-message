/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.transaction.message.common.utils.httpclient;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author binghe
 * @version 1.0.0
 * @description OkHttpTools
 */
public class OkHttpTools {
    /**
     * The constant JSON.
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpTools OK_HTTP_TOOLS = new OkHttpTools();

    private static final Gson GOSN = new Gson();

    private OkHttpTools() {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static OkHttpTools getInstance() {
        return OK_HTTP_TOOLS;
    }

    /**
     * Build post request.
     *
     * @param url    the url
     * @param params the params
     * @return the request
     */
    public Request buildPost(String url, Map<String, String> params) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                formBuilder.add(key, params.get(key));
            }
        }
        return new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(formBuilder.build())
                .build();
    }

    /**
     * Post string.
     *
     * @param url  the url
     * @param json the json
     * @return the string
     * @throws IOException the io exception
     */
    public String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();

    }

    /**
     * Get t.
     *
     * @param <T>      the type parameter
     * @param url      the url
     * @param params   the params
     * @param classOfT the class of t
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T get(String url, Map<String, String> params, Class<T> classOfT) throws IOException {
        return execute(buildPost(url, params), classOfT);
    }

    /**
     * Get t.
     *
     * @param <T>  the type parameter
     * @param url  the url
     * @param type the type
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T get(String url, Type type) throws IOException {
        return execute(buildPost(url, null), type);
    }


    private <T> T execute(Request request, Class<T> classOfT) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        return GOSN.fromJson(response.body().string(), classOfT);
    }

    private String execute(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Execute t.
     *
     * @param <T>     the type parameter
     * @param request the request
     * @param type    the type
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T execute(Request request, Type type) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        return GOSN.fromJson(response.body().string(), type);
    }
}
