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
import type { CommonSiteSetting } from './common-site-setting';

/**
 * 
 * @export
 * @interface CommonResponseCommonSiteSetting
 */
export interface CommonResponseCommonSiteSetting {
    /**
     * 
     * @type {number}
     * @memberof CommonResponseCommonSiteSetting
     */
    'code': number;
    /**
     * 
     * @type {CommonSiteSetting}
     * @memberof CommonResponseCommonSiteSetting
     */
    'data'?: CommonSiteSetting | null;
    /**
     * 
     * @type {boolean}
     * @memberof CommonResponseCommonSiteSetting
     */
    'isSuccessful': boolean;
    /**
     * 
     * @type {string}
     * @memberof CommonResponseCommonSiteSetting
     */
    'message': string;
}

