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
import type { UserManageVO } from './user-manage-vo';

/**
 * 
 * @export
 * @interface PageResultUserManageVO
 */
export interface PageResultUserManageVO {
    /**
     * 
     * @type {Array<UserManageVO>}
     * @memberof PageResultUserManageVO
     */
    'data': Array<UserManageVO>;
    /**
     * 
     * @type {number}
     * @memberof PageResultUserManageVO
     */
    'page': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultUserManageVO
     */
    'pageSize': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultUserManageVO
     */
    'total': number;
    /**
     * 
     * @type {number}
     * @memberof PageResultUserManageVO
     */
    'totalPage': number;
}
