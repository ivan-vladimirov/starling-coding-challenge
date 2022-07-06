package com.ivanov.scc.config;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    private static final int DEFAULT_CONNECTION_TTL_SECONDS = 59;
    private static final int DEFAULT_REQUEST_TIMEOUT_SECONDS = 180;
    private static final int DEFAULT_NUMBER_OF_CONNECTIONS = 1;
    private static final int DEFAULT_RETRY_COUNT = 0;
    private final TypeReference<String> STRING_TYPE = new TypeReference<String>() {
    };
    private final TypeReference<byte[]> BYTES_TYPE = new TypeReference<byte[]>() {
    };
    private final CloseableHttpClient httpClient;
    private final String name;
    private final String rootUrl;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<Type, ObjectReader> readerCache = new ConcurrentHashMap();
    private final ConcurrentMap<Class, ObjectWriter> writerCache = new ConcurrentHashMap();
    private final String token;

    private HttpClient(String name, int requestTimeout, int retryCount, int numberOfConnections, int connectionTtl,
                       ObjectMapper objectMapper, String rootUrl, String token) {
        this.name = name;
        this.objectMapper = objectMapper;
        this.rootUrl = rootUrl;
        this.httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectTimeout(requestTimeout * 1000)
                .setConnectionRequestTimeout(requestTimeout * 1000)
                .setSocketTimeout(requestTimeout * 1000).build())
                .setMaxConnPerRoute(numberOfConnections)
                .setMaxConnTotal(numberOfConnections)
                .setConnectionTimeToLive((long)connectionTtl, TimeUnit.SECONDS)
                .setRetryHandler((e, i, httpContext) -> {
            LOG.warn("[{}] HTTP request failed, retry attempt {} of {}: {}",
                    new Object[]{name, i, retryCount, httpContext.getAttribute("http.request").toString(), e});
            return i <= retryCount;
        }).build();
        this.token = token;
    }

    public <T> T sendGetWithJsonResponse(String uriPath, final Class<T> responseType) {
        return this.sendGetWithJsonResponse(uriPath, new TypeReference<T>() {
            public Type getType() {
                return responseType;
            }
        });
    }

    public <T> T sendGetWithJsonResponse(String uriPath, TypeReference<T> responseType) {
        this.validateJsonSupport();
        return this.processResponse(this.prepareHttpRequest(new HttpGet(), uriPath), responseType);
    }

    public <T> T sendPostWithJsonResponse(String uriPath, Object body, final Class<T> responseType) {
        return this.sendPostWithJsonResponse(uriPath, body, new TypeReference<T>() {
            public Type getType() {
                return responseType;
            }
        });
    }

    public <T> T sendPostWithJsonResponse(String uriPath, Object body, TypeReference<T> responseType) {
        this.validateJsonSupport();
        return this.processResponse(this.processHttpRequestBody((HttpPost)this.prepareHttpRequest(new HttpPost(), uriPath), body), responseType);
    }

    public <T> T sendPutWithJsonResponse(String uriPath, Object body, final Class<T> responseType) {
        return this.sendPutWithJsonResponse(uriPath, body, new TypeReference<T>() {
            public Type getType() {
                return responseType;
            }
        });
    }

    public <T> T sendPutWithJsonResponse(String uriPath, Object body, TypeReference<T> responseType) {
        this.validateJsonSupport();
        return this.processResponse(this.processHttpRequestBody((HttpPut)this.prepareHttpRequest(new HttpPut(), uriPath), body), responseType);
    }

    public String sendGet(String uriPath) {
        return (String)this.processResponse(this.prepareHttpRequest(new HttpGet(), uriPath), this.STRING_TYPE);
    }

    public byte[] sendGetWithBinaryResponse(String uriPath) {
        return (byte[])this.processResponse(this.prepareHttpRequest(new HttpGet(), uriPath), this.BYTES_TYPE);
    }

    public String sendPost(String uriPath, Object body) {
        return (String)this.processResponse(this.processHttpRequestBody((HttpPost)this.prepareHttpRequest(new HttpPost(), uriPath), body), this.STRING_TYPE);
    }

    public byte[] sendPostWithBinaryResponse(String uriPath, Object body) {
        return (byte[])this.processResponse(this.processHttpRequestBody((HttpPost)this.prepareHttpRequest(new HttpPost(), uriPath), body), this.BYTES_TYPE);
    }

    public String sendPut(String uriPath, Object body) {
        return (String)this.processResponse(this.processHttpRequestBody((HttpPut)this.prepareHttpRequest(new HttpPut(), uriPath), body), this.STRING_TYPE);
    }

    public byte[] sendPutWithBinaryResponse(String uriPath, Object body) {
        return (byte[])this.processResponse(this.processHttpRequestBody((HttpPut)this.prepareHttpRequest(new HttpPut(), uriPath), body), this.BYTES_TYPE);
    }

    private void validateJsonSupport() {
        if (this.objectMapper == null) {
            throw new IllegalStateException("[" + this.name + "] can't send request due to missing object mapper");
        }
    }

    protected <T extends HttpRequestBase> T prepareHttpRequest(T httpRequest, String path) {
        httpRequest.setURI(URI.create(this.rootUrl == null ? path : this.rootUrl + path));
        if (this.token != null) {
            httpRequest.setHeader("Application", "application/json");
            httpRequest.setHeader("Authorization", "Bearer " + this.token);
        }

        return httpRequest;
    }

    protected <T extends HttpEntityEnclosingRequestBase> T processHttpRequestBody(T postRequest, Object body) {
        if (body != null) {
            if (body.getClass() == String.class) {
                postRequest.setEntity(new StringEntity((String)body, Charset.defaultCharset()));
            } else if (body instanceof HttpEntity) {
                postRequest.setEntity((HttpEntity)body);
            } else {
                if (this.objectMapper == null) {
                    throw new IllegalStateException("[" + this.name + "] can't convert object '" + body + "' for '" + postRequest + "' to JSON due to missing object mapper");
                }

                postRequest.setHeader("Content-Type", "application/json");

                ObjectMapper mapper = new ObjectMapper();

                try {
                    // convert user object to json string and return it
                    StringEntity params = new StringEntity(mapper.writeValueAsString(body));
                    postRequest.setEntity(params);
                }
                catch (JsonGenerationException | JsonMappingException e) {
                    // catch various errors
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                ConcurrentMap var10003 = this.writerCache;
                Class var10004 = body.getClass();
                ObjectMapper var10005 = this.objectMapper;
                Objects.requireNonNull(var10005);
                //postRequest.setEntity(new ByteArrayEntity(((ObjectWriter)var10003.computeIfAbsent(var10004, var10005::writerFor)).writeValueAsBytes(body)));
            }
        }

        return postRequest;
    }

    protected <T> T processResponse(HttpUriRequest request, TypeReference<T> responseType) {
        CloseableHttpResponse response = null;

        Object var6;
        try {
            response = this.httpClient.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) {
                String body = null;
                if (response.getEntity() != null) {
                    body = EntityUtils.toString(response.getEntity());
                }

                throw new HttpNoOkResponse(request.toString(), body, responseCode);
            }

            Object responseBody;
            if (responseType.getType() == Void.class) {
                responseBody = null;
            } else if (response.getEntity() == null) {
                responseBody = null;
            } else if (responseType.getType() == String.class) {
                responseBody = EntityUtils.toString(response.getEntity());
                if (StringUtils.isBlank((String)responseBody)) {
                    responseBody = null;
                }
            } else if (responseType.getType() == byte[].class) {
                responseBody = EntityUtils.toByteArray(response.getEntity());
            } else {
                if (this.objectMapper == null) {
                    throw new IllegalStateException("[" + this.name + "] request " + request + " sent but can't deserialize response body to object due to missing object mapper: " + EntityUtils.toString(response.getEntity()));
                }

                responseBody = ((ObjectReader)this.readerCache.computeIfAbsent(responseType.getType(), (type) -> {
                    return this.objectMapper.readerFor(new TypeReference<T>() {
                        public Type getType() {
                            return type;
                        }
                    });
                })).readValue(response.getEntity().getContent());
            }

            var6 = responseBody;
        } catch (IOException var10) {
            throw new HttpRequestFailed("[" + this.name + "] request " + request + " failed with IO exception", var10);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }

        return (T) var6;
    }

    ConcurrentMap<Type, ObjectReader> getReaderCache() {
        return this.readerCache;
    }

    ConcurrentMap<Class, ObjectWriter> getWriterCache() {
        return this.writerCache;
    }

    public static HttpClient.Builder newBuilder() {
        return new HttpClient.Builder();
    }

    public static class Builder {
        private Integer requestTimeout;
        private Integer retryCount;
        private Integer numberOfConnections;
        private Integer connectionTtl;
        private ObjectMapper objectMapper;
        private String rootUrl;
        private String basicAuthentication;
        private String token;
        protected Builder() {
        }

        public HttpClient.Builder requestTimeoutSeconds(int requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public HttpClient.Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public HttpClient.Builder numberOfConnections(int numberOfConnections) {
            this.numberOfConnections = numberOfConnections;
            return this;
        }

        public HttpClient.Builder connectionTtlSeconds(int connectionTtl) {
            this.connectionTtl = connectionTtl;
            return this;
        }

        public HttpClient.Builder token(String token) {
            this.token = token;
            return this;
        }

        public HttpClient.Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public HttpClient.Builder rootUrl(String rootUrl) {
            this.rootUrl = rootUrl;
            return this;
        }

        public HttpClient build(String clientName) {
            if (clientName == null || clientName == "") {
                throw new IllegalStateException("Client name is mandatory");
            } else {
                if (this.objectMapper == null) {
                    HttpClient.LOG.info("ObjectMapper not set for HTTP client {}, JSON requests/responses will fail", clientName);
                }

                return new HttpClient(clientName, this.requestTimeout == null ? 30000 : this.requestTimeout, this.retryCount == null ? 0 : this.retryCount, this.numberOfConnections == null ? 1 : this.numberOfConnections, this.connectionTtl == null ? 59 : this.connectionTtl, this.objectMapper, this.rootUrl, this.token);
            }
        }
    }
}