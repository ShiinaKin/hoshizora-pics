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
 * @interface UserPageVO
 */
export interface UserPageVO {
    /**
     * 
     * @type {KotlinxDatetimeLocalDateTime}
     * @memberof UserPageVO
     */
    'createTime': KotlinxDatetimeLocalDateTime;
    /**
     * 
     * @type {string}
     * @memberof UserPageVO
     */
    'groupName': string;
    /**
     * 
     * @type {number}
     * @memberof UserPageVO
     */
    'id': number;
    /**
     * 
     * @type {number}
     * @memberof UserPageVO
     */
    'imageCount': number;
    /**
     * 
     * @type {boolean}
     * @memberof UserPageVO
     */
    'isBanned': boolean;
    /**
     * 
     * @type {number}
     * @memberof UserPageVO
     */
    'totalImageSize': number;
    /**
     * 
     * @type {string}
     * @memberof UserPageVO
     */
    'username': string;
}
