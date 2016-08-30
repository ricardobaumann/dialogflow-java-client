package ai.api;

/***********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2015 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************/

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import ai.api.util.StringUtils;

/**
 * AI service configuration
 */
public class AIConfiguration implements Cloneable {

    private static final String SERVICE_PROD_URL = "https://api.api.ai/v1/";
    protected static final String CURRENT_PROTOCOL_VERSION = "20150910";
    protected static final String QUESTION_ENDPOINT = "query";
    protected static final String USER_ENTITIES_ENDPOINT = "userEntities";

    private final String apiKey;
    private final SupportedLanguages language;
    private String serviceUrl;

    /**
     * Protocol version used for api queries. Can be changed if old protocol version required.
     * 
     * See https://docs.api.ai/v20/docs/versioning for details
     */
    private String protocolVersion;
    private boolean writeSoundLog = false;
    private Proxy proxy;

    /**
     * Create configuration with given client access token and language.
     * 
     * See https://docs.api.ai/v20/docs/authentication for details
     * 
     * @param clientAccessToken An agent unique key for
     * @param language An agent language
     */
    public AIConfiguration(@NotNull final String clientAccessToken, final SupportedLanguages language) {
    	if (clientAccessToken == null) {
    		throw new IllegalArgumentException("clientAccessToken");
    	}
        this.apiKey = clientAccessToken;
        this.language = language != null ? language : SupportedLanguages.DEFAULT;

        protocolVersion = CURRENT_PROTOCOL_VERSION;
        serviceUrl = SERVICE_PROD_URL;
    }
    
    /**
     * Create configuration with given client access token.
     * 
     * See https://docs.api.ai/v20/docs/authentication for details
     * 
     * @param clientAccessToken An agent unique key for
     */
    public AIConfiguration(@NotNull final String clientAccessToken) {
    	this(clientAccessToken, null);
    }

    /**
     * Get client access key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Get client agent language
     */
    @NotNull
    public String getLanguage() {
        return language.languageTag;
    }

    /**
     * Get api.ai agent language
     */
    @NotNull
    public String getApiAiLanguage() {
        return language.apiaiLanguage;
    }

    /**
     * This flag is for testing purposes ONLY. Don't change it.
     * @param writeSoundLog value, indicating recorded sound will be saved in storage (if possible)
     */
    public void setWriteSoundLog(final boolean writeSoundLog) {
        this.writeSoundLog = writeSoundLog;
    }

    /**
     * This flag is for testing purposes ONLY. Don't use it in your code.
     * @return value, indicating recorded sound will be saved in storage  (if possible)
     */
    public boolean isWriteSoundLog() {
        return writeSoundLog;
    }

    /**
     * Check list of supported protocol versions on the api.ai website.
     * @return protocol version in YYYYMMDD format
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Set protocol version for API queries. Must be in YYYYMMDD format.
     * This option for special cases only, should not be used in usual cases.
     * @param protocolVersion Protocol version in YYYYMMDD format or empty string for the oldest version.
     *                        Check list of supported protocol versions on the api.ai website.
     */
    public void setProtocolVersion(final String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * Set API service url. Used primarily for test requests.
     */
    public void setServiceUrl(final String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * Get connection proxy information. If <code>null</code> then direct connection would be used.
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Set connection proxy information. 
     * @param proxy If <code>null</code> then direct connection would be used.
     */
    public void setProxy(final Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Clone the configuration
     */
    public AIConfiguration clone() {
    	try {
    		AIConfiguration result = (AIConfiguration)super.clone();
    		return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
    }
    
    String getQuestionUrl(final String sessionId) {
    	if (StringUtils.isEmpty(protocolVersion)) {
    		return String.format("%s%s?sessionId=%s", serviceUrl, QUESTION_ENDPOINT, sessionId);
    	} else {
    		return String.format("%s%s?v=%s&sessionId=%s", serviceUrl, QUESTION_ENDPOINT, protocolVersion, sessionId);
    	}
    }

    String getUserEntitiesEndpoint(final String sessionId) {
        if (StringUtils.isEmpty(protocolVersion)) {
        	return String.format("%s%s?sessionId=%s", serviceUrl, USER_ENTITIES_ENDPOINT, sessionId);
        } else {
        	return String.format("%s%s?v=%s&sessionId=%s", serviceUrl, USER_ENTITIES_ENDPOINT, protocolVersion, sessionId);
        }
    }
    
    private static Map<String, SupportedLanguages> STRING_TO_LANGUAGE = new HashMap<>();
    
    /**
     * Currently supported languages
     */
    public static enum SupportedLanguages {
        English("en"),
        EnglishUS("en-US", "en"),
        EnglishGB("en-GB", "en"),
        Russian("ru"),
        German("de"),
        Portuguese("pt"),
        PortugueseBrazil("pt-BR"),
        Spanish("es"),
        French("fr"),
        Italian("it"),
        Japanese("ja"),
        Korean("ko"),
        ChineseChina("zh-CN"),
        ChineseHongKong("zh-HK"),
        ChineseTaiwan("zh-TW");

        /**
         * Default language value
         */
        public static SupportedLanguages DEFAULT = SupportedLanguages.English;
        
        @NotNull
        private final String languageTag;
        @NotNull
        private final String apiaiLanguage;

        SupportedLanguages(@NotNull final String languageTag) {
        	this(languageTag, languageTag);
        }

        SupportedLanguages(@NotNull final String languageTag, @NotNull final String apiaiLanguage) {
        	assert languageTag != null;
        	assert apiaiLanguage != null;
        	
            this.languageTag = languageTag;
            this.apiaiLanguage = apiaiLanguage;
            
            SupportedLanguages retValue = STRING_TO_LANGUAGE.put(languageTag, this);
            assert retValue == null : "languageTag duplicates";
        }

        public static SupportedLanguages fromLanguageTag(final String languageTag) {
        	SupportedLanguages result = STRING_TO_LANGUAGE.get(languageTag);
        	return result != null ? result : DEFAULT;
        }
    }
}
