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
import type { KotlinxDatetimeLocalDateTime } from './kotlinx-datetime-local-date-time';

/**
 * 
 * @export
 * @interface UserVO
 */
export interface UserVO {
    /**
     * 
     * @type {number}
     * @memberof UserVO
     */
    'albumCount': number;
    /**
     * 
     * @type {number}
     * @memberof UserVO
     */
    'allSize': number;
    /**
     * 
     * @type {KotlinxDatetimeLocalDateTime}
     * @memberof UserVO
     */
    'createTime': KotlinxDatetimeLocalDateTime;
    /**
     * 
     * @type {string}
     * @memberof UserVO
     */
    'email'?: string | null;
    /**
     * 
     * @type {string}
     * @memberof UserVO
     */
    'groupName': string;
    /**
     * 
     * @type {number}
     * @memberof UserVO
     */
    'id': number;
    /**
     * 
     * @type {number}
     * @memberof UserVO
     */
    'imageCount': number;
    /**
     * 
     * @type {boolean}
     * @memberof UserVO
     */
    'isBanned': boolean;
    /**
     * 
     * @type {boolean}
     * @memberof UserVO
     */
    'isDefaultImagePrivate': boolean;
    /**
     * 
     * @type {number}
     * @memberof UserVO
     */
    'totalImageSize': number;
    /**
     * 
     * @type {string}
     * @memberof UserVO
     */
    'username': string;
}

