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
import type { PersonalAccessTokenPageVO } from './personal-access-token-page-vo';

/**
 * 
 * @export
 * @interface PageResultPersonalAccessTokenPageVO
 */
export interface PageResultPersonalAccessTokenPageVO {
    /**
     * 
     * @type {Array<PersonalAccessTokenPageVO>}
     * @memberof PageResultPersonalAccessTokenPageVO
     */
    'data': Array<PersonalAccessTokenPageVO>;
    /**
     * 
     * @type {number}
     * @memberof PageResultPersonalAccessTokenPageVO
     */
    'page': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultPersonalAccessTokenPageVO
     */
    'pageSize': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultPersonalAccessTokenPageVO
     */
    'total': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultPersonalAccessTokenPageVO
     */
    'totalPage': number;
}

