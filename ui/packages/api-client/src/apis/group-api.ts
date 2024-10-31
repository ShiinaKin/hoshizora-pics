/* tslint:disable */
/* eslint-disable */
/**
 * HoshizoraPics API
 * API for testing and demonstration purposes.
 *
 * The version of the OpenAPI document: latest
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import type { Configuration } from '../configuration';
import type { AxiosPromise, AxiosInstance, RawAxiosRequestConfig } from 'axios';
import globalAxios from 'axios';
// Some imports not used depending on template conditions
// @ts-ignore
import { DUMMY_BASE_URL, assertParamExists, setApiKeyToObject, setBasicAuthToObject, setBearerAuthToObject, setOAuthToObject, setSearchParams, serializeDataIfNeeded, toPathString, createRequestFunction } from '../common';
// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS, type RequestArgs, BaseAPI, RequiredError, operationServerMap } from '../base';
// @ts-ignore
import type { CommonResponseGroupAllowedImageType } from '../models';
// @ts-ignore
import type { CommonResponseGroupVO } from '../models';
// @ts-ignore
import type { CommonResponseKotlinUnit } from '../models';
// @ts-ignore
import type { CommonResponsePageResultGroupPageVO } from '../models';
// @ts-ignore
import type { GroupInsertRequest } from '../models';
// @ts-ignore
import type { GroupPatchRequest } from '../models';
/**
 * GroupApi - axios parameter creator
 * @export
 */
