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
 * @interface ImagePageVO
 */
export interface ImagePageVO {
    /**
     * 
     * @type {KotlinxDatetimeLocalDateTime}
     * @memberof ImagePageVO
     */
    'createTime': KotlinxDatetimeLocalDateTime;
    /**
     * 
     * @type {string}
     * @memberof ImagePageVO
     */
    'displayName': string;
    /**
     * 
     * @type {string}
     * @memberof ImagePageVO
     */
    'externalUrl': string;
    /**
     * 
     * @type {number}
     * @memberof ImagePageVO
     */
    'id': number;
    /**
     * 
     * @type {boolean}
     * @memberof ImagePageVO
     */
    'isPrivate': boolean;
}

