package br.rnp.futebol.verona.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Criado por Joao em 12/04/16.
 */

public class VolleyRequest implements Serializable {


    private Context ctx;

    public VolleyRequest(Context ctx) {
        this.ctx = ctx;
    }

    public void getJSON(String URL, Response.Listener<String> listener) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest getRequest = new StringRequest(URL, listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("volleyError", error.toString());
                    }
                });
        queue.add(getRequest);
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000*10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @SuppressWarnings("unused")
    public void getJsonArray(String URL, Response.Listener<JSONObject> listener) {
        JsonObjectRequest getRequest = new JsonObjectRequest(URL, listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("volleyError", error.toString());
                    }
                });
        Volley.newRequestQueue(ctx).add(getRequest);
    }

    public void postJsonArray(String URL, JSONObject json, Response.Listener<JSONObject> listener) {
        try {
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL,
                    json, listener,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        Bloco de condições para testar qual foi exatamente o erro
                            if (error instanceof TimeoutError) {
                                Log.i("Volley", "TimeoutError");
                            } else if (error instanceof NoConnectionError) {
                                Log.i("Volley", "NoConnectionError");
                            } else if (error instanceof AuthFailureError) {
                                Log.i("Volley", "AuthFailureError");
                            } else if (error instanceof ServerError) {
                                Log.i("Volley", "ServerError");
                            } else if (error instanceof NetworkError) {
                                Log.i("Volley", "NetworkError");
                            } else if (error instanceof ParseError) {
                                Log.i("Volley", "ParseError");
                            }
                        Log.i("volleyError", error.toString());
                        }
                    }
            ) {
            };
            Volley.newRequestQueue(ctx).add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postJsonURL(String URL, Response.Listener<String> listener, final Map<String, String> urlParams) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volleyError", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return urlParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        Volley.newRequestQueue(ctx).add(postRequest);

    }
}
