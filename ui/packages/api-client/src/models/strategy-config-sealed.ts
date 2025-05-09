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


// May contain unused imports in some cases
// @ts-ignore
import type { LocalStrategy } from './local-strategy';
// May contain unused imports in some cases
// @ts-ignore
import type { S3Strategy } from './s3-strategy';
// May contain unused imports in some cases
// @ts-ignore
import type { StrategyTypeEnum } from './strategy-type-enum';
// May contain unused imports in some cases
// @ts-ignore
import type { WebDavStrategy } from './web-dav-strategy';

/**
 * 
 * @export
 * @interface StrategyConfigSealed
 */
export interface StrategyConfigSealed {
    /**
     * 
     * @type {StrategyTypeEnum}
     * @memberof StrategyConfigSealed
     */
    'strategyType': StrategyTypeEnum;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'thumbnailFolder': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'uploadFolder': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'accessKey': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'bucketName': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'endpoint': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'publicUrl': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'region': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'secretKey': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'password': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'serverUrl': string;
    /**
     * 
     * @type {string}
     * @memberof StrategyConfigSealed
     */
    'username': string;
}



