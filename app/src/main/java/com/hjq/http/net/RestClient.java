package com.hjq.http.net;
import android.content.Context;

import com.hjq.http.net.callback.IError;
import com.hjq.http.net.callback.IFailure;
import com.hjq.http.net.callback.IRequest;
import com.hjq.http.net.callback.ISuccess;
import com.hjq.http.net.callback.RequestCallbacks;

import java.io.File;
import java.util.WeakHashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class RestClient {

    private final WeakHashMap<String, Object> PARAMS;
    private final String URL;
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;
    private final String DOWNLOAD_DIR;
    private final String EXTENSION;
    private final String NAME;
    private final RequestBody BODY;
    private final File FILE;
    private final Context CONTEXT;


    public RestClient(String mUrl,
                      WeakHashMap<String, Object> params,
                      String download_dir,
                      String extension,
                      String name,
                      IRequest mIRequest,
                      ISuccess mISuccess,
                      IFailure mIFailure,
                      IError mIError,
                      RequestBody mBody,
                      File file,
                      Context mContext) {
        this.URL = mUrl;
        this.DOWNLOAD_DIR = download_dir;
        this.EXTENSION = extension;
        this.NAME = name;
        this.PARAMS = params;
        this.REQUEST = mIRequest;
        this.SUCCESS = mISuccess;
        this.FAILURE = mIFailure;
        this.ERROR = mIError;
        this.BODY = mBody;
        this.FILE = file;
        this.CONTEXT = mContext;
    }


    public static RestClientBuilder builder(){
        return new RestClientBuilder();
    }

    private void request(HttpMethod method) {
        final RestService service = RestCreator.getRestService();
        Call<String> call = null;

        if (REQUEST != null) {
            REQUEST.onRequestStart();
        }

        switch (method) {
            case GET:
                call = service.get(URL, PARAMS);
                break;
            case POST:
                call = service.post(URL, PARAMS);
                break;
            case POST_RAW:
                call = service.postRaw(URL, BODY);
                break;
            case PUT:
                call = service.put(URL, PARAMS);
                break;
            case PUT_RAW:
                call = service.putRaw(URL, BODY);
                break;
            case DELETE:
                call = service.delete(URL, PARAMS);
                break;
            case UPLOAD:
                final RequestBody requestBody =
                        RequestBody.create(MediaType.parse(MultipartBody.FORM.toString()), FILE);
                final MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", FILE.getName(), requestBody);
                call = service.upload(URL, body);
                break;
            default:
                break;
        }

        if (call != null) {
            call.enqueue(getRequestCallback());
        }
    }

    private Callback<String> getRequestCallback() {
        return new RequestCallbacks(
                REQUEST,
                SUCCESS,
                FAILURE,
                ERROR
        );
    }

    public final void get(){
        request(HttpMethod.GET);
    }

    public final void post(){
        if (BODY == null) {
            request(HttpMethod.POST);
        } else {
            if (!PARAMS.isEmpty()) {
                throw new RuntimeException("params must be null!");
            }
            request(HttpMethod.POST_RAW);
        }
    }
}