export const GroupApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * 
         * @param {number} id group id
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupIdDelete: async (id: number, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'id' is not null or undefined
            assertParamExists('apiGroupIdDelete', 'id', id)
            const localVarPath = `/api/group/{id}`
                .replace(`{${"id"}}`, encodeURIComponent(String(id)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'DELETE', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication JWT required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * 
         * @param {number} id group id
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupIdGet: async (id: number, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'id' is not null or undefined
            assertParamExists('apiGroupIdGet', 'id', id)
            const localVarPath = `/api/group/{id}`
                .replace(`{${"id"}}`, encodeURIComponent(String(id)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication JWT required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * 
         * @param {number} id group id
         * @param {GroupPatchRequest} groupPatchRequest 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupIdPatch: async (id: number, groupPatchRequest: GroupPatchRequest, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'id' is not null or undefined
            assertParamExists('apiGroupIdPatch', 'id', id)
            // verify required parameter 'groupPatchRequest' is not null or undefined
            assertParamExists('apiGroupIdPatch', 'groupPatchRequest', groupPatchRequest)
            const localVarPath = `/api/group/{id}`
                .replace(`{${"id"}}`, encodeURIComponent(String(id)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'PATCH', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication JWT required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            localVarHeaderParameter['Content-Type'] = 'application/json';

            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};
            localVarRequestOptions.data = serializeDataIfNeeded(groupPatchRequest, localVarRequestOptions, configuration)

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * 
         * @param {number} page page
         * @param {number} pageSize pageSize
         * @param {string} [order] order
         * @param {string} [orderBy] orderBy
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupPageGet: async (page: number, pageSize: number, order?: string, orderBy?: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'page' is not null or undefined
            assertParamExists('apiGroupPageGet', 'page', page)
            // verify required parameter 'pageSize' is not null or undefined
            assertParamExists('apiGroupPageGet', 'pageSize', pageSize)
            const localVarPath = `/api/group/page`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication JWT required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)

            if (page !== undefined) {
                localVarQueryParameter['page'] = page;
            }

            if (pageSize !== undefined) {
                localVarQueryParameter['pageSize'] = pageSize;
            }

            if (order !== undefined) {
                localVarQueryParameter['order'] = order;
            }

            if (orderBy !== undefined) {
                localVarQueryParameter['orderBy'] = orderBy;
            }


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * 
         * @param {GroupInsertRequest} groupInsertRequest 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupPost: async (groupInsertRequest: GroupInsertRequest, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'groupInsertRequest' is not null or undefined
            assertParamExists('apiGroupPost', 'groupInsertRequest', groupInsertRequest)
            const localVarPath = `/api/group`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'POST', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication JWT required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            localVarHeaderParameter['Content-Type'] = 'application/json';

            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};
            localVarRequestOptions.data = serializeDataIfNeeded(groupInsertRequest, localVarRequestOptions, configuration)

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupTypeGet: async (options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            const localVarPath = `/api/group/type`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication JWT required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * GroupApi - functional programming interface
 * @export
 */
export const GroupApiFp = function(configuration?: Configuration) {
    const localVarAxiosParamCreator = GroupApiAxiosParamCreator(configuration)
    return {
        /**
         * 
         * @param {number} id group id
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async apiGroupIdDelete(id: number, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<CommonResponseKotlinUnit>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.apiGroupIdDelete(id, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['GroupApi.apiGroupIdDelete']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * 
         * @param {number} id group id
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async apiGroupIdGet(id: number, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<CommonResponseGroupVO>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.apiGroupIdGet(id, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['GroupApi.apiGroupIdGet']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * 
         * @param {number} id group id
         * @param {GroupPatchRequest} groupPatchRequest 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async apiGroupIdPatch(id: number, groupPatchRequest: GroupPatchRequest, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<CommonResponseKotlinUnit>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.apiGroupIdPatch(id, groupPatchRequest, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['GroupApi.apiGroupIdPatch']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * 
         * @param {number} page page
         * @param {number} pageSize pageSize
         * @param {string} [order] order
         * @param {string} [orderBy] orderBy
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async apiGroupPageGet(page: number, pageSize: number, order?: string, orderBy?: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<CommonResponsePageResultGroupPageVO>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.apiGroupPageGet(page, pageSize, order, orderBy, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['GroupApi.apiGroupPageGet']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * 
         * @param {GroupInsertRequest} groupInsertRequest 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async apiGroupPost(groupInsertRequest: GroupInsertRequest, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<CommonResponseKotlinUnit>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.apiGroupPost(groupInsertRequest, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['GroupApi.apiGroupPost']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async apiGroupTypeGet(options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<CommonResponseGroupAllowedImageType>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.apiGroupTypeGet(options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['GroupApi.apiGroupTypeGet']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
    }
};

/**
 * GroupApi - factory interface
 * @export
 */
export const GroupApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    const localVarFp = GroupApiFp(configuration)
    return {
        /**
         * 
         * @param {GroupApiApiGroupIdDeleteRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupIdDelete(requestParameters: GroupApiApiGroupIdDeleteRequest, options?: RawAxiosRequestConfig): AxiosPromise<CommonResponseKotlinUnit> {
            return localVarFp.apiGroupIdDelete(requestParameters.id, options).then((request) => request(axios, basePath));
        },
        /**
         * 
         * @param {GroupApiApiGroupIdGetRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupIdGet(requestParameters: GroupApiApiGroupIdGetRequest, options?: RawAxiosRequestConfig): AxiosPromise<CommonResponseGroupVO> {
            return localVarFp.apiGroupIdGet(requestParameters.id, options).then((request) => request(axios, basePath));
        },
        /**
         * 
         * @param {GroupApiApiGroupIdPatchRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupIdPatch(requestParameters: GroupApiApiGroupIdPatchRequest, options?: RawAxiosRequestConfig): AxiosPromise<CommonResponseKotlinUnit> {
            return localVarFp.apiGroupIdPatch(requestParameters.id, requestParameters.groupPatchRequest, options).then((request) => request(axios, basePath));
        },
        /**
         * 
         * @param {GroupApiApiGroupPageGetRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupPageGet(requestParameters: GroupApiApiGroupPageGetRequest, options?: RawAxiosRequestConfig): AxiosPromise<CommonResponsePageResultGroupPageVO> {
            return localVarFp.apiGroupPageGet(requestParameters.page, requestParameters.pageSize, requestParameters.order, requestParameters.orderBy, options).then((request) => request(axios, basePath));
        },
        /**
         * 
         * @param {GroupApiApiGroupPostRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupPost(requestParameters: GroupApiApiGroupPostRequest, options?: RawAxiosRequestConfig): AxiosPromise<CommonResponseKotlinUnit> {
            return localVarFp.apiGroupPost(requestParameters.groupInsertRequest, options).then((request) => request(axios, basePath));
        },
        /**
         * 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        apiGroupTypeGet(options?: RawAxiosRequestConfig): AxiosPromise<CommonResponseGroupAllowedImageType> {
            return localVarFp.apiGroupTypeGet(options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * Request parameters for apiGroupIdDelete operation in GroupApi.
 * @export
 * @interface GroupApiApiGroupIdDeleteRequest
 */
export interface GroupApiApiGroupIdDeleteRequest {
    /**
     * group id
     * @type {number}
     * @memberof GroupApiApiGroupIdDelete
     */
    readonly id: number
}

/**
 * Request parameters for apiGroupIdGet operation in GroupApi.
 * @export
 * @interface GroupApiApiGroupIdGetRequest
 */
export interface GroupApiApiGroupIdGetRequest {
    /**
     * group id
     * @type {number}
     * @memberof GroupApiApiGroupIdGet
     */
    readonly id: number
}

/**
 * Request parameters for apiGroupIdPatch operation in GroupApi.
 * @export
 * @interface GroupApiApiGroupIdPatchRequest
 */
export interface GroupApiApiGroupIdPatchRequest {
    /**
     * group id
     * @type {number}
     * @memberof GroupApiApiGroupIdPatch
     */
    readonly id: number

    /**
     * 
     * @type {GroupPatchRequest}
     * @memberof GroupApiApiGroupIdPatch
     */
    readonly groupPatchRequest: GroupPatchRequest
}

/**
 * Request parameters for apiGroupPageGet operation in GroupApi.
 * @export
 * @interface GroupApiApiGroupPageGetRequest
 */
export interface GroupApiApiGroupPageGetRequest {
    /**
     * page
     * @type {number}
     * @memberof GroupApiApiGroupPageGet
     */
    readonly page: number

    /**
     * pageSize
     * @type {number}
     * @memberof GroupApiApiGroupPageGet
     */
    readonly pageSize: number

    /**
     * order
     * @type {string}
     * @memberof GroupApiApiGroupPageGet
     */
    readonly order?: string

    /**
     * orderBy
     * @type {string}
     * @memberof GroupApiApiGroupPageGet
     */
    readonly orderBy?: string
}

/**
 * Request parameters for apiGroupPost operation in GroupApi.
 * @export
 * @interface GroupApiApiGroupPostRequest
 */
export interface GroupApiApiGroupPostRequest {
    /**
     * 
     * @type {GroupInsertRequest}
     * @memberof GroupApiApiGroupPost
     */
    readonly groupInsertRequest: GroupInsertRequest
}

/**
 * GroupApi - object-oriented interface
 * @export
 * @class GroupApi
 * @extends {BaseAPI}
 */
export class GroupApi extends BaseAPI {
    /**
     * 
     * @param {GroupApiApiGroupIdDeleteRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof GroupApi
     */
    public apiGroupIdDelete(requestParameters: GroupApiApiGroupIdDeleteRequest, options?: RawAxiosRequestConfig) {
        return GroupApiFp(this.configuration).apiGroupIdDelete(requestParameters.id, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * 
     * @param {GroupApiApiGroupIdGetRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof GroupApi
     */
    public apiGroupIdGet(requestParameters: GroupApiApiGroupIdGetRequest, options?: RawAxiosRequestConfig) {
        return GroupApiFp(this.configuration).apiGroupIdGet(requestParameters.id, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * 
     * @param {GroupApiApiGroupIdPatchRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof GroupApi
     */
    public apiGroupIdPatch(requestParameters: GroupApiApiGroupIdPatchRequest, options?: RawAxiosRequestConfig) {
        return GroupApiFp(this.configuration).apiGroupIdPatch(requestParameters.id, requestParameters.groupPatchRequest, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * 
     * @param {GroupApiApiGroupPageGetRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof GroupApi
     */
    public apiGroupPageGet(requestParameters: GroupApiApiGroupPageGetRequest, options?: RawAxiosRequestConfig) {
        return GroupApiFp(this.configuration).apiGroupPageGet(requestParameters.page, requestParameters.pageSize, requestParameters.order, requestParameters.orderBy, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * 
     * @param {GroupApiApiGroupPostRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof GroupApi
     */
    public apiGroupPost(requestParameters: GroupApiApiGroupPostRequest, options?: RawAxiosRequestConfig) {
        return GroupApiFp(this.configuration).apiGroupPost(requestParameters.groupInsertRequest, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * 
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof GroupApi
     */
    public apiGroupTypeGet(options?: RawAxiosRequestConfig) {
        return GroupApiFp(this.configuration).apiGroupTypeGet(options).then((request) => request(this.axios, this.basePath));
    }
}
