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
 * @interface PersonalAccessTokenPageVO
 */
export interface PersonalAccessTokenPageVO {
    /**
     * 
     * @type {KotlinxDatetimeLocalDateTime}
     * @memberof PersonalAccessTokenPageVO
     */
    'createTime': KotlinxDatetimeLocalDateTime;
    /**
     * 
     * @type {string}
     * @memberof PersonalAccessTokenPageVO
     */
    'description'?: string | null;
    /**
     * 
     * @type {KotlinxDatetimeLocalDateTime}
     * @memberof PersonalAccessTokenPageVO
     */
    'expireTime': KotlinxDatetimeLocalDateTime;
    /**
     * 
     * @type {number}
     * @memberof PersonalAccessTokenPageVO
     */
    'id': number;
    /**
     * 
     * @type {boolean}
     * @memberof PersonalAccessTokenPageVO
     */
    'isExpired': boolean;
    /**
     * 
     * @type {string}
     * @memberof PersonalAccessTokenPageVO
     */
    'name': string;
}

