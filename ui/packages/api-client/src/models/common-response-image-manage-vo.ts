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
import type { ImageManageVO } from './image-manage-vo';

/**
 * 
 * @export
 * @interface CommonResponseImageManageVO
 */
export interface CommonResponseImageManageVO {
    /**
     * 
     * @type {number}
     * @memberof CommonResponseImageManageVO
     */
    'code': number;
    /**
     * 
     * @type {ImageManageVO}
     * @memberof CommonResponseImageManageVO
     */
    'data'?: ImageManageVO | null;
    /**
     * 
     * @type {boolean}
     * @memberof CommonResponseImageManageVO
     */
    'isSuccessful': boolean;
    /**
     * 
     * @type {string}
     * @memberof CommonResponseImageManageVO
     */
    'message': string;
}

