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
import type { UserPageVO } from './user-page-vo';

/**
 * 
 * @export
 * @interface PageResultUserPageVO
 */
export interface PageResultUserPageVO {
    /**
     * 
     * @type {Array<UserPageVO>}
     * @memberof PageResultUserPageVO
     */
    'data': Array<UserPageVO>;
    /**
     * 
     * @type {number}
     * @memberof PageResultUserPageVO
     */
    'page': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultUserPageVO
     */
    'pageSize': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultUserPageVO
     */
    'total': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultUserPageVO
     */
    'totalPage': number;
}

