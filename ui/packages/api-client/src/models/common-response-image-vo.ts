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
import type { ImageVO } from './image-vo';

/**
 * 
 * @export
 * @interface CommonResponseImageVO
 */
export interface CommonResponseImageVO {
    /**
     * 
     * @type {number}
     * @memberof CommonResponseImageVO
     */
    'code': number;
    /**
     * 
     * @type {ImageVO}
     * @memberof CommonResponseImageVO
     */
    'data'?: ImageVO | null;
    /**
     * 
     * @type {boolean}
     * @memberof CommonResponseImageVO
     */
    'isSuccessful': boolean;
    /**
     * 
     * @type {string}
     * @memberof CommonResponseImageVO
     */
    'message': string;
}